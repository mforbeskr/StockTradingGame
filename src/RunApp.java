import domain.*;
import persistence.fileImplementation.*;
import persistence.interfaces.*;
import shared.configuration.AppConfig;
import shared.logging.ConsoleLogOutput;
import shared.logging.Logger;

import java.time.Instant;
import java.util.UUID;

public class RunApp {
  public static void main(String[] args) {
    // 1. Setup Infrastructure
    Logger logger = Logger.getInstance();
    logger.setOutput(new ConsoleLogOutput());
    AppConfig appConfig = AppConfig.getInstance();
    String dataPath = appConfig.getDataPath();

    logger.log("INFO", "--- Starting Comprehensive Persistence & Append Test ---");

    // 2. Initialize Persistence Layer
    FileUnitOFWork uow = new FileUnitOFWork(dataPath);

    // Use Interfaces for the variables, Implementations for the objects
    StockDAO stockDao = new FileStockDAO(uow);
    PortfolioDAO portfolioDao = new FilePortfolioDAO(uow);
    OwnedStockDAO ownedStockDao = new FileOwnedStockDAO(uow);
    TransactionDAO transactionDao = new FileTransactionDAO(uow);
    StockPriceHistoryDAO historyDao = new FileStockPriceHistoryDAO(uow);

    try {
      uow.beginTransaction();
      logger.log("INFO", "Transaction started. Processing batch...");

      // --- PART A: STANDARD CREATION (Overwrite Logic) ---

      // Create Stock only if it doesn't exist to avoid duplicate errors
      String symbol = "AAPL";
      if (stockDao.getBySymbol(symbol).isEmpty()) {
        stockDao.create(new Stock(symbol, "Apple Inc.", 180.00, State.STEADY));
        logger.log("INFO", "[CREATE] New Stock added: " + symbol);
      } else {
        logger.log("INFO", "[SKIP] Stock " + symbol + " already exists.");
      }

      // Create a unique Portfolio for this specific test run
      UUID myPortId = UUID.randomUUID();
      portfolioDao.create(new Portfolio(myPortId, "Test Wallet", 50000.00));
      logger.log("INFO", "[CREATE] Portfolio created with ID: " + myPortId);

      // --- PART B: OPTIMIZED APPENDING (Append-Only Logic) ---

      // Simulate price fluctuations
      logger.log("INFO", "Appending price history updates...");
      historyDao.append(new StockPriceHistory(symbol, 180.50));
      historyDao.append(new StockPriceHistory(symbol, 181.20));

      // Simulate a trade
      logger.log("INFO", "Appending new transaction...");
      Transaction trade = new Transaction(
          myPortId,
          symbol,
          Instant.now(),
          appConfig.getTransactionFee(),
          181.20,
          10,
          TransactionType.BUY
      );
      transactionDao.append(trade);

      // --- PART C: THE COMMIT ---
      uow.commit();
      logger.log("INFO", "--- COMMIT SUCCESSFUL ---");

      // --- PART D: VERIFICATION ---
      logger.log("INFO", "Final Disk Audit:");
      logger.log("INFO", "Total Stocks: " + stockDao.getAll().size());
      logger.log("INFO", "Total Portfolios: " + portfolioDao.getAll().size());
      logger.log("INFO", "Total History Records: " + historyDao.getAll().size());
      logger.log("INFO", "Total Transactions: " + transactionDao.getAll().size());

    } catch (Exception e) {
      logger.log("ERROR", "CRITICAL FAILURE: " + e.getMessage());
      e.printStackTrace(); // Useful for debugging the "red" lines
      uow.rollback();
      logger.log("WARNING", "Rollback executed. No data was written to files.");
    }

    logger.log("INFO", "--- Test Run Finished ---");
  }
}