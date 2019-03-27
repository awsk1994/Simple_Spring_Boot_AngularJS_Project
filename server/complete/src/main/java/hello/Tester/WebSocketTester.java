
package hello;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketTester {

    private String url;

    public void run(){
        String testWsUrl = "ws://echo.websocket.org";       // Echo websocket server.

        try {
            WebSocketWrapper ws = new WebSocketWrapper(new URI(testWsUrl)); // more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
            ws.connect();
            heartbeatForNSeconds(3);
            ws.close();
        } catch (Exception e){
            e.printStackTrace();
        };
    };

    public void heartbeatForNSeconds(int n) throws Exception {
        int count = 0;
        while(true){
            Thread.sleep(1000);
            System.out.println("Heartbeat count: " + count);
            if((++count) > n){ break; };
        };
    }
};
