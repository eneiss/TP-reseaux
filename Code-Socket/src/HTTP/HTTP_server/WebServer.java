
///A Simple Web Server (WebServer.java)

package HTTP.HTTP_server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.io.File;
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
    private static final int BUFFER_SIZE = 1024;

    protected void endResponse() {
        try{
            out.flush();
            remote.close();
//            System.err.println("> End of response");
        } catch (IOException exception){
            System.err.println("Exception caught while ending response");
            exception.printStackTrace();
        }
    }

    protected void notFound(){
        sendHeaders(404, "text/html");
        sendTextResource("/404.html");
        endResponse();
    }

    protected void forbidden(){
        sendHeaders(403, "text/html");
        sendTextResource("/403.html");
        endResponse();
    }

    protected void sendTextResource(String resource){
        BufferedReader bufferedReader;

        try {
            bufferedReader = new BufferedReader(new FileReader(contentPath + resource));
            String line = bufferedReader.readLine();
            while (line != null) {
                out.println(line);
                line = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            System.err.println("Resource not found: " + resource);
        } catch (IOException e){
            System.err.println("IOException while sending text resource");
            e.printStackTrace();
        }


    }

    protected void sendHeaders(int status){
        sendHeaders(status, "text/html");
    }

    protected void sendHeaders(int status, String content_type){

        String CRLF = "\r\n";
//        System.err.println("========== RESPONSE SENT ==========");
        out.print("HTTP/1.0 ");
//        System.err.print("HTTP/1.0 ");

        switch (status){
            case 200:
                out.print("200 OK" + CRLF);
//                System.err.print("200 OK" + CRLF);
                break;
            case 403:
                out.print("403 FORBIDDEN" + CRLF);
//                System.err.print("403 FORBIDDEN" + CRLF);
                break;
            case 404:
                out.print("404 NOT_FOUND" + CRLF);
//                System.err.print("404 NOT_FOUND" + CRLF);
                break;
            case 400:
                out.print("400 BAD_REQUEST" + CRLF);
//                System.err.print("400 BAD_REQUEST" + CRLF);
                break;
            case 204:
                out.print("204 NO_CONTENT" + CRLF);
//                System.err.print("204 NO_CONTENT" + CRLF);
                break;
            default:
                out.print("500 SERVER_ERROR" + CRLF);
//                System.err.print("500 SERVER_ERROR" + CRLF);
                break;
        }

        out.print("Content-Type: " + content_type + CRLF);
//        System.err.print("Content-Type: " + content_type + CRLF);

        out.print(CRLF);
//        System.err.print(CRLF);

        out.flush();

    }

    protected void getResource(String resource) throws IOException {

        // find resource type
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
            case "png":
                content_type = "image/png";
                break;
            case "mp3":
                content_type = "audio/mp3";
                break;
            case "pdf":
                content_type = "application/pdf";
                break;
            case "js":
                content_type = "text/javascript";
                break;
            default:
                content_type = "text/plain";
                break;
        }

        try {

            File file = new File(cwd + resource);
//            System.err.println("requested file path : " + file.toPath().toString());

            if(file.isFile()) {
                sendHeaders(200, content_type);
                Files.copy(file.toPath(), remote.getOutputStream());
            } else {    // file not found
                System.err.println("File not found : " + resource);
                notFound();
                return;
            }

        } catch (NullPointerException e){
            e.printStackTrace();
        }

        endResponse();

    }

    protected void handleGet(List<String> request) {

        String target = request.get(0).split(" ", 3)[1];
        System.err.println("GET request on " + target);

        try {
            if (target.equals("/")){    // main page
                sendHeaders(200);
                sendTextResource("/index.html");

                endResponse();
            } else {
                getResource(target);
            }
        } catch (IOException e) {
            System.err.println("IOException in handleGet on target " + target);
        }
    }


    private void handlePost(List<String> request, BufferedReader in) {
        // TODO
        System.err.println("POST request received");
        int content_length = -1;
        for (String line: request){
//            System.err.println(line);
            if (line.split(":")[0].equals("Content-Length")){
                content_length = Integer.parseInt(line.split(": ")[1]);
            }
        }

        // read request body (parameters)
        try {
            StringBuilder resultBuilder = new StringBuilder();
            int count = 0;
            int intch;
            while (count < content_length && ((intch = in.read()) != -1)) {
                resultBuilder.append((char) intch);
//                System.err.print((char) intch);
                count++;
            }
            String result = resultBuilder.toString();

            sendHeaders(200, "text/plain");
            out.println("Bonjour " + result.split("=")[1]);
            endResponse();

        } catch (IOException e){
            e.printStackTrace();
            sendHeaders(500);
            endResponse();
        }
    }

    private void handleDelete(List<String> request) {

        String target = request.get(0).split(" ", 3)[1];
        System.err.println("DELETE request on " + target);

        if (target.equals("/deleteme.txt")){

            File toDelete = new File(cwd + target);
            if (toDelete.delete()) {
                System.err.println("Deleted the file: " + toDelete.getName());
                sendHeaders(204);       // todo passer en 200 et renvoyer une petite page ?
                endResponse();
            } else {
                System.err.println("Failed to delete the file.");
                notFound();
            }
        }else{  // trying to delete a file that cannon be deleted
            forbidden();
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

        System.out.println("Waiting for connection...\n");
        for (; ; ) {
            try {

                // wait for a connection
                remote = s.accept();
                // remote is now the connected socket
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
                        handlePost(request, in);
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
                System.out.println("Error occurred in main loop: " + e);
            }
        }
    }

    /**
     * Lance le serveur sur la machine locale.
     *
     * @param args Port utilisé par le serveur renseigné dans argv[0]
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
