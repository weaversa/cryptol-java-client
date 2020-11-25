package na.na.na.cryptol.caas;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;
import org.json.*;

public class PrimitiveCaaS {
  private String hostOrIP;
  private InetAddress inetAddress;
  private int port;
  private Socket socket;
  private PrintStream out;
  private BufferedReader in;
  private InputStreamReader in_;
  private Stack<String> states = new Stack<String>(); // states in never null
  private int id;
  private boolean connected;
  private String latestModule;
  private Random random = new Random();
  
  public PrimitiveCaaS(String hostOrIP, int port) throws CaaSException {
    try {
      this.hostOrIP = hostOrIP;
      this.inetAddress = InetAddress.getByName(hostOrIP);
      this.port = port;
    } catch (Exception e) {
      throw new CaaSException("Trouble converting `" + hostOrIP + "' InetAddress.", e);
    }
    reconnect();
  }
  
  public PrimitiveCaaS(InetAddress inetAddress, int port) throws CaaSException {
    this.inetAddress = inetAddress;
    this.port = port;
    reconnect();
  }
  
  private void reconnect() throws CaaSException {
    try {
      socket    = new Socket(inetAddress, port);
      out       = new PrintStream(socket.getOutputStream());
      in_       = new InputStreamReader(socket.getInputStream());
      in        = new BufferedReader(in_);
    }
    catch (Exception e) {
      throw new CaaSException ("Troublesome connection to CaaS.", e);
    }
    connected = true;
    id        = random.nextInt();
    resetState();
    loadModule("Cryptol");
  }
  
  private void disconnect() {
    connected = false;
    in     = null;
    in_    = null;
    out    = null;
    socket = null;
  }
  
  private void resetState() {
    states.clear();
    states.push(null);
  }
  
  private void send(JSONObject message) throws CaaSException {
    message.putOnce("jsonrpc", "2.0");
    if (!message.has("params")) {
      message.putOnce("params", new JSONObject());
    }
    JSONObject params = message.getJSONObject("params");
    if (states.empty() || null == states.peek()) {
      params.put("state", JSONObject.NULL);
    } else {
      params.put("state", states.peek());
    }
    message.put("id", id);
    String netstring = message.toString();
    netstring = Integer.toString(netstring.length()) + ":" + netstring + ",";
    // System.out.println("Sending: " + message.toString());
    try {
      out.print(netstring);
    } catch (Exception e) {
      throw new CaaSException("Trouble sending to CaaS.", e);
    }
  }
  
  private JSONObject receive() throws CaaSException {
    JSONObject jsonObject = null;
    try {
      int c;
      StringBuilder length_ = new StringBuilder();
      while (true) {
        c = in.read();
        if (-1 == c) {
          throw new CaaSException("Ill-formed netstring.");
        }
        if (':' == c) {
          break;
        }
        length_.append((char) c);
      }
      int length = Integer.parseInt(length_.toString());
      StringBuilder octets = new StringBuilder(length);
      for (int i = 0; i < length; i++) {
        c = in.read(); // TODO: needs to by octet based rather than char based.
        if (-1 == c) {
          throw new CaaSException("Ill-formed netstring.");
        }
        octets.append((char) c);
      }
      c = in.read();
      if (',' != c) {
        throw new CaaSException("Ill-formed netstring.");
      }
      // do utf8?
      jsonObject = new JSONObject(octets.toString());
      if (id != jsonObject.getInt("id")) {
        throw new CaaSException("Incorrect id in response from CaaS.");
      }
      id = random.nextInt();
      JSONObject result = jsonObject.getJSONObject("result");
      String newState = result.getString("state");
      if (states.empty() || states.peek() != newState) {
        states.push(newState);
      }
      return result;
    } catch (CaaSException e) {
      throw e;
    } catch (Exception e) {
      throw new CaaSException("Trouble receiving from CaaS." + ((null == jsonObject) ? "" : ("JSON: " +  jsonObject.toString())), e);
    }
  }
  
