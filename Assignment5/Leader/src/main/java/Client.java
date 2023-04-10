import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.*;

/**
 * Client
 * This is the Client thread class, there is a client thread for each peer we are listening to.
 * We are constantly listening and if we get a message we print it.
 */

public class Client extends Thread{
	private BufferedReader bufferedReader;

	public Client(Socket socket) throws IOException {
		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	public void run() {
		while (true) {
			JSONObject json = null;

			try {
				json = new JSONObject(bufferedReader.readLine());
				System.out.println("[" + json.getString("username")+"]: " + json.getString("message"));
			} catch (IOException e) {
				System.out.println();
				try {
					json = new JSONObject(bufferedReader.readLine());
					System.out.println("[" + json.getString("username")+"]: " + json.getString("message"));
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}

			}


		}
	}

}
