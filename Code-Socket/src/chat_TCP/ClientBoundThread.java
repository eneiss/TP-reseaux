package chat_TCP;

import java.io.*;
import java.net.*;
import java.util.List;

public class ClientBoundThread extends Thread {

    private Socket clientSocket;
    private List<PrintStream> allClientsSockets;
    private int clientId;

    ClientBoundThread(Socket s, List<PrintStream> allClientsSockets) {
        this.clientSocket = s;
        this.allClientsSockets = allClientsSockets;
    }

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
            //socOut.println("Fin historique");

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
            System.err.println("Error in EchoServer:" + e);
        }
    }
}
