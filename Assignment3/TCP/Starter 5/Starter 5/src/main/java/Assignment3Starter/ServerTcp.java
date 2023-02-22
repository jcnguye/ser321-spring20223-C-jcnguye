package Assignment3Starter;

import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.net.*;
import java.io.*;

public class ServerTcp {
    public static JSONObject askUser() {
        JSONObject response = new JSONObject();
        response.put("code",200);
        response.put("type", "response");
        response.put("message", "Enter your name");
        return response;
    }
    public static JSONObject askPlayBoard() {
        JSONObject response = new JSONObject();
        response.put("code",200);
        response.put("type", "game");
        response.put("message", "Type 'leader' to see leader board or 'play' to play game ");
        return response;
    }
    public static void main(String[] args) throws IOException {
        ServerSocket serv = null;
        Socket sock;
        /*
         res = {ok: true, message: "Enter name"}
         res = {
            more:
         }
         res = {next: }
         */
        try {
            serv = new ServerSocket(Integer.parseInt(args[0]));
            System.out.println("Server ready for connections");
            while (true) {
                System.out.println("Server waiting for a connection");
                sock = serv.accept(); //waiting here
                System.out.println("Client made connection");
                // setup the object reading channel
                ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
                // get output channel
                OutputStream out = sock.getOutputStream();
                // create an object output writer (Java only)
                ObjectOutputStream os = new ObjectOutputStream(out); //sends to client
                JSONObject res = new JSONObject();
                res = askUser();
                os.writeObject(res.toString());
                os.flush();

                String s = (String) in.readObject();//wait
                JSONObject req = new JSONObject(s);
                if(req.getInt("code") == 200 && req.get("type").equals("name")){
                    System.out.println(req.getString("message"));
                    res = null;
                    res.put("type", "reply");
                    res.put("message", req.get("message"));
                    os.writeObject(res.toString());
                    os.flush();
                }
//                res = askPlayBoard();
//                os.writeObject(res.toString());
//                os.flush();
                //if user enters to play
                //goes in a loop
                //else if user enters to see leader board
                //display leader board
//                do{
//
//                }while (true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(serv != null){
                serv.close();
            }
        }
    }
}
