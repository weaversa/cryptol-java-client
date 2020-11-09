package na.na.na;

import java.util.BigInteger;
import org.json.*;

/*
 simple types
 
 Bit ~~> boolean
 [n] ~~> BigInteger + bitlength of n
 Z n ~~> BigInteger + modulus of n
 Integer ~~> BigInteger + modulus of n
 
 complex types
 
 [n_0]a ~~> Vector<CryptolDatum> of length n_0
 (t_0, t_1, ... t_k) ~~> SortedMap<Integer><CryptolDatum> from int 0..k to t_0, t_1, ... t_k respectively
 {l_0 = t_0, l_1 = t_1, ... l_k = t_k} ~~> Map<String>
 
 */

public class CryptolResult {
  private JSONObject result = null;
  private CryptolType type = null;
  private BigInteger word = null;
  private int wordLength = 0;
  private int modulus = 0;
  
  public int getWordLength() {
    return wordLength;
  }
  
  public int getModulus() {
    return modulus;
  }
  
  public String getHexString() {
    switch (type) {
      case WORD:
        if null == word {
          parse();
        }
        int hexChars = (wordLength + 3) >> 2;
        String hexString = word.toString(16);
        if (hexString.length() > hexChars) {
          throw new UnsupportedOperationException("Word has too many bits.")
        }
        while (hexString.length() < hexChars) {
          hexString = "0" + hexString;
        }
        return hexString;
        break;
      case RESIDUE:
        if null == word {
          parse();
        }
        return word.toString(16);
      default:
        throw new UnsupportedOperationException("getHexString only operates on Cryptol types [n], Integer or Z n.");
        
    }
  }
  
  public BigInteger getBigInteger() {
    
  }
  
}
