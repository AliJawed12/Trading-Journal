public class AccountTradeHistoryV5 {

    public static void main(String[] args) {

        String multipleTradeFile = "/Users/Jawed/OneDrive/Desktop/2023-12-31-AccountStatement.csv";
        String twoTradeFile = "/Users/Jawed/OneDrive/Desktop/twoTradeAccountStatement.csv";
        String customTradeFile = "/Users/Jawed/OneDrive/Desktop/customTradeFile.txt";
        String customTradeFile2 = "/Users/Jawed/OneDrive/Desktop/customTradeFileV2.txt";
        String customTradeFile3 = "/Users/Jawed/OneDrive/Desktop/customTradeFileV3.txt";

        AccTradeHistory tradeHistoryProcessor = new AccTradeHistory(customTradeFile3);

        // Accessing data using getters
        String[][] allTradeExecutions = tradeHistoryProcessor.getAllTradeExecutions();
        int lineCount = tradeHistoryProcessor.getLineCount();
        int[][] allTransactionsPerPositions = tradeHistoryProcessor.getAllTransactionsPerPositions();
        int numOfPositions = tradeHistoryProcessor.getTotalPositions();
        String[][] reversed = tradeHistoryProcessor.getAllTradesReversed();
        String[][] arranged = tradeHistoryProcessor.getArrangedPositions();

        

    }
}
