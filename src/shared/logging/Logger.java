package shared.logging;

public class Logger
{
  private static final Logger instance = new Logger();
  private LogOutput output;

  private Logger()
  {
  }

  public static Logger getInstance()
  {
    return instance;
  }

  public void setOutput(LogOutput output)
  {
    this.output = output;
  }

  public synchronized void log(String level, String message)
  {
    output.log(level, message);
  }
}
