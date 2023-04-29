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
  private final ZodiacGrpc.ZodiacBlockingStub blockingStub6Zodiac;
  private final CoinDiceGrpc.CoinDiceBlockingStub blockingStub7CoinDice;

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
    blockingStub6Zodiac = ZodiacGrpc.newBlockingStub(channel);
    blockingStub7CoinDice = CoinDiceGrpc.newBlockingStub(channel);
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
      if(response.getIsSuccess()){
        System.out.println("This persons name: "+ response.getHometowns(0).getName());
        System.out.println("Lives in a city called: "+ response.getHometowns(0).getCity());
        System.out.println("In a region: "+ response.getHometowns(0).getRegion() + "\n");
      }else{
        System.out.println("Can not find " + city);
      }

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
      System.out.println("List of hometowns\n");
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

  public void signZodiac(String name,String month, int day){
    System.out.println("Adding sign");
    SignRequest request = SignRequest.newBuilder().setDay(day).setName(name).setMonth(month).build();
    SignResponse response;
    try {
      response = blockingStub6Zodiac.sign(request);
      System.out.println(response.toString() + "\n");
      System.out.println(response.getMessage());

    }catch (Exception e) {
      System.err.println("RPC failed: " + e);
    }
  }

  public void findZodiac(String sign){
    FindRequest request = FindRequest.newBuilder().setSign(sign).build();
    FindResponse response;
    try {
      response = blockingStub6Zodiac.find(request);
      System.out.println("List of zodiac entries\n");
      if(response.getIsSuccess()){
        for(ZodiacEntry zodiacEntry:response.getEntriesList()){
          System.out.println("Name: "+ zodiacEntry.getName());
          System.out.println("Month: "+ zodiacEntry.getMonth());
          System.out.println("Day: "+ zodiacEntry.getDay());
          System.out.println("Sign: "+ zodiacEntry.getSign() + "\n");
        }
      }else{
        System.out.println(response.getError());
      }

    }catch (Exception e) {
      System.err.println("RPC failed: " + e);
    }
  }

  public void flipCoin(int numFlip){
    System.out.println("Flipping coin");
    CoinFlipRequest request = CoinFlipRequest.newBuilder().setNumFlips(numFlip).build();
    CoinFlipResponse response;
    try {
      response = blockingStub7CoinDice.flipCoin(request);
      for(String s: response.getCoinList()){
        System.out.println(s);
      }
      System.out.println();
    }catch (Exception e){
      System.err.println("RPC failed: " + e);
    }
  }
  public void rollDice(int numRolls, int numSide){
    System.out.println("Rolling dice");
    DiceRollRequest request = DiceRollRequest.newBuilder().setNumRolls(numRolls).setNumSides(numSide).build();
    DiceRollResponse response;

    try {
      response = blockingStub7CoinDice.rollDice(request);
      for(Integer s: response.getDiceList()){
        System.out.println(s);
      }
      System.out.println();
    }catch (Exception e){
      System.err.println("RPC failed: " + e);
    }
  }

  //gradle
  public static void main(String[] args) throws Exception {
    String host = "";
    String regHost = "";
    String message = "";
    int port = 9099;
    int regPort = 9002;
    int auto = 0;
    if(args.length == 6){
      host = args[0];
      regHost = args[2];
      message = args[4];
    }else if(args.length == 3){
      host = args[0];
      port = Integer.parseInt(args[1]);
      auto = Integer.parseInt(args[2]);
      message = "hello";
      regHost = "localhost";
      System.out.println(auto );
    }else{
      System.out
              .println("Expected arguments: <host(String)> <port(int)> <regHost(string)> <regPort(int)> <message(String)> <regOn(bool)>");
      System.exit(1);
    }
    if(args.length == 6){
      try {
        port = Integer.parseInt(args[1]);
        regPort = Integer.parseInt(args[3]);
      } catch (NumberFormatException nfe) {
        System.out.println("[Port] must be an integer");
        System.exit(2);
      }
    }else if (args.length == 3){
      try {
        port = Integer.parseInt(args[1]);
      } catch (NumberFormatException nfe) {
        System.out.println("[Port] must be an integer");
        System.exit(2);
      }
    }


    // Create a communication channel to the server, known as a Channel. Channels
    // are thread-safe
    // and reusable. It is common to create channels at the beginning of your
    // application and reuse
    // them until the application shuts down.
    //localhost:8000
    String target = host + ":" + port;
    ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS
        // to avoid
        // needing certificates.
        .usePlaintext().build();
    //localhost:9002
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
      if(auto == 1){
        client.askServerToParrot(message);
        System.out.println("-----Home town-----\n");
        client.searchHometown("Phoenix");
        client.searchHometown("New York");
        client.readHometown();
        client.writeHometown("Scottsdale","Gard","West");
        client.searchHometown("Scottsdale");
        client.searchHometown("Scotale");
        client.readHometown();
        System.out.println("----- End of Home town testing -----\n");
        System.out.println("----- Zodiac -----\n");
        client.findZodiac("Pisces");
        client.signZodiac("Bill","May",15);
        client.findZodiac("Taurus");
        client.findZodiac("Taurusdfew");

        System.out.println("----- End of Zodiac -----\n");
        System.out.println("----- Own Service -----\n");
        client.flipCoin(6);
        client.rollDice(6,6);
        System.out.println("----- End of service -----\n");
      }else{
        client.askServerToParrot(message);
        boolean flag = true;
        // ask the user for input how many jokes the user wants
        do{
          BufferedReader reader = null;
          int choice = 0;
          boolean numChoice = false;
          System.out.println("Pick number between  1 - 5 to run services\n");
          System.out.println("1: Run joke service");
          System.out.println("2: Run hometown service");
          System.out.println("3: Run zodiac service");
          System.out.println("4: Run CoinDice service");
          System.out.println("5: Exit");
          while (!numChoice){
            try {
              reader = new BufferedReader(new InputStreamReader(System.in));
              choice = Integer.parseInt(reader.readLine());
              if((choice >= 1) && (choice <= 5) ){
                numChoice = true;
              }else {
                System.out.println("Number not with in bounds pick between 1 - 5\n");
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
              boolean flag1 =true;
              do{
                System.out.println("Pick number between  1 - 4 \n");
                System.out.println("1: Search for hometown");
                System.out.println("2: Write hometown");
                System.out.println("3: Read Hometown");
                System.out.println("4: Exit out of hometown service");
                boolean numChoice1 = false;
                int choice1 = 0;
                while (!numChoice1){
                  try {
                    reader = new BufferedReader(new InputStreamReader(System.in));
                    choice1 = Integer.parseInt(reader.readLine());
                    if((choice1 >= 1) && (choice1 <= 4) ){
                      numChoice1 = true;
                    }else {
                      System.out.println("Number not with in bounds pick between 1 - 4\n");
                    }
                  }catch (NumberFormatException e){
                    System.out.println("Not a number\n");
                  }
                }
                switch (choice1) {
                  case 1 -> {
                    reader = new BufferedReader(new InputStreamReader(System.in));
                    System.out.println("Enter city name to search\n");
                    String city = reader.readLine();
                    client.searchHometown(city);
                  }
                  case 2 -> {
                    reader = new BufferedReader(new InputStreamReader(System.in));
                    boolean flag2 = true;
                    String[] data;
                    do {
                      System.out.println("Enter city, name, and region where in format of ... City:Name:Region");
                      System.out.println("Example \t Orange County:Flint:West");
                      String input = reader.readLine();
                      data = input.split(":");
                      if (data.length != 3) {
                        System.out.println("Invalid size input\n");
                      } else {
                        flag2 = false;
                      }
                    } while (flag2);
                    client.writeHometown(data[0], data[1], data[2]);
                  }
                  case 3 -> client.readHometown();
                  case 4 -> flag1 = false;
                  default -> System.out.println("No valid options");
                }
              }while (flag1);

              break;
            case 3:
              //implemented Zodiac service here
              boolean flag2 =true;
              do {
                System.out.println("Pick number between  1 - 3 \n");
                System.out.println("1: Add a sign for person");
                System.out.println("2: Find a sign of person");
                System.out.println("3: Exit out of zodiac service");
                boolean numChoice1 = false;
                int choice1 = 0;
                while (!numChoice1) {
                  try {
                    reader = new BufferedReader(new InputStreamReader(System.in));
                    choice1 = Integer.parseInt(reader.readLine());
                    if ((choice1 >= 1) && (choice1 <= 3)) {
                      numChoice1 = true;
                    } else {
                      System.out.println("Number not with in bounds pick between 1 - 3\n");
                    }
                  } catch (NumberFormatException e) {
                    System.out.println("Not a number\n");
                  }
                }
                switch (choice1) {
                  case 1 -> {
                    reader = new BufferedReader(new InputStreamReader(System.in));
                    boolean flag3 = true;
                    String[] data;
                    do {
                      System.out.println("Enter name, month and day in format of name:month:day");
                      System.out.println("Example Lizzy:May:26");
                      String input = reader.readLine();
                      data = input.split(":");
                      if (data.length != 3) {
                        System.out.println("Invalid size input\n");
                      } else {
                        flag3 = false;
                      }
                    } while (flag3);
                    client.signZodiac(data[0], data[1], Integer.parseInt(data[2]));
                  }
                  case 2 -> {
                    System.out.println("Enter the sign you are trying to find\n");
                    reader = new BufferedReader(new InputStreamReader(System.in));
                    String sign = reader.readLine();
                    client.findZodiac(sign);
                  }
                  case 3 -> flag2 = false;
                  default -> System.out.println("No valid option");
                }
              }while (flag2);
              break;
            case 4:
              boolean flag3 = true;
              do {
                System.out.println("1: Flip coin");
                System.out.println("2: Roll dice");
                System.out.println("3: exit coin dice service");
                boolean numChoice1 = false;
                while (!numChoice1){
                  try {
                    reader = new BufferedReader(new InputStreamReader(System.in));
                    choice = Integer.parseInt(reader.readLine());
                    if((choice >= 1) && (choice <= 3) ){
                      numChoice1 = true;
                    }else {
                      System.out.println("Number not with in bounds pick between 1 - 2\n");
                    }
                  }catch (NumberFormatException e){
                    System.out.println("Not a number\n");
                  }
                }
                switch (choice) {
                  case 1 -> {
                    System.out.println("Enter number if times coin is flipped");
                    reader = new BufferedReader(new InputStreamReader(System.in));
                    int flip = Integer.parseInt(reader.readLine());
                    client.flipCoin(flip);
                  }
                  case 2 -> {
                    System.out.println("Enter number if times dice is rolled, and number of sides on dice in format <numRolls:numSides>");
                    reader = new BufferedReader(new InputStreamReader(System.in));
                    String input = reader.readLine();
                    String[] data = input.split(":");
                    client.rollDice(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
                  }
                  case 3 -> flag3 = false;
                  default -> System.out.println("No valid options");
                }

              }while (flag3);
              client.flipCoin(6);
              client.rollDice(6,6);
              break;
            case 5:
              flag = false;
              break;
            default:

          }

          // ############### Contacting the registry just so you see how it can be done
          if (args[5].equals("true")) {
            // Comment these last Service calls while in Activity 1 Task 1, they are not needed and wil throw issues without the Registry running
            // get thread's services
//            client.getServices(); // get all registered services
//
//            // get parrot
//            client.findServer("services.Echo/parrot"); // get ONE server that provides the parrot service
//
//            // get all setJoke
//            client.findServers("services.Joke/setJoke"); // get ALL servers that provide the setJoke service
//
//            // get getJoke
//            client.findServer("services.Joke/getJoke"); // get ALL servers that provide the getJoke service
//
//            // does not exist
//            client.findServer("random"); // shows the output if the server does not find a given service
          }
        }while (flag);
      }

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
