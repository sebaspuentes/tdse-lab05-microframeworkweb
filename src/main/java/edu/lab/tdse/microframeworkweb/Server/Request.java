package edu.lab.tdse.microframeworkweb.Server;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private URI uri;
    private Map<String, String> queryParams = new HashMap<>();

    public Request(URI uri) {
        this.uri = uri;
        parseQuery();
    }

    private void parseQuery() {
        String query = uri.getQuery();
        if (query == null) return;

        String[] pairs = query.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                queryParams.put(keyValue[0], keyValue[1]);
            }
        }
    }

    public String getValues(String key) {
        return queryParams.get(key);
    }
}
