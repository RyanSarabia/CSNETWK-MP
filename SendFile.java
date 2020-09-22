import java.net.*;
import java.io.*;

public class SendFile {
    
    File file;
    Socket sender;

    public SendFile(Socket sender, File sendfile) {
        this.sender = sender;
        this.file = sendfile;

    }

    public void sendTextFile() {
        try {
            file.createNewFile();

            DataInputStream disReader = new DataInputStream(new FileInputStream(file));
			DataOutputStream dosWriter = new DataOutputStream(sender.getOutputStream());

            int count;
            byte[] buffer = new byte[8192];
            while ((count = disReader.read(buffer)) > 0)
            {
                dosWriter.write(buffer, 0, count);
            }

            disReader.close();
            dosWriter.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
