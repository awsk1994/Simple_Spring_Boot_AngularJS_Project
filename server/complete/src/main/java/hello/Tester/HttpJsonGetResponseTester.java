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

public class HttpJsonGetResponseTester {
    private String url;

    public HttpJsonGetResponseTester(String url){
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

        ResponseHandler<JSONObject> responseHandler = new JSONResponseHandlerWrapper();
        JSONObject jsonObj = httpclient.execute(httpget, responseHandler);

        System.out.println("----------------------------------------");

        String title = (String) jsonObj.get("title");
        System.out.println("The title is " + title + ".");
        System.out.println(jsonObj);
    };
};

class JSONResponseHandlerWrapper implements ResponseHandler{
    public JSONObject handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
        int status = response.getStatusLine().getStatusCode();
        JSONObject returnData = new JSONObject();
        JSONParser parser = new JSONParser();

        if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();

            try{
                if(entity == null){
                    returnData.put("status_code", "1");
                    returnData.put("error_message", "null Data Found");
                } else {
                    returnData = (JSONObject) parser.parse(EntityUtils.toString(entity));
                };
            } catch (ParseException | IOException e) {
                returnData.put("status_code", "1");
                returnData.put("error_message", e.getMessage());
            };
        } else {
            returnData.put("status_code", "1");
            returnData.put("error_message", "Unexpected response status: " + status);
        };
        return returnData;
    };
};


