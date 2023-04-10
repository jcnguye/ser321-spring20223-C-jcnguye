import java.io.*;
import java.net.Socket;

/**
 * Client 
 * This is the Client thread class, there is a client thread for each peer we are listening to.
 * We are constantly listening and if we get a message we print it. 
 */

public class Client {

	private BufferedReader bufferedReader;
	private String userName;
	OutputStream out = null;
	InputStream in = null;
	public Client(Socket socket) throws IOException {
		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void run(){
		boolean flag = true;
		while (flag){

			int choice = 0;
			switch (choice){
				case 1:
					System.out.println("Credit");
					break;
				case 2:
					System.out.println("Pay Back");
					break;
				default:
					System.out.println("Not a valid choice");
			}



		}
	}

	public static void main (String[] args) throws IOException {
		String name = args[0];
		int port = Integer.parseInt(args[1]);
		Socket socket = new Socket("localhost",port);
		Client client = new Client(socket);
		client.setUserName(name);
		client.run();

	}


}
