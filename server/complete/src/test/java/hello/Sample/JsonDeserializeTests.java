package hello.Sample;

import hello.Agent.Agent;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
public class JsonDeserializeTests {
    @Test
    public void serializeJsonStringToObj() {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "{\"age\":33,\"messages\":[\"msg 1\",\"msg 2\"],\"name\":\"mkyong\"}";

        try {
            User user = mapper.readValue(jsonInString, User.class);
            System.out.println("User response: ");
            System.out.println(user);

            if(user.age != 33){
                assert false;
            }
            if(user.messages.size() != 2){
                assert false;
            }
            if(!user.messages.get(0).equals("msg 1")){
                assert false;
            }
            if(!user.messages.get(1).equals("msg 2")){
                assert false;
            }
            if(!user.name.equals("mkyong")) {
                assert false;
            }
        } catch (JsonGenerationException e) {
            e.printStackTrace();
            assert false;
        } catch (JsonMappingException e) {
            e.printStackTrace();
            assert false;
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        };
    }

    @Test
    public void serializeAgentObj() {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "{\"username\":\"agent1\",\"password\":\"pw\",\"extension\":123}";

        try {
            Agent agent = mapper.readValue(jsonInString, Agent.class);
            System.out.println("Agent response: ");
            System.out.println(agent);

            assert (agent.getUsername().equals("agent1"));
            assert(agent.getPassword().equals("pw"));
            assert (agent.getExtension() == 123);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
            assert false;
        } catch (JsonMappingException e) {
            e.printStackTrace();
            assert false;
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        };
    }

    @Test
    public void convertStringToIntArray() {
        ObjectMapper mapper = new ObjectMapper();
        String str = "[1,2,3]";

        try {
            ArrayList<Integer> arr = mapper.readValue(str, ArrayList.class);
            System.out.println("Agent response: ");
            System.out.println(arr.size());
            System.out.println(arr.get(1));
        } catch (JsonGenerationException e) {
            e.printStackTrace();
            assert false;
        } catch (JsonMappingException e) {
            e.printStackTrace();
            assert false;
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        };
    }
}

class User {
    String name;
    int age;
    List<String> messages;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public List<String> getMessages() {
        return messages;
    }

    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", messages=" + messages +
                '}';
    }
};