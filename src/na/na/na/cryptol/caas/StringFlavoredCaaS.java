package na.na.na.cryptol.caas;

import na.na.na.cryptol.CryptolValue;

public class StringFlavoredCaaS {

  private CryptolValueFlavoredCaaS cvfCaaS;
    
  public static StringFlavoredCaaS SUITE_B;
  static {
    try {
      SUITE_B = new StringFlavoredCaaS("localhost", 65521, "SuiteB");
    } catch (Exception e) {
      throw new ExceptionInInitializerError(e);
    }
  }
  
  public StringFlavoredCaaS(String hostOrIP, int port, String module) throws CaaSException {
      cvfCaaS = new CryptolValueFlavoredCaaS(hostOrIP, port, module);
  }
  
  private String callFunction(String f, String[] ins) throws CaaSException {
    CryptolValue[] args = new CryptolValue[ins.length];
    for (int i = 0; i < ins.length; i++) {
      args[i] = new CryptolValue(ins[i].length(), ins[i], 2);
    }
    CryptolValue v = cvfCaaS.callFunction(f, args);
    return v.getBinString();
  }
  
  public String callFunction(String f) throws CaaSException {
    return callFunction(f, new String[]{});
  }
  
  public String callFunction(String f, String in0) throws CaaSException {
    return callFunction(f, new String[]{in0});
  }
  
  public String callFunction(String f, String in0, String in1) throws CaaSException {
    return callFunction(f, new String[]{in0, in1});
  }
  
  public String callFunction(String f, String in0, String in1, String in2) throws CaaSException {
    return callFunction(f, new String[]{in0, in1, in2});
  }
  
  public String callFunction(String f, String in0, String in1, String in2, String in3) throws CaaSException {
    return callFunction(f, new String[]{in0, in1, in2, in3});
  }
  
  public String callFunction(String f, String in0, String in1, String in2, String in3, String in4) throws CaaSException {
    return callFunction(f, new String[]{in0, in1, in2, in3, in4});
  }
  
}
