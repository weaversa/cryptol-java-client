package na.na.na.cryptol.caas;

import na.na.na.cryptol.CryptolType;
import na.na.na.cryptol.CryptolValue;
import org.json.JSONArray;

public class CryptolValueFlavoredCaaS {
  private String hostOrIP;
  private int port;
  private String module;
  private JSONFlavoredCaaS jfCaaS;
  
  private static int CALL_ATTEMPTS = 3;
  private static long ATTEMPT_DELAY = 2000;
  
  public static CryptolValueFlavoredCaaS SUITE_B;
  static {
    try {
      SUITE_B = new CryptolValueFlavoredCaaS("localhost", 65521, "SuiteB");
    } catch (Exception e) {
      throw new ExceptionInInitializerError(e);
    }
  }
  
  public CryptolValueFlavoredCaaS(String hostOrIP, int port, String module) throws CaaSException {
    this.hostOrIP = hostOrIP;
    this.port = port;
    this.module = module;
    jfCaaS = new JSONFlavoredCaaS(hostOrIP, port);
    jfCaaS.loadModule(module);
  }
  
  public CryptolValue callFunction(String f, CryptolValue[] ins) throws CaaSException {
    JSONArray args = new JSONArray();
    for (CryptolValue in: ins) {
      args.put(in.getJSONForArgument());
    }
    Exception[] es = new Exception[CALL_ATTEMPTS];
    for (int i = 0; i < CALL_ATTEMPTS; i++) {
      try {
        return new CryptolValue(jfCaaS.call(f, args));
      } catch (CaaSException e) {
        es[i] = e;
        jfCaaS = new JSONFlavoredCaaS(hostOrIP, port);
        jfCaaS.loadModule(module);
        try {
          Thread.sleep(ATTEMPT_DELAY);
        } catch (InterruptedException ie) {
          throw new CaaSException(ie);
        }
      }
    }
    throw new CaaSException("callFunction failed after " + CALL_ATTEMPTS + "attempt(s). Stack traces for all attempts displayed as one. Read carefully!", es);
  }
  
  public CryptolValue callFunction(String f) throws CaaSException {
    return callFunction(f, new CryptolValue[]{});
  }
  
  public CryptolValue callFunction(String f, CryptolValue in0) throws CaaSException {
    return callFunction(f, new CryptolValue[]{in0});
  }
  
  public CryptolValue callFunction(String f, CryptolValue in0, CryptolValue in1) throws CaaSException {
    return callFunction(f, new CryptolValue[]{in0, in1});
  }
  
  public CryptolValue callFunction(String f, CryptolValue in0, CryptolValue in1, CryptolValue in2) throws CaaSException {
    return callFunction(f, new CryptolValue[]{in0, in1, in2});
  }
  
  public CryptolValue callFunction(String f, CryptolValue in0, CryptolValue in1, CryptolValue in2, CryptolValue in3) throws CaaSException {
    return callFunction(f, new CryptolValue[]{in0, in1, in2, in3});
  }
  
  public CryptolValue callFunction(String f, CryptolValue in0, CryptolValue in1, CryptolValue in2, CryptolValue in3, CryptolValue in4) throws CaaSException {
    return callFunction(f, new CryptolValue[]{in0, in1, in2, in3, in4});
  }
  
}
