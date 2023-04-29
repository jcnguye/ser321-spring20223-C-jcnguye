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

## Task 2 inventing own service
### Implemented a coin and dice game where user picks to roll a dice or flip a coin
- [x] Service allows at least 2 different requests
- [x] Each request needs at least 1 input
- [x] Response returns different data for different requests
- [x] Response returns a repeated field
- [x] Data is held persistent on the server