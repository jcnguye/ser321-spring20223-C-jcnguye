package server;

import java.net.*;
import java.io.*;
import java.util.*;

import org.json.*;

import java.lang.*;

import buffers.RequestProtos.Request;
import buffers.RequestProtos.Logs;
import buffers.RequestProtos.Message;
import buffers.ResponseProtos.Response;
import buffers.ResponseProtos.Entry;

import static buffers.ResponseProtos.Response.ResponseType.*;

class SockBaseServer {
    static String logFilename = "logs.txt";

    ServerSocket serv = null;
    InputStream in = null;
    OutputStream out = null;
    Socket clientSocket = null;
    int port = 9099; // default port
    Game game;

    JSONObject leaderBoard = new JSONObject(); //keeps track of all new users and existing ones


    public SockBaseServer(Socket sock, Game game) {
        this.clientSocket = sock;
        this.game = game;
        try {
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();
        } catch (Exception e) {
            System.out.println("Error in constructor: " + e);
        }
    }


    public JSONObject readJsonLeaderFile() {
        JSONObject JSON = new JSONObject();
        try {

            FileReader reader = new FileReader("leaderboard.json");
            JSONTokener tokener = new JSONTokener(reader);
            JSON = new JSONObject(tokener);
            return JSON;

        } catch (FileNotFoundException E) {
            System.out.println("File not found");
        }
        return JSON;
    }

    public boolean isJsonFileEmpty() {

        File file = new File("leaderboard.json");

        return file.exists() && file.length() == 0;

    }

