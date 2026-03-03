package edu.lab.tdse.microframeworkweb.Server;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {

    static Map<String, WebMethod> endPoints = new HashMap<>();
    private static String staticPath = "target/classes";

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        boolean running = true;

        while (running) {

            System.out.println("Listo para recibir ...");
            Socket clientSocket = serverSocket.accept();

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            boolean isFirstLine = true;

            URI requestedURI = null;

            while ((inputLine = in.readLine()) != null) {

                if (isFirstLine) {

                    String[] firstLineTokens = inputLine.split(" ");
                    String uriStr = firstLineTokens[1];

                    try {
                        requestedURI = new URI(uriStr);
                        System.out.println("Path: " + requestedURI.getPath());

                        if (requestedURI.getQuery() != null) {
                            System.out.println("Query: " + requestedURI.getQuery());
                        }

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    isFirstLine = false;
                }

                if (!in.ready())
                    break;
            }

            String outputLine = "";

            if (requestedURI != null) {

                String reqPath = requestedURI.getPath();
                WebMethod wm = endPoints.get(reqPath);

                if (wm != null) {

                    Request request = new Request(requestedURI);
                    Response response = new Response();

                    String body = wm.execute(request, response);

                    outputLine = "HTTP/1.1 200 OK\n\r"
                            + "Content-Type: " + response.getContentType() + "\n\r"
                            + "\n\r"
                            + "<!DOCTYPE html>"
                            + "<html>"
                            + "<head>"
                            + "<meta charset=\"UTF-8\">"
                            + "<title>Response</title>"
                            + "</head>"
                            + "<body>"
                            + body
                            + "</body>"
                            + "</html>";

                } else {

                    outputLine = serveStaticFile(reqPath);

                }
            }

            out.println(outputLine);

            out.close();
            in.close();
            clientSocket.close();
        }

        serverSocket.close();
    }

    public static void get(String path, WebMethod wm) {
        endPoints.put(path, wm);
    }

    public static void staticfiles(String path) {
        staticPath = "target/classes" + path;
        System.out.println("Archivos en: " + staticPath);
    }

    private static String serveStaticFile(String path) {

        try {

            File file = new File(staticPath + path);

            if (!file.exists()) {
                return "HTTP/1.1 404 Not Found\r\n\r\nFile Not Found";
            }

            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                content.append(line);
            }

            br.close();

            return "HTTP/1.1 200 OK\r\n\r\n" + content;

        } catch (IOException e) {
            return "HTTP/1.1 500 Internal Server Error\r\n\r\nError";
        }
    }

}
