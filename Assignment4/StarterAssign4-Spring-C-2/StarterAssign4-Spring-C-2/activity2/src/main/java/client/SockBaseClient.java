package client;

import java.net.*;
import java.io.*;

import org.json.*;

import buffers.RequestProtos.Request;
import buffers.ResponseProtos.Response;
import buffers.ResponseProtos.Entry;


import java.util.*;
import java.util.stream.Collectors;

import static buffers.ResponseProtos.Response.ResponseType.*;


class SockBaseClient {
    private enum States {
        // no game started
        GameRunning,
        // game is at menu
        GameMenu,
        // game fully initialized
        GameViktory,

    }

    private enum GuessState {
        Guess1,
        Guess2,
    }

    public static States gameState = States.GameMenu;
    public static GuessState guessState = GuessState.Guess1;

    public static void main(String[] args) throws Exception {
        Socket serverSock = null;
        OutputStream out = null;
        InputStream in = null;
        int port = 9099; // default port


        // Make sure two arguments are given
        if (args.length != 2) {
            System.out.println("Expected arguments: <host(String)> <port(int)>");
            System.exit(1);
        }
        String host = args[0];

        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] must be integer");
            System.exit(2);
        }

        // Ask user for username
        System.out.println("Please provide your name for the server.");

        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String strToSend = stdin.readLine();
        // Build the first request object just including the name

        Request op = Request.newBuilder()
                .setOperationType(Request.OperationType.NAME)
                .setName(strToSend).build();
        Response response = null;
        boolean flag = true;
        int flag1 = 0;
//            do {

        // connect to the server
        serverSock = new Socket(host, port);

        // write to the server
        out = serverSock.getOutputStream();

        in = serverSock.getInputStream();

        op.writeDelimitedTo(out); //write requests
        int tileRepeater = 0;
        String tile = "";
        do {
            try {
                // read from the server
//                if (serverSock.isClosed()) {
//                    serverSock = new Socket(host, port);
//                }

                if (flag1 != 0) {
//                    BufferedReader stdin1 = new BufferedReader(new InputStreamReader(System.in));
//                    String strToSend1 = stdin1.readLine();
                    // connect to the server
//                    try {
                    if (gameState == States.GameMenu) {
                        Scanner scanner = new Scanner(System.in);
                        int choice = scanner.nextInt(); //should wait here why is it not waiting
                        // write to the server
//                    out = serverSock.getOutputStream();
                        // user entered an integer
                        switch (choice) {
                            case (1):
//                            op = Request.newBuilder()
//                                    .setOperationType(Request.OperationType.LEADER).build();
//                            op.writeDelimitedTo(out);
                                Request op1 = Request.newBuilder()
                                        .setOperationType(Request.OperationType.LEADER)
                                        .setName(strToSend).build();
                                op1.writeDelimitedTo(out);
                                break;
                            case (2):
                                op = Request.newBuilder().setOperationType(Request.OperationType.NEW).build();
                                op.writeDelimitedTo(out);
                                System.out.println("Game start");
                                break;
                            case (3):
                                op = Request.newBuilder().setOperationType(Request.OperationType.QUIT).build();
                                op.writeDelimitedTo(out);
                                System.out.println("leaving game");
                                break;
                            default:
                                System.out.println("Not a valid choice try again");
                                break;
                        }
                    }

                }
                    if(gameState == States.GameRunning){
                        Scanner scanner = new Scanner(System.in);
                        String gameInput;
                        boolean flag2 = false;
                        do{
                            System.out.println("Enter game input\n");
                            gameInput = scanner.nextLine();
                            if((!gameInput.matches("[a-d][1-4]")) || gameInput.length() != 2){

                                System.out.println("Invalid game input\n");
                                System.out.println("Try again\n");
                            }else {
                                flag2 = true;
                            }
                        }while (flag2==false);
                        System.out.println("In game state");
                        if(guessState == GuessState.Guess1){
                            op = Request.newBuilder().setOperationType(Request.OperationType.TILE1).setTile(gameInput).build();
                            op.writeDelimitedTo(out);
                            guessState = GuessState.Guess2;
                        }else if (guessState == GuessState.Guess2){
                            op = Request.newBuilder().setOperationType(Request.OperationType.TILE2).setTile(gameInput).build();
                            op.writeDelimitedTo(out);
                            guessState = GuessState.Guess1;
                        }
                        response = Response.parseDelimitedFrom(in);//wait response here?
                    }

                    if(gameState != States.GameRunning){
                        response = Response.parseDelimitedFrom(in);//wait response here?
                    }


                if (response == null || !response.hasResponseType()) {
                    System.out.println("response is empty or null");
                }

                switch (response.getResponseType()) {
                    case GREETING:
                        if (flag1 == 0) {
                            System.out.println(response.getMessage());
                            System.out.println("* \nWhat would you like to do? \n 1 - to see the leader board \n 2 - to enter a game \n 3 - quit the game");
                            flag1++;
                        }
                        op = null;
                        break;
                    case LEADER:
                        for (Entry lead : response.getLeaderList()) {
                            System.out.println(lead.getName() + ": " + lead.getLogins());
                        }
                        op = null;
                        break;
                    case PLAY:
                        System.out.println("Player is playing");
                        System.out.println("Type enter to continue");
//                        response.getBoard();
                        System.out.println(response.getBoard());
                        System.out.println(response.getMessage());
                        gameState = States.GameRunning;
                        break;
                    case BYE:
                        System.out.println("Game exit");
                        System.out.println(response.getMessage());
                        if (in != null) in.close();
                        if (out != null) out.close();
                        serverSock.close();
                        flag = false;
                        System.exit(0);
                        break;
                    case ERROR:
                        System.out.println(response.getMessage());
                        guessState = GuessState.Guess1;
                    default:
                        System.out.println();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (flag);

    }
}


