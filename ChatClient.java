import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import static java.lang.System.out;

public class ChatClient extends JFrame implements ActionListener {
    String uname;
    PrintWriter pw;
    BufferedReader br;
    JTextArea  taMessages;
    JTextField tfInput;
    JButton btnSend,btnExit, btnLog;
    Socket client;

    public ChatClient(String uname, String servername, int serverPort,  String serverAddress) throws Exception {
        super(uname);  // set title for frame
        this.uname = uname;
        client  = new Socket(serverAddress, serverPort);
        br = new BufferedReader( new InputStreamReader( client.getInputStream()) ) ;
        pw = new PrintWriter(client.getOutputStream(),true);
        pw.println(uname);  // send name to server
        buildInterface();
        this.addWindowListener(new WindowAdapter(){
            public void WindowClosing(WindowEvent e){
                pw.println("end");
                System.exit(0);
            }
        });
        new MessagesThread().start();  // create thread for listening for messages
    }
    
    public void buildInterface() {
        
        btnSend = new JButton("Send");
        btnExit = new JButton("Exit");
        btnLog = new JButton("Logs");
        taMessages = new JTextArea();
        taMessages.setRows(10);
        taMessages.setColumns(50);
        taMessages.setEditable(false);
        tfInput  = new JTextField(50);
        JScrollPane sp = new JScrollPane(taMessages, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(sp,"Center");
        JPanel bp = new JPanel( new FlowLayout());
        bp.add(tfInput);
        bp.add(btnSend);
        bp.add(btnLog);
        bp.add(btnExit);
        add(bp,"South");
        btnSend.addActionListener(this);
        btnExit.addActionListener(this);
        btnLog.addActionListener(this);
        setSize(500,300);
        setVisible(true);
        pack();
    }
    
    public void actionPerformed(ActionEvent evt) {
        if ( evt.getSource() == btnExit ) {
            pw.println("end");  // send end to server so that server know about the termination
            System.exit(0);
        }
        else if (evt.getSource() == btnLog){
            pw.println("printLogs");
        }
        
        else {
            // send message to server
            pw.println(tfInput.getText());
        }
    }
    
    public static void main(String ... args) {

    
        // take username from user
        String name = JOptionPane.showInputDialog(null,"Enter your name :", "Username",
             JOptionPane.PLAIN_MESSAGE);
        String serverPortString = JOptionPane.showInputDialog(null,"Enter the server port:", "Port Number",
            JOptionPane.PLAIN_MESSAGE);
        String serverAddress = JOptionPane.showInputDialog(null,"Enter the IP Address:", "IP Address",
            JOptionPane.PLAIN_MESSAGE);
        int serverPort = Integer.parseInt(serverPortString);
        String servername = "localhost";  
        try {
            ChatClient frame = new ChatClient( name ,servername, serverPort, serverAddress);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    frame.pw.println("end");
                    System.exit(0);
                }
            });
        } catch(Exception ex) {
            out.println( "Error --> " + ex.getMessage());
        }
        
    } // end of main
    
    // inner class for Messages Thread
    class  MessagesThread extends Thread {
        public void run() {
            
            String line;
            try {
                while(true) {
                    line = br.readLine();
                    if(line.substring(0,10).equals("Print Logs")){ //creates log text files
                        try{
                            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                            File logText = new File("chatLog.txt");
                            PrintWriter logWriter = new PrintWriter(logText);
                            String curLine = line.substring(11);
                            while(!curLine.equals("end of file")){
                                logWriter.println(curLine);
                                curLine = br.readLine();
                            }
                            logWriter.flush();
                            logWriter.close();
                            System.out.println("Log text file created!");
                        }
                        catch(IOException e){}
                    }
                    else{
                        taMessages.append(line + "\n"); //appends to chatbox
                    }
                } // end of while
            } catch(Exception ex) {}
        }
    }
} //  end of client