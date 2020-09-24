import java.io.*;
import java.util.*;
import java.net.*;
import java.sql.*;
import static java.lang.System.out;

public class ChatServer {
	Vector<String> users = new Vector<String>();
	Vector<HandleClient> clients = new Vector<HandleClient>();
	Vector<String> logs = new Vector<String>();

	public void process() throws Exception  {
		ServerSocket server = new ServerSocket(9999,10, InetAddress.getLocalHost());
		out.println("Server Running...");
		out.println("Server listening on port: " + 	server.getLocalPort());
		out.println("IP address: " + server.getInetAddress().getHostAddress());
		int i =0;
		while( true) {
			Socket client = server.accept();
			HandleClient c = new HandleClient(client);
			clients.add(c);
		}
	}
	public static void main(String ... args) throws Exception {
		new ChatServer().process();
	} // end of main

  	public void broadcast(String user, String message)  {
		// send message to all connected users
		
		String dest = "";
	    for ( HandleClient c : clients )
	    	if ( ! c.getUserName().equals(user)){
				c.sendMessage(user,message);
				dest = dest + ", "+c.getUserName();
			}
		if (! user.equals("Server"))
			newLog(user, dest.substring(1), "Send message");
	 }
	 
	public void newLog(String source, String dest, String event){
		Timestamp time = new Timestamp(System.currentTimeMillis());
		String log = time+" "+source+" to "+dest+" "+event;
		logs.add(log);

	}

  	class  HandleClient extends Thread {
        String name = "";
		BufferedReader input;
		PrintWriter output;

		public HandleClient(Socket  client) throws Exception {
			// get input and output streams
			input = new BufferedReader( new InputStreamReader( client.getInputStream())) ;
			output = new PrintWriter ( client.getOutputStream(),true);
			// read name
			name  = input.readLine();
			users.add(name); // add to vector
			newLog(name, "Server", "Login");
			start();
		}

        public void sendMessage(String uname,String  msg) {
	  		output.println( uname + ":" + msg);
		}
		
        public String getUserName() {  
            return name; 
        }
        public void run()  {
			String line;
			broadcast("Server", name+ " has connected to the chat.");
			try{
				while(true){
					line = input.readLine();
					if (line.equals("end")){
						broadcast("Server", name+ " has disconnected...");
						clients.remove(this);
						users.remove(name);
						newLog(name, "Server", "Logout");
						if(users.size()==0){
							out.println("Both users disconnected...");
							out.println("Shutting down server");
							System.exit(0);
						}
						break;
					}
					else if(line.equals("printLogs")){
						String textLogs = "";
						for(String log: logs){
							textLogs = textLogs + log + "\n";
						}
						textLogs = textLogs + "end of file";
						sendMessage("Print Logs", textLogs);
					}
					else {
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