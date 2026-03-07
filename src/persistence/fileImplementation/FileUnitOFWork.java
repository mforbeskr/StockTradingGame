package persistence.fileImplementation;

import domain.*;
import persistence.interfaces.UnitOfWork;
import shared.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class FileUnitOFWork implements UnitOfWork
{
  private final String directoryPath;
  private static final Object file_write_lock = new Object();
  private List<Portfolio> portfolios;
  private List<Stock> stocks;
  private List<OwnedStock> ownedStocks;
  private List<Transaction> transactions;
  private List<StockPriceHistory> stockPriceHistories;
  private final List<Transaction> transactionBuffer = new ArrayList<>();
  private final List<StockPriceHistory> stockPriceHistoryBuffer = new ArrayList<>();
  private static final String portfolios_file = "portfolios.psv";
  private static final String stocks_file = "stocks.psv";
  private static final String owned_stocks_file = "ownedStocks.psv";
  private static final String transactions_file = "transactions.psv";
  private static final String stock_price_history_file = "stockPriceHistory.psv";

  public FileUnitOFWork(String directoryPath)
  {
    this.directoryPath = directoryPath;
    ensureFilesExist();
  }

  private void ensureFilesExist()
  {
    try
    {
      Files.createDirectories(Paths.get(directoryPath));
      String[] fileNames = {stocks_file, portfolios_file, owned_stocks_file,
          transactions_file, stock_price_history_file};
      for (String name : fileNames)
      {
        File file = new File(directoryPath + "/" + name);
        if (!file.exists())
          file.createNewFile();
      }
    }
    catch (IOException e)
    {
      throw new RuntimeException("Could not initialize storage files", e);
    }
  }

  @Override public void beginTransaction()
  {
    clearData();

  }

  @Override public void rollback()
  {
    clearData();
  }

  @Override public void commit()
  {
    synchronized (file_write_lock)
    {
      if (stocks != null)
        saveToFile(stocks_file, stocks, this::stockToPSV);

      if (portfolios != null)
        saveToFile(portfolios_file, portfolios, this::portfolioToPSV);

      if (ownedStocks != null)
        saveToFile(owned_stocks_file, ownedStocks, this::ownedStockToPSV);

      if (!transactionBuffer.isEmpty())
      {
        List<String> transactionLines = transactionBuffer.stream()
            .map(this::transactionToPSV).toList();

        appendLinesToFile(transactions_file, transactionLines);
      }

      if (!stockPriceHistoryBuffer.isEmpty())
      {
        List<String> historyLines = stockPriceHistoryBuffer.stream()
            .map(this::stockPriceHistoryToPSV).toList();

        appendLinesToFile(stock_price_history_file, historyLines);
      }
    }
    clearData(); // Reset all the in-memory lists after saving/committing
  }

  private void clearData()
  {
    this.stocks = null;
    this.portfolios = null;
    this.ownedStocks = null;
    this.transactions = null;
    this.stockPriceHistories = null;
    this.transactionBuffer.clear();
    this.stockPriceHistoryBuffer.clear();
  }

  // StockPSV
  private String stockToPSV(Stock stock)
  {
    return stock.getSymbol() + "|" + stock.getName() + "|"
        + stock.getCurrentPrice() + "|" + stock.getCurrentState().name();
  }

  private Stock stockFromPSV(String psv)
  {
    String[] parts = psv.split("\\|");

    String symbol = parts[0];
    String name = parts[1];
    double price = Double.parseDouble(parts[2]);
    State state = State.valueOf(parts[3].toUpperCase());

    return new Stock(symbol, name, price, state);
  }

  // PortfolioPSV
  private String portfolioToPSV(Portfolio portfolio)
  {
    return portfolio.getId() + "|" + portfolio.getName() + "|"
        + portfolio.getCurrentBalance();
  }

  private Portfolio portfolioFromPSV(String psv)
  {
    String[] parts = psv.split("\\|");

    UUID id = UUID.fromString(parts[0]);
    String name = parts[1];
    double currentBalance = Double.parseDouble(parts[2]);

    return new Portfolio(id, name, currentBalance);
  }

  // OwnedStockPSV
  private String ownedStockToPSV(OwnedStock ownedStock)
  {
    return ownedStock.getId() + "|" + ownedStock.getNumberOfShares() + "|"
        + ownedStock.getPortfolioId() + "|" + ownedStock.getStockSymbol();
  }

  private OwnedStock ownedStockFromPSV(String psv)
  {
    String[] parts = psv.split("\\|");

    UUID id = UUID.fromString(parts[0]);
    int numberOfShares = Integer.parseInt(parts[1]);
    UUID portfolioId = UUID.fromString(parts[2]);
    String stockSymbol = parts[3];

    return new OwnedStock(id, numberOfShares, portfolioId, stockSymbol);
  }

  // TransactionPSV
  private String transactionToPSV(Transaction transaction)
  {
    return transaction.getId() + "|" + transaction.getPortfolioId() + "|"
        + transaction.getStockSymbol() + "|" + transaction.getType() + "|"
        + transaction.getQuantity() + "|" + transaction.getPricePerShare() + "|"
        + transaction.getTotalAmount() + "|" + transaction.getFee() + "|"
        + transaction.getTimestamp();
  }

  private Transaction transactionFromPSV(String psv)
  {
    String[] parts = psv.split("\\|");

    UUID id = UUID.fromString(parts[0]);
    UUID portfolioId = UUID.fromString(parts[1]);
    String stockSymbol = parts[2];
    TransactionType transactionType = TransactionType.valueOf(
        parts[3].toUpperCase());
    int quantity = Integer.parseInt(parts[4]);
    double pricePerShare = Double.parseDouble(parts[5]);
    double totalAmount = Double.parseDouble(parts[6]);
    double fee = Double.parseDouble(parts[7]);
    Instant timestamp = Instant.parse(parts[8]);

    return new Transaction(id, portfolioId, stockSymbol, transactionType,
        quantity, pricePerShare, totalAmount, fee, timestamp);
  }

  // StockPriceHistoryPSV
  private String stockPriceHistoryToPSV(StockPriceHistory stockPriceHistory)
  {
    return stockPriceHistory.getId() + "|" + stockPriceHistory.getStockSymbol()
        + "|" + stockPriceHistory.getPrice() + "|"
        + stockPriceHistory.getTimeStamp();
  }

  private StockPriceHistory stockPriceHistoryFromPSV(String psv)
  {
    String[] parts = psv.split("\\|");

    UUID id = UUID.fromString(parts[0]);
    String stockSymbol = parts[1];
    double price = Double.parseDouble(parts[2]);
    Instant timestamp = Instant.parse(parts[3]);

    return new StockPriceHistory(id, stockSymbol, price, timestamp);
  }

  // Read
  private List<String> readAllLines(String fileName)
  {
    try
    {
      return Files.readAllLines(Paths.get(directoryPath + "/" + fileName));
    }
    catch (IOException e)
    {
      throw new RuntimeException("Failed to read from file: " + fileName, e);
    }
  }

  // Load from file
  private <T> List<T> loadList(String fileName, Function<String, T> parser)
  {
    List<T> list = new ArrayList<>();
    List<String> lines = readAllLines(fileName);
    for (String line : lines)
    {
      if (line != null && !line.isBlank())
      {
        list.add(parser.apply(line));
      }
    }
    return list;
  }

  // Save to file
  private <T> void saveToFile(String fileName, List<T> list,
      Function<T, String> mapper)
  {
    List<String> lines = new ArrayList<>();
    for (T item : list)
    {
      lines.add(mapper.apply(item));
    }
    try
    {
      Files.write(Paths.get(directoryPath + "/" + fileName), lines);
    }
    catch (IOException e)
    {
      throw new RuntimeException("Failed to write to file: " + fileName, e);
    }
  }

  // Append to file
  private void appendLinesToFile(String fileName, List<String> list)
  {
    try
    {
      Files.write(Path.of(directoryPath + "/" + fileName), list,
          StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
    catch (IOException e)
    {
      Logger.getInstance().log("ERROR",
          "Append failed for " + fileName + ": " + e.getMessage());
      throw new RuntimeException("Persistence Error: Append failed", e);
    }
  }

  // getters
  @Override public List<Stock> getStocks()
  {
    if (this.stocks == null)
      stocks = loadList(stocks_file, this::stockFromPSV);
    return this.stocks;
  }

  @Override public List<Portfolio> getPortfolios()
  {
    if (this.portfolios == null)
      portfolios = loadList(portfolios_file, this::portfolioFromPSV);
    return this.portfolios;
  }

  @Override public List<OwnedStock> getOwnedStocks()
  {
    if (this.ownedStocks == null)
      ownedStocks = loadList(owned_stocks_file, this::ownedStockFromPSV);
    return this.ownedStocks;
  }

  @Override public List<Transaction> getTransactions()
  {
    if (this.transactions == null)
      transactions = loadList(transactions_file, this::transactionFromPSV);
    return this.transactions;
  }

  @Override public List<StockPriceHistory> getStockPriceHistory()
  {
    if (this.stockPriceHistories == null)
      stockPriceHistories = loadList(stock_price_history_file,
          this::stockPriceHistoryFromPSV);
    return this.stockPriceHistories;
  }

  public List<Transaction> getTransactionBuffer()
  {
    return transactionBuffer;
  }

  public List<StockPriceHistory> getStockPriceHistoryBuffer()
  {
    return stockPriceHistoryBuffer;
  }
}
