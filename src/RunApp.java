import shared.configuration.AppConfig;
import shared.logging.ConsoleLogOutput;
import shared.logging.Logger;

public class RunApp
{
  public static void main(String[] args)
  {
    Logger logger = Logger.getInstance();
    logger.setOutput(new ConsoleLogOutput());
    logger.log("INFO", "Application started");

    AppConfig appConfig = AppConfig.getInstance();

    // Log data from appConfig
    logger.log("INFO", "Starting Balance: " + appConfig.getStartingBalance());
    logger.log("INFO", "Transaction Fee: " + appConfig.getTransactionFee());
    logger.log("INFO", "Stock Reset Value: " + appConfig.getStockResetValue());
    logger.log("INFO", "Update Frequency in ms: " + appConfig.getUpdateFrequencyInMs());

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


  }
}
