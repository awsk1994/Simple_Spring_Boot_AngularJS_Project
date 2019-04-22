package hello;

import hello.Agent.Agent;
import hello.Agent.AgentState;
import hello.Runnables.LoginRunnable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
public class AgentTest {
    Agent agent;
    String username = "agent1";
    String password = "pw";
    int extension = 123;

    @Before
    public void setup() {
        System.out.println("Setting Up");

        // TODO: test more things - eg. agent login. - need Powermockito for static methods... but not working.

        agent = Mockito.spy(new Agent(username, password, extension));
    };

    @Test
    public void CheckInitAgentParameters() {
        System.out.println("CheckInitAgentParameters");
        assertThat(agent.getIdx()).isEqualTo(-1);
        assertThat(agent.getUsername()).isEqualTo(username);
        assertThat(agent.getPassword()).isEqualTo(password);
        assertThat(agent.getExtension()).isEqualTo(extension);
        assertThat(agent.getLastUpdated()).isEqualTo("none");
        assertThat(agent.getSessionId()).isEqualTo(null);
        assertThat(agent.getState()).isEqualTo(AgentState.TURNED_OFF);
    }

    @Test
    public void TestLoginAgent(){
        System.out.println("TestLoginAgent");
/*
        Mockito.doNothing().when(executor).execute(Mockito.mock(LoginRunnable.class));
        agent.login();
        assertThat(agent.getState()).isEqualTo(AgentState.TURNED_ON);
*/
        //Mockito.doReturn(null).when(Settings.class, "testMethod");
        //testMethod();
    }

    @Test
    public void TestTurnoff(){
        System.out.println("TestTurnoff");

        assertThat(agent.getState()).isEqualTo(AgentState.TURNED_OFF);
        assertThat(agent.getSessionId()).isEqualTo(null);
        // assertThat(agent.getLastUpdated()).isNotEqualTo("none");
    }
}