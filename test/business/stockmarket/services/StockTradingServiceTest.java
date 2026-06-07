package business.stockmarket.services;

import business.dto.StockTransactionRequest;
import business.stockmarket.strategies.FeeCalculationStrategy;
import business.stockmarket.strategies.FlatFeeStrategy;
import domain.Portfolio;
import domain.State;
import domain.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.fileImplementation.*;
import persistence.interfaces.*;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class StockTradingServiceTest
{
  private FileUnitOfWork uow;
  private PortfolioDAO portfolioDAO;
  private StockDAO stockDAO;
  private OwnedStockDAO ownedStockDAO;
  private TransactionDAO transactionDAO;
  private FeeCalculationStrategy feeStrategy;
  private StockTradingService tradingService;
  private GameService gameService;
  private UUID portfolioId;

  @BeforeEach
  public void setUp()
  {
    // Vi bruger en separat 'test_data' mappe, så vi ikke rører ved rigtige filer
    uow = new FileUnitOfWork("test_data");
    portfolioDAO = new FilePortfolioDAO(uow);
    stockDAO = new FileStockDAO(uow);
    ownedStockDAO = new FileOwnedStockDAO(uow);
    transactionDAO = new FileTransactionDAO(uow);

    feeStrategy = new FlatFeeStrategy(5.0);
    tradingService = new StockTradingService(uow, portfolioDAO, stockDAO, ownedStockDAO, transactionDAO, feeStrategy);
    gameService = new GameService(uow, portfolioDAO, stockDAO, ownedStockDAO, transactionDAO);

    // Nulstil og seed et friskt testmiljø
    portfolioId = gameService.startNewGame();
  }

  @Test
  public void testBuyStockSuccessfully()
  {
    StockTransactionRequest request = new StockTransactionRequest(portfolioId, "AAPL", 10);

    tradingService.buy(request);

    Portfolio portfolio = portfolioDAO.getById(portfolioId).orElseThrow();
    // Startbalance var 100.000. 10 * 150 = 1500 kr. + gebyr trækkes fra
    assertTrue(portfolio.getCurrentBalance() < 100000.0);
    assertFalse(ownedStockDAO.getAll().isEmpty());
  }

  @Test
  public void testBuyStockThrowsExceptionWhenInsufficientFunds()
  {
    // Forsøg at købe 5000 aktier (langt over budgettet på 100.000 kr)
    StockTransactionRequest request = new StockTransactionRequest(portfolioId, "AAPL", 5000);

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      tradingService.buy(request);
    });

    // Tjek at den dybereliggende årsag (cause) eller selve beskeden indeholder den rigtige forklaring
    assertTrue(exception.getMessage().contains("Failed to process buy order"));
    assertTrue(exception.getCause().getMessage().contains("You are too broke for this"));
  }

  @Test
  public void testSellStockSuccessfully()
  {
    // 1. Køb først 10 aktier
    StockTransactionRequest buyRequest = new StockTransactionRequest(portfolioId, "AAPL", 10);
    tradingService.buy(buyRequest);

    Portfolio portfolioAfterBuy = portfolioDAO.getById(portfolioId).orElseThrow();
    double balanceAfterBuy = portfolioAfterBuy.getCurrentBalance();

    // 2. Sælg 5 af dem igen
    StockTransactionRequest sellRequest = new StockTransactionRequest(portfolioId, "AAPL", 5);
    tradingService.sell(sellRequest);

    Portfolio portfolioAfterSell = portfolioDAO.getById(portfolioId).orElseThrow();
    // Balancen skal stige efter salg (minus gebyr)
    assertTrue(portfolioAfterSell.getCurrentBalance() > balanceAfterBuy);
  }
}