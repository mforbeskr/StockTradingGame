package domain;

import java.util.UUID;

public class Portfolio
{
  private final UUID id;
  private double currentBalance;

  public Portfolio(double currentBalance)
  {
    this.id = UUID.randomUUID();
    this.currentBalance = currentBalance;
  }

  // for EXISTING portfolio -> fromPSV usage (because ID not initialized before)
  public Portfolio(UUID id, double currentBalance)
  {
    this.id = id;
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

  public void setCurrentBalance(double currentBalance)
  {
    this.currentBalance = currentBalance;
  }
}