  public JSONObject evaluateExpression(JSONObject expression) throws CaaSException {
    JSONObject message = new JSONObject();
    JSONObject params = new JSONObject();
    message.put("params", params);
    message.put("method", "evaluate expression");
    params.put("expression", expression);
    send(message);
    return receive();
  }
  
  public JSONObject evaluateExpression(String command) throws CaaSException {
    JSONObject message = new JSONObject();
    JSONObject params = new JSONObject();
    message.put("params", params);
    message.put("method", "evaluate expression");
    params.put("expression", command);
    send(message);
    return receive();
  }
  
  public JSONObject getAnswer(JSONObject result) throws CaaSException {
    JSONObject answer, value;
    
    try {
      answer = result.getJSONObject("answer");
    } catch (JSONException e) {
      throw new CaaSException("Problem with `answer' tag", e);
    }
    
    try {
      value = answer.getJSONObject("value");
    } catch (JSONException e) {
      throw new CaaSException("Problem with `value' tag", e);
    }
    
    return value;
  }
  
  public void loadModule(String moduleName) throws CaaSException {
    if (latestModule != moduleName) {
      try {
        JSONObject result = loadModuleJSON(moduleName);
      } catch (Exception e) {
        throw new CaaSException("Could not load module `" + moduleName + "'", e);
      }
    }
  }
  
  public JSONObject loadModuleJSON(String moduleName) throws CaaSException {
    JSONObject message = new JSONObject();
    message.put("method", "load module");
    JSONObject module = new JSONObject();
    module.put("module name", moduleName);
    message.put("params", module);
    send(message);
    JSONObject result = receive();
    latestModule = moduleName;
    return result;
  }
  
  public JSONObject call(String functionName, JSONArray arguments) throws CaaSException {
    JSONObject params = new JSONObject();
    params.put("function", functionName);
    params.put("arguments", arguments);
    JSONObject message = new JSONObject();
    message.put("method", "call");
    message.put("params", params);
    send(message);
    return receive();
  }
  
  public JSONArray addArgument(JSONArray arguments, JSONObject argument) {
    if (null == arguments) {
      arguments = new JSONArray();
    }
    arguments.put(argument);
    return arguments;
  }
  
  public JSONArray addArgument(JSONArray arguments, String argument, int numBits) {
    return addArgument(arguments, fromHex(argument, numBits));
  }
  
  public JSONObject addToTuple(JSONObject tuple, JSONObject value) throws CaaSException {
    JSONArray data;
    if (null == tuple) {
      tuple = new JSONObject();
      tuple.put("expression", "tuple");
      data = new JSONArray();
      tuple.put("data", data);
    } else {
      try {
        data = tuple.getJSONArray("data");
      } catch (JSONException e) {
        throw new CaaSException("Problem with `data' tag", e);
      }
    }
    data.put(value);
    return tuple;
  }
  
  public JSONObject addToTuple(JSONObject tuple, String value, int numBits) throws CaaSException {
    return addToTuple(tuple, fromHex(value, numBits));
  }
  
  public static JSONObject fromHex(String hex, int numBits) {
    // System.out.println("*** " + hex + " " + numBits);
    JSONObject jhex = new JSONObject();
    jhex.put("expression", "bits");
    jhex.put("encoding", "hex");
    jhex.put("data", hex);
    jhex.put("width", numBits);
    return jhex;
  }
  
  public static JSONObject fromHex(String hex) {
    return fromHex(hex, 4 * hex.length());
  }
  
  public static JSONObject fromHexArray(String hex[], int numBits) {
    JSONObject jseq = new JSONObject();
    jseq.put("expression", "sequence");
    JSONArray data = new JSONArray();
    for (int i = 0; i < hex.length; i++) {
      data.put(fromHex(hex[i], numBits));
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
  public String getHex(JSONObject bv) {
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
  
  public String[] getHexArray(JSONObject bv) {
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
    if (!expression.equals("sequence")) {
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
    for (int i = 0; i < data.length(); i++) {
      ret[i] = getHex(data.getJSONObject(i));
    }
    return ret;
  }
  
}
