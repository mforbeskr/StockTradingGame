package shared.configuration;

public class AppConfig
{
  private static final AppConfig instance = new AppConfig();
  private final int startingBalance;
  private final double transactionFee;
  private final int updateFrequencyInMs;
  private final double stockResetValue;
  private final String dataPath;

  private AppConfig()
  {
    this.startingBalance = 10000;
    this.transactionFee = 2.50;
    this.updateFrequencyInMs = 2000;
    this.stockResetValue = 100.0;
    this.dataPath = "data";
  }

  public static AppConfig getInstance()
  {
    return instance;
  }

  public int getStartingBalance()
  {
    return startingBalance;
  }

  public double getTransactionFee()
  {
    return transactionFee;
  }

  public int getUpdateFrequencyInMs()
  {
    return updateFrequencyInMs;
  }

  public double getStockResetValue()
  {
    return stockResetValue;
  }

  public String getDataPath()
  {
    return dataPath;
  }
}