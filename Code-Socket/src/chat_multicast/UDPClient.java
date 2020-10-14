package chat_multicast;

import java.io.*;
import java.net.*;

public class UDPClient {

    private static int id;
    private static MulticastSocket socket;
    private static InetAddress groupAddr;

    private static int groupPort = 6789;
    private static String groupIP = "228.5.6.7";

    public static void main(String[] args) throws IOException {


        try {
            groupAddr = InetAddress.getByName(groupIP);
        } catch (UnknownHostException e){
            System.out.println("Error: unknown host " + groupIP);
            System.exit(1);
        }

        // Create a multicast socket
        socket = new MulticastSocket(groupPort);
        // Join the group
        socket.joinGroup(groupAddr);

    }

    private static void sendMessage(String message) throws IOException {
        // Build a datagram packet for a message
        // to send to the group
        String msg = "Hello";
        DatagramPacket hi = new DatagramPacket(msg.getBytes(),
                                msg.length(), groupAddr, groupPort);
        // Send a multicast message to the group
        socket.send(hi);
    }

}
