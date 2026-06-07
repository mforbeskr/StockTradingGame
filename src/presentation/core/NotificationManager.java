package presentation.core;

public interface NotificationManager
{
  void show(String message, NotificationType type);

  enum NotificationType
  {
    INFO, WARNING, ERROR,
  }
}
