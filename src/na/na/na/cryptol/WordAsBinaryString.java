package na.na.na.cryptol;

public class WordAsBinaryString {
  
  private String wabs;
  
  private WordAsBinaryString(String s) {
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

  public static WordAsBinaryString valueOf(String s) {
    return new WordAsBinaryString(s);
  }
  
  public String toString() {
    return wabs;
  }
  
  public int Size() {
    return wabs.length();
  }
  
}
