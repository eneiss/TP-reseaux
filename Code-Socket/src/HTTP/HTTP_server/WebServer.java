
///A Simple Web Server (WebServer.java)

package HTTP.HTTP_server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.io.File;
import java.nio.file.Path;
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
    protected String cwd = "./src/HTTP/HTTP_server";

    protected void endResponse() {
        try{
            out.flush();
            remote.close();
        } catch (IOException exception){
            System.err.println("Exception caught while ending response");
            exception.printStackTrace();
        }
    }

    protected void sendHeaders(int status){
        sendHeaders(status, "text/html");
    }

    protected void sendHeaders(int status, String content_type){

        String CRLF = "\r\n";
        System.err.println("========== RESPONSE SENT ==========");
        out.print("HTTP/1.0 ");
        System.err.print("HTTP/1.0 ");

        switch (status){
            case 200:
                out.print("200 OK" + CRLF);
                System.err.print("200 OK" + CRLF);
                break;
            case 404:
                out.print("404 NOT_FOUND" + CRLF);
                System.err.print("404 NOT_FOUND" + CRLF);
                break;
            case 400:
                out.print("400 BAD_REQUEST" + CRLF);
                System.err.print("400 BAD_REQUEST" + CRLF);
                break;
            default:
                out.print("500 SERVER_ERROR" + CRLF);
                System.err.print("500 SERVER_ERROR" + CRLF);
                break;
        }

        out.print("Content-Type: " + content_type + CRLF);
        System.err.print("Content-Type: " + content_type + CRLF);

        out.print(CRLF);
        System.err.print(CRLF);

    }

    protected void getResource(String resource) throws IOException {

        // find resource type
        String[] split_resource = resource.split("\\.");
        String resource_type = split_resource[split_resource.length - 1];
        String content_type;
        boolean isBinary = false;

        switch (resource_type){
            case "html":
                content_type = "text/html";
                break;
            case "txt":
                content_type = "text/plain";
                break;
            case "png":
                content_type = "image/png";
                isBinary = true;
                break;
            default:
                content_type = "text/plain";
                break;
        }

        if (isBinary){

            try {
                File file = new File(cwd + resource);
                System.err.println("requested binary file path : " + file.toPath().toString());

                // debug
//                System.err.println("--- Working Directory = " + System.getProperty("user.dir"));
//                File test_file = new File("./out/production/Code-Socket/HTTP/HTTP_server/example.html");
//                if (test_file.isFile()){
//                    System.err.println("Test file exists");
//                } else {
//                    System.err.println("!!! Test file does NOT exist ! :(");
//                }

                if(file.isFile()) {
                    sendHeaders(200, content_type);
                    Files.copy(file.toPath(), remote.getOutputStream());
                } else {    // file not found
                    System.err.println("Binary file not found : " + resource);
                    sendHeaders(404);
                }
            } catch (NullPointerException e){
                e.printStackTrace();
            }

        } else {

            BufferedReader bufferedReader;

            try {
                bufferedReader = new BufferedReader(new FileReader(contentPath + resource));
            } catch (FileNotFoundException e) {
                System.err.println("File not found: ." + resource);
                sendHeaders(404);
                endResponse();
                return;
            }

            // file found

            System.err.println("content : " + content_type);

            sendHeaders(200, content_type);

            String line = bufferedReader.readLine();
            while (line != null) {
                out.println(line);
                line = bufferedReader.readLine();
            }

        }       // resource not binary

        endResponse();

    }

    protected void handleGet(List<String> request) {

        String target = request.get(0).split(" ", 3)[1];
        System.err.println("GET request on " + target);

        try {
            if (target.equals("/")){    // main dummy page
                // Send the response
                // Send the headers
                out.println("HTTP/1.0 200 OK");
                out.println("Content-Type: text/html");
                out.println("Server: Bot");
                // this blank line signals the end of the headers
                out.println("");
                // Send the HTML page
                out.println("<h1>Welcome to the Ultra Mini-WebServer</h1>");

                endResponse();
            } else {
                getResource(target);
            }
        } catch (IOException e) {
            System.err.println("IOException in handleGet on target " + target);
        }
    }


    private void handlePost(List<String> request) {
        // TODO
        System.err.println("POST request received");
    }

    private void handleDelete(List<String> request) {
        // TODO
        System.err.println("DELETE request received");
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

                String request_type = request.get(0).split(" ")[0];
                switch (request_type){
                    case "GET":
                        handleGet(request);
                        break;
                    case "POST":
                        handlePost(request);
                        break;
                    case "DELETE":
                        handleDelete(request);
                        break;
                    default:
                        sendHeaders(400, "text/html");
                        endResponse();
                        break;
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
