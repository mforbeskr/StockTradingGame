package business.stockmarket.services;

import business.stockmarket.StockMarket;
import business.stockmarket.events.StockUpdateEvent;
import domain.OwnedStock;
import domain.State;
import persistence.interfaces.OwnedStockDAO;
import persistence.interfaces.UnitOfWork;
import shared.logging.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class StockBankruptService implements PropertyChangeListener
{
  private final OwnedStockDAO ownedStockDAO;
  private final UnitOfWork uow;


  public StockBankruptService(OwnedStockDAO ownedStockDAO, UnitOfWork uow)
  {
    this.ownedStockDAO = ownedStockDAO;
    this.uow = uow;
    StockMarket.getInstance().addPropertyChangeListener(this);
  }

  @Override public void propertyChange(PropertyChangeEvent e)
  {
    if ("STOCK_UPDATED".equals(e.getPropertyName()))
    {
      StockUpdateEvent updateEventData = (StockUpdateEvent) e.getNewValue();

      if (updateEventData.currentState() == State.BANKRUPT)
      {
        handleBankruptcy(updateEventData.symbol());
      }
    }
  }

  private void handleBankruptcy(String symbol)
  {
    List<OwnedStock> allOwned = ownedStockDAO.getAll();

    for (OwnedStock ownedStock : allOwned)
    {
      if (ownedStock.getStockSymbol().equalsIgnoreCase(symbol))
      {
        ownedStockDAO.delete(ownedStock.getId());
        Logger.getInstance().log("CRITICAL",
            "BANKRUPTCY CLEANUP: Removed" + symbol + " from Portfolio: "
                + ownedStock.getPortfolioId());

        uow.commit();
      }
    }
  }
}
