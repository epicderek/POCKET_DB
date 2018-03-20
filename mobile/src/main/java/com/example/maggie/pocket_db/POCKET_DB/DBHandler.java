package com.example.maggie.pocket_db.POCKET_DB;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Maggie on 3/20/2018.
 */

public class DBHandler extends SQLiteOpenHelper
{
    private static final int DATA_BASE_VERSION = 1;
    private static final String DATA_BASE_NAME = "pocket_database";
    private static final String[] TABLE_NAME = {"place","profile","event"};

    //Table information regarding Place.
    protected static final String LAT = "lat";
    protected static final String LNG = "lng";
    protected static final String NAME = "name";
    protected static final String TYPE = "Place type";

    /**
     * All the relevant information to this Place in accord with the categories given by the reverse geocode api of google.
     */
    protected static final String STREET_NUM,ROUTE,NEIGHBORHOOD,LOCALITY,ADMINISTRATIVE2,ADMINISTRATIVE1,COUNTRY,ZIP;
    /**
     * The formatted address of this Place.
     */
    protected static final String ADDRESS = "address";
    /**
     * A unique id to each Place stored in the database.
     */
    protected static final String LOC_ID = "loc_id";
    /**
     * All the columns of this table.
     */
    protected static final String[] COLS_PLACE;

    //Table information regarding Profile.
    protected static final String PRO_ID = "profile id";
    protected static final String FNAME,LNAME,MNAME,DIAL,EMAIL,GENDER;
    protected static final String[] COLS_PROFILE;

    //Auxiliary table of the miscellaneous names associated with a Place.
    private static final String AUX_NAME_TABLE_NAME = "auxiliary_place_names";
    private static final String PLA_NAME = "place name";

    static
    {
        //Initialization of table information regarding Place.
        STREET_NUM = "street number";
        ROUTE = "route";
        NEIGHBORHOOD = "neighborhood";
        LOCALITY = "city";
        ADMINISTRATIVE2 = "county";
        ADMINISTRATIVE1 = "state";
        COUNTRY = "country";
        ZIP = "postal code";
        COLS_PLACE = new String[]{LAT,LNG,NAME,TYPE,STREET_NUM,ROUTE,NEIGHBORHOOD,LOCALITY,ADMINISTRATIVE2,ADMINISTRATIVE1,COUNTRY,ZIP,ADDRESS};
        //Initialization of table information regarding Profile.
        FNAME = "first name";
        LNAME = "last name";
        MNAME = "middle name";
        DIAL = "phone number";
        EMAIL = "email address";
        GENDER = "gender";
        COLS_PROFILE = new String[]{FNAME,LNAME,MNAME,DIAL,EMAIL,GENDER,LOC_ID,PRO_ID};
    }

    public DBHandler(Context con)
    {
        super(con,DATA_BASE_NAME,null,DATA_BASE_VERSION);
    }

    /**
     * Create all requisite tables in the designated database.
     * @param db The database designated.
     */
    public void onCreate(SQLiteDatabase db)
    {
        String PLACE_TABLE = String.format("create table %s ( %s double(25) unsigned, %s double(25) unsigned, %s varchar(20), %s varchar(10), %s varchar(10), %s varchar(20), %s varchar(20), %s varchar(15), %s varchar(15), %s varchar(15), %s varchar(20), %s char(5), %s varchar(60), %s long unsigned auto_increment, constraint pk_loc primary key (%s, %s)",TABLE_NAME[0],LAT,LNG,NAME,TYPE,STREET_NUM,ROUTE,NEIGHBORHOOD,LOCALITY,ADMINISTRATIVE2,ADMINISTRATIVE1,COUNTRY,ZIP,ADDRESS,LOC_ID,LAT,LNG);
        String PROFILE_TABLE = String.format("create table %s ( %s varchar(10), %s varchar(10), %s varchar(10), %s varchar(15), %s varchar(40), %s enum(\"Female\",\"male\"), %s long unsigned, %s long unsigned, constraint pk_pro primary key (%s), constraint fk_loc foreign key (%s) references %s (%s));",TABLE_NAME[1],FNAME,LNAME,MNAME,DIAL,EMAIL,GENDER,LOC_ID,PRO_ID,PRO_ID,LOC_ID, TABLE_NAME[0],LOC_ID);
        String AUX_NAME_TABLE = String.format("create table %s ( %s long unsigned, %s varchar(20), constraint pk_pnames primary key (%s,%s), constraint fk_loc foreign key (%s) references %s (%s) );",AUX_NAME_TABLE_NAME,LOC_ID,PLA_NAME,LOC_ID,PLA_NAME,LOC_ID,TABLE_NAME[0],LOC_ID);
        db.execSQL(PLACE_TABLE);
        db.execSQL(PROFILE_TABLE);
        db.execSQL(AUX_NAME_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int old, int ne)
    {
        db.execSQL("drop table if exists "+TABLE_NAME[0]);
        db.execSQL("drop table if exists "+TABLE_NAME[1]);
        onCreate(db);
    }

    /**
     * Record this Place information in the sql database and return the loc_id of this Place. If the Place already exists in the database, the id of that Place is returned.
     * @param loc The Place to be recorded in the database.
     *@return The assigned place id of this place.
     */
    public long appendPlace(Place loc)
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
        loc.setId(db.insert(TABLE_NAME[0],null,values));
        return loc.getId();
    }

