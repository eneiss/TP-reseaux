
package chat_TCP;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ServerConnectionThread {

    static private int totalUserNb;
    static private String historyFilePath;

    public static void main(String args[]){

        int port = 1235;

        historyFilePath = System.getProperty("user.dir") + "/history.txt";

        ServerSocket listenSocket;
        List<PrintStream> allClientsSockets = new ArrayList<PrintStream>();

        try {
            listenSocket = new ServerSocket(port);
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

    public static void writeToHistory(String line) throws IOException {
        FileWriter fw = new FileWriter(historyFilePath, true);
        fw.write(line + "\n");
        fw.close();
    }

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
