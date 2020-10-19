
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

                // on verifie si on arrive a la fin de l'historique
                //if(line.equals("Fin historique")){
                //    continue;
                //}

                // Message de connexion d'un autre client
                if(line.length() > 9 && line.substring(0, 9).equals("Connexion")){
                    // ne pas print si c'est nous-même
                    if(Integer.parseInt(line.split(" ", 2)[1]) != ChatClient.id) {
                        ChatClient.displayNotif(line);
                    }
                    continue;
                }

                // Message de deconnexion d'un autre client
                if(line.length() > 11 && line.substring(0, 11).equals("Deconnexion")){
                    ChatClient.displayNotif(line);
                    continue;
                }

                // Affichage du message dans le cas general
                String[] words = line.split(" ", 2);
                ChatClient.printMessage(words[1], words[0]);

            }
        } catch (Exception e) {
            if(!e.getMessage().equals("Socket closed")) {
                System.err.println("Error in ClientReceiverThread :" + e);
            }
            System.exit(0);
        }
    }

}
