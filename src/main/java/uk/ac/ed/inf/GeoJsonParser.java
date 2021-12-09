package uk.ac.ed.inf;

import com.mapbox.geojson.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GeoJsonParser {
    private static final String urlStringNoFlyZones = "http://localhost:"+App.webServerPort+"/buildings/no-fly-zones.geojson";
    private static final String urlStringLandmarks = "http://localhost:"+App.webServerPort+"/buildings/landmarks.geojson";
    private static final HttpClient client = HttpClient.newHttpClient();
    public static List<Feature> noFlyZoneFeatures;
    public static List<Feature> landmarkFeatures;


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
    public static String movesToFCCollection(List<LongLat> moves){
        List<Point> pointList = new ArrayList<>();
        for (LongLat move : moves) {
            pointList.add(move.point);
        }
        Geometry geometry = (Geometry) LineString.fromLngLats(pointList);
        FeatureCollection fc = FeatureCollection.fromFeature(Feature.fromGeometry(geometry));
        return fc.toJson();
    }
    public static void outputGeoJsonFile(String fc){
        File gj = new File(Paths.get(".").toAbsolutePath().normalize().toString() +"\\drone-"+App.day+"-"+App.month+"-"+App.year+".geojson");
        try{
            FileWriter writer = new FileWriter("drone-"+App.day+"-"+App.month+"-"+App.year+".geojson");
            writer.write(fc);
            writer.close();
        }catch (IOException e){
            System.err.println("IO Exception Erroe");
            e.printStackTrace();
        }

    }

}
