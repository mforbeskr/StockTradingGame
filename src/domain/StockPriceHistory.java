package domain;

import java.time.Instant;
import java.util.UUID;

public class StockPriceHistory
{
  private final UUID id;
  private final String stockSymbol;
  private final double price;
  private final Instant timestamp;

  public StockPriceHistory(double price, String stockSymbol, UUID id,
      Instant timestamp)
  {
    this.price = price;
    this.stockSymbol = stockSymbol;
    this.id = id;
    this.timestamp = timestamp;
  }

  public UUID getId()
  {
    return id;
  }

  public String getStockSymbol()
  {
    return stockSymbol;
  }


  public double getPrice()
  {
    return price;
  }

  public Instant getTimestamp()
  {
    return timestamp;
  }
}
