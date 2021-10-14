package uk.ac.ed.inf;

public class Menus {
    public String name;
    public String port;

    /**
     *
     * @param name
     * @param port
     */
    Menus(String name, String port){
        this.name = name;
        this.port = port;
    }

    /**
     * 
     * @param items
     * @return
     */
    public static int  getDeliveryCost(String ... items){
        
        return 1;
    }
}
