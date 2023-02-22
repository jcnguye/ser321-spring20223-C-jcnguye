package Assignment3Starter;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.json.JSONObject;
import javax.swing.JDialog;
import javax.swing.WindowConstants;

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
public class ClientGui implements Assignment3Starter.OutputPanel.EventHandlers {
  JDialog frame;
  PicturePanel picturePanel;
  OutputPanel outputPanel;
  boolean gameStart = false;
  String currentMessage;
  Socket sock;
  OutputStream out;
  ObjectOutputStream os;
  BufferedReader bufferedReader;
  ObjectInputStream in;

  String host;
  int port;

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


    picturePanel.newGame(1);
    insertImage("img/hi.png",0,0);
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
  public void newGame(int dimension) {
    picturePanel.newGame(dimension);
    outputPanel.appendOutput("Started new game with a " + dimension + "x" + dimension + " board.");
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
        outputPanel.appendOutput("Inserting " + filename + " in position (" + row + ", " + col + ")");
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

  /**
   * Submit button handling
   * 
   * Change this to whatever you need
   */
  @Override
  public void submitClicked() {
    // An example how to update the points in the UI
    System.out.println("Submit clicked ");
//    outputPanel.setPoints(0);
    // Pulls the input box text
    String input = outputPanel.getInputText();
//    client(input);
    //client here
    try {
      sock = new Socket(host,port);
      os = new ObjectOutputStream(sock.getOutputStream());
      in = new ObjectInputStream(sock.getInputStream());

      JSONObject res = null;
      String s;
      try {
        s = (String) in.readObject();
        res = new JSONObject(s);
      } catch (Exception e) {
        System.out.println("Error reading object");
      }
      if(res.getInt("code") == 200 && res.getString("type").equals("response")&& res.has("message")){
        System.out.println(res.getString("message")); //ask user for username
        outputPanel.appendOutput(res.getString("message"));
        //wait here
      }
      if(res.getInt("code") == 200 && res.getString("type").equals("response")&& res.has("message")){
        res.put("code", 200);
        res.put("type","request");
        res.put("message",input);
        os.writeObject(res.toString());
        os.flush();
      }
      outputPanel.setInputText("");
    }catch(IOException e){
      e.printStackTrace();
    }
    //client here
//    outputPanel.setPoints(10);
    // if has input
    if (input.length() > 0) {
      // append input to the output panel
      outputPanel.appendOutput(input);
      // clear input text box
      outputPanel.setInputText("");
    }
  }
  public void client(String input){
    try {
      sock = new Socket(host,port);
      os = new ObjectOutputStream(sock.getOutputStream());
      in = new ObjectInputStream(sock.getInputStream());

      JSONObject res = null;
      String s;
      try {
        s = (String) in.readObject();
        res = new JSONObject(s);
      } catch (Exception e) {
        System.out.println("Error reading object");
      }
      if(res.getInt("code") == 200 && res.getString("type").equals("name")&& res.has("message")){
        System.out.println(res.getString("message")); //ask user for username
        outputPanel.appendOutput(res.getString("message"));
        //wait here
        res.put("code", 200);
        res.put("type","name");
        res.put("message",input);
        os.writeObject(res.toString());
        os.flush();
      }
      outputPanel.setInputText("");
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

  public static void main(String[] args) throws IOException {
    // create the frame
    try {
      ClientGui main = new ClientGui();
      main.setHost(args[1]);
      main.setPort(Integer.parseInt(args[0]));
//      main.client();

      // setup the UI to display on image
      // add images to the grid
//      main.submitClicked();
//      main.insertImage("img/Joker/quote3.png", 0, 0);
      // show the GUI dialog as modal
      main.show(true);// you should not have your logic after this. You main logic should happen whenever "submit" is clicked
    }catch (Exception e){e.printStackTrace();}
  }
}
