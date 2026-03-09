package business.stockmarket.simulation;

import shared.configuration.AppConfig;

public class ResetState implements StockState
{
  @Override public double calculatePriceChange(LiveStock stock)
  {
    // reset value from AppConfig
    double resetValue = AppConfig.getInstance().getStockResetValue();
    // Calculate the jump needed to reach that value from 0
    return resetValue - stock.getCurrentPrice();
  }

  @Override public void updateState(LiveStock stock)
  {
    // Immediately move back to Steady once the price is reset
    stock.setState(new SteadyState());
  }

  @Override public String getStateName()
  {
    return "Reset";
  }
}

