package na.na.na.cryptol.caas;

import na.na.na.BinaryString;
import na.na.na.cryptol.*;
import org.json.JSONArray;

public class ModuleSpecificCaaS {
  private String hostOrIP;
  private int port;
  private String module;
  private PrimitiveCaaS caas;
  private int calls = 0;
  
  private static int CALL_ATTEMPTS = 100;
  private static long ATTEMPT_DELAY_ms = 3000;
  
  public static ModuleSpecificCaaS CHEESE = new ModuleSpecificCaaS("Gouda");
  
  public static ModuleSpecificCaaS SUITE_B = new ModuleSpecificCaaS("SuiteB");
  
  public ModuleSpecificCaaS(String hostOrIP, int port, String module) {
    this.hostOrIP = hostOrIP;
    this.port = port;
    this.module = module;
  }
  
  public ModuleSpecificCaaS(String module) {
    this(null, 0, module);
  }
  
  private void restartCaaS() throws CaaSException {
    System.err.print("***** Connecting to CaaS...");
    try {
      if (null == hostOrIP) {
        caas = new PrimitiveCaaS();
      } else {
        caas = new PrimitiveCaaS(hostOrIP, port);
      }
    } catch (CaaSException e) {
      System.err.println(" FAILED!");
      throw e;
    } catch (Throwable t) {
      System.err.println(" FAILED!");
      throw new CaaSException("Trouble connecting to CaaS.", t);
    }
    System.err.println("\r***** Connected to CaaS at " + caas.getHostOrIP() + ":" + caas.getPort());
    System.err.print("***** Loading module `" + module + "'...");
    try {
      caas.loadModule(module);
    } catch (Throwable t) {
      System.err.println(" FAILED!");
      throw t;
    }
    System.err.println("\r***** Loaded module `" + module + "'.   ");
  }
  
  private CryptolValue callFunction(String f, JSONArray args) throws CaaSException {
    calls++;
    if (0 == (calls & (calls - 1))) {
      System.err.println("***** Call 2^^" + (31 - Integer.numberOfLeadingZeros(calls)) + " to `" + module + "' via CaaS.");
    }
    Exception[] es = new Exception[CALL_ATTEMPTS];
    for (int i = 0; i < CALL_ATTEMPTS; i++) {
      try {
        if ((0 < i) && (0 == (i & (i - 1)))) { // power of 2
          System.err.println("***** Retry 2^^" + (31 - Integer.numberOfLeadingZeros(i)) + " of call " + calls + " to `" + module + "' via CaaS.");
        }
        if (1 == calls) {
          restartCaaS();
        }
        return new CryptolValue(caas.call(f, args));
      } catch (CaaSException e) {
        try {
          Thread.sleep(ATTEMPT_DELAY_ms);
        } catch (InterruptedException ie) {
          throw new CaaSException(ie);
        }
        es[i] = e;
        try {
          restartCaaS();
        } catch (Throwable t) {
        }
      }
    }
    throw new CaaSException("callFunction `" + f + "' failed after " + CALL_ATTEMPTS + " attempt(s). Stack traces for all attempts displayed as one. Read carefully!", es);
  }
  
  public CryptolValue call(String f, Object[] ins) throws CaaSException {
    JSONArray args = new JSONArray();
    for (Object in: ins) {
      if (in instanceof CryptolValue) {
        args.put(((CryptolValue) in).getJSONForArgument());
      } else if (in instanceof BinaryString) {
        CryptolValue cv = new CryptolValue(((BinaryString) in).toString(), 2);
        args.put(cv.getJSONForArgument());
      } else {
        throw new CaaSException("Invalid argument class in function call: " + ins.getClass().getName());
      }
    }
    return callFunction(f, args);
  }
  
  public CryptolValue call(String f) throws CaaSException {
    return call(f, new Object[]{});
  }
  
  public CryptolValue call(String f, Object in0) throws CaaSException {
    return call(f, new Object[]{in0});
  }
  
  public CryptolValue call(String f, Object in0, Object in1) throws CaaSException {
    return call(f, new Object[]{in0, in1});
  }
  
  public CryptolValue call(String f, Object in0, Object in1, Object in2) throws CaaSException {
    return call(f, new Object[]{in0, in1, in2});
  }
  
