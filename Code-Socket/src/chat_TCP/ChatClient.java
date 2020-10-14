
package chat_TCP;

import java.io.*;
import java.net.*;

public class ChatClient {

    public static int id;
    private static BufferedReader socIn = null;
    private static PrintStream socOut = null;
    private static BufferedReader stdIn = null;

    public static void main(String[] args) throws IOException {

        int port = 8080;
//        String host = "192.168.137.223"; //"localhost";
        String host = "192.168.137.1"; // ip yan

        Socket echoSocket = null;

        // connexion au serveur
        try {
            echoSocket = new Socket(host, port);
            socIn = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            socOut = new PrintStream(echoSocket.getOutputStream());
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + args[0]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to:"+ args[0]);
            System.exit(1);
        }

        // todo : lecture du fichier de stockage de l'id
        // si fichier et id trouves : envoi au serveur d'un message signalant qu'on se connecte avec cet id
        // wip
        // sinon : envoi au serveur d'une demande d'un nouvel id
        sendIDdemand();
        id = Integer.parseInt(socIn.readLine());

        System.out.println("- Connecté avec l'id " + id + " -");

        // demarrage du thread de reception des messages
        ClientReceiverThread ct = new ClientReceiverThread(socIn);
        ct.start();

        // loop principale d'attente d'input et envoi de messages au serveur
        runLoop();

        // si on a quitte la loop : fin de l'execution du client
        socOut.close();
        socIn.close();
        stdIn.close();
        echoSocket.close();
    }

    // loop principale d'attente d'input et envoi de messages au serveur
    private static void runLoop() throws IOException{
        String line;
        while (true) {
            line=stdIn.readLine();
            if (line.equals(".")) break;
            sendMessage(line);
        }
    }

    // envoi d'un message au serveur
    private static void sendMessage(String message){
        socOut.println(id + " " + message);
        System.out.println("Me : " + message);
    }

    private static void sendIDdemand(){
        socOut.println("demande d'ID");
    }

    // affichage d'un message reçu sur la console / l'IHM (wip)
    public static void printMessage(String message){
        System.out.println(message);
    }

    private void restoreHistory(){

    }

}
