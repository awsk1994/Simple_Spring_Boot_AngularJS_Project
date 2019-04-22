package hello.Agent;

import hello.Application;
import hello.HttpResponse.HttpResult;
import hello.HttpResponse.ErrorResult;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.TextMessage;

import hello.Http.SocketMessage;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class AgentController {
    private final AtomicLong counter = new AtomicLong();        // TODO: Add to logging as unique_id

    @GetMapping("/agents/info")
    public HttpResult GetAgentsInfo() {
        System.out.println("AgentController | GetAgentsInfo");
        return AgentHttpActions.getAgents();
    }

    @GetMapping("/agents/active/info")
    public HttpResult GetActiveAgentsInfo() {
        System.out.println("AgentController | GetActiveAgentsInfo");
        return AgentHttpActions.getActiveAgents();
    }

    @GetMapping("/agent/{idx}/info")
    public HttpResult GetAgentInfo(@PathVariable("idx") String idxStr) {
        System.out.println("AgentController | GetAgentInfo for idx = " + idxStr);
        return AgentHttpActions.getAgent(idxStr);
    }

    @PostMapping("/agent/start")
    public HttpResult startAgent(@RequestParam(value="idx", defaultValue="-1") String idxStr) {
        System.out.println("AgentController | startAgent | Starting agent idx, " + idxStr + ".");
        return AgentHttpActions.startAgent(idxStr);
    }

    @PostMapping("/agents/start")
    public HttpResult startAgents(@RequestParam(value="idxs", defaultValue="[]") String idxsStr) {
        System.out.println("AgentController | startAgent | Starting Agents idxs, " + idxsStr + ".");
        return AgentHttpActions.startAgents(idxsStr);
    }

    @PostMapping("/agent/stop")
    public HttpResult stopAgent(@RequestParam(value="idx", defaultValue="-1") String idxStr) {
        System.out.println("AgentController | stopAgent | Stopping Agents idx, " + idxStr + ".");
        return AgentHttpActions.stopAgent(idxStr);
    }

    @PostMapping("/agents/stop")
    public HttpResult stopAgents(@RequestParam(value="idxs", defaultValue="[]") String idxsStr) {
        System.out.println("AgentController | stopAgent | Stopping Agents idx, " + idxsStr + ".");
        return AgentHttpActions.stopAgents(idxsStr);
    }

    /* Dev Mode API */
    @PostMapping("/agent/add")
    public HttpResult AddAgent(@RequestParam(value="username", defaultValue="[No Name]") String username,
                                 @RequestParam(value="password", defaultValue="[No password]") String password,
                                 @RequestParam(value="extension", defaultValue="-1") String extension) {
        System.out.println("AgentController | AddAgent | Starting agent with username, " + username + ".");
        return AgentHttpActions.addAgent(username, password, Integer.parseInt(extension));
    }

    @PostMapping("/shutdown")
    public HttpResult ShutDown() {
        System.out.println("AgentController | Shutdown VAD");
        try{
            Application.main.close();
            return new HttpResult("OK");
        } catch (InterruptedException e){
            e.printStackTrace();
            return new ErrorResult(e.getMessage());
        }
    }

    /*
            Note that for the trigger sockets APIs to work, there must be some connection to the websocket to get it started.
            Eg. Using a ws client to connect to localhost:8080/sockets
     */
    @GetMapping("/sockets/send")
    public HttpResult SendSocketMsg(@RequestParam(value="msg", defaultValue="Testing...") String msg) {
        System.out.println("Trigger to send ws message : " + msg);
        try{
            if(Application.isSocketReadyToUse()){
                Application.echoToAllSockets(msg);
            } else {
                String err = "Websocket sockets state is not open. Possible reason: no connected sessions.";
                System.err.println(err);
                throw new Exception(err);
            }
            return new HttpResult("OK");
        } catch (Exception e){
            e.printStackTrace();
            return new ErrorResult(e.getMessage());
        }
    }

    @GetMapping("/sockets/sample-agent")
    public HttpResult SendSampleAgent(@RequestParam(value="idx", defaultValue="0") String idxStr) {
        System.out.println("Trigger to send sample agent (idx = " + idxStr + ") via ws message.");

        try{
            int idx = Integer.parseInt(idxStr);
            if(Application.isSocketReadyToUse()){
                Application.echoToAllSockets(Application.main.Agents.get(idx).toJsonString());
            } else {
                String err = "Websocket sockets state is not open. Possible reason: no connected sessions.";
                System.err.println(err);
                throw new Exception(err);
            }
            return new HttpResult("OK");
        } catch (Exception e){
            e.printStackTrace();
            return new ErrorResult(e.getMessage());
        }
    }

    @GetMapping("/sockets/sample-agents")
    public HttpResult SendSampleAgents() {
        System.out.println("Trigger to send sample agents via ws message.");

        try{
            if(Application.isSocketReadyToUse()){
                String jsonStr = new ObjectMapper().writeValueAsString(Application.main.Agents);
                Application.echoToAllSockets(jsonStr);
            } else {
                String err = "Websocket sockets state is not open. Possible reason: no connected sessions.";
                System.err.println(err);
                throw new Exception(err);
            }
            return new HttpResult("OK");
        } catch (Exception e){
            e.printStackTrace();
            return new ErrorResult(e.getMessage());
        }
    }

    @GetMapping("/sockets/sample-socket-start-agent")
    public HttpResult SendSampleStartAgent(@RequestParam(value="idx", defaultValue="-1") String idx) {
        System.out.println("Trigger to send sample start agent.");
        int agentIdx = Integer.parseInt(idx);

        if(agentIdx == -1){
            return new ErrorResult("Idx cannot be -1");
        }

        try{
            if(Application.isSocketReadyToUse()){
                AgentActions.startAgent(agentIdx);
                ArrayList<Agent> updatedAgents = new ArrayList<>();
                updatedAgents.add(Application.main.Agents.get(agentIdx));
                String agentStr = new ObjectMapper().writeValueAsString(updatedAgents);
                Application.echoToAllSockets(new SocketMessage("existing-agent", agentStr).toJsonString());
            } else {
                String err = "Websocket sockets state is not open. Possible reason: no connected sessions.";
                System.err.println(err);
                throw new Exception(err);
            }
            return new HttpResult("OK");
        } catch (Exception e){
            e.printStackTrace();
            return new ErrorResult(e.getMessage());
        }
    }

    @GetMapping("/sockets/sample-socket-stop-agent")
    public HttpResult SendSampleStopAgent(@RequestParam(value="idx", defaultValue="-1") String idx) {
        System.out.println("Trigger to send sample stop agent.");
        int agentIdx = Integer.parseInt(idx);

        if(agentIdx == -1){
            return new ErrorResult("Idx cannot be -1");
        }

        try{
            if(Application.isSocketReadyToUse()){
                AgentActions.stopAgent(agentIdx);
                ArrayList<Agent> updatedAgents = new ArrayList<>();
                Agent updatedAgent = Application.main.Agents.get(agentIdx);
                // updatedAgent.setState(AgentState.TURNED_OFF);
                updatedAgents.add(updatedAgent);
                String agentStr = new ObjectMapper().writeValueAsString(updatedAgents);
                Application.echoToAllSockets(new SocketMessage("existing-agent", agentStr).toJsonString());
            } else {
                String err = "Websocket sockets state is not open. Possible reason: no connected sessions.";
                System.err.println(err);
                throw new Exception(err);
            }
            return new HttpResult("OK");
        } catch (Exception e){
            e.printStackTrace();
            return new ErrorResult(e.getMessage());
        }
    }


    @GetMapping("/sockets/sample-socket-update-all-agents")
    public HttpResult SendSampleAllAgents() {
        System.out.println("Trigger to send sample stop agent.");

        try{
            if(Application.isSocketReadyToUse()){
                String agentsStr = new ObjectMapper().writeValueAsString(AgentActions.getActiveAgents());
                Application.echoToAllSockets(new SocketMessage("update-all-agents", agentsStr).toJsonString());
            } else {
                String err = "Websocket sockets state is not open. Possible reason: no connected sessions.";
                System.err.println(err);
                throw new Exception(err);
            }
            return new HttpResult("OK");
        } catch (Exception e){
            e.printStackTrace();
            return new ErrorResult(e.getMessage());
        }
    }
}
