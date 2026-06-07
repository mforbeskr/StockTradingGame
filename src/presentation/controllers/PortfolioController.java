package presentation.controllers;

import business.dto.OwnedStockOverviewDTO;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;
import presentation.core.ViewManager;
import presentation.views.PortfolioViewModel;
import shared.configuration.ApplicationContext;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

public class PortfolioController implements Initializable
{

  private final PortfolioViewModel viewModel;
  private final ViewManager viewManager;
  private final ApplicationContext appContext;
  private Timeline portfolioRefreshTimeline;

  @FXML private Button stockMarketNavButton;
  @FXML private Button resetGameButton;
  @FXML private Label cashBalanceLabel;
  @FXML private Label totalWorthLabel;
  @FXML private Label netProfitLossLabel;

  @FXML private TextField portfolioQuantityField;
  @FXML private Button portfolioSellButton;

  @FXML private TableView<OwnedStockOverviewDTO> ownedStocksTable;
  @FXML private TableColumn<OwnedStockOverviewDTO, String> symbolColumn;
  @FXML private TableColumn<OwnedStockOverviewDTO, Integer> sharesColumn;
  @FXML private TableColumn<OwnedStockOverviewDTO, Double> priceColumn;
  @FXML private TableColumn<OwnedStockOverviewDTO, Double> valueColumn;

  public PortfolioController(PortfolioViewModel viewModel,
      ViewManager viewManager, ApplicationContext appContext)
  {
    this.viewModel = viewModel;
    this.viewManager = viewManager;
    this.appContext = appContext;
  }

  @Override public void initialize(URL location, ResourceBundle resources)
  {
    if (stockMarketNavButton != null)
    {
      stockMarketNavButton.setOnAction(event -> {
        stopRefreshTimeline();
        viewManager.openView("StockMarketView");
      });
    }

    if (resetGameButton != null)
    {
      resetGameButton.setOnAction(event -> {
        UUID newGamePortfolioId = viewModel.resetGame();

        if (newGamePortfolioId != null)
        {
          appContext.setCurrentPortfolioId(newGamePortfolioId);
        }
      });
    }

    cashBalanceLabel.textProperty()
        .bind(Bindings.format("%,.2f", viewModel.availableCashProperty()));
    totalWorthLabel.textProperty()
        .bind(Bindings.format("%,.2f", viewModel.totalWorthProperty()));
    netProfitLossLabel.textProperty()
        .bind(Bindings.format("%,.2f", viewModel.netProfitLossProperty()));

    viewModel.netProfitLossProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal.doubleValue() >= 0)
      {
        netProfitLossLabel.setStyle(
            "-fx-text-fill: #00DB8B; -fx-font-name: 'System Bold';");
      }
      else
      {
        netProfitLossLabel.setStyle(
            "-fx-text-fill: #FF4A4A; -fx-font-name: 'System Bold';");
      }
    });

    symbolColumn.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().symbol()));
    sharesColumn.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleObjectProperty<>(
            cellData.getValue().sharesOwned()));

    priceColumn.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleObjectProperty<>(
            cellData.getValue().currentPrice()));

    valueColumn.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleObjectProperty<>(
            cellData.getValue().totalValue()));

    formatDoubleColumn(priceColumn);
    formatDoubleColumn(valueColumn);

    ownedStocksTable.setItems(viewModel.getOwnedStocksLiveList());

    portfolioSellButton.setOnAction(event -> {
      OwnedStockOverviewDTO selectedAsset = ownedStocksTable.getSelectionModel()
          .getSelectedItem();
      int qty = getQuantityInput();
      if (qty > 0 && selectedAsset != null)
      {
        viewModel.sellSelectedPosition(appContext.getCurrentPortfolioId(),
            selectedAsset, qty);
      }
    });

    viewModel.loadPortfolioData(appContext.getCurrentPortfolioId());
    startRefreshTimeline();
  }

  private int getQuantityInput()
  {
    try
    {
      int qty = Integer.parseInt(portfolioQuantityField.getText().trim());
      return Math.max(qty, 0);
    }
    catch (NumberFormatException e)
    {
      return 0;
    }
  }

  private void startRefreshTimeline()
  {
    portfolioRefreshTimeline = new Timeline(
        new KeyFrame(Duration.seconds(1), event -> {
          OwnedStockOverviewDTO selectedAssetBefore = ownedStocksTable.getSelectionModel()
              .getSelectedItem();
          String selectedSymbol = (selectedAssetBefore != null) ?
              selectedAssetBefore.symbol() :
              null;

          viewModel.loadPortfolioData(appContext.getCurrentPortfolioId());

          if (selectedSymbol != null)
          {
            for (OwnedStockOverviewDTO asset : ownedStocksTable.getItems())
            {
              if (asset.symbol().equalsIgnoreCase(selectedSymbol))
              {
                ownedStocksTable.getSelectionModel().select(asset);
                break;
              }
            }
          }
        }));
    portfolioRefreshTimeline.setCycleCount(Animation.INDEFINITE);
    portfolioRefreshTimeline.play();
  }

  private void stopRefreshTimeline()
  {
    if (portfolioRefreshTimeline != null)
    {
      portfolioRefreshTimeline.stop();
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