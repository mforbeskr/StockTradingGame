package business.stockmarket.simulation;

import java.util.Random;

public class WildingState implements StockState
{
  private static final Random random = new Random();

  @Override public double calculatePriceChange(LiveStock liveStock)
  {
    // Big swings .... (-10.0 to +10.0)
    double change = (random.nextDouble() * 20) - 10;

    // High 20% chance of SteadyState
    if (random.nextDouble() < 0.20)
    {
      liveStock.setState(new SteadyState());
    }
    return change;
  }

  @Override public String getStateName()
  {
    return "Wilding";
  }

  @Override public void updateState(LiveStock liveStock)
  {
    double chance = random.nextDouble();

    // 20% chance to calm down and go Steady
    if (chance < 0.20)
    {
      liveStock.setState(new SteadyState());
    }
    // 5% chance to turn into Declining
    else if (chance < 0.25)
    {
      liveStock.setState(new DecliningState());
    }
    // 75% chance the stockState stays Wilding for another tick
  }
}


