package com.pobox.djb;

public class NetstringException extends RuntimeException {

  public NetstringException() {
    super();
  }
  
  public NetstringException(String message) {
    super(message);
  }
  
  public NetstringException(Throwable cause) {
    super(cause);
  }
  
  public NetstringException(String message, Throwable cause) {
    super(message, cause);
  }
  
}
