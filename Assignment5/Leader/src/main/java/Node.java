import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

	//hashmap client
	private HashMap<String,Integer> clientAccount = new HashMap<>();
	public Node(String portNum, int money) throws IOException {
		server = new ServerSocket(Integer.parseInt(portNum));
		this.money = money;
	}
	public void run() {
		try {
			while (true) {
				Socket sock = server.accept();
				OutputStream out = sock.getOutputStream();
				InputStream in = sock.getInputStream();
				byte[] messageBytes = NetworkUtils.Receive(in);
				JSONObject request = JsonUtils.fromByteArray(messageBytes);
				System.out.println("-----Connection made-----");
				String choice = request.getString("type");

				switch (choice) {
					case "qw":
						break;
					case "gw":
						break;
					default:
						System.out.println("No valid request option");
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void ConnectionCement(){

	}

	public static void main (String[] args) throws Exception {
		int money = Integer.parseInt(args[1]);
		String port = args[0];

		Node node = new Node(port,money);

		node.run();
	}



}
