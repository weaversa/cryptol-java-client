import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import org.json.*;

public class CaaS {
    private Socket socket;
    private PrintStream out;
    private BufferedReader in;

    private String state;
    private int id;
    
    //If the connection is successful, return true, otherwise return
    //false
    public boolean Connect(String ip_address, int port) {
	try {
	    socket = new Socket(ip_address, port);
	    out    = new PrintStream(socket.getOutputStream());
	    in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    state  = null;
            id     = 0;

	    JSONObject result = LoadModule("Cryptol");
	    
	    return true;
	}
	catch (Exception e) {
	    e.printStackTrace();
	    return false;
	}
    }

    public void Disconnect() {
	//todo
    }

    public void ResetState() {
	state = null;
	
	//Load the prelude (we could instead save the prelude
	//state directly after loading the prelude in `Connect` and
	//assign that value to `state` here.
	JSONObject result = LoadModule("Cryptol");
    }
    
    private boolean Send(JSONObject message) {
	//todo
	message.putOnce("jsonrpc", "2.0");

	JSONObject params = message.optJSONObject("params");

	if(state == null) {
	    params.put("state", JSONObject.NULL);
	} else {
	    params.put("state", state);
	}
	
	message.put("id", id);

	String netstring = message.toString();
	int netstring_size = netstring.length();

        netstring = Integer.toString(netstring_size) + ":" + netstring + ",";

	System.out.println("Sending: " + message.toString());
	
	out.print(netstring);

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

	System.out.println("Receiving: " + resultJSON.toString());
	
	//Need to check and change id
	if(id != resultJSON.optInt("id")) {
	    //We got the wrong response back
	    //This is bad.
	    return null;
	}

	id++;

	//Pull out new state and save
	JSONObject r = resultJSON.optJSONObject("result");
	state = r.optString("state");
	
	return resultJSON;
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

    public JSONObject EvaluateExpression(String expression) {
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

    public JSONObject FromHex(String hex) {
	JSONObject jhex = new JSONObject();
	jhex.put("expression", "bits");
	jhex.put("encoding", "hex");
	jhex.put("data", hex);
	jhex.put("width", hex.length() * 4);

	return jhex;
    }

    public JSONObject FromHexArray(String hex[]) {
	JSONObject jseq = new JSONObject();
	jseq.put("expression", "sequence");

	JSONArray data = new JSONArray();

	for (int i = 0; i < hex.length; i++) {
	    data.put(FromHex(hex[i]));
	}

	jseq.put("data", data);
	
	return jseq;
    }

    //For testing
    public static void main(String[] args) {
	CaaS caas = new CaaS();
	boolean success = caas.Connect(args[0], Integer.parseInt(args[1]));
	System.out.println("Success: " + success);

	JSONObject hexResult;
	hexResult =
	    caas.EvaluateExpression(caas.FromHex("ab10"));
	//or,
	hexResult =
	    caas.EvaluateExpression("0xab10");
	
	caas.LoadModule("Primitive::Symmetric::Cipher::Block::AES");
	JSONObject aesResult =
	    caas.EvaluateExpression("aesEncrypt(10, 11)");
    }
}
