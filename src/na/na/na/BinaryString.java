package na.na.na;

public class BinaryString {
  
  private String wabs;
  
  private BinaryString(String s) {
    for (int i = 0; i < s.length(); i++) {
      switch (s.charAt(i)) {
        case '0':
        case '1':
          break;
        default:
          throw new IllegalArgumentException("Nonbinary digits.");
      }
    }
    wabs = s;
  }

  public static BinaryString valueOf(String s) {
    return new BinaryString(s);
  }
  
  public String toString() {
    return wabs;
  }
  
  public int size() {
    return wabs.length();
  }
  
}
