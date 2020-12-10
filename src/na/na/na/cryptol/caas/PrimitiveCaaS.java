package na.na.na.cryptol.caas;

import com.pobox.djb.Netstring;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PrimitiveCaaS {
  private String hostOrIP;
  private int port;
  private Socket socket;
  private BufferedInputStream in;
  private BufferedOutputStream out;
  private Stack<String> states = new Stack<String>(); // states is never null
  private int id;
  private String latestModule;
  private Random random = new Random();
  
  public PrimitiveCaaS() throws CaaSException {
    this(defaultHostOrIP(), defaultPort().intValue());
  }
  
  public PrimitiveCaaS(String hostOrIP, int port) throws CaaSException {
    this.hostOrIP = hostOrIP;
    this.port = port;
    reconnect();
  }
  
  public static String defaultHostOrIP() {
    try {
      return System.getProperty("na.na.na.cryptol.caas.hostOrIP");
    } catch (Throwable t) {
      return null;
    }
  }
  
  public static Integer defaultPort() {
    try {
      return Integer.valueOf(System.getProperty("na.na.na.cryptol.caas.port"));
    } catch (Throwable t) {
      return null;
    }
  }
  
  public String getHostOrIP() {
    return hostOrIP;
  }

  public int getPort() {
    return port;
  }
  
  private void reconnect() throws CaaSException {
    try {
      socket = new Socket(hostOrIP, port);
      in = new BufferedInputStream(socket.getInputStream());
      out = new BufferedOutputStream(socket.getOutputStream());
      resetState();
      loadModule("Cryptol");
    } catch (CaaSException e) {
      throw e;
    } catch (Throwable t) {
      throw new CaaSException ("Troublesome connection to CaaS, hosrOrIP: `" + hostOrIP + "', port: `" + port + "'.", t);
    }
  }
  
  private void disconnect() {
    in = null;
    out = null;
    socket = null;
  }
  
  private void resetState() {
    states.clear();
    states.push(null);
  }
  
  // do we want non-blocking IO?
  
  private JSONObject transceive(JSONObject cryptolInput) throws CaaSException {
    try { // send
      cryptolInput.putOnce("jsonrpc", "2.0");
      if (!cryptolInput.has("params")) {
        cryptolInput.putOnce("params", new JSONObject());
      }
      JSONObject params = cryptolInput.getJSONObject("params");
      if (states.empty() || null == states.peek()) {
        params.put("state", JSONObject.NULL);
      } else {
        params.put("state", states.peek());
      }
      id = random.nextInt();
      cryptolInput.put("id", id);
      byte[] netstring = Netstring.render(cryptolInput.toString(), "UTF-8");
      out.write(netstring, 0, netstring.length);
      out.flush();
    } catch (Throwable t) {
      throw new CaaSException("Trouble sending to CaaS.", t);
    }
    try {
      JSONObject cryptolOutput = new JSONObject(Netstring.parse(in, "UTF-8"));
      if (cryptolOutput.getInt("id") != id) {
        throw new CaaSException("Incorrect id in response from CaaS.");
      }
      if (cryptolOutput.has("result")) {
        JSONObject result = cryptolOutput.getJSONObject("result");
        String newState = result.getString("state");
        if (states.empty() || states.peek() != newState) {
          states.push(newState);
        }
        return cryptolOutput;
      }
      if (cryptolOutput.has("error")) {
        JSONObject error = cryptolOutput.getJSONObject("error");
        int code = error.getInt("code");
        String message = error.getString("message");
        System.err.println();
        System.err.println("*****************************");
        System.err.println("Cryptol returned error code " + code + " and the following error message:");
        System.err.println(message);
        System.err.println("*****************************");
        System.err.println();
        throw new CaaSException("Cryptol error " + code + ": " + message);
      }
      System.err.println();
      System.err.println("*****************************");
      System.err.println("Cryptol returned neither a result nor an error. JSON follows:");
      System.err.println(cryptolOutput);
      System.err.println("*****************************");
      System.err.println();
      throw new CaaSException("Cryptol returned neither a result nor an error. JSON: " + cryptolOutput);
    } catch (CaaSException e) {
      throw e;
    } catch (Throwable t) {
      throw new CaaSException("Trouble receiving from CaaS.", t);
    }
  }
  
  public JSONObject evaluateExpression(JSONObject expression) throws CaaSException {
    JSONObject message = new JSONObject();
    JSONObject params = new JSONObject();
    message.put("params", params);
    message.put("method", "evaluate expression");
    params.put("expression", expression);
    return transceive(message);
  }
  
  public JSONObject evaluateExpression(String command) throws CaaSException {
    JSONObject message = new JSONObject();
    JSONObject params = new JSONObject();
    message.put("params", params);
    message.put("method", "evaluate expression");
    params.put("expression", command);
    return transceive(message);
  }
  
  public JSONObject getAnswer(JSONObject cryptolOut) throws CaaSException {
    JSONObject result, answer, value;
    try {
      result = cryptolOut.getJSONObject("result");
    } catch (JSONException e) {
      throw new CaaSException("Problem with `result' tag", e);
    }
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
      } catch (CaaSException e) {
        throw e;
      } catch (Throwable t) {
        throw new CaaSException("Trouble loading module `" + moduleName + "'.", t);
      }
    }
  }
  
  public JSONObject loadModuleJSON(String moduleName) throws CaaSException {
    JSONObject message = new JSONObject();
    message.put("method", "load module");
    JSONObject module = new JSONObject();
    module.put("module name", moduleName);
    message.put("params", module);
    JSONObject cryptolOut = transceive(message);
    latestModule = moduleName;
    return cryptolOut;
  }
  
  public JSONObject call(String functionName, JSONArray arguments) throws CaaSException {
    JSONObject params = new JSONObject();
    params.put("function", functionName);
    params.put("arguments", arguments);
    JSONObject message = new JSONObject();
    message.put("method", "call");
    message.put("params", params);
    return transceive(message);
  }
  
    
  public static JSONObject fromHex(String hex, int numBits) {
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
