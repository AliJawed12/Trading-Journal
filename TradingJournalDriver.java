


import java.io.*;
import java.util.Scanner;

import static java.lang.String.valueOf;

public class TradingJournalDriver {

    public static void main (String[] args){

        String customTestFile = "/Users/Jawed/OneDrive/Desktop/javaTradingJournalTestFile.txt";
        String thinkOrSwimFile = "/Users/Jawed/OneDrive/Desktop/2023-12-27-AccountStatement.csv";
        File newFile = new File(thinkOrSwimFile);
        String line = "";
        String commaSplit = ",";




        // Initializing Data Members for data grabbing

        String dateer = "";
        String time = "";
        String orderSizeTickerPrice = "";
        String tradeSide = "";
        String tradeSize = "";
        String stockTicker = "";
        String tradePrice = "";
        String rawTotalCost = ""; // To get rid of the negative/positive sign
        Double totalCost = 0.0;
        String lastTestVal = ""; // Checking to see if it reads in the quotes
        try (BufferedReader br = new BufferedReader(new FileReader(newFile))){

            while ((line = br.readLine()) != null){
                String[] dataSplitForStorage = line.split(commaSplit);


                // If Branch to get initial Trade Entry Metrics
                if (dataSplitForStorage.length >= 7 && dataSplitForStorage[2].equals("TRD")){
                    dateer = dataSplitForStorage[0];
                    time = dataSplitForStorage[1];

                    orderSizeTickerPrice = dataSplitForStorage[4]; // contains 4 data pieces, the trade side,
                    // the trade size, the stock ticker, and the trade price.
                    // Breaking down orderSizeTickerPrice to grab the 4 pieces of data

                    String[] splitOrderSizeTickerPrice = orderSizeTickerPrice.split(" ");
                    tradeSide = splitOrderSizeTickerPrice[0];
                    tradeSize = splitOrderSizeTickerPrice[1].replaceAll("[^0-9]","");
                    stockTicker = splitOrderSizeTickerPrice[2];
                    tradePrice = splitOrderSizeTickerPrice[3].replaceAll("[^0-9.]","");

                    totalCost = Double.parseDouble(tradePrice) * Integer.parseInt(tradeSize);

                    /*
                    Ok, so the number this code is finding isn't really the total cost, the better approach is to just
                    multiple trade price by trade size

                    // Gets rid of the - or + from total cost then check to see if value is double
                    if (dataSplitForStorage[7].startsWith("-") || dataSplitForStorage[7].startsWith("+")){
                        rawTotalCost = dataSplitForStorage[7].substring(1);
                        if (rawTotalCost.contains(".")){
                            totalCost = rawTotalCost;
                        }
                        else {
                            totalCost = rawTotalCost + dataSplitForStorage[8];
                        }
                    }
                    else {

                        //This is just a precautionary branch, in the future if TOS as a platform changes it's CSV format,
                        //and stops including + or - in its total trade cost, this branch will execute letting me know to
                        //update the program

                        System.out.println("ERROR: Total Cost doesn't include + or - " +
                                "which is causing error in if else statement");
                        System.exit(0); // Terminates the program

                    }
                */

                }


                // If branch to allow reading of only filled trade orders in the csv file
                // if (line.contains(""))

                if (dataSplitForStorage.length > 12 && dataSplitForStorage[1].contains(" ") &&
                        (dataSplitForStorage[12].equals("LMT") || dataSplitForStorage[12].equals("STPLMT") ||
                                dataSplitForStorage[12].equals("MKT"))) {
                    // Your code here
                    String accTrdHisTest = dataSplitForStorage[3];
                    System.out.println("ACCOUNT TRADE HISTORY: " + accTrdHisTest);


                }



            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        System.out.println(dateer);
        System.out.println(time);
        System.out.println(tradeSide);
        System.out.println(tradeSize);
        System.out.println(stockTicker);
        System.out.println(tradePrice);
        System.out.println(totalCost);


        // For index 4 of dataSplitForStorage, need to split that index again to extract,
        // Whether bot or sold, how many shares, the ticker, as well as at what price

    }



}





