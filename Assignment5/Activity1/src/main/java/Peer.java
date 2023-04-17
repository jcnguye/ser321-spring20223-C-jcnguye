import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * This is the main class for the peer2peer program.
 * It starts a client with a username and port. Next the peer can decide who to listen to. 
 * So this peer2peer application is basically a subscriber model, we can "blurt" out to anyone who wants to listen and 
 * we can decide who to listen to. We cannot limit in here who can listen to us. So we talk publicly but listen to only the other peers
 * we are interested in. 
 * 
 */

public class Peer {
	private String username;
	private BufferedReader bufferedReader;
	private ServerThread serverThread;

	private InputStream in;
	private OutputStream out;
	
	public Peer(InputStream reader, String username, ServerThread serverThread){
		this.username = username;
		in = reader;
		this.serverThread = serverThread;
	}
	public Peer(String username, ServerThread serverThread){
		this.username = username;
		this.serverThread = serverThread;
	}

	public ServerThread getServerThread() {
		return serverThread;
	}

	/**
	 * Main method saying hi and also starting the Server thread where other peers can subscribe to listen
	 *
	 * @param args[0] username
	 * @param args[1] port for server
	 */
	public static void main (String[] args) throws Exception {
//		InputStream in = new InputStream();
		String ip = args[0];
		String port = args[1];//localport
		int node = Integer.parseInt(args[2]);//port trying to connect to
		System.out.println("Hello your name " + ip + " and welcome! Your port will be " + port);
		// starting the Server Thread, which waits for other peers to want to connect
		ServerThread serverThread = new ServerThread(port);
		Peer peer = new Peer(ip, serverThread);
		serverThread.setPeer(peer);
		serverThread.start();
//		Socket socket = new Socket("localhost",Integer.parseInt(args[2]));
//		new ClientThread(socket,peer).start();
		peer.autoUpdateListenPeers("localhost",node,peer);
		peer.askForInput1();


	}
	
	/**
	 * Auto update peers
	 */
	public void autoUpdateListenPeers(String host, int port, Peer peer) throws Exception {
		if(UtilList.connectedPeers.contains(port)){
			return;
		}
		System.out.println("Listening to port: "+ port + "\n");
			Socket socket = null;
			try {
				socket = new Socket(host, port);
				UtilList.connectedPeers.add(port);
				new ClientThread(socket,peer).start(); //new node
			} catch (Exception c) {
				if (socket != null) {
					socket.close();
				} else {
					System.out.println("Port does not exist");
				}
			}
	}

	public void askForInput1(){
		try {
			System.out.println("Waiting for connection");
			while(true) {
				Scanner in = new Scanner(System.in);
				String message = in.nextLine();
				System.out.println("> You can now start chatting (exit to exit)");
				if (message.equals("exit")) {
					System.out.println("bye, see you next time");
					serverThread.sendMessage("{'username': '"+ username +"','message':'" + "exit" + "'}");
					break;
				}else {
					// we are sending the message to our server thread. this one is then responsible for sending it to listening peers
					serverThread.sendMessage("{'username': '"+ username +"','message':'" + message + "'}");
				}
			}
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
