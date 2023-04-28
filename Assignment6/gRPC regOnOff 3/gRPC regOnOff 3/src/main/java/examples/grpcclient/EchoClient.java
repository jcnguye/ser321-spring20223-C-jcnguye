package examples.grpcclient;

import com.google.protobuf.Empty;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;
import service.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

// just to show how to use the empty in the protobuf protocol
//     Empty empt = Empty.newBuilder().build();

/**
 * Client that requests `parrot` method from the `EchoServer`.
 */
public class EchoClient {
  private final EchoGrpc.EchoBlockingStub blockingStub;
  private final JokeGrpc.JokeBlockingStub blockingStub2Joke;
  private final RegistryGrpc.RegistryBlockingStub blockingStub3;
  private final RegistryGrpc.RegistryBlockingStub blockingStub4;
  private final HometownsGrpc.HometownsBlockingStub blockingStub5Home;

  /** Construct client for accessing server using the existing channel. */
  public EchoClient(Channel channel, Channel regChannel) {
    // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's
    // responsibility to
    // shut it down.

    // Passing Channels to code makes code easier to test and makes it easier to
    // reuse Channels.
    blockingStub = EchoGrpc.newBlockingStub(channel);
    blockingStub2Joke = JokeGrpc.newBlockingStub(channel);
    blockingStub3 = RegistryGrpc.newBlockingStub(regChannel);
    blockingStub4 = RegistryGrpc.newBlockingStub(channel);
    blockingStub5Home = HometownsGrpc.newBlockingStub(channel);
  }

  public void askServerToParrot(String message) {

    


    ClientRequest request = ClientRequest.newBuilder().setMessage(message).build();
    ServerResponse response;
    try {
      response = blockingStub.parrot(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e.getMessage());
      return;
    }
    System.out.println("Received from server: " + response.getMessage());
  }

