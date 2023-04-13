package Assignment3Starter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;

import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * The ClientGui class is a GUI frontend that displays an image grid, an input text box,
 * a button, and a text area for status. 
 * 
 * Methods of Interest
 * ----------------------
 * show(boolean modal) - Shows the GUI frame with the current state
 *     -> modal means that it opens the GUI and suspends background processes. Processing still happens 
 *        in the GUI. If it is desired to continue processing in the background, set modal to false.
 * newGame(int dimension) - Start a new game with a grid of dimension x dimension size
 * insertImage(String filename, int row, int col) - Inserts an image into the grid
 * appendOutput(String message) - Appends text to the output panel
 * submitClicked() - Button handler for the submit button in the output panel
 * 
 * Notes
 * -----------
 * > Does not show when created. show() must be called to show he GUI.
 * 
 */
public class ClientGui implements Assignment3Starter.OutputPanel.EventHandlers{
  JDialog frame;
  PicturePanel picturePanel;
  OutputPanel outputPanel;
  boolean gameStart = false;
  String currentMessage;

  OutputStream out;
  ObjectOutputStream os;
  BufferedReader bufferedReader;
  ObjectInputStream in;

  String host;
  int port;
  Socket sock;
  int score = 0;

  public String name;
  public void setScore(int score){
    this.score = score;
  }
  public int getScore(){
    return this.score;
  }
  /**
   * Construct dialog
   */
  public ClientGui() throws IOException {
    frame = new JDialog();
    frame.setLayout(new GridBagLayout());
    frame.setMinimumSize(new Dimension(500, 500));
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    // setup the top picture frame
    picturePanel = new PicturePanel();
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.weighty = 0.25;
    frame.add(picturePanel, c);

    // setup the input, button, and output area
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 1;
    c.weighty = 0.75;
    c.weightx = 1;
    c.fill = GridBagConstraints.BOTH;
    outputPanel = new OutputPanel();
    outputPanel.addEventHandlers(this);
    frame.add(outputPanel, c);

    //make exit button
//    JButton button = new JButton("Click me");
//    button.addActionListener(new ButtonClickListener());
//    button.setBounds(50, 50, 100, 30);
//    frame.add(button);
    // make exit button
    // make exit button
  }
  /**
   * Shows the current state in the GUI
   * @param makeModal - true to make a modal window, false disables modal behavior
   */
  public void show(boolean makeModal) {
    frame.pack();
    frame.setModal(makeModal);
    frame.setVisible(true);
  }

  /**
   * Creates a new game and set the size of the grid 
   * @param dimension - the size of the grid will be dimension x dimension
   */
  public void newGame(int dimension) throws IOException {
    picturePanel.newGame(dimension);
    insertImage("img/hi.png",0,0);
//    outputPanel.appendOutput("Whats your name");
  }

  /**
   * Insert an image into the grid at position (col, row)
   * 
   * @param filename - filename relative to the root directory
   * @param row - the row to insert into
   * @param col - the column to insert into
   * @return true if successful, false if an invalid coordinate was provided
   * @throws IOException An error occured with your image file
   */
  public boolean insertImage(String filename, int row, int col) throws IOException {
    String error = "";
    try {
      // insert the image
      if (picturePanel.insertImage(filename, row, col)) {
      // put status in output
        return true;
      }
      error = "File(\"" + filename + "\") not found.";
    } catch(PicturePanel.InvalidCoordinateException e) {
      // put error in output
      error = e.toString();
    }
    outputPanel.appendOutput(error);
    return false;
  }
  String username;
  /**
   * Submit button handling
   * Change this to whatever you need
   */
  @Override
  public void submitClicked(){
    // An example how to update the points in the UI
    System.out.println("Submit clicked ");

    String input = outputPanel.getInputText();
    currentMessage = input;
    String message = currentMessage;
    if(input.length() > 0) {
      clientRun(message);
      outputPanel.setInputText("");
    }else{
      outputPanel.appendOutput("Missing input");
      outputPanel.setInputText("");
    }
  }

