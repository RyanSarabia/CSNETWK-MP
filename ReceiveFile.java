import java.net.*;
import java.io.*;

public class ReceiveFile {
    
    File file;
    Socket client;

    public ReceiveFile() {

    }

    public void receiveTextFile(DataInputStream reader, DataOutputStream writer, PrintWriter pw) {
        try {
            
            // DataInputStream disReader = new DataInputStream(client.getInputStream()); //sender
            // DataOutputStream dosWriter = new DataOutputStream(client.getOutputStream());

           writer.writeBytes("SEND_FILE_RANDOM_STRING_123456789\n");

            int count;
            byte[] buffer = new byte[8192];
            while ((count = reader.read(buffer)) > 0)
            {
                writer.write(buffer, 0, count);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void saveTextFile(DataInputStream reader, String dir) {
        try {
            this.file = new File(dir); //gives path to file
            file.createNewFile();

            DataOutputStream dosWriter = new DataOutputStream(new FileOutputStream(file));

            int count;
            byte[] buffer = new byte[8192];
            while ((count = reader.read(buffer)) > 0)
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
