package business.stockmarket.simulation;

import business.stockmarket.StockMarket;
import shared.configuration.AppConfig;
import shared.logging.Logger;

import java.util.Random;

public class MarketUpdateTask implements Runnable
{
  private boolean running = true;
  private final Logger logger = Logger.getInstance();

  @SuppressWarnings("BusyWait") @Override public void run()
  {
    Random random = new Random();
    int baseFrequency = AppConfig.getInstance().getUpdateFrequencyInMs();
    logger.log("INFO",
        "Market Update Thread started with an irregular update frequency!");

    while (running){
      StockMarket.getInstance().update();
      try
      {
        int irregularity = random.nextInt(baseFrequency);
        int sleepTime = (baseFrequency / 2) + irregularity;
        Thread.sleep(sleepTime);
      }
      catch (InterruptedException e)
      {
        running = false;
        logger.log("WARNING", "Market Thread was interrupted");
      }
    }
  }

  public void stop()
  {
    this.running = false;
  }
}
