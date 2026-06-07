package presentation.controllers;

import business.dto.StockDTO;
import domain.State;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.util.Duration;
import presentation.core.ViewManager;
import presentation.views.StockMarketViewModel;
import shared.configuration.ApplicationContext;

import java.net.URL;
import java.util.ResourceBundle;

public class StockMarketController implements Initializable
{
  private final StockMarketViewModel viewModel;
  private final ViewManager viewManager;
  private final ApplicationContext appContext;
  private Timeline marketRefreshTimeline;

  @FXML private Button portfolioNavButton;
  @FXML private TextField quantityField;
  @FXML private Button buyButton;
  @FXML private Button sellButton;
  @FXML private TableView<StockDTO> marketStocksTable;

  @FXML private TableColumn<StockDTO, String> symbolColumn;
  @FXML private TableColumn<StockDTO, Double> priceColumn;
  @FXML private TableColumn<StockDTO, State> stateColumn;
  @FXML private LineChart<Number, Number> stockPriceChart;

  public StockMarketController(StockMarketViewModel viewModel,
      ViewManager viewManager, ApplicationContext appContext)
  {
    this.viewModel = viewModel;
    this.viewManager = viewManager;
    this.appContext = appContext;
  }

  @Override public void initialize(URL location, ResourceBundle resources)
  {
    if (portfolioNavButton != null)
    {
      portfolioNavButton.setOnAction(event -> {
        stopRefreshTimeline();
        viewManager.openView("PortfolioView");
      });
    }
    symbolColumn.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().symbol()));

    priceColumn.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleObjectProperty<>(
            cellData.getValue().currentPrice()));
    formatDoubleColumn(priceColumn);

    stateColumn.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleObjectProperty<>(
            cellData.getValue().currentState()));

    marketStocksTable.setItems(viewModel.getAvailableStocks());

    marketStocksTable.getSelectionModel().selectedItemProperty()
        .addListener(((obs, oldSelection, newSelection) -> {
          viewModel.selectNewStock(newSelection);
        }));

    buyButton.setOnAction(event -> {
      int qty = getQuantityInput();
      if (qty > 0)
        viewModel.buySelectedStock(appContext.getCurrentPortfolioId(), qty);
    });

    sellButton.setOnAction(event -> {
      int qty = getQuantityInput();
      if (qty > 0)
        viewModel.sellSelectedStock(appContext.getCurrentPortfolioId(), qty);
    });
    stockPriceChart.setTitle("Market Tracking");
    stockPriceChart.setCreateSymbols(false);
    stockPriceChart.setAnimated(false);

    NumberAxis xAxis = (NumberAxis) stockPriceChart.getXAxis();
    xAxis.setForceZeroInRange(false);
    xAxis.setTickUnit(1.0);
    xAxis.setMinorTickCount(0);

    stockPriceChart.getData().add(viewModel.getStockPriceSeries());

    viewModel.loadMarketData();

    startRefreshTimeline();
  }

  private void startRefreshTimeline()
  {
    marketRefreshTimeline = new Timeline(
        new KeyFrame(Duration.seconds(1), event -> {
          StockDTO selectedStockBefore = marketStocksTable.getSelectionModel()
              .getSelectedItem();
          String selectedSymbol = (selectedStockBefore != null) ?
              selectedStockBefore.symbol() :
              null;

          viewModel.loadMarketData();

          if (selectedSymbol != null)
          {
            for (StockDTO stock : marketStocksTable.getItems())
            {
              if (stock.symbol().equalsIgnoreCase(selectedSymbol))
              {
                marketStocksTable.getSelectionModel().select(stock);
                viewModel.selectNewStock(stock);
                break;
              }
            }
          }
        }));
    marketRefreshTimeline.setCycleCount(Animation.INDEFINITE);
    marketRefreshTimeline.play();
  }

  private void stopRefreshTimeline()
  {
    if (marketRefreshTimeline != null)
    {
      marketRefreshTimeline.stop();
    }
  }

  private int getQuantityInput()
  {
    try
    {
      int qty = Integer.parseInt(quantityField.getText().trim());
      if (qty <= 0)
      {
        return 0;
      }
      return qty;
    }
    catch (NumberFormatException e)
    {
      return 0;
    }
  }

  private <T> void formatDoubleColumn(TableColumn<T, Double> column)
  {
    column.setCellFactory(tc -> new javafx.scene.control.TableCell<>()
    {
      @Override protected void updateItem(Double item, boolean empty)
      {
        super.updateItem(item, empty);
        setText((empty || item == null) ? null : String.format("%,.2f", item));
      }
    });
  }
}