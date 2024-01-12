import com.sun.prism.shader.AlphaOne_Color_AlphaTest_Loader;

import java.io.*;
import java.sql.Time;
import java.time.LocalTime;
import java.util.Arrays;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Set;

public class AccountTradeHistory {
    public static void main(String[] args) {


        String multipleTradeFile = "/Users/Jawed/OneDrive/Desktop/2023-12-31-AccountStatement.csv";
        String twoTradeFile = "/Users/Jawed/OneDrive/Desktop/twoTradeAccountStatement.csv";
        String customTradeFile = "/Users/Jawed/OneDrive/Desktop/customTradeFile.txt";
        File newFile = new File(twoTradeFile);
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

        try (BufferedReader br = new BufferedReader(new FileReader(newFile))) {

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
            try (BufferedReader br2 = new BufferedReader(new FileReader(newFile))) {
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

        // Method to check if allocated array data is valid
        // arrayInitializationTesting(allTradeExecutions, lineCount);
        countTotalTradePositions(allTradeExecutions, lineCount);


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











    public static void countTotalTradePositions(String[][] arrayToPrint, int lineCount) {

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

            System.out.println("positionTransaction Array should hold line number " + (i + 1) + " for index " + i);
            System.out.println("positionTransaction Array holds line number " + positionTransactions[i][0] + " for index " + i);

            System.out.println("Line " + (i + 1) + ": " + arrayToPrint[i][4] + " open with " + arrayToPrint[i][5] +
                    " shares");

            //System.out.println("Line " + tickerHolder + ": ");

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

                            //System.out.println("Subtracting: " + arrayToPrint[j][4] + " line " + (j + 1) +
                            //        " from " + arrayToPrint[i][4] + " line " + (i + 1));
                            System.out.println("Line " + (j + 1) + " closes " + arrayToPrint[j][5] + " shares");
                            positionTransactions[i][rightIndexPlacer] = j + 1;
                            System.out.println("positionTransaction stores line " + positionTransactions[i][rightIndexPlacer]);
                            sizeHolder -= tempSizeHolder;
                            rightIndexPlacer++;

                            // Just a accuracy checker can remove once, everything is right
                            if (sizeHolder == 0){
                                System.out.println("Position Closed\n");
                                numOfPositions++;
                            }
                            processedLines.add(j + 1);
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

        System.out.println("Positions: " + numOfPositions);

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



    }

}
