package hello.Runnables;

import hello.Agent.Agent;
import hello.Agent.AgentState;
import hello.Application;
import hello.Settings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;

public class GetAllAgentStateRunnable implements Runnable{

    public static final String  API_URL = "https://api-c71.nice-incontact.com/InContactAPI/services/v11.0/";
    public static final String  AUTHORIZATION_HEADER = "Authorization";
    public static final String  AUTHORIZATION_BEARER_PREFIX = "Bearer ";
    public static final String  ACCEPT_HEADER = "Accept";
    public static final String  APPLICATION_JSON_VALUE = "application/json";
    public static String ApiToken;

    @Override
    public void run(){
        try {
            while(Application.main.KeepPollingAgentsState) {     // Turned off when
                System.out.println(Thread.currentThread().getName() + " | " + System.currentTimeMillis() + " | Polling in " + Settings.POLLING_SLEEP_MS + " ms.");
                Thread.currentThread().sleep(Settings.POLLING_SLEEP_MS);

                System.out.println(Thread.currentThread().getName() + " | " + System.currentTimeMillis() + " | Polling all agents' state.");
                HashMap<Integer, AgentState> updatedAgentsState = pollAllAgentsState();
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                System.out.println(updatedAgentsState);
                if(updatedAgentsState.size() > 0){
                    Application.main.updateAgentsState(updatedAgentsState);     // Update UI with updated agents' state.
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            Application.logger.debug(e.getMessage());
            // TODO: Better Error Handling
        }
    }

    public static HashMap<Integer, AgentState> getAgentList(JSONObject agentsResponse) throws JSONException {
        ArrayList<Agent> agents = Application.main.Agents;
        HashMap<Integer,AgentState> AgentsWithStates =new HashMap<Integer,AgentState>();

        JSONArray agentsData = (JSONArray) agentsResponse.get("agentStates");
        for(int i = 0; i < agentsData.length(); i++)
        {
            JSONObject objects = agentsData.getJSONObject(i);
            Iterator key = objects.keys();
            while (key.hasNext()) {
                String k = key.next().toString();
                if(k.equals("agentId")){
                    int agentIdx = (Integer) objects.get(k);
                    String state = (String) objects.get("agentStateName");

                    for(int x=0; x<agents.size(); x++){

                        if(agents.get(x).getAgentId()==agentIdx){

                            switch (state) {
                                case "LoggedOut":
                                    AgentsWithStates.put(agents.get(x).getIdx(),AgentState.LOGOUT);
                                    break;
                                case "Available":
                                    AgentsWithStates.put(agents.get(x).getIdx(),AgentState.READY);
                                    System.out.println(agents.get(x).getAgentId()+"==============="+agentIdx);
                                    System.out.println(agentIdx+"-----------"+state);
                                    System.out.println(AgentsWithStates);

                                    break;
                                case "Unavailable":
                                    AgentsWithStates.put(agents.get(x).getIdx(),AgentState.NOT_READY);
                                    break;
                            }

                        }
                    }
                }
                //System.out.println("----->>>>Key : " + k + ", value : "
                //        + objects.getString(k));
            }
        }
        return AgentsWithStates;
    }

    private String getAgentApiToken(){
        ArrayList<Agent> agents = Application.main.Agents;
        for(int i=0; i<agents.size(); i++){
            if(agents.get(i).getApiToken() != null){
                return agents.get(i).getApiToken();
            }
        }
        return null;
    }

    public HashMap<Integer, AgentState> pollAllAgentsState() throws IOException, JSONException {

        System.out.println("///////////------"+getAgentApiToken());
        if(getAgentApiToken()!=null){
            String AgentStateUrl = API_URL+"agents/states";

            // Create a connection to the API using the appropriate base uri
            URLConnection  urlConnection = new URL(AgentStateUrl).openConnection();

            // Set the required headers and connect
            urlConnection.setRequestProperty(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER_PREFIX + getAgentApiToken());
            urlConnection.setRequestProperty(ACCEPT_HEADER, APPLICATION_JSON_VALUE);

            urlConnection.connect();
            // Read the response
            String response = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())).lines().collect(Collectors.joining());

            JSONObject responseData = new JSONObject(response);

            // TODO: the response comes back as a JSON document. Replace the following code with your preferred JSON package to
            // handle the response appropriately
            return getAgentList(responseData);

        }


        /*
            TODO: Access GetAllAgentsState API, and return a list of agents' state.
            TODO: Return: HashMap<AgentIdx, AgentState>
         */
        return new HashMap<>();
    }
}
