package hello.Runnables;

import hello.Agent.Agent;
import hello.Utils.SessionIdFileHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class CloseSessionRunnable implements Runnable{
    Agent agent;
    public static final String  API_URL = "https://api-c71.nice-incontact.com/InContactAPI/services/v11.0/";
    public static final String  AUTHORIZATION_HEADER = "Authorization";
    public static final String  AUTHORIZATION_BEARER_PREFIX = "Bearer ";
    public static final String  ACCEPT_HEADER = "Accept";
    public static final String  APPLICATION_JSON_VALUE = "application/json";

    public CloseSessionRunnable(Agent agent){
        this.agent = agent;
    }

    @Override
    public void run(){
        try {
            System.out.println(Thread.currentThread().getName() + " | Agent (idx=" + agent.getIdx() + ") | Starting CloseSessionRunnable.");

            closeSession();
            this.agent.setSessionId(null);
            this.agent.updateLastUpdated();

            System.out.println(Thread.currentThread().getName() + " | Agent (idx=" + agent.getIdx() + ") | Finished CloseSessionRunnable");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    void closeSession() throws InterruptedException, IOException {
        System.out.println("idx: " + this.agent.getIdx() + ", sessioN: " + this.agent.getSessionId());
        HttpURLConnection   urlConnection = (HttpURLConnection)new URL(API_URL+"agent-sessions/"+this.agent.getSessionId()).openConnection();

        // Set the required headers and connect
        urlConnection.setRequestProperty(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER_PREFIX + this.agent.getApiToken());
        urlConnection.setRequestProperty(ACCEPT_HEADER, APPLICATION_JSON_VALUE);
        urlConnection.setRequestMethod("DELETE");

        urlConnection.connect();
        // Read the response
        String response = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())).lines().collect(Collectors.joining());

        // TODO: the response comes back as a JSON document. Replace the following code with your preferred JSON package to
        // handle the response appropriately
        System.out.println(response);

        SessionIdFileHandler.removeAgent(this.agent.getIdx());
        this.agent.setSessionId(null);
    }
}
