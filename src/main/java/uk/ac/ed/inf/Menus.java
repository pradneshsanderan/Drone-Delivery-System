package uk.ac.ed.inf;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Menus {

    private static final String urlString = "http://localhost:9898/menus/menus.json";
    private static final HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlString))
                        .build();
    HttpResponse<String> response =
            client.send(request, HttpResponse.BodyHandlers.ofString());
    public String name;
    public String port;

    /**
     *
     * @param name
     * @param port
     */
    Menus(String name, String port) throws IOException, InterruptedException {
        this.name = name;
        this.port = port;
    }

    /**
     * 
     * @param items
     * @return
     */
    public static int  getDeliveryCost(String ... items){
        int standardDeliveryCharge = 50;
        return 1;
    }
}
