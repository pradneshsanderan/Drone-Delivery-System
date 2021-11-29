package uk.ac.ed.inf;

/**
 * Hello world!
 *
 */
public class App 
{
    public static String day;
    public static String month;
    public static String year;
    public static int webServerPort;
    public static int databasePort;
    public static void main( String[] args ) {
        day = args[0];
        month= args[1];
        year = args[2];
        webServerPort = Integer.parseInt( args[3]);
        databasePort = Integer.parseInt(args[4]);
    }
}
