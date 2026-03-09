package business.stockmarket.simulation;

import java.util.Random;

public class DecliningState implements StockState
{
  private static final Random random = new Random();

  @Override public double calculatePriceChange(LiveStock stock)
  {
    return (Math.random() * 3.0) - 2.5;
  }

  @Override public void updateState(LiveStock stock)
  {
    double chance = Math.random();
    if (chance < 0.10)
    {
      stock.setState(new SteadyState());
    }
    else if (chance < 0.15)
    {
      stock.setState(new GrowingState());
    }
  }

  @Override public String getStateName()
  {
    return "Declining";
  }
}