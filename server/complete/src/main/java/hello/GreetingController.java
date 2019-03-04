package hello;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    @ResponseBody
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        System.out.println("/greetings | " + System.currentTimeMillis());
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    };
}
