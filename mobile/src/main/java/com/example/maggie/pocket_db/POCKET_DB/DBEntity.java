package com.example.maggie.pocket_db.POCKET_DB;
import java.util.Map;
import java.util.HashMap;

/**
 * A requisite template for all the entities of allowed data types comprised in POCKET to serve as a convenient and standard means of database storage and retrieval. Each data type is to inherit from this abstract class which contains a map with its keys representing the fields of the data type, and its values as its values. Each data type must also include a method responsible for filling this map. This class also contains an enumerated type consists of all the allowed data types.
 */
public abstract class DBEntity
{
    protected Map<String,Object> info = new HashMap<String,Object>();

    /**
     * Fill the information map with respect to all the fields and their according values.
     */
    protected abstract void fillMap();

    public enum DBEntityType
    {
        PLACE,PROFILE,EVENT;
    }
}
