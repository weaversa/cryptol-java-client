import na.na.na.BinaryString;
import na.na.na.cryptol.CryptolValue;
import na.na.na.cryptol.caas.CaaSException;
import na.na.na.cryptol.caas.ModuleSpecificCaaS;

public class CryptolSequenceTest {
  
  public static void main(String[] args) throws CaaSException {
    BinaryString a0 = BinaryString.valueOf("10010");
    BinaryString a1 = BinaryString.valueOf("01011");
        
    CryptolValue c = new CryptolValue(new BinaryString[] {a0, a1});
    
  }
  
}
