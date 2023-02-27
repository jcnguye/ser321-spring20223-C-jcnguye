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

import java.io.*;
import java.util.*;

import java.net.*;


public class ServerUdp {
    public static JSONObject image() throws IOException {
        JSONObject json = new JSONObject();
        json.put("datatype", 2);

        json.put("type", "img");

        File file = new File("img/Joker/quote4.png");
        if (!file.exists()) {
            System.err.println("Cannot find file: " + file.getAbsolutePath());
            System.exit(-1);
        }
        // Read in image
        BufferedImage img = ImageIO.read(file);
        byte[] bytes = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", out);
            bytes = out.toByteArray();
        }
        if (bytes != null) {
            Base64.Encoder encoder = Base64.getEncoder();
            json.put("data", encoder.encodeToString(bytes));
            return json;
        }
       return json;
    }
    public static JSONObject askName(){
        JSONObject json = new JSONObject();
        json.put("type", "name");
        json.put("data", "Hello what is your name?");
        return json;
    }
    public static JSONObject returnName(String name){
        JSONObject json = new JSONObject();
        json.put("type", "name");
        json.put("data", "Hello " + name + " what a lovely day it is, type 'picture' for picture");
        return json;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        DatagramSocket sock = null;
        try {
            sock = new DatagramSocket(Integer.parseInt(args[0]));
            while (true) {
                NetworkUtils.Tuple messageTuple = NetworkUtils.Receive(sock);//stops here
                JSONObject message = JsonUtils.fromByteArray(messageTuple.Payload);//stop here
                JSONObject returnMessage = null;
                if (message.getString("type").equals("name")) {
                    returnMessage = askName();
                } else if (message.getString("type").equals("greeting")) {
                    returnMessage = returnName(message.getString("data"));
                }else if (message.getString("type").equals("img")) {
                    returnMessage = image();
                }else{
                    returnMessage.put("type","error");
                    returnMessage.put("data","missing name");
                }
                byte[] output = JsonUtils.toByteArray(returnMessage);
                NetworkUtils.Send(sock, messageTuple.Address, messageTuple.Port, output);
            }

        }catch (IOException e) {
            e.printStackTrace();
        }

    }
}

