package hello.Utils;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

public class SavedSessionId {
    @JsonProperty("agentIdx")String agentIdx;
    @JsonProperty("sessionid")String sessionid;

    @JsonCreator
    public SavedSessionId(@JsonProperty("agentIdx") String agentIdx, @JsonProperty("sessionId") String sessionid) {
        this.agentIdx = agentIdx;
        this.sessionid = sessionid;
    }

    public String getAgentIdx() {
        return agentIdx;
    }

    public String getSessionid() {
        return sessionid;
    }

    public String toJsonString(){
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @Override
    public String toString() {
        return "SavedSessionId{" +
                "agentIdx=" + agentIdx +
                ", sessionid='" + sessionid + '\'' +
                '}';
    }
}
