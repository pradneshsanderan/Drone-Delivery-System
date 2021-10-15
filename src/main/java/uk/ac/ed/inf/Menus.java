package uk.ac.ed.inf;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.util.ArrayList;
import java.util.HashMap;

public class Menus {


    public String name;
    public String port;
    private static final String urlString = "http://localhost:9898/menus/menus.json";
    private static final HttpClient client = HttpClient.newHttpClient();


    /**
     * The Menu class which represents the
     */
    public class Menu {
        String name;
        String location;
        ArrayList<uk.ac.ed.inf.Menu> menu;
    }


    /**
     * the Constructor for the Menus class which accepts 2 Strings which represent the name of the machine
     * and the port where the web server is running
     * @param name a String representing the name of the machine
     * @param port a String representing the port where the wen server is running
     */
    Menus(String name, String port){
        this.name = name;
        this.port = port;
    }

    /**
     * A method which states the delivery cost for a valid order
     * @param items a collection of Strings where each String represents an item that was ordered
     * @return the total delivery cost for the order, including the price of the item and the standard delivery charge
     */
    public static int  getDeliveryCost(String ... items) {
        int standardDeliveryCharge = 50;
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .build();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode()!=200){
                return 0;
            }
            Type listType = new TypeToken<ArrayList<Menu>>() {}.getType();
            ArrayList<Menu> menuList = new Gson().fromJson(response.body(), listType);


            HashMap<String,Integer> prices = new HashMap<>();
            for (Menu menu : menuList) {
                int size = menu.menu.size();
                for (int j = 0; j < size; j++) {
                    String currItem = menu.menu.get(j).item;
                    int currPence = menu.menu.get(j).pence;
                    prices.put(currItem, currPence);
                }
            }
            int total = standardDeliveryCharge;
            for (String item : items) {
                total += prices.get(item);
            }
            return total;
        }catch (IOException e){
            System.out.println("IOException " + e.getMessage());
        }catch (InterruptedException e){
            System.out.println("InterruptedException " + e.getMessage());
        }
        return 0;

    }
}
