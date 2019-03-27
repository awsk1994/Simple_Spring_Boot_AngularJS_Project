package hello;

/* Tutorial: https://hc.apache.org/httpcomponents-client-ga/index.html */
/* http://hc.apache.org/httpcomponents-client-ga/httpclient/examples/org/apache/http/examples/client/ClientWithResponseHandler.java */

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpStringGetResponseTester {
    private String url;

    public HttpStringGetResponseTester(String url){
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
        HttpGet httpget = new HttpGet(url);
        System.out.println("Executing request " + httpget.getRequestLine());

        ResponseHandler<String> responseHandler = new ResponseHandlerWrapper();
        String responseBody = httpclient.execute(httpget, responseHandler);

        System.out.println("----------------------------------------");
        System.out.println(responseBody);
    };
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


