import com.pobox.djb.Netstring;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;

public class NetstringTest {
  
  /* These examples are from:
   
   Wikipedia contributors. Netstring. Wikipedia, The Free Encyclopedia. June 1, 2020, 18:16 UTC. Available at: https://en.wikipedia.org/w/index.php?title=Netstring&oldid=960209616. Accessed December 2, 2020.
   
   */
        
  public static void main(String[] args) {
    
    // System.out.println("\nDefault charset: " + Charset.defaultCharset() + "\n");
    
    System.out.println("Available charsets: " + Charset.availableCharsets().keySet().size() + "\n");
        
    System.out.println();
    byte[] emptyNetstring = new byte[] {0x30, 0x3a, 0x2c};
    System.out.println("Checking emptyNetstring rendering: " + Arrays.equals(emptyNetstring, Netstring.render("")));
    
    System.out.println();
    byte[] helloWorldNetstring = new byte[] {0x31, 0x32, 0x3a, 0x68, 0x65, 0x6c, 0x6c, 0x6f, 0x20, 0x77, 0x6f, 0x72, 0x6c, 0x64, 0x21, 0x2c};
    System.out.println("Checking helloWorldNetstring rendering: " + Arrays.equals(helloWorldNetstring, Netstring.render("hello world!")));
    
    System.out.println();
    byte[] questionableNetstring = {49, 58, 63, 44};
    for (int i = MIN_CODE_POINT; i <= MAX_CODE_POINT; i++) {
      System.out.print(i + " " + (100 * i) / CODE_POINTS + "%...\r");
      String s = new String(new int[] {i}, 0, 1);
      byte[] bs = Netstring.render(s);
      if (MIN_SURROGATE_CODE_POINT <= i && i <= MAX_SURROGATE_CODE_POINT) {
        if (!Arrays.equals(bs, questionableNetstring)) {
          System.out.println("Invalid code point behaved unexpectedly: " + i );
        }
      } else {
        if (false) {
        } else if (   MIN_BASIC_MULTILINGUAL_PLANE_CODE_POINT <= i
                   && i <= MAX_BASIC_MULTILINGUAL_PLANE_CODE_POINT) {
          if (1 != s.length()) {
            System.out.println("Code point " + i + " produced unexpected length " + s.length() + ".");
          }
        } else if (   MIN_SUPPLEMENTARY_PLANES_CODE_POINT <= i
                   && i <= MAX_SUPPLEMENTARY_PLANES_CODE_POINT) {
          if (2 != s.length()) {
            System.out.println("Code point " + i + " produced unexpected length " + s.length() + ".");
          }
        } else {
          System.out.println(i + " untested for string conversion length.");
        }
        if (!s.equals(Netstring.parse(bs))) {
          System.out.println("Failed for codepoint: " + i );
          System.out.println("  s.codePointAt(0): " + s.codePointAt(0));
          System.out.println("  Corresponding string, \"" + s + "\", has length " + s.length());
          System.out.println("  Rendered netstring: " + Arrays.toString(bs));
        }
      }
    }
    System.out.println("Completed testing of all individual code points.");
    
    int RANDOM_STRING_TESTS = 10000000;
    
    System.out.println();
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
    System.out.println("Random string tests completed.");
    System.out.println("  Lengths histogram: " + Arrays.toString(lengths));

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
      if (0 >= n) {
        return 0;
      }
    }
    return n;
  }
  
  private static String randomString() {
    return randomString(randomLength());
  }
  
  private static String randomString(int n) {
    return new String(randomCodePoints(n), 0, n);
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
  
  
}
