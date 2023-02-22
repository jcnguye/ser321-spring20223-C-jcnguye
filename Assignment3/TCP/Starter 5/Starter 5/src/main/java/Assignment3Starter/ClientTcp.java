package Assignment3Starter;
import Assignment3Starter.ClientGui;
import org.json.*;

import java.io.*;
import java.util.Scanner;
import java.net.Socket;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ClientTcp {
    public static JSONObject resName(String msg) {
    JSONObject response = new JSONObject();
    response.put("type","echo");
    response.put("message", msg);
    return response;
}


    public static void main(String[] args) throws IOException {

        Socket sock;
        try {
            sock = new Socket(args[1],Integer.parseInt(args[0]));

            // get output channel
            OutputStream out = sock.getOutputStream();
            // create an object output writer (Java only)
            ObjectOutputStream os = new ObjectOutputStream(out);
            ObjectInputStream in = new ObjectInputStream(sock.getInputStream());

            JSONObject req = null;
            JSONObject res = null;
            String s;
            try {
                s = (String) in.readObject();
                req = new JSONObject();
                res = new JSONObject(s);
            } catch (Exception e) {
                System.out.println("Error reading object");
            }
            Scanner scanner = new Scanner(System.in);//wait
            if(res.getInt("code") == 200 && res.has("message") && res.get("type").equals("name")){
                System.out.println(res.getString("message")); //ask user for username
                String message = scanner.nextLine();//wait here
                req.put("code", 200);
                req.put("type", "name");
                req.put("message",message);
                os.writeObject(req.toString());
                os.flush();
            }

        }catch(IOException e){
            e.printStackTrace();
        }

    }

}
