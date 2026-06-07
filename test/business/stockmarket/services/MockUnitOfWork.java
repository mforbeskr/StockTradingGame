package business.stockmarket.services;

import domain.*;
import persistence.interfaces.UnitOfWork;

import java.util.ArrayList;
import java.util.List;

public class MockUnitOfWork implements UnitOfWork
{
  public int commitCount = 0;
  public int rollbackCount = 0;
  public int clearCount = 0;

  private final List<Stock> stocks = new ArrayList<>();
  private final List<Portfolio> portfolios = new ArrayList<>();
  private final List<OwnedStock> ownedStocks = new ArrayList<>();
  private final List<Transaction> transactions = new ArrayList<>();
  private final List<StockPriceHistory> stockPriceHistories = new ArrayList<>();

  @Override public void clearAllPhysicalData()
  {
    this.clearCount++;
    this.stocks.clear();
    this.portfolios.clear();
    this.ownedStocks.clear();
    this.transactions.clear();
    this.stockPriceHistories.clear();
  }

  @Override public void beginTransaction()
  {
  }

  @Override public void rollback()
  {
    this.rollbackCount++;
  }

  @Override public void commit()
  {
    this.commitCount++;
  }

  @Override public List<Stock> getStocks()
  {
    return this.stocks;
  }

  @Override public List<Portfolio> getPortfolios()
  {
    return this.portfolios;
  }

  @Override public List<OwnedStock> getOwnedStocks()
  {
    return this.ownedStocks;
  }

  @Override public List<Transaction> getTransactions()
  {
    return this.transactions;
  }

  @Override public List<StockPriceHistory> getStockPriceHistory()
  {
    return this.stockPriceHistories;
  }
}