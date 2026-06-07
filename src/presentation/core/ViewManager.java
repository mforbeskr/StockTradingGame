package presentation.core;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewManager {
  private final Stage primaryStage;
  private final ControllerFactory controllerFactory;

  public ViewManager(Stage primaryStage, ControllerFactory controllerFactory) {
    this.primaryStage = primaryStage;
    this.controllerFactory = controllerFactory;
  }

  public void openView(String viewName) {
    try {
      String fxmlPath = "/presentation/views/" + viewName + ".fxml";
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
      loader.setControllerFactory(clazz -> controllerFactory.create(clazz));

      Parent root = loader.load();

      if (primaryStage.getScene() == null) {
        primaryStage.setScene(new Scene(root));
      } else {
        primaryStage.getScene().setRoot(root);
      }

      primaryStage.setTitle("Stock Trading Game - " + viewName);
      primaryStage.show();
    } catch (Exception e) {
      throw new RuntimeException("Failed to open view: " + viewName, e);
    }
  }
}