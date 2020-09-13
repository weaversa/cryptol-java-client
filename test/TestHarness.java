import org.json.*;
import org.CaaS.*;

public class TestHarness {

    //For testing
    public static void main(String[] args) {
	CaaS caas = new CaaS();
	boolean success = caas.Connect(args[0], Integer.parseInt(args[1]));
	System.out.println("Success: " + success + "\n");

	JSONObject hexResult;
	hexResult =
	    caas.EvaluateExpression(caas.FromHex("ab10", 16));
	//or,
	hexResult =
	    caas.EvaluateExpression("0xab10");
	
	caas.LoadModule("Primitive::Symmetric::Cipher::Block::AES");
	JSONObject aesResult =
	    caas.EvaluateExpression("aesEncrypt(10, 11)");
	String ct = caas.GetHex(caas.GetAnswer(aesResult));

	System.out.println("ct = 0x" + ct);

	//Test state reset
	caas.ResetState();

	hexResult =
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
	
	caas.LoadModule("Primitive::Symmetric::Cipher::Block::AES");
	aesResult =
	    caas.EvaluateExpression("aesEncrypt(1, 2)");
	ct = caas.GetHex(caas.GetAnswer(aesResult));

	System.out.println(ct + "\n");

	JSONObject tuple = null;
	tuple = caas.AddToTuple(tuple, "1", 128);
	tuple = caas.AddToTuple(tuple, "2", 128);
	JSONArray aesArguments = null;
	aesArguments = caas.AddArgument(aesArguments, tuple);
	aesResult =
	    caas.EvaluateExpression(caas.Call("aesEncrypt", aesArguments));
	ct = caas.GetHex(caas.GetAnswer(aesResult));
	System.out.println(ct + "\n");
    }
}
