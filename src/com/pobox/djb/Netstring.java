package com.pobox.djb;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

public class Netstring {
  
  public static byte[] render(CharSequence s, Charset charset) {
    try {
      byte[] dataOctets = s.toString().getBytes(charset);
      int dataLength = dataOctets.length;
      byte[] lengthOctets = Integer.toString(dataLength).getBytes("US-ASCII");
      int lengthLength = lengthOctets.length;
      byte[] netstring = new byte[lengthLength + 1 + dataLength + 1];
      for (int i = 0; i < lengthLength; i++) {
        netstring[i] = lengthOctets[i];
      }
      netstring[lengthLength] = ':';
      for (int i = 0; i < dataLength; i++) {
        netstring[lengthLength + 1 + i] = dataOctets[i];
      }
      netstring[lengthLength + 1 + dataLength] = ',';
      return netstring;
    } catch (Exception e) {
      throw new NetstringException(e);
    }
  }
  
  public static byte[] render(CharSequence s, String charsetName) {
    try {
      return render(s, Charset.forName(charsetName));
    } catch (Exception e) {
      throw new NetstringException(e);
    }
  }

  public static byte[] render(CharSequence s) {
    try {
      return render(s, Charset.defaultCharset());
    } catch (Exception e) {
      throw new NetstringException(e);
    }
  }
  
  public static String parse(InputStream in, Charset charset) {
    String s = null;
    try {
      int c;
      StringBuilder lengthChars = new StringBuilder();
      do {
        c = in.read();
        switch (c) {
          case ':':
            break;
          case '0':
          case '1':
          case '2':
          case '3':
          case '4':
          case '5':
          case '6':
          case '7':
          case '8':
          case '9':
            lengthChars.append((char) c);
            break;
          case -1:
            throw new NetstringException("End of input while parsing netstring length.");
          default:
            throw new NetstringException("Invalid character `" + (char) c + "' while parsing netstring length.");
        }
      } while (':' != c);
      if (0 == lengthChars.length()) {
        throw new NetstringException("Netstring length has no digits.");
      }
      if (1 < lengthChars.length() && '0' == lengthChars.charAt(0)) {
        throw new NetstringException("Netstring length has invalid leading zero(es).");
      }
      int length = Integer.parseInt(lengthChars.toString());
      byte[] dataOctets = new byte[length];
      for (int i = 0; i < dataOctets.length; i++) {
        c = in.read();
        if (-1 == c) {
          throw new NetstringException("Netstring data ended prematurely.");
        }
        dataOctets[i] = (byte) c;
      }
      c = in.read();
      if (',' != c) {
        throw new NetstringException("Netstring data not terminate with comma.");
      }
      s = new String(dataOctets, charset);
    } catch (Exception e) {
      throw new NetstringException(e);
    }
    return s;
  }

  public static String parse(InputStream in, String charsetName) {
    return parse(in, Charset.forName(charsetName));
  }

  public static String parse(InputStream in) {
    return parse(in, Charset.defaultCharset());
  }

  public static String parse(byte[] netstring, Charset charset) {
    ByteArrayInputStream in = new ByteArrayInputStream(netstring);
    String s = parse(in, charset);
    if (0 == in.available()) {
      return s;
    } else {
      throw new NetstringException("Bytes remaining after parse.");
    }
  }
  
  public static String parse(byte[] netstring, String charsetName) {
    return parse(netstring, Charset.forName(charsetName));
  }

  public static String parse(byte[] netstring) {
    return parse(netstring, Charset.defaultCharset());
  }

}
