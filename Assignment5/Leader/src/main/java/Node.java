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

	//hashmap client
	private HashMap<String,Integer> clientAccount = new HashMap<>();
	public Node(String portNum, int money) throws IOException {
		server = new ServerSocket(Integer.parseInt(portNum));
		this.money = money;
	}
	private static JSONObject responseName(String name){
		JSONObject obj = new JSONObject();
		obj.put("type",name);
		return obj;
	}

	public void setLeaderSocket(Socket leaderSocket) {
		this.leaderSocket = leaderSocket;
	}
	public void connectionEstablish(){
		try {
			OutputStream out = this.leaderSocket.getOutputStream();
			InputStream in = this.leaderSocket.getInputStream();
			JSONObject request = new JSONObject();
			request.put("Node",0);
			request.put("ServerPort",server.getLocalPort());

			NetworkUtils.Send(out, JsonUtils.toByteArray(request));
			System.out.println("Waiting on response from leader");
			byte[] messageBytes = NetworkUtils.Receive(in);
			JSONObject response = JsonUtils.fromByteArray(messageBytes);
			System.out.println("You are connected \n"+response.getString("ServerMessage"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public void run() {
		try {
			connectionEstablish();
			while (true) {
				Socket sock = server.accept();
				OutputStream out = sock.getOutputStream();
				InputStream in = sock.getInputStream();
				byte[] messageBytes = NetworkUtils.Receive(in);
				JSONObject request = JsonUtils.fromByteArray(messageBytes);
				JSONObject response = null;
				System.out.println("-----Connection made-----");
				if(request.has("type")){
					String choice = request.getString("type");
					switch (choice) {
						case "Name":
							clientAccount.put(request.getString("User"),0);
							response = responseName(request.getString("User"));
							break;
						case "gw":
							break;
						default:
							System.out.println("No valid request option");
							break;
					}
				}


				if (response != null){
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

	public void ConnectionCement(){

	}

	public static void main (String[] args) throws Exception {
		int money = Integer.parseInt(args[1]);
		String port = args[0]; //bank port
		int leaderPort = Integer.parseInt(args[2]); //port connected to leader
		//starts the leader thread if exist go on to logic
//		try {
//			ServerSocket server = new ServerSocket(leaderPort);
//			Leader leader = new Leader(server);
//			leader.start();
//		}catch (BindException e){
//			Socket socket = new Socket("localhost",leaderPort);
//			Node node = new Node(port,money);
//			node.setLeaderSocket(socket);
//			node.run();
//		}

		Socket socket = new Socket("localhost",leaderPort);
		Node node = new Node(port,money);
		node.setLeaderSocket(socket);
		node.run();

	}



}
