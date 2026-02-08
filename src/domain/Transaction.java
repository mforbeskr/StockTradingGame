package domain;

import java.time.Instant;
import java.util.UUID;

public class Transaction
{
  private final UUID id;
  private final UUID portfolioId;
  private final String stockSymbol;
  public TransactionType type;
  private int quantity;
  private double pricePerShare;
  private double totalAmount;
  private double fee;
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

  public void setQuantity(int quantity)
  {
    this.quantity = quantity;
  }

  public void setPricePerShare(double pricePerShare)
  {
    this.pricePerShare = pricePerShare;
  }

  public void setTotalAmount(double totalAmount)
  {
    this.totalAmount = totalAmount;
  }

  public void setFee(double fee)
  {
    this.fee = fee;
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
