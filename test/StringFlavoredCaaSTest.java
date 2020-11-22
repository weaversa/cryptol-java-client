import na.na.na.cryptol.caas.CaaSException;
import static na.na.na.cryptol.caas.StringFlavoredCaaS.SUITE_B;

public class StringFlavoredCaaSTest {
  
  private static final String IN = "0101010001101000011001010010000001110001011101010110100101100011011010110010000001100010011100100110111101110111011011100010000001100110011011110111100000100000011010100111010101101101011100000111001100100000011011110111011001100101011100100010000001110100011010000110010100100000011011000110000101111010011110010010000001100100011011110110011100101110";
  
  private static final String OUT = "01100001100111001011101010001110100011100000010110000010011011101001101110001100010100011001110000001010010111000110100011110100111110110110010100111110100010100011110110001010101000000100101110110010110010001100110101001100";
  
  public static void main(String[] args) throws CaaSException {
    
    System.out.println("Evaluating `sha224 '...");
    String s = SUITE_B.callFunction("sha224", IN);
    System.out.println("\n" + OUT.equals(s) + "\n");
    
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
