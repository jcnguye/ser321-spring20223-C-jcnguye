## Task 1
1. [x] (3 points) Must have: We need to be able to run the service node through "gradle
   runNode" which should use default arguments, and the client through "gradle runClientJava" using the correct default values to connect to the started service node!!!!
   If this does not work we will not run things.
2. [x] (20 points each service) Implement 2 from the 4 services that are given in the
   .proto files, choose from these 4 protos: recipe, weather, password, hometownsRead
   through the Protobuf files for more details on how the services are supposed to work. You can talk to your friends to coordinate which services you guys
   implement, so you can test each others nodes with your own clients. BUT do not
   exchange code!
3. [x] (8 points) Your client should let the user decide what they want to do with some
   nice terminal input easy to understand, e.g. first showing all the available services,
   then asking the user which service they want to use, then asking for the input the
   service needs. Good overall client that does not crash.
4. [x] (4 points) Give the option that we can run "gradle runClient -Phost=host -Pport=port
   -Pauto=1" which will run all requests on its own with input data you hardcode and
   give good output results and also of course shows what was called. This will call the
   server directly without using any registry. So basically shows your test cases running
   successfully. See video about Task 1 for more details.
5. [x] (5 points) Server and Client should be robust and not crash

## Task 2
1. MUST: Create a new version of your Node.java ==> NodeService.java and your
   EchoClient.java ==> Client.java. You should be able to call them through "gradle
   registerServiceNode" and "gradle runClient2" asking for the same parameters as the
   calls that were already in the given Gradle file for the original files. This call will use
   the Registry again, so is not the same as runClient from the previous task. These
   should use default values so that they connect correctly!
2. Turn the registry back on through the gradle command line flag.
3. Test this: Run your Registry, run your node (you need to provide the correct host
   and port of course) – you should set this as default values for us. You should see a
   println on the Registry side that the service is registered. If you do not, try to figure
   out what happened (or did not happen).
4. Now, you should run your Client (also with the parts included which you need to
   uncomment now) and see if it will find the registered services correctly.
5. (8 points) If all this works, adapt your client so it does not just call the service on
   the node you provide directly as was done in Task 1 but that the client can choose
   between all services registered on the Registry (in this case locally it will still just be
   your services. For testing purposes you can run a couple server nodes and register
   all of them to your local registry. You do not hard code which server to talk to
   anymore but use the following workflow:
   a) Client contacts Registry to check for available services
   b) Client shows all registered services it received from the registry in the terminal
   and the client can choose one (preferably through numbering)
   c) (You should basically have this already) Based on what the client chooses the
   terminal should ask for input
   d) The request should be sent to one of the available service nodes with the following workflow: 1) client should call the registry again and ask for a Server
   providing the chosen service 2) the returned server should then be used (so the
   ip and port), 3) should send the request to the server that was returned, 4)
   return the response in a good way to the client
   e) Make sure that your Client does not crash in case the Server did not respond
   or crashed. Make it as robust as possible.