  public CryptolValue call(String f, Object in0, Object in1, Object in2, Object in3) throws CaaSException {
    return call(f, new Object[]{in0, in1, in2, in3});
  }
  
  public CryptolValue call(String f, Object in0, Object in1, Object in2, Object in3, Object in4) throws CaaSException {
    return call(f, new Object[]{in0, in1, in2, in3, in4});
  }
  
  public CryptolValue call(String f, Object in0, Object in1, Object in2, Object in3, Object in4, Object in5) throws CaaSException {
    return call(f, new Object[]{in0, in1, in2, in3, in4, in5});
  }
  
  public CryptolValue call(String f, Object in0, Object in1, Object in2, Object in3, Object in4, Object in5, Object in6) throws CaaSException {
    return call(f, new Object[]{in0, in1, in2, in3, in4, in5, in6});
  }
  
  public CryptolValue call(String f, Object in0, Object in1, Object in2, Object in3, Object in4, Object in5, Object in6, Object in7) throws CaaSException {
    return call(f, new Object[]{in0, in1, in2, in3, in4, in5, in6, in7});
  }
  
  public CryptolValue call(String f, Object in0, Object in1, Object in2, Object in3, Object in4, Object in5, Object in6, Object in7, Object in8) throws CaaSException {
    return call(f, new Object[]{in0, in1, in2, in3, in4, in5, in6, in7, in8});
  }
  
  public CryptolValue call(String f, Object in0, Object in1, Object in2, Object in3, Object in4, Object in5, Object in6, Object in7, Object in8, Object in9) throws CaaSException {
    return call(f, new Object[]{in0, in1, in2, in3, in4, in5, in6, in7, in8, in9});
  }
  
  public BinaryString invoke(String f, Object[] ins) throws CaaSException {
    return BinaryString.valueOf(call(f, ins).toBinString());
  }
  
  public BinaryString invoke(String f) throws CaaSException {
    return invoke(f, new Object[]{});
  }
  
  public BinaryString invoke(String f, Object in0) throws CaaSException {
    return invoke(f, new Object[]{in0});
  }
  
  public BinaryString invoke(String f, Object in0, Object in1) throws CaaSException {
    return invoke(f, new Object[]{in0, in1});
  }
  
  public BinaryString invoke(String f, Object in0, Object in1, Object in2) throws CaaSException {
    return invoke(f, new Object[]{in0, in1, in2});
  }
  
  public BinaryString invoke(String f, Object in0, Object in1, Object in2, Object in3) throws CaaSException {
    return invoke(f, new Object[]{in0, in1, in2, in3});
  }
  
  public BinaryString invoke(String f, Object in0, Object in1, Object in2, Object in3, Object in4) throws CaaSException {
    return invoke(f, new Object[]{in0, in1, in2, in3, in4});
  }
  
  public BinaryString invoke(String f, Object in0, Object in1, Object in2, Object in3, Object in4, Object in5) throws CaaSException {
    return invoke(f, new Object[]{in0, in1, in2, in3, in4, in5});
  }
  
  public BinaryString invoke(String f, Object in0, Object in1, Object in2, Object in3, Object in4, Object in5, Object in6) throws CaaSException {
    return invoke(f, new Object[]{in0, in1, in2, in3, in4, in5, in6});
  }
  
  public BinaryString invoke(String f, Object in0, Object in1, Object in2, Object in3, Object in4, Object in5, Object in6, Object in7) throws CaaSException {
    return invoke(f, new Object[]{in0, in1, in2, in3, in4, in5, in6, in7});
  }
  
  public BinaryString invoke(String f, Object in0, Object in1, Object in2, Object in3, Object in4, Object in5, Object in6, Object in7, Object in8) throws CaaSException {
    return invoke(f, new Object[]{in0, in1, in2, in3, in4, in5, in6, in7, in8});
  }
  
  public BinaryString invoke(String f, Object in0, Object in1, Object in2, Object in3, Object in4, Object in5, Object in6, Object in7, Object in8, Object in9) throws CaaSException {
    return invoke(f, new Object[]{in0, in1, in2, in3, in4, in5, in6, in7, in8, in9});
  }
  
}
