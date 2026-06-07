package business.stockmarket.services;

import business.stockmarket.StockMarket;
import business.stockmarket.events.StockUpdateEvent;
import domain.StockPriceHistory;
import persistence.interfaces.StockDAO;
import persistence.interfaces.StockPriceHistoryDAO;
import persistence.interfaces.UnitOfWork;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class StockListenerService implements PropertyChangeListener
{
  private final StockDAO stockDAO;
  private final StockPriceHistoryDAO priceHistoryDAO;
  private final UnitOfWork uow;
  private final PropertyChangeSupport support = new PropertyChangeSupport(this);

  public StockListenerService(StockDAO stockDAO, StockPriceHistoryDAO priceHistoryDAO, UnitOfWork uow)
  {
    this.stockDAO = stockDAO;
    this.priceHistoryDAO = priceHistoryDAO;
    this.uow = uow;
    StockMarket.getInstance().addPropertyChangeListener(this);
  }

  public void addPropertyChangeListener(PropertyChangeListener listener)
  {
    support.addPropertyChangeListener(listener);
  }

  @Override public void propertyChange(PropertyChangeEvent e)
  {
    if ("STOCK_UPDATED".equalsIgnoreCase(e.getPropertyName()))
    {
      StockUpdateEvent updateEventData = (StockUpdateEvent) e.getNewValue();

      // SYNCHRONIZED block ensures the background thread can commit safely
      // without locking up against the UI thread timeline pulls!
      synchronized (uow)
      {
        stockDAO.getBySymbol(updateEventData.symbol()).ifPresent(stock -> {
          stock.setCurrentPrice(updateEventData.price());
          stock.setCurrentState(updateEventData.currentState());
          stockDAO.update(stock);

          priceHistoryDAO.create(new StockPriceHistory(stock.getSymbol(),
              updateEventData.price()));

          uow.commit();

          // Let the event fire cleanly to notify any observing layers
          support.firePropertyChange("Stock Updated", null, updateEventData);
        });
      }
    }
  }
}