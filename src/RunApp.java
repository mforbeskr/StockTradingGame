import business.stockmarket.StockMarket;
import business.stockmarket.services.StockAlertService;
import business.stockmarket.services.StockBankruptService;
import business.stockmarket.services.StockListenerService;
import business.stockmarket.simulation.MarketUpdateTask;
import javafx.application.Application;
import javafx.stage.Stage;
import presentation.core.ControllerFactory;
import presentation.core.ViewManager;
import shared.configuration.ApplicationContext;
import shared.logging.ConsoleLogOutput;
import shared.logging.Logger;

public class RunApp extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
    Logger.getInstance().setOutput(new ConsoleLogOutput());

    ApplicationContext appContext = new ApplicationContext();
    ControllerFactory controllerFactory = new ControllerFactory(appContext);
    ViewManager viewManager = new ViewManager(primaryStage, controllerFactory);

    controllerFactory.setViewManager(viewManager);

    StockMarket.getInstance().initialize(appContext.getStockDAO()); // !!!!!

    new StockListenerService(appContext.getStockDAO(), appContext.getPriceHistoryDAO(), appContext.getUow());
    new StockAlertService();
    new StockBankruptService(appContext.getOwnedStockDAO(), appContext.getUow());

    MarketUpdateTask task = new MarketUpdateTask();
    Thread marketThread = new Thread(task);
    marketThread.setDaemon(true);
    marketThread.start();

    viewManager.openView("StartMenuView");
  }

  public static void main(String[] args) {
    launch(args);
  }
}