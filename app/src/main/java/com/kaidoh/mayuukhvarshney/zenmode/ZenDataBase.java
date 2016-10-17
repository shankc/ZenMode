package com.kaidoh.mayuukhvarshney.zenmode;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
/**
 * Created by mayuukhvarshney on 16/10/16.
 */
public class ZenDataBase extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION=2;
    public static final String DATABASE_NAME = "ZenData";
    public static final String TABLE_ZEN = "ZEN_TABLE";

    private static String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_ZEN_HOURS = "zen_hours";
    private static final String KEY_MOVEMENT_HOURS = "movement_hours";
    ContentValues values = new ContentValues();
    public ZenDataBase(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    String CREATE_ZEN_HOURS_TABLE = "CREATE TABLE " + TABLE_ZEN + "("+ KEY_ID +" INTEGER PRIMARY KEY,"+ KEY_DATE +" TEXT,"+KEY_ZEN_HOURS +" TEXT,"+ KEY_MOVEMENT_HOURS +" TEXT"+")";
       db.execSQL(CREATE_ZEN_HOURS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
db.execSQL("DROP TABLE IF EXISTS "+TABLE_ZEN);
        onCreate(db);
    }
    public void addZenHours(String hrs)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        values.put(KEY_ZEN_HOURS,hrs);
        db.insert(TABLE_ZEN,null,values);
        db.close();

    }
     public void addZenDate(String dte){

         SQLiteDatabase db = this.getWritableDatabase();

         values.put(KEY_DATE,dte);

         db.insert(TABLE_ZEN,null,values);
         db.close();
     }

    public void addMovementhours(String move_hrs)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        values.put(KEY_MOVEMENT_HOURS,move_hrs);

        db.insert(TABLE_ZEN,null,values);
        db.close();
    }

    public ArrayList<DATA> getInfo(){
        ArrayList<DATA> Zen_data = new ArrayList<>();
        String Query = "SELECT * FROM "+TABLE_ZEN;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(Query,null);

        if(cursor.moveToFirst())
        {
            do{
                DATA data = new DATA();
                data.setDate(cursor.getString(1));
               // Log.d("ZenDatabase"," date is "+cursor.getString(1));
                data.setZenHours(cursor.getString(2));
               // Log.d("Zendatabase"," zenhours is "+cursor.getString(2));
                data.setMoveHours(cursor.getString(3));
                Zen_data.add(data);

            }
            while(cursor.moveToNext());
        }
        else
        {
            Log.d("ZenDatabase"," the database is empty");
        }

        return Zen_data;
    }

}
