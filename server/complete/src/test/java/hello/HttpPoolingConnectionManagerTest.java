package hello;

import hello.HttpResponseHandler.JsonResponseHandlerWrapper;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class HttpPoolingConnectionManagerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldRunHttpOnMultipleThreads() throws Exception {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();

        // URIs to perform GETs on
        String[] urisToGet = {
                "https://jsonplaceholder.typicode.com/posts/1",
                "https://jsonplaceholder.typicode.com/posts/2",
                "https://jsonplaceholder.typicode.com/posts/3",
                "https://jsonplaceholder.typicode.com/posts/4"
        };

        // create a thread for each URI
        GetThread[] threads = new GetThread[urisToGet.length];
        for (int i = 0; i < threads.length; i++) {
            HttpGet httpget = new HttpGet(urisToGet[i]);
            threads[i] = new GetThread(httpClient, httpget);
        }

        // start the threads
        for (int j = 0; j < threads.length; j++) {
            threads[j].start();
        }

        // join the threads
        for (int j = 0; j < threads.length; j++) {
            threads[j].join();
        }
    }

    static class GetThread extends Thread {

        private final CloseableHttpClient httpClient;
        private final HttpGet httpget;
        ResponseHandler<JSONObject> responseHandler;


        public GetThread(CloseableHttpClient httpClient, HttpGet httpget) {
            this.httpClient = httpClient;
            this.httpget = httpget;
            this.responseHandler = new JsonResponseHandlerWrapper();
        }

        @Override
        public void run() {
            try {
                JSONObject response = httpClient.execute(httpget, responseHandler);
                System.out.println("Output: " + response);
            } catch (ClientProtocolException e) {
                // Handle protocol errors
                e.printStackTrace();
                assert false;
            } catch (IOException e) {
                // Handle I/O errors
                e.printStackTrace();
                assert false;
            }
        }

    }
}
