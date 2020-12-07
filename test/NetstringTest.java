import com.pobox.djb.Netstring;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;

public class NetstringTest {
  
  /* These examples are from:
   
   Wikipedia contributors. Netstring. Wikipedia, The Free Encyclopedia. June 1, 2020, 18:16 UTC. Available at: https://en.wikipedia.org/w/index.php?title=Netstring&oldid=960209616. Accessed December 2, 2020.
   
   */
        
  public static void main(String[] args) {
        
    System.out.println();

    System.out.println("Default charset: " + Charset.defaultCharset());

    System.out.println();

    System.out.println("Available charsets: " + Charset.availableCharsets().keySet().size());

    System.out.println();

    {
      byte[] emptyNetstring = new byte[] {0x30, 0x3a, 0x2c};
      System.out.println("Checking emptyNetstring rendering/parsing: " + Arrays.equals(emptyNetstring, Netstring.render("")) + " && " + "".equals(Netstring.parse(emptyNetstring)));
    }
    
    System.out.println();

    {
      String helloWorld = "hello world!";
      byte[] helloWorldNetstring = new byte[] {0x31, 0x32, 0x3a, 0x68, 0x65, 0x6c, 0x6c, 0x6f, 0x20, 0x77, 0x6f, 0x72, 0x6c, 0x64, 0x21, 0x2c};
      System.out.println("Checking helloWorldNetstring rendering/parsing: " + Arrays.equals(helloWorldNetstring, Netstring.render(helloWorld)) + " && " + helloWorld.equals(Netstring.parse(helloWorldNetstring)));
    }
    
    System.out.println();
    
    {
      byte[] questionableNetstring = {49, 58, 63, 44};
      for (int i = MIN_CODE_POINT; i <= MAX_CODE_POINT; i++) {
        System.out.print(i + " " + (100 * i) / CODE_POINTS + "%...\r");
        String s = new String(new int[] {i}, 0, 1);
        byte[] bs = Netstring.render(s);
        if (MIN_SURROGATE_CODE_POINT <= i && i <= MAX_SURROGATE_CODE_POINT) {
          if (!Arrays.equals(bs, questionableNetstring)) {
            System.out.println("Surrogate code point behaved unexpectedly: " + i );
          }
        } else {
          if (false) {
          } else if (MIN_BASIC_MULTILINGUAL_PLANE_CODE_POINT <= i && i <= MAX_BASIC_MULTILINGUAL_PLANE_CODE_POINT) {
            if (1 != s.length()) {
              System.out.println("Basic multilingual plane code point " + i + " produced unexpected length " + s.length() + ".");
            }
          } else if (MIN_SUPPLEMENTARY_PLANES_CODE_POINT <= i && i <= MAX_SUPPLEMENTARY_PLANES_CODE_POINT) {
            if (2 != s.length()) {
              System.out.println("Supplementary planes code point " + i + " produced unexpected length " + s.length() + ".");
            }
          } else {
            System.out.println("Code point " + i + " untested for string conversion length.");
          }
          if (!s.equals(Netstring.parse(bs))) {
            System.out.println("Netstring.parse failed for codepoint: " + i );
            System.out.println("  s.codePointAt(0): " + s.codePointAt(0));
            System.out.println("  Corresponding string, \"" + s + "\", has length " + s.length());
            System.out.println("  Rendered netstring: " + Arrays.toString(bs));
          }
          if (!Arrays.equals(bs, Netstring.render(s))) {
            System.out.println("Netstring.render failed for codepoint: " + i );
            System.out.println("  s.codePointAt(0): " + s.codePointAt(0));
            System.out.println("  Corresponding string, \"" + s + "\", has length " + s.length());
            System.out.println("  Rendered netstring: " + Arrays.toString(bs));
          }
          byte[] cs = codePointToUTF8Bytes(i);
          for (int j = 0; j < cs.length; j++) {
            if (bs[bs.length - cs.length - 1 + j] != cs[j]) {
              System.out.println("Netstring.render failed for codepoint: " + i );
              System.out.println("    s.codePointAt(0): " + s.codePointAt(0));
              System.out.println("  Rendered netstring: " + Arrays.toString(bs));
              System.out.println("         UTF-8 bytes: " + Arrays.toString(cs));
              break;
            }
          }
        }
      }
      System.out.println("Tested parsing/rendering of all individual code points.");
    }
    
    System.out.println();

    {
      int RANDOM_STRING_TESTS = 10000019;
      int[] lengths = new int[128];
      for (int i = 0; i < RANDOM_STRING_TESTS; i++) {
        System.out.print(i + " " + (100 * i / RANDOM_STRING_TESTS) + "%...\r");
        int n = randomLength();
        lengths[n]++;
        int[] cs = randomCodePoints(n);
        String s = new String(cs, 0, cs.length);
        if (!s.equals(Netstring.parse(Netstring.render(s)))) {
          System.out.println("Failed for codepoints: " + Arrays.toString(cs));
        }
      }
      System.out.println(RANDOM_STRING_TESTS + " random string tests completed.");
      System.out.println("  Lengths histogram: " + Arrays.toString(lengths));
    }

    System.out.println();

    {
      int[] lengths = new int[128];
      int RANDOM_BYTE_ARRAY_TESTS = 10000019;
      for (int i = 0; i < RANDOM_BYTE_ARRAY_TESTS; i++) {
        System.out.print(i + " " + (100 * i / RANDOM_BYTE_ARRAY_TESTS) + "%...\r");
        int n = randomLength();
        lengths[n]++;
        String length = String.valueOf(n);
        int m = length.length() + 1 + n + 1;
        ByteBuffer bb = ByteBuffer.allocate(m);
        bb.put(length.getBytes());
        bb.put((byte) ':');
        bb.put(randomUTF8ByteArray(n));
        bb.put((byte) ',');
        byte[] bs = bb.array();
        String s = Netstring.parse(bs);
        byte[] cs = Netstring.render(s);
        if (!Arrays.equals(bs, cs)) {
          System.out.println("");
          System.out.println("     byte buffer: " + Arrays.toString(bs));
          System.out.println("      comparison: " + Arrays.toString(cs));
          System.out.println("  parse as bytes: " + Arrays.toString(s.getBytes()));
          System.out.println("  parse as chars: " + Arrays.toString(s.toCharArray()));
          break;
        }
      }
      System.out.println(RANDOM_BYTE_ARRAY_TESTS + " random byte array tests completed.");
      System.out.println("  Lengths histogram: " + Arrays.toString(lengths));
    }

    System.out.println();

    {
      int[] distribution = byteUTF8Distribution();
      System.out.println("UTF8 bytes distribution: " + Arrays.toString(distribution));
      int sum = 0;
      for (int i = 0; i < distribution.length; i++) {
        sum += distribution[i];
      }
      int esum = 128 + 1920*2 + 53248*3 + 8192*3 + 1048576*4;
      System.out.println("  sum over distribution = " + sum + " which should agree with " + esum + ".");
    }
    
    System.out.println();

    {
      int[] distribution = lengthUTF8Distribution();
      System.out.println("UTF8 bytes' length distribution: " + Arrays.toString(distribution));
      int sum = 0;
      for (int i = 0; i < distribution.length; i++) {
        sum += distribution[i];
      }
      System.out.println("  sum over distribution = " + sum + " which should agree with " + LEGAL_CODE_POINTS + ".");
      sum = 0;
      for (int i = 0; i < distribution.length; i++) {
        sum += i * distribution[i];
      }
      int esum = 128 + 1920*2 + 53248*3 + 8192*3 + 1048576*4;
      System.out.println("  weighted sum over distribution = " + sum + " which should agree with " + esum + ".");
    }
    
    System.out.println();

  }
    
