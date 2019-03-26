# Simple SpringBoot + AngularJs Project

## Features
 - Using SpringBoot to host Rest APIs (CORS configured)
 - Using AngularJS as frontend

## Quick Start
1. Open terminal
2. cd server/complete
3. mvnw spring-boot:run
4. Open another terminal
5. cd client
6. npm install
7. npm start

## Simple Run
8. On browser, head to http://localhost:8000
9. Click on "Get Greeting" button. 
 - This triggers a GET request to the server and the result is displayed next to "Result:"

10. Click on "Run Test".
 - This triggers another GET request to the server, but the server will take 5 seconds to return. During this 5 seconds, you will see Test in Progress? to be true. Once the 5 seconds is over, you will get Test Result in form of {"status": "Done"} and Test in Progress? set to false.

## Documentations

Coming soon...

## Todo:
 - Server:
     - Multi-threading
     - Performance Opt.
     - Internet API calls:
        - Websocket
             - find a websocket js test
             - add logger to websocket


		free api test to test: https://jsonplaceholder.typicode.com/




 - Client:
     - css, bootstrap

 - General:
     - Tidy up files
