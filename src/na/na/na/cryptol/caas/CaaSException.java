package na.na.na.cryptol.caas;

import java.util.Arrays;
import java.util.ArrayList;

public class CaaSException extends Exception {
  
  public CaaSException() {
    super();
  }
  
  public CaaSException(String message) {
    super(message);
  }
  
  public CaaSException(Throwable cause) {
    super(cause);
  }
  
  public CaaSException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public CaaSException(String message, Throwable[] causes) {
    super(message);
    ArrayList<StackTraceElement> stackTraces = new ArrayList<StackTraceElement>();
    for (Throwable cause: causes) {
      stackTraces.addAll(Arrays.asList(cause.getStackTrace()));
    }
    setStackTrace((StackTraceElement[]) stackTraces.toArray(new StackTraceElement[]{}));
  }
  
}
