package client;

import java.io.*;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws IOException {
        Socket s = new Socket("localhost", 8080);
        DataInputStream in = new DataInputStream(s.getInputStream());
        DataOutputStream out = new DataOutputStream(s.getOutputStream());
        out.writeInt(1);

        while (true) {
            System.out.println(in.readUTF());
            out.writeUTF("");
        }
    }

    private static String readLine()
    {
        try
        {
            InputStreamReader reader = new InputStreamReader(System.in);
            BufferedReader buffer = new BufferedReader(reader);
            return buffer.readLine();
        }
        catch (Exception ex)
        {
            return "";
        }
    }
}
