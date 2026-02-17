package domain;

import java.time.Instant;
import java.util.UUID;

public class Transaction
{
  private final UUID id;
  private final UUID portfolioId;
  private final String stockSymbol;
  public TransactionType type;
  private final int quantity;
  private final double pricePerShare;
  private final double totalAmount;
  private final double fee;
  private final Instant timestamp;

  public Transaction(UUID id, UUID portfolioId, String stockSymbol,
      Instant timestamp, double fee, double pricePerShare, int quantity,
      TransactionType type)
  {
    this.id = id;
    this.portfolioId = portfolioId;
    this.stockSymbol = stockSymbol;
    this.timestamp = timestamp;
    this.fee = fee;
    this.totalAmount = (quantity * pricePerShare) + fee;
    this.pricePerShare = pricePerShare;
    this.quantity = quantity;
    this.type = type;
  }

  public UUID getId()
  {
    return id;
  }

  public UUID getPortfolioId()
  {
    return portfolioId;
  }

  public String getStockSymbol()
  {
    return stockSymbol;
  }

  public int getQuantity()
  {
    return quantity;
  }

  public double getPricePerShare()
  {
    return pricePerShare;
  }

  public double getTotalAmount()
  {
    return totalAmount;
  }

  public double getFee()
  {
    return fee;
  }

  public Instant getTimestamp()
  {
    return timestamp;
  }

  public TransactionType getType()
  {
    return type;
  }
}