  private static Random random = new Random();
  
  private static byte randomLength() {
    byte n, d;
    if (!random.nextBoolean()) {
      n = 7; d = -1;
    } else {
      n = 8; d = +1;
    }
    for (; !random.nextBoolean(); n += d) {
      if (0 > n) {
        return Byte.MAX_VALUE;
      }
    }
    if (0 > n) {
      return Byte.MAX_VALUE;
    }
    return n;
  }
  
  private static String randomString() {
    return randomString(randomLength());
  }
  
  private static String randomString(int n) {
    return new String(randomCodePoints(n), 0, n);
  }
  
  private static byte[] randomUTF8ByteArray() {
    return randomUTF8ByteArray(randomLength());
  }
  
  private static byte[] randomUTF8ByteArray(int n) {
    switch (Integer.signum(n)) {
      case -1:
        throw new IllegalArgumentException();
      case 0:
        return new byte[] {};
      default:
        ByteBuffer bb = ByteBuffer.allocate(n);
        while (0 < bb.remaining()) {
          bb.put(randomCodePointAsUTF8Bytes(Integer.min(4, bb.remaining())));
        }
        return bb.array();
    }
  }
  
  public static final int MIN_BASIC_MULTILINGUAL_PLANE_CODE_POINT = 0x0; // 0
  
  public static final int MIN_SURROGATE_CODE_POINT = 0xd800; // 55,296

  public static final int MAX_SURROGATE_CODE_POINT = 0xdfff; // 57,343

  public static final int MAX_BASIC_MULTILINGUAL_PLANE_CODE_POINT = 0xffff; // 65,535

  public static final int MIN_SUPPLEMENTARY_PLANES_CODE_POINT = 0x10000; // 65,536

  public static final int MAX_SUPPLEMENTARY_PLANES_CODE_POINT = 0x10ffff; // 1,114,111
  
  public static final int MIN_CODE_POINT = MIN_BASIC_MULTILINGUAL_PLANE_CODE_POINT;

  public static final int MAX_CODE_POINT = MAX_SUPPLEMENTARY_PLANES_CODE_POINT;

  public static final int CODE_POINTS = MAX_CODE_POINT - MIN_CODE_POINT + 1; // 1,114,112

  public static final int SURROGATE_CODE_POINTS = MAX_SURROGATE_CODE_POINT - MIN_SURROGATE_CODE_POINT + 1;  // 2,048

