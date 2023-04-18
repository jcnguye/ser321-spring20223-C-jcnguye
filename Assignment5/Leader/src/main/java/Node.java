import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * SERVER
 * This is the ServerThread class that has a socket where we accept clients contacting us.
 * We save the clients ports connecting to the server into a List in this class.
 * When we wand to send a message we send it to all the listening ports
 */

public class Node {
    ServerSocket server;
    int money;
    Socket leaderSocket; //socket that node is trying to communicate to
    Socket socket;

    //hashmap client
    private HashMap<String, Integer> clientAccount = new HashMap<>();

    public Node(String portNum, int money) throws IOException {
        server = new ServerSocket(Integer.parseInt(portNum));
        this.money = money;
    }

    private static JSONObject responseName(String name) {
        JSONObject obj = new JSONObject();
        obj.put("type", name);
        return obj;
    }


    public void run() {
        try {
//			connectionEstablish();
            Socket sock = server.accept();

            while (true) {
                OutputStream out = sock.getOutputStream();
                InputStream in = sock.getInputStream();
                System.out.println("Waiting for request");
                byte[] messageBytes = NetworkUtils.Receive(in);
                JSONObject request = JsonUtils.fromByteArray(messageBytes);
                JSONObject response = null;
                System.out.println("-----Connection made-----");

                if (request.has("type")) {
                    String choice = request.getString("type");
					System.out.println(request.getString("type"));
                    switch (choice) {
                        case "Name":

                            clientAccount.put(request.getString("Name"), 0);
                            response = new JSONObject();
                            response.put("Message", "Client can borrow");
//							System.out.println(request.toString());
//							response = responseName(request.getString("Name"));
                            break;
                        case "Credit":
                            System.out.println(request.toString());
                            money = money - request.getInt("Money");
                            int val = clientAccount.get(request.getString("Name"));
                            if(val > 0){
                                val = val + request.getInt("Money");
                                clientAccount.put(request.getString("Name"),val);
                            }else{
                                clientAccount.put(request.getString("Name"),request.getInt("Money"));
                            }

                            response = new JSONObject();
                            response.put("Message", request.getString("Name") + " has borrowed "+ clientAccount.get(request.getString("Name")) + "from bank");
                            System.out.println("RECIVE CREDIT");

                            break;
                        default:
                            System.out.println("No valid request option");
                            break;
                    }
                }


                if (response != null) {
                    NetworkUtils.Send(out, JsonUtils.toByteArray(response));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ServerSocket getServer() {
        return server;
    }

    public void ConnectionCement() {

    }

    public static void main(String[] args) throws Exception {
        int money = Integer.parseInt(args[1]);
        String port = args[0]; //bank port

        Node node = new Node(port, money);

        node.run();

    }


}
