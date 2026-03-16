import business.stockmarket.services.StockAlertService;
import business.stockmarket.services.StockBankruptService;
import business.stockmarket.services.StockListenerService;
import business.stockmarket.simulation.MarketUpdateTask;
import business.stockmarket.StockMarket;
import persistence.fileImplementation.FileOwnedStockDAO;
import persistence.fileImplementation.FileStockDAO;
import persistence.fileImplementation.FileUnitOfWork;
import persistence.interfaces.OwnedStockDAO;
import persistence.interfaces.StockDAO;
import shared.logging.ConsoleLogOutput;
import shared.logging.Logger;

public class RunApp {
  public static void main(String[] args) {
    // 1. Setup Logger og DAOs (UnitOfWork ville normalt klare dette)
    Logger logger = Logger.getInstance();
    logger.setOutput(new ConsoleLogOutput());

    // Du skal bruge dine DAOs til dine services
    // Her antages det at du har en måde at få fat i dine DAO implementeringer
    // F.eks. via din FileUnitOfWork:
    FileUnitOfWork uow = new FileUnitOfWork("data");
    StockDAO stockDAO = new FileStockDAO(uow);
    OwnedStockDAO ownedStockDAO = new FileOwnedStockDAO(uow);

    // 2. Setup Marked
    StockMarket market = StockMarket.getInstance();
    market.initialize(stockDAO); // Indlæser eksisterende aktier fra filen

    // 3. INSTANSIER SERVICES (Vigtigt for Observer Pattern!)
    // De tilmelder sig selv til markedet i deres constructorer
    new StockListenerService(stockDAO, uow);
    new StockAlertService();
    new StockBankruptService(ownedStockDAO, uow);

    // 4. Start simulationen
    MarketUpdateTask task = new MarketUpdateTask();
    Thread marketThread = new Thread(task);
    marketThread.setDaemon(true);
    marketThread.start();

    try {
      Thread.sleep(40000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}

