package shared.logging;

public class ConsoleLogOutput implements LogOutput
{
  @Override public void log(String level, String message)
  {
    System.out.println("[[" + level + "]] " + message);
  }
}
