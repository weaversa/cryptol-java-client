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

	System.out.println("Sending " + netstring);
	
	out.print(netstring);

	return true;
    }

    public String Receive() {
	String result = "";
	try{
	    int c, i;
	    String length = "";
	    while((c=in.read())!=':'){
		length = length + ((char) c);
	    }
	    System.out.print(length + "\n");
	    for(i = 0; i < Integer.parseInt(length); i++) {
		c = in.read();
		result = result + ((char) c);
	    }
	    c = in.read();
	    if(c != ',') {
		//Something went wrong
	    }

	} catch (Exception e) {
	    e.printStackTrace();	    
	}

	return result;
    }
    
    public void LoadModule(String module_name) {
	JSONObject message = new JSONObject();

	message.put("method", "load module");

	JSONObject moduleName = new JSONObject();
	moduleName.put("module name", module_name);

	message.put("params", moduleName);

	//reset state?

	Send(message);
    }
    
    //For testing
    public static void main(String[] args) {
	CaaS caas = new CaaS();
	boolean success = caas.Connect(args[0], Integer.parseInt(args[1]));
	System.out.println("Success: " + success);

	caas.LoadModule("Primitive::Symmetric::Cipher::Block::AES");

	System.out.println(caas.Receive());
	
    }
}
