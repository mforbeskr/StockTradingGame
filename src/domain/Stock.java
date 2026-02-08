package domain;

public class Stock
{
  private static final String STEADY = "Steady";
  private static final String GROWING = "Growing";
  private static final String DECLINING = "Declining";
  private static final String BANKRUPT = "Bankrupt";
  private static final String RESET = "Reset";

  private final String symbol;
  private final String name;
  private double currentPrice;
  private String currentState;

  public Stock(String symbol, String name, double currentPrice,
      String currentState)
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

  public void setCurrentState(String currentState)
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

  public String getCurrentState()
  {
    return currentState;
  }
}
