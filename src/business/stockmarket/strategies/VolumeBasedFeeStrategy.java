package business.stockmarket.strategies;

public class VolumeBasedFeeStrategy implements FeeCalculationStrategy
{
  private final double feePerShare;

  public VolumeBasedFeeStrategy(double pricePerShare, double feePerShare)
  {
    this.feePerShare = feePerShare;
  }

  @Override public double calculateFee(int quantity, double pricePerShare)
  {
    return quantity * feePerShare;
  }
}
