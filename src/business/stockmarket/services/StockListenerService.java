package business.stockmarket.services;

import business.stockmarket.StockMarket;
import business.stockmarket.events.StockUpdateEvent;
import domain.Stock;
import persistence.interfaces.StockDAO;
import persistence.interfaces.UnitOfWork;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class StockListenerService implements PropertyChangeListener
{
  private final StockDAO stockDAO;
  private final UnitOfWork uow;
  private final PropertyChangeSupport support = new PropertyChangeSupport(this);

  public StockListenerService(StockDAO stockDAO, UnitOfWork uow)
  {
    this.stockDAO = stockDAO;
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

      Stock stockToPersist = new Stock(updateEventData.symbol(),
          updateEventData.symbol(), updateEventData.price(), updateEventData.currentState());

      stockDAO.update(stockToPersist);
      uow.commit();

      support.firePropertyChange("UI_UPDATE", null, updateEventData);
    }
  }
}
