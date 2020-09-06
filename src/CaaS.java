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
	    state  = null;
            id     = new JSONObject();
	    
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
	
	return true;
    }
    
    //For testing
    public static void main(String[] args) {
	CaaS caas = new CaaS();
	boolean success = caas.Connect(args[0], Integer.parseInt(args[1]));
	System.out.println("Success: " + success);
    }
}
