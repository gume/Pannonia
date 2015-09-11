package hu.edudroid.gume.pannoniaDB;

/**
 * Created by gume7 on 9/9/2015.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class PannoniaDBHelper extends SQLiteOpenHelper {

    private static PannoniaDBHelper _instance;

    private PannoniaDBHelper(Context context) {
        super(context, "database", null, 1);
    }

    public static PannoniaDBHelper getInstance(Context context) {
        if (_instance == null)
        {
            _instance = new PannoniaDBHelper(context);
        }
        return _instance;
    }

    public static PannoniaDBHelper getInstance() {
        return _instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql;

        sql = "CREATE TABLE courses (name TEXT, day INTEGER, hour INTEGER, course TEXT, PRIMARY KEY (name, day, hour));";
        db.execSQL(sql);

        sql = "CREATE TABLE students (name TEXT, omid INTEGER, class TEXT, password TEXT, PRIMARY KEY (omid));";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    public ArrayList<ArrayList<String>> getCourses(String name) {

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<ArrayList<String>> aad = new ArrayList<ArrayList<String>>();
        for (int d = 0; d < 5; d++) {
            Cursor c = db.query("courses", new String[] { "hour", "course" },
                    "name =? AND day =?", new String[] { name, String.valueOf(d) },
                    null, null, "hour");

            ArrayList<String> ad = new ArrayList<String>();
            for (int i = 0; i < 12; i++) {
                ad.add("?");
            }
            if (c != null) {
                while (c.moveToNext()) {
                    int hour = c.getShort(0);
                    String course = c.getString(1);
                    ad.set(hour - 8, course);
                }
            }
            c.close();
            aad.add(ad);
        }
        db.close();

        return aad;
    }

    public void setCourse(String name, int day, int hour, String course) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("day", day);
        cv.put("hour", hour);
        cv.put("course", course);
        db.insertWithOnConflict("courses", null, cv, SQLiteDatabase.CONFLICT_REPLACE);

        db.close();
    }

    public List<String> getStudents() {

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> l = new ArrayList<String>();

        Cursor c = db.query("students", new String[] {"omid" }, null, null, null, null, "name");
        if (c != null) {
            while (c.moveToNext()) {
                String omid = c.getString(0);
                l.add(omid);
            }
            c.close();
        }
        db.close();
        return l;
    }

    public HashMap<String, String> getStudent(String omid) {

        SQLiteDatabase db = this.getReadableDatabase();
        HashMap<String, String> m = new HashMap<String, String>();

        Cursor c = db.query("students", new String[] { "name", "omid", "class", "password" },
                "omid =?", new String[] { omid }, null, null, null);
        if (c != null) {
            c.moveToFirst();
            m.put("name", c.getString(0));
            m.put("omid", omid);
            m.put("class", c.getString(2));
            m.put("password", c.getString(3));
            c.close();
        }
        db.close();
        return m;
    }

    public void setStudent(String name, String omid, String clas, String password) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("omid", omid);
        cv.put("class", clas);
        cv.put("password", password);
        db.insertWithOnConflict("students", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void delStudent(String omid) {

        SQLiteDatabase db = getWritableDatabase();
        db.delete("students", "omid =?", new String[] { omid });
        db.close();
    }

}

