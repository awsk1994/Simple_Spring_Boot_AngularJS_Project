package hello;

/* Tutorial: https://hc.apache.org/httpcomponents-client-ga/index.html */
/* https://examples.javacodegeeks.com/enterprise-java/apache-http-client/apache-http-client-example -> (4. Parsing JSON from Server Using ResponseHandler
) */

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class HttpJsonDeserializeGetResponseTester {
    private String url;

    public HttpJsonDeserializeGetResponseTester(String url){
        this.url = url;
    };

    public void run(){
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            try {
                execute(this.url, httpclient);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                httpclient.close();
            };
        } catch (Exception e){
            e.printStackTrace();
        }
    };

    public void execute(String url, CloseableHttpClient httpclient) throws Exception {
        HttpGet httpget = new HttpGet(url);
        System.out.println("Executing request " + httpget.getRequestLine());

        ResponseHandler<TodosObject> responseHandler = new JSONResponseHandlerWrapper();
        TodosObject todosObject = httpclient.execute(httpget, responseHandler);
        System.out.println(todosObject);
    };

    class JSONResponseHandlerWrapper implements ResponseHandler{
        public TodosObject handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
            int status = response.getStatusLine().getStatusCode();
            JSONObject returnData = new JSONObject();
            JSONParser parser = new JSONParser();
            ObjectMapper mapper = new ObjectMapper();

            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();

                try{
                    if(entity == null){
                        return null;
                    } else {
                        String jsonInString = EntityUtils.toString(entity);
                        TodosObject todos = mapper.readValue(jsonInString, TodosObject.class);
                        return todos;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }
        }
    }

};


class TodosObject{
    private int userId;
    private int id;
    private String title;
    private boolean completed;

    public int getUserId() {
        return userId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public java.lang.String toString() {
        return "TodosObject{" +
                "userId=" + userId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", completed=" + completed +
                '}';
    };
};