package business.stockmarket.strategies;

public class PercentageFeeStrategy implements FeeCalculationStrategy
{
  private final double percentageRate;

  public PercentageFeeStrategy(double percentageRate)
  {
    this.percentageRate = percentageRate;
  }

  @Override public double calculateFee(int quantity, double pricePerShare)
  {
    double totalTransactionValue = quantity * pricePerShare;
    return totalTransactionValue * percentageRate;
  }
}
