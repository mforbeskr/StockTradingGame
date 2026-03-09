package business.stockmarket.simulation;

import java.util.Random;

public class SteadyState implements StockState
{
  private static final Random random = new Random();

  @Override public double calculatePriceChange(LiveStock liveStock)
  {
    //  up or down (-0.5 to 0.5)
    double change = (random.nextDouble()) - 0.5;

    double rand = random.nextDouble();
    if (rand < 0.05)
    {
      liveStock.setState(new GrowingState());
    }
    else if (rand < 0.10)
    {
      liveStock.setState(new DecliningState());

    }
    return change;
  }

  @Override public void updateState(LiveStock stock)
  {
    double chance = random.nextDouble();
    if (chance < 0.05)
    {
      stock.setState(new GrowingState()); // 5% chance to start growin
    }
    else if (chance < 0.10)
    {
      stock.setState(new DecliningState()); // 5% chance to start dipping
    }
  }

  @Override public String getStateName()
  {
    return "Steady";
  }
}