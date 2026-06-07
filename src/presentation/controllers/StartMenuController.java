package presentation.controllers;

import business.stockmarket.services.GameService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import presentation.core.ViewManager;
import shared.configuration.ApplicationContext;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

public class StartMenuController implements Initializable
{
  private final GameService gameService;
  private final ApplicationContext appContext;
  private final ViewManager viewManager;

  @FXML private Button continueButton;
  @FXML private Button newGameButton;

  public StartMenuController(GameService gameService,
      ApplicationContext appContext, ViewManager viewManager)
  {
    this.gameService = gameService;
    this.appContext = appContext;
    this.viewManager = viewManager;
  }

  @Override public void initialize(URL location, ResourceBundle resources)
  {
    continueButton.setOnAction(event -> {
      UUID id = gameService.initOrLoadGame();
      appContext.setCurrentPortfolioId(id);
      viewManager.openView("PortfolioView");
    });

    newGameButton.setOnAction(event -> {
      UUID id = gameService.startNewGame();
      appContext.setCurrentPortfolioId(id);
      viewManager.openView("PortfolioView");
    });
  }
}