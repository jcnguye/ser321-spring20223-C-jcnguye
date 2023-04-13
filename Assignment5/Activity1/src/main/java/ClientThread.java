import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.json.*;

import javax.sound.sampled.Port;

/**
 * Client 
 * This is the Client thread class, there is a client thread for each peer we are listening to.
 * We are constantly listening and if we get a message we print it. 
 */

public class ClientThread extends Thread{


	private InputStream in;
	private OutputStream out;
	private static Set<Integer> list = new HashSet<>();
	Peer peer;
	private static int port;
	private static Socket sock;
	

	public ClientThread(Socket socket,Peer peer) throws IOException {
		in = socket.getInputStream();
		out = socket.getOutputStream();
		this.sock = socket;
		this.peer = peer;

	}
	public void run() {
		//sends server ip and
		String val = String.valueOf(this.peer.getServerThread().getPortNum());
		UtilList.Send(out, val);
		//recive list server ip and port
		JSONArray obj = new JSONArray();
		obj = new JSONArray(UtilList.Recieve(in));
		//check if contains
//		String[] p = input.split(" ");
//		int ports = Integer.parseInt();
		for (var element : obj) {
			Integer num = (Integer) element;
			try {
				peer.autoUpdateListenPeers("localhost", num, peer);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}


		System.out.println("Heres your ports");
		//got your message
		while (true) {
			JSONObject json = new JSONObject(UtilList.Recieve(in));
				System.out.println("[" + json.getString("username")+"]: " + json.getString("message"));
		}
	}

}
