import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This is the main class for the peer2peer program.
 * It starts a client with a username and port. Next the peer can decide who to listen to.
 * So this peer2peer application is basically a subscriber model, we can "blurt" out to anyone who wants to listen and
 * we can decide who to listen to. We cannot limit in here who can listen to us. So we talk publicly but listen to only the other peers
 * we are interested in.
 */

public class Leader {

    private static Set<Socket> listeningSockets = new HashSet<Socket>();
    //	private HashMap <String,Socket> list= new HashMap<String,Socket>();
    private HashMap<String, Socket> list = new HashMap<String, Socket>();
    private static HashMap<String, Integer> clientRecord = new HashMap<String, Integer>();
    private Socket socket;
    private Socket SocketLeader;

    public Leader(Socket socket) {
        this.socket = socket;
    }

    private InputStream inputStream;
    private OutputStream outputStream;



    private HashMap<Integer, InputStream> integerInputStreamHashMap = new HashMap<>();
    private HashMap<Integer, OutputStream> outputStreamHashMap = new HashMap<>();

    //	public Leader(ServerSocket serverSocket){
//
//		this.SocketLeader = serverSocket;
//	}
    private static JSONObject resName(String name) {
        JSONObject obj = new JSONObject();
        obj.put("Data", "Hello " + name);
//		obj.put("Client",0);
        obj.put("Name", name);
        clientRecord.put(name, 0);
        return obj;
    }

    private static JSONObject responseNodeAdded() {
        JSONObject obj = new JSONObject();
        obj.put("Node", "Got your message");
        return obj;
    }


    private void sendJsonToAllNodes(JSONObject res) throws IOException {
        try {
            for (Socket s : listeningSockets) {
                int port = s.getPort();
                System.out.println(port);
                integerInputStreamHashMap.put(port, s.getInputStream());
                outputStreamHashMap.put(port, s.getOutputStream());

            }
            outPutSendJson(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void sendJsonNodeCredit(JSONObject res) throws IOException {
        try {
            System.out.println("---Sending to nodes---");
            System.out.println(res.toString());
            outPutSendJson(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void outPutSendJson(JSONObject res) throws IOException {
        for (OutputStream out : outputStreamHashMap.values()) {
            NetworkUtils.Send(out, JsonUtils.toByteArray(res));
        }
        for (InputStream in : integerInputStreamHashMap.values()) {
            byte[] responseBytes = NetworkUtils.Receive(in);
            JSONObject req = JsonUtils.fromByteArray(responseBytes);
            if (req.has("Message")) {
                System.out.println(req.getString("Message"));
            }
        }
    }

    public void run() {
        boolean flag = true;


        while (true) {
            try {

                System.out.println("--Waiting--\n");
                //still waits after user sends name why??

                //check for client
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();

                byte[] responseBytes = NetworkUtils.Receive(inputStream);
                JSONObject req = JsonUtils.fromByteArray(responseBytes);
                System.out.println("--connection made--\n");
                JSONObject res = null;
                if (req.has("type")) {
                    String type = req.getString("type");
                    System.out.println("type given " + type);
                    //if client doesnt exist add its
                    System.out.println(req.toString());
                    switch (type) {
                        case "NameSend":
                            res = new JSONObject();
                            res.put("type", "Name");
                            res.put("Client1", 0);
                            res.put("Name", req.getString("Data"));
                            res.put("Notify", "Hello " + req.getString("Data"));
                            clientRecord.put(req.getString("Data"), 0);
                            break;
                        case "NamePromp":
                            res = new JSONObject();
                            res.put("Notify", "Whats your name");
                            res.put("Client", 0);
                            break;
                        case "Credit":

                            int numBanks = listeningSockets.size();
                            int amountLoan = req.getInt("Credit");
                            clientRecord.put(req.getString("User"), amountLoan);
                            int splitAmount = amountLoan / numBanks;
                            res = new JSONObject();
                            res.put("type", "Credit");
                            res.put("Money", splitAmount);
                            res.put("Name",req.getString("User"));
                            sendJsonNodeCredit(res);


                        case "creditAmount":
                            res = new JSONObject();
                            res.put("type", "creditAmount");
                            res.put("User", req.get("User"));
                            int amountBorrowed = 0;
                            clientRecord.containsKey(req.get("User"));
                            if (clientRecord.containsKey(req.get("User"))) {
                                amountBorrowed = clientRecord.get(req.get("User"));
                            } else {
                                System.out.println("Is not in client list");
                            }
                            res.put("Data", req.get("User") + " has borrowed " + amountBorrowed);
                            res.put("Client", 0);
                            break;
                        default:
                            System.out.println("No valid option");
                            break;
                    }
                }
//				if(req.has("ServerPort")){
//					Socket sock = new Socket("localhost",req.getInt("ServerPort"));
//					listeningSockets.add(sock);
//					res = new JSONObject();
//					res.put("ServerMessage","Port "+ sock.getPort() + " is connected");
//				}
//				else{
//					if(req.has("Node") && !listeningSockets.contains(socket)){
//						listeningSockets.add(new Socket("localhost",req.getInt("ServerPort")));
//						System.out.println("added to listen sockets port " + socket.getLocalPort());
//						System.out.println("Listening socket size " + listeningSockets.size());
//						res = responseNodeAdded();
//					}
//				}

                if (res != null) {
                    System.out.println("Sending message");
                    if (!res.has("Client")) {
                        sendJsonToAllNodes(res);
                        if (res.has("Client1")) {
                            NetworkUtils.Send(outputStream, JsonUtils.toByteArray(res));
                        }

                    } else if (res.has("Client")) {
                        System.out.println("Sending to client");
                        NetworkUtils.Send(outputStream, JsonUtils.toByteArray(res));
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //	public void msgFromNode(){
//		for(){
//		}
//	}
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(args[0]);
        ServerSocket server = new ServerSocket(port);
        Socket Socket = null;
        String nodes = args[1];
        String[] strArr = nodes.split(":");

        for (int i = 0; i < strArr.length; i++) {
            listeningSockets.add(new Socket("localhost", Integer.parseInt(strArr[i])));
        }

        while (true) {
            //tried adding here issue is cant add on a thread
            System.out.println("Wait for connection");

//			InputStream inputStream = Socket.getInputStream();
////			OutputStream outputStream = Socket.getOutputStream();
//			byte[] responseBytes = NetworkUtils.Receive(inputStream);
//			JSONObject req = JsonUtils.fromByteArray(responseBytes);
//
//			if(req.has("Node") && !listeningSockets.contains(Socket)){
//						listeningSockets.add(new Socket("localhost",req.getInt("ServerPort")));
//			}

            Socket = server.accept();
//			listeningSockets.add(Socket);
            System.out.println("Connection made");
            Leader server1 = new Leader(Socket);
//			server1.setListeningSockets(listSockets);

            server1.run();
//            server.start();
        }

    }

}
