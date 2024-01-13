/*
Ali Jawed

Program allows for reading of CSV files from thinkorswim rtading platform. Java does array and hash work to
extract trading details. Java then outputs technical and fundamental metrics of a position and allows
entry of metrics such as stop loss, price target, and a wide variety of potential mistakes.
 */

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

public class AccTradeHistory {

    // Private Data Members
    private String[][] allTrades;
    private String[][] allTradesReversed;
    private int lineCount;
    private int[][] allPositionTransactions;
    private int totalPositions;
    private String[][] arrangedPositions;


    // Overloaded Constructor, automatic file work to arrange array for processing
    public AccTradeHistory(String filePath) {
        File newFile = new File(filePath);
        allTrades = readAndProcessFile(newFile);
        lineCount = allTrades.length;
        allPositionTransactions = countTotalTradePositions(allTrades, lineCount);
        totalPositions = countAllPositions(allPositionTransactions, lineCount);
        allTradesReversed = reverseOrder(allTrades);
        arrangedPositions = arrangePositions(allPositionTransactions, allTrades, lineCount);
    }

    // Setter and Getter Methods
    //------------------------------------------------------------------------------------------------------------------

    // Setter and Getter for AllTradeExecutions
    public void setAllTradeExecutions(String[][] allTradeExecutions) {
        this.allTrades = allTradeExecutions;
    }

    public String[][] getAllTradeExecutions() {
        return allTrades;
    }

    // Setter and Getter for LineCount
    public void setLineCount(int countedLines) {
        this.lineCount = countedLines;
    }

    public int getLineCount() {
        return lineCount;
    }

    // Setter and Getter for AllTransactionsPerPosition
    public void setAllTransactionsPerPositions(int[][] allTransactionsPerPositions) {
        this.allPositionTransactions = allTransactionsPerPositions;
    }

    public int[][] getAllTransactionsPerPositions() {
        return allPositionTransactions;
    }

    // Setter and Getter for totalPositions
    public void setTotalPositions(int totalPositions) {
        this.totalPositions = totalPositions;
    }

    public int getTotalPositions() {
        return totalPositions;
    }

    // Setter and Getter Methods for allTradesReversed
    public void setAllTradesReversed(String[][] allTrades) {
        allTradesReversed = allTrades;
    }

    public String[][] getAllTradesReversed() {
        return allTradesReversed;
    }

    // Setter and Getter Methods for arrangedPostitions

    public void setArrangedPositions(String[][] arrangedPositions) {
        arrangedPositions = arrangedPositions;
    }

    public String[][] getArrangedPositions() {
        return arrangedPositions;
    }


    // Class Methods
    //------------------------------------------------------------------------------------------------------------------

    // Method to read file and then stores line in 2D array and returns the array
    private String[][] readAndProcessFile(File file) {

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
    private void arrayInitializationTesting(String[][] arrayToPrint, int lineCounter) {
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
    private int[][] countTotalTradePositions(String[][] arrayToPrint, int lineCount) {

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


            // Start while loop
            int j = i - 1;
            while (j >= 0) {
                // Assign date to allow for 3 day range search
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
                            if (sizeHolder == 0) {
                                numOfPositions++;
                            }
                            processedLines.add(j + 1);

                        } else if ((tickerHolder.equals(arrayToPrint[j][4])) && (sizeHolder != 0) &&
                                (arrayToPrint[j][2].equals("TO OPEN"))) {
                            positionTransactions[i][rightIndexPlacer] = j + 1;
                            sizeHolder += tempSizeHolder;
                            rightIndexPlacer++;
                            processedLines.add(j + 1);
                        } else if (sizeHolder != 0 && tickerHolder.equals(arrayToPrint[j][4])) {
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

    public int countAllPositions(int[][] totalTradePositions, int countedLines) {

        // Counter to count number of total positions
        int count = 0;

        for (int i = 0; i < countedLines; i++) {
            // Sentinal Value to check and see if index holds a value
            if (totalTradePositions[i][0] != 0) {
                count++;
            }
        }

        return count;
    }


    private String[][] reverseOrder(String[][] inputArray) {
        String[][] reversedArray = new String[inputArray.length][inputArray[0].length];
        for (int i = 0; i < inputArray.length; i++) {
            reversedArray[i] = Arrays.copyOf(inputArray[inputArray.length - 1 - i], inputArray[i].length);
        }
        return reversedArray;
    }


    public String[][] arrangePositions(int[][] totalTradePositions, String[][] allTrades, int countedLines) {

        // Initialize a new array to store trades in order
        // Since each position can have 10 transactions, multiply positions by 10 to initialize arrangedPositions Array
        String[][] arrangedPositions = new String[totalTradePositions.length * 10][8];

        // Identifier used to assist in grabbing and copying line from allTrades Array
        int targetLine = 0;
        // Identifier to track line of arrangedPositions Array
        int arrangedPositionArrayLine = 0;

        // Loop iteratates from the bottom to the top
        for (int i = lineCount - 1; i >= 0; i--) {

            // If != 0, it means that there is lines present. Then program will start reading those
            if (totalTradePositions[i][0] != 0) {

                // Holds the line number allowing for grabbing of line from array
                targetLine = i;

                // Copies whole line from allTrades into designated arrangedPositions array line
                System.arraycopy(allTrades[targetLine], 0, arrangedPositions[arrangedPositionArrayLine],
                        0, allTrades[targetLine].length);



                // Loop to grab the line number from the next index in the row
                for (int j = 1; totalTradePositions[i][j] != 0; j++){

                    if (totalTradePositions[i][j] != 0){
                        arrangedPositionArrayLine++; // Moves reader to next column in array to allow for copying
                        targetLine = totalTradePositions[i][j] - 1;
                        // Copies the line into the arrangedPositions line
                        System.arraycopy(allTrades[targetLine], 0, arrangedPositions[arrangedPositionArrayLine],
                                0, allTrades[targetLine].length);

                    }
                }

                /*
                Increases index by one to allow initalizing of null for next line
                Then once a line is filled, this makes the next line hold null, it's simply to seperate positions
                and to make reading this array easier for future array processing
                 */
                arrangedPositionArrayLine++;
                Arrays.fill(arrangedPositions[arrangedPositionArrayLine], null);

                // Increases index by one for the next loop reads a line number to store
                arrangedPositionArrayLine++;

            }


        }

        // Loop to print and check if lines valid
        // Lines are valid
        for (int i = 0; i < arrangedPositions.length; i++) {
            System.out.print("Line " + (i + 1) + ": ");

            for (int j = 0; j < arrangedPositions[i].length; j++) {
                System.out.print(arrangedPositions[i][j] + " ");
            }

            System.out.println(); // Move to the next line after printing the entire line
        }



        return arrangedPositions;
    }

    // Methods To Arrange File For Processing End Here
    //------------------------------------------------------------------------------------------------------------------

    // Methods to analyze trades
    //------------------------------------------------------------------------------------------------------------------
}