  public static final int LEGAL_CODE_POINTS = CODE_POINTS - SURROGATE_CODE_POINTS;  // 1,112,064

  public static final int LEGAL_1BYTE_CODE_POINTS = 0x80;  // 128

  public static final int LEGAL_2BYTE_CODE_POINTS = 0x800;  // 2,048

  public static final int LEGAL_3BYTE_CODE_POINTS = 0x10000 - SURROGATE_CODE_POINTS;  // 63,488

  public static final int LEGAL_4BYTE_CODE_POINTS = LEGAL_CODE_POINTS; // 0x110000 - SURROGATE_CODE_POINTS;  // 1,112,064
  
  public static final int[] LEGAL_CODE_POINTS_BY_UTF8_BYTES = new int[]{
  0,
  0x80,  // 128
  0x800,  // 2,048
  0x10000 - SURROGATE_CODE_POINTS,  // 63,488
  0x110000 - SURROGATE_CODE_POINTS  // 1,112,064
  };
  
  private static int randomCodePoint() {
    int c = random.nextInt(LEGAL_CODE_POINTS);
    return (MIN_SURROGATE_CODE_POINT > c) ? c : c + SURROGATE_CODE_POINTS;
  }
  
  private static int[] randomCodePoints(int n) {
    int[] cs = new int[n];
    for (int i = 0; i < n; i++) {
      cs[i] = randomCodePoint();
    }
    return cs;
  }
  
  private static byte[] codePointToUTF8Bytes(int a) {
    if (MIN_CODE_POINT > a || MAX_CODE_POINT < a) {
      throw new IllegalArgumentException(a + " outside of [" + MIN_CODE_POINT + ".." + MAX_CODE_POINT + "]");
    }
    if (MIN_SURROGATE_CODE_POINT <= a && MAX_SURROGATE_CODE_POINT >= a) {
      throw new IllegalArgumentException(a + " is a surrogate code point; i.e. within [" + MIN_SURROGATE_CODE_POINT + ".." + MAX_SURROGATE_CODE_POINT + "]");
    }
    if (0x00 <= a && a <= 0x7f) {
      return new byte[] {toByte(a)};
    }
    if (0x80 <= a && a <= 0x7ff) {
      int b = (a & 0x3f) | 0x80;
      a >>>= 6;
      a |= 0xc0;
      return new byte[] {toByte(a), toByte(b)};
    }
    if (0x800 <= a && a <= 0xffff) {
      int c = (a & 0x3f) | 0x80;
      a >>>= 6;
      int b = (a & 0x3f) | 0x80;
      a >>>= 6;
      a |= 0xe0;
      return new byte[] {toByte(a), toByte(b), toByte(c)};
    }
    if (0x10000 <= a && a <= 0x10ffff) {
      int d = (a & 0x3f) | 0x80;
      a >>>= 6;
      int c = (a & 0x3f) | 0x80;
      a >>>= 6;
      int b = (a & 0x3f) | 0x80;
      a >>>= 6;
      a |= 0xf0;
      return new byte[] {toByte(a), toByte(b), toByte(c), toByte(d)};
    }
    throw new IllegalArgumentException(a + " outside of [0x0..0x10ffff]");
  }
  
  private static byte[] randomCodePointAsUTF8Bytes(int n) {
    if (1 > n || 4 < n) {
      throw new IllegalArgumentException(n + " outside of [1..4]");
    }
    int a = random.nextInt(LEGAL_CODE_POINTS_BY_UTF8_BYTES[n]);
    a = (MIN_SURROGATE_CODE_POINT > a) ? a : a + SURROGATE_CODE_POINTS;
    return codePointToUTF8Bytes(a);
  }
  
  private static byte toByte(int i) {
    if (0 > i || 255 < i) {
      throw new IllegalArgumentException(i + " not in [0..255].");
    }
    return (byte) i;
  }
  
  private static int[] byteUTF8Distribution() {
    int[] counts = new int[256];
    for (int i = 0; i < MIN_SURROGATE_CODE_POINT; i++) {
      byte[] bs = codePointToUTF8Bytes(i);
      for (int j = 0; j < bs.length; j++) {
        counts[(bs[j] + 0x100) & 0xff]++;
      }
    }
    for (int i = MAX_SURROGATE_CODE_POINT + 1; i < CODE_POINTS; i++) {
      byte[] bs = codePointToUTF8Bytes(i);
      for (int j = 0; j < bs.length; j++) {
        counts[(bs[j] + 0x100) & 0xff]++;
      }
    }
    return counts;
  }
  
  private static int[] lengthUTF8Distribution() {
    int[] counts = new int[5];
    for (int i = 0; i < MIN_SURROGATE_CODE_POINT; i++) {
      byte[] bs = codePointToUTF8Bytes(i);
      counts[bs.length]++;
    }
    for (int i = MAX_SURROGATE_CODE_POINT + 1; i < CODE_POINTS; i++) {
      byte[] bs = codePointToUTF8Bytes(i);
      counts[bs.length]++;
    }
    return counts;
  }
  
}
