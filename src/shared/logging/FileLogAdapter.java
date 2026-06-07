package shared.logging;

import provided.FileLogOutputter;

public class FileLogAdapter implements LogOutput
{
  private final FileLogOutputter adaptee;

  public FileLogAdapter(FileLogOutputter adaptee)
  {
    this.adaptee = adaptee;
  }

  @Override public void log(String level, String message)
  {
    if (level == null)
    {
      adaptee.logInfo(message);
      return;
    }

    switch (level.toUpperCase())
    {
      case "ERROR" -> adaptee.logError(message);
      case "WARNING" -> adaptee.logWarning(message);
      case "INFO" -> adaptee.logInfo(message);
      default -> adaptee.logInfo("[" + level + "]" + message);
    }
  }
}
