package business.stockmarket.simulation;

import java.util.Random;

public class TransitionManager
{
  private static final TransitionManager instance = new TransitionManager(); // singleton
  private final Random random = new Random();

  private TransitionManager(){}

  public static TransitionManager getInstance()
  {
    return instance;
  }

  public StockState getNextRandomState(StockState currentState)
  {
    double rand = random.nextDouble();

    if (currentState instanceof SteadyState)
    {
      if (rand < 0.10)
        return new GrowingState();
      if (rand < 0.20)
        return new DecliningState();
      if (rand < 0.22)
        return new WildingState();
    }
    else if (currentState instanceof GrowingState)
    {
      if (rand < 0.10)
        return new SteadyState();
      if (rand < 0.15)
        return new DecliningState();
      if (rand < 0.16)
        return new WildingState();
    }
    else if (currentState instanceof DecliningState)
    {
      if (rand < 0.15)
        return new SteadyState();
      if (rand < 0.20)
        return new GrowingState();
      if (rand < 0.205)
        return new WildingState();
    }
    else if (currentState instanceof WildingState)
    {
      if (rand < 0.20)
      {
        return new SteadyState();
      }
    }
    else if (currentState instanceof BankruptState)
    {
      if (rand < 0.15)
        return new ResetState();
    }
    else if (currentState instanceof ResetState)
    {
      return new SteadyState();
    }
    return null;
  }

  public StockState getForcedNextState(StockState currentState)
  {
    StockState next = getNextRandomState(currentState);
    if (next == null)
    {
      return new SteadyState();
    }
    return next;
  }
}
