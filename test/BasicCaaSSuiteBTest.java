import org.json.*;
import na.na.na.*;

public class BasicCaaSSuiteBTest {
  
  public static void main(String[] args) throws CaaSException {
    System.out.println("Evaluating `sha224 '...");
    CryptolValue in0 = new CryptolValue(352, "54686520717569636b2062726f776e20666f78206a756d7073206f76657220746865206c617a7920646f672e", 16);
    CryptolValue out = BasicCaaS.SUITE_B.callFunction("sha224", in0);
    System.out.println(out);
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
