package na.na.na.cryptol;

import java.math.BigInteger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 Intended invariants:
 
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
 [n] ~~> BigInteger + size of n
 Z n ~~> BigInteger + modulus of n + size of -1
 Integer ~~> BigInteger + modulus of 0 + size of -1
 
 complex types
 
 [n_0]a ~~> Vector<CryptolValue> of length n_0
 (t_0, t_1, ... t_k) ~~> SortedMap<Integer><CryptolValue> from int 0..k to t_0, t_1, ... t_k respectively
 {l_0 = t_0, l_1 = t_1, ... l_k = t_k} ~~> Map<String><CryptolValue>
 
 */

public class CryptolValue {
  
  private JSONObject json;
  private CryptolType type;
  private boolean bit;
  private BigInteger modulus; // n for Z n or 2^n for [n] or 0 for Integer or null for other types
  private int size; // n for [n] so we're disallowing [n] where n > Integer.MAX_VALUE == 2^^31 - 1
  private BigInteger bitseq;
  
  public CryptolValue(JSONObject jsonObject) {
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
  
  public JSONObject getJSON() {
    return json;
  }
  
  public JSONObject getJSONForArgument() {
    try {
      return json.getJSONObject("result").getJSONObject("answer").getJSONObject("value");
    } catch (JSONException e) {
      return json;
    }
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
        throw new IllegalArgumentException("Radix must be 2, 8 or 16, rather than " + radix + '.');
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
  
  public String toHexString() {
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
        throw new UnsupportedOperationException("toHexString only operates on Cryptol types [n], Integer or Z n.");
    }
  }
  
  public String toBinString() {
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
        throw new UnsupportedOperationException("toBinString only operates on Cryptol types [n], Integer or Z n.");
    }
  }
  
  public String toDecString() {
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
        throw new UnsupportedOperationException("toDecString only operates on Cryptol types [n], Integer or Z n.");
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
    JSONObject jsonObject = getJSONForArgument();
    String expression = null;
    String encoding = null;
    String data = null;
    try {
      expression = jsonObject.getString("expression");
      encoding = jsonObject.getString("encoding");
      data = jsonObject.getString("data");
      size = jsonObject.getInt("width");
      calculateModulus();
    } catch (JSONException e) {
      throw new UnsupportedOperationException("Could not access expression, encoding, data or width in JSON of Cryptol result.\nJSON: " + json.toString(), e);
    }
    if (("bits".equals(expression)) && ("hex".equals(encoding))) {
      bitseq = new BigInteger(data, 16);
      type = CryptolType.WORD;
      checkValue();
      return;
    } else {
      throw new UnsupportedOperationException("JSON result from Cryptol troublesome.\nJSON: " + json.toString());
    }
  }
  
  public boolean getBit() {
    if (CryptolType.BIT == type) {
      return bit;
    } else {
      throw new UnsupportedOperationException("Requested bit from a nonbit Cryotol value");
    }
  }
  
  private void toJSON() {
    json = new JSONObject();
    switch (type) {
      case BIT:
        json.put("value", bit);
        break;
      case WORD:
        json.put("expression", "bits");
        json.put("encoding", "hex");
        json.put("data", bitseq.toString(16));
        json.put("width", size);
        break;
      default:
        throw new UnsupportedOperationException("Conversion to JSON unimplemented for type `" + type + "'.");
    }
  }
  
  public String toString() {
    try {
      fromJSON();
      switch (type) {
        case BIT:
          if (bit) {
            return "True";
          } else {
            return "False";
          }
        case WORD:
          if (0 == size % 4) {
            String s = bitseq.toString(16);
            while (size / 4 > s.length()) {
              s = "0" + s;
            }
            return "0x" + s;
          }
          if (0 == size % 3) {
            String s = bitseq.toString(8);
            while (size / 3 > s.length()) {
              s = "0" + s;
            }
            return "0o" + s;
          }
          String s = bitseq.toString(2);
          while (size > s.length()) {
            s = "0" + s;
          }
          return "0b" + s;
        default:
          throw new UnsupportedOperationException("Conversion to String unimplemented.");
      }
    } catch (Exception e) {
      return json.toString();
    }
  }
  
}
