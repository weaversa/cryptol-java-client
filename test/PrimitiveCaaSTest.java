import na.na.na.cryptol.caas.PrimitiveCaaS;
import na.na.na.cryptol.caas.CaaSException;
import org.json.*;

public class PrimitiveCaaSTest {
  
  //For testing
  public static void main(String[] args) throws CaaSException {
    
    JSONArray arguments = new JSONArray();

    PrimitiveCaaS caas = (2 == args.length) ? new PrimitiveCaaS(args[0], Integer.parseInt(args[1])) : new PrimitiveCaaS();
    System.out.println("Connected to CaaS without exception.\n");

    System.out.println("Evaluating `False'...");
    System.out.println(caas.evaluateExpression("False").toString());
    System.out.println("");

    System.out.println("Evaluating `β where β = 7'...");
    System.out.println(caas.evaluateExpression("β where β = 7").toString());
    System.out.println("");

    System.out.println("");
    System.out.println("Evaluating `lg2 2'...");
    arguments = new JSONArray();
    arguments.put(caas.fromHex("2", 2));
    System.out.println(caas.call("lg2", arguments));
    System.out.println("");
    
    System.out.println("");
    caas.loadModule("SuiteB");
    System.out.println("");
    System.out.println("Evaluating `sha224 '...");
    arguments = new JSONArray();
    arguments.put(caas.fromHex("54686520717569636b2062726f776e20666f78206a756d7073206f76657220746865206c617a7920646f672e", 352));
    System.out.println(caas.call("sha224", arguments));
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

    System.out.println("Evaluating `~zero : [2][5]'...");
    System.out.println(caas.evaluateExpression("~zero : [2][5]").toString());
    System.out.println("");

    System.out.println("Evaluating `(2, 3, 5) : ([2], [2], [3])'...");
    System.out.println(caas.evaluateExpression("(2, 3, 5) : ([2], [2], [3])").toString());
    System.out.println("");

    String tenPlusTwo =
    caas.getHex(caas.getAnswer(caas.evaluateExpression("0xa + 0x1")));
    System.out.println("0xa + 0x1 == 0x" + tenPlusTwo + "\n");
    
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
