import na.na.na.cryptol.CryptolValue;
import na.na.na.cryptol.caas.CaaSException;
import static na.na.na.cryptol.caas.ModuleSpecificCaaS.*;

public class sha224 {
  
  public static void main(String[] args) throws CaaSException {
    sha224 foo = new sha224();
  }
  
  private String functionName() {
    return this.getClass().getSimpleName();
  }
  
  public sha224() throws CaaSException {
    
    //    ModuleSpecificCaaS suiteB = new ModuleSpecificCaaS("SuiteB");
    System.out.println("Evaluating `sha224' ...");
    CryptolValue in = new CryptolValue(352, "54686520717569636b2062726f776e20666f78206a756d7073206f76657220746865206c617a7920646f672e", 16);
    
    {
      CryptolValue out = SUITE_B.call("sha224", in);
      System.out.println(out + "\n");
    }
    {
      CryptolValue out = SUITE_B.call(this.getClass().getSimpleName(), in);
      System.out.println(out + "\n");
    }
    {
      CryptolValue out = SUITE_B.call(functionName(), in);
      System.out.println(out + "\n");
    }
    /* could maybe make the following work, so that an unspecified function name is taken to be the class name
     {
     CryptolValue out = SUITE_B.call(in);
     System.out.println(out + "\n");
     System.out.println(out.toHexString() + "\n");
     }
     
     */
    
    System.out.println("0x619cba8e8e05826e9b8c519c0a5c68f4fb653e8a3d8aa04bb2c8cd4c" + " <~~ expected value\n");
    
    in = new CryptolValue(0, "", 16);
    CryptolValue out = SUITE_B.call("sha224", in);
    System.out.println(out);
    System.out.println("0xd14a028c2a3a2bc9476102bb288234c415a2b01f828ea62ac5b3e42f" + " <~~ expected value");
    
    System.out.println();
    
  }
  
}

