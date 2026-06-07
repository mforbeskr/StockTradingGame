package business.stockmarket;

import business.dto.PortfolioOverviewDTO;
import business.dto.StockTransactionRequest;
import business.stockmarket.services.PortfolioService;
import business.stockmarket.services.StockTradingService;
import business.stockmarket.strategies.FeeCalculationStrategy;
import business.stockmarket.strategies.FlatFeeStrategy;
import domain.Portfolio;
import domain.State;
import domain.Stock;
import domain.Transaction;
import domain.TransactionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.fileImplementation.*;
import persistence.interfaces.*;

import java.io.File;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class StockTradingIntegrationTest {
  private FileUnitOfWork uow;
  private PortfolioDAO portfolioDAO;
  private StockDAO stockDAO;
  private OwnedStockDAO ownedStockDAO;
  private TransactionDAO transactionDAO;
  private FeeCalculationStrategy feeStrategy;
  private StockTradingService tradingService;
  private PortfolioService portfolioService;

  private UUID portfolioId;
  private final String STOCK_SYMBOL = "AAPL";

  @BeforeEach
  public void setUp() {
    // Setup temporary test directory
    uow = new FileUnitOfWork("test-data");
    portfolioDAO = new FilePortfolioDAO(uow);
    stockDAO = new FileStockDAO(uow);
    ownedStockDAO = new FileOwnedStockDAO(uow);
    transactionDAO = new FileTransactionDAO(uow);
    feeStrategy = new FlatFeeStrategy(5.0);
    tradingService = new StockTradingService(uow, portfolioDAO, stockDAO, ownedStockDAO, transactionDAO, feeStrategy);
    portfolioService = new PortfolioService(portfolioDAO, stockDAO, ownedStockDAO, transactionDAO);

    uow.beginTransaction();

    // Create baseline data in storage
    stockDAO.create(new Stock(STOCK_SYMBOL, "Apple Inc.", 150.0, State.STEADY));
    Portfolio portfolio = new Portfolio("Test Portfolio", 10000.0);
    portfolioDAO.create(portfolio);
    portfolioId = portfolio.getId();

    uow.commit();
  }

  @AfterEach
  public void tearDown() {
    // Wipe local test folder entirely
    File testDir = new File("test-data");
    if (testDir.exists()) {
      File[] files = testDir.listFiles();
      if (files != null) {
        for (File file : files) file.delete();
      }
      testDir.delete();
    }
  }

  @Test
  public void testBuyStock_Integration() {
    // Arrange: Pack the transaction into your DTO request object
    StockTransactionRequest request = new StockTransactionRequest(portfolioId, STOCK_SYMBOL, 10);

    // Act: Fire order through business tier
    tradingService.buy(request);

    // Assert: Verify state transitions down into files via DAOs
    Portfolio updatedPortfolio = portfolioDAO.getById(portfolioId).orElseThrow();

    // Account for fee inside balance calculations if configured in AppConfig
    assertTrue(updatedPortfolio.getCurrentBalance() < 10000.0);

    var ownedStocks = ownedStockDAO.getAll().stream()
        .filter(os -> os.getPortfolioId().equals(portfolioId))
        .toList();
    assertEquals(1, ownedStocks.size());
    assertEquals(10, ownedStocks.get(0).getNumberOfShares());
  }

  @Test
  public void testSellStock_Integration() {
    // Arrange: Pre-buy 10 shares to ensure we can sell safely
    StockTransactionRequest buyRequest = new StockTransactionRequest(portfolioId, STOCK_SYMBOL, 10);
    tradingService.buy(buyRequest);

    // Act: Sell 5 shares via service
    StockTransactionRequest sellRequest = new StockTransactionRequest(portfolioId, STOCK_SYMBOL, 5);
    tradingService.sell(sellRequest);

    // Assert: Verify half position remains in files
    var ownedStocks = ownedStockDAO.getAll().stream()
        .filter(os -> os.getPortfolioId().equals(portfolioId))
        .toList();
    assertEquals(1, ownedStocks.size());
    assertEquals(5, ownedStocks.get(0).getNumberOfShares());
  }

  @Test
  public void testViewPortfolioData_Integration() {
    // Arrange: Purchase assets to change the baseline stats
    StockTransactionRequest request = new StockTransactionRequest(portfolioId, STOCK_SYMBOL, 2);
    tradingService.buy(request);

    // Act: Extract complex view model data from PortfolioService
    PortfolioOverviewDTO overview = portfolioService.getPortfolioOverview(portfolioId);

    // Assert: Check structure maps properly based on simulated file storage
    assertNotNull(overview);
    assertEquals(300.0, overview.totalStockValue()); // 2 * 150
    assertEquals(overview.balance() + overview.totalStockValue(), overview.totalPortfolioValue());
  }

  @Test
  public void testViewTransactionHistory_Integration() {
    // Arrange: Generate clear operational tracking entries
    StockTransactionRequest buyRequest = new StockTransactionRequest(portfolioId, STOCK_SYMBOL, 4);
    tradingService.buy(buyRequest);

    StockTransactionRequest sellRequest = new StockTransactionRequest(portfolioId, STOCK_SYMBOL, 2);
    tradingService.sell(sellRequest);

    // Act: Query histories across layers
    List<Transaction> history = portfolioService.getTransactionHistory(portfolioId);

    // Assert: Verify order, types, and logging integrity
    assertEquals(2, history.size());
    assertEquals(TransactionType.BUY, history.get(0).getType());
    assertEquals(TransactionType.SELL, history.get(1).getType());
    assertEquals(4, history.get(0).getQuantity());
    assertEquals(2, history.get(1).getQuantity());
  }
}