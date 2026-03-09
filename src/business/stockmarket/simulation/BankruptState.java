package business.stockmarket.simulation;

import java.util.Random;

public class BankruptState implements StockState
{
  private static final Random random = new Random();

  @Override public double calculatePriceChange(LiveStock stock)
  {
    return 0; // Bankrupt = price 0
  }

  @Override public void updateState(LiveStock stock)
  {
    // After a random duration of "waiting", move to Reset
    if (random.nextDouble() < 0.04)
    {
      stock.setState(new ResetState());
    }
  }

  @Override public String getStateName()
  {
    return "Bankrupt";
  }
}
