package chat_multicast;

import chat_multicast.UDPReceiverThread;

import java.io.*;
import java.net.*;

public class UDPClient {

    private static int id;
    private static MulticastSocket socket;
    private static InetAddress groupAddr;

    private static int groupPort = 6789;
    private static String groupIP = "228.5.6.7";

    private static BufferedReader stdIn = null;

    public static void main(String[] args) throws IOException {


        try {
            groupAddr = InetAddress.getByName(groupIP);

            // Create a multicast socket
            socket = new MulticastSocket(groupPort);
            // Join the group
            socket.joinGroup(groupAddr);

            // demarrage du thread de reception des messages
            UDPReceiverThread receiverThread = new UDPReceiverThread(socket);
            receiverThread.start();

            stdIn = new BufferedReader(new InputStreamReader(System.in));

            sendMessage("Bonjour, test");   // debug
            runLoop();

            socket.leaveGroup(groupAddr);

        } catch (UnknownHostException e){
            System.err.println("Error: unknown host " + groupIP);
            System.exit(1);
        } catch (IOException e){
            System.err.println("IOException : ");
            e.printStackTrace();
        }

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

    private static void sendMessage(String message) throws IOException {
        // Build a datagram packet for a message
        // to send to the group
        DatagramPacket packet = new DatagramPacket(message.getBytes(),
                                message.length(), groupAddr, groupPort);
        // Send a multicast message to the group
        socket.send(packet);
    }

    public static void printMessage(String message){

        System.out.println(message);
    }


}
