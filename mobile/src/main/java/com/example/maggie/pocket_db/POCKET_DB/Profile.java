package com.example.maggie.pocket_db.POCKET_DB;

/**
 * Created by Maggie on 3/18/2018.
 */

public class Profile
{
    /**
     * The id assigned to this person.
     */
    protected long contact_id;
    /**
     * Counter of given ids.
     */
    private static int id_count;

    private String fname;
    private String lname;
    private String mname;
    private String name;
    private boolean gen;
    private String dial;
    private String email;
    private Place home;

    /**
     * Initialize a person with his/her first name and last name requisite.
     * @param fname First name.
     * @param lname Last name.
     * @param mn Optional middle name.
     */
    public Profile(String fname, String lname,String... mn)
    {
        this.fname = fname;
        this.lname = lname;
        this.contact_id = id_count++;
        StringBuilder builder = new StringBuilder();
        name = builder.append(fname).append(lname).append(mn.length==0?"":mn[0]).toString();
    }

    protected Profile()
    {

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

    public void setDial(String cal)
    {
        dial = cal;
    }

    public void setPlace(Place loc)
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

    public String getDial()
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

    public Place getLoc()
    {
        return home;
    }

    protected long getId()
    {
        return contact_id;
    }

    /**
     * Import and generate a Profile data from the given information of the profile in the order of the COLS.
     * @param que The textual information.
     * @param home The Place of this person's home.
     * @param id The contact_id of this person.
     * @return
     */
    public static Profile processProfile(String[] que, Place home, long id)
    {
        Profile output = new Profile();
        output.fname = que[0];
        output.lname = que[1];
        output.mname = que[2];
        output.name = que[3];
        output.gen = que[4].equals("Female")?true:false;
        output.dial = que[5];
        output.email = que[6];
        output.home = home;
        output.contact_id = id;
        return output;
    }















}

