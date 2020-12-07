package na.na.na.cryptol.caas;

import na.na.na.cryptol.*;
import org.json.JSONArray;

public class ModuleSpecificCaaS {
  private String hostOrIP;
  private int port;
  private String module;
  private PrimitiveCaaS caas;
  
  private static int CALL_ATTEMPTS = 100;
  private static long ATTEMPT_DELAY_ms = 3000;
  
  public static ModuleSpecificCaaS SUITE_B;
  static {
    try {
      SUITE_B = new ModuleSpecificCaaS("SuiteB");
    } catch (Exception e) {
      throw new ExceptionInInitializerError(e);
    }
  }
  
  public ModuleSpecificCaaS(String hostOrIP, int port, String module) throws CaaSException {
    caas = new PrimitiveCaaS(hostOrIP, port);
    caas.loadModule(module);
    this.hostOrIP = hostOrIP;
    this.port = port;
    this.module = module;
  }
  
  public ModuleSpecificCaaS(String module) throws CaaSException {
    caas = new PrimitiveCaaS();
    caas.loadModule(module);
    this.module = module;
  }
  
  private CryptolValue callFunction(String f, CryptolValue[] ins) throws CaaSException {
    JSONArray args = new JSONArray();
    for (CryptolValue in: ins) {
      args.put(in.getJSONForArgument());
    }
    Exception[] es = new Exception[CALL_ATTEMPTS];
    for (int i = 0; i < CALL_ATTEMPTS; i++) {
      try {
        return new CryptolValue(caas.call(f, args));
      } catch (CaaSException e) {
        try {
          Thread.sleep(ATTEMPT_DELAY_ms);
        } catch (InterruptedException ie) {
          throw new CaaSException(ie);
        }
        es[i] = e;
        if (null == hostOrIP) {
          caas = new PrimitiveCaaS();
        } else {
          caas = new PrimitiveCaaS(hostOrIP, port);
        }
        caas.loadModule(module);
      }
    }
    throw new CaaSException("callFunction `" + f + "' failed after " + CALL_ATTEMPTS + "attempt(s). Stack traces for all attempts displayed as one. Read carefully!", es);
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
    return new CryptolValue(caas.call(f, args));
  }
  
  public BinaryString invoke(String f, Object[] ins) throws CaaSException {
    return BinaryString.valueOf(call(f, ins).toBinString());
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
