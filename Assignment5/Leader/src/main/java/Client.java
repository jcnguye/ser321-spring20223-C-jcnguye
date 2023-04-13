import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import org.json.*;

/**
 * Client
 * This is the Client thread class, there is a client thread for each peer we are listening to.
 * We are constantly listening and if we get a message we print it.
 */
public class Client {
    public static String name;
    Socket socket;

    public Client(Socket socket) throws IOException {
        this.socket = socket;
    }

	public static JSONObject reqGetCreditAmount(){
		JSONObject obj = new JSONObject();
		obj.put("type","creditAmount");
		return obj;
	}

    public void run() throws Exception {
        boolean flag = true;
        do {
            JSONObject req = null;
            OutputStream out = null;

            out = socket.getOutputStream();

//			NetworkUtils.Send(out, JsonUtils.toByteArray(req));


            int choice = 0;
            System.out.println();
            System.out.println("Client Menu");
            System.out.println("Please select a valid option (1-3). 0 to disconnect the client");
            System.out.println("1. Get amount borrowed");
            System.out.println("2. Request to borrow");
            System.out.println("3. Pay bank back");
            System.out.println("4. exit menu");
            System.out.println();
            boolean flag1 = true;
            Scanner input = new Scanner(System.in);
            while (flag1) {
                try {
                    choice = input.nextInt();
					flag1 = false;
                } catch (NumberFormatException e) {
                    System.out.println("Not a number");
                }
            }
            switch (choice) {
                case 1:
					req = reqGetCreditAmount();
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                default:
                    System.out.println("No valid option");
					req = new JSONObject();
                    break;
            }
			NetworkUtils.Send(out, JsonUtils.toByteArray(req));
			InputStream in = socket.getInputStream();
			byte[] responseBytes = NetworkUtils.Receive(in);
			JSONObject response = JsonUtils.fromByteArray(responseBytes);


        } while (flag);

    }


    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", Integer.parseInt(args[1]));
        Client client = new Client(socket);
        client.run();

//
//        JSONObject req = new JSONObject();
//        req.put("name", args[0]);
//
//
//        OutputStream out = socket.getOutputStream();
//        NetworkUtils.Send(out, JsonUtils.toByteArray(req));
//        InputStream in = socket.getInputStream();
//        byte[] responseBytes = NetworkUtils.Receive(in);
//        JSONObject response = JsonUtils.fromByteArray(responseBytes);
//        System.out.println(response.get("name"));
    }

}
