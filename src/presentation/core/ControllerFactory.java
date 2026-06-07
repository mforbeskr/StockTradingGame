package presentation.core;

import business.stockmarket.services.GameService;
import business.stockmarket.services.PortfolioService;
import business.stockmarket.services.StockService;
import business.stockmarket.services.StockTradingService;
import presentation.controllers.PortfolioController;
import presentation.controllers.StartMenuController;
import presentation.controllers.StockMarketController;
import presentation.views.PortfolioViewModel;
import presentation.views.StockMarketViewModel;
import shared.configuration.ApplicationContext;

public class ControllerFactory {
  private final ApplicationContext appContext;
  private ViewManager viewManager;

  public ControllerFactory(ApplicationContext appContext) {
    this.appContext = appContext;
  }

  public void setViewManager(ViewManager viewManager) {
    this.viewManager = viewManager;
  }

  public Object create(Class<?> controllerClass) {

    if (controllerClass == PortfolioController.class) {
      PortfolioService portfolioService = appContext.getPortfolioService();
      StockTradingService tradingService = appContext.getStockTradingService();
      GameService gameService = appContext.getGameService();
      NotificationManager notificationManager = appContext.getNotificationService();
      PortfolioViewModel viewModel = new PortfolioViewModel(portfolioService, tradingService, gameService,
          notificationManager);
      return new PortfolioController(viewModel, viewManager, appContext);
    }

    if (controllerClass == StockMarketController.class)
    {
      StockService stockService = appContext.getStockService();
      StockTradingService tradingService = appContext.getStockTradingService();
      NotificationManager notificationManager = appContext.getNotificationService();
      StockMarketViewModel viewModel = new StockMarketViewModel(stockService,
          tradingService, notificationManager);
      return new StockMarketController(viewModel, viewManager, appContext);
    }

    if (controllerClass == StartMenuController.class) {
      GameService gameService = appContext.getGameService();
      return new StartMenuController(gameService, appContext, viewManager);
    }

    try {
      return controllerClass.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Couldn't create controller: " + controllerClass.getName(), e);
    }
  }
}