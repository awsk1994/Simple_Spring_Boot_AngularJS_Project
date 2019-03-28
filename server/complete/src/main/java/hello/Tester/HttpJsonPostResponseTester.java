package hello;

/* https://www.baeldung.com/httpclient-post-http-request */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

public class HttpJsonPostResponseTester {
    private String url;

    public HttpJsonPostResponseTester(String url){
        this.url = url;
    };

    public void run(){
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            try {
                this.execute(this.url, httpclient);
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
        /* Http post with Parameter Body */
        HttpPost httppost1 = new HttpPost(url);
        System.out.println("Executing request " + httppost1.getRequestLine());

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", "John"));
        params.add(new BasicNameValuePair("password", "pass"));
        httppost1.setEntity(new UrlEncodedFormEntity(params));

        ResponseHandler<String> responseHandler = new ResponseHandlerWrapper();
        String responseBody = httpclient.execute(httppost1, responseHandler);

        System.out.println(responseBody);
        System.out.println("----------------------------------------");

        /* Http post with JSON body */
        HttpPost httppost2 = new HttpPost(url);
        System.out.println("Executing request " + httppost2.getRequestLine());

        String json = "{\"id\":1,\"name\":\"John\"}";
        StringEntity entity = new StringEntity(json);
        httppost2.setEntity(entity);
        httppost2.setHeader("Accept", "application/json");
        httppost2.setHeader("Content-type", "application/json");

        responseBody = httpclient.execute(httppost2, responseHandler);

        System.out.println(responseBody);
        System.out.println("----------------------------------------");
    };

    class ResponseHandlerWrapper<T> implements ResponseHandler{
        @Override
        public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        };
    };
};




