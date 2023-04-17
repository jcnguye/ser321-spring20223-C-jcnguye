import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class UtilList {
    public static Set<Integer> connectedPeers = new ConcurrentSkipListSet<>();

    public static void Send(OutputStream out, String data) {
        //convert data byte array
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(bytes.length);
        try {
            out.write(buffer.array());
            out.flush();
            out.write(bytes);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String Recieve(InputStream in) {
        //convert data byte array
        ByteBuffer buffer = ByteBuffer.allocate(4);

        try {
            buffer.put(0,in.readNBytes(4));
            int byteCount = buffer.getInt(0);
            buffer = ByteBuffer.allocate(byteCount);
            buffer.put(in.readNBytes(byteCount));
            return new String(buffer.array(),StandardCharsets.UTF_8);
        } catch (IOException e) {
//            throw new RuntimeException(e);
            System.out.println("User has left");
        }
        return "";
    }

}
