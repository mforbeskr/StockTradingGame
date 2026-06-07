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

public class SellStockTest {
  private MockUnitOfWork uow;
  private PortfolioDAO portfolioDAO;
  private StockDAO stockDAO;
  private OwnedStockDAO ownedStockDAO;
  private TransactionDAO transactionDAO;
  private StockTradingService tradingService;
  private FeeCalculationStrategy feeStrategy;

  private UUID portfolioId;
  private final String STOCK_SYMBOL = "TSLA";

  @BeforeEach
  public void setUp() {
    uow = new MockUnitOfWork();
    portfolioDAO = new MockPortfolioDAO(uow);
    stockDAO = new MockStockDAO(uow);
    ownedStockDAO = new MockOwnedStockDAO(uow);
    transactionDAO = new MockTransactionDAO(uow);

    Stock tsla = new Stock(STOCK_SYMBOL, "Tesla Inc.", 200.0, State.STEADY);
    stockDAO.create(tsla);

    Portfolio portfolio = new Portfolio("Test Portfolio", 1000.0);
    portfolioId = portfolio.getId();
    portfolioDAO.create(portfolio);
    feeStrategy = new FlatFeeStrategy(5.0);
    tradingService = new StockTradingService(uow, portfolioDAO, stockDAO, ownedStockDAO, transactionDAO, feeStrategy);
  }

  @Test
  public void testSellShares_IncreasesBalance() {
    OwnedStock position = new OwnedStock(10, portfolioId, STOCK_SYMBOL);
    ownedStockDAO.create(position);

    double initialBalance = portfolioDAO.getById(portfolioId).orElseThrow().getCurrentBalance();
    StockTransactionRequest request = new StockTransactionRequest(portfolioId, STOCK_SYMBOL, 5);

    tradingService.sell(request);

    double newBalance = portfolioDAO.getById(portfolioId).orElseThrow().getCurrentBalance();
    assertTrue(newBalance > initialBalance);
    assertEquals(5, ownedStockDAO.getAll().get(0).getNumberOfShares());
  }

  @Test
  public void testSellMoreSharesThanOwned_Fails() {
    OwnedStock position = new OwnedStock(2, portfolioId, STOCK_SYMBOL);
    ownedStockDAO.create(position);

    StockTransactionRequest request = new StockTransactionRequest(portfolioId, STOCK_SYMBOL, 5);
    assertThrows(RuntimeException.class, () -> tradingService.sell(request));
  }

  @Test
  public void testSellAllShares_RemovesOrZerosRecord() {
    OwnedStock position = new OwnedStock(5, portfolioId, STOCK_SYMBOL);
    ownedStockDAO.create(position);

    StockTransactionRequest request = new StockTransactionRequest(portfolioId, STOCK_SYMBOL, 5);
    tradingService.sell(request);

    assertTrue(ownedStockDAO.getAll().isEmpty() || ownedStockDAO.getAll().get(0).getNumberOfShares() == 0);
  }

  @Test
  public void testSellZeroShares_Fails() {
    StockTransactionRequest request = new StockTransactionRequest(portfolioId, STOCK_SYMBOL, 0);
    assertThrows(RuntimeException.class, () -> tradingService.sell(request));
  }

  @Test
  public void testSellNegativeQuantity_Fails() {
    StockTransactionRequest request = new StockTransactionRequest(portfolioId, STOCK_SYMBOL, -5);
    assertThrows(RuntimeException.class, () -> tradingService.sell(request));
  }
}