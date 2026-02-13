package domain;

import java.util.UUID;

public class OwnedStock
{

  private final UUID id;
  private int numberOfShares;
  private final UUID portfolioId;
  private final String stockSymbol;

  public OwnedStock(UUID id, int numberOfShares, UUID portfolio,
      String stock)
  {
    this.id = id;
    this.numberOfShares = numberOfShares;
    this.portfolioId = portfolio;
    this.stockSymbol = stock;
  }

  public UUID getId()
  {
    return id;
  }

  public int getNumberOfShares()
  {
    return numberOfShares;
  }

  public void setNumberOfShares(int numberOfShares)
  {
    this.numberOfShares = numberOfShares;
  }

  public UUID getPortfolio()
  {
    return portfolioId;
  }

  public String getStockSymbol()
  {
    return stockSymbol;
  }
}
