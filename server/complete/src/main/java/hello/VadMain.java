package hello;

import hello.Agent.Agent;
import hello.Agent.AgentActions;
import hello.Agent.AgentState;
import hello.Http.SocketMessage;
import hello.Runnables.GetAllAgentStateRunnable;
import hello.Utils.SessionIdFileHandler;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.socket.TextMessage;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VadMain {
    public static ArrayList<Agent> Agents;
    public static ExecutorService Executor;             // TODO: what happens if pool size reached? Should set a super high limit?
    public static boolean KeepPollingAgentsState;       // TODO: Should be its own thread - because even if pool size is reached, it should keep running.

    private Thread getAllAgentStateThread;

    JSONParser parser = new JSONParser();
    ObjectMapper mapper = new ObjectMapper();

    public VadMain(){
        Agents = this.parseAgentsFromJson(Settings.JSON_FILENAME);
        Executor = Executors.newFixedThreadPool(Settings.THREAD_POOL_SIZE);
        startGetAllAgentsStateThread();
    }

    /* Start up VadMain */
    void startGetAllAgentsStateThread(){
        KeepPollingAgentsState = true;
        getAllAgentStateThread = new Thread(new GetAllAgentStateRunnable(), "Get All Agents State Thread");
        getAllAgentStateThread.start();
    }

    ArrayList<Agent> parseAgentsFromJson(String filename){
        System.out.println("VadMain | Parse Agents.json");
        ArrayList<Agent> list = new ArrayList<>();

        try {
            JSONArray agents = (JSONArray) parser.parse(new FileReader(filename));
            for(int i=0; i<agents.size(); i++){
                Agent agentInfo = mapper.readValue(agents.get(i).toString(), Agent.class);
                agentInfo.setIdx(i);
                list.add(agentInfo);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    /* Agent State Change */
    public synchronized void updateAgentsState(HashMap<Integer, AgentState> states){
        System.out.println("VadMain | updateAgentStates()");
        ArrayList<Agent> updatedAgents = new ArrayList<>();

        Iterator it = states.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry agentPair = (Map.Entry)it.next();

            int updatedAgentIdx = (int)agentPair.getKey();
            AgentState updatedAgentState = (AgentState)agentPair.getValue();
            Agent updatedAgent = updateAgentIfChanged(updatedAgentIdx, updatedAgentState);
            if(updatedAgent != null){
                updatedAgents.add(updatedAgent);
            }

            it.remove(); // avoids a ConcurrentModificationException
        }

        sendUpdatedAgentsToSocket(updatedAgents);
    }

    Agent updateAgentIfChanged(int agentIdx, AgentState updatedAgentState){
        Agent agent = Agents.get(agentIdx);
        agent.updateLastUpdated();
        if(agent.getState() != updatedAgentState){
            agent.setState(updatedAgentState);
            if(agent.getState() == AgentState.LOGOUT){
                agent.turnOff();    // TODO: Confirm - do not implement turn_off yet.
            }
            return agent;
        } else {
            return null;
        }
    }

    void sendUpdatedAgentsToSocket(ArrayList<Agent> agents){
        System.out.println("VadMain | sendUpdatedAgentsToSocket.");

        try{
            if(Application.isSocketReadyToUse()){
                String jsonStr = new ObjectMapper().writeValueAsString(agents);
                Application.echoToAllSockets(new SocketMessage("existing-agent", jsonStr).toJsonString());
            } else {
                String err = "Socket not ready to use. Possible reason: no connected sessions.";
                System.out.println(err);
                // throw new Exception(err);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void close() throws InterruptedException {
        System.out.println("VadMain | Closing VADMain");

//        getAllAgentStateThread.interrupt();
//        System.out.println("VadMain | Closed GetAllAgentStateThread");

        for(Agent agent : Agents){
            agent.closeSession();
            agent.turnOff();
        }
        System.out.println("VadMain | Turning off all agents.");

//        Executor.shutdown();
//        System.out.println("VadMain | Shutted down ExecutorService.");
    }
}