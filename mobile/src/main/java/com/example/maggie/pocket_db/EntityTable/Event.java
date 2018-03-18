package com.example.maggie.pocket_db.EntityTable;
import com.example.maggie.pocket_db.POCKET_DB.Profile;

import java.util.*;

/**
 * Created by Maggie on 3/18/2018.
 */

public class Event
{
    /**
     * The title, succinct description of this event.
     */
    private String title;

    //Times associated with this event.
    private long startTime;
    private long endTime;
    private long stamp;

    /**
     * The organizer of this event.
     */
    private Profile org;
    /**
     * The participants of this event,
     */
    private List<Profile> part = new ArrayList<>();

    public Event(String desc, long stamp, Profile orga, Profile... pro)
    {
        title = desc;
        this.stamp = stamp;
        org = orga;
        Collections.addAll(part,pro);
    }

    public void addStart(long start)
    {
        startTime = start;
    }

    public void addEnd(long end)
    {
        endTime = end;
    }

    public String getTitle()
    {
        return title;
    }

    public long getTime()
    {
        return stamp;
    }

    public long getStart()
    {
        return startTime;
    }

    public long getEnd()
    {
        return endTime;
    }

    public Profile getOrg()
    {
        return org;
    }

    public Profile[] getPart()
    {
        return part.toArray(new Profile[part.size()]);
    }

}
