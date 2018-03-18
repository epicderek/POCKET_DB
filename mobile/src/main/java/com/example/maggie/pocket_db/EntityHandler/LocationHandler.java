package com.example.maggie.pocket_db.EntityHandler;
import com.example.maggie.pocket_db.EntityTable.*;
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
    private static final String TABLE_NAME = "location";

    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private static final String NAME = "name";
    private static final String TYPE = "location type";
    private static final String ADDRESS = "address";
    private static final String CITY = "city";
    private static final String COUNTRY = "country";
    private static final String ZIP = "zip";

    private static final String[] COLS = {LAT,LNG,NAME,TYPE,ADDRESS,CITY,COUNTRY,ZIP};

    public LocationHandler(Context con)
    {
        super(con,DATA_BASE_NAME,null,DATA_BASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_TABLE = String.format("create table %s ( %s double(25) unsigned, %s double(25) unsigned, %s varchar(20), %s varchar(10), %s varchar(50),%s varchar(10), %s varchar(10), %s smallint unsigned, constraint pk_loc primary key (%s, %s)",TABLE_NAME,LAT,LNG,NAME,TYPE,ADDRESS,CITY,COUNTRY,ZIP,LAT,LNG);
        db.execSQL(CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int old, int ne)
    {
        db.execSQL("drop table if exists "+TABLE_NAME);
        onCreate(db);
    }

    /**
     * Record this Location information in the sql database.
     * @param loc The location to be recorded in the database.
     */
    public void appendLocation(Location loc)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LAT,loc.getLat());
        values.put(LNG,loc.getLng());
        values.put(NAME,loc.getName());
        values.put(TYPE,loc.getType());
        values.put(ADDRESS,loc.getAddress());
        values.put(CITY,loc.getCity());
        values.put(COUNTRY,loc.getCountry());
        values.put(ZIP,loc.getZip());
        db.insert(TABLE_NAME,null,values);
        db.close();
    }

    /**
     * Get an location from the latitude and longitude.
     * @param lat
     * @param lon
     */
    public Location getLocation(double lat, double lon)
    {
        SQLiteDatabase db = getReadableDatabase();
        //The where constraint.
        String where = String.format("%s = ? and %s = ?",LAT,LNG);
        //The desired values of the constraints.
        String[] vals = {String.valueOf(lat),String.valueOf(lon)};
        Cursor cur = db.query(TABLE_NAME,null,where,vals,null,null,"name");
        Object[] que = new Object[8];
        if(cur.moveToNext())
        {
            que[0] = cur.getDouble(0);
            que[1] = cur.getDouble(1);
            que[2] = cur.getString(2);
            que[3] = cur.getString(3);
            que[4] = cur.getString(4);
            que[5] = cur.getString(5);
            que[6] = cur.getString(6);
            que[7] = cur.getInt(7);
        }
        return Location.processLocation(que);
    }



}
