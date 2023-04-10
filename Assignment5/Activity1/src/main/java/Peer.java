import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
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

public class Peer {
	private String username;
	private BufferedReader bufferedReader;
	private ServerThread serverThread;
	
	public Peer(BufferedReader bufReader, String username, ServerThread serverThread){
		this.username = username;
		this.bufferedReader = bufReader;
		this.serverThread = serverThread;
	}
	/**
	 * Main method saying hi and also starting the Server thread where other peers can subscribe to listen
	 *
	 * @param args[0] username
	 * @param args[1] port for server
	 */
	public static void main (String[] args) throws Exception {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		String username = args[0];
		System.out.println("Hello " + username + " and welcome! Your port will be " + args[1]);
		String host = "localhost";
		int count = Integer.parseInt(args[3]); //node number it is
		// starting the Server Thread, which waits for other peers to want to connect
		ServerThread serverThread = new ServerThread(args[1]);

		Peer peer = new Peer(bufferedReader, args[0], serverThread);
		serverThread.setPeer(peer);
		serverThread.start();
		//Starting out when theres no active nodes
		if(count == 1){
//			peer.waitConnectionClient("local",);
			peer.askForInput1(Integer.parseInt(args[2]));
		} else if (count > 1) {
			peer.autoUpdateListenPeers(host,Integer.parseInt(args[2]));
		}



	}
	
	/**
	 * Auto update peers
	 *
	 */
	public void autoUpdateListenPeers(String host, int port) throws Exception {

		System.out.println("Listening to port: "+ port + "\n");
			Socket socket = null;
			try {
				socket = new Socket(host, port);
				new ClientThread(socket).start(); //new node
			} catch (Exception c) {
				if (socket != null) {
					socket.close();
				} else {
					System.out.println("Port does not exist");
					System.exit(0);
				}
			}
		askForInput();
	}

	/**
	 * User is asked to define who they want to subscribe/listen to
	 * Per default we listen to no one
	 *
	 */
	public void waitConnectionClient(String host,int port) throws Exception {
		System.out.println("Listening to port: "+ port + "\n");

		Socket socket = null;
		try {
			socket = new Socket(host, port);
			new ClientThread(socket).start(); //new node
		} catch (Exception c) {
			if (socket != null) {
				socket.close();
			} else {
				System.out.println("Port does not exist");
				System.exit(0);
			}
		}
		askForInput();
	}
	
	/**
	 * Client waits for user to input their message or quit
	 *
	 * @param bufReader bufferedReader to listen for user entries
	 * @param username name of this peer
	 * @param serverThread server thread that is waiting for peers to sign up
	 */
	public void askForInput(){
		try {
			System.out.println("> You can now start chatting (exit to exit)");
			while(true) {
				String message = bufferedReader.readLine();
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

	public void askForInput1(int port){
		try {
			System.out.println("Waiting for connection");
			while(true) {
				String message = bufferedReader.readLine();
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
