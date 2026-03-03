package edu.lab.tdse.microframeworkweb.Server;

import java.io.IOException;

import edu.lab.tdse.microframeworkweb.Server.HttpServer;

public class WebApp {
    public static void main(String[] args)  throws IOException {
        HttpServer.staticfiles("/webroot");
        HttpServer.get("/hello", (req, resp) -> "Hello " + req.getValues("name"));
        HttpServer.get("/pi", (req, resp) -> {
            return String.valueOf(Math.PI); 
        });

        HttpServer.main(args);
    }
    
}
