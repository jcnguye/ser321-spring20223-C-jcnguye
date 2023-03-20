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

    public static void main(String args[]) throws Exception {
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

        do {
            try {
                // read from the server
                if (serverSock.isClosed()) {
                    serverSock = new Socket(host, port);
                }

                if (flag1 != 0) {
//                    BufferedReader stdin1 = new BufferedReader(new InputStreamReader(System.in));
//                    String strToSend1 = stdin1.readLine();
                    // connect to the server
//                    try {
                    Scanner scanner = new Scanner(System.in);
                    int choice = scanner.nextInt(); //should wait here why is it not waiting
                    // write to the server
                    out = serverSock.getOutputStream();
                    // user entered an integer
                    switch (choice) {
                        case (1):
                            op = Request.newBuilder()
                                    .setOperationType(Request.OperationType.LEADER)
                                    .setName(strToSend).build();
                            op.writeDelimitedTo(out);
                            break;
                        case (2):
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
//                    } catch (NumberFormatException e) {
//                        System.out.println("Not a number");
//                    }


                    in = serverSock.getInputStream();

                }
                // print the server response.
//                if (op.hasOperationType() || response != null || response.getResponseType() != GREETING || response.getResponseType() != LEADER || response.getResponseType() != PLAY || response.getResponseType() != WON || response.getResponseType() != ERROR || response.getResponseType() != BYE) {
//                    response = Response.parseDelimitedFrom(in);
//                }
                response = Response.parseDelimitedFrom(in);//wait response here?
                if(response == null){
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
                                System.out.println(lead.getName() + ": " + lead.getWins());
                            }

                        op = null;
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
                    default:
                        System.out.println();
                        break;
                }

//                if(flag1 == 1) {
//                    BufferedReader stdin1 = new BufferedReader(new InputStreamReader(System.in));
//                    String strToSend1 = stdin1.readLine();
//                    try {
//                        int choice = Integer.parseInt(strToSend1);
//                        // user entered an integer
//                        switch (choice) {
//                            case (1):
//                                op = Request.newBuilder()
//                                        .setOperationType(Request.OperationType.LEADER)
//                                        .setName(strToSend).build();
//                                op.writeDelimitedTo(out);
//                                break;
//                            case (2):
//                                break;
//                            case (3):
//                                op = Request.newBuilder().setOperationType(Request.OperationType.QUIT).build();
//                                op.writeDelimitedTo(out);
//                                System.out.println("leaving game");
//                                break;
//                            default:
//                                System.out.println("Not a valid choice try again");
//                                break;
//                        }
//                    } catch (NumberFormatException e) {
//                        System.out.println("Not a number");
//                    }
//                }
//                if(response.getResponseType() == Response.ResponseType.GREETING){
//                    System.out.println(response.getMessage());
//                }

//                    System.out.println("* \nWhat would you like to do? \n 1 - to see the leader board \n 2 - to enter a game \n 3 - quit the game");
//                BufferedReader stdin1 = new BufferedReader(new InputStreamReader(System.in));
//                String strToSend1 = stdin1.readLine();
//                try {
//                    int choice = Integer.parseInt(strToSend1);
//                    // user entered an integer
//                    switch (choice) {
//                        case (1):
//                            System.out.println(response.getMessage());
//                            break;
//                        case (2):
//
//                            break;
//                        case (3):
//                            if (in != null) in.close();
//                            if (out != null) out.close();
//                            serverSock.close();
//                            flag = false;
//                            System.exit(0);
//                            break;
//                        default:
//                            System.out.println("Not a valid choice try again");
//                            break;
//                    }
//                } catch (NumberFormatException e) {
//                    System.out.println("Not a number");
//                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) in.close();
                if (out != null) out.close();
                if (serverSock != null) serverSock.close();
            }
        } while (flag);
//                if(flag1 == 0){
//                    flag1++;
//                }

    }
}


