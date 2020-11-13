package na.na.na;

import java.math.BigInteger;
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

public class CryptolDatum {
  private JSONObject jsonObject = null;
  private CryptolType type = null;
  private BigInteger word = null;
  private int wordSize = 0; // so we're disallowing [n] where n > Integer.MAX_VALUE == 2^^31 - 1
  private BigInteger modulus = null; // n for Z n or 2^^n for [n] or 0 for Integer or null for other types
  
  public CryptolDatum(JSONObject jsonObject) {
    this.jsonObject = jsonObject;
  }
  
  public int getWordSize() {
    return wordSize;
  }
  
  public BigInteger getModulus() {
    return modulus;
  }
  
  private void checkWordValue() {
    switch (type) {
      case WORD:
        if (word.compareTo(modulus) >= 0) {
          throw new UnsupportedOperationException("Parsed value exceeds maximum for the number bits in the bit sequence.");
        }
        break;
      case RESIDUE:
        if (0 != modulus.signum()) {
          if (word.compareTo(modulus) >= 0) {
            throw new UnsupportedOperationException("Parsed value meets or exceeds modulus.");
          }
        }
        break;
      default:
        break;
    }
  }
  
  public String getHexString() {
    if (null == type) {
      parse();
    }
    switch (type) {
      case WORD:
        if (null == word) {
          throw new UnsupportedOperationException("Could not parse result from Cryptol");
        }
        if (0 != wordSize % 4) {
          throw new UnsupportedOperationException("Word length not a multiple of 4 so hex string conversion failed.");
        }
        int hexChars = wordSize >> 2;
        String hexString = word.toString(16);
        while (hexString.length() < hexChars) {
          hexString = "0" + hexString;
        }
        return hexString;
      case RESIDUE:
        return word.toString(16);
      default:
        throw new UnsupportedOperationException("getHexString only operates on Cryptol types [n], Integer or Z n.");
    }
  }
  
  public String getBinString() {
    if (null == type) {
      parse();
    }
    switch (type) {
      case WORD:
        if (null == word) {
          throw new UnsupportedOperationException("Could not parse result from Cryptol");
        }
        String binString = word.toString(2);
        while (binString.length() < wordSize) {
          binString = "0" + binString;
        }
        return binString;
      case RESIDUE:
        return word.toString(2);
      default:
        throw new UnsupportedOperationException("getBinString only operates on Cryptol types [n], Integer or Z n.");
    }
  }
  
  public String getDecString() {
    if (null == type) {
      parse();
    }
    switch (type) {
      case WORD:
        if (null == word) {
          throw new UnsupportedOperationException("Could not parse result from Cryptol");
        }
        String decString = word.toString(10);
        return decString;
      case RESIDUE:
        return word.toString(10);
      default:
        throw new UnsupportedOperationException("getBinString only operates on Cryptol types [n], Integer or Z n.");
    }
  }
  
  public BigInteger getBigInteger() {
    if (null == type) {
      parse();
    }
    switch (type) {
      case WORD:
      case RESIDUE:
        if (null == word) {
          throw new UnsupportedOperationException("Could not parse result from Cryptol");
        }
        return word;
      default:
        throw new UnsupportedOperationException("getBigInteger only operates on Cryptol types [n], Integer or Z n.");
    }
  }
  
  private void parse() {
    String expression = null;
    try {
      expression = jsonObject.getString("expression");
    } catch (JSONException e) {
      throw new UnsupportedOperationException("Could not access expression in JSON of Cryptol result.\nJSON: " + jsonObject.toString(), e);
    }
    String encoding = null;
    if ("bits".equals(expression)) {
      try {
        encoding = jsonObject.getString("encoding");
      } catch (JSONException e) {
        throw new UnsupportedOperationException("Could not access encoding in JSON of Cryptol result.\nJSON: " + jsonObject.toString(), e);
      }
    } else {
      throw new UnsupportedOperationException("Result from Cryptol not of type [n]\nJSON: " + jsonObject.toString());
    }
    String data = null;
    if ("hex".equals(encoding)) {
      try {
        data = jsonObject.getString("data");
      } catch (JSONException e) {
        throw new UnsupportedOperationException("Could not access data in JSON of Cryptol result.\nJSON: " + jsonObject.toString(), e);
      }
    } else {
      throw new UnsupportedOperationException("Result from Cryptol not in hex encoding.\nJSON: " + jsonObject.toString());
    }
    try {
      wordSize = jsonObject.getInt("width");
    } catch (JSONException e) {
      throw new UnsupportedOperationException("Could not access width in JSON of Cryptol result.\nJSON: " + jsonObject.toString(), e);
    }
    word = new BigInteger(data, 16);
    checkWordValue();
    type = CryptolType.WORD;
  }
  
}
