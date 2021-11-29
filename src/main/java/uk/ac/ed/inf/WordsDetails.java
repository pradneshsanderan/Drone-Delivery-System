package uk.ac.ed.inf;

import java.net.URL;

public class WordsDetails {
    String country;
    Square square;
    public static class Square{
        Coordinates southwest;
        Coordinates northeast;
    }
    String nearestPlace;
    Coordinates coordinates;
    String words;
    String language;
    URL map;
    public static class Coordinates{
        float lng;
        float lat;
    }
}
