package shared.logging;

public class Logger
{
  private static Logger instance;
  private LogOutput output;

  private Logger()
  {
  }

  public static synchronized Logger getInstance()
  {
    if (instance == null)
    {
      instance = new Logger();
    }
    return instance;
  }

  public void setOutput(LogOutput output)
  {
    this.output = output;
  }

  public void log(String level, String message)
  {
    if (output != null)
    {
      synchronized (output)
      {
        output.log(level, message);
      }
    }
  }
}
