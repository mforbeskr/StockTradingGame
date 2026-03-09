package business.stockmarket.simulation;

import java.util.Random;

public class GrowingState implements StockState
{
  private static final Random random = new Random();

  @Override public double calculatePriceChange(LiveStock stock)
  {
    return (random.nextDouble() * 5.0) - 1.0;
  }

  @Override public void updateState(LiveStock stock)
  {
    if (random.nextDouble() < 0.1)
    {
      stock.setState(new SteadyState());
    }
  }

  @Override public String getStateName()
  {
    return "Growing";
  }
}
