
package HTTP.HTTP_server.src;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Serveur HTTP basé sur l'exemple de "Chapter 1 Programming Spiders, Bots and
 * Aggregators in Java", Copyright 2001 by Jeff Heaton
 * <p>
 * Ce serveur répond aux requêtes GET, POST et DELETE conformément au
 * standard HTTP.
 *
 * @author Yann Dupont, Emma Neiss
 */
public class WebServer {

    /**
     * PrintWriter pour répondre au client connecté
     */
    protected PrintWriter out;

    /**
     * Socket connectée au client
     */
    protected Socket remote;

    /**
     * Port sur lequel le serveur écoute les requêtes
     */
    protected int port;

    /**
     * Chemin d'accès relatif aux ressources disponibles via GET
     */
    protected String resource_path = "./src/HTTP/HTTP_server/doc/resources";

    /**
     * Met fin à la réponse du serveur et à la connexion avec le client
     */
    protected void endResponse() {
        try {
            out.flush();
            remote.close();
        } catch (IOException exception) {
            System.err.println("Exception caught while ending response");
            exception.printStackTrace();
        }
    }

    /**
     * Envoie une réponse au client en cas de ressource non trouvée sur le
     * serveur (erreur 404)
     */
    protected void notFound() {
        sendHeaders(404, "text/html");
        sendTextResource("/404.html");
        endResponse();
    }

    /**
     * Envoie une réponse au client en cas de requête non autorisée (erreur 403)
     */
    protected void forbidden() {
        sendHeaders(403, "text/html");
        sendTextResource("/403.html");
        endResponse();
    }

