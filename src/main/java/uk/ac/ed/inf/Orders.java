package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class Orders {
    private static final String jdbcString = "jdbc:derby://localhost:"+ App.databasePort+"/derbyDB";
    private static final HttpClient client = HttpClient.newHttpClient();
    public static HashMap<String, ArrayList<double[]>> pickUpCoordinates  = new HashMap<>();
    public static HashMap<String,HashSet<String>> pickUpWords = new HashMap<>();
    public static HashMap<String,double[]> deliveryCoordinates = new HashMap<>();
    public static HashMap<String,String> deliveryAddress = new HashMap<>();
    public static HashMap<String,String> customers = new HashMap<>();
    public static HashMap<String,String> items = new HashMap<>();
    public static ArrayList<String> orderNos = new ArrayList<>();
    private static Date deliveryDate = Date.valueOf(App.year+"-"+App.month+"-"+App.day);





    private static void getOrders(){
        try{
            Connection conn = DriverManager.getConnection(jdbcString);
            Statement statement = conn.createStatement();
            final String ordersQuery = "select * from orders where deliveryDate=(?)";
            PreparedStatement psCourseQuery = conn.prepareStatement(ordersQuery);
            psCourseQuery.setString(1, deliveryDate.toString());
            ResultSet rs = psCourseQuery.executeQuery();
            while (rs.next()) {
                String orderNumber = rs.getString("orderNo");
                String custName = rs.getString("customer");
                String deliverTo = rs.getString("deliverTo");
                orderNos.add(orderNumber);
                customers.put(orderNumber,custName);
                deliveryAddress.put(orderNumber,deliverTo);

            }
        }catch(java.sql.SQLException e){
            e.printStackTrace();
        }


    }
    private static void getOrderDetails(){
        try{
            Connection conn = DriverManager.getConnection(jdbcString);
            Statement statement = conn.createStatement();
            final String ordersQuery = "select * from orderDetails where orderNo=(?)";
            PreparedStatement psCourseQuery = conn.prepareStatement(ordersQuery);
            for (String orderNo : orderNos) {
                psCourseQuery.setString(1, orderNo);
                ResultSet rs = psCourseQuery.executeQuery();
                while (rs.next()) {
                    String orderNumber = rs.getString("orderNo");
                    String item = rs.getString("item");
                    items.put(orderNumber, item);


                }
            }

        }catch(java.sql.SQLException e){
            e.printStackTrace();
        }


    }
    private static void getDeliveryCoordinates(){
        for(int i=0;i<orderNos.size();i++){
            String addressString = deliveryAddress.get(orderNos.get(i));
            String[] address = addressString.split("\\.");

            String word1 = address[0];
            String word2 = address[1];
            String word3 = address[2];
            String urlString = "http://localhost:"+App.webServerPort+"/words/"+word1+"/"+word2+"/"+word3+"/details.json";

            try{
                //the request that would be sent to the website as a http request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlString))
                        .build();
                //once we sent the request, we save the response in "response"
                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());
                //check the status code to check if the request had failed or not
                if(response.statusCode()!=200){
                    System.out.println("Status code 200 returned; request failed to execute");
                }

                WordsDetails deliveryDetails = new Gson().fromJson(response.body(), WordsDetails.class);
                deliveryCoordinates.put(orderNos.get(i),new double[]{deliveryDetails.coordinates.lng,deliveryDetails.coordinates.lat});

             //catches any IO Exception or interrupted exception and prints the error.
            }catch (IOException e){
                System.out.println("IOException " + e.getMessage());
            }catch (InterruptedException e){
                System.out.println("InterruptedException " + e.getMessage());
            }
        }

    }


    private static void getPickUpWords(){
        String urlString = "http://localhost:"+App.webServerPort+"/menus/menus.json";
        for(int i =0;i<orderNos.size();i++){
            String[] itemsOrdered = items.get(orderNos.get(i)).split("\\.");
            HashSet<String> words = new HashSet<>();
            for(int j=0;j<itemsOrdered.length;j++){
                try{
                    //the request that would be sent to the website as a http request
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(urlString))
                            .build();
                    //once we sent the request, we save the response in "response"
                    HttpResponse<String> response =
                            client.send(request, HttpResponse.BodyHandlers.ofString());
                    //check the status code to check if the request had failed or not
                    if(response.statusCode()!=200){
                        System.out.println("Status code 200 returned; request failed to execute");
                    }

                    Type listType = new TypeToken<ArrayList<Menus.Menu>>() {}.getType();
                    //use the fromJson(String,Type) to get a list of the Menu.
                    ArrayList<Menus.Menu> menuList = new Gson().fromJson(response.body(), listType);
                    for (Menus.Menu menu : menuList) {
                        int size = menu.menu.size();
                        for (int k = 0; k < size; k++) {
                            // the current item
                            String currItem = menu.menu.get(j).item;
                            if(currItem.equals(itemsOrdered[j])){
                                words.add(menu.location);
                            }

                        }
                    }
                    //catches any IO Exception or interrupted exception and prints the error.
                }catch (IOException e){
                    System.out.println("IOException " + e.getMessage());
                }catch (InterruptedException e){
                    System.out.println("InterruptedException " + e.getMessage());
                }
            }
            pickUpWords.put(orderNos.get(i),words);

        }


    }
    private static void getPickUpCoordinates(){
        for(int i=0;i<orderNos.size();i++){
            HashSet<String> curr = pickUpWords.get(orderNos.get(i));
            Iterator val = curr.iterator();
            ArrayList<double[]> addressList = new ArrayList<>();
            while (val.hasNext()){
                String[] currWords = val.next().toString().split("\\.");
                String word1 = currWords[0];
                String word2 = currWords[1];
                String word3 = currWords[2];
                String urlString = "http://localhost:"+App.webServerPort+"/words/"+word1+"/"+word2+"/"+word3+"/details.json";
                try{
                    //the request that would be sent to the website as a http request
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(urlString))
                            .build();
                    //once we sent the request, we save the response in "response"
                    HttpResponse<String> response =
                            client.send(request, HttpResponse.BodyHandlers.ofString());
                    //check the status code to check if the request had failed or not
                    if(response.statusCode()!=200){
                        System.out.println("Status code 200 returned; request failed to execute");
                    }

                    WordsDetails pickUpDetails = new Gson().fromJson(response.body(), WordsDetails.class);
                    double[] n = new double[] { pickUpDetails.coordinates.lng,pickUpDetails.coordinates.lat};
                    addressList.add(n);
                    //catches any IO Exception or interrupted exception and prints the error.
                }catch (IOException e){
                    System.out.println("IOException " + e.getMessage());
                }catch (InterruptedException e){
                    System.out.println("InterruptedException " + e.getMessage());
                }
            }
            pickUpCoordinates.put(orderNos.get(i),addressList);


        }
    }
    public static void getCoordinatesAndOrders(){
        getOrders();
        getOrderDetails();
        getDeliveryCoordinates();
        getPickUpWords();
        getPickUpCoordinates();
//        for(int i=0;i<orderNos.size();i++){
//            System.out.println(Arrays.toString(deliveryCoordinates.get(orderNos.get(i))));
//        }
    }


}
