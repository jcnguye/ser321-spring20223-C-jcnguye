package Assignment3Starter;

import org.json.JSONObject;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Base64;

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
  String host;
  int port;
  DatagramSocket sock;
  public void setHost(String host){
    this.host = host;
  }
  public void setPort(int port){
    this.port = port;
  }
  public String getHost(){return host;}
  public int getPort(){return port;}

  /**
   * Construct dialog
   */
  public ClientGui() {
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
    outputPanel.setPoints(10);

    // Pulls the input box text
    String input = outputPanel.getInputText();
//    runClient(input);
    // if has input
    if (input.length() > 0) {
      runClient(input);
      // append input to the output panel
      outputPanel.appendOutput(input);
      // clear input text box
      outputPanel.setInputText("");
    }else {
      outputPanel.appendOutput("no input");
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
  public static int flag = 0;
  public void runClient(String input){
    try {
      InetAddress address = InetAddress.getByName("localhost");
      sock = new DatagramSocket();

      JSONObject req = new JSONObject();
      if(flag == 0) {
        req.put("type", "greeting");
        req.put("data", input);
        flag = 1;
      }
      String in = input.toLowerCase();

      if(in.equals("picture")){
        req.put("type", "img");
        req.put("data","give me joker");
      }
      NetworkUtils.Send(sock, address, port, JsonUtils.toByteArray(req));
      if (req != null) {

//        NetworkUtils.Tuple messageTuple = NetworkUtils.Receive(sock);
        NetworkUtils.Tuple responseTuple = NetworkUtils.Receive(sock);
        JSONObject response = JsonUtils.fromByteArray(responseTuple.Payload);


        if(response.getString("type").equals("name")){
          outputPanel.appendOutput(response.getString("data"));
        }
        if(response.getString("type").equals("greeting")){
          outputPanel.appendOutput(response.getString("data"));
        }
        if(response.getString("type").equals("img")){
          imageDisplay(response);
          outputPanel.appendOutput("HERES JOKER!!");
        }else{
          outputPanel.appendOutput("No valid option - try 'picture' ");
        }

      }

      sock.close();
    }catch(IOException e){
      e.printStackTrace();
    }
  }
  public void runStart(){
    try {
      InetAddress address = InetAddress.getByName("localhost");
      sock = new DatagramSocket();

      JSONObject req =  new JSONObject();
      req.put("type","name");
      req.put("data","asking for name?");
      if(req != null){
        NetworkUtils.Send(sock, address, port, JsonUtils.toByteArray(req));
        NetworkUtils.Tuple responseTuple = NetworkUtils.Receive(sock);
        JSONObject response = JsonUtils.fromByteArray(responseTuple.Payload);

        if (response.getString("type").equals("error")) {
          outputPanel.appendOutput("invalid request");
        }else{
          outputPanel.appendOutput(response.getString("data"));
        }
      }

    }catch(IOException e){
      e.printStackTrace();
    }
  }
  public static void main(String[] args) throws IOException {
    // create the frame
    ClientGui main = new ClientGui();
    // setup the UI to display on image
    main.newGame(1);
    main.setHost(args[1]);
    main.setPort(Integer.parseInt(args[0]));

    main.runStart();
    // add images to the grid


  
    
    // show the GUI dialog as modal
    main.show(true); // you should not have your logic after this. You main logic should happen whenever "submit" is clicked
  }
}
