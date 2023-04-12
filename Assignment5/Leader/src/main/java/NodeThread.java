import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * SERVER
 * This is the ServerThread class that has a socket where we accept clients contacting us.
 * We save the clients ports connecting to the server into a List in this class.
 * When we wand to send a message we send it to all the listening ports
 */

public class NodeThread extends Thread{
	ServerSocket server;
	private Set<Socket> listeningSockets = new HashSet<Socket>();
	int money;

	public NodeThread(String portNum, int money) throws IOException {
		server = new ServerSocket(Integer.parseInt(portNum));
		this.money = money;
	}
	public void run() {
		try {
			while (true) {
				Socket sock = server.accept();
				listeningSockets.add(sock);
				String input = UtilList.Recieve(sock.getInputStream());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
