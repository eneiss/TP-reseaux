
package chat_TCP;

import java.io.*;
import java.net.*;

public class ClientReceiverThread extends Thread {

    private BufferedReader socIn;

    ClientReceiverThread(BufferedReader socIn) {
        this.socIn = socIn;
    }

    public void run(){
        try {
            while (true) {
                String line = socIn.readLine();
                ChatClient.printMessage(line);
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

}
