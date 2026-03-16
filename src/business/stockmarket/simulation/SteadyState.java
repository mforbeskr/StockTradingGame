package business.stockmarket.simulation;


import java.util.Random;

public class SteadyState implements StockState
{
  private static final Random random = new Random();

  @Override public double calculatePriceChange(LiveStock liveStock)
  {
    return (random.nextDouble()) - 0.5;
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
    return "Steady";
  }
}