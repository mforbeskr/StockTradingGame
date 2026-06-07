package business.stockmarket.services;

import business.dto.PortfolioOverviewDTO;
import business.dto.StockTransactionRequest;
import business.stockmarket.strategies.FeeCalculationStrategy;
import business.stockmarket.strategies.FlatFeeStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.fileImplementation.*;
import persistence.interfaces.*;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class PortfolioServiceTest
{
  private FileUnitOfWork uow;
  private PortfolioDAO portfolioDAO;
  private StockDAO stockDAO;
  private OwnedStockDAO ownedStockDAO;
  private TransactionDAO transactionDAO;
  private PortfolioService portfolioService;
  private FeeCalculationStrategy feeStrategy;
  private StockTradingService tradingService;
  private GameService gameService;
  private UUID portfolioId;

  @BeforeEach
  public void setUp()
  {
    uow = new FileUnitOfWork("test_data");
    portfolioDAO = new FilePortfolioDAO(uow);
    stockDAO = new FileStockDAO(uow);
    ownedStockDAO = new FileOwnedStockDAO(uow);
    transactionDAO = new FileTransactionDAO(uow);

    portfolioService = new PortfolioService(portfolioDAO, stockDAO, ownedStockDAO, transactionDAO);
    feeStrategy = new FlatFeeStrategy(5.0);
    tradingService = new StockTradingService(uow, portfolioDAO, stockDAO, ownedStockDAO, transactionDAO, feeStrategy);
    gameService = new GameService(uow, portfolioDAO, stockDAO, ownedStockDAO, transactionDAO);

    portfolioId = gameService.startNewGame();
  }

  @Test
  public void testGetPortfolioOverviewReflectsCorrectInitialBalance()
  {
    PortfolioOverviewDTO overview = portfolioService.getPortfolioOverview(portfolioId);

    assertEquals(100000.0, overview.balance());
    assertEquals(100000.0, overview.totalPortfolioValue());
  }

  @Test
  public void testGetPortfolioOverviewAfterPurchasingStock()
  {
    StockTransactionRequest request = new StockTransactionRequest(portfolioId, "AAPL", 10);
    tradingService.buy(request);

    PortfolioOverviewDTO overview = portfolioService.getPortfolioOverview(portfolioId);

    // Total formue bør ligge tæt på 100.000 kr. (kun faldet en smule pga. transaktionsgebyret)
    assertTrue(overview.totalPortfolioValue() < 100000.0);
    assertTrue(overview.totalPortfolioValue() > 99000.0);
    assertEquals(10, portfolioService.getOwnedStocksDetails(portfolioId).get(0).sharesOwned());
  }
}