package hello;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class JsonDeserializeTester {
    private String url;

    public void run(){
        ObjectMapper mapper = new ObjectMapper();

        try {
            String jsonInString = "{\"age\":33,\"messages\":[\"msg 1\",\"msg 2\"],\"name\":\"mkyong\"}";
            User user1 = mapper.readValue(jsonInString, User.class);
            System.out.println(user1);
            /* Expect: User [name=mkyong, age=33, messages=[msg 1, msg 2]] */
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        };
    };

};

class User {
    private String name;
    private int age;
    private List<String> messages;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public List<String> getMessages() {
        return messages;
    }

    public java.lang.String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", messages=" + messages +
                '}';
    }
};