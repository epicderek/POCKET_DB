package com.example.maggie.pocket_db.EntityTable;
import java.io.IOException;
import java.util.*;

/**
 * Created by Maggie on 3/18/2018.
 */


/**
 * A comprehensive representation of a geographical position in terms of a coordinate with the times accessed, people related, and events involved. Locations of different associated information but identical coordinates are stored as one location object. A search for a location object given the time, person, or event concerned may be conducted. Relevant information of this location such as the zip code and the region are derived.
 * @author Derek
 *
 */
public class Location
{
    /**
     * The coordinate of the location.
     */
    private double lon, lat;
    /**
     * The name of the place.
     */
    private String name;
    /**
     * The type of location.
     */
    private String type;
    /**
     * The formatted address.
     */
    private String formatted;
    /**
     * The zip code of this location.
     */
    private int zip;
    private String city;
    private String country;

    public Location(double lat, double lon)
    {
        this.lat = lat;
        this.lon = lon;
        reverseGeocode();
    }

    protected Location()
    {

    }

    public double getLat()
    {
        return lat;
    }

    public double getLng()
    {
        return lon;
    }

    public String getName()
    {
        return name;
    }

    public String getAddress()
    {
        return formatted;
    }

    public String getType()
    {
        return type;
    }

    public int getZip()
    {
        return zip;
    }

    public String getCity()
    {
        return city;
    }

    public String getCountry()
    {
        return country;
    }

    private void reverseGeocode()
    {

    }

    /**
     * Process a location extracted from the database.
     * @param vals The values returned by the query. All information must be present.
     * @return The Location equivalent to the information extracted from the database.
     */
    public static Location processLocation(Object[] vals)
    {
        Location output = new Location();
        output.lat = (double)vals[0];
        output.lon = (double)vals[1];
        output.name = (String)vals[2];
        output.type = (String)vals[3];
        output.formatted = (String) vals[4];
        output.city = (String)vals[5];
        output.country = (String)vals[6];
        output.zip = (int)vals[7];
        return output;
    }


}
