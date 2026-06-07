package business.stockmarket.strategies;

public class ValueBasedStrategy implements FeeCalculationStrategy
{
  private final double tierThreshold;
  private final double lowValueFee;
  private final double highValueFee;

  public ValueBasedStrategy(double tierThreshold, double lowValueFee,
      double highValueFee)
  {
    this.tierThreshold = tierThreshold;
    this.lowValueFee = lowValueFee;
    this.highValueFee = highValueFee;
  }

  @Override public double calculateFee(int quantity, double pricePerShare)
  {
    if (pricePerShare >= tierThreshold)
    {
      return highValueFee;
    }
    return lowValueFee;
  }
}
