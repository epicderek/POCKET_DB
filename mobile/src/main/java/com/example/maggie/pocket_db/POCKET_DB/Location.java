package com.example.maggie.pocket_db.POCKET_DB;
import java.io.*;
import java.net.URL;

import javax.json.*;


/**
 * Created by Maggie on 3/18/2018.
 */


/**
 * A comprehensive representation of a geographical position in terms of a coordinate with the times accessed, people related, and events involved. Locations of different associated information but identical coordinates are stored as one location object. A search for a location object given the time, person, or event concerned may be conducted. Relevant information of this location such as the zip code and the region are derived.
 * @author Derek
 *
 */
public class Location {
    /**
     * The coordinate of the location.
     */
    private double lon, lat;
    /**
     * A preferred name of this place.
     */
    private String name;
    /**
     * The type of this location, premise, coffee shop, ect. .
     */
    private String type;
    /**
     * All the relevant information to this location in accord with the categories given by the reverse geocode api of google.
     */
    private String street_num, route, neighborhood, locality, administrative2, administrative1, country, zip;
    /**
     * The formatted address of this place.
     */
    private String formatted;
    /**
     * The location id of this Location in the database.
     */
    private long loc_id;
    /*
        The portions of the url for the google api.
     */
    private static final String uFirst;
    private static final String uSecond;
    /**
     * The api keys for the google api.
     */
    private static String[] keys;
    /**
     * The counter that keeps track of the number of keys exhausted.
     */
    public static int keyCount;

    static {
        keys = new String[]{"AIzaSyAe67qjsHOQomCNyIIyi_UKm5uqNvTGcvA", "AIzaSyDzqYwvSvnHhBAPf0ZisEPCuZWZv2ldry4", "AIzaSyDG4Gjp_mW0T25VA17Jmk5pYJRJUFvnbUA", "AIzaSyDnPcdlEZyqR6Cr2ite9aAvoTMDzDhty_E", "AIzaSyDnfk0csfpKC1G12EUh4BzyuXOoa_B0fKE", "AIzaSyCZ-htBGcdRL3edgKX3XIHbvcH52Z-ITIk", "AIzaSyCmhv5_zalf68jlqsdeZAbwJxzsbCy7U4k", "AIzaSyC7_s8YWKxnaKqswFBp7riTccHxNI3EBoA", "AIzaSyC-a4AUdyCipXHFev5hopOUz_Mo0QVg2Tk"};
        keyCount = 0;
        uFirst = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
        uSecond = "&key=";
    }

    /**
     * Create a location of the given latitude and longitude along with an optional name.
     *
     * @param lat  The latitude.
     * @param lon  The longitude.
     * @param name
     */
    public Location(double lat, double lon, String... name) {
        this.lat = lat;
        this.lon = lon;
        this.name = name[0];
    }

    /**
     * Reserved constructor for inputting from the database.
     */
    private Location() {

    }

    protected void setId(long id)
    {
        loc_id = id;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lon;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getStreetNum()
    {
        return street_num;
    }

    public String getRoute()
    {
        return route;
    }

    public String getNeighborhood()
    {
        return neighborhood;
    }

    public String getCity()
    {
        return locality;
    }

    public String getCounty()
    {
        return administrative2;
    }

    public String getState()
    {
        return administrative1;
    }

    public String getCountry()
    {
        return country;
    }

    public String getZip()
    {
        return zip;
    }

    public String getAddress()
    {
        return formatted;
    }

    protected long getId()
    {
        return loc_id;
    }

    /**
     * A helper method that computes relevant geographic information based on the latitude and longitude given through the google map geocoding api. The results are recorded in the given map whose keys, to be filled by this method along with their proper values, are the enumerated types declared in PhyLocation.
     * @param lati The latitude of the location.
     * @param lon The longitude of the location.
     * @return An object array whose first index is the formatted address, the second the types of this location, third and fourth the adjusted coordinate returned by the google map that comports with the facility located.
     * @throws IOException
     */
    protected void interpretLoc(Double lati, Double lon) throws IOException
    {
        URL url = new URL(String.format("%s%s,%s%s%s",uFirst,lati,lon,uSecond,keys[keyCount]));
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder json = new StringBuilder();
        String line;
        while((line=reader.readLine())!=null)
        {
            json.append(line);
            json.append("\n");
        }
        String resp = json.substring(0,json.length()-1);
        JsonReader read = Json.createReader(new StringReader(resp));
        JsonObject loc = read.readObject().getJsonArray("results").getJsonObject(0);
        JsonArray add = loc.getJsonArray("address_components");
        for(JsonValue holder: add)
        {
            JsonObject obj = (JsonObject)holder;
            if(obj.getJsonArray("types").getString(0).equals("street_number"))
                street_num = obj.getString("long_name");
            else if(obj.getJsonArray("types").getString(0).equals("route"))
                route = obj.getString("short_name");
            else if(obj.getJsonArray("types").getString(0).equals("neighborhood"))
                neighborhood = obj.getString("short_name");
            else if(obj.getJsonArray("types").getString(0).equals("locality"))
                locality = obj.getString("short_name");
            else if(obj.getJsonArray("types").getString(0).equals("administrative_area_level_2"))
               administrative2 = obj.getString("short_name");
            else if(obj.getJsonArray("types").getString(0).equals("administrative_area_level_1"))
                administrative1 = obj.getString("short_name");
            else if(obj.getJsonArray("types").getString(0).equals("country"))
                country = obj.getString("short_name");
            else if(obj.getJsonArray("types").getString(0).equals("postal_code"))
                zip = obj.getString("short_name");
        }
        formatted =  loc.getString("formatted_address");
        JsonObject geo = loc.getJsonObject("geometry");
        type = (String)loc.getJsonArray("types").toArray()[0];
        JsonObject lal = geo.getJsonObject("location");
        lat = lal.getJsonNumber("lat").doubleValue() ;
        lon = lal.getJsonNumber("lng").doubleValue();
    }

    /**
     * Process a location extracted from the database.
     * @param vals The values returned by the query. All information must be present. The sequence must be as it is stored in the database.
     * @return The Location equivalent to the information extracted from the database.
     */
    public static Location processLocation(Object[] vals)
    {
        Location output = new Location();
        output.lat = (double)vals[0];
        output.lon = (double)vals[1];
        output.name = (String)vals[2];
        output.type = (String)vals[3];
        output.street_num = (String)vals[4];
        output.route = (String)vals[5];
        output.neighborhood = (String)vals[6];
        output.locality = (String)vals[7];
        output.administrative2 = (String)vals[8];
        output.administrative1 = (String)vals[9];
        output.country = (String)vals[10];
        output.zip = (String)vals[11];
        output.formatted = (String) vals[12];
        return output;
    }


}