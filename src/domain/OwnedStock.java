package domain;

import java.util.UUID;

public class OwnedStock
{

  private final UUID id;
  private int numberOfShares;
  private final UUID portfolioId;
  private final String stockSymbol;

  public OwnedStock(int numberOfShares, UUID portfolioId,
      String stockSymbol)
  {
    this.id = UUID.randomUUID();
    this.numberOfShares = numberOfShares;
    this.portfolioId = portfolioId;
    this.stockSymbol = stockSymbol;
  }

  public OwnedStock(UUID id, int numberOfShares, UUID portfolioId,
      String stockSymbol)
  {
    this.id = id;
    this.numberOfShares = numberOfShares;
    this.portfolioId = portfolioId;
    this.stockSymbol = stockSymbol;
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

  public UUID getPortfolioId()
  {
    return portfolioId;
  }

  public String getStockSymbol()
  {
    return stockSymbol;
  }
}
