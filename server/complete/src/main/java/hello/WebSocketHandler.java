package hello;

import hello.Http.SocketMessage;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;

public class WebSocketHandler extends AbstractWebSocketHandler {
    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        Application.sockets.add(session);
        try {
            session.sendMessage(new TextMessage(new SocketMessage("Connected", "").toJsonString()));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        int removeIdx = -1;
        if(Application.sockets.size() > 0){
            for(int i=0; i<Application.sockets.size(); i++){
                if(Application.sockets.get(i).equals(session)){
                    removeIdx = i;
                    break;
                }
            }
        }

        if(removeIdx != -1){
            Application.sockets.remove(removeIdx);
            System.out.println("Removing a socket session. Updated sockets size = " + Application.sockets.size());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        System.out.println("New Text Message Received");
        session.sendMessage(message);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
        System.out.println("New Binary Message Received");
        session.sendMessage(message);
    }
}
