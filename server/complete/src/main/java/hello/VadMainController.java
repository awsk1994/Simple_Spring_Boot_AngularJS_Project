package hello;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class VadMainController {

    private static final String template = "Running Test: %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/runTest")
    public HttpResult vadMain(@RequestParam(value="name", defaultValue="World") String name) {
        System.out.println("/runTest | " + System.currentTimeMillis());     // TODO: Better logging library usage required.
        VadMain vadMain = new VadMain(counter.incrementAndGet(), name);
        vadMain.run();  // VadMain will run, and finish in 5 seconds.
       return new HttpResult("Done");   // HttpResult object will return the HTTP in the json form of {data: <STATUS>}
    };
}
