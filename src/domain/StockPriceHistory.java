package domain;

import java.time.Instant;
import java.util.UUID;

public class StockPriceHistory
{
  private final UUID id;
  private final String stockSymbol;
  private final double price;
  private final Instant timeStamp;

  public StockPriceHistory(String stockSymbol, double price)
  {
    this.id = UUID.randomUUID();
    this.stockSymbol = stockSymbol;
    this.price = price;
    this.timeStamp = Instant.now();
  }

  // for EXISTING StockPriceHistory -> fromPSV usage (because ID not initialized before)
  public StockPriceHistory(UUID id, String stockSymbol, double price,
      Instant timeStamp)
  {
    this.id = id;
    this.stockSymbol = stockSymbol;
    this.price = price;
    this.timeStamp = timeStamp;
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

  public Instant getTimeStamp()
  {
    return timeStamp;
  }
}
