// Class is a trading journal

import java.awt.*;

public class TradingJournal {

    // Private Data Members

    private String ticker;
    private String time;
    private String date;
    private String tradeSide; // Could keep as string or change to int and set 0 = bot, 1 = sell
    private int tradeSize;

    /*
    Additional Data Members could include
    - Duration of trade
    - Initial Risk
    - Status of Trade (Whether it's a win or loss)
     */

    // Could potentially add Data Members for trade tags for mistakes in javafx later

    // Data Members that will be accessed after taking notes on trade


    public TradingJournal () {
        ticker = "----";
        time = "----";
        date = "----";
        tradeSide = "----";
        tradeSize = 0;
    }

    public TradingJournal (String tickerIn, String timeIn, String dateIn, String tradeSideIn, int tradeSizeIn){
        ticker = tickerIn;
        time = timeIn;
        date = dateIn;
        tradeSide = tradeSideIn;
        tradeSize = tradeSizeIn;
    }

    // Set and Get Methods

    // Set and Get Methods for Ticker
    public void setTicker (String tickerIn) {
        ticker = tickerIn;
    }
    public String getTicker () {
        return ticker;
    }


    // Set and Get Methods for Time
    public void setTime (String timeIn) {
        time = timeIn;
    }
    public String getTime () {
        return time;
    }


    // Set and Get Methods for Date
    public void setDate (String dateIn) {
        date = dateIn;
    }
    public String getDate () {
        return date;
    }


    // Set and Get Methods for TradeSide
    public void setTradeSide (String tradeSideIn) {
        tradeSide = tradeSideIn;
    }
    public String getTradeSide () {
        return tradeSide;
    }


    // Set and Get Methods for TradeSize
    public void setTradeSize (int tradeSizeIn) {
        tradeSize = tradeSizeIn;
    }
    public int getTradeSize () {
        return tradeSize;
    }

    // Method for Date and Time

    // Method for R-Multiple
    //Pass in entry price, stop loss, and target price


}
