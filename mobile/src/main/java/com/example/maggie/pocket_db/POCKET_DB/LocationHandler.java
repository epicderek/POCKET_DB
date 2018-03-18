package com.example.maggie.pocket_db.POCKET_DB;
import android.database.sqlite.*;
import android.database.Cursor;
import android.content.*;

/**
 * Created by Maggie on 3/18/2018.
 */

public class LocationHandler extends SQLiteOpenHelper
{
    private static final int DATA_BASE_VERSION = 1;
    private static final String TAG = "LocationHandler";
    private static final String DATA_BASE_NAME = "LocationManager";
    protected static final String TABLE_NAME = "location";

    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private static final String NAME = "name";
    private static final String TYPE = "location type";

    /**
     * All the relevant information to this location in accord with the categories given by the reverse geocode api of google.
     */
    private static final String STREET_NUM,ROUTE,NEIGHBORHOOD,LOCALITY,ADMINISTRATIVE2,ADMINISTRATIVE1,COUNTRY,ZIP;
    /**
     * The formatted address of this location.
     */
    private static final String ADDRESS = "address";
    /**
     * A unique id to each location stored in the database.
     */
    protected static final String LOC_ID = "loc_id";
    /**
     * All the columns of this table.
     */
    protected static final String[] COLS;

    static
    {
        STREET_NUM = "street number";
        ROUTE = "route";
        NEIGHBORHOOD = "neighborhood";
        LOCALITY = "city";
        ADMINISTRATIVE2 = "county";
        ADMINISTRATIVE1 = "state";
        COUNTRY = "country";
        ZIP = "postal code";
        COLS = new String[]{LAT,LNG,NAME,TYPE,STREET_NUM,ROUTE,NEIGHBORHOOD,LOCALITY,ADMINISTRATIVE2,ADMINISTRATIVE1,COUNTRY,ZIP,ADDRESS};
    }

    public LocationHandler(Context con)
    {
        super(con,DATA_BASE_NAME,null,DATA_BASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_TABLE = String.format("create table %s ( %s double(25) unsigned, %s double(25) unsigned, %s varchar(20), %s varchar(10), %s varchar(10), %s varchar(20), %s varchar(20), %s varchar(15), %s varchar(15), %s varchar(15), %s varchar(20), %s char(5), %s varchar(60), %s long unsigned auto_increment, constraint pk_loc primary key (%s, %s)",TABLE_NAME,LAT,LNG,NAME,TYPE,STREET_NUM,ROUTE,NEIGHBORHOOD,LOCALITY,ADMINISTRATIVE2,ADMINISTRATIVE1,COUNTRY,ZIP,ADDRESS,LOC_ID,LAT,LNG);
        db.execSQL(CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int old, int ne)
    {
        db.execSQL("drop table if exists "+TABLE_NAME);
        onCreate(db);
    }

    /**
     * Record this Location information in the sql database and return the loc_id of this location. If the location already exists in the database, the id of that location is returned.
     * @param loc The location to be recorded in the database.
     *
     */
    public Long appendLocation(Location loc)
    {
        Cursor cur = dupSearch(loc.getLat(),loc.getLng());
        //If the location already exists in the database.
        if(cur.moveToNext())
            return cur.getLong(13);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LAT,loc.getLat());
        values.put(LNG,loc.getLng());
        values.put(NAME,loc.getName());
        values.put(TYPE,loc.getType());
        values.put(STREET_NUM,loc.getStreetNum());
        values.put(ROUTE,loc.getRoute());
        values.put(NEIGHBORHOOD,loc.getNeighborhood());
        values.put(LOCALITY,loc.getCity());
        values.put(ADMINISTRATIVE2,loc.getCounty());
        values.put(ADMINISTRATIVE1,loc.getState());
        values.put(COUNTRY,loc.getCountry());
        values.put(ZIP,loc.getZip());
        values.put(ADDRESS,loc.getAddress());
        loc.setId(db.insert(TABLE_NAME,null,values));
        return loc.getId();
    }

    /**
     * Get an location from the current database by its latitude and longitude.If there presently exists no location qualified within the allowances, null is returned.
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     */
    public Location getLocation(double lat, double lon)
    {
        Cursor cur = dupSearch(lat,lon);
        Object[] que = new Object[13];
        if(cur.moveToNext()) {
           //The longitude and latitude.
            que[0] = cur.getDouble(0);
            que[1] = cur.getDouble(1);
            //The rest of the information in string format.
            for(int i=2; i<13; i++)
                que[i] = cur.getString(i);
            return Location.processLocation(que);
        }
        return null;
    }

    /**
     * Browse the database to check for any existent location identical to this location within the tolerance of 0.0001 in both latitude and longitude.
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return A cursor that might contain the location queried.
     */
    public Cursor dupSearch(double lat, double lon)
    {
        SQLiteDatabase db = getReadableDatabase();
        //The where statement in which the allowance of the difference in position is specified.
        String where = String.format("%s-%s <= ? and %s-%s <= ?",LAT,lat,LNG,lon);
        String[] allow = {String.valueOf(0.0001),String.valueOf(0.0001)};
        Cursor cur = db.query(TABLE_NAME,null,where,allow,null,null,String.format("LAT-%s+LNG-%s",lat,lon));
        return cur;
    }




}
