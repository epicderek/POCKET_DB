package com.example.maggie.pocket_db.EntityTable;

/**
 * Created by Maggie on 3/18/2018.
 */

public class Profile
{
    /**
     * The id assigned to this person.
     */
    private int contact_id;

    private String fname;
    private String lname;
    private String mname;
    private String name;
    private boolean gen;
    private long dial;
    private String email;
    private Location home;

    public Profile(String fname, String lname, int countact_id, String... mn)
    {
        this.fname = fname;
        this.lname = lname;
        this.contact_id = contact_id;
        StringBuilder builder = new StringBuilder();
        name = builder.append(fname).append(lname).append(mn.length==0?"":mn[0]).toString();
    }

    /**
     * Set the gender of this person.
     * @param gen The gender of the person. A True corresponds to a woman; a false corresponds to a man.
     */
    public void setGender(boolean gen)
    {
        this.gen = gen;
    }

    public void setMName(String mname)
    {
        this.mname = mname;
        StringBuilder builder = new StringBuilder();
        name = builder.append(fname).append(lname).append(mname).toString();
    }


    public void setDial(long cal)
    {
        dial = cal;
    }

    public void setLocation(Location loc)
    {
        home = loc;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getName()
    {
        return name;
    }

    public String getFirstN()
    {
        return fname;
    }

    public String getLastN()
    {
        return lname;
    }

    public String getMN()
    {
        return mname;
    }

    public long getDial()
    {
        return dial;
    }

    public String getEmail()
    {
        return email;
    }

    public boolean getGen()
    {
        return gen;
    }

    public Location getLoc()
    {
        return home;
    }















}

