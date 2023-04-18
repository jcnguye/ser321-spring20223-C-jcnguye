import org.json.JSONObject;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class JsonUtils {
  public static Set<Integer> connectedPeers = new ConcurrentSkipListSet<>();

  public static JSONObject fromByteArray(byte[] bytes) {
    String jsonString = new String(bytes);
    return new JSONObject(jsonString);
  }
  
  public static byte[] toByteArray(JSONObject object) {
    return object.toString().getBytes();
  }
}
