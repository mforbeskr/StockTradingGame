package domain;

import java.util.UUID;

public class Portfolio
{
  private final UUID id;
  private final String name;
  private double currentBalance;

  public Portfolio(String name, double currentBalance)
  {
    this.id = UUID.randomUUID();
    this.name = name;
    this.currentBalance = currentBalance;
  }

  // for EXISTING portfolio -> fromPSV usage (because ID not initialized before)
  public Portfolio(UUID id, String name, double currentBalance)
  {
    this.id = id;
    this.name = name;
    this.currentBalance = currentBalance;
  }

  public UUID getId()
  {
    return id;
  }

  public double getCurrentBalance()
  {
    return currentBalance;
  }

  public String getName()
  {
    return name;
  }

  public void setCurrentBalance(double currentBalance)
  {
    this.currentBalance = currentBalance;
  }
}
