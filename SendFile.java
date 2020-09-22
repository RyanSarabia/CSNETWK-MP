import java.net.*;
import java.io.*;

public class SendFile {
    
    File file;
    DataOutputStream writer;

    public SendFile(DataOutputStream writer, File file) {
        this.file = file;
        this.writer = writer;

    }

    public void sendTextFile() {
        try {
            file.createNewFile();

            DataInputStream disReader = new DataInputStream(new FileInputStream(file));

            int count;
            byte[] buffer = new byte[8192];
            while ((count = disReader.read(buffer)) > 0)
            {
                writer.write(buffer, 0, count);
            }

            disReader.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
