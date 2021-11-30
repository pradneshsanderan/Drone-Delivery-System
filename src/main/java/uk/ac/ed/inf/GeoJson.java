package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GeoJson {
    private static final String urlStringNoFlyZones = "http://localhost:9898/buildings/no-fly-zones.geojson";
    private static final String urlStringLandmarks = "http://localhost:9898/buildings/landmarks.geojson";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static List<Feature> noFlyZoneFeatures;
    private static List<Feature> landmarkFeatures;


    public static void getNoFlyZones(){
        try{
            //the request that would be sent to the website as a http request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlStringNoFlyZones))
                    .build();
            //once we sent the request, we save the response in "response"
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            //check the status code to check if the request had failed or not
            if(response.statusCode()!=200){
                System.out.println("wrong status code");

            }
            FeatureCollection featureCollection = FeatureCollection.fromJson(response.body());
            noFlyZoneFeatures = featureCollection.features();
            //catches any IO Exception or interrupted exception and prints the error.
        }catch (IOException e){
            System.out.println("IOException " + e.getMessage());
        }catch (InterruptedException e){
            System.out.println("InterruptedException " + e.getMessage());
        }
    }
    public static void getLandmarks(){
        try{
            //the request that would be sent to the website as a http request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlStringLandmarks))
                    .build();
            //once we sent the request, we save the response in "response"
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            //check the status code to check if the request had failed or not
            if(response.statusCode()!=200){
                System.out.println("wrong status code");

            }
            FeatureCollection featureCollection = FeatureCollection.fromJson(response.body());
            landmarkFeatures = featureCollection.features();
            //catches any IO Exception or interrupted exception and prints the error.
        }catch (IOException e){
            System.out.println("IOException " + e.getMessage());
        }catch (InterruptedException e){
            System.out.println("InterruptedException " + e.getMessage());
        }
    }
}
