package business.stockmarket.strategies;

public interface FeeCalculationStrategy
{
  double calculateFee(int quantity, double pricePerShare);
}
