package hello.Agent;

import hello.Application;
import hello.Runnables.CloseSessionRunnable;
import hello.Runnables.LoginRunnable;
import hello.Runnables.LogoutRunnable;
import hello.Utils.SessionIdFileHandler;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

public class Agent {
    @JsonProperty("idx") int idx;
    @JsonProperty("username") String username;
    @JsonProperty("password") String password;
    @JsonProperty("extension") int extension;
    @JsonProperty("lastUpdated") String lastUpdated;
    @JsonProperty("state") AgentState state;
    @JsonProperty("sessionId") String sessionId;
    @JsonProperty("apiToken") String apiToken;
    @JsonProperty("agentId") int agentId;

    @JsonCreator
    public Agent(@JsonProperty("username") String username, @JsonProperty("password") String password, @JsonProperty("extension") int extension) {
        this.idx = -1;
        this.username = username;
        this.password = password;
        this.extension = extension;
        this.lastUpdated = "none";
        this.state = AgentState.TURNED_OFF;
        this.sessionId = null;
        this.apiToken = null;
        this.agentId = -1;
    }

    /* Agent Specific Functions */

    public void login(){
        this.setState(AgentState.TURNED_ON);
        Application.main.Executor.execute(new LoginRunnable(this));
    }

    public void logout(){
        Application.main.Executor.execute(new LogoutRunnable(this));
    }

    public void turnOff(){
        // Reset agent - almost like destroying the agent instance, but without removing it.
        // this.state = AgentState.TURNED_OFF;
        this.updateLastUpdated();
    }

    public void closeSession(){
        if(this.sessionId != null) {
            Application.main.Executor.execute(new CloseSessionRunnable(this));
        }
    }

    public void updateLastUpdated(){
        this.lastUpdated = System.currentTimeMillis() + "";     // TODO: Better date format.
    }

    /* Standard Class Functions */

    public void setIdx(int idx){
        this.idx = idx;
    }

    public void setAgentId(int agentid){
        this.agentId = agentid;
    }

    public void setState(AgentState state) {
        this.state = state;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
        SessionIdFileHandler.addAgent(this.idx, this.sessionId);
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public int getIdx() {
        return idx;
    }

    public int getAgentId() {
        return agentId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getExtension() {
        return extension;
    }

    public AgentState getState() {
        return state;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getApiToken() {
        return apiToken;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    /* To String Functions */

    @Override
    public String toString() {
        return "Agent{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", extension=" + extension +
                ", lastUpdated='" + lastUpdated + '\'' +
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
