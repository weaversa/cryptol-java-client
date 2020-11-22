import na.na.na.cryptol.caas.JSONFlavoredCaaS;
import na.na.na.cryptol.caas.CaaSException;
import org.json.*;

public class JSONFlavoredCaaSTest {
  
  //For testing
  public static void main(String[] args) throws CaaSException {
    JSONArray arguments = new JSONArray();

    JSONFlavoredCaaS jfCaaS = new JSONFlavoredCaaS(args[0], Integer.parseInt(args[1]));
    System.out.println("Connected to CaaS without exception.\n");

    System.out.println("Evaluating `False'...");
    System.out.println(jfCaaS.evaluateExpression("False").toString());
    System.out.println("");

    System.out.println("");
    System.out.println("Evaluating `lg2 2'...");
    arguments = new JSONArray();
    arguments.put(jfCaaS.fromHex("2", 2));
    System.out.println(jfCaaS.call("lg2", arguments));
    System.out.println("");
    
    System.out.println("");
    jfCaaS.loadModule("SuiteB");
    System.out.println("");
    System.out.println("Evaluating `sha224 '...");
    arguments = new JSONArray();
    arguments.put(jfCaaS.fromHex("54686520717569636b2062726f776e20666f78206a756d7073206f76657220746865206c617a7920646f672e", 352));
    System.out.println(jfCaaS.call("sha224", arguments));
    System.out.println("Should return 619cba8e8e05826e9b8c519c0a5c68f4fb653e8a3d8aa04bb2c8cd4c");

    /*
     According to Wikipedia:
SHA224("The quick brown fox jumps over the lazy dog.") ~~>

0x 619cba8e8e05826e9b8c519c0a5c68f4fb653e8a3d8aa04bb2c8cd4c

     From Cryptol:
     
     join("The quick brown fox jumps over the lazy dog.") ~~>
     
     0x54686520717569636b2062726f776e20666f78206a756d7073206f76657220746865206c617a7920646f672e
     
     */

    System.out.println("Evaluating `False'...");
    System.out.println(jfCaaS.evaluateExpression("False").toString());
    System.out.println("");

    System.out.println("Evaluating `0b1 + 0b1'...");
    System.out.println(jfCaaS.evaluateExpression("0b1 + 0b1").toString());
    System.out.println("");

    System.out.println("Evaluating `62831853072 : Integer'...");
    System.out.println(jfCaaS.evaluateExpression("62831853072 : Integer").toString());
    System.out.println("");

    System.out.println("Evaluating `(2 : Z 3) + 2'...");
    System.out.println(jfCaaS.evaluateExpression("(2 : Z 3) + 2").toString());
    System.out.println("");

    System.out.println("Evaluating `~zero : [2][3]'...");
    System.out.println(jfCaaS.evaluateExpression("~zero : [2][3]").toString());
    System.out.println("");

    System.out.println("Evaluating `(2, 3, 5) : ([2], [2], [3])'...");
    System.out.println(jfCaaS.evaluateExpression("(2, 3, 5) : ([2], [2], [3])").toString());
    System.out.println("");

    String onePlusTwo =
    jfCaaS.getHex(jfCaaS.getAnswer(jfCaaS.evaluateExpression("0xa + 0x1")));
    System.out.println("0xa + 0x1 == 0x" + onePlusTwo + "\n");
    
    JSONObject aesResult = null;
    String ct = null;
    
//    jfCaaS.LoadModule("Primitive::Symmetric::Cipher::Block::AES");
//    JSONObject aesResult =
//    jfCaaS.EvaluateExpression("aesEncrypt(10, 11)");
//    String ct = jfCaaS.GetHex(jfCaaS.GetAnswer(aesResult));
//    System.out.println("aesEncrypt(10, 11) == 0x" + ct + "\n");
    
    //Test state reset
    // jfCaaS.ResetState();
    
    JSONObject hexResult =
    jfCaaS.evaluateExpression("0xab10");
    
    String[] hexArrayResult =
    jfCaaS.getHexArray(
                     jfCaaS.getAnswer(
                                    jfCaaS.evaluateExpression(
                                                            jfCaaS.fromHexArray(new String[]{"ab", "bc", "de"}, 8))));
    
    for(int i = 0; i < hexArrayResult.length; i++) {
      System.out.print(hexArrayResult[i] + " ");
    }
    System.out.println();
    
    jfCaaS.loadModule("SuiteB");
    aesResult =
    jfCaaS.evaluateExpression("aesEncryptBlock (aes128EncryptSchedule 0x000102030405060708090a0b0c0d0e0f) 0x00112233445566778899aabbccddeeff");
    ct = jfCaaS.getHex(jfCaaS.getAnswer(aesResult));
    
    System.out.println("    computed: 0x" + ct);
    System.out.println("known answer: 0x" + "69c4e0d86a7b0430d8cdb78070b4c55a");
    System.out.println();

    System.out.println("Loading SuiteB again...");
    jfCaaS.loadModule("SuiteB");
    System.out.println();

    System.out.println("Loading SuiteX...");
    jfCaaS.loadModule("SuiteX");
    System.out.println();


//    JSONObject tuple = null;
//    tuple = jfCaaS.AddToTuple(tuple, "1", 128);
//    tuple = jfCaaS.AddToTuple(tuple, "2", 128);
//    JSONArray aesArguments = null;
//    aesArguments = jfCaaS.AddArgument(aesArguments, tuple);
//    aesResult =
//    jfCaaS.EvaluateExpression(jfCaaS.Call("aesEncrypt", aesArguments));
//    ct = jfCaaS.GetHex(jfCaaS.GetAnswer(aesResult));
//    System.out.println(ct + "\n");
  }
}



// aesEncryptBlock   (aesExpandEncryptSchedule 0x000102030405060708090a0b0c0d0e0f) 0x00112233445566778899aabbccddeeff == 0x69c4e0d86a7b0430d8cdb78070b4c55a
