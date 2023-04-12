import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
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

public class ServerThread extends Thread{
	private ServerSocket serverSocket;
	private Set<Socket> listeningSockets = new HashSet<Socket>();
	Peer peer;
	private int portNum;

	public int getPortNum() {
		return portNum;
	}

	public ServerThread(String portNum) throws IOException {
		serverSocket = new ServerSocket(Integer.parseInt(portNum));
		this.portNum = Integer.parseInt(portNum);
	}

	public void setPeer(Peer peer){
		this.peer = peer;
	}

	
	/**
	 * Starting the thread, we are waiting for clients wanting to talk to us, then save the socket in a list
	 */
	public void run() {
		try {
			while (true) {
				JSONObject json = null;
				Socket sock = serverSocket.accept(); //waits and accepts connection
				System.out.println("------Connection made---------");
				//open input stream
				InputStream in = sock.getInputStream();
				//read in server ip and port num from client
				String ip = UtilList.Recieve(in);
//				int ports = Integer.parseInt(String.valueOf(sock.getLocalPort()));
				int ports = Integer.parseInt(ip);

				//open output stream
				OutputStream out = sock.getOutputStream();
				//sends list of current connections made server ip and port
				JSONArray peers = new JSONArray();

				UtilList.connectedPeers.forEach(peers::put);
				//adds socket to list of listening sock
				UtilList.Send(out,peers.toString());
				peer.autoUpdateListenPeers("localhost", ports,peer);
				listeningSockets.add(sock);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public ArrayList<Integer> Ports(){
		ArrayList port1 = new ArrayList<>();


		return port1;
	}
	public JSONObject response(Set<Socket> sock){
		JSONObject json = new JSONObject();

		json.put("data",sock);

		return json;
	}
	/**
	 * Sends lists of sockets as json obj
	 */
	void sendMessageSock(JSONObject message) {
		try {
			for (Socket s : listeningSockets) {
				UtilList.Send(s.getOutputStream(),message.toString());
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Sending the message to the OutputStream for each socket that we saved
	 */
	void sendMessage(String message) {
		try {
			for (Socket s : listeningSockets) {
				UtilList.Send(s.getOutputStream(),message);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public Set<Socket> getListeningSockets(){
		return listeningSockets;
	}

	public int getAmountPort(){
		return listeningSockets.size();
	}
	/**
	 *
	 * @param port takes in port number
	 * @return true if port exists and matches
	 */
	public boolean getSpecificPort(int port){
		for (Socket s : listeningSockets) {
			return s.getPort() == port;
		}
		return false;
	}
}
