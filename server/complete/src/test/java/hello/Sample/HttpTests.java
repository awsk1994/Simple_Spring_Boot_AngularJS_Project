package hello.Sample;
import hello.HttpResponseHandler.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.*;
import org.apache.http.impl.client.CloseableHttpClient;

import org.json.simple.parser.JSONParser;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.codehaus.jackson.map.ObjectMapper;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.springframework.test.context.web.WebAppConfiguration;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
public class HttpTests {
    @Test
    public void shouldSendGetRequestAndSerializeResponse() {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            try {
                // Execute GET request
                String url = "https://jsonplaceholder.typicode.com/posts/1";
                HttpGet httpget = new HttpGet(url);
                System.out.println("Executing GET request " + httpget.getRequestLine());

                ResponseHandler<String> responseHandler = new StringResponseHandlerWrapper();
                String responseBody = httpclient.execute(httpget, responseHandler);
                System.out.println("String Response: " + responseBody);

                // Check Response
                String expectedOutput = "{\n" +
                        "  \"userId\": 1,\n" +
                        "  \"id\": 1,\n" +
                        "  \"title\": \"sunt aut facere repellat provident occaecati excepturi optio reprehenderit\",\n" +
                        "  \"body\": \"quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum\\nreprehenderit molestiae ut ut quas totam\\nnostrum rerum est autem sunt rem eveniet architecto\"\n" +
                        "}";

                if(!responseBody.equals(expectedOutput)){
                    assert false;
                }

                // Serialize
                Post post = new ObjectMapper().readValue(responseBody, Post.class);
                System.out.println("Serialized Response in Post Object form: " + post);
                if(post.userId != 1){
                    assert false;
                }
                if(post.id != 1){
                    assert false;
                }
                if(!post.title.equals("sunt aut facere repellat provident occaecati excepturi optio reprehenderit")){
                    assert false;
                }
                String expectedBody = "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto";
                if(!post.body.equals(expectedBody)){
                    assert false;
                }

            } catch (Exception e) {
                e.printStackTrace();
                assert false;
            } finally {
                httpclient.close();
            }
        } catch (Exception e){
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void shouldSendGetRequestAndConvertResponseToJsonObject(){
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            try {
                // Execute GET request
                String url = "https://jsonplaceholder.typicode.com/todos/1";
                HttpGet httpget = new HttpGet(url);
                System.out.println("Executing GET request " + httpget.getRequestLine());

                ResponseHandler<JSONObject> responseHandler = new JsonResponseHandlerWrapper();
                JSONObject jsonObj = httpclient.execute(httpget, responseHandler);
                System.out.println("Json Response in String format: " + jsonObj.toString());

                // Check Response
                String expectedJsonString = "{\"id\":1,\"completed\":false,\"title\":\"delectus aut autem\",\"userId\":1}\n";
                JSONAssert.assertEquals(expectedJsonString, jsonObj.toString(), JSONCompareMode.NON_EXTENSIBLE);
            } catch (Exception e) {
                e.printStackTrace();
                assert false;
            } finally {
                httpclient.close();
            };
        } catch (Exception e){
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void shouldSendPostRequestInNameValuePairFormat(){
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            try {
                // Prepare HttpPost
                String url = "https://jsonplaceholder.typicode.com/posts";  // This url will bounce back whatever we send it (in JSON format), but id is always 101.
                HttpPost httppost = new HttpPost(url);
                System.out.println("Executing Post request " + httppost.getRequestLine());

                // Prepare Body
                String inputUsername = "John";
                String inputPassword = "pass";
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", inputUsername));
                params.add(new BasicNameValuePair("password", inputPassword));
                httppost.setEntity(new UrlEncodedFormEntity(params));

                // Execute Post Request
                ResponseHandler<String> responseHandler = new StringResponseHandlerWrapper();
                String responseBody = httpclient.execute(httppost, responseHandler);
                System.out.println("String Response: " + responseBody);

                // Check Response
                JSONParser parser = new JSONParser();
                String expectedResponse = "{\"username\":\"" + inputUsername + "\",\"password\":\"" + inputPassword + "\",\"id\": 101}";
                JSONObject expectedJson = (JSONObject) parser.parse(expectedResponse);
                JSONObject responseJson = (JSONObject) parser.parse(responseBody);

                System.out.println("Expected Json: " + expectedJson.toString());
                System.out.println("Response Json: " + responseJson.toString());

                JSONAssert.assertEquals(expectedJson.toString(), responseJson.toString(), JSONCompareMode.NON_EXTENSIBLE);
            } catch (Exception e) {
                e.printStackTrace();
                assert false;
            } finally {
                httpclient.close();
            };
        } catch (Exception e){
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void shouldSendPostRequestInJsonFormat(){
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            try {
                // Prepare HttpPost
                String url = "https://jsonplaceholder.typicode.com/posts";  // This url will bounce back whatever we send it (in JSON format), but id is always 101.
                HttpPost httppost = new HttpPost(url);
                System.out.println("Executing Post request " + httppost.getRequestLine());

                // Prepare Body
                String inputJson = "{\"userId\":9,\"name\":\"JohnD\"}";
                StringEntity entity = new StringEntity(inputJson);
                httppost.setEntity(entity);
                httppost.setHeader("Accept", "application/json");
                httppost.setHeader("Content-type", "application/json");

                // Execute Post Request
                ResponseHandler<String> responseHandler = new StringResponseHandlerWrapper();
                String responseBody = httpclient.execute(httppost, responseHandler);
                System.out.println("String Response: " + responseBody);

                // Check Response
                JSONParser parser = new JSONParser();
                JSONObject expectedJson = (JSONObject) parser.parse(inputJson);
                expectedJson.put("id", 101);
                JSONObject responseJson = (JSONObject) parser.parse(responseBody);
                JSONAssert.assertEquals(expectedJson.toString(), responseJson.toString(), JSONCompareMode.NON_EXTENSIBLE);
            } catch (Exception e) {
                e.printStackTrace();
                assert false;
            } finally {
                httpclient.close();
            };
        } catch (Exception e){
            e.printStackTrace();
            assert false;
        }
    }
}

class Post {
    int userId;
    int id;
    String title;
    String body;

    public int getUserId() {
        return userId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Post{" +
                "userid=" + userId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
