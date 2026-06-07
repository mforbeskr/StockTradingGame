package persistence.interfaces;

import domain.*;

import java.util.List;

public interface UnitOfWork
{
  void beginTransaction();
  void rollback();
  void commit();
  void clearAllPhysicalData();
  List<Stock> getStocks();
  List<Portfolio> getPortfolios();
  List<OwnedStock> getOwnedStocks();
  List<Transaction> getTransactions();
  List<StockPriceHistory> getStockPriceHistory();


}