    public void writeToJson(String name, int wins, int logins) {
        JSONObject playerObj = new JSONObject();

        if (!isJsonFileEmpty()) {
            try {
                playerObj.put("Name", name);
                playerObj.put("Won", wins);
                playerObj.put("Login", logins);
                leaderBoard.put(name, playerObj);
                FileWriter file = new FileWriter("leaderboard.json");
                file.write(leaderBoard.toString());
                file.flush();
            } catch (IOException E) {
                E.printStackTrace();
            }
            System.out.println("Successfully wrote JSON object to file.");
        } else {//is empty
            playerObj.put("Name", name);
            playerObj.put("Won", wins);
            playerObj.put("Login", logins);
            leaderBoard.put(name, playerObj);
            try {
                FileWriter file = new FileWriter("leaderboard.json");
                file.write(leaderBoard.toString());
                file.flush();
            } catch (IOException E) {
                E.printStackTrace();
            }

        }

    }
//    public int numberFormat(char num,Response response){
//        int number = 0;
//        String c = "";
//        switch (num){
//            case ('1'):
//                return number = 2;
//                break;
//            case ('2'):
//                return number = 4;
//                break;
//            case ('3'):
//                return number = 6;
//                break;
//            case ('4'):
//                return number = 8;
//                break;
//            default:
//                System.out.println("Invalid column number\n");
//                response = Response.newBuilder()
//                        .setResponseType(ERROR)
//                        .setMessage("Invalid column number\n")
//                        .build();
//                try {
//                    response.writeDelimitedTo(out);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//                return 0;
//                break;
//        }
//        return 0;
//    }
    // Handles the communication right now it just accepts one input and then is done you should make sure the server stays open
    // can handle multiple requests and does not crash when the server crashes
    // you can use this server as based or start a new one if you prefer. 
    public void start() throws IOException {
        boolean flag = true;
        String name = "";
        System.out.println("Ready...");
        Response response = null;
        int row1 = 0;
        int col1 = 0;
        int row2 = 0;
        int col2 = 0;
        do {
            try {
                // read the proto object and put into new objct
                Request op = Request.parseDelimitedFrom(in);

                int flag1 = 0;
                if (!isJsonFileEmpty()) {//if not empty
                    JSONObject read = readJsonLeaderFile();
                    leaderBoard = read;
                }

                switch (op.getOperationType()) {
                    case NAME:
                        // get name from proto object
                        name = op.getName();
                        // writing a connect message to the log with name and CONNENCT
//                        writeToLog(name, Message.CONNECT);
                        System.out.println("Got a connection and a name: " + name);
                        response = Response.newBuilder()
                                .setResponseType(Response.ResponseType.GREETING)
                                .setMessage("Hello " + name + " and welcome. Welcome to a simple game of battleship. ")
                                .build();
                        response.writeDelimitedTo(out);
//                        if (isJsonFileEmpty()) {
//                            Player player = new Player(name, 0, 1);
//                            writeToJson(player.getName(), player.getWins(), player.getLogin());
//                        } else if (!leaderBoard.has(name)) { //if does not contain in leader is a new player
//                            Player player = new Player(name, 0, 1);
//                            writeToJson(player.getName(), player.getWins(), player.getLogin());
//                        } else {//updates leader board if they exist
//                            String name1 = leaderBoard.getJSONObject(name).getString("Name");
//                            int won = leaderBoard.getJSONObject(name).getInt("Won");
//                            int login = leaderBoard.getJSONObject(name).getInt("Login");
//                            login++;
//                            writeToJson(name1, won, login);
//                        }
                        break;
                    case NEW:
                        game.newGame(); // starting a new game
                        System.out.println("GAME HAS STARTED");
                        System.out.println("\n\nExample response:");
                        System.out.println("Type: " + response.getResponseType());
                        System.out.println("Board: \n" + response.getBoard());
                        System.out.println("Original Board: \n" + game.showOriginalBoard());
                        System.out.println("Eval: \n" + response.getEval());
                        System.out.println("Second: \n" + response.getSecond());
                        // Example on how you could build a simple response for PLAY as answer to NEW
                        response = Response.newBuilder()
                                .setResponseType(Response.ResponseType.PLAY)
                                .setBoard(game.getHiddenBoard()) // gets the hidden board
                                .setEval(false)
                                .setSecond(false)
                                .build();

                        response.writeDelimitedTo(out);
                        break;
                    case TILE1:
                        game.showOriginalBoard();
                        String tile = op.getTile();
                        row1 = tile.charAt(0) - 'a' + 1;
//                        col1 = (tile.charAt(1) - '0') * 2 - 1;
                        switch (tile.charAt(1)){
                            case ('1'):
                                col1 = 2;
                            break;
                            case ('2'):
                                col1 = 4;
                                break;
                            case ('3'):
                                col1 = 6;
                                break;
                            case ('4'):
                                col1 = 8;
                                break;
                            default:
                                System.out.println("Invalid column number\n");
                                response = Response.newBuilder()
                                        .setResponseType(ERROR)
                                        .setMessage("Invalid column number\n")
                                        .build();
                                response.writeDelimitedTo(out);
                                break;
                        }
                        System.out.println("row1: \t"+ row1);
                        System.out.println("Col: \t"+ col1);
                        if (!tile.matches("[a-d][1-4]")) {
                            System.out.println("Incorrect format\n");
                        }else if (tile.length() != 2){
                            System.out.println("Invalid input. Please enter a letter (a-d) followed by a number (1-4).\n");
                            response = Response.newBuilder()
                                    .setResponseType(ERROR)
                                    .setMessage("Invalid input. Please enter a letter (a-d) followed by a number (1-4).\n")
                                    .build();
                            response.writeDelimitedTo(out);
                        }
                        else if (row1 < 1 || row1 >= game.getRow() || col1 < 2 || col1 > game.getCol()) {
                            System.out.println("Not with in bounds\n");
                            System.out.println("Column size " + game.getCol());
                            System.out.println("Row size " + game.getRow());
                            response = Response.newBuilder()
                                    .setResponseType(ERROR)
                                    .setMessage("Not with in bounds\n")
                                    .build();
                            response.writeDelimitedTo(out);
                        }
                        else if (row1 > 1 || row1 < game.getRow() || col1 > 2 || col1 < game.getCol()){
                            System.out.println("tile location "+ game.getTile(row1,col1));
//                            System.out.println("Wrong tile temp flip: "+ game.tempFlipWrongTiles(row1,col1));
//                            game.replaceOneCharacter(row1,col1);
                            response = Response.newBuilder()
                                    .setResponseType(Response.ResponseType.PLAY)
                                    .setBoard(game.tempFlipWrongTiles(row1,col1)) // gets the hidden board
                                    .setEval(false)
                                    .setSecond(false)
                                    .setMessage(String.valueOf(game.getTile(row1,col1)))
                                    .build();
                            response.writeDelimitedTo(out);
                        }else{
                            System.out.println("Options not recognize\n");
                            response = Response.newBuilder()
                                    .setResponseType(ERROR)
                                    .setMessage("Options not recognize\n")
                                    .build();
                            response.writeDelimitedTo(out);
                        }
                        break;
                    case TILE2:
//                        game.showBoard();
                        System.out.println("Hidden board: "+game.getHiddenBoard());
                        System.out.println("Original board: "+game.showOriginalBoard());

                        String tile1 = op.getTile();
                        row2 = tile1.charAt(0) - 'a' + 1;
                        switch (tile1.charAt(1)){
                            case ('1'):
                                col2 = 2;
                                break;
                            case ('2'):
                                col2 = 4;
                                break;
                            case ('3'):
                                col2 = 6;
                                break;
                            case ('4'):
                                col2 = 8;
                                break;
                            default:
                                System.out.println("Invalid column number\n");
                                response = Response.newBuilder()
                                        .setResponseType(ERROR)
                                        .setMessage("Invalid column number\n")
                                        .build();
                                response.writeDelimitedTo(out);
                                break;
                        }
                        System.out.println("Tile input "+ tile1);
                        System.out.println("row2: "+ row2);
                        System.out.println("Co2: "+ col2);
                        if (!tile1.matches("[a-d][1-4]")) {
                            System.out.println("Incorrect format");
                        }else if (tile1.length() != 2){
                            System.out.println("Invalid input. Please enter a letter (a-d) followed by a number (1-4).\n");
                            response = Response.newBuilder()
                                    .setResponseType(ERROR)
                                    .setMessage("Invalid input. Please enter a letter (a-d) followed by a number (1-4).\n")
                                    .build();
                            response.writeDelimitedTo(out);
                        }
                        else if (row2 < 1 || row2 >= game.getRow() || col2 < 2 || col2 > game.getCol()) {
                            System.out.println("Not with in bounds\n");
                            response = Response.newBuilder()
                                    .setResponseType(ERROR)
                                    .setMessage("Not with in bounds\n")
                                    .build();
                            response.writeDelimitedTo(out);
                        }
                        else if (row2 > 1 || row2 < game.getRow() || col2 > 2 || col2 < game.getCol()){
                            System.out.println("tile location "+ game.getTile(row2,col2));
                            System.out.println("Original board: "+ game.showOriginalBoard());
                            System.out.println("Hidden Board: "+ game.getHiddenBoard());
                            if(game.matchFound(row1,col1,row2,col2)){
                                System.out.println("Match found\n");
                                game.replaceTwoCharacter(row1,col1,row2,col2);
                                response = Response.newBuilder()
                                        .setResponseType(PLAY)
                                        .setBoard(game.getHiddenBoard())
                                        .setEval(true)
                                        .setMessage("Match has been found\n")
                                        .build();
                                response.writeDelimitedTo(out);
                                row1 = 0;
                                row2 = 0;
                                col2 = 0;
                                col1 = 0;
                            }else{
                                System.out.println("Match has not been found\n");
                                game.tempFlipWrongTiles(row1,col1,row2,col2);
                                response = Response.newBuilder()
                                        .setResponseType(Response.ResponseType.PLAY)
                                        .setBoard(game.tempFlipWrongTiles(row1,col1,row2,col2)) // gets the hidden board
                                        .setMessage("Not a match") // gets the hidden board
                                        .setEval(false)
                                        .setSecond(false)
                                        .build();
                                response.writeDelimitedTo(out);
                                row1 = 0;
                                row2 = 0;
                                col2 = 0;
                                col1 = 0;
                            }
                        }
                        else{
                            System.out.println("Options not recognize\n");
                            response = Response.newBuilder()
                                    .setResponseType(ERROR)
                                    .setBoard(game.getHiddenBoard()) // gets the hidden board
                                    .setEval(false)
                                    .setSecond(false)
                                    .setMessage("Invalid option")
                                    .build();
                            response.writeDelimitedTo(out);
                        }

                        break;
                    case QUIT:
                        Response response3 = Response.newBuilder()
                                .setResponseType(Response.ResponseType.BYE)
                                .setMessage("Leaving menu good bye").build();
                        response3.writeDelimitedTo(out);
                        flag = false;
                        break;
                    case LEADER:

                        Response.Builder res = Response.newBuilder()
                                .setResponseType(Response.ResponseType.LEADER);

                        // building an Entry for the leaderboard
                        Entry leader = null;

                        for (String player : leaderBoard.keySet()) {
                            JSONObject playerObj = leaderBoard.getJSONObject(player);
                            String namePlayer = playerObj.getString("Name");
                            int win = playerObj.getInt("Won");
                            int login1 = playerObj.getInt("Login");
                            System.out.println(name + " - Won: " + win + " - Login: " + login1);
                            leader = Entry.newBuilder()
                                    .setName(namePlayer)
                                    .setWins(win)
                                    .setLogins(login1)
                                    .build();
                            res.addLeader(leader);

                        }
                        Response res1 = res.build();
                        res1.writeDelimitedTo(out);
                        break;
                    default:
                        System.out.println();
                        break;
                }

            } catch (Exception ex) {

                if (out != null){
                    out.close();
                    System.out.println("Output stream close");
                }
                if (in != null) {
                    in.close();
                    System.out.println("Input stream close");
                }
                if (clientSocket != null) {
                    clientSocket.close();
                    System.out.println("Client socket close");
                }
                break;
            }
        } while (flag);
        // Example how to start a new game and how to build a response with the board which you could then send to the server
        // LINES between ====== are just an example for Protobuf and how to work with the differnt types. They DO NOT
        // belong into this code as is!

        // ========= Example start
//            game.newGame(); // starting a new game
//
//            // Example on how you could build a simple response for PLAY as answer to NEW
//            Response response2 = Response.newBuilder()
//            .setResponseType(Response.ResponseType.PLAY)
//            .setBoard(game.getBoard()) // gets the hidden board
//            .setEval(false)
//            .setSecond(false)
//            .build();


//
//            // this just temporarily unhides, the "hidden" image in game is still the same
//            System.out.println("One flipped tile");
//            System.out.println(game.tempFlipWrongTiles(1,2));
//
//            System.out.println("Two flipped tiles");
//            System.out.println(game.tempFlipWrongTiles(1,2, 2, 4));
//
//            System.out.println("Flip for found match, hidden in game will now be changed");
//            // I flip two tiles here but it will NOT necessarily be a match, since I hard code the rows/cols here
//            // and the board is randomized
//            game.replaceOneCharacter(1,2);
//            game.replaceOneCharacter(2,4);
//            System.out.println(game.getBoard()); // shows the now not hidden tiles


        // On the client side you would receive a Response object which is the same as the one in line 73, so now you could read the fields
//            System.out.println("\n\nExample response:");
//            System.out.println("Type: " + response2.getResponseType());
//            System.out.println("Board: \n" + response2.getBoard());
//            System.out.println("Eval: \n" + response2.getEval());
//            System.out.println("Second: \n" + response2.getSecond());
//
//            // Creating Entry and Leader response
//            Response.Builder res = Response.newBuilder()
//            .setResponseType(Response.ResponseType.LEADER);
//
//            // building an Entry for the leaderboard
//            Entry leader = Entry.newBuilder()
//            .setName("name")
//            .setWins(0)
//            .setLogins(0)
//            .build();
//
//            // building another Entry for the leaderboard
//            Entry leader2 = Entry.newBuilder()
//            .setName("name2")
//            .setWins(1)
//            .setLogins(1)
//            .build();
//
//            // adding entries to the leaderboard
//            res.addLeader(leader);
//            res.addLeader(leader2);
//
//            // building the response
//            Response response3 = res.build();
//
//            // iterating through the current leaderboard and showing the entries
//
//            System.out.println("\n\nExample Leaderboard:");
//            for (Entry lead: response3.getLeaderList()){
//                System.out.println(lead.getName() + ": " + lead.getWins());
//            }

        // ========= Example end


//        finally {
//            if (out != null) out.close();
//            if (in != null) in.close();
//            if (clientSocket != null) clientSocket.close();
//        }
    }