    /**
     * Update the primary name, the name directly stored in the Place object, of a Place by replacing the name.
     * @param name The new primary name to replace the previous preferred name.
     * @param id The id of this Place, whose preferred name is to be modified.
     */
    public void updatePrimaryPlaceName(String name, long id)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put(NAME,name);
        String where = String.format("%s = ?",LOC_ID);
        String[] cons = {String.valueOf(id)};
        db.update(TABLE_NAME[0],vals,where,cons);
        db.close();
    }

    /**
     * Append a secondary name of this Place. This method takes care of duplicity either in the primary name or the present secondary names by inspection.
     * @param name A secondary name representing this place.
     * @param id The id of this place, whose list of referred names are to be added another.
     */
    public void appendAuxPlaceName(String name, long id)
    {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cur = db.query(TABLE_NAME[0],new String[]{NAME},String.format("%s = ?",LOC_ID),new String[]{String.valueOf(id)},null,null,null);
        if(cur.moveToNext())
            if(cur.getString(0).equals(name))
                return;
        else
            return;
        ContentValues vals = new ContentValues();
        vals.put(LOC_ID,id);
        vals.put(PLA_NAME,name);
        db.insertWithOnConflict(TABLE_NAME[0],null,vals,SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
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
        Cursor cur = db.query(TABLE_NAME[0],null,where,allow,null,null,String.format("LAT-%s+LNG-%s",lat,lon));
        return cur;
    }

    /**
     * A convenient method of getting the Id of a 
     * @param lat
     * @param lon
     * @return
     */
//    public long getLocId(double lat, double lon)
//    {
//
//    }


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
        Cursor cur = db.query(TABLE_NAME[0],null,where,cons,null,null,null);
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


    /**
     * Retrieve a person by the relevant information given in the array, whose sequence is given by the COLS in the Place Class.
     * @param quals The relevant information of this person organized in the order of the COLS of Place.
     */
    public List<Profile> getPerson(String[] quals)
    {
        SQLiteDatabase db = getReadableDatabase();
        String where = String.format("%s = ? and %s = ? and %s = ? and %s = ? and %s = ? and %s = ?",FNAME,LNAME,MNAME,DIAL,EMAIL,GENDER);
        Cursor cur = db.query(TABLE_NAME[1],null,where,quals,null,null,null);
        List<Profile> pros = new LinkedList<>();
        String[] que = new String[6];
        while(cur.moveToNext())
        {
            for(int i=0; i<6; i++)
                que[i] = cur.getString(i);
            pros.add(Profile.processProfile(que,getPlaceById(cur.getLong(6)),cur.getLong(7)));
        }
        return pros;
    }

    /**
     * Retrieve a person from the database by the personal id.
     * @param id The id of the person to be retrieved.
     * @return The profile of this person correlated with this contact_id. Null if no profile of such person with such id exists.
     */
    public Profile getPersonById(long id)
    {
        Profile output = new Profile();
        SQLiteDatabase db = getReadableDatabase();
        String where = String.format("%s = ?",PRO_ID);
        String[] cons = {String.valueOf(id)};
        Cursor cur = db.query(TABLE_NAME[1],null,where,cons,null,null,null);
        String[] que = new String[6];
        if(cur.moveToNext())
        {
            for(int i=0; i<6; i++)
                que[i] = cur.getString(i);
            return  Profile.processProfile(que,getPlaceById(cur.getLong(6)),cur.getLong(7));
        }
        return null;
    }

    public void retrieve()
    {

    }

}