  int flag1 = 0;
  int token;
  public void imageDisplay(JSONObject response){
    System.out.println("Your image");
    Base64.Decoder decoder = Base64.getDecoder();
    byte[] bytes = decoder.decode(response.getString("data"));
    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
    try {
      picturePanel.insertImage(bais, 0, 0);
    } catch (PicturePanel.InvalidCoordinateException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }catch (RuntimeException e){
      System.out.println(e);
    }
  }
  public void clientRun(String input){

    try {
      Socket sock = new Socket(host,port);
      System.out.println("new connection made");

      OutputStream out = sock.getOutputStream();
      InputStream in = sock.getInputStream();
      System.out.println("client connected");

      JSONObject req = null;
      String input1 = input.toLowerCase();
      try {
        req = new JSONObject();
      } catch (Exception e) {
        System.out.println("Error reading object");
      }
      if(flag1 == 0){
        req.put("type", "name");
        req.put("name", input1);
        name = input1;
        flag1 = 1;
        NetworkUtils.Send(out, JsonUtils.toByteArray(req));


        byte[] responseBytes = NetworkUtils.Receive(in);
        JSONObject response = JsonUtils.fromByteArray(responseBytes);
        if(response.isEmpty()){
          outputPanel.appendOutput("No response from server");
          sock.close();
          out.close();
          in.close();
          return;
        }
        outputPanel.appendOutput(response.getString("message"));//sends back name
        token = response.getInt("token");
        System.out.println(token);
        username = response.getString("message");
        sock.close();
        out.close();
        in.close();
        return;
      }

      switch (input1){
        case "play":
          req.put("type", "play");
          req.put("message", "they want to play the game");
          score = 0;
          break;
        case "joker", "tony stark", "captain america", "wolverine", "homer simpson", "darth vader", "jack sparrow":
          req.put("type", "guess");
          req.put("score",getScore());
          req.put("score",score);
          req.put("name",name);
          req.put("message", input1);//check this again
          break;
        case "leader":
          req.put("type","leaderboard");
          break;
        case "next":
          req.put("type","next");
          break;
        case "more":
          req.put("type","more");
          break;
        case "exit game":
          req.put("type","exit game");
          req.put("message","game has ended");
          req.put("score",score);
          req.put("name",name);
          score = 0;
          break;
        default:
          if(req.isEmpty()) {
            System.out.println("error code made");
            req.put("type", "error");
            req.put("message", "not a valid option or does not exist, list of options available (menu - leader, play) (game - next, more)");
          }
          break;
      }
      if(req!=null){
        NetworkUtils.Send(out, JsonUtils.toByteArray(req));
        byte[] responseBytes = NetworkUtils.Receive(in);
        JSONObject response = JsonUtils.fromByteArray(responseBytes);
        if(response.isEmpty()){
          outputPanel.appendOutput("No response from server");
          sock.close();
          out.close();
          in.close();
          return;
        }
        String choice = "";
        choice = response.getString("type");
        switch (choice) {
          case "game":
            outputPanel.appendOutput(response.getString("message"));//says game has started
            break;
          case "next":
            imageDisplay(response);
            setScore(response.getInt("score"));
            outputPanel.setPoints(response.getInt("score"));

            break;
          case "more":
            imageDisplay(response);
            break;
          case "error":
            outputPanel.appendOutput(response.getString("message"));//says game has started
            break;
          case "play":
            imageDisplay(response);
            outputPanel.appendOutput("game has started");
            outputPanel.appendOutput("Type 'exit game' to exit out of game");
            break;
          case "imageQuote":
            imageDisplay(response);
            if(response.has("message")){
              outputPanel.appendOutput(response.getString("message"));
            }
            break;
          case "wrong":
            if(response.has("message")){
              outputPanel.appendOutput(response.getString("message"));
            }
            break;
          case "correct":
            if(response.has("message")){
              outputPanel.appendOutput(response.getString("message"));
              outputPanel.appendOutput(response.getString("characterName"));
              outputPanel.setPoints(response.getInt("score"));
              setScore(response.getInt("score"));
              insertImage(response.getString("file"),0,0);
            }
            break;
          case "leaderboard":
            System.out.println(response.get("type").toString());
            JSONObject boardJson = response.getJSONObject("board");
            outputPanel.appendOutput("LeaderBoard: "+ String.valueOf(boardJson));
//            outputPanel.appendOutput("LeaderBoard: "+ String.valueOf(boardJson));
            break;
          case "exit game":
            System.out.println(response.get("type").toString());
            outputPanel.appendOutput(response.getString("message"));
            outputPanel.appendOutput("back to menu");
            insertImage("img/questions.jpg",0,0);
            break;
          case "won":
            outputPanel.appendOutput(response.getString("message"));
            insertImage("img/win.jpg",0,0);
            break;
          case "loss":
            outputPanel.appendOutput(response.getString("message"));
            insertImage("img/lose.jpg",0,0);
            break;
          default:
            System.out.println("No message from server.");
            break;
        }

      }
      if(sock.isConnected()){
        sock.close();
        out.close();
        in.close();
      }
    }catch(IOException e){
      e.printStackTrace();
    }

  }
  int flag = 0;
  public void clientStart(String input){
    try {
      Socket sock = new Socket(host,port);
      System.out.println("new connection made");
      OutputStream out = sock.getOutputStream();
      InputStream in = sock.getInputStream();
      System.out.println("client connected");
      JSONObject req = null;
      try {
        req = new JSONObject();
      } catch (Exception e) {
        System.out.println("Error reading object");
      }

      switch (input){
        case "start":
            req.put("type", input);
            flag = 1;
          break;
        default:
          System.out.println(input);
          break;
      }
        if(req!=null){
          NetworkUtils.Send(out, JsonUtils.toByteArray(req));
          byte[] responseBytes = NetworkUtils.Receive(in);
          JSONObject response = JsonUtils.fromByteArray(responseBytes);
          String choice = "";
          choice = response.getString("type");
          switch (choice) {
          case "demand":
            outputPanel.appendOutput(response .getString("message"));//asking for name
            break;
          default:
            System.out.println("No message from server.");
            break;
          }
        }
      if(sock.isConnected()){
        sock.close();
        out.close();
        in.close();
      }
    }catch(IOException e){
      e.printStackTrace();
    }
  }
  /**
   * Key listener for the input text box
   * 
   * Change the behavior to whatever you need
   */
  @Override
  public void inputUpdated(String input) {
    if (input.equals("surprise")) {
      outputPanel.appendOutput("You found me!");
    }
  }
  public void setHost(String host){this.host = host;}
  public void setPort(int port){this.port = port;}

  public static void main(String[] args) throws IOException{
    // create the frame
      ClientGui main = new ClientGui();
      main.newGame(1);
      main.setHost(args[1]);
      main.setPort(Integer.parseInt(args[0]));
      main.clientStart("start");
      main.show(true);// you should not have your logic after this. Your main logic should happen whenever "submit" is clicked
  }
}
