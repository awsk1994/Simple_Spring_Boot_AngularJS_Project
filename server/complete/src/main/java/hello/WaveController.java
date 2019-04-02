package hello;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hello.Http.*;

@RestController
public class WaveController {
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/runTest")
    public HttpResult vadMain(@RequestParam(value="name", defaultValue="World") String name) {
        System.out.println("/runTest | " + System.currentTimeMillis());     // TODO: Better logging library usage required.
        new Wave(counter.incrementAndGet(), name).run();
        return new HttpResult("Done", "");   // HttpResult object will return the HTTP in the json form of {data: {status: <STATUS>, details: <DETAILS>}}
    }
}
