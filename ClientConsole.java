// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import client.*;
import common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Daria Przhebelska
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 * @version September 2020
 */
public class ClientConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;
  
  
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String host, int port) 
  {
	  // Create scanner object to read from console
	  fromConsole = new Scanner(System.in); 
	  
	  String log = fromConsole.nextLine();
	  String[] input = log.split(" ");
	  String loginID;
	  
	  
	  if (input[0].equals("#login")) {
		  loginID = input[1];
	  } else {
		  System.out.println("ERROR - No login ID specified.  Connection aborted.");
		  System.exit(0);
	  }
	  
	  try 
	  {
	      client= new ChatClient(host, port, this);
	      
	      
	  } 
	  catch(IOException exception) 
	  {
	      System.out.println("Error: Can't setup connection!"
	                + " Terminating client.");
	      System.exit(1);
	  }
    
    client.handleMessageFromClientUI(log);
    System.out.println("Connection open. Awaiting command");
  }
  
  /**
   * Constructs an instance of the ClientConsole UI.
   * This is used if the client puts their login id in the command arguments
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String host, int port, String login) 
  {

	  try 
	  {
	      client= new ChatClient(host, port, this);
	      
	  } 
	  catch(IOException exception) 
	  {
	      System.out.println("Error: Can't setup connection!"
	                + " Terminating client.");
	      System.exit(1);
	  }
    
    client.handleMessageFromClientUI(login);
    System.out.println("Connection open. Awaiting command");
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() 
  {
    try
    {

      String message;

      while (true) 
      {
        message = fromConsole.nextLine();
        String[] words = message.split(" ");
        if (!message.isEmpty()) {
        	if(message.startsWith("#")) { // checks if the input is a command
        		
        		switch (words[0]) { 
        		case "#quit": //closes the connection and exits the console
        			if (client.isConnected()) {
        				client.quit();
        			} 
        			System.exit(0);
        			break;
        		case "#logoff": //closes the connection
        			if (!client.isConnected()) {
        				System.out.println("Already disconnected");
        			} else {
        				client.quit();
        			}
        			break;
        		case "#sethost": //sets the host name
        			client.setHost(words[1]);
        			System.out.println("Connected to host " + client.getHost());
        			break;
        		case "#setport": //sets the port number
        			client.setPort(Integer.parseInt(words[1]));
        			System.out.println("Connected to port " + client.getPort());
        			break;
        		case "#login": //logs in
        			if (client.isConnected()) {
        			System.out.println("Already connected");
        			} else {
        				String host = client.getHost();
        				int port = client.getPort();
        				client= new ChatClient(host, port, this);
        				client.handleMessageFromClientUI(message + " " + words[1]);
        				client.openConnection();
        			}
        			break;
        		case "#gethost": //prints out the current host name
        			System.out.println(client.getHost());
        			break;
        		case "#getport": //prints out the current port number
        			System.out.println(client.getPort());
        		}
        	} else {
        		client.handleMessageFromClientUI(message);
        	}
        }
      }
    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println(message);
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   */
  public static void main(String[] args) 
  {
    String host = "";
    int port;
    String login = "";
    int i = 0; //index to keep track of commands

    try
    {//checks if the login was specified
    	if (args[i].startsWith("#login")) { 
    		login = args[i] + " " + args[i+1];
    		i++;
    		i++;
    	}
    } catch (ArrayIndexOutOfBoundsException e) {}
	
	try {//checks if the hostname was specified
    	host = args[i];
    	i++;
    } catch(ArrayIndexOutOfBoundsException e)
	    {
	      host = "localhost";
	    }
	try {//checks is the port number was specified
	  port = Integer.parseInt(args[i]);
	} catch (ArrayIndexOutOfBoundsException e) {
	  port = DEFAULT_PORT;
	  }    

	ClientConsole chat;
	if (i > 2) { //chooses between if the login was specified or not
		chat = new ClientConsole(host, port, login);
	} else {
		chat= new ClientConsole(host, port);
	}
    chat.accept();  //Wait for console data
  }
}
//End of ConsoleChat class