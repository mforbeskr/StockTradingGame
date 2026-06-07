package business.stockmarket.services;

import business.stockmarket.StockMarket;
import domain.Portfolio;
import domain.State;
import domain.Stock;
import persistence.interfaces.*;

import java.util.List;
import java.util.UUID;

public class GameService
{
  private final UnitOfWork uow;
  private final PortfolioDAO portfolioDAO;
  private final StockDAO stockDAO;
  private final OwnedStockDAO ownedStockDAO;
  private final TransactionDAO transactionDAO;

  public GameService(UnitOfWork uow, PortfolioDAO portfolioDAO,
      StockDAO stockDAO, OwnedStockDAO ownedStockDAO,
      TransactionDAO transactionDAO)
  {
    this.uow = uow;
    this.portfolioDAO = portfolioDAO;
    this.stockDAO = stockDAO;
    this.ownedStockDAO = ownedStockDAO;
    this.transactionDAO = transactionDAO;
  }

  public UUID initOrLoadGame()
  {
    return portfolioDAO.getAll().stream().findFirst().map(Portfolio::getId)
        .orElseGet(this::startNewGame);
  }

  public UUID startNewGame()
  {
    try
    {
      uow.clearAllPhysicalData();

      uow.beginTransaction();

      clearAllData();

      List<Stock> defaultStocks = List.of(
          new Stock("AAPL", "Apple Inc.", 175.50, State.STEADY),
          new Stock("MSFT", "Microsoft Corp.", 420.20, State.STEADY),
          new Stock("GOOGL", "Alphabet Inc.", 170.80, State.STEADY),
          new Stock("NVDA", "NVIDIA Corporation", 900.00, State.STEADY),
          new Stock("AMD", "Advanced Micro Devices", 160.25, State.STEADY),

          new Stock("TSLA", "Tesla Inc.", 175.00, State.STEADY),
          new Stock("F", "Ford Motor Company", 12.10, State.STEADY),

          new Stock("NFLX", "Netflix Inc.", 610.50, State.STEADY),
          new Stock("SONY", "Sony Group Corp.", 85.30, State.STEADY),
          new Stock("NTDOY", "Nintendo Co. Ltd.", 13.40, State.STEADY),

          new Stock("AMZN", "Amazon.com Inc.", 185.10, State.STEADY),
          new Stock("BABA", "Alibaba Group Holding", 72.40, State.STEADY),

          new Stock("XOM", "Exxon Mobil Corp.", 118.20, State.STEADY),
          new Stock("JPM", "JPMorgan Chase & Co.", 195.60, State.STEADY),

          new Stock("COIN", "Coinbase Global Inc.", 225.00, State.STEADY),
          new Stock("GME", "GameStop Corp.", 18.50, State.STEADY));

      for (Stock stock : defaultStocks)
      {
        stockDAO.create(stock);
      }

      StockMarket.getInstance().initialize(stockDAO);

      Portfolio startingPortfolio = new Portfolio("Main Portfolio", 100000.0);
      portfolioDAO.create(startingPortfolio);

      uow.commit();
      return startingPortfolio.getId();
    }
    catch (Exception e)
    {
      uow.rollback();
      throw new RuntimeException("Failed to start or reset the game", e);
    }
  }

  private void clearAllData()
  {
    ownedStockDAO.getAll().forEach(os -> ownedStockDAO.delete(os.getId()));
    transactionDAO.clearAll();
    stockDAO.getAll().forEach(s -> stockDAO.delete(s.getSymbol()));
    portfolioDAO.getAll().forEach(p -> portfolioDAO.delete(p.getId()));
  }
}