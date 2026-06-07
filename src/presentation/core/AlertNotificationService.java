package presentation.core;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class AlertNotificationService implements NotificationManager
{
  @Override public void show(String message, NotificationType type)
  {
    Alert.AlertType alertType = switch (type)
    {
      case INFO -> Alert.AlertType.INFORMATION;
      case WARNING -> Alert.AlertType.WARNING;
      case ERROR -> Alert.AlertType.ERROR;
    };

    Platform.runLater(() ->{
      Alert alert = new Alert(alertType);
      alert.setTitle("Stock Trading Game");
      alert.setHeaderText(null);
      alert.setContentText(message);
      alert.showAndWait();
    });
  }
}
