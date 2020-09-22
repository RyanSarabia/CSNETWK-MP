import java.net.*;
import java.io.*;

public class ReceiveFile {
    
    File file;
    Socket client;

    public ReceiveFile(Socket client) {

        this.client = client; 
    }

    public File receiveTextFile() {
        try {

            file = File.createTempFile("tmp", ".txt", new File("./test"));
            
            DataInputStream disReader = new DataInputStream(client.getInputStream()); //sender
            DataOutputStream dosWriter = new DataOutputStream(new FileOutputStream(file));

            int count;
            byte[] buffer = new byte[8192];
            while ((count = disReader.read(buffer)) > 0)
            {
                dosWriter.write(buffer, 0, count);
            }

            file.deleteOnExit();
            dosWriter.close();
            return file;

        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public void saveTextFile() {
        try {

            String dir = new FileChooser().openDirectoryChooser() + "/New.txt";
            this.file = new File(dir); //gives path to file
            file.createNewFile();

			DataInputStream disReader = new DataInputStream(client.getInputStream()); //receiver
            DataOutputStream dosWriter = new DataOutputStream(new FileOutputStream(file));

            int count;
            byte[] buffer = new byte[8192];
            while ((count = disReader.read(buffer)) > 0)
            {
                dosWriter.write(buffer, 0, count);
            }

            dosWriter.close();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
