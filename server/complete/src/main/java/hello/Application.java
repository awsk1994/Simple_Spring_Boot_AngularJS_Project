package hello;

//import org.springframework.web.sockets.WebSocketSession;
import hello.Utils.SessionIdFileHandler;
import org.json.simple.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;

@SpringBootApplication
public class Application {
    public static final Logger logger = LogManager.getLogger(Application.class);
    public static VadMain main;
    public static ArrayList<WebSocketSession> sockets = new ArrayList<>();     // Session to the UI. sockets.sendMessage(new TextMessage(<STRING MESSAGE>)

    public static void main(String[] args) {
        logger.debug("Application Started...");
        SpringApplication.run(Application.class, args);
        main = new VadMain();
    }

    // Below is configuration for CORS to work properly. (TODO: More research into this is needed)
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/agents/info").allowedOrigins("*");
                registry.addMapping("/agents/active/info*").allowedOrigins("*");
                registry.addMapping("/agents/*/info*").allowedOrigins("*");
                registry.addMapping("/agent/start").allowedOrigins("*");
                registry.addMapping("/agent/stop").allowedOrigins("*");
                registry.addMapping("/agents/start").allowedOrigins("*");
                registry.addMapping("/agents/stop").allowedOrigins("*");
                registry.addMapping("/agent/add").allowedOrigins("*");
                registry.addMapping("/shutdown").allowedOrigins("*");
                registry.addMapping("/sockets/send").allowedOrigins("*");
                registry.addMapping("/sockets/sample-agent").allowedOrigins("*");
                registry.addMapping("/sockets/sample-agents").allowedOrigins("*");
                registry.addMapping("/sockets/sample-socket-start-agent").allowedOrigins("*");
                registry.addMapping("/sockets/sample-socket-stop-agent").allowedOrigins("*");
                registry.addMapping("/sockets/sample-socket-update-all-agents").allowedOrigins("*");
            }
        };
    }

    public static boolean isSocketReadyToUse(){
        return (sockets.size() > 0);
    }

    public static void echoToAllSockets(String msg){
        try{
            for(int i=0; i<sockets.size();i++){
                sockets.get(i).sendMessage(new TextMessage(msg));
            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }

}
