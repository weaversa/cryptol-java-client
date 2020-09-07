import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import org.json.*;

public class CaaS {
    Socket socket;
    PrintStream out;
    BufferedReader in;

    JSONObject state;
    JSONObject id;
    
    //If the connection is successful, return true, otherwise return
    //false
    public boolean Connect(String ip_address, int port) {
	try {
	    socket = new Socket(ip_address, port);
	    out    = new PrintStream(socket.getOutputStream());
	    in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    state  = new JSONObject();
	    state.put("state", JSONObject.NULL);
	    
            id     = new JSONObject();
	    id.put("id", 0);

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

    public boolean Send(JSONObject message) {
	//todo
	message.putOnce("jsonrpc", "2.0");

	JSONObject params = message.optJSONObject("params");
	
	params.put("state", state.opt("state"));
	message.put("id", id.optInt("id"));

	String netstring = message.toString();
	int netstring_size = netstring.length();

        netstring = Integer.toString(netstring_size) + ":" + netstring + ",";

	System.out.println("Sending: " + netstring);
	
	out.print(netstring);

	return true;
    }

    public JSONObject Receive() {
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
	
	//Need to check and change (increment?) ID.
	//Pull out new state and save

	JSONObject r = resultJSON.optJSONObject("result");
	state.put("state", r.optString("state"));
	
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

    public JSONObject fromHex(String hex) {
	JSONObject jhex = new JSONObject();
	jhex.put("expression", "bits");
	jhex.put("encoding", "hex");
	jhex.put("data", hex);
	jhex.put("width", hex.length() * 4);

	return jhex;
    }

    public JSONObject fromHexArray(String hex[]) {
	JSONObject jseq = new JSONObject();
	jseq.put("expression", "sequence");

	JSONArray data = new JSONArray();

	for (int i = 0; i < hex.length; i++) {
	    data.put(fromHex(hex[i]));
	}

	jseq.put("data", data);
	
	return jseq;
    }
    
    //For testing
    public static void main(String[] args) {
	CaaS caas = new CaaS();
	boolean success = caas.Connect(args[0], Integer.parseInt(args[1]));
	System.out.println("Success: " + success);

	caas.LoadModule("Primitive::Symmetric::Cipher::Block::AES");

	String hex = "ab10";
	JSONObject jhex = caas.fromHex(hex);
	JSONObject hexResult = caas.EvaluateExpression(jhex);
	System.out.println("Receiving: " + hexResult.toString());
    }
}
