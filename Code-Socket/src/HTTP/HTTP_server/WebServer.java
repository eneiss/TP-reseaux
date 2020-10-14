
///A Simple Web Server (WebServer.java)

package HTTP.HTTP_server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * <p>
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 *
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

    protected PrintWriter out;
    protected String contentPath = "src/HTTP/HTTP_server";
    protected Socket remote;

    protected void getResource(String resource) throws IOException {

        BufferedReader bufferedReader;

        try {
            bufferedReader = new BufferedReader(new FileReader(contentPath + resource));
        } catch (FileNotFoundException e) {
            System.err.println("File not found: ." + resource);
            out.println("HTTP/1.1 404 NOT FOUND\n");
            return;
        }

        // file found

        String file_type;
        String[] split_resource = resource.split("\\.");
        String resource_type = split_resource[split_resource.length - 1];
        String content_type;

        switch (resource_type){
            case "html":
                break;
            default:

        }

        String line = bufferedReader.readLine();
        while (line != null) {
            line = bufferedReader.readLine();
            out.println(line);
        }
    }

    protected void handleGet(List<String> request) {

        String target = request.get(0).split(" ", 3)[1];
        System.err.println("GET request on " + target);

        try {
            if (target.equals("/")){
                // Send the response
                // Send the headers
                out.println("HTTP/1.0 200 OK");
                out.println("Content-Type: text/html");
                out.println("Server: Bot");
                // this blank line signals the end of the headers
                out.println("");
                // Send the HTML page
                out.println("<h1>Welcome to the Ultra Mini-WebServer</h1>");
                out.flush();
                remote.close();
            } else {
                getResource(target);
            }
        } catch (IOException e) {
            System.err.println("IOException in handleGet on target " + target);
        }
    }

    /**
     * WebServer constructor.
     */
    protected void start() {
        ServerSocket s;

        System.out.println("Webserver starting up on port 3000");
        System.out.println("(press ctrl-c to exit)");
        try {
            // create the main server socket
            s = new ServerSocket(3000);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return;
        }

        System.out.println("Waiting for connection");
        for (; ; ) {
            try {

                // wait for a connection
                remote = s.accept();
                // remote is now the connected socket
                System.out.println("Connection, sending data.");
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        remote.getInputStream()));
                out = new PrintWriter(remote.getOutputStream());

                // read the HTTP headers
                List<String> request = new ArrayList<String>();
                String line;
                do {
                    line = in.readLine();
//                    System.err.println("headers line: " + line);
                    request.add(line);
                } while (!line.equals(""));

                if (request.get(0).substring(0, 3).equals("GET")) {
                    handleGet(request);
                }


            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }

    /**
     * Start the application.
     *
     * @param args Command line parameters are not used.
     */
    public static void main(String[] args) {
        WebServer ws = new WebServer();
        ws.start();
    }
}
