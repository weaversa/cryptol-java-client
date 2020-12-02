package na.na.na.cryptol;

public class CryptolValueException extends RuntimeException {
  
  public CryptolValueException() {
    super();
  }
  
  public CryptolValueException(String message) {
    super(message);
  }
  
  public CryptolValueException(Throwable cause) {
    super(cause);
  }
  
  public CryptolValueException(String message, Throwable cause) {
    super(message, cause);
  }
  
}
