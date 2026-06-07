package presentation.core;

import provided.CustomAlertBox;

public class CustomAlertBoxAdapter implements NotificationManager
{
  private final CustomAlertBox adaptee;

  public CustomAlertBoxAdapter(CustomAlertBox adaptee)
  {
    this.adaptee = adaptee;
  }

  @Override public void show(String message, NotificationType type)
  {
    CustomAlertBox.AlertType targetType = switch (type)
    {
      case INFO -> CustomAlertBox.AlertType.INFO;
      case WARNING -> CustomAlertBox.AlertType.WARNING;
      case ERROR -> CustomAlertBox.AlertType.ERROR;
    };

    String defaultTitle = "Stock Market Notification";

    adaptee.showAlert(message, defaultTitle, targetType);
  }
}
