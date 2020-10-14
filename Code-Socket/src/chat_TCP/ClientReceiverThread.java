
package chat_TCP;

import java.io.*;
import java.net.*;

public class ClientReceiverThread extends Thread {

    private BufferedReader socIn;

    ClientReceiverThread(BufferedReader socIn) {
        this.socIn = socIn;
    }

    public void run(){
        // on veut afficher les messages qu'on a nous meme envoye
        // jusqu'a la fin de l'historique
        Boolean printOwnMessages = true;

        try {
            while (true) {
                String line = socIn.readLine();
                // on verifie si on arrive a la fin de l'historique
                if(line.equals("Fin historique")){
                    printOwnMessages = false;
                    continue;
                }

                String[] words = line.split(" ", 2);
                if(Integer.parseInt(words[0]) != ChatClient.id){
                    ChatClient.printMessage(words[0]+ ": " + words[1]);
                } else if(printOwnMessages){
                    ChatClient.printMessage("Me : " + words[1]);
                }

            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

}
