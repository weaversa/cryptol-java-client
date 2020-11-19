package na.na.na;

import na.na.na.CaaS;

public class BasicCaaS {
  private String hostOrIP;
  private int port;
  private String module;
  private CaaS caas;
  
  public static BasicCaaS SUITE_B;
  
  static {
    try {
      SUITE_B = new BasicCaaS("localhost", 65521, "SuiteB");
    } catch (Exception e) {
      throw new ExceptionInInitializerError(e);
    }
  }
  
  public BasicCaaS(String hostOrIP, int port, String module) throws CaaSException {
    caas = new CaaS(hostOrIP, port);
    caas.loadModule(module);
  }
  
  public CryptolValue callFunction(String f) throws CaaSException {
    return caas.callFunction(f);
  }
  
  public CryptolValue callFunction(String f, CryptolValue in0) throws CaaSException {
    return caas.callFunction(f, in0);
  }
  
  public CryptolValue callFunction(String f, CryptolValue in0, CryptolValue in1) throws CaaSException {
    return caas.callFunction(f, in0, in1);
  }
  
  public CryptolValue callFunction(String f, CryptolValue in0, CryptolValue in1, CryptolValue in2) throws CaaSException {
    return caas.callFunction(f, in0, in1, in2);
  }
  
  public CryptolValue callFunction(String f, CryptolValue in0, CryptolValue in1, CryptolValue in2, CryptolValue in3) throws CaaSException {
    return caas.callFunction(f, in0, in1, in2, in3);
  }
  
  public CryptolValue callFunction(String f, CryptolValue in0, CryptolValue in1, CryptolValue in2, CryptolValue in3, CryptolValue in4) throws CaaSException {
    return caas.callFunction(f, in0, in1, in2, in3, in4);
  }

}
