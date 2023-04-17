import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import org.json.*;

/**
 * Client
 * This is the Client thread class, there is a client thread for each peer we are listening to.
 * We are constantly listening and if we get a message we print it.
 */
public class Client {
    public String name;
    Socket socket;
    ServerSocket serverSocket;
    public Client(Socket socket,ServerSocket serverSocket){
        this.socket = socket;
        this.serverSocket = serverSocket;
    }

	public static JSONObject reqGetCreditAmount(){
		JSONObject obj = new JSONObject();
		obj.put("type","creditAmount");
        obj.put("Client",0);
		return obj;
	}
    int count = 0;
    public void run() throws Exception {
//        ServerSocket serverSocket = new ServerSocket(socket.getLocalPort());
//        Leader leader = new Leader(socket,serverSocket);
//        leader.start();
        boolean flag = true;
        do {
            JSONObject req = null;
            OutputStream out = null;



//			NetworkUtils.Send(out, JsonUtils.toByteArray(req));

            if(count == 0){
                notifyLeader();
                count++;
                sendName();
            }


            out = socket.getOutputStream();
            int choice = 0;
            System.out.println();
            System.out.println("Client Menu");
            System.out.println("Please select a valid option (1-3). 0 to disconnect the client");
            System.out.println("1. Request amount borrowed");
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
            String type = response.getString("type");
            switch (type){
                case "amount":
                    System.out.println("Total borrowed: " + response.getInt("total"));
                    break;
                default:
                    System.out.println("No valid option");
                    break;
            }
        } while (flag);

    }


    public void notifyLeader(){


        OutputStream out = null;
        try {
            out = socket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONObject req = new JSONObject();
        req.put("type","NamePromp");
        req.put("Client",0);
        req.put("Port",serverSocket.getLocalPort());
        try {
            NetworkUtils.Send(out, JsonUtils.toByteArray(req));

            InputStream in = socket.getInputStream();
            byte[] responseBytes = NetworkUtils.Receive(in);


            JSONObject response = JsonUtils.fromByteArray(responseBytes);
            System.out.println("Message recieved");
            if(response.has("Notify")){
                System.out.println(response.getString("Notify"));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendName(){
        OutputStream out = null;
        try {
            out = socket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONObject req = new JSONObject();
        req.put("type","NameSend");
        Scanner input = new Scanner(System.in);
        String name = input.nextLine();
        this.name = name;
        req.put("Data",name);
        req.put("Client",0);
//        req.put("Client",0);
        try {
            //stuck here after sending name not getting response from leader
            NetworkUtils.Send(out, JsonUtils.toByteArray(req));
            InputStream in = socket.getInputStream();


            byte[] responseBytes = NetworkUtils.Receive(in);
            JSONObject response = JsonUtils.fromByteArray(responseBytes);
            System.out.println("Message recieved");
            if(response.has("Data")){
                System.out.println(response.getString("Data"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws Exception {
        ServerSocket socket1 = new ServerSocket(Integer.parseInt(args[1]));
        Socket socket = new Socket("localhost", Integer.parseInt(args[0]));
        Client client = new Client(socket,socket1);
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
