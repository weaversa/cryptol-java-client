import org.json.*;
import na.na.na.*;

public class TestHarness {
  
  //For testing
  public static void main(String[] args) {
    CaaS caas = new CaaS();
    boolean success = caas.Connect(args[0], Integer.parseInt(args[1]));
    System.out.println("Success: " + success + "\n");
    
    String onePlusTwo =
    caas.GetHex(caas.GetAnswer(caas.EvaluateExpression("0xa + 0x1")));
    System.out.println("0xa + 0x1 == 0x" + onePlusTwo + "\n");
    
    JSONObject aesResult = null;
    String ct = null;
    
//    caas.LoadModule("Primitive::Symmetric::Cipher::Block::AES");
//    JSONObject aesResult =
//    caas.EvaluateExpression("aesEncrypt(10, 11)");
//    String ct = caas.GetHex(caas.GetAnswer(aesResult));
//    System.out.println("aesEncrypt(10, 11) == 0x" + ct + "\n");
    
    //Test state reset
    caas.ResetState();
    
    JSONObject hexResult =
    caas.EvaluateExpression("0xab10");
    
    String[] hexArrayResult =
    caas.GetHexArray(
                     caas.GetAnswer(
                                    caas.EvaluateExpression(
                                                            caas.FromHexArray(new String[]{"ab", "bc", "de"}, 8))));
    
    for(int i = 0; i < hexArrayResult.length; i++) {
      System.out.print(hexArrayResult[i] + " ");
    }
    System.out.println();
    
    caas.LoadModule("SuiteB");
    aesResult =
    caas.EvaluateExpression("aesEncryptBlock (aes128EncryptSchedule 0x000102030405060708090a0b0c0d0e0f) 0x00112233445566778899aabbccddeeff");
    ct = caas.GetHex(caas.GetAnswer(aesResult));
    
    System.out.println("    computed: 0x" + ct);
    System.out.println("known answer: 0x" + "69c4e0d86a7b0430d8cdb78070b4c55a");
    System.out.println();
    
//    JSONObject tuple = null;
//    tuple = caas.AddToTuple(tuple, "1", 128);
//    tuple = caas.AddToTuple(tuple, "2", 128);
//    JSONArray aesArguments = null;
//    aesArguments = caas.AddArgument(aesArguments, tuple);
//    aesResult =
//    caas.EvaluateExpression(caas.Call("aesEncrypt", aesArguments));
//    ct = caas.GetHex(caas.GetAnswer(aesResult));
//    System.out.println(ct + "\n");
  }
}



// aesEncryptBlock   (aesExpandEncryptSchedule 0x000102030405060708090a0b0c0d0e0f) 0x00112233445566778899aabbccddeeff == 0x69c4e0d86a7b0430d8cdb78070b4c55a
