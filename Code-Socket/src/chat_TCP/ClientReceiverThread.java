
package chat_TCP;

import java.io.*;

/**
 * Classe implémentant le thread chargé de recevoir les messages du serveur.
 * @author Emma Neiss, Yann Dupont
 * @see ChatClient
 * @see ClientIHM
 */
public class ClientReceiverThread extends Thread {

    /**
     * Flux entrant de la socket connectée au serveur.
     */
    private BufferedReader socIn;

    /**
     * Constructeur de ClientReceiverThread, initialisant le thread (sans le démarrer).
     * @param socIn     Flux entrant de la socket connectée au serveur
     */
    ClientReceiverThread(BufferedReader socIn) {
        this.socIn = socIn;
    }

    /**
     * Démarre le thread de réception des messages.
     */
    public void run(){
        try {
            while (true) {
                String line = socIn.readLine();

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
            if(!(e.getMessage().equals("Socket closed") ||e.getMessage().equals("socket closed"))) {
                System.err.println("Error in ClientReceiverThread :" + e);
            }
            System.exit(0);
        }
    }

}
