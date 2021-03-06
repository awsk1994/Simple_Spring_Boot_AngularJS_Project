# Spring Boot Documentation

## How to start server?

1. cd complete
2. mvnw spring-boot:run

## Notes

src/main/java/hello: all the spring boot files

Application.java: Initial springBoot run file + CORS configuration

*Controller.java: REST end points.

Greeting/HttpResult/VadMain.java: Class Definitions.

## CORs
To allow CORS, you will need to have the path configured in Application.java

## Logging
Using Spring Boot Log4J to do logging.

Configuration file is located in: src/main/resource/log4j2.xml

*Logging hello package:*

```
    <Logger name="hello" level="debug" additivity="false">

        <AppenderRef ref="ConsoleAppender" />

        <AppenderRef ref="ApplicationAppender"/>

    </Logger>
```

*Logging the rest:*

```
    <Root level="info">

        <AppenderRef ref="ConsoleAppender" />

        <AppenderRef ref="FileAppender" />

    </Root>
 ```

In the application itself, to do logging:

```
    import org.apache.logging.log4j.LogManager;

    import org.apache.logging.log4j.Logger;

    ...

    private static final Logger logger = LogManager.getLogger(Application.class);

    ...

    logger.debug("Debugging log");

```

## WebSocket

Implementation referenced from http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts.

To test websocket, you might want to use "ws://echo.websocket.org", which automatically echos any message you send it.


## Http Request

Using Apache HttpClient Library for HTTP requests.
Using json-simple (by Google) for JSON Parsing.

## References:

https://www.callicoder.com/spring-boot-log4j-2-example/

https://github.com/callicoder/spring-boot-log4j-2-demo

http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts

https://www.baeldung.com/java-http-request

# TODO:

 - Better logging for API part.
 