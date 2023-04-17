import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This is the main class for the peer2peer program.
 * It starts a client with a username and port. Next the peer can decide who to listen to.
 * So this peer2peer application is basically a subscriber model, we can "blurt" out to anyone who wants to listen and
 * we can decide who to listen to. We cannot limit in here who can listen to us. So we talk publicly but listen to only the other peers
 * we are interested in.
 *
 */

public class Leader extends Thread{

	private static Set<Socket> listeningSockets = new HashSet<Socket>();
	private HashMap <String,Socket> list= new HashMap<String,Socket>();
	private static HashMap <String,Integer> clientRecord = new HashMap<String,Integer>();
	private Socket socket;
	private Socket SocketLeader;
	public Leader(Socket socket){
		this.socket = socket;
//		this.SocketLeader = serverSocket;
	}


//	public Leader(ServerSocket serverSocket){
//
//		this.SocketLeader = serverSocket;
//	}
	private static JSONObject resName(String name){
		JSONObject obj = new JSONObject();
		obj.put("Data","Hello " + name);
		clientRecord.put(name,0);
		return obj;
	}
	private static JSONObject responseNodeAdded(){
		JSONObject obj = new JSONObject();
		obj.put("Node","Got your message");
		return obj;
	}

	public void setListeningSockets(Set<Socket> listeningSockets) {
		this.listeningSockets = listeningSockets;
	}

	private void sendJsonToAllNodes(JSONObject res) throws IOException {
		try {
			for (Socket s : listeningSockets) {

				NetworkUtils.Send(s.getOutputStream(), JsonUtils.toByteArray(res));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void run(){
		boolean flag = true;



			while (true){
			try {

				System.out.println("--Waiting--\n");
				//still waits after user sends name why??

				//check for client
				InputStream in = socket.getInputStream();
				OutputStream out = socket.getOutputStream();

				byte[] responseBytes = NetworkUtils.Receive(in);
				JSONObject req = JsonUtils.fromByteArray(responseBytes);
				System.out.println("--connection made--\n");
				JSONObject res = null;
				if(req.has("type")){
					String type = req.getString("type");
					System.out.println("type given "+ type);
					//if client doesnt exist add its
					switch (type){
						case "NameSend":
							res = resName(req.getString("Data"));
							break;
						case "NamePromp":
							res = new JSONObject();
							res.put("Notify","Whats your name");
							break;
						default:
							System.out.println("No valid option");
							break;
					}
				}
				if(req.has("ServerPort")){
					Socket sock = new Socket("localhost",req.getInt("ServerPort"));
					listeningSockets.add(sock);
					res = new JSONObject();
					res.put("ServerMessage","Port "+ sock.getPort() + " is connected");
				}
//				else{
//					if(req.has("Node") && !listeningSockets.contains(socket)){
//						listeningSockets.add(socket);
//						System.out.println("added to listen sockets port " + socket.getLocalPort());
//						System.out.println("Listenging socket size " + listeningSockets.size());
//						res = responseNodeAdded();
//					}
//				}

				if(res != null){
					System.out.println("Sending message");
					if(!req.has("Client")){
						sendJsonToAllNodes(res);
					}else if(req.has("Client")){
						System.out.println("Sending to client");
						NetworkUtils.Send(out,JsonUtils.toByteArray(res));
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	public void updateLink(Socket sock){

	}
	public static void main (String[] args) throws Exception {
		int port = Integer.parseInt(args[0]);
		ServerSocket server = new ServerSocket(port);
		Socket Socket = null;
		while (true) {
			System.out.println("Wait for connection");
			Socket = server.accept();
			System.out.println("Connection made");
			Leader server1 = new Leader(Socket);
//			server1.setListeningSockets(listSockets);
			Thread thread = new Thread(server1);
			thread.start();

//            server.start();
		}

	}

}
