package hello.Agent;

import hello.Application;
import hello.Settings;

import java.net.URL;
import java.util.ArrayList;

public class AgentActions {

    public static Agent getAgent(String idxStr) throws Exception {
        int idx = Integer.parseInt(idxStr);
        if(idx == -1){
            throw new Exception("Agent idx = -1 is Invalid.");
        } else if(idx > Application.main.Agents.size()){
            throw new Exception("Cannot find agent idx. Requested agent idx > Agents.size()");
        } else {
            return Application.main.Agents.get(idx);
        }
    }

    public static ArrayList<Agent> getAgents(String type) throws Exception {
        if(type.equals("active")){
            return getActiveAgents();
        } else if(type.equals("saved")){
            return Application.main.Agents;
        } else {
            throw new Exception("Unknown agent type.");
        }
    }

    public static ArrayList<Agent> getActiveAgents(){
        ArrayList<Agent> agents = Application.main.Agents;
        ArrayList<Agent> activeAgents = new ArrayList<>();

        for(int i=0; i<agents.size(); i++){
            if(agents.get(i).getState() != AgentState.TURNED_OFF){
                activeAgents.add(agents.get(i));
            }
        }
        return activeAgents;
    }

    public static void startAgent(int idx){
        System.out.println("AgentActions | Start Agent process for idx = " + idx);
        try {
            Application.main.Agents.get(idx).login();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopAgent(int idx){
        System.out.println("AgentActions | Stop Agent process for idx = " + idx);
        Application.main.Agents.get(idx).logout();
    }
}
