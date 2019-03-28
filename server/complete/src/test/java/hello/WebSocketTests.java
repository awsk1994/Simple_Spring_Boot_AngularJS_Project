package hello;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger logger = LogManager.getLogger(WebSocketTests.class);

    @Test
    public void basicWebSocketTests() {
        try {
            // Prep
            String wsUrl = "ws://echo.websocket.org";       // This server will echo anything we send it.
            DummyObj dummy = new DummyObj();
            ExampleClient ws = new ExampleClient(new URI(wsUrl), dummy);

            // Connect and Send messages
            ws.connect();
            ws.send("Message number 0 (this message is sent too early - doesn't get echo-ed back)");

            heartbeatForNSeconds(1);
            ws.send("Message number 1");

            heartbeatForNSeconds(1);
            ws.send("Message number 2");

            heartbeatForNSeconds(1);
            ws.send("Message number 3 (this message might not echo back in time).");

            ws.close();

            // Testing
            logger.info("Last message is: " + dummy.getLastMessage() + "[ counter = " + dummy.getCounter() + "]");
            boolean gotLast2Messages = (dummy.getCounter() == 2 && dummy.getLastMessage().equals("Message number 2"));
            boolean gotLast3Messages = (dummy.getCounter() == 3 && dummy.getLastMessage().equals("Message number 3 (this message might not echo back in time)."));
            if(!(gotLast2Messages || gotLast3Messages)){
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
            logger.debug(" --- heartbeat count: " + count);
            if((++count) > n){ break; };
        }
    }


}

class DummyObj {
    int counter = 0;
    String lastMessage = "";
    private static final Logger logger = LogManager.getLogger(DummyObj.class);

    void whenOpen(){
        logger.debug("Dummy whenOpen");
    }

    void whenMessage(String message){
        logger.debug("[" + (counter++) + "] Dummy whenMessage: " + message);
        this.lastMessage = message;
    }

    void whenClose(){
        logger.debug("Dummy whenClose");
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
    private static final Logger logger = LogManager.getLogger(ExampleClient.class);

    private DummyObj dummy;

    public ExampleClient( URI serverURI, DummyObj dummy) {
        super( serverURI );
        this.dummy = dummy;
    }

    @Override
    public void send(String m){
        if(super.isOpen() && !super.isClosed() && !super.isClosing()){
            super.send(m);
        } else {
            logger.error("Cannot send - No connection opened. (Message = " + m + ").");     // TODO: should somehow resend this after a few seconds?
        }
    }

    @Override
    public void onOpen( ServerHandshake handshakedata ) {
        logger.debug( "WebSocket connection is open." );
        dummy.whenOpen();
    }

    @Override
    public void onMessage( String message ) {
        logger.debug( "Received: " + message );
        dummy.whenMessage(message);
    }

    @Override
    public void onClose( int code, String reason, boolean remote ) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        logger.debug( "Connection closed by " + ( remote ? "remote peer" : "us" ) + " Code: " + code + " Reason: " + reason );
        dummy.whenClose();
    }

    @Override
    public void onError( Exception ex ) {
        ex.printStackTrace();
        logger.error("FATAL error.");
        // if the error is fatal then onClose will be called additionally
    }

    @Override
    public void onClosing(int code, String reason, boolean remote){
        logger.debug("onClosing");
    }
}