    /**
     * Writing a new entry to our log
     *
     * @param name    - Name of the person logging in
     * @param message - type Message from Protobuf which is the message to be written in the log (e.g. Connect)
     * @return String of the new hidden image
     */
    public static void writeToLog(String name, Message message) {
        try {
            // read old log file 
            Logs.Builder logs = readLogFile();

            // get current time and data
            Date date = java.util.Calendar.getInstance().getTime();
            System.out.println(date);

            // we are writing a new log entry to our log
            // add a new log entry to the log list of the Protobuf object
            logs.addLog(date.toString() + ": " + name + " - " + message);

            // open log file
            FileOutputStream output = new FileOutputStream(logFilename);
            Logs logsObj = logs.build();

            // This is only to show how you can iterate through a Logs object which is a protobuf object
            // which has a repeated field "log"

            for (String log : logsObj.getLogList()) {

                System.out.println(log);
            }

            // write to log file
            logsObj.writeTo(output);
        } catch (Exception e) {
            System.out.println("Issue while trying to save");
        }
    }

    /**
     * Reading the current log file
     *
     * @return Logs.Builder a builder of a logs entry from protobuf
     */
    public static Logs.Builder readLogFile() throws Exception {
        Logs.Builder logs = Logs.newBuilder();

        try {
            // just read the file and put what is in it into the logs object
            return logs.mergeFrom(new FileInputStream(logFilename));
        } catch (FileNotFoundException e) {
            System.out.println(logFilename + ": File not found.  Creating a new file.");
            return logs;
        }
    }


    public static void main(String args[]) throws Exception {
        Game game = new Game();
        if (args.length != 2) {
            System.out.println("Expected arguments: <port(int)> <delay(int)>");
            System.exit(1);
        }
        int port = 9099; // default port
        int sleepDelay = 10000; // default delay
        Socket clientSocket = null;
        ServerSocket serv = null;

        try {
            port = Integer.parseInt(args[0]);
            sleepDelay = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port|sleepDelay] must be an integer");
            System.exit(2);
        }
        try {
            serv = new ServerSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }


        while (true) {
            System.out.println("Wait for connection");
            clientSocket = serv.accept();
            System.out.println("Connection made");
//            SockBaseServer server = new SockBaseServer(clientSocket, game, list, tracker);
            SockBaseServer server = new SockBaseServer(clientSocket, game);
            server.start();
        }


    }
}

