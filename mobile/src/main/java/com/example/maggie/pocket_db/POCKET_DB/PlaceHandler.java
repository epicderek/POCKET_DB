package com.example.maggie.pocket_db.POCKET_DB;
import android.database.sqlite.*;
import android.database.Cursor;
import android.content.*;

/**
 * Created by Maggie on 3/18/2018.
 */

public class PlaceHandler extends SQLiteOpenHelper
{
    private static final int DATA_BASE_VERSION = 1;
    private static final String TAG = "PlaceHandler";
    private static final String DATA_BASE_NAME = "PlaceManager";
    protected static final String TABLE_NAME = "Place";

    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private static final String NAME = "name";
    private static final String TYPE = "Place type";

    /**
     * All the relevant information to this Place in accord with the categories given by the reverse geocode api of google.
     */
    private static final String STREET_NUM,ROUTE,NEIGHBORHOOD,LOCALITY,ADMINISTRATIVE2,ADMINISTRATIVE1,COUNTRY,ZIP;
    /**
     * The formatted address of this Place.
     */
    private static final String ADDRESS = "address";
    /**
     * A unique id to each Place stored in the database.
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

    public PlaceHandler(Context con)
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
     * Record this Place information in the sql database and return the loc_id of this Place. If the Place already exists in the database, the id of that Place is returned.
     * @param loc The Place to be recorded in the database.
     *
     */
    public Long appendPlace(Place loc)
    {
        Cursor cur = dupSearch(loc.getLat(),loc.getLng());
        //If the Place already exists in the database.
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
     * Get an Place from the current database by its latitude and longitude.If there presently exists no Place qualified within the allowances, null is returned.
     * @param lat The latitude of the Place.
     * @param lon The longitude of the Place.
     */
    public Place getPlace(double lat, double lon)
    {
        Cursor cur = dupSearch(lat,lon);
        Object[] que = new Object[14];
        if(cur.moveToNext()) {
           //The longitude and latitude.
            que[0] = cur.getDouble(0);
            que[1] = cur.getDouble(1);
            //The rest of the information in string format.
            for(int i=2; i<13; i++)
                que[i] = cur.getString(i);
            que[13] = cur.getLong(13);
            return Place.processPlace(que);
        }
        return null;
    }

    /**
     * Browse the database to check for any existent Place identical to this Place within the tolerance of 0.0001 in both latitude and longitude.
     * @param lat The latitude of the Place.
     * @param lon The longitude of the Place.
     * @return A cursor that might contain the Place queried.
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

    /**
     * Retrieve a Place from the database by the locational id.
     * @param id The id of the location to be retrieved.
     * @return The place correlated with this locational id. Null if no profile of such person with such id exists.
     */
    public Place getPlaceById(long id)
    {
        SQLiteDatabase db = getReadableDatabase();
        String where = String.format("%s = ?",LOC_ID);
        String[] cons = {String.valueOf(id)};
        Cursor cur = db.query(TABLE_NAME,null,where,cons,null,null,null);
        Object[] que = new Object[14];
        if(cur.moveToNext())
        {
            //The longitude and latitude.
            que[0] = cur.getDouble(0);
            que[1] = cur.getDouble(1);
            //The rest of the information in string format.
            for(int i=2; i<13; i++)
                que[i] = cur.getString(i);
            que[13] = cur.getLong(13);
            return Place.processPlace(que);
        }
        return null;
    }

}
