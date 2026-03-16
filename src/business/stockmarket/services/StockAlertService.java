package business.stockmarket.services;

import business.stockmarket.StockMarket;
import business.stockmarket.events.StockUpdateEvent;
import domain.State;
import shared.logging.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class StockAlertService implements PropertyChangeListener
{
  public StockAlertService()
  {
    StockMarket.getInstance().addPropertyChangeListener(this);
  }

  @Override public void propertyChange(PropertyChangeEvent e)
  {
    if ("STOCK_UPDATED".equals(e.getPropertyName()))
    {
      StockUpdateEvent updateEventData = (StockUpdateEvent) e.getNewValue();

      if (updateEventData.currentState() == State.BANKRUPT)
      {
        Logger.getInstance().log("CRITICAL",
            "!" + updateEventData.symbol() + " has gone BANKRUPT!");
      }

      if (updateEventData.currentState() == State.WILDING)
      {
        if (updateEventData.previousState() != State.WILDING)
        {
          Logger.getInstance().log("ALERT",
              " " + updateEventData.symbol() + " has entered WILDING state!");
        }
      }

      if (updateEventData.currentState() == State.RESET)
      {
        Logger.getInstance().log("INFO",
            updateEventData.symbol() + " has REVIVED from bankruptcy!");
      }

      if (updateEventData.currentState() == State.STEADY)
      {
        if (updateEventData.previousState() == State.WILDING
            || updateEventData.previousState() == State.RESET)
        {
          Logger.getInstance()
              .log("INFO", updateEventData.symbol() + " has been stabilized.");
        }
      }
    }
  }
}