package Assignment3Starter;

import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.net.*;
import java.io.*;
import java.util.*;

public class ServerTcp {
    public static Map<String, Integer> leaderboard = new HashMap<>();
    public static ArrayList<String> charList = new ArrayList<>();
    public static String currCharacter;
    public static JSONObject res = null;
    public static Map<Integer,String> charListPick = new HashMap<>();//when character already selected
    public static int score;
    public static int correctGuesses;
    public static int numTrys = 0;
    public static int wrongGuesses;
    public static String clientName;
    public static int id = 1;
    public static void loadsArray(){
        charList.add("captain america");//0
        charList.add("darth vader");//1
        charList.add("homer simpson");//2
        charList.add("jack sparrow");//3
        charList.add("joker");//4
        charList.add("tony stark");//5
        charList.add("wolverine");//7
    }
    /**
     * Sets the current chacters when random is used
     * @return a string random character
     */
    public static String randomChar(){
        loadsArray();
        Random random = new Random();
        int randomNumber = random.nextInt(7);
        String character = charList.get(randomNumber);
        currCharacter = character;
        return character;
    }

    public static JSONObject askUser() {
        JSONObject response = new JSONObject();
        response.put("code", 200);
        response.put("type", "demand");
        response.put("question", "name");
        response.put("message", "Enter your name");
        return response;
    }
    public static JSONObject errorReq() {
        JSONObject response = new JSONObject();
        response.put("code", 400);
        response.put("type", "error");
        response.put("message", "Not a valid option");
        return response;
    }
    public static int token;
    public static JSONObject writeReply(String input) throws IOException {
        JSONObject response = new JSONObject();
        response.put("code", 200);
        response.put("type", "welcome");
        response.put("token", token);
        token++;
        response.put("message", "welcome to my game " + input + ", type 'play' to play game or 'leader' to see leader board");
        leaderboard.put(input,score);
//        os.writeObject(response.toString());
//        os.flush();
        return response;
    }
    public static JSONObject gamePlay() throws IOException {
        JSONObject response = new JSONObject();
        response.put("type", "game");
        response.put("message", "game has started");
//        os.writeObject(response.toString());
//        os.flush();
        return response;
    }
    public static int count = 1;
    public static int moreCount = 1;
    public static JSONObject imageIterQuote(String Character) throws IOException {
        JSONObject json = new JSONObject();
            json.put("type", "imageQuote");
            json.put("characterName", Character);
            String filePath = "";
            if (count > 4) {
                count = 1;
                filePath = "img/" + Character.replace(" ", "_") + "/quote" + 4 + ".png";
                File file = new File(filePath);
                if (!file.exists()) {
                    System.err.println("Cannot find file: " + file.getAbsolutePath());
                    json.put("error", "Can not find file");
                    return json;
                }
                // Read in image
                BufferedImage img = ImageIO.read(file);
                byte[] bytes = null;
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    ImageIO.write(img, "png", out);
                    bytes = out.toByteArray();
                }
                if (bytes != null) {
                    Base64.Encoder encoder = Base64.getEncoder();
                    json.put("data", encoder.encodeToString(bytes));
                    json.put("message", "No more qutoes to this character");
                    return json;
                }
                if (!json.has("data")) {
                    json.put("data", "is missing");
                }
                return json;
            }
            filePath = "img/" + Character.replace(" ", "_") + "/quote" + count+ ".png";
            count++;
            File file = new File(filePath);
            if (!file.exists()) {
                System.err.println("Cannot find file: " + file.getAbsolutePath());
                json.put("errorMsg", "Can not find file");
                return json;
            }
            // Read in image
            BufferedImage img = ImageIO.read(file);
            byte[] bytes = null;
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                ImageIO.write(img, "png", out);
                bytes = out.toByteArray();
            }
            if (bytes != null) {
                Base64.Encoder encoder = Base64.getEncoder();
                json.put("data", encoder.encodeToString(bytes));
                return json;
            }
            if (!json.has("data")) {
                json.put("data", "is missing");
            }
            return json;
    }
    private enum States {
        // no game started
        GameRunning,
        // game is at menu
        GameMenu,
        // game fully initialized
        Gameloss,

        GameViktory,
    }
    public static States gameState = States.GameMenu;

    /**
     * gives a random quote to a character
     * @param Character
     * @return JsonObject with image data random quote from character
     * @throws IOException
     */
    public static JSONObject image(String Character, String type,int deduction) throws IOException {
        JSONObject json = new JSONObject();
        json.put("type", type);
        score = score - deduction;
        json.put("score", score);
        Random rand = new Random();
        int randomNum = rand.nextInt((4 - 1) + 1) + 1;
        String numRan = String.valueOf(randomNum);
        String filePath = "img/"+ Character.replace(" ", "_") +"/quote" + numRan +".png";
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("Cannot find file: " + file.getAbsolutePath());
            json.put("errorMsg", "Can not find file");
            return json;
        }
        // Read in image
        BufferedImage img = ImageIO.read(file);
        byte[] bytes = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", out);
            bytes = out.toByteArray();
        }
        if (bytes != null) {
            Base64.Encoder encoder = Base64.getEncoder();
            json.put("data", encoder.encodeToString(bytes));
            return json;
        }
        if(!json.has("data")){
            json.put("data", "is missing");
        }
        return json;
    }
    public static JSONObject imageScore(String Character, int point) throws IOException {
        JSONObject json = new JSONObject();
        json.put("type", "correct");
        json.put("characterName", Character);
        json.put("message", "you guessed right");
        score = point + score;
        json.put("score",score);
        Random rand = new Random();
        int randomNum = rand.nextInt((4 - 1) + 1) + 1;
        String num = String.valueOf(randomNum);
        Character = randomChar();
        String filePath = "img/"+ Character.replace(" ", "_") +"/quote" + num +".png";
        json.put("file", filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("Cannot find file: " + file.getAbsolutePath());
            json.put("errorMsg", "Can not find file");
            return json;
        }
        // Read in image
        BufferedImage img = ImageIO.read(file);
        byte[] bytes = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", out);
            bytes = out.toByteArray();
        }
        if (bytes != null) {
            Base64.Encoder encoder = Base64.getEncoder();
            json.put("data", encoder.encodeToString(bytes));
            return json;
        }
        if(!json.has("data")){
            json.put("data", "is missing");
        }
        return json;
    }
    public static long startTime;
    public static long elapseTime;;
    public static long timer = 30_000_000_000L;
    public static long targetTime = startTime + timer;
    public static void main(String[] args) throws IOException {

        gameState = States.GameMenu;
        ServerSocket serv = null;
        Socket sock;
        try {
            serv = new ServerSocket(Integer.parseInt(args[0]));
            System.out.println("Server ready for connections");

            System.out.println("Server waiting for a connection");
            do {
                sock = serv.accept(); //waiting here
                System.out.println("Client made connection");
                OutputStream out = sock.getOutputStream();
                InputStream in = sock.getInputStream();
                byte[] messageBytes = NetworkUtils.Receive(in);
                JSONObject message = JsonUtils.fromByteArray(messageBytes);
                String choice = "";
                choice = message.getString("type");
                if(gameState == States.GameMenu && !choice.equals("")){

                    switch (choice) {
                        case "start":
                            System.out.println("got type: " + message.getString("type"));
                            res = askUser();//ask for name
                            score = 0;
                            break;
                        case "name":
                            res = writeReply(message.getString("name"));//sends back there name
                            clientName = message.getString("name");
                            break;
                        case "play":
                            res = gamePlay();
                            gameState = States.GameRunning;
                            startTime = System.nanoTime();
                            break;
                        case "leaderboard":
                            message.put("board", new JSONObject(leaderboard));
                            message.put("type", "leaderboard");
                            res = message;
                            break;
                        case "exit":
                            message.put("type","exit game");
                            message.put("messsage", "exiting game");
                            leaderboard.put(clientName,score);
                            gameState = States.GameMenu;
                            score = 0;
                            res = message;
                            numTrys = 0;
                            break;
                        case "error":
                                res = errorReq();
                            break;
                        default:
                            System.out.println("No request made");
                            if(message.getString("type").equals("exit game")){
                                break;
                            }
                            if(choice.equals("next") || choice.equals("more")){
                                message.put("type","error");
                                message.put("message","you are still in menu type 'play' first to start");
                                res = message;
                                break;
                            }
                            res = errorReq();
                            break;
                    }
                }
                if((gameState == States.GameRunning && !choice.equals(""))){
                    System.out.println(currCharacter);
                    switch (choice) {
                        case "guess":
                            elapseTime = System.nanoTime() - startTime;
                            if(elapseTime >= targetTime){
                                if(correctGuesses >= 3){
                                    gameState = States.GameViktory;
                                    correctGuesses = 0;
                                    break;
                                }else{
                                    gameState = States.Gameloss;
                                    wrongGuesses = 0;
                                }
                                break;
                            }
                            if(message.getString("message").equals(currCharacter)){
                                if(numTrys == 1){
                                    res = imageScore(message.getString("message"),5);
                                } else if (numTrys == 2) {
                                    res = imageScore(message.getString("message"),3);
                                }else {
                                    res = imageScore(message.getString("message"),1);
                                }
                                numTrys++;
//                              res = imageScore(message.getString("message"),message.getInt("score"));
                                correctGuesses++;
                            }else{
                                if(elapseTime >= targetTime){
                                    if(correctGuesses >= 3){
                                        gameState = States.GameViktory;
                                        correctGuesses = 0;
                                        break;
                                    }else{
                                        gameState = States.Gameloss;
                                        wrongGuesses = 0;
                                    }
                                }else {
                                    numTrys++;
                                    message.put("type", "wrong");
                                    message.put("message", "wrong guess try again, or type 'next' for new character quote or 'more' for more qutoes to current character");
                                    res = message;
                                    wrongGuesses++;
                                }
                            }
                            break;
                        case "play":
                            numTrys++;
                            String character1 = randomChar();
                            charListPick.put(id,character1);
                            id++;
                            res = image(character1,choice,0);
                            currCharacter = character1;
                            System.out.println("current character set "+ currCharacter);
                            break;
                        case "more":
                            elapseTime = System.nanoTime() - startTime;
                            if(elapseTime >= targetTime){
                                if(correctGuesses >= 3){
                                    gameState = States.GameViktory;
                                    correctGuesses = 0;
                                    break;
                                }else{
                                    gameState = States.Gameloss;
                                    wrongGuesses = 0;
                                }
                                break;

                            }
                            if(moreCount > 4){
                                message.put("type","wrong");
                                message.put("message","no more quotes");
                                res = message;
                                break;
                            }
                            res = imageIterQuote(currCharacter);
                            moreCount++;
                            numTrys++;
                            break;
                        case "next":
                            elapseTime = System.nanoTime() - startTime;
                            if(elapseTime >= targetTime){
                                if(correctGuesses >= 3){
                                    gameState = States.GameViktory;
                                    correctGuesses = 0;
                                    break;
                                }else{
                                    gameState = States.Gameloss;
                                    wrongGuesses = 0;
                                }
                                    break;
                            }
                            String characters = randomChar();
                            res = image(characters, choice,2);
                            moreCount = 1;
                            break;
                        case "exit game":
                            message.put("type",choice);
                            message.put("messsage", "exiting game going back to menu");
                            score = message.getInt("score");
                            String name = message.getString("name");
                            leaderboard.put(name,score);
                            res = message;
                            gameState = States.GameMenu;
                            numTrys = 0;
                            break;
                        case "error":
                            res = errorReq();
                            break;
                        default:
                            if(message.getString("type").equals("player")){
                                System.out.println("game has began here line 212");
                                break;
                            }
                            System.out.println("No request made");
                            res = errorReq();
                            break;
                    }
                }
                if((gameState == States.GameViktory && !choice.equals(""))) {
                    message.put("type", "won");
                    message.put("message", "you won, heres your prize!!!!!,type 'play' to play again or leader to see leaderboard");
//                    score = message.getInt("score");
                    String name = message.getString("name");
                    leaderboard.put(name,score);
                    res = message;
                    gameState = States.GameMenu;
                    numTrys = 0;
                    score = 0;
                }
                if((gameState == States.Gameloss && !choice.equals(""))){
                    message.put("type", "loss");
                    message.put("message", "haha you lose your time as expired, type 'play' to play again or leader to see leaderboard");
//                    score = message.getInt("score");
//                    String name = message.getString("name");
//                    leaderboard.put(name,score);
                    res = message;
                    gameState = States.GameMenu;
                    numTrys = 0;
                    score = 0;
                }
                System.out.println(currCharacter);
                try {
                    if (!res.isEmpty()) {
                        byte[] output = JsonUtils.toByteArray(res);
                        NetworkUtils.Send(out, output);
                    } else {
                        res = errorReq();
                        byte[] output = JsonUtils.toByteArray(res);
                        NetworkUtils.Send(out, output);
                    }
                }catch (NullPointerException e2){
                    System.out.println(e2);
                }

            } while (true);
        }catch (NullPointerException e){
            System.out.println(e);
        }
    }
}

