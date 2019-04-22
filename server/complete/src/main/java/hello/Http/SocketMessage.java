package hello.Http;

import hello.Application;
import hello.Runnables.LoginRunnable;
import hello.Runnables.LogoutRunnable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

public class SocketMessage {
    @JsonProperty("key") String key;
    @JsonProperty("msg") String msg;

    @JsonCreator
    public SocketMessage(@JsonProperty("key") String key, @JsonProperty("msg") String msg){
        this.key = key;
        this.msg = msg;
    }

    public String getKey() {
        return key;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "SocketMessage{" +
                "key='" + key + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }

    public String toJsonString(){
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
