package business.stockmarket.services;

import business.dto.StockTransactionRequest;
import business.stockmarket.strategies.FeeCalculationStrategy;
import business.stockmarket.strategies.FlatFeeStrategy;
import domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.interfaces.*;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class BuyStockTest {
  private MockUnitOfWork uow;
  private PortfolioDAO portfolioDAO;
  private StockDAO stockDAO;
  private OwnedStockDAO ownedStockDAO;
  private TransactionDAO transactionDAO;
  private FeeCalculationStrategy feeStrategy;
  private StockTradingService tradingService;

  private UUID portfolioId;
  private final String STOCK_SYMBOL = "AAPL";

  @BeforeEach
  public void setUp() {
    uow = new MockUnitOfWork();
    portfolioDAO = new MockPortfolioDAO(uow);
    stockDAO = new MockStockDAO(uow);
    ownedStockDAO = new MockOwnedStockDAO(uow);
    transactionDAO = new MockTransactionDAO(uow);

    Stock apple = new Stock(STOCK_SYMBOL, "Apple Inc.", 100.0, State.STEADY);
    stockDAO.create(apple);

    Portfolio portfolio = new Portfolio("Test Portfolio", 1000.0);
    portfolioId = portfolio.getId();
    portfolioDAO.create(portfolio);

    feeStrategy = new FlatFeeStrategy(5.0);
    tradingService = new StockTradingService(uow, portfolioDAO, stockDAO, ownedStockDAO, transactionDAO, feeStrategy);
  }

  @Test
  public void testBuySingleShare_Succeeds() {
    StockTransactionRequest request = new StockTransactionRequest(portfolioId, STOCK_SYMBOL, 1);
    tradingService.buy(request);
    assertFalse(ownedStockDAO.getAll().isEmpty());
    assertEquals(1, ownedStockDAO.getAll().get(0).getNumberOfShares());
  }

  @Test
  public void testBuyZeroShares_Fails() {
    StockTransactionRequest request = new StockTransactionRequest(portfolioId, STOCK_SYMBOL, 0);
    assertThrows(RuntimeException.class, () -> tradingService.buy(request));
  }

  @Test
  public void testBuyNewStock_CreatesRecord() {
    StockTransactionRequest request = new StockTransactionRequest(portfolioId, STOCK_SYMBOL, 1);
    tradingService.buy(request);
    assertEquals(1, ownedStockDAO.getAll().size());
  }

  @Test
  public void testBuyExistingStock_UpdatesQuantity() {
    tradingService.buy(new StockTransactionRequest(portfolioId, STOCK_SYMBOL, 2));
    tradingService.buy(new StockTransactionRequest(portfolioId, STOCK_SYMBOL, 3));
    assertEquals(1, ownedStockDAO.getAll().size());
    assertEquals(5, ownedStockDAO.getAll().get(0).getNumberOfShares());
  }

  @Test
  public void testBuyJustOneCentTooExpensive_Fails() {
    Portfolio p = portfolioDAO.getById(portfolioId).orElseThrow();
    p.setCurrentBalance(99.99);

    StockTransactionRequest request = new StockTransactionRequest(portfolioId, STOCK_SYMBOL, 1);
    assertThrows(RuntimeException.class, () -> tradingService.buy(request));
  }

  @Test
  public void testBuyNegativeQuantity_Fails() {
    StockTransactionRequest request = new StockTransactionRequest(portfolioId, STOCK_SYMBOL, -5);
    assertThrows(RuntimeException.class, () -> tradingService.buy(request));
  }

  @Test
  public void testBuyNullSymbol_Fails() {
    StockTransactionRequest request = new StockTransactionRequest(portfolioId, null, 5);
    assertThrows(RuntimeException.class, () -> tradingService.buy(request));
  }

  @Test
  public void testBuyNonExistingSymbol_Fails() {
    StockTransactionRequest request = new StockTransactionRequest(portfolioId, "NONEXIST", 5);
    assertThrows(RuntimeException.class, () -> tradingService.buy(request));
  }

  @Test
  public void testBuyUpdatesPortfolioBalance() {
    StockTransactionRequest request = new StockTransactionRequest(portfolioId, STOCK_SYMBOL, 2);
    tradingService.buy(request);
    Portfolio p = portfolioDAO.getById(portfolioId).orElseThrow();
    assertTrue(p.getCurrentBalance() < 1000.0);
  }

  @Test
  public void testBuyCreatesTransactionRecord() {
    StockTransactionRequest request = new StockTransactionRequest(portfolioId, STOCK_SYMBOL, 1);
    tradingService.buy(request);
    assertFalse(transactionDAO.getAll().isEmpty());
    assertEquals(TransactionType.BUY, transactionDAO.getAll().get(0).getType());
  }
}