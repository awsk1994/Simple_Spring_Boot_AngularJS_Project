package hello.HttpResponse;

import hello.Agent.Agent;

import java.util.ArrayList;

public class AgentsResult extends HttpResult {
    private final ArrayList<Agent> agents;

    public AgentsResult(String status, ArrayList<Agent> agent) {
        super(status);
        this.agents = agent;
    }

    public String getStatus() {
        return super.getStatus();
    }

    public ArrayList<Agent> getAgents() {
        return agents;
    }
}
