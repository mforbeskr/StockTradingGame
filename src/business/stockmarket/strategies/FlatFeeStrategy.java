package business.stockmarket.strategies;

public class FlatFeeStrategy implements FeeCalculationStrategy
{
  private final double flatFeeAmount;

  public FlatFeeStrategy(double flatFeeAmount)
  {
    this.flatFeeAmount = flatFeeAmount;
  }

  @Override public double calculateFee(int quantity, double pricePerShare)
  {
    return flatFeeAmount;
  }
}
