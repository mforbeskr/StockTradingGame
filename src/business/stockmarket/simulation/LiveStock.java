package business.stockmarket.simulation;

import shared.configuration.AppConfig;
import shared.logging.Logger;

public class LiveStock
{
  private final String stockSymbol;
  private double currentPrice;
  private StockState currentState;

  public LiveStock(String symbol)
  {
    this.stockSymbol = symbol;
    this.currentPrice = AppConfig.getInstance().getStockResetValue();
    this.currentState = new SteadyState();
  }

  // for loading saved data
  public LiveStock(String symbol, double price, String stateName)
  {
    this.stockSymbol = symbol;
    this.currentPrice = price;
    this.currentState = mapStateNameToObject(stateName);
  }

  public void updatePrice()
  {
    double priceChange = currentState.calculatePriceChange(this);
    this.currentPrice += priceChange;

    if (this.currentPrice <= 0)
    {
      this.currentPrice = 0;
      this.setState(new BankruptState());
      Logger.getInstance().log("WARNING", stockSymbol + " has gone bankrupt!");
    }
    currentState.updateState(this);
  }

  private StockState mapStateNameToObject(String stateName)
  {
    return switch (stateName) {
      case "Growing" -> new GrowingState();
      case "Declining" -> new DecliningState();
      case "Bankrupt" -> new BankruptState();
      default -> new SteadyState();
    };
  }

  public void setState(StockState newState)
  {
    this.currentState = newState;
  }

  public String getStockSymbol()
  {
    return stockSymbol;
  }

  public StockState getCurrentState()
  {
    return currentState;
  }

  public double getCurrentPrice()
  {
    return currentPrice;
  }
}
