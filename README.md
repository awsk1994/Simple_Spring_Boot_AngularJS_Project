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
     
     - [HIGH] Connection Manager's GET thread -> what happens if they update person class rapidly? Synchronization problem?
         - this.person.statusUpdate(response.toString());       // TODO: throwing exception - need to figure out why.
         - update documentation on this
         - create a class out of this.
     
     - [HIGH] add nice diagram of the architecture.


     - Websocket 
            - need more research on correct way to test ws sockets.
            - add logger to websocket

	 - IMPORTANT FEATURES:
		 - should check ws connections are maintained every few seconds. Reconnecting automatically after some sleep time. (RMQ? Messaging queue?)
		 - running new threads and scaling?
		 - classes emit status?
		 
 - Client:
     - css, bootstrap

 - General:
     - Tidy up files


## Error Message notes
"error while storign mojo status maven" -> try removing the 'target' folder.

