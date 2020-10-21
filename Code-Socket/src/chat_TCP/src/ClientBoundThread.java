
package chat_TCP.src;

import java.io.*;
import java.net.*;
import java.util.List;

/**
 * Classe implémentant un thread dédié à l'écoute côté serveur des messages d'un client donné.
 * Notamment, retransmet les messages envoyés par ce client à tous les autres clients.
 * @author Emma Neiss, Yann Dupont
 * @see ServerConnectionThread
 */
public class ClientBoundThread extends Thread {

    /**
     * ID unique du client auquel ce thread est lié.
     */
    private int clientId;

    /**
     * Socket connectée au client, par laquelle on reçoit les messages qu'il envoie.
     */
    private Socket clientSocket;

    /**
     * Liste des sockets connectées chacune à un des clients, par lesquelles on leur retransmet les messages.
     */
    private List<PrintStream> allClientsSockets;

    /**
     * Constructeur de ClientBoundThread, initialisant le thread sans le démarrer.
     * @param s                 Socket connectée au client auquel ce thread est lié
     * @param allClientsSockets Liste des sockets connectées chacune à un des clients.
     */
    ClientBoundThread(Socket s, List<PrintStream> allClientsSockets) {
        this.clientSocket = s;
        this.allClientsSockets = allClientsSockets;
    }

    /**
     * Démarre le thread de retransmission des messages.
     * Effectue également l'interface pour les demandes de connexion, les notifications de déconnexion,
     * et envoie l'historique lors de la connexion du client.
     */
    public void run() {
        try {
            BufferedReader socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintStream socOut = new PrintStream(clientSocket.getOutputStream());

            // reception de soit la demande d'attribution d'ID, soit la declaration d'utilisation d'un ID
            String line = socIn.readLine();
            if(line.equals("demande d'ID")){
                clientId = ServerConnectionThread.newIdAttribution();
                socOut.println(clientId);
            } else {
                clientId = Integer.parseInt(line);
            }

            // envoyer l'historique au client
            List<String> hist = ServerConnectionThread.getHistory();
            for(String histLine : hist){
                socOut.println(histLine);
            }

            // annoncer a tous les autres participants qu'on s'est connecte
            for(PrintStream soc : allClientsSockets) {
                soc.println("Connexion " + clientId);
            }

            while (true) {
                line = socIn.readLine();

                // si le client se deconnecte : on sort de la boucle pour effectuer le processus de deconnexion
                if(line.equals("Deconnexion")){
                    break;
                }

                for(PrintStream soc : allClientsSockets) {
                    soc.println(line);      // envoie ce qu'il a reçu
                }
                System.out.println(line);
                ServerConnectionThread.writeToHistory(line);
            }

            // Deconnexion du client
            allClientsSockets.remove(socOut);
            for(PrintStream soc : allClientsSockets) {
                soc.println("Deconnexion " + clientId);
            }
            socOut.close();
            socIn.close();
            clientSocket.close();

        } catch (Exception e) {
            System.err.println("Error in ClientBoundThread:" + e);
        }
    }
}
