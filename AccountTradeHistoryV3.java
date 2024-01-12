import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class AccountTradeHistoryV3 {
    public static void main(String[] args) {

        // Different Files to Test
        String multipleTradeFile = "/Users/Jawed/OneDrive/Desktop/2023-12-31-AccountStatement.csv";
        String twoTradeFile = "/Users/Jawed/OneDrive/Desktop/twoTradeAccountStatement.csv";
        String customTradeFile = "/Users/Jawed/OneDrive/Desktop/customTradeFile.txt";
        String customTradeFile2 = "/Users/Jawed/OneDrive/Desktop/customTradeFileV2.txt";
        String customTradeFile3 = "/Users/Jawed/OneDrive/Desktop/customTradeFileV3.txt";

        File newFile = new File(twoTradeFile);
        String[][] allTradeExecutions = null;
        int lineCount = 0;

        // Call to method to read file and then get lineCount to pass for other method success
        allTradeExecutions = readAndProcessFile(newFile);
        lineCount = allTradeExecutions.length; // Gets number of lines in file

        // Method to check if allocated array data is valid
        //arrayInitializationTesting(allTradeExecutions, lineCount);

        int[][] allTransactionsPerPositions = countTotalTradePositions(allTradeExecutions, lineCount);


    }





    // Method to read file and output
    public static String[][] readAndProcessFile(File file) {

        String line = "";
        String commaSplit = ",";
        String date = "";
        String time = "";
        String tradeSide = "";
        String tradeSize = "";
        String stockTicker = "";
        String tradePrice = "";
        String posEffect = "";
        String orderType = "";
        int lineCount = 0;
        String[][] allTradeExecutions = null;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            while ((line = br.readLine()) != null) {
                String[] dataSplitForStorage = line.split(commaSplit);

                // Loop to get the number of readable lines
                if (dataSplitForStorage.length > 12 && dataSplitForStorage[1].contains(" ") &&
                        (dataSplitForStorage[12].equals("LMT") || dataSplitForStorage[12].equals("STPLMT") ||
                                dataSplitForStorage[12].equals("MKT"))) {
                    lineCount++;
                }
            }

            // Now that we know the number of lines, initialize the array
            allTradeExecutions = new String[lineCount][8];


            // Process the file again to populate the array
            try (BufferedReader br2 = new BufferedReader(new FileReader(file))) {
                int leftIndexCount = 0;

                while ((line = br2.readLine()) != null) {
                    String[] dataSplitForStorage = line.split(commaSplit);

                    if (dataSplitForStorage.length > 12 && dataSplitForStorage[1].contains(" ") &&
                            (dataSplitForStorage[12].equals("LMT") || dataSplitForStorage[12].equals("STPLMT") ||
                                    dataSplitForStorage[12].equals("MKT"))) {

                        // Single Array Format
                        // Date, time, pos effect, side, ticker, size, price, order type
                        String[] dateTimeSplit = dataSplitForStorage[1].split(" ");
                        date = dateTimeSplit[0];
                        time = dateTimeSplit[1];
                        posEffect = dataSplitForStorage[5];
                        tradeSide = dataSplitForStorage[3];
                        stockTicker = dataSplitForStorage[6];

                        if (dataSplitForStorage[4].startsWith("-") || dataSplitForStorage[4].startsWith("+")) {
                            tradeSize = dataSplitForStorage[4].substring(1);
                        }

                        tradePrice = dataSplitForStorage[10];
                        orderType = dataSplitForStorage[12];

                        allTradeExecutions[leftIndexCount][0] = date;
                        allTradeExecutions[leftIndexCount][1] = time;
                        allTradeExecutions[leftIndexCount][2] = posEffect;
                        allTradeExecutions[leftIndexCount][3] = tradeSide;
                        allTradeExecutions[leftIndexCount][4] = stockTicker;
                        allTradeExecutions[leftIndexCount][5] = tradeSize;
                        allTradeExecutions[leftIndexCount][6] = tradePrice;
                        allTradeExecutions[leftIndexCount][7] = orderType;

                        leftIndexCount++;
                    }
                }

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return allTradeExecutions;
    }





    // Method simply used to verify array data allocation and validity
    public static void arrayInitializationTesting(String[][] arrayToPrint, int lineCounter) {
        // Array Initialization Checker
        System.out.println("Line Count: " + lineCounter);
        // Print information for each trade execution
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.println(arrayToPrint[i][j]);
            }
        }
    }





    // Method Which Allocates and returns a formatted 2D Array holding positions and their transactions
    public static int[][] countTotalTradePositions(String[][] arrayToPrint, int lineCount) {

        // arrayToPrint format
        // Date, time, pos effect, side, ticker, size, price, order type

        LocalDate dateHolder = null;
        String tickerHolder = "";
        int sizeHolder = 0;
        int tempSizeHolder = 0;
        int numOfPositions = 0;
        int rightIndexPlacer = 0;

        // Set to keep track of processed lines
        Set<Integer> processedLines = new HashSet<>();

        // 2D Array to store all trades of a position
        int[][] positionTransactions = new int[lineCount][10];

        // Looping is correct: CONFIRMED/
        for (int i = lineCount - 1; i >= 0; i--) {
            // System.out.println("Line: " + (i + 1) +  " = " + arrayToPrint[i][4]);
            // Tester Above

            tickerHolder = arrayToPrint[i][4];
            dateHolder = LocalDate.parse(arrayToPrint[i][0], DateTimeFormatter.ofPattern("M/d/yy"));
            sizeHolder = Integer.parseInt(arrayToPrint[i][5]);

            // Check if the line has already been processed
            if (processedLines.contains(i + 1)) {
                continue;  // Skip the iteration if the line has been processed

            }
            positionTransactions[i][0] = i + 1;
            rightIndexPlacer = 1;





            int j = i - 1;
            while (j >= 0) {
                LocalDate threeDayRange = LocalDate.parse(arrayToPrint[j][0], DateTimeFormatter.ofPattern("M/d/yy"));

                // Searches between a 3 day range
                if (dateHolder.minusDays(3).isBefore(threeDayRange)) {

                    // Check if the line has not been processed before
                    if (!processedLines.contains(j + 1)) {
                        tempSizeHolder = Integer.parseInt(arrayToPrint[j][5]);

                        if (tickerHolder.equals(arrayToPrint[j][4]) && sizeHolder != 0 &&
                                arrayToPrint[j][2].equals("TO CLOSE")) {


                            positionTransactions[i][rightIndexPlacer] = j + 1;
                            sizeHolder -= tempSizeHolder;
                            rightIndexPlacer++;

                            // Just a accuracy checker can remove once, everything is right
                            if (sizeHolder == 0){
                                numOfPositions++;
                            }
                            processedLines.add(j + 1);

                        }
                        else if ((tickerHolder.equals(arrayToPrint[j][4])) && (sizeHolder != 0) &&
                                (arrayToPrint[j][2].equals("TO OPEN"))){
                            positionTransactions[i][rightIndexPlacer] = j + 1;
                            sizeHolder += tempSizeHolder;
                            rightIndexPlacer++;
                            processedLines.add(j + 1);
                        }
                        else if (sizeHolder != 0 && tickerHolder.equals(arrayToPrint[j][4])){
                            System.out.println("ERROR IN countTotalTradePositions METHOD.");
                            System.out.println("METHOD NEEDS UPDATING");

                            System.out.println("POSSIBLE CAUSES:");
                            System.out.println("- Method only reads trades within the next 3 day range");
                            System.out.println("- Program can't handle overfills yet (Ex. Open 1 share, " +
                                    "but close with 2 shares)");
                            System.out.println("- Program can only handle 10 trades per position");
                        }

                        // Add more conditions and actions based on different scenarios
                        // else if (additional condition) { additional action }
                        // else if (another condition) { another action }
                        // ...
                    }
                }
                j--;
            }
        }



        // Loop To Print out the whole array
        System.out.println("Position Transactions:");
        for (int i = 0; i < lineCount; i++) {
            System.out.print("Position " + (i + 1) + ": ");
            for (int j = 0; j < 10; j++) {
                if (positionTransactions[i][j] != 0) {
                    System.out.print(positionTransactions[i][j] + " ");
                }
            }
            System.out.println();
        }

        return positionTransactions;

    }

}

