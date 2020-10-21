
package chat_TCP.src;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe principale du serveur de chat.
 * Gère la connexion des clients et l'historique des messages.
 * @author Emma Neiss, Yann Dupont
 * @see ClientBoundThread
 */
public class ServerConnectionThread {

    /**
     * Nombre de clients connectés actuellement
     */
    static private int totalUserNb;

    /**
     * Chemin vers le fichier texte contenant l'historique des messages
     */
    static private String historyFilePath;

    /**
     * Main à exécuter pour démarrer le serveur de chat
     * et autoriser les clients à se connecter.
     * @param args      Contient le numéro de port (args[0])
     */
    public static void main(String args[]){

        int port = 1235;

        historyFilePath = System.getProperty("user.dir") + "/history.txt";

        ServerSocket listenSocket;
        List<PrintStream> allClientsSockets = new ArrayList<PrintStream>();

        try {
            listenSocket = new ServerSocket(Integer.parseInt(args[0]));
            System.out.println("Server ready...");
            while (true) {
                Socket clientSocket = listenSocket.accept(); // bloque jusqu'à ce qu'on ait une demande de connexion
                System.out.println("Connexion from:" + clientSocket.getInetAddress());
                allClientsSockets.add(new PrintStream(clientSocket.getOutputStream()));
                ClientBoundThread ct = new ClientBoundThread(clientSocket, allClientsSockets);
                ct.start();
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

    /**
     * Attribue un ID actuellement utilisé, pour utilisation par un client.
     * @return l'ID attribué
     */
    public static int newIdAttribution(){
        totalUserNb += 1;
        return totalUserNb;
    }

    /**
     * Persiste un message reçu par le serveur
     * dans le fichier texte d'historique.
     * @param line      Le message à persister, avec métadonnées
     * @exception IOException si le fichier est inaccessible
     */
    public static void writeToHistory(String line) throws IOException {
        FileWriter fw = new FileWriter(historyFilePath, true);
        fw.write(line + "\n");
        fw.close();
    }

    /**
     * Récupère tout l'historique des messages.
     * @return      Une liste de messages sous forme de String, avec métadonnées
     * @exception IOException en cas d'erreur à la lecture du fichier
     */
    public static List<String> getHistory() throws IOException {
        List<String> hist = new ArrayList<String>();
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(historyFilePath))) {
            String line = bufferedReader.readLine();
            while(line != null) {
                hist.add(line);
                line = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found :/");
        }
        return hist;
    }

}
