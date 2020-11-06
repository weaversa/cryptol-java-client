package na.na.na;

import java.lang.Exception;

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
  
}
