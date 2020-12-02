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
    
    // System.out.println("Available charsets: " + Charset.availableCharsets() + "\n");
    
    System.out.println();
    byte[] emptyNetstring = new byte[] {0x30, 0x3a, 0x2c};
    System.out.println("Checking emptyNetstring rendering: " + Arrays.equals(emptyNetstring, Netstring.render("")));
    
    System.out.println();
    byte[] helloWorldNetstring = new byte[] {0x31, 0x32, 0x3a, 0x68, 0x65, 0x6c, 0x6c, 0x6f, 0x20, 0x77, 0x6f, 0x72, 0x6c, 0x64, 0x21, 0x2c};
    System.out.println("Checking helloWorldNetstring rendering: " + Arrays.equals(helloWorldNetstring, Netstring.render("hello world!")));
    
    System.out.println();
    for (int i = 0; i < 0x110000; i++) {
      System.out.print(i + " " + (100 * i) / 0x110000 + "%...\r");
      //      if (0xd800 <= i && i <= 0xdfff) {
      //        continue;
      //      }
      String s = new String(new int[] {i}, 0, 1);
      byte[] bs = Netstring.render(s);
      byte[] questionableNetstring = {49, 58, 63, 44};
      if (0x00d800 <= i && i <= 0x00dfff) {
        if (!Arrays.equals(bs, questionableNetstring)) {
          System.out.println("Bad code point misbehaved: " + i );
        }
      } else {
        if (false) {
        } else if (0x000000 <= i && i < 0x010000) {
          if (1 != s.length()) {
            System.out.println(i + " " + s.length() + "           ");
          }
        } else if (0x010000 <= i && i < 0x110000) {
          if (2 != s.length()) {
            System.out.println(i + " " + s.length() + "           ");
          }
        } else {
          System.out.println(i + " untested for string conversion length.");
        }
        if (!s.equals(Netstring.parse(bs))) {
          System.out.println("Failed for codepoint: " + i );
          System.out.println("  s.codePointAt(0): " + s.codePointAt(0));
          System.out.println("  Corresponding string, \"" + s + "\", has length " + s.length());
          System.out.print("  Rendered netstring: {");
          for (int j = 0; j < bs.length; j++) {
            System.out.print(((0 < j) ? ", " : "") + bs[j]);
          }
          System.out.println("}");
        }
      }
    }
    System.out.println("Completed testing of all individual code points.");
    
    System.out.println();
    for (int i = 0; i < 100000; i++) {
      System.out.print(i + "...\r");
      int[] cs = randomCodePoints(i);
      String s = new String(cs, 0, cs.length);
      if (!s.equals(Netstring.parse(Netstring.render(s)))) {
        System.out.print("Failed for codepoints: {");
        for (int j = 0; j < cs.length; j++) {
          System.out.print(((0 < j) ? ", " : "") + cs[j]);
        }
        System.out.println("}");
      }
    }
    System.out.println("Random string tests completed.");
    
    System.out.println();
  }
  
  // private static double expectedFattening = 2 * (1048576 / 1114112) + 1 * (65536 / 1114112); // ~ 1.9411764706 we just use 2.
  
  private static Random random = new Random();
  
  private static String randomString(int n) {
    return new String(randomCodePoints(n), 0, n);
  }
  
  private static int randomCodePoint() {
    int c = random.nextInt(0x110000 - 0x800); // 1,112,064
    return (0xd800 > c) ? c : c + 0x800;
  }
  
  private static int[] randomCodePoints(int n) {
    int[] cs = new int[n];
    for (int i = 0; i < n; i++) {
      cs[i] = randomCodePoint();
    }
    return cs;
  }
  
  
}
