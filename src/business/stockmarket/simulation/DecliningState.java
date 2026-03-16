package business.stockmarket.simulation;

import java.util.Random;

public class DecliningState implements StockState
{
  private static final Random random = new Random();

  @Override public double calculatePriceChange(LiveStock stock)
  {
    return (random.nextDouble() * 3.0) - 2.5;
  }

  @Override public void updateState(LiveStock stock)
  {
    StockState next = TransitionManager.getInstance().getNextRandomState(this);
    if (next != null)
    {
      stock.setState(next);
    }
  }

  @Override public String getStateName()
  {
    return "Declining";
  }
}