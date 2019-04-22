package hello.Runnables;

import hello.Agent.Agent;
import hello.Agent.AgentState;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.Connection;

public class LoginRunnable implements Runnable{
    Agent agent;
    public static final String agentLoginPage = "https://home-c71.nice-incontact.com/inContact/Login.aspx";
    public static final String  API_URL = "https://api-c71.nice-incontact.com/InContactAPI/services/v11.0/";
    public static String viewState;
    public static String eventValidation;
    public static Integer agentId;
    public static String ApiToken;
    public static String agentSessionId;

    public static final String  AUTHORIZATION_HEADER = "Authorization";
    public static final String  AUTHORIZATION_BEARER_PREFIX = "Bearer ";
    public static final String  ACCEPT_HEADER = "Accept";
    public static final String  APPLICATION_JSON_VALUE = "application/json";

    public LoginRunnable(Agent agent){
        this.agent = agent;
    }

    @Override
    public void run(){
        try {
            System.out.println(Thread.currentThread().getName() + " | Agent (idx=" + agent.getIdx() + ") | Starting LoginRunnable.");

            // TODO: check agent's session id. If valid, need to attempt to re-connect to it.
            String sessionId = loginAgent();
            this.agent.updateLastUpdated();
            this.agent.setState(AgentState.STARTED);

            System.out.println(Thread.currentThread().getName() + " | Agent (idx=" + agent.getIdx() + ") | Finished LoginRunnable");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String loginAgent() throws InterruptedException {

        try {
            getAgentSession();
            changeAgentState("Available");
            this.agent.setState(AgentState.STARTED);

        } catch (Exception e){
            e.printStackTrace();
        }
        return agentSessionId;
        //Thread.currentThread().sleep(5 * 1000);     // TODO: dummy code - remove this.
        // TODO: Access NICE InContact website, login agent and create session.
        //return null;
    }

    public void getViewStateAgent() throws InterruptedException{
        try {
            Document document = Jsoup.connect(agentLoginPage).get();
            Element inputViewState = document.select("input[name=__VIEWSTATE]").first();
            viewState = inputViewState.attr("value");

            Element inputEventValidation = document.select("input[name=__EVENTVALIDATION]").first();
            eventValidation = inputEventValidation.attr("value");
            System.out.println("View State : "+viewState);

        }catch (Exception e) {
            System.err.println("For '" + agentLoginPage + "': " + e.getMessage());
        }
    }


    public void getAgentApiToken() throws InterruptedException{

        try {
            getViewStateAgent();
            Connection.Response res = Jsoup.connect(agentLoginPage)
                    .data("__EVENTTARGET", "ctl00$BaseContent$btnLogin")
                    .data("__VIEWSTATE", viewState)
                    .data("__VIEWSTATEGENERATOR", "C8D2F021")
                    .data("__EVENTVALIDATION", eventValidation)
                    .data("ctl00$BaseContent$tbxUserName", this.agent.getUsername())
                    .data("ctl00$BaseContent$tbxPassword", this.agent.getPassword())

                    // and other hidden fields which are being passed in post request.
                    .userAgent("Mozilla")
                    .timeout(0)
                    .method(Connection.Method.POST)
                    .execute();

            Document doc = res.parse();
            String accessToken = res.cookie("accessToken");
            Map<String, String> cookies = res.cookies();

            String ApiTokenData = cookies.get("apiToken");
            String[] token = ApiTokenData.split("&");
            String[] apiTokenArr = token[0].split("=");
            ApiToken = apiTokenArr[1];

            this.agent.setApiToken(apiTokenArr[1]);

            System.out.println("API TOKEN : " + ApiToken);

            String[] agentIdArr = token[1].split("=");
            agentId = Integer.parseInt(agentIdArr[1]);

            this.agent.setAgentId(agentId);

        }catch (Exception e) {
            System.err.println("For '" + agentLoginPage + "': " + e.getMessage());
        }
    }

    public void getAgentSession() throws InterruptedException{
        try {

            if(this.agent.getSessionId()==null){
                getAgentApiToken();

                String AgentSessionUrl = API_URL + "agent-sessions/join";
                String agentSessionData = "?stationPhoneNumber="+this.agent.getExtension()+"&inactivityTimeout=300&inactivityForceLogout=false&asAgentId=" + agentId;
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(AgentSessionUrl + agentSessionData).openConnection();

                // Set the required headers and connect
                urlConnection.setRequestProperty(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER_PREFIX + this.agent.getApiToken());
                urlConnection.setRequestProperty(ACCEPT_HEADER, APPLICATION_JSON_VALUE);

                // Post an empty request to the connection stream (forces a POST)
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(0);

                // Read the response
                int code = urlConnection.getResponseCode();
                System.out.println("Response Code :---->> " + code);
                if (code!=409) {
                    String response = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())).lines().collect(Collectors.joining());

                    // handle the response appropriately
                    System.out.println("\n\n\n>>------>>>>>" + response);
                    JSONObject sessionData = new JSONObject(response);
                    agentSessionId = (String) sessionData.get("sessionId");
                    this.agent.setSessionId(agentSessionId);
                    System.out.println("Session ID : " + this.agent.getSessionId());

                }
                else{
                    System.out.println("Session Not created");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean changeAgentState(String state) throws InterruptedException {

        if(this.agent.getSessionId()!=null){
            try{

                String AgentStateUrl = API_URL+"agent-sessions/"+this.agent.getSessionId()+"/state?state="+state;

                URLConnection  urlConnection = new URL(AgentStateUrl).openConnection();

                // Set the required headers and connect
                urlConnection.setRequestProperty(AUTHORIZATION_HEADER, AUTHORIZATION_BEARER_PREFIX +  ApiToken);
                urlConnection.setRequestProperty(ACCEPT_HEADER, APPLICATION_JSON_VALUE);

                // Post an empty request to the connection stream (forces a POST)
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(0);
                // Read the response
                String response = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())).lines().collect(Collectors.joining());

                // handle the response appropriately
                System.out.println("\n\n\n\n\n"+response);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return true;
    }
}
