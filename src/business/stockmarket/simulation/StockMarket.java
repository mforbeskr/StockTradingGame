package business.stockmarket.simulation;

import domain.Stock;
import persistence.interfaces.StockDAO;
import shared.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class StockMarket
{
  private static final StockMarket instance = new StockMarket();
  private final List<LiveStock> liveStocks;
  private final Logger logger = Logger.getInstance();

  private StockMarket()
  {
    this.liveStocks = new ArrayList<>();
  }

  public static synchronized StockMarket getInstance()
  {
    return instance;
  }


  public void initialize(StockDAO stockDao)
  {
    liveStocks.clear();
    List<Stock> savedStocks = stockDao.getAll();

    for (Stock s : savedStocks)
    {
      LiveStock live = new LiveStock(s.getSymbol(), s.getCurrentPrice(),
          s.getCurrentState().toString());
      liveStocks.add(live);
    }
    logger.log("INFO",
        "StockMarket initialized with " + liveStocks.size() + " stocks.");
  }

  public void update()
  {
    for (LiveStock stock : liveStocks)
    {
      stock.updatePrice();
    }
  }

  public List<LiveStock> getLiveStocks()
  {
    return List.copyOf(liveStocks);
  }
}
