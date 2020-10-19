
package chat_TCP;

import java.io.*;
import java.net.*;

/**
 * Classe principale du client de chat.
 * Gère les inputs utilisateur,
 * et initialise l'IHM du client
 * ainsi que le thread de réception des messages.
 * @author Emma Neiss, Yann Dupont
 * @see ClientIHM
 * @see ClientReceiverThread
 */
public class ChatClient {

    /**
     * L'ID unique de ce client, utilisé pour le repérer par rapport aux autres clients.
     */
    public static int id;

    /**
     * Socket connectée au serveur.
     */
    private static Socket echoSocket = null;

    /**
     * Divers flux d'entrée et sortie pour la gestion des inputs/outputs de messages.
     */
    private static BufferedReader socIn = null;
    private static PrintStream socOut = null;
    private static BufferedReader stdIn = null;

    /**
     * Fenêtre d'unterface graphique du client
     * @see ClientIHM
     */
    private static ClientIHM window;

    /**
     * Thread gérant la réception de messages envoyés par le serveur.
     * @see ClientReceiverThread
     */
    private static ClientReceiverThread ct;

    /**
     * Main à executer pour lancer le client et se connecter au système de chat.
     * @param args      Contient le port auquel se connecter (args[0]), et l'hôte auquel se connecter (args[1])
     * @exception IOException si il y a eu une erreur lors de la connection au serveur
     */
    public static void main(String[] args) throws IOException {

        int port = 1235;
        String host = "localhost"; // c'est le localhost;

        // connexion au serveur
        try {
            echoSocket = new Socket(host, port);
            socIn = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            socOut = new PrintStream(echoSocket.getOutputStream());
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to:"+ port);
            System.exit(1);
        }

        // initialisation IHM
        window = new ClientIHM();

        // todo : lecture du fichier de stockage de l'id
        // si fichier et id trouves : envoi au serveur d'un message signalant qu'on se connecte avec cet id
        // wip
        // sinon : envoi au serveur d'une demande d'un nouvel id
        sendIDdemand();
        id = Integer.parseInt(socIn.readLine());

        window.displayNotif("Connecté avec l'id " + id);
        System.out.println("- Connecté avec l'id " + id + " -");

        // demarrage du thread de reception des messages
        ct = new ClientReceiverThread(socIn);
        ct.start();

        // loop principale d'attente d'input et envoi de messages au serveur en ligne de commande
        runLoop();
    }

    /**
     * Loop principale d'attente d'input et envoi de messages au serveur en ligne de commande.
     * @see ClientIHM pour la gestion de l'envoi des messages en interface graphique
     * @exception IOException si il y a une erreur lors de l'utilisation du flux d'entrée lors de l'input d'un message
     */
    private static void runLoop() throws IOException{
        String line;
        while (true) {
            line=stdIn.readLine();
            sendMessage(line);
        }
    }

    /**
     * Envoie un message entré par l'utilisateur au serveur, pour retransmission aux autres clients connectés.
     * @param message       Message à envoyer, sans métadonnées
     */
    public static void sendMessage(String message){
        socOut.println(id + " " + message);
        System.out.println("Me : " + message);
    }

    /**
     * Envoie une demande d'ID au serveur.
     * Appelé au démarrage du client.
     */
    private static void sendIDdemand(){
        socOut.println("demande d'ID");
    }

    /**
     * Transmet à l'IHM un message reçu du serveur (relayé pour un autre client), pour affichage sur l'IHM.
     * @param message       Message reçu, sans métadonnées
     * @param senderId      ID de l'expéditeur du message
     */
    public static void printMessage(String message, String senderId){
        if(Integer.parseInt(senderId) == id){
            window.printMessage(message, "   Me");
        } else {
            window.printMessage(message, senderId);
        }
    }

    /**
     * Transmet à l'IHM une notification reçue du serveur
     * (connexion ou déconnextion d'un autre client),
     * pour affichage sur l'IHM.
     * @param message       Message reçu
     */
    public static void displayNotif(String message){
        window.displayNotif(message);
    }

    /**
     * Déconnecte le client du serveur :
     * envoie une notification de déconnexion,
     * et ferme les sockets et flux.
     */
    public static void disconnect(){
        socOut.println("Deconnexion");
        try {
            ct.interrupt();
            socOut.close();
            socIn.close();
            stdIn.close();
            echoSocket.close();
        } catch(Exception e){
            System.out.println("Erreur lors de la fermeture des sockets");
        }
    }

}
