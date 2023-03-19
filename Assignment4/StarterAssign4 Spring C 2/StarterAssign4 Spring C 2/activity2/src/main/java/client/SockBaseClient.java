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
        Response response;
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
                response = Response.parseDelimitedFrom(in);
                if(flag1 == 1) {
                    BufferedReader stdin1 = new BufferedReader(new InputStreamReader(System.in));
                    String strToSend1 = stdin1.readLine();
                    try {
                        int choice = Integer.parseInt(strToSend1);
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
                                if (in != null) in.close();
                                if (out != null) out.close();
                                serverSock.close();
                                flag = false;
                                System.out.println("leaving game");
                                System.exit(0);
                                break;
                            default:
                                System.out.println("Not a valid choice try again");
                                break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Not a number");
                    }
                }
                // print the server response.

                switch (response.getResponseType()) {
                    case GREETING:
                        if (flag1 == 0) {
                            System.out.println(response.getMessage());
                            System.out.println("* \nWhat would you like to do? \n 1 - to see the leader board \n 2 - to enter a game \n 3 - quit the game");
                            flag1++;
                        }
                        break;
                    case BYE:
                        System.out.println("Game exit");
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
            }
//            finally {
//                if (in != null) in.close();
//                if (out != null) out.close();
//                if (serverSock != null) serverSock.close();
//            }
        } while (flag);
//                if(flag1 == 0){
//                    flag1++;
//                }

    }
}