  public void askForJokes(int num) {
    JokeReq request = JokeReq.newBuilder().setNumber(num).build();
    JokeRes response;


    try {
      response = blockingStub2Joke.getJoke(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
    System.out.println("Your jokes: ");
    for (String joke : response.getJokeList()) {
      System.out.println("--- " + joke);
    }
  }

  public void setJoke(String joke) {
    JokeSetReq request = JokeSetReq.newBuilder().setJoke(joke).build();
    JokeSetRes response;

    try {
      response = blockingStub2Joke.setJoke(request);


      System.out.println(response.getOk());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
    }
  }

  public void getNodeServices() {
    GetServicesReq request = GetServicesReq.newBuilder().build();
    ServicesListRes response;
    try {
      response = blockingStub4.getServices(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
    }
  }

  public void getServices() {
    GetServicesReq request = GetServicesReq.newBuilder().build();
    ServicesListRes response;
    try {
      response = blockingStub3.getServices(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
    }
  }

  public void findServer(String name) {
    FindServerReq request = FindServerReq.newBuilder().setServiceName(name).build();
    SingleServerRes response;
    try {
      response = blockingStub3.findServer(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
    }
  }

  public void findServers(String name) {
    FindServersReq request = FindServersReq.newBuilder().setServiceName(name).build();
    ServerListRes response;
    try {
      response = blockingStub3.findServers(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
    }
  }

  public void searchHometown(String city){
    System.out.println("Searching for " + city + "\n");
    HometownsSearchRequest request = HometownsSearchRequest.newBuilder().setCity(city).build();
    HometownsReadResponse response;
    try {
      response = blockingStub5Home.search(request);
      System.out.println("This persons name: "+ response.getHometowns(0).getName());
      System.out.println("Lives in a city called: "+ response.getHometowns(0).getCity());
      System.out.println("In a region: "+ response.getHometowns(0).getRegion() + "\n");
//      System.out.println(response.getHometowns(0));
    }catch (Exception e) {
      System.err.println("RPC failed: " + e);
    }
  }

  public void writeHometown(String city,String name, String region){
    System.out.println("Writing to hometown");
    Hometown.Builder hometown = Hometown.newBuilder();
    hometown.setCity(city).setName(name).setRegion(region);

    HometownsWriteRequest request = HometownsWriteRequest.newBuilder().setHometown(hometown).build();
    HometownsWriteResponse response;
    try {
      response = blockingStub5Home.write(request);
      if(response.getIsSuccess()){
        System.out.println("Successfully written hometown\n");
      }
    }catch (Exception e) {
      System.err.println("RPC failed: " + e);
    }
  }

  public void readHometown(){
    System.out.println("Reading hometown");
    Empty empty = Empty.newBuilder().build();
    HometownsReadResponse response;
    try {
      response = blockingStub5Home.read(empty);
      System.out.println("List of hometowns");
      for(Hometown hometown: response.getHometownsList()){
        System.out.println("Name: " + hometown.getName());
        System.out.println("Region: " + hometown.getRegion());
        System.out.println("City: " + hometown.getCity() + "\n");
      }
      System.out.println("----End of list----\n");
    }catch (Exception e) {
      System.err.println("RPC failed: " + e);
    }

  }
  //gradle
  public static void main(String[] args) throws Exception {
    if (args.length != 6) {
      System.out
          .println("Expected arguments: <host(String)> <port(int)> <regHost(string)> <regPort(int)> <message(String)> <regOn(bool)>");
      System.exit(1);
    }
    int port = 9099;
    int regPort = 9003;
    String host = args[0];
    String regHost = args[2];
    String message = args[4];
    try {
      port = Integer.parseInt(args[1]);
      regPort = Integer.parseInt(args[3]);
    } catch (NumberFormatException nfe) {
      System.out.println("[Port] must be an integer");
      System.exit(2);
    }

    // Create a communication channel to the server, known as a Channel. Channels
    // are thread-safe
    // and reusable. It is common to create channels at the beginning of your
    // application and reuse
    // them until the application shuts down.
    String target = host + ":" + port;
    ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS
        // to avoid
        // needing certificates.
        .usePlaintext().build();

    String regTarget = regHost + ":" + regPort;
    ManagedChannel regChannel = ManagedChannelBuilder.forTarget(regTarget).usePlaintext().build();
    try {

      // ##############################################################################
      // ## Assume we know the port here from the service node it is basically set through Gradle
      // here.
      // In your version you should first contact the registry to check which services
      // are available and what the port
      // etc is.

      /**
       * Your client should start off with 
       * 1. contacting the Registry to check for the available services
       * 2. List the services in the terminal and the client can
       *    choose one (preferably through numbering) 
       * 3. Based on what the client chooses
       *    the terminal should ask for input, eg. a new sentence, a sorting array or
       *    whatever the request needs 
       * 4. The request should be sent to one of the
       *    available services (client should call the registry again and ask for a
       *    Server providing the chosen service) should send the request to this service and
       *    return the response in a good way to the client
       * 
       * You should make sure your client does not crash in case the service node
       * crashes or went offline.
       */

      // Just doing some hard coded calls to the service node without using the
      // registry
      // create client
      EchoClient client = new EchoClient(channel, regChannel);

      // call the parrot service on the server
      client.askServerToParrot(message);
      boolean flag = true;
      // ask the user for input how many jokes the user wants
      do{
        BufferedReader reader = null;
        int choice = 0;
        boolean numChoice = false;
        System.out.println("Pick number between  1 - 4 to run services\n");
        System.out.println("1: Run joke service");
        System.out.println("2: Run hometown service");
        System.out.println("3: Run recipe service");
        System.out.println("4: Run registry service");
        while (!numChoice){
          try {
            reader = new BufferedReader(new InputStreamReader(System.in));
            choice = Integer.parseInt(reader.readLine());
            if((choice >= 1) && (choice <= 4) ){
              numChoice = true;
            }else {
              System.out.println("Number not with in bounds pick between 1 - 4\n");
            }
          }catch (NumberFormatException e){
            System.out.println("Not a number\n");
          }
        }
      switch (choice){
        case 1:
          reader = new BufferedReader(new InputStreamReader(System.in));
          // Reading data using readLine
          System.out.println("How many jokes would you like?"); // NO ERROR handling of wrong input here.
          String num = reader.readLine();
          // calling the joked service from the server with num from user input
          client.askForJokes(Integer.valueOf(num));
          // adding a joke to the server
          client.setJoke("I made a pencil with two erasers. It was pointless.");
          // showing 6 joked
          client.askForJokes(Integer.valueOf(6));

          // list all the services that are implemented on the node that this client is connected to

          System.out.println("Services on the connected node. (without registry)");
          client.getNodeServices(); // get all registered services
          break;
        case 2:
          client.searchHometown("Tampa");
          client.writeHometown("Orange County","Flint","West");
          client.readHometown();
          //implemented Hometown service here
          break;
        case 3:
          //implemented Recipe service here

          break;
        case 4:
          break;
        default:

      }
//
//        reader = new BufferedReader(new InputStreamReader(System.in));
//        // Reading data using readLine
//        System.out.println("How many jokes would you like?"); // NO ERROR handling of wrong input here.
//        String num = reader.readLine();
//
//        // calling the joked service from the server with num from user input
//        client.askForJokes(Integer.valueOf(num));
//
//        // adding a joke to the server
//        client.setJoke("I made a pencil with two erasers. It was pointless.");
//
//        // showing 6 joked
//        client.askForJokes(Integer.valueOf(6));
//
//        // list all the services that are implemented on the node that this client is connected to
//
//        System.out.println("Services on the connected node. (without registry)");
//        client.getNodeServices(); // get all registered services

        // ############### Contacting the registry just so you see how it can be done
        if (args[5].equals("true")) {
          // Comment these last Service calls while in Activity 1 Task 1, they are not needed and wil throw issues without the Registry running
          // get thread's services
          client.getServices(); // get all registered services

          // get parrot
          client.findServer("services.Echo/parrot"); // get ONE server that provides the parrot service

          // get all setJoke
          client.findServers("services.Joke/setJoke"); // get ALL servers that provide the setJoke service

          // get getJoke
          client.findServer("services.Joke/getJoke"); // get ALL servers that provide the getJoke service

          // does not exist
          client.findServer("random"); // shows the output if the server does not find a given service
        }
      }while (flag);
//      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//      // Reading data using readLine
//      System.out.println("How many jokes would you like?"); // NO ERROR handling of wrong input here.
//      String num = reader.readLine();
//
//      // calling the joked service from the server with num from user input
//      client.askForJokes(Integer.valueOf(num));
//
//      // adding a joke to the server
//      client.setJoke("I made a pencil with two erasers. It was pointless.");
//
//      // showing 6 joked
//      client.askForJokes(Integer.valueOf(6));
//
//      // list all the services that are implemented on the node that this client is connected to
//
//      System.out.println("Services on the connected node. (without registry)");
//      client.getNodeServices(); // get all registered services
//
//      // ############### Contacting the registry just so you see how it can be done
//      if (args[5].equals("true")) {
//        // Comment these last Service calls while in Activity 1 Task 1, they are not needed and wil throw issues without the Registry running
//        // get thread's services
//        client.getServices(); // get all registered services
//
//        // get parrot
//        client.findServer("services.Echo/parrot"); // get ONE server that provides the parrot service
//
//        // get all setJoke
//        client.findServers("services.Joke/setJoke"); // get ALL servers that provide the setJoke service
//
//        // get getJoke
//        client.findServer("services.Joke/getJoke"); // get ALL servers that provide the getJoke service
//
//        // does not exist
//        client.findServer("random"); // shows the output if the server does not find a given service
//      }

    } finally {
      // ManagedChannels use resources like threads and TCP connections. To prevent
      // leaking these
      // resources the channel should be shut down when it will no longer be used. If
      // it may be used
      // again leave it running.
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
      regChannel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }
}
