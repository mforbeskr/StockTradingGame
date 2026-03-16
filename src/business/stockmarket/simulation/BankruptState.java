package business.stockmarket.simulation;


public class BankruptState implements StockState
{
  private int cooldown = 5;

  @Override public double calculatePriceChange(LiveStock stock)
  {
    return (-stock.getCurrentPrice());
  }

  @Override public void updateState(LiveStock stock)
  {
    // minimum 5 ticks being on 'cooldown' --> 15% chance to reset
    if (cooldown > 0)
    {
      cooldown--;
      return;
    }
    StockState next = TransitionManager.getInstance().getNextRandomState(this);
    if (next != null)
    {
      stock.setState(next);
    }
  }

  @Override public String getStateName()
  {
    return "Bankrupt";
  }
}