    /**
     * Envoie au client le contenu d'une ressource textuelle (comme un fichier
     * HTML)
     *
     * @param resource Le chemin d'accès à la ressource demandée (relatif par
     *                 rapport au dossier racine des ressources)
     */
    protected void sendTextResource(String resource) {
        BufferedReader bufferedReader;

        try {
            bufferedReader = new BufferedReader(new FileReader(resource_path + resource));
            String line = bufferedReader.readLine();
            while (line != null) {
                out.println(line);
                line = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            System.err.println("Resource not found: " + resource_path + resource);
        } catch (IOException e) {
            System.err.println("IOException while sending text resource");
            e.printStackTrace();
        }
    }

    /**
     * Envoie au client l'en-tête de la réponse du serveur pour une ressource
     * HTML
     *
     * @param status Le code correspondant au statut de la réponse (ex: 404)
     * @see WebServer#sendHeaders(int, String)
     */
    protected void sendHeaders(int status) {
        sendHeaders(status, "text/html");
    }

    /**
     * Envoie au client l'en-tête de la réponse du serveur dans le cas général,
     *
     * @param status       Le code correspondant au statut de la réponse (ex: 404)
     * @param content_type Le type MIME du contenu envoyé dans le corps de la
     *                     réponse HTTP
     */
    protected void sendHeaders(int status, String content_type) {

        String CRLF = "\r\n";
        out.print("HTTP/1.0 ");

        switch (status) {
            case 200:
                out.print("200 OK" + CRLF);
                break;
            case 403:
                out.print("403 FORBIDDEN" + CRLF);
                break;
            case 404:
                out.print("404 NOT_FOUND" + CRLF);
                break;
            case 400:
                out.print("400 BAD_REQUEST" + CRLF);
                break;
            case 204:
                out.print("204 NO_CONTENT" + CRLF);
                break;
            default:
                out.print("500 SERVER_ERROR" + CRLF);
                break;
        }

        out.print("Content-Type: " + content_type + CRLF);

        out.print(CRLF);

        out.flush();

    }

    /**
     * Exécute le script Python demandé et renvoie le résultat produit sur la
     * sortie standard.
     *
     * @param file Le chemin relatif du fichier par rapport au dossier de
     *             ressources
     * @param args Les arrguments à passer au script
     * @see WebServer#handleGet(List)
     */
    protected List<String> runPythonScript(String file, List<String> args) throws IOException {

        List<String> output = new ArrayList<>();

        String res;
        StringBuilder command = new StringBuilder("python ");
        command.append(resource_path);
        command.append(file);
        for (String arg : args) {
            command.append(" ");
            command.append(arg);
        }

        Process process = Runtime.getRuntime().exec(command.toString());
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

        while ((res = in.readLine()) != null) {
            output.add(res);
        }

        return output;
    }

    /**
     * Envoie la ressource demandée au client dans le corps de la réponse HTTP,
     * puis met fin à la connexion
     *
     * @param resource Le chemin d'accès à la ressource demandée (relatif par
     *                 rapport au dossier racine des ressources)
     * @throws IOException Lève une IOException en cas de problème d'écriture
     *                     sur la sortie de la socket liée au client.
     * @see WebServer#handleGet(List)
     */
    protected void getResource(String resource) throws IOException {

        // find resource type
        String[] split_resource = resource.split("\\.");
        String resource_type = split_resource[split_resource.length - 1].split("\\?")[0];
        String content_type;
        boolean isDynamic = false;

        switch (resource_type) {
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
            case "css":
                content_type = "text/css";
                break;
            case "py":
                content_type = "text/plain";
                isDynamic = true;
            default:
                content_type = "text/plain";
                break;
        }

        if (isDynamic) {

            try {
                // parse arguments
                String file = resource.split("\\?")[0];
                String raw_args = resource.split("\\?")[1];
                List<String> args = new ArrayList<>();

                for (String arg : raw_args.split("&")) {
                    args.add(arg.split("=")[1]);
                }

                // run the (python) dynamic resource and get its response
                List<String> res = runPythonScript(file, args);

                sendHeaders(200, "text/plain");

                for (String line : res) {
                    out.write(line);
                }

            } catch (IOException e) {

                System.err.println("Error while trying to run target resource: " + resource);
                notFound();
                return;
            } catch (ArrayIndexOutOfBoundsException e){
                sendHeaders(400);
                out.write("Error, please fill in the required script parameters.");
            }

        } else {        // ressource statique

            try {

                File file = new File(resource_path + resource);

                if (file.isFile()) {
                    sendHeaders(200, content_type);
                    Files.copy(file.toPath(), remote.getOutputStream());
                } else {    // file not found
                    System.err.println("File not found : " + resource);
                    notFound();
                    return;
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        endResponse();

    }

    /**
     * Traite la requête GET passée en paramètre en la parsant et en répondant
     * à cette requête
     *
     * @param request Les lignes de la requête reçue, sous forme de List de
     *                String
     * @see WebServer#handlePost(List, BufferedReader)
     * @see WebServer#handleDelete(List)
     */
    protected void handleGet(List<String> request) {

        String target = request.get(0).split(" ", 3)[1];
        System.err.println("GET request on " + target);

        try {
            if (target.equals("/")) {    // main page
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

    /**
     * Traite la requête POST passée en paramètre en la parsant et en répondant
     * éventuellement à cette requête
     *
     * @param request Les lignes de la requête reçue, sous forme de List<String>
     * @param in      Le BufferedReader depuis lequel on lit le corps de la requête
     *                client
     * @see WebServer#handleGet(List)
     * @see WebServer#handleDelete(List)
     */
    private void handlePost(List<String> request, BufferedReader in) {
        System.err.println("POST request received");
        int content_length = -1;
        for (String line : request) {
            if (line.split(":")[0].equals("Content-Length")) {
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
                count++;
            }
            String result = resultBuilder.toString();

            sendHeaders(200, "text/plain");
            out.println("Hello " + result.split("=")[1]);
            endResponse();

        } catch (IOException e) {
            e.printStackTrace();
            sendHeaders(500);
            endResponse();
        }
    }

    /**
     * Traite la requête DELETE passée en paramètre en la parsant et en répondant
     * à cette requête
     *
     * @param request Les lignes de la requête reçue, sous forme de List<String>
     * @see WebServer#handleGet(List)
     * @see WebServer#handlePost(List, BufferedReader)
     */
    private void handleDelete(List<String> request) {

        String target = request.get(0).split(" ", 3)[1];
        System.err.println("DELETE request on " + target);

        if (target.equals("/deleteme.txt")) {

            File toDelete = new File(resource_path + target);
            if (toDelete.delete()) {
                System.err.println("Deleted the file: " + toDelete.getName());
                sendHeaders(204);       // todo passer en 200 et renvoyer une petite page ?
                endResponse();
            } else {
                System.err.println("Failed to delete the file.");
                notFound();
            }
        } else {  // trying to delete a file that cannon be deleted
            forbidden();
        }

    }

    /**
     * Constructeur du WebServer.
     *
     * @param port Le port sur lequel le serveur écoutera les requêtes HTTP
     */
    protected void start(int port) {
        ServerSocket s;
        this.port = port;

        System.out.println("Webserver starting up on port " + Integer.toString(port));
        System.out.println("(press ctrl-c to exit)");

        // debug
        System.err.println("Working Directory = " + System.getProperty("user.dir"));
        String[] cwd_path = System.getProperty("user.dir").split("/");

	System.err.println("cwd path last elem:" + cwd_path[cwd_path.length - 1]);
        if (cwd_path[cwd_path.length - 1].equals("HTTP_server")) {  // Linux cwd
            resource_path = "./doc/resources";
        }

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
                List<String> request = new ArrayList<>();
                String line;
                do {
                    line = in.readLine();
                    request.add(line);
                } while (!line.equals(""));

                String request_type = request.get(0).split(" ")[0];
                switch (request_type) {
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
     * @see WebServer#start(int)
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
