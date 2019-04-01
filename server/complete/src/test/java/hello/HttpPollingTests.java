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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class HttpPollingTests {
    static int NUM_PERSONS = 5;
    static int MAX_HTTP_SESSIONS = 5;
    static int RUN_MAIN_METHOD_FOR_SECS = 7;
    static int GET_REQUEST_DELAY_SECS = 2;

    static ConnectionManager manager;

    @Test
    public void HttpPollingWithNotEnoughHttpSessions() {
        MAX_HTTP_SESSIONS = 1;
        manager = new ConnectionManager();      // initialize connection manager.

        Person[] persons = new Person[NUM_PERSONS];
        String urlBase = "https://jsonplaceholder.typicode.com/posts/";

        System.out.println(System.currentTimeMillis() + " | main | Start ");
        initializePersons(persons);
        triggerPersonsToSendGetRequest(persons, urlBase);
        startHeartbeat(RUN_MAIN_METHOD_FOR_SECS);
        System.out.println(System.currentTimeMillis() + " | main | Done ");

        manager.close();
    }

    @Test
    public void HttpPollingWithEnoughHttpSessions() {
        MAX_HTTP_SESSIONS = NUM_PERSONS;
        manager = new ConnectionManager();      // initialize connection manager.

        Person[] persons = new Person[NUM_PERSONS];
        String urlBase = "https://jsonplaceholder.typicode.com/posts/";

        System.out.println(System.currentTimeMillis() + " | main | Start ");
        initializePersons(persons);
        triggerPersonsToSendGetRequest(persons, urlBase);
        startHeartbeat(RUN_MAIN_METHOD_FOR_SECS);
        System.out.println(System.currentTimeMillis() + " | main | Done ");

        manager.close();
    }

    void initializePersons(Person[] persons){
        for(int i=0; i<persons.length; i++){
            persons[i] = new Person();
        }
    }

    void triggerPersonsToSendGetRequest(Person[] persons, String urlBase){
        for(int i=0; i<persons.length; i++){
            persons[i].getRequest(urlBase + i);
        }
    }

    void startHeartbeat(int seconds){
        int counter = 0;
        while(counter < seconds){
            try {
                Thread.sleep(1000);
                counter++;
                System.out.println("\t\t-Heartbeat -- " + counter);
            } catch (Exception e){
                e.printStackTrace();
                assert false;
            }
        }
    }

    class Person{
        void getRequest(String url){
            System.out.println(System.currentTimeMillis() + " | Person | Sending GET request to Connection Manager.");
            manager.sendGetRequest(this, url);
            System.out.println(System.currentTimeMillis() + " | Person | DONE");
        }

        void statusUpdate(String update){
            System.out.println(System.currentTimeMillis() + " | Person | New Status: " + update);
        }
    }

    class ConnectionManager{
        PoolingHttpClientConnectionManager cm;
        CloseableHttpClient httpClient;
        ArrayList<GetThread> getThreads;

        public ConnectionManager(){
            this.cm = new PoolingHttpClientConnectionManager();
            this.cm.setMaxTotal(MAX_HTTP_SESSIONS);

            this.httpClient = HttpClients.custom()
                    .setConnectionManager(this.cm)
                    .build();
            this.getThreads = new ArrayList<>();
        }

        void sendGetRequest(Person person, String url){
            System.out.println(System.currentTimeMillis() + " | Connection Manager | Received request for GET request to " + url);
            GetThread getThread = new GetThread(person, this.httpClient, new HttpGet(url));
            getThread.start();
            getThreads.add(getThread);
            System.out.println(System.currentTimeMillis() + " | Connection Manager | Spawned GetThread...");
        }

        void close(){
            for (int counter = 0; counter < getThreads.size(); counter++) {
                getThreads.get(counter).interrupt();
            }
        }
    }

    class GetThread extends Thread {
        private final CloseableHttpClient httpClient;
        private final HttpGet httpget;
        ResponseHandler<JSONObject> responseHandler;
        boolean keepGoing;
        Person person;

        public GetThread(Person person, CloseableHttpClient httpClient, HttpGet httpget) {
            this.httpClient = httpClient;
            this.httpget = httpget;
            this.responseHandler = new JsonResponseHandlerWrapper();
            this.keepGoing = true;
            this.person = person;
        }

        @Override
        public void run() {
            while(keepGoing){
                JSONObject response = executeRequest();
                this.person.statusUpdate(response.toString());       // TODO: throwing exception - need to figure out why.
            }
        }

        JSONObject executeRequest(){
            try {
                Thread.sleep(GET_REQUEST_DELAY_SECS * 1000);     // Simulate http request delay
                JSONObject response = httpClient.execute(httpget, responseHandler);
                System.out.println(System.currentTimeMillis() + " | GetThread | Get output.");
                return response;
            } catch (ClientProtocolException e) {
                // Handle protocol errors
                e.printStackTrace();
                assert false;
            } catch (IOException e) {
                // Handle I/O errors
                e.printStackTrace();
                assert false;
            } catch (InterruptedException e){
                System.out.println(System.currentTimeMillis() + " | GetThread | Thread interrupted.");
            } catch (Exception e){
                e.printStackTrace();
                assert false;
            }
            return null;
        }
    }

}
