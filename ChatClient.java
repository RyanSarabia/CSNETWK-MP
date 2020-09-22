import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import static java.lang.System.out;

public class ChatClient extends JFrame implements ActionListener {
    String uname;
    PrintWriter pw;
    BufferedReader br;
    JTextArea  taMessages;
    JTextField tfInput;
    JButton btnSend,btnFile, btnExit;
    Socket client;
    FileChooser filechooser;
    SendFile filesender;
    File sendfile;
    DataInputStream disReader;
    DataOutputStream dosWriter;
    
    public ChatClient(String uname, String servername) throws Exception {
        super(uname);  // set title for frame
        this.uname = uname;
        this.filechooser = new FileChooser();
        client  = new Socket(servername, 9999);
        br = new BufferedReader( new InputStreamReader( client.getInputStream()) ) ;
        pw = new PrintWriter(client.getOutputStream(), true);
        dosWriter = new DataOutputStream(client.getOutputStream()); //for sending
        disReader = new DataInputStream(client.getInputStream()); //for receiving
        pw.println(uname);  // send name to server
        buildInterface();
        new MessagesThread().start();  // create thread for listening for messages
    }
    
    public void buildInterface() {
        btnSend = new JButton("Send");
        btnFile = new JButton("Add File");
        btnExit = new JButton("Exit");
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
        bp.add(btnFile);
        bp.add(btnExit);
        add(bp,"South");
        btnSend.addActionListener(this);
        btnFile.addActionListener(this);
        btnExit.addActionListener(this);
        setSize(500,300);
        setVisible(true);
        pack();
    }
    
    public void actionPerformed(ActionEvent e) {

        if ( e.getSource() == btnExit ) {
            pw.println("end");  // send end to server so that server know about the termination
            System.exit(0);

        } 
        else if ( e.getSource() == btnFile ) {
            sendfile = filechooser.openFileChooser();

            if (sendfile != null) {
                pw.println("SEND_FILE_RANDOM_STRING_123456789"); // send code to sever for file sending. should come first so client can absorb it.
                filesender = new SendFile(dosWriter, sendfile);
                filesender.sendTextFile();
            }
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
        String servername = "localhost";  
        try {
            new ChatClient( name ,servername);
        } catch(Exception ex) {
            out.println( "Error --> " + ex.getMessage());
        }
        
    } // end of main
    
    // inner class for Messages Thread
    class  MessagesThread extends Thread {
        ReceiveFile receiver = new ReceiveFile();

        public void run() {
            String line;
            try {
                while(true) {
                    line = br.readLine();

                    System.out.println(line);

                    if (line.equals("SEND_FILE_RANDOM_STRING_123456789"))
                    {
                        String dir = filechooser.openDirectoryChooser();
                        dir += "/New.txt";

                        System.out.println(dir);

                        if (dir != null){
                            receiver.saveTextFile(disReader, dir);
                        }
                        
                       else {
                           //iuser did not accept.
                       }

                    } else {
                        taMessages.append(line + "\n");
                    }
                } // end of while
            } catch(Exception ex) {}
        }
    }
} //  end of client