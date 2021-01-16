import java.io.*;
import java.util.Scanner;

import client.ChatClient;
import common.ChatIF;


public class ServerConsole implements ChatIF {
	
	  //Class variables *************************************************
	  
	  /**
	   * The default port to connect on.
	   */
	  final public static int DEFAULT_PORT = 5555;
	  
	  //Instance variables **********************************************
	  
	  /**
	   * The instance of the server that created this ServerConsole.
	   */
	  EchoServer server;
	  
	  
	  
	  /**
	   * Scanner to read from the console
	   */
	  Scanner fromConsole; 
	  
	  
	  //Constructors ****************************************************

	  /**
	   * Constructs an instance of the ServerConsole UI.
	   *
	   * @param host The host to connect to.
	   * @param port The port to connect on.
	 * @throws IOException 
	   */
	  public ServerConsole(int port) throws IOException 
	  {
	    server = new EchoServer(port);
	    
	    try 
	    {
	      server.listen(); //Start listening for connections
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println("ERROR - Could not listen for clients!");
	    }
	    
	    // Create scanner object to read from console
	    fromConsole = new Scanner(System.in); 
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
	        	if(message.startsWith("#")) { //checks for a command
	        		switch (words[0]) {
	        		case "#quit": //closes connections and quits the console
	        			server.close();
	        			System.exit(0);
	        			break;
	        		case "#stop": //stops listening
	        			if (server.isListening() ) {
	        				server.sendToAllClients("WARNING! Server will stop listening for connections");
	        				server.stopListening();
	        			} else {
	        				System.out.println("Server already not listening");
	        			}
	        			break;
	        		case "#close": //closes all connections
	        			server.close();
	        			break;
	        		case "#setport": //sets the port number
	        			if (server.isListening()) {
	        				System.out.println("Server is stil listenning. You must close the server first.");
	        			} else {
	        				server.setPort(Integer.parseInt(words[1]));
	        				System.out.println("Server port has been set to " + words[1]);
	        			}
	        			break;
	        		case "#start": //starts listening
	        			server.listen();
	        			break;
	        		case "#getport": //gets teh current port number
	        			System.out.println(server.getPort());
	        		}
	        	} else {
			        server.sendToAllClients("SERVER MSG> " + message); //sends message to clients
			        display(message);
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
	@Override
	public void display(String message) {
		System.out.println("SERVER MSG> " + message);
		
	}

}