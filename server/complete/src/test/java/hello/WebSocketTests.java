package hello;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WebSocketTests {
    @Test
    public void basicWebSocketTests() {
        try {
            // Prep
            String wsUrl = "ws://echo.websocket.org";       // This server will echo anything we send it.
            DummyObj dummy = new DummyObj();
            ExampleClient ws = new ExampleClient(new URI(wsUrl), dummy);

            // Connect and Send messages
            ws.connect();
            ws.send("Message 0 - this message is sent too early - should get bounced back.");

            heartbeatForNSeconds(1);
            ws.send("Message number 1");

            heartbeatForNSeconds(1);
            ws.send("Message number 2");

            heartbeatForNSeconds(1);
            ws.send("Message number 3 - this message should not work - should get bounced back.");      // TODO: Where does this message end up?

            ws.close();

            // Testing
            if(dummy.getCounter() != 2){
                assert false;
            }

            if(!dummy.getLastMessage().equals("Message number 2")){
                assert false;
            }
        } catch (Exception e){
            e.printStackTrace();
            assert false;
        }
    }

    void heartbeatForNSeconds(int n) throws Exception {
        int count = 0;
        while(true){
            Thread.sleep(1000);
            System.out.println("Heartbeat count: " + count);
            if((++count) > n){ break; };
        }
    }


}

class DummyObj {
    int counter = 0;
    String lastMessage = "";

    void whenOpen(){
        System.out.println("Dummy whenOpen");
    }

    void whenMessage(String message){
        System.out.println("[" + (counter++) + "] Dummy whenMessage: " + message);
        this.lastMessage = message;
    }

    void whenClose(){
        System.out.println("Dummy whenClose");
    }

    public int getCounter() {
        return counter;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    @Override
    public String toString() {
        return "DummyObj{" +
                "counter=" + counter +
                ", lastMessage='" + lastMessage + '\'' +
                '}';
    }
}

class ExampleClient extends WebSocketClient {

    private DummyObj dummy;

    public ExampleClient( URI serverURI, DummyObj dummy) {
        super( serverURI );
        this.dummy = dummy;
    }

    @Override
    public void send(String m){
        if(this.isOpen()){
            super.send(m);
        } else {
            System.out.println("Cannot send - No connection opened. " + m);
        }
    }

    @Override
    public void onOpen( ServerHandshake handshakedata ) {
        //System.out.println( "WebSocket connection is open." );
        dummy.whenOpen();
    }

    @Override
    public void onMessage( String message ) {
        //System.out.println( "Received: " + message );
        dummy.whenMessage(message);
    }

    @Override
    public void onClose( int code, String reason, boolean remote ) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        System.out.println( "Connection closed by " + ( remote ? "remote peer" : "us" ) + " Code: " + code + " Reason: " + reason );
        dummy.whenClose();
    }

    @Override
    public void onError( Exception ex ) {
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }
}
