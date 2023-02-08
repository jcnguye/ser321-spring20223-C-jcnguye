/*
Simple Web Server in Java which allows you to call 
localhost:9000/ and show you the root.html webpage from the www/root.html folder
You can also do some other simple GET requests:
1) /random shows you a random picture (well random from the set defined)
2) json shows you the response as JSON for /random instead the html page
3) /file/filename shows you the raw file (not as HTML)
4) /multiply?num1=3&num2=4 multiplies the two inputs and responses with the result
5) /github?query=users/amehlhase316/repos (or other GitHub repo owners) will lead to receiving
   JSON which will for now only be printed in the console. See the todo below

The reading of the request is done "manually", meaning no library that helps making things a 
little easier is used. This is done so you see exactly how to pars the request and 
write a response back
*/

package funHttpServer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Map;
import java.util.LinkedHashMap;
import java.nio.charset.Charset;

class WebServer {
  public static void main(String args[]) {
    WebServer server = new WebServer(9000);
  }

  /**
   * Main thread
   * @param port to listen on
   */
  public WebServer(int port) {
    ServerSocket server = null;
    Socket sock = null;
    InputStream in = null;
    OutputStream out = null;

    try {
      server = new ServerSocket(port);
      while (true) {
        sock = server.accept();
        out = sock.getOutputStream();
        in = sock.getInputStream();
        byte[] response = createResponse(in);
        out.write(response);
        out.flush();
        in.close();
        out.close();
        sock.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (sock != null) {
        try {
          server.close();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Used in the "/random" endpoint
   */
  private final static HashMap<String, String> _images = new HashMap<>() {
    {
      put("streets", "https://iili.io/JV1pSV.jpg");
      put("bread", "https://iili.io/Jj9MWG.jpg");
    }
  };

  private Random random = new Random();

  /**
   * Reads in socket stream and generates a response
   * @param inStream HTTP input stream from socket
   * @return the byte encoded HTTP response
   */
  public byte[] createResponse(InputStream inStream) {

    byte[] response = null;
    BufferedReader in = null;

    try {

      // Read from socket's input stream. Must use an
      // InputStreamReader to bridge from streams to a reader
      in = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));

      // Get header and save the request from the GET line:
      // example GET format: GET /index.html HTTP/1.1

      String request = null;

      boolean done = false;
      while (!done) {
        String line = in.readLine();

        System.out.println("Received: " + line);

        // find end of header("\n\n")
        if (line == null || line.equals(""))
          done = true;
        // parse GET format ("GET <path> HTTP/1.1")
        else if (line.startsWith("GET")) {
          int firstSpace = line.indexOf(" ");
          int secondSpace = line.indexOf(" ", firstSpace + 1);

          // extract the request, basically everything after the GET up to HTTP/1.1
          request = line.substring(firstSpace + 2, secondSpace);
        }

      }
      System.out.println("FINISHED PARSING HEADER\n");

      // Generate an appropriate response to the user
      if (request == null) {
        response = "<html>Illegal request: no GET</html>".getBytes();
      } else {
        // create output buffer
        StringBuilder builder = new StringBuilder();
        // NOTE: output from buffer is at the end

        if (request.length() == 0) {
          // shows the default directory page

          // opens the root.html file
          String page = new String(readFileInBytes(new File("www/root.html")));
          // performs a template replacement in the page
          page = page.replace("${links}", buildFileList());

          // Generate response
          builder.append("HTTP/1.1 200 OK\n");
          builder.append("Content-Type: text/html; charset=utf-8\n");
          builder.append("\n");
          builder.append(page);

        } else if (request.equalsIgnoreCase("json")) {
          // shows the JSON of a random image and sets the header name for that image

          // pick a index from the map
          int index = random.nextInt(_images.size());

          // pull out the information
          String header = (String) _images.keySet().toArray()[index];
          String url = _images.get(header);

          // Generate response
          builder.append("HTTP/1.1 200 OK\n");
          builder.append("Content-Type: application/json; charset=utf-8\n");
          builder.append("\n");
          builder.append("{");
          builder.append("\"header\":\"").append(header).append("\",");
          builder.append("\"image\":\"").append(url).append("\"");
          builder.append("}");

        } else if (request.equalsIgnoreCase("random")) {
          // opens the random image page

          // open the index.html
          File file = new File("www/index.html");

          // Generate response
          builder.append("HTTP/1.1 200 OK\n");
          builder.append("Content-Type: text/html; charset=utf-8\n");
          builder.append("\n");
          builder.append(new String(readFileInBytes(file)));

        } else if (request.contains("file/")) {
          // tries to find the specified file and shows it or shows an error

          // take the path and clean it. try to open the file
          File file = new File(request.replace("file/", ""));

          // Generate response
          if (file.exists()) { // success
            builder.append("HTTP/1.1 200 OK\n");
            builder.append("Content-Type: text/html; charset=utf-8\n");
            builder.append("\n");
            builder.append(new String(readFileInBytes(file)));
          } else { // failure
            builder.append("HTTP/1.1 404 Not Found\n");
            builder.append("Content-Type: text/html; charset=utf-8\n");
            builder.append("\n");
            builder.append("File not found: " + file);

          }
        } else if (request.contains("multiply?")) {
          // This multiplies two numbers, there is NO error handling, so when
          // wrong data is given this just crashes
          // TODO: Include error handling here with a correct error code and
          try {
          Map<String, String> query_pairs = new LinkedHashMap<String, String>();
          // extract path parameters

          query_pairs = splitQuery(request.replace("multiply?", ""));
            if (query_pairs.size() != 2) {
              builder.append("HTTP/1.1 406 Not Acceptable\n");
              builder.append("Content-Type: text/html; charset=utf-8\n");
              builder.append("\n");
              builder.append("missing querys");
            } else {
              String req2 = request.substring(9);
              String[] strArr = req2.split("&");
              String nu1 = strArr[0];
              String[] st1 = nu1.split("=");
              String nu2 = strArr[1];
              String[] st2 = nu2.split("=");
              if ((st1[0].equals("num1") && st2[0].equals("num2")) && (isNumeric(st1[1]) && isNumeric(st2[1]))) {
                Integer num1 = Integer.parseInt(query_pairs.get("num1"));
                Integer num2 = Integer.parseInt(query_pairs.get("num2"));
                // do math
                Integer result = num1 * num2;
                builder.append("HTTP/1.1 200 OK\n");
                builder.append("Content-Type: text/html; charset=utf-8\n");
                builder.append("\n");
                builder.append("Result is: " + result);

              }else{
                builder.append("HTTP/1.1 406 Not Acceptable\n");
                builder.append("Content-Type: text/html; charset=utf-8\n");
                builder.append("\n");
                builder.append("Wrong parameters, querys, or wrong values");
              }
            }
          }catch (NumberFormatException |  StringIndexOutOfBoundsException e){
              builder.append("HTTP/1.1 406 Not Acceptable\n");
              builder.append("Content-Type: text/html; charset=utf-8\n");
              builder.append("\n");
              builder.append("No querys added");
          }


        } else if (request.contains("github?")) {
          // pulls the query from the request and runs it with GitHub's REST API
          // check out https://docs.github.com/rest/reference/
          //
          // HINT: REST is organized by nesting topics. Figure out the biggest one first,
          //     then drill down to what you care about
          // "Owner's repo is named RepoName. Example: find RepoName's contributors" translates to
          //     "/repos/OWNERNAME/REPONAME/contributors"

          Map<String, String> query_pairs = new LinkedHashMap<String, String>();
          String s1 = request.replace("github?", "");
          if(s1.isBlank() || s1.isEmpty()){
            builder.append("HTTP/1.1 400 Bad request\n");
            builder.append("HTTP/1.1 406 Not Acceptable\n");
            builder.append("Content-Type: text/html; charset=utf-8\n");
            builder.append("<br>");
            builder.append("No input\n");
          }
          try {
            query_pairs = splitQuery(request.replace("github?", ""));
            String json = fetchURL("https://api.github.com/" + query_pairs.get("query"));
            // TODO: Parse the JSON returned by your fetch and create an appropriate
            // response based on what the assignment document asks for
            JsonParser jsonObj = new JsonParser();
            JsonElement element = jsonObj.parse(json);
            if (element.isJsonArray()) {
              builder.append("HTTP/1.1 200 OK\n");
              builder.append("Content-Type: text/html; charset=utf-8\n");
              builder.append("\n");
              builder.append("Sucess JSON\n");
              builder.append("\n");
              var array = element.getAsJsonArray();
              var object1 = array.get(0);
              var object2 = array.get(1);
              if (object1.isJsonObject()) {
                var obj1 = object1.getAsJsonObject();
                var obj2 = object2.getAsJsonObject();
                if (obj1.has("owner") && obj2.has("owner")) {
                  var owner1 = obj1.getAsJsonObject("owner");
                  var owner2 = obj2.getAsJsonObject("owner");
                  builder.append("<br>[");
                  builder.append(" {<br>fullname: " + obj1.get("full_name") + "<br>");
                  builder.append("  id: " + owner1.get("id") + "<br>");
                  builder.append("  login: " + owner1.get("login"));
                  builder.append("  },<br>");
                  builder.append(" {<br>fullname: " + obj2.get("full_name") + "\n");
                  builder.append("  id: " + owner2.get("id") + "<br>");
                  builder.append("  login: " + owner2.get("login"));
                  builder.append("  }<br>");
                  builder.append("]");
                }
              }
            } else {
              builder.append("HTTP/1.1 400 Bad request\n");
              builder.append("HTTP/1.1 404 Not Found\n");
              builder.append("Content-Type: text/html; charset=utf-8\n");
              builder.append("\n");
              builder.append("Does not lead to JSON\n");
              builder.append("\n");
            }
          }catch (NumberFormatException |  StringIndexOutOfBoundsException e) {
            builder.append("HTTP/1.1 406 Not Acceptable\n");
            builder.append("Content-Type: text/html; charset=utf-8\n");
            builder.append("\n");
            builder.append("Missing path does not lead to json try http://192.168.56.1:80/github?query=users/amehlhase316/repos");
          }
        } else if (request.contains("joke?")) {
          // This multiplies two numbers, there is NO error handling, so when
          // wrong data is given this just crashes
          try {
            Map<String, String> query_pairs = new LinkedHashMap<String, String>();
            // extract path parameters
            query_pairs = splitQuery(request.replace("joke?", ""));
            if (query_pairs.size() != 2) {
              builder.append("HTTP/1.1 406 Not Acceptable\n");
              builder.append("Content-Type: text/html; charset=utf-8\n");
              builder.append("\n");
              builder.append("not valid querys try /joke?name=first&lastname=lastname");
            }else{
              String req2 = request.substring(5);
              String[] strArr = req2.split("&");

              String nu1 = strArr[0];
              String[] st1 = nu1.split("=");
              String nu2 = strArr[1];
              String[] st2 = nu2.split("=");

              if((st1[0].equals("firstname")&& st2[0].equals("lastname")) && (nu1.contains("=") && nu2.contains("="))){
                builder.append("HTTP/1.1 200 ok\n");
                builder.append("Content-Type: text/html; charset=utf-8\n");
                builder.append("\n");
                builder.append("Its in german☺️ : Warum hat " +  st1[1]+ " " + st2[1]+ " die Straße überquert, weil sie einen Landstreicher schlagen wollten");
              }
            }
          }catch (NumberFormatException |  StringIndexOutOfBoundsException e){
            builder.append("HTTP/1.1 406 Not Acceptable\n");
            builder.append("Content-Type: text/html; charset=utf-8\n");
            builder.append("\n");
            builder.append("missing querys try /joke?name=first&lastname=lastname");
          }
        }else if (request.contains("roulette?")) {
          //if user picks two numbers between 1 - 6 if they choose a number generated by random number they die
          //if user chooses any number generated, they die
          try {
            Random rand = new Random();
            Integer randomNum = rand.nextInt((6 - 1) + 1) + 1;
            Map<String, String> query_pairs = new LinkedHashMap<String, String>();
            // extract path parameters

            query_pairs = splitQuery(request.replace("roulette?", ""));
            if (query_pairs.size() != 2) {
              builder.append("HTTP/1.1 406 Not Acceptable\n");
              builder.append("Content-Type: text/html; charset=utf-8\n");
              builder.append("\n");
              builder.append("missing querys");
            }else {
              Integer num1 = Integer.parseInt(query_pairs.get("num1"));
              Integer num2 = Integer.parseInt(query_pairs.get("num2"));
              String req2 = request.substring(9);
              String[] strArr = req2.split("&");
              String nu1 = strArr[0];
              String[] st1 = nu1.split("=");
              String nu2 = strArr[1];
              String[] st2 = nu2.split("=");
              if (!(num1 >= 1 && num1 <= 6) && !(num2 >= 1 && num2 <= 6)) {
                builder.append("HTTP/1.1 200 ok\n");
                builder.append("Content-Type: text/html; charset=utf-8\n");
                builder.append("\n");
                builder.append("Pick number between 1 - 6");
              }else if (randomNum.equals(num1) || randomNum.equals(num2)) {
                builder.append("HTTP/1.1 200 ok\n");
                builder.append("Content-Type: text/html; charset=utf-8\n");
                builder.append("\n");
                builder.append("HA HA you dead");
              } else {
                builder.append("HTTP/1.1 200 ok\n");
                builder.append("Content-Type: text/html; charset=utf-8\n");
                builder.append("\n");
                builder.append("Get you next time");
              }
            }
          }catch (NumberFormatException | StringIndexOutOfBoundsException e){
            builder.append("HTTP/1.1 406 Not Acceptable\n");
            builder.append("HTTP/1.1 400 Bad Request\n");
            builder.append("Content-Type: text/html; charset=utf-8\n");
            builder.append("\n");
            builder.append("query is empty or wrong value given");
          }
        }else {
          // if the request is not recognized at all
          builder.append("HTTP/1.1 400 Bad Request\n");
          builder.append("Content-Type: text/html; charset=utf-8\n");
          builder.append("\n");
          builder.append("I am not sure what you want me to do.......");
        }

        // Output
        //Gets every string on the builder
        response = builder.toString().getBytes();
      }
    } catch (IOException e) {
      e.printStackTrace();
      response = ("<html>ERROR: " + e.getMessage() + "</html>").getBytes();
    }

    return response;
  }

  public boolean isNumeric(String str) {
    return str.matches("-?\\d+(\\.\\d+)?");
  }

  /**
   * Method to read in a query and split it up correctly
   * @param query parameters on path
   * @return Map of all parameters and their specific values
   * @throws UnsupportedEncodingException If the URLs aren't encoded with UTF-8
   */
  public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
    Map<String, String> query_pairs = new LinkedHashMap<String, String>();
    // "q=hello+world%2Fme&bob=5"
    String[] pairs = query.split("&");
    // ["q=hello+world%2Fme", "bob=5"]
    for (String pair : pairs) {
      int idx = pair.indexOf("=");
      //something is happening here
      query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
          URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
    }
    // {{"q", "hello world/me"}, {"bob","5"}}
    return query_pairs;
  }

  /**
   * Builds an HTML file list from the www directory
   * @return HTML string output of file list
   */
  public static String buildFileList() {
    ArrayList<String> filenames = new ArrayList<>();

    // Creating a File object for directory
    File directoryPath = new File("www/");
    filenames.addAll(Arrays.asList(directoryPath.list()));

    if (filenames.size() > 0) {
      StringBuilder builder = new StringBuilder();
      builder.append("<ul>\n");
      for (var filename : filenames) {
        builder.append("<li>" + filename + "</li>");
      }
      builder.append("</ul>\n");
      return builder.toString();
    } else {
      return "No files in directory";
    }
  }

  /**
   * Read bytes from a file and return them in the byte array. We read in blocks
   * of 512 bytes for efficiency.
   */
  public static byte[] readFileInBytes(File f) throws IOException {

    FileInputStream file = new FileInputStream(f);
    ByteArrayOutputStream data = new ByteArrayOutputStream(file.available());

    byte buffer[] = new byte[512];
    int numRead = file.read(buffer);
    while (numRead > 0) {
      data.write(buffer, 0, numRead);
      numRead = file.read(buffer);
    }
    file.close();

    byte[] result = data.toByteArray();
    data.close();

    return result;
  }

  /**
   *
   * a method to make a web request. Note that this method will block execution
   * for up to 20 seconds while the request is being satisfied. Better to use a
   * non-blocking request.
   * 
   * @param aUrl the String indicating the query url for the OMDb api search
   * @return the String result of the http request.
   *
   **/
  public String fetchURL(String aUrl) {
    StringBuilder sb = new StringBuilder();
    URLConnection conn = null;
    InputStreamReader in = null;
    try {
      URL url = new URL(aUrl);
      conn = url.openConnection();
      if (conn != null)
        conn.setReadTimeout(20 * 1000); // timeout in 20 seconds
      if (conn != null && conn.getInputStream() != null) {
        in = new InputStreamReader(conn.getInputStream(), Charset.defaultCharset());
        BufferedReader br = new BufferedReader(in);
        if (br != null) {
          int ch;
          // read the next character until end of reader
          while ((ch = br.read()) != -1) {
            sb.append((char) ch);
          }
          br.close();
        }
      }
      in.close();
    } catch (Exception ex) {
      System.out.println("Exception in url request:" + ex.getMessage());
    }
    return sb.toString();
  }
}
