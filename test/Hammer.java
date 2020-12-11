import java.util.Random;
import na.na.na.cryptol.CryptolValue;
import na.na.na.cryptol.caas.CaaSException;
//import na.na.na.cryptol.caas.ModuleSpecificCaaS;
import static na.na.na.cryptol.caas.ModuleSpecificCaaS.*;

public class Hammer {
  
  public static void main(String[] args) throws CaaSException {
    
    Random random = new Random();
    
    {
      CryptolValue x = new CryptolValue(0, random);
      CryptolValue y = SUITE_B.call("aes255EncryptSchedule", x);
    }
    
    for (int i = 0; i < Integer.MAX_VALUE; i++) {
      System.out.print(i + "...\r");
      CryptolValue k = new CryptolValue(256, random);
      CryptolValue es = SUITE_B.call("aes256EncryptSchedule", k);
      CryptolValue pt = new CryptolValue(128, random);
      CryptolValue ct = SUITE_B.call("aesEncryptBlock", es, pt);
      CryptolValue ds = SUITE_B.call("aes256DecryptSchedule", k);
      CryptolValue pt_ = SUITE_B.call("aesDecryptBlock", ds, ct);
      if (!pt.toString().equals(pt_.toString())) {
        System.err.println();
        System.err.println("Discrepancy in AES256 en/decrypt.");
        System.err.println("    k: " + k.toString());
        System.err.println("   pt: " + pt.toString());
        System.err.println("   ct: " + ct.toString());
        System.err.println("  pt_: " + pt_.toString());
        System.err.println();
      }
    }
    
    
    CryptolValue k = new CryptolValue(128, "000102030405060708090a0b0c0d0e0f", 16);
    CryptolValue ks = SUITE_B.call("aes128EncryptSchedule", k);
    System.out.println("\nks:\n" + ks + "\n");
    CryptolValue pt = new CryptolValue(128, "00112233445566778899aabbccddeeff", 16);
    CryptolValue ct = SUITE_B.call("aesEncryptBlock", ks, pt);
    System.out.println("");
    System.out.println("ct: " + ct);
    System.out.println("    " + "0x69c4e0d86a7b0430d8cdb78070b4c55a");
    System.out.println("");
    
    /*
    CryptolValue foo = suiteB.call(ModuleSpecificCaaSTest.class.getSimpleName());
     */
  }
}
    /*
     According to Wikipedia:
SHA224("The quick brown fox jumps over the lazy dog.") ~~>

0x 619cba8e8e05826e9b8c519c0a5c68f4fb653e8a3d8aa04bb2c8cd4c

     From Cryptol:
     
     join("The quick brown fox jumps over the lazy dog.") ~~>
     
     0x54686520717569636b2062726f776e20666f78206a756d7073206f76657220746865206c617a7920646f672e
     
     */




// aesEncryptBlock   (aesExpandEncryptSchedule 0x000102030405060708090a0b0c0d0e0f) 0x00112233445566778899aabbccddeeff == 0x69c4e0d86a7b0430d8cdb78070b4c55a
