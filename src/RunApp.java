import domain.Portfolio;
import domain.State;
import domain.Stock;
import persistence.fileImplementation.FilePortfolioDAO;
import persistence.fileImplementation.FileStockDAO;
import persistence.fileImplementation.FileUnitOFWork;
import shared.configuration.AppConfig;
import shared.logging.ConsoleLogOutput;
import shared.logging.Logger;

import java.util.UUID;

public class RunApp
{
  public static void main(String[] args)
  {
    Logger logger = Logger.getInstance();
    logger.setOutput(new ConsoleLogOutput());
    logger.log("INFO", "Application started");
    AppConfig appConfig = AppConfig.getInstance();

    // Log data from appConfig
    logger.log("INFO", "Starting Balance: ¤" + appConfig.getStartingBalance());
    logger.log("INFO", "Transaction Fee: " + appConfig.getTransactionFee());
    logger.log("INFO", "Update Frequency in ms: " + appConfig.getUpdateFrequencyInMs());
    logger.log("INFO", "Stock Reset Value: " + appConfig.getStockResetValue());

    // Warning message testing
    logger.log("WARNING", "Stock not found in database");

    try
    {
      int result = 10 / 0;
    }
    catch (Exception e)
    {
      logger.log("ERROR", "Failed to calculate: " + e.getMessage());
    }

    System.out.println();
    logger.log("INFO", "--- Starting Application Persistence Test ---");
    try
    {
      // Load the configuration
      String dataPath = AppConfig.getInstance().getDataPath();
      logger.log("INFO", "Data path initialized to: " + dataPath);

      // create Unit of Work and DAOs
      FileUnitOFWork uow = new FileUnitOFWork(dataPath);
      FileStockDAO stockDao = new FileStockDAO(uow);
      FilePortfolioDAO portfolioDao = new FilePortfolioDAO(uow);

      // try entity creation
      logger.log("INFO", "Testing Entity Creation...");
      Stock testStock = new Stock("MSFT", "Microsoft", 400.00, State.GROWING);
      stockDao.create(testStock);

      Portfolio testPortfolio = new Portfolio(UUID.randomUUID(),
          "Growth Portfolio", 50000.00);
      portfolioDao.create(testPortfolio);

      // testing update method
      logger.log("INFO", "Testing Update Logic...");
      testPortfolio.setCurrentBalance(55000.00);
      portfolioDao.update(testPortfolio);

      // commit to the files
      logger.log("INFO", "Committing changes to .psv files...");
      uow.commit();

      // verification message
      logger.log("INFO",
          "Test complete. Stocks in system: " + stockDao.getAll().size());

    }
    catch (Exception e)
    {
      logger.log("ERROR", "Integration test failed: " + e.getMessage());
    }


  }
}
