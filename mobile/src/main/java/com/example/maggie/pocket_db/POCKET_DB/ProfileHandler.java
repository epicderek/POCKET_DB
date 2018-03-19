package com.example.maggie.pocket_db.POCKET_DB;
import java.util.*;
import android.database.sqlite.*;
import android.database.Cursor;
import android.content.*;

/**
 * Created by Maggie on 3/18/2018.
 */

public class ProfileHandler extends SQLiteOpenHelper
{
    private static final int DATA_BASE_VERSION = 1;
    private static final String TAG = "ProfileHandler";
    private static final String DATA_BASE_NAME = "ProfileManager";
    private static final String TABLE_NAME = "profile";

    private static final String PRO_ID = "profile id";
    private static final String FNAME,LNAME,MNAME,DIAL,EMAIL,GENDER,LOC_ID;
    static
    {
        FNAME = "first name";
        LNAME = "last name";
        MNAME = "middle name";
        DIAL = "phone number";
        EMAIL = "email address";
        GENDER = "gender";
        LOC_ID = "location id";
    }

    public static final String[] COLS = {FNAME,LNAME,MNAME,DIAL,EMAIL,GENDER,LOC_ID,PRO_ID};

    public ProfileHandler(Context con)
    {
        super(con,DATA_BASE_NAME,null,DATA_BASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_TABLE = String.format("create table %s ( %s varchar(10), %s varchar(10), %s varchar(10), %s varchar(15), %s varchar(40), %s enum(\"Female\",\"male\"), %s long unsigned, %s long unsigned, constraint pk_pro primary key (%s), constraint fk_loc foreign key (%s) references %s (%s));",TABLE_NAME,FNAME,LNAME,MNAME,DIAL,EMAIL,GENDER,LOC_ID,PRO_ID,PRO_ID,LOC_ID, LocationHandler.TABLE_NAME,LocationHandler.LOC_ID);
        db.execSQL(CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int old, int ne)
    {
        db.execSQL("drop table if exists "+TABLE_NAME);
        onCreate(db);
    }

    /**
     * Append the profile of this specific person into the database.
     * @param person The profile to be added into the database.
     */
    public void appendPerson(Profile person)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put(FNAME,person.getFirstN());
        vals.put(LNAME,person.getLastN());
        vals.put(MNAME,person.getMN());
        vals.put(DIAL,person.getDial());
        vals.put(EMAIL,person.getEmail());
        vals.put(GENDER,person.getGen()?"Female":"Male");
        vals.put(LOC_ID,person.getLoc()==null?null:person.getLoc().getId());
        vals.put(PRO_ID,person.getId());
        db.insert(TABLE_NAME,null,vals);
    }

    /**
     * Retrieve a person by the relevant information given in the array, whose sequence is given by the COLS in the Place Class.
     * @param quals The relevant information of this person organized in the order of the COLS of Place.
     */
    public List<Profile> getPerson(String[] quals, LocationHandler loch)
    {
        SQLiteDatabase db = getReadableDatabase();
        String where = String.format("%s = ? and %s = ? and %s = ? and %s = ? and %s = ? and %s = ?",FNAME,LNAME,MNAME,DIAL,EMAIL,GENDER);
        Cursor cur = db.query(TABLE_NAME,null,where,quals,null,null,null);
        List<Profile> pros = new LinkedList<>();
        String[] que = new String[6];
        while(cur.moveToNext())
        {
            for(int i=0; i<6; i++)
                que[i] = cur.getString(i);
            pros.add(Profile.processProfile(que,loch.getLocationById(cur.getLong(6)),cur.getLong(7)));
        }
        return pros;
    }

    /**
     * Retrieve a person from the database by the personal id.
     * @param id The id of the person to be retrieved.
     * @return The profile of this person correlated with this contact_id. Null if no profile of such person with such id exists.
     */
    public Profile getPersonById(long id, LocationHandler loch)
    {
        Profile output = new Profile();
        SQLiteDatabase db = getReadableDatabase();
        String where = String.format("%s = ?",PRO_ID);
        String[] cons = {String.valueOf(id)};
        Cursor cur = db.query(TABLE_NAME,null,where,cons,null,null,null);
        String[] que = new String[6];
        if(cur.moveToNext())
        {
            for(int i=0; i<6; i++)
                que[i] = cur.getString(i);
           return  Profile.processProfile(que,loch.getLocationById(cur.getLong(6)),cur.getLong(7));
        }
        return null;
    }
}
