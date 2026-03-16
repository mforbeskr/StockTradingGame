package business.stockmarket;

import business.stockmarket.events.StockUpdateEvent;
import business.stockmarket.simulation.BankruptState;
import business.stockmarket.simulation.LiveStock;
import business.stockmarket.simulation.WildingState;
import domain.State;
import domain.Stock;
import persistence.interfaces.StockDAO;
import shared.logging.Logger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class StockMarket
{
  private static final StockMarket instance = new StockMarket(); // singleton
  private final List<LiveStock> liveStocks;
  private final Logger logger = Logger.getInstance();
  private final PropertyChangeSupport support;

  private StockMarket()
  {
    this.liveStocks = new ArrayList<>();
    this.support = new PropertyChangeSupport(this);
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
      State oldState = State.valueOf(
          stock.getCurrentState().getStateName().toUpperCase());

      stock.updatePrice();

      State newState = State.valueOf(
          stock.getCurrentState().getStateName().toUpperCase());

      String stateName = stock.getCurrentState().getStateName().toUpperCase();

      logger.log("UPDATE",
          ": " + stock.getStockSymbol() + " | Price: " + stock.getCurrentPrice()
              + " | State: " + stateName);

      StockUpdateEvent updateEventData = new StockUpdateEvent(
          stock.getStockSymbol(),
          stock.getCurrentState().getStateName().toUpperCase(),
          stock.getCurrentPrice(), newState, oldState);

      support.firePropertyChange("STOCK_UPDATED", null, updateEventData);
    }
  }

  public void addNewStock(String symbol)
  {
    liveStocks.add(new LiveStock(symbol));
  }

  public void addPropertyChangeListener(PropertyChangeListener listener)
  {
    support.addPropertyChangeListener(listener);
  }

  // may be usefull to be able to remove listening at some point..
  public void removePropertyChangeListener(PropertyChangeListener listener)
  {
    support.removePropertyChangeListener(listener);
  }

  public List<LiveStock> getLiveStocks()
  {
    return List.copyOf(liveStocks);
  }

  public static StockMarket getInstance()
  {
    return instance;
  }
}
