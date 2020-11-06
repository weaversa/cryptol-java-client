import org.json.*;
import na.na.na.*;

public class CaaSTest {
  
  //For testing
  public static void main(String[] args) throws CaaSException {
    CaaS caas = new CaaS(args[0], Integer.parseInt(args[1]));
    System.out.println("Connected to CaaS without exception.\n");

    System.out.println("Evaluating `lg2 2'...");
    System.out.println(caas.callUnaryHexFunction("lg2", "2"));
    System.out.println("");
    
    System.out.println("Evaluating `False'...");
    System.out.println(caas.evaluateExpression("False").toString());
    System.out.println("");

    System.out.println("Evaluating `0b1 + 0b1'...");
    System.out.println(caas.evaluateExpression("0b1 + 0b1").toString());
    System.out.println("");

    System.out.println("Evaluating `62831853072 : Integer'...");
    System.out.println(caas.evaluateExpression("62831853072 : Integer").toString());
    System.out.println("");

    System.out.println("Evaluating `(2 : Z 3) + 2'...");
    System.out.println(caas.evaluateExpression("(2 : Z 3) + 2").toString());
    System.out.println("");

    System.out.println("Evaluating `~zero : [2][3]'...");
    System.out.println(caas.evaluateExpression("~zero : [2][3]").toString());
    System.out.println("");

    System.out.println("Evaluating `(2, 3, 5) : ([2], [2], [3])'...");
    System.out.println(caas.evaluateExpression("(2, 3, 5) : ([2], [2], [3])").toString());
    System.out.println("");

    String onePlusTwo =
    caas.getHex(caas.getAnswer(caas.evaluateExpression("0xa + 0x1")));
    System.out.println("0xa + 0x1 == 0x" + onePlusTwo + "\n");
    
    JSONObject aesResult = null;
    String ct = null;
    
//    caas.LoadModule("Primitive::Symmetric::Cipher::Block::AES");
//    JSONObject aesResult =
//    caas.EvaluateExpression("aesEncrypt(10, 11)");
//    String ct = caas.GetHex(caas.GetAnswer(aesResult));
//    System.out.println("aesEncrypt(10, 11) == 0x" + ct + "\n");
    
    //Test state reset
    // caas.ResetState();
    
    JSONObject hexResult =
    caas.evaluateExpression("0xab10");
    
    String[] hexArrayResult =
    caas.getHexArray(
                     caas.getAnswer(
                                    caas.evaluateExpression(
                                                            caas.fromHexArray(new String[]{"ab", "bc", "de"}, 8))));
    
    for(int i = 0; i < hexArrayResult.length; i++) {
      System.out.print(hexArrayResult[i] + " ");
    }
    System.out.println();
    
    //caas.loadModule("SuiteX");
    caas.loadModule("SuiteB");
    aesResult =
    caas.evaluateExpression("aesEncryptBlock (aes128EncryptSchedule 0x000102030405060708090a0b0c0d0e0f) 0x00112233445566778899aabbccddeeff");
    ct = caas.getHex(caas.getAnswer(aesResult));
    
    System.out.println("    computed: 0x" + ct);
    System.out.println("known answer: 0x" + "69c4e0d86a7b0430d8cdb78070b4c55a");
    System.out.println();

    System.out.println("Loading SuiteB again...");
    caas.loadModule("SuiteB");
    System.out.println();

    System.out.println("Loading SuiteX...");
    caas.loadModule("SuiteX");
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
