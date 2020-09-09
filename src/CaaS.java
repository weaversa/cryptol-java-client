import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Stack;
import org.json.*;

public class CaaS {
    private Socket socket;
    private PrintStream out;
    private BufferedReader in;
    private boolean connected;
    
    Stack<String> states;
    private int id;

    CaaS () {
	connected = false;
	states = new Stack<String>();
    }
    
    //If the connection is successful, return true, otherwise return
    //false
    public boolean Connect(String ip_address, int port) {
	try {
	    socket = new Socket(ip_address, port);
	    out    = new PrintStream(socket.getOutputStream());
	    in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));

	    connected = true;
	    
   	    //Empty the states stack
	    while(!states.empty()) {
		states.pop();
	    }
	    states.push(null);
	    id     = 0;

    	    JSONObject result = LoadModule("Cryptol");
	}
	catch (Exception e) {
	    e.printStackTrace();
	    connected = false;
	}

	return connected;
    }

    public void Disconnect() {
	connected = false;

	//todo
    }

    public void ResetState() {
	while(states.size() > 2) {
	    states.pop();
	}

	//This will either do nothing, or pop until the state is
	//the one directly after loading the prelude in `Connect`.
    }
    
    private boolean Send(JSONObject message) {
	if(!connected) return false;
	
	message.putOnce("jsonrpc", "2.0");

	JSONObject params = message.getJSONObject("params");

	if(states.peek() == null) {
	    params.put("state", JSONObject.NULL);
	} else {
	    params.put("state", states.peek());
	}
	
	message.put("id", id);

	String netstring = message.toString();
	int netstring_size = netstring.length();

        netstring = Integer.toString(netstring_size) + ":" + netstring + ",";

	System.out.println("Sending: " + message.toString());

	try {
	    out.print(netstring);
	} catch (Exception e) {
	    System.out.println("Send failed: " + e);
	    return false;
	}

	return true;
    }

    private JSONObject Receive() {
	String result = "";
	try{
	    int c, i;
	    String length = "";
	    while((c=in.read())!=':' && c != -1){
		length = length + ((char) c);
	    }
	    if(c == -1) return new JSONObject("");
	    for(i = 0; i < Integer.parseInt(length); i++) {
		c = in.read();
		result = result + ((char) c);
	    }
	    if(c == -1) return new JSONObject("");
	    c = in.read();
	    if(c != ',') {
		//Something went wrong
		return new JSONObject("");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    return new JSONObject("");	   
	}

	JSONObject resultJSON = new JSONObject(result);

	//Need to check and change id
	if(id != resultJSON.getInt("id")) {
	    //We got the wrong response back
	    //This is bad.
	    return null;
	}

	id++;

	//Pull out new state and save
	JSONObject r = resultJSON.getJSONObject("result");
	states.push(r.getString("state"));

	System.out.println("Result: " + r.toString());
	
	return r;
    }
    
    public JSONObject EvaluateExpression(JSONObject expression) {
	JSONObject message = new JSONObject();
	JSONObject params = new JSONObject();
	message.put("params", params);
	message.put("method", "evaluate expression");
	params.put("expression", expression);

	if(Send(message) == false) {
	    //Some problem happened
	    return null;
	}

	JSONObject result = Receive();

	return result;
    }

    public JSONObject EvaluateExpression(String command) {
	JSONObject message = new JSONObject();
	JSONObject params = new JSONObject();
	message.put("params", params);
	message.put("method", "evaluate expression");
	params.put("expression", command);

	if(Send(message) == false) {
	    //Some problem happened
	    return null;
	}

	JSONObject result = Receive();

	return result;
    }
    
    public JSONObject LoadModule(String module_name) {
	JSONObject message = new JSONObject();

	message.put("method", "load module");

	JSONObject moduleName = new JSONObject();
	moduleName.put("module name", module_name);

	message.put("params", moduleName);

	//reset state?

	Send(message);

	JSONObject result = Receive();

	return result;
    }

    public JSONObject FromHex(String hex, int numBits) {
	JSONObject jhex = new JSONObject();
	jhex.put("expression", "bits");
	jhex.put("encoding", "hex");
	jhex.put("data", hex);
	jhex.put("width", numBits);

	return jhex;
    }

    public JSONObject FromHexArray(String hex[], int numBits) {
	JSONObject jseq = new JSONObject();
	jseq.put("expression", "sequence");

	JSONArray data = new JSONArray();

	for (int i = 0; i < hex.length; i++) {
	    data.put(FromHex(hex[i], numBits));
	}

	jseq.put("data", data);
	
	return jseq;
    }

    /**
     * Create a hex string from a json expression.
     * Sample input ---
     *   { "data": "1" , "width": 8, "expression": "bits", "encoding": "hex" }
     */

    //Does not return length, yet.
    public String GetHex(JSONObject bv) {
	String expression, encoding, data;
	int width;

	//Check for correct expression tag
	try {
	    expression = bv.getString("expression");
	} catch (JSONException e) {
	    System.out.println("Problem with 'expression' tag: " + e.toString());
	    return null;
	}
	if(!expression.equals("bits")) {
	    System.out.println("'expression' tag not \"bits\"");
	}

	//Check for correct encoding tag
	try {
	    encoding = bv.getString("encoding");
	} catch (JSONException e) {
	    System.out.println("Problem with 'encoding' tag: " + e.toString());
	    return null;
	}
	if(!encoding.equals("hex")) {
	    System.out.println("'encoding' tag not \"hex\"");
	}

	//Get data
	try {
	    data = bv.getString("data");
	} catch (JSONException e) {
	    System.out.println("Problem with 'data' tag: " + e.toString());
	    return null;
	}

	//Get width
	try {
	    width = bv.getInt("width");
	} catch (JSONException e) {
	    System.out.println("Problem with 'width' tag: " + e.toString());
	    return null;
	}

	return data;	
    }

    public String[] GetHexArray(JSONObject bv) {
	String expression;
	JSONArray data;
	int width;

	//Check for correct expression tag
	try {
	    expression = bv.getString("expression");
	} catch (JSONException e) {
	    System.out.println("Problem with 'expression' tag: " + e.toString());
	    return null;
	}
	if(!expression.equals("sequence")) {
	    System.out.println("'expression' tag not \"sequence\"");
	}    

	//Get data
	try {
	    data = bv.getJSONArray("data");
	} catch (JSONException e) {
	    System.out.println("Problem with 'data' tag: " + e.toString());
	    return null;
	}

	String[] ret = new String[data.length()];

	for(int i = 0; i < data.length(); i++) {
	    ret[i] = GetHex(data.getJSONObject(i));
	}

	return ret;	
    }
	
    //For testing
    public static void main(String[] args) {
	CaaS caas = new CaaS();
	boolean success = caas.Connect(args[0], Integer.parseInt(args[1]));
	System.out.println("Success: " + success);

	JSONObject hexResult;
	hexResult =
	    caas.EvaluateExpression(caas.FromHex("ab10", 16));
	//or,
	hexResult =
	    caas.EvaluateExpression("0xab10");
	
	caas.LoadModule("Primitive::Symmetric::Cipher::Block::AES");
	JSONObject aesResult =
	    caas.EvaluateExpression("aesEncrypt(10, 11)");
	String ct = caas.GetHex(aesResult.getJSONObject("answer").getJSONObject("value"));

	System.out.println("ct = 0x" + ct);

	//Test state reset
	caas.ResetState();

	hexResult =
	    caas.EvaluateExpression("0xab10");

	String[] hexArrayResult =
	    caas.GetHexArray(
	        caas.EvaluateExpression(
                    caas.FromHexArray(new String[]{"ab", "bc", "de"}, 8)).getJSONObject("answer").getJSONObject("value"));

	for(int i = 0; i < hexArrayResult.length; i++) {
	    System.out.print(hexArrayResult[i] + " ");
	}
	System.out.println();
	
	caas.LoadModule("Primitive::Symmetric::Cipher::Block::AES");
	aesResult =
	    caas.EvaluateExpression("aesEncrypt(10, 11)");
	ct = caas.GetHex(aesResult.getJSONObject("answer").getJSONObject("value"));
	
    }
}
