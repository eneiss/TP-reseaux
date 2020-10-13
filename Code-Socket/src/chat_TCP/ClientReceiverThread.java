
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
                String[] line = socIn.readLine().split(" ", 2);
                if(Integer.parseInt(line[0]) != ChatClient.id){
                    ChatClient.printMessage(line[1]);
                }

            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

}
