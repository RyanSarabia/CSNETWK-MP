import java.io.*;
import java.util.*;
import java.net.*;
import static java.lang.System.out;

public class ChatServer {
  Vector<String> users = new Vector<String>();
  Vector<HandleClient> clients = new Vector<HandleClient>();

  public void process() throws Exception  {
      ServerSocket server = new ServerSocket(9999,10);
      out.println("Server Started...");
      while( true) {
 		 Socket client = server.accept();
 		 HandleClient c = new HandleClient(client);
  		 clients.add(c);
     }  // end of while
  }
  public static void main(String ... args) throws Exception {
      new ChatServer().process();
  } // end of main

  public void broadcast(String user, String message)  {
	    // send message to all connected users
	    for ( HandleClient c : clients )
	       if ( ! c.getUserName().equals(user) )
	          c.sendMessage(user,message);
  }
  
  public void broadcastFile(String user, Socket client)  {
	// send file to all connected users
	for ( HandleClient c : clients )
	   if ( ! c.getUserName().equals(user) )
		  c.sendFile(user, client);
}


	class  HandleClient extends Thread {
		String name = "";
		BufferedReader input;
		DataOutputStream writer;
		PrintWriter output;
		SendFile sender;
		ReceiveFile receiver;
		Socket client;

		public HandleClient(Socket client) throws Exception {
			this.client = client;
			// get input and output streams

			this.input = new BufferedReader( new InputStreamReader( client.getInputStream())) ;
			this.output = new PrintWriter ( client.getOutputStream(), true);
			this.writer = new DataOutputStream(client.getOutputStream());
			// read name
			this.name  = input.readLine();
			users.add(name); // add to vector
			start();
		}

		public void sendMessage(String uname,String  msg)  {
			output.println( uname + ":" + msg);
		}

		public void sendFile(String uname, Socket sender) {

			try {
				DataInputStream reader = new DataInputStream(sender.getInputStream()); //sender
				
				receiver = new ReceiveFile();
				receiver.receiveTextFile(reader, writer, output); //receive from sender
			}

			catch (Exception e)
			{
				System.out.println(e.getMessage());
			}

		}
			
		public String getUserName() {  
			return name; 
		}
			
		public void run()  {
			String line;

			try	{
				while(true) {
					
					line = input.readLine();
					System.out.println("Line:" + line);

					if ( line.equals("end") ) {
						clients.remove(this);
						users.remove(name);
						break;
					}

					else if ( line.equals("SEND_FILE_RANDOM_STRING_123456789")) {
						broadcastFile(name, this.client);

					} else {
						System.out.println("BROADCAST!");
						broadcast(name,line); // method  of outer class - send messages to all
					}
				} // end of while
			} // try

			catch(Exception ex) {
				System.out.println(ex.getMessage());
			}
		} // end of run()
   
	} // end of inner class

} // end of Server