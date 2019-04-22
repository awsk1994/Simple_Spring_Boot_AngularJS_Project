package hello.Utils;

import hello.Settings;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.*;

// TODO implement synchronize block, for locks.

public class SessionIdFileHandler {
    final private static String filepath = Settings.SESSION_ID_FILENAME;

    public static void startNew(){
        JSONObject obj = new JSONObject();
        write(obj, filepath);
    }

    public static void addAgent(int agentIdx, String sessionId){
        JSONObject obj = read(filepath);
        obj.put(Integer.toString(agentIdx), sessionId);
        write(obj, filepath);
    }

    public static void removeAgent(int agentIdx){
        JSONObject obj = read(filepath);
        obj.remove(Integer.toString(agentIdx));
        write(obj, filepath);
    }

    public static JSONObject read(String filepath){
        JSONParser parser = new JSONParser();
        JSONObject sessionIdJson = null;
        try {
            sessionIdJson = (JSONObject) parser.parse(new FileReader(filepath));
        } catch (Exception e){
            e.printStackTrace();
        }
        return sessionIdJson;
    };

    public static void write(JSONObject obj, String filepath) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
            String content = new ObjectMapper().writeValueAsString(obj);
            writer.write(content);
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
