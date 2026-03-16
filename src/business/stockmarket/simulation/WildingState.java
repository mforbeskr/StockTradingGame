package business.stockmarket.simulation;

import java.util.Random;

public class WildingState implements StockState
{
  private static final Random random = new Random();
  private int ticksRemaining = 7;

  @Override public double calculatePriceChange(LiveStock liveStock)
  {
    return (random.nextDouble() * 20) - 10;
  }

  @Override public void updateState(LiveStock stock)
  {
    ticksRemaining--;
    // After maximum 7 ticks, it will go 'Steady'
    if (ticksRemaining <= 0)
    {
      stock.setState(TransitionManager.getInstance().getForcedNextState(this));
      return;
    }
    // TransitionManager checks if we exit WILDING State early (20%)
    StockState nextState = TransitionManager.getInstance()
        .getNextRandomState(this);
    if (nextState != null)
    {
      stock.setState(nextState);
    }
  }

  @Override public String getStateName()
  {
    return "Wilding";
  }
}
