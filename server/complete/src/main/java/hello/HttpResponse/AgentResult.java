package hello.HttpResponse;

import hello.Agent.Agent;

public class AgentResult extends HttpResult {
    private final Agent agent;

    public AgentResult(String status, Agent agent) {
        super(status);
        this.agent = agent;
    }

    /* Response is based on how many get<Key> methods you have. */

    public String getStatus() {
        return super.getStatus();
    }

    public Agent getAgent() {
        return agent;
    }
}
