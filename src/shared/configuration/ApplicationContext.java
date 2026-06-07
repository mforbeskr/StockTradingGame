package shared.configuration;

import business.stockmarket.services.*;
import business.stockmarket.strategies.*;
import persistence.fileImplementation.*;
import persistence.interfaces.*;
import presentation.core.AlertNotificationService;
import presentation.core.CustomAlertBoxAdapter;
import presentation.core.NotificationManager;
import provided.CustomAlertBox;
import provided.FileLogOutputter;

import java.util.UUID;

public class ApplicationContext {
  private final UnitOfWork uow;
  private final PortfolioDAO portfolioDAO;
  private final StockDAO stockDAO;
  private final OwnedStockDAO ownedStockDAO;
  private final TransactionDAO transactionDAO;
  private final StockPriceHistoryDAO priceHistoryDAO;

  private final GameService gameService;
  private final PortfolioService portfolioService;
  private final StockService stockService;
  private final StockTradingService stockTradingService;
  private final NotificationManager notificationService;

  private UUID currentPortfolioId;

  public ApplicationContext() {
    this.uow = new FileUnitOfWork("data");
    this.portfolioDAO = new FilePortfolioDAO((FileUnitOfWork) uow);
    this.stockDAO = new FileStockDAO((FileUnitOfWork) uow);
    this.ownedStockDAO = new FileOwnedStockDAO((FileUnitOfWork) uow);
    this.transactionDAO = new FileTransactionDAO((FileUnitOfWork) uow);
    this.priceHistoryDAO = new FileStockPriceHistoryDAO((FileUnitOfWork) uow);
    this.gameService = new GameService(uow, portfolioDAO, stockDAO, ownedStockDAO, transactionDAO);
    this.portfolioService = new PortfolioService(portfolioDAO, stockDAO, ownedStockDAO, transactionDAO);
    this.stockService = new StockService(stockDAO);

    FeeCalculationStrategy feeStrategy = new FlatFeeStrategy(5.00);
//    FeeCalculationStrategy feeStrategy = new PercentageFeeStrategy(0.014);
//    FeeCalculationStrategy feeStrategy = new ValueBasedStrategy(125.00,4.00,12.00);
//    FeeCalculationStrategy feeStrategy = new VolumeBasedFeeStrategy(125.00,6.00);
    this.stockTradingService = new StockTradingService(uow, portfolioDAO, stockDAO, ownedStockDAO, transactionDAO, feeStrategy);

    // from console to fileLog, adapter
    FileLogOutputter providedOutputter = new FileLogOutputter("data/logs.txt", "INFO");
    shared.logging.LogOutput fileLogAdapter = new shared.logging.FileLogAdapter(
        providedOutputter);
    shared.logging.Logger.getInstance().setOutput(fileLogAdapter);

    // adapt to using CustomAlertBox
    CustomAlertBox providedAlertBox = new CustomAlertBox();
    this.notificationService = new CustomAlertBoxAdapter(providedAlertBox);
    // original              = new AlertNotificationService();
  }

  public UnitOfWork getUow() { return uow; }

  public PortfolioDAO getPortfolioDAO() { return portfolioDAO; }
  public StockDAO getStockDAO() { return stockDAO; }
  public OwnedStockDAO getOwnedStockDAO() { return ownedStockDAO; }
  public TransactionDAO getTransactionDAO() { return transactionDAO; }
  public StockPriceHistoryDAO getPriceHistoryDAO() { return priceHistoryDAO; }
  public GameService getGameService() { return gameService; }
  public PortfolioService getPortfolioService() { return portfolioService; }
  public StockService getStockService() { return stockService; }
  public StockTradingService getStockTradingService() { return stockTradingService; }
  public NotificationManager getNotificationService() {return notificationService;}

  public UUID getCurrentPortfolioId() { return currentPortfolioId; }
  public void setCurrentPortfolioId(UUID currentPortfolioId) { this.currentPortfolioId = currentPortfolioId; }
}