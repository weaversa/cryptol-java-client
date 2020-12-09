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
    
    System.out.println();
    System.out.println("Testing sha224 ...");
    System.out.println();

    {
      CryptolValue pangram = new CryptolValue(352, "54686520717569636b2062726f776e20666f78206a756d7073206f76657220746865206c617a7920646f672e", 16);
      {
        CryptolValue hash = SUITE_B.call("sha224", pangram);
        System.out.println(hash);
      }
      {
        CryptolValue hash = SUITE_B.call(this.getClass().getSimpleName(), pangram);
        System.out.println(hash);
      }
      {
        CryptolValue hash = SUITE_B.call(functionName(), pangram);
        System.out.println(hash);
      }
      /* could maybe make the following work, so that an unspecified function name is taken to be the class name
       {
       CryptolValue hash = SUITE_B.call(in);
       System.out.println(hash + "\n");
       System.out.println(hash.toHexString() + "\n");
       }
       
       */
      
      System.out.println("0x619cba8e8e05826e9b8c519c0a5c68f4fb653e8a3d8aa04bb2c8cd4c" + " <~~ expected value\n");
    }
    
    System.out.println();
    
    {
      CryptolValue pangram_ = new CryptolValue(344, "54686520717569636b2062726f776e20666f78206a756d7073206f76657220746865206c617a7920646f67", 16);
      CryptolValue hash = SUITE_B.call("sha224", pangram_);
      System.out.println(hash);
      System.out.println("0x730e109bd7a8a32b1cb9d9a09aa2325d2430587ddbc0c38bad911525" + " <~~ expected value");
    }
    
    System.out.println();
    
    {
      CryptolValue empty = new CryptolValue(0, "", 16);
      CryptolValue hash = SUITE_B.call("sha224", empty);
      System.out.println(hash);
      System.out.println("0xd14a028c2a3a2bc9476102bb288234c415a2b01f828ea62ac5b3e42f" + " <~~ expected value");
    }
    
    System.out.println();
    
  }
  
}

