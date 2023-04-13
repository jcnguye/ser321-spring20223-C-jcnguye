import java.net.ServerSocket;
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

public class Leader extends Thread{
	private Set<Socket> listeningSockets = new HashSet<Socket>();
	private Socket socket;
	private ServerSocket serverSocket;
	public Leader(Socket socket, ServerSocket serverSocket,Set<Socket> listeningSockets){
		this.socket = socket;
		this.serverSocket = serverSocket;
		this.listeningSockets = listeningSockets;
	}

	public void run(){

	}
	/**
	 * Main method saying hi and also starting the Server thread where other peers can subscribe to listen
	 *
	 * @param args[0] username
	 * @param args[1] port for server
	 */
	public static void main (String[] args) throws Exception {
		String ports = args[0];
		String[] listPort = ports.split(":");


//		String[] money = new String[listPort.length];
//		String[] port = new String[listPort.length];
//		for(int i = 0; i < listPort.length; i++){
//			String[] splitArr = listPort[i].split("_");
//			money[i] = splitArr[0];
//			port[i] = splitArr[1];
//			int mon = Integer.parseInt(money[i]);
//		}

	}

}
