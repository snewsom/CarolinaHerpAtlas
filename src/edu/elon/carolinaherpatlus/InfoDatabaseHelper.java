/**
 * InfoDatabaseHelper.java 1.0 May 9, 2013
 * 
 * COPYRIGHT (c) 2013 David B. Belyea. All Rights Reserved
 */
package edu.elon.carolinaherpatlus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Start with summary description line
 * 
 * @author dbelyea
 * @version 1.0
 * 
 */
public class InfoDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "herpderb.sqlite";
    private static final int VERSION = 1;

    /**
     * @param context
     * @param name
     * @param factory
     * @param version
     */
    public InfoDatabaseHelper(Context context) {

        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table location ("
                                        + " state text, county text, group text, genus text,"
                                        + " species text, common text, date text, location text,"
                                        + " east real, north real, zone real, comments texts, photoPath text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertLocation(String state, String county, String group, String genus,
                                    String species, String commonName, String date,
                                    String location, float east, float north, float zone,
                                    String comments, String photoPath) {

        ContentValues cv = new ContentValues();
        cv.put("state", state);
        cv.put("county", county);
        cv.put("group", group);
        cv.put("genus", genus);
        cv.put("species", species);
        cv.put("commonName", commonName);
        cv.put("date", date);
        cv.put("location", location);
        cv.put("east", east);
        cv.put("north", north);
        cv.put("zone", zone);
        cv.put("comments", comments);
        cv.put("photoPath", photoPath);

        return getWritableDatabase().insert("location", null, cv);
    }

    public Cursor queryDB() {

        Cursor c = getReadableDatabase().query("location", null, null, null, null, null, null);
        return c;
    }

    public void clear() {

        getWritableDatabase().execSQL("delete from location");
    }
}