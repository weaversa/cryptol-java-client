package na.na.na;

import java.math.BigInteger;
import org.json.*;

/*
 Desired invariants:
 
 convert from JSON when needed
 
 convert to JSON at construction
 
 for WORD:
   size >= 0
   modulus == 2^size
   modulus > 0 // implied from previous
   0 <= bitseq < modulus
 
 for RESIDUE:
   modulus >= 0  // we are modeling Z_n where n is the modulus; Z_0 is Z
   if modulus > 0, 0 <= bitseq < modulus
 
 */

/*
 simple types
 
 Bit ~~> boolean
 [n] ~~> BigInteger + bitlength of n
 Z n ~~> BigInteger + modulus of n
 Integer ~~> BigInteger + modulus of n
 
 complex types
 
 [n_0]a ~~> Vector<CryptolValue> of length n_0
 (t_0, t_1, ... t_k) ~~> SortedMap<Integer><CryptolValue> from int 0..k to t_0, t_1, ... t_k respectively
 {l_0 = t_0, l_1 = t_1, ... l_k = t_k} ~~> Map<String><CryptolValue>
 
 */

public class CryptolValue {
  private boolean bit;
  private JSONObject json;
  private BigInteger modulus; // n for Z n or 2^n for [n] or 0 for Integer or null for other types
  private int size = -1; // so we're disallowing [n] where n > Integer.MAX_VALUE == 2^^31 - 1
  private CryptolType type;
  private BigInteger bitseq;
  
  protected CryptolValue(JSONObject jsonObject) {
    json = jsonObject;
  }

  public CryptolValue(int bits, String digits, int radix) {
    size = bits;
    calculateModulus();
    type = CryptolType.WORD;
    bitseq = new BigInteger(digits, radix);
    if (size < 0) {
      throw new IllegalArgumentException();
    }
    if (0 > bitseq.signum()) {
      throw new IllegalArgumentException();
    }
    if (bitseq.compareTo(modulus) >= 0) {
      throw new IllegalArgumentException();
    }
    toJSON();
  }
  
  public CryptolValue(String digits, int radix) {
    new CryptolValue(computeSize(digits, radix), digits, radix);
  }
  
  public CryptolValue(BigInteger big) {
    modulus = BigInteger.ZERO;
    size = -1;
    type = CryptolType.RESIDUE;
    bitseq = big;
    toJSON();
  }
  
  public CryptolValue(boolean bool) {
    bit = bool;
    type = CryptolType.BIT;
    toJSON();
  }
    
  protected JSONObject getJSON() {
    return json;
  }
  
  private static int computeSize(String digits, int radix) {
    switch (radix) {
      case 2:
        return digits.length();
      case 16:
        return 4 * digits.length();
      case 8:
        return 3 * digits.length();
      default:
        throw new IllegalArgumentException();
    }
  }
  
  public int getSize() {
    return size;
  }
  
  private void calculateModulus() {
    modulus = BigInteger.ONE.shiftLeft(size);
  }
  
  public BigInteger getModulus() {
    return new BigInteger(modulus.toString());
  }
  
  private void checkValue() {
    switch (type) {
      case RESIDUE:
      case WORD:
        if (0 > modulus.signum()) {
          throw new UnsupportedOperationException("Negative modulus");
        }
        if (bitseq.compareTo(modulus) >= 0) {
          throw new UnsupportedOperationException("Value exceeds maximum for size or modulus.");
        }
        if (modulus.signum() > 0) {
          if (bitseq.signum() < 0) {
            throw new UnsupportedOperationException("Negative bitseq.");
          }
        }
        break;
      default:
        break;
    }
  }
  
  public String getHexString() {
    if (null == type) {
      fromJSON();
    }
    switch (type) {
      case WORD:
        if (null == bitseq) {
          throw new UnsupportedOperationException("Could not parse result from Cryptol");
        }
        if (0 != size % 4) {
          throw new UnsupportedOperationException("Word length not a multiple of 4 so hex string conversion failed.");
        }
        int hexChars = size >> 2;
        String hexString = bitseq.toString(16);
        while (hexString.length() < hexChars) {
          hexString = "0" + hexString;
        }
        return hexString;
      case RESIDUE:
        return bitseq.toString(16);
      default:
        throw new UnsupportedOperationException("getHexString only operates on Cryptol types [n], Integer or Z n.");
    }
  }
  
  public String getBinString() {
    if (null == type) {
      fromJSON();
    }
    switch (type) {
      case WORD:
        if (null == bitseq) {
          throw new UnsupportedOperationException("Could not parse result from Cryptol");
        }
        String binString = bitseq.toString(2);
        while (binString.length() < size) {
          binString = "0" + binString;
        }
        return binString;
      case RESIDUE:
        return bitseq.toString(2);
      default:
        throw new UnsupportedOperationException("getBinString only operates on Cryptol types [n], Integer or Z n.");
    }
  }
  
  public String getDecString() {
    if (null == type) {
      fromJSON();
    }
    switch (type) {
      case WORD:
        if (null == bitseq) {
          throw new UnsupportedOperationException("Could not parse result from Cryptol");
        }
        String decString = bitseq.toString(10);
        return decString;
      case RESIDUE:
        return bitseq.toString(10);
      default:
        throw new UnsupportedOperationException("getBinString only operates on Cryptol types [n], Integer or Z n.");
    }
  }
  
  public BigInteger getBigInteger() {
    if (null == type) {
      fromJSON();
    }
    switch (type) {
      case WORD:
      case RESIDUE:
        if (null == bitseq) {
          throw new UnsupportedOperationException("Could not parse result from Cryptol");
        }
        return bitseq;
      default:
        throw new UnsupportedOperationException("getBigInteger only operates on Cryptol types [n], Integer or Z n.");
    }
  }
  
  private void fromJSON() {
    String expression = null;
    try {
      expression = json.getString("expression");
    } catch (JSONException e) {
      throw new UnsupportedOperationException("Could not access expression in JSON of Cryptol result.\nJSON: " + json.toString(), e);
    }
    String encoding = null;
    if ("bits".equals(expression)) {
      try {
        encoding = json.getString("encoding");
      } catch (JSONException e) {
        throw new UnsupportedOperationException("Could not access encoding in JSON of Cryptol result.\nJSON: " + json.toString(), e);
      }
    } else {
      throw new UnsupportedOperationException("Result from Cryptol not of type [n]\nJSON: " + json.toString());
    }
    String data = null;
    if ("hex".equals(encoding)) {
      try {
        data = json.getString("data");
      } catch (JSONException e) {
        throw new UnsupportedOperationException("Could not access data in JSON of Cryptol result.\nJSON: " + json.toString(), e);
      }
    } else {
      throw new UnsupportedOperationException("Result from Cryptol not in hex encoding.\nJSON: " + json.toString());
    }
    try {
      size = json.getInt("width");
    } catch (JSONException e) {
      throw new UnsupportedOperationException("Could not access width in JSON of Cryptol result.\nJSON: " + json.toString(), e);
    }
    bitseq = new BigInteger(data, 16);
    checkValue();
    type = CryptolType.WORD;
  }
  
  public boolean getBit() {
    if (CryptolType.BIT == type) {
      return bit;
    } else {
      throw new UnsupportedOperationException("Requested bit from a nonbit bitseq");
    }
  }
  
  private void toJSON() {
    // TODO
  }
                                              
}
