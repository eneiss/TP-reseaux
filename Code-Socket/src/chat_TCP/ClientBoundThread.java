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

            // envoyer les infos de connexion au client
            //socOut.println(" ");

            while (true) {
                line = socIn.readLine();
                for(PrintStream soc : allClientsSockets) {
                    soc.println(line);      // envoie ce qu'il a reçu
                }
                System.out.println(line);
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }
}
