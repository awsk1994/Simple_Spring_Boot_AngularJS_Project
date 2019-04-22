package hello.Agent;

import hello.Application;
import hello.HttpResponse.*;
import hello.Settings;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

public class AgentHttpActions {
    public static HttpResult getAgents() {
        try {
            return new AgentsResult("OK", AgentActions.getAgents("saved"));
        } catch (Exception e){
            e.printStackTrace();
            return new ErrorResult(e.getMessage());
        }
    }

    public static HttpResult getActiveAgents() {
        try {
            return new AgentsResult("OK", AgentActions.getAgents("active"));
        } catch (Exception e){
            e.printStackTrace();
            return new ErrorResult(e.getMessage());
        }
    }

    public static HttpResult getAgent(String idxStr){
        try{
            return new AgentResult("OK", AgentActions.getAgent(idxStr));
        } catch (Exception e){
            e.printStackTrace();
            return new ErrorResult(e.getMessage());
        }
    }

    public static HttpResult addAgent(String user, String password, int extension) {
        Application.main.Agents.add(new Agent(user, password, extension));
        String message = "Size: " + Application.main.Agents.size();
        return new StatusResult("OK", message);   // HttpResult object will return the HTTP in the json form of {data: {status: <STATUS>, details: <DETAILS>}}
    }

    public static HttpResult startAgent(String idxStr) {
        System.out.println("----->>"+idxStr);
        int idx = Integer.parseInt(idxStr);
        System.out.println("----->>"+idx);
        if(idx == -1){
            String errMsg = "Got idx of -1";
            System.err.println(errMsg);
            return new ErrorResult(errMsg);
        } else {
            AgentActions.startAgent(idx);
            return new HttpResult("OK");
        }
    }

    public static HttpResult startAgents(String idxsStr){
        try {
            ArrayList<Integer> idxs = new ObjectMapper().readValue(idxsStr, ArrayList.class);
            for(int i=0; i<idxs.size(); i++){
                AgentActions.startAgent(Integer.parseInt(String.valueOf(idxs.get(i))));
                Thread.sleep(Settings.RAMP_UP_SECS);        // TODO: confirm all API are on separate thread.
            }
            return new HttpResult("OK");
        } catch (JsonGenerationException | JsonMappingException e) {
            e.printStackTrace();
            return new ErrorResult(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return new ErrorResult(e.getMessage());
        } catch (Exception e){
            return new ErrorResult(e.getMessage());
        }
    }

    public static HttpResult stopAgent(String idxStr) {
        int idx = Integer.parseInt(idxStr);
        if(idx == -1){
            String errMsg = "Got idx of -1";
            System.err.println(errMsg);
            return new ErrorResult(errMsg);
        } else {
            AgentActions.stopAgent(idx);
            return new HttpResult("OK");
        }
    }

    public static HttpResult stopAgents(String idxsStr){
        try {
            ArrayList<Integer> idxs = new ObjectMapper().readValue(idxsStr, ArrayList.class);
            for(int i=0; i<idxs.size(); i++){
                AgentActions.stopAgent(idxs.get(i));
            }
            return new HttpResult("OK");
        } catch (JsonGenerationException | JsonMappingException e) {
            e.printStackTrace();
            return new ErrorResult(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return new ErrorResult(e.getMessage());
        } catch (Exception e){
            return new ErrorResult(e.getMessage());
        }
    }
}
