
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
    protected int port;

    protected void sendHeaders(int status, String content_type){

        String CRLF = "\r\n";
        out.print("HTTP/1.0 ");

        System.err.println("========== DEBUG OUTPUT ==========");
        System.err.print("HTTP/1.0 ");

        switch (status){
            case 200:
                out.print("200 OK" + CRLF);
                System.err.print("200 OK" + CRLF);
                break;
            case 404:
                out.print("404 NOT_FOUND" + CRLF);
                System.err.print("404 NOT FOUND" + CRLF);
                break;
            default:
                out.print("500 SERVER_ERROR" + CRLF);
                System.err.print("500 SERVER ERROR" + CRLF);
                break;
        }

        out.print("Content-Type: " + content_type + CRLF);
        System.err.print("Content-Type: " + content_type + CRLF);

        out.print(CRLF);
        System.err.print(CRLF);
    }

    protected void getResource(String resource) throws IOException {

        BufferedReader bufferedReader;

        try {
            bufferedReader = new BufferedReader(new FileReader(contentPath + resource));
        } catch (FileNotFoundException e) {
            System.err.println("File not found: ." + resource);
            sendHeaders(404, "text/html");
            out.flush();
            remote.close();
            return;
        }

        // file found

        String file_type;
        String[] split_resource = resource.split("\\.");
        String resource_type = split_resource[split_resource.length - 1];
        String content_type;

        switch (resource_type){
            case "html":
                content_type = "text/html";
                break;
            case "txt":
                content_type = "text/plain";
                break;
            default:
                content_type = "text/plain";
                break;
        }

        System.err.println("content : " + content_type);

        sendHeaders(200, content_type);

        String line = bufferedReader.readLine();
        while (line != null) {
            line = bufferedReader.readLine();
            out.println(line);
        }

        out.flush();
        remote.close();
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
    protected void start(int port) {
        ServerSocket s;
        this.port = port;

        System.out.println("Webserver starting up on port " + Integer.toString(port));
        System.out.println("(press ctrl-c to exit)");
        try {
            // create the main server socket
            s = new ServerSocket(port);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
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

        if (args.length != 1) {
            System.out.println("Usage: java WebServer <WebServer port>");
            System.exit(1);
        }

        WebServer ws = new WebServer();
        ws.start(Integer.parseInt(args[0]));
    }
}
