
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

                // Message de connexion d'un autre client
                if(line.length() > 9 && line.substring(0, 9).equals("Connexion")){
                    // ne pas print si c'est nous-même
                    if(Integer.parseInt(line.split(" ", 2)[1]) != ChatClient.id) {
                        ChatClient.printMessage(line);
                    }
                    continue;
                }

                // Message de deconnexion d'un autre client
                if(line.length() > 11 && line.substring(0, 11).equals("Deconnexion")){
                    ChatClient.printMessage(line);
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
            if(!e.getMessage().equals("socket closed")) {
                System.err.println("Error in ClientReceiverThread :" + e);
            }
        }
    }

}
