package domain;

public class Stock
{
  private final String symbol;
  private final String name;
  private double currentPrice;
  private State currentState;

  public Stock(String symbol, String name, double currentPrice,
      State currentState)
  {
    this.symbol = symbol;
    this.name = name;
    this.currentPrice = currentPrice;
    this.currentState = currentState;
  }

  public void setCurrentPrice(double currentPrice)
  {
    this.currentPrice = currentPrice;
  }

  public void setCurrentState(State currentState)
  {
    this.currentState = currentState;
  }

  public String getSymbol()
  {
    return symbol;
  }

  public String getName()
  {
    return name;
  }

  public double getCurrentPrice()
  {
    return currentPrice;
  }

  public State getCurrentState()
  {
    return currentState;
  }
}
