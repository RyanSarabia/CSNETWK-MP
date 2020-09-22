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
		DataInputStream fileinput;
		PrintWriter output;
		SendFile sender;
		ReceiveFile receiver;
		Socket client;

		public HandleClient(Socket client) throws Exception {
			this.client = client;
			// get input and output streams
			this.input = new BufferedReader( new InputStreamReader( client.getInputStream())) ;
			this.output = new PrintWriter ( client.getOutputStream(), true);
			// read name
			this.name  = input.readLine();
			users.add(name); // add to vector
			start();
		}

		public void sendMessage(String uname,String  msg)  {
			output.println( uname + ":" + msg);
		}

		public void sendFile(String uname, Socket sender) {

			receiver = new ReceiveFile(sender);
			File file = receiver.receiveTextFile(); //receive from sender

			SendFile filesender = new SendFile(this.client, file);

			//output something to client na code rin na magtrigger yung receivefile.
			output.println("SEND_FILE_RANDOM_STRING_123456789");
			filesender.sendTextFile();

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