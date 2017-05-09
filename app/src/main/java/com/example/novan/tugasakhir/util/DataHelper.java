package com.example.novan.tugasakhir.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.novan.tugasakhir.models.Contact;
import com.example.novan.tugasakhir.models.Medicine;
import com.example.novan.tugasakhir.models.Schedule;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Novan on 29/03/2017.
 */

public class DataHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DBase_EphA";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "TAGapp";
    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    //Table Name
    private static  final String TABLE_USER = "user";
    private static  final String TABLE_MEDICINE = "db_medic";
    private static  final String TABLE_SCHEDULE = "db_schedule";
    private static  final String TABLE_HISTORY = "db_history";
    private static  final String TABLE_RELAPSE = "db_relapse";
    private static  final String TABLE_EPILEPTICS = "db_epileptic";
    private static  final String TABLE_CONTACTS = "db_contact";

    //Table User Data
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PREVILLAGE = "previllage";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_UID = "uid";
    public static final String KEY_CREATED_AT = "created_at";

    //Table Medicine Data
    public static final String COLUMN_ID_MEDICINE = "id_medicine";
    public static final String COLUMN_UID_MEDICINE = "uid_medicine";
    public static final String COLUMN_AMOUNT_MEDICINE = "amount_medicine";
    public static final String COLUMN_NAME_MEDICINE = "name_medicine";
    public static final String COLUMN_DOSAGE_MEDICINE = "dosage_medicine";
    public static final String COLUMN_REMAINS_MEDICINE = "remain_medicine";
    public static final String COLUMN_COUNT_MEDICINE = "count_medicine";

    //Table Schedule Data
    public static final String COLUMN_ID_SCHEDULE = "id_schedule";
    public static final String COLUMN_UIDMedicine_SCHEDULE = "uid_schedule";
    public static final String COLUMN_HOUR_SCHEDULE = "hour_schedule";
    public static final String COLUMN_MINUTE_SCHEDULE = "minute_schedule";
    public static final String COLUMN_STATUS_SCHEDULE = "status_schedule";

    //Table History Data
    private static final String COLUMN_ID_HISTORY = "id_history";
    private static final String COLUMN_IDmedicine_HISTORY = "id_medicine";
    private static final String COLUMN_IDuser_HISTORY = "id_user";
    private static final String COLUMN_TIME_HISTORY = "time_history";
    private static final String COLUMN_STATUS_HISTORY = "status_history";

    //Table Contact Data
    public static final String COLUMN_ID_CONTACT = "id_contact";
    public static final String COLUMN_NUMBER_CONTACT = "number_contact";
    public static final String COLUMN_NAME_CONTACT = "name_contact";

    //Table Relapse Data
    private static final String COLUMN_ID_RELAPSE = "id_relapse";
    private static final String COLUMN_IDuser_RELAPSE = "id_user";
    private static final String COLUMN_LOCATION_RELAPSE = "location_relapse";
    private static final String COLUMN_TIME_RELAPSE = "time_relapse";

    //Table Epileptics Data
    private static final String COLUMN_ID_EPILEPTICS = "id_epileptic";
    private static final String COLUMN_IDuser_EPILEPTICS = "id_user";
    private static final String COLUMN_NAME_EPILEPTICS = "name_epileptic";

    //Create Table
    private static final String DB_MEDICINE = "create table db_medic (id_medicine integer primary key AUTOINCREMENT, uid_medicine text, name_medicine text, amount_medicine integer , dosage_medicine integer, count_medicine integer, remain_medicine integer );";
    private static final String DB_CONTACT = "create table db_contact (id_contact integer primary key AUTOINCREMENT, name_contact text, number_contact text);";
    private static final String DB_SCHEDULE = "create table db_schedule (id_schedule integer primary key AUTOINCREMENT, uid_schedule text, hour_schedule integer, minute_schedule integer, status_schedule integer);";
    String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_PREVILLAGE + " TEXT,"
            + KEY_USERNAME + " TEXT UNIQUE," + KEY_UID + " TEXT," + KEY_CREATED_AT + " TEXT" + ")";

    private SQLiteDatabase db;
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(DB_MEDICINE);
        db.execSQL(DB_CONTACT);
        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(DB_SCHEDULE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST db_medic");
        db.execSQL("DROP TABLE IF EXIST db_contact");
        db.execSQL("DROP TABLE IF EXIST db_schedule");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
    }


    //CRUD Medicine
    public Medicine save_medicine(String uid, String name, int amount, int dosage, int remain, int count){
        SQLiteDatabase db = this.getWritableDatabase();

        Medicine medicine = new Medicine(name,amount,remain,dosage,count);

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_UID_MEDICINE,uid);
        cv.put(COLUMN_NAME_MEDICINE,name);
        cv.put(COLUMN_AMOUNT_MEDICINE,amount);
        cv.put(COLUMN_DOSAGE_MEDICINE,dosage);
        cv.put(COLUMN_COUNT_MEDICINE,count);
        cv.put(COLUMN_REMAINS_MEDICINE,remain);
        addSchedule(uid,count);
        db.insert(TABLE_MEDICINE,null,cv);
        db.close();

        return medicine;
    }

    public Medicine update_medicine(int id, String name, int amount, int dosage, int remain, int count){
        SQLiteDatabase db = this.getWritableDatabase();

        Medicine medicine = new Medicine(name,amount,remain,dosage,count);

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME_MEDICINE,name);
        cv.put(COLUMN_AMOUNT_MEDICINE,amount);
        cv.put(COLUMN_DOSAGE_MEDICINE,dosage);
        cv.put(COLUMN_COUNT_MEDICINE,count);
        cv.put(COLUMN_REMAINS_MEDICINE,remain);
        db.update(TABLE_MEDICINE,cv,"id_medicine="+id,null);
        db.close();

        Log.d(TAG,"Success update medicine");

        return medicine;
    }

    public void delete_medicine(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEDICINE,"id_medicine="+id, null);
        db.close();
    }

    public ArrayList<Medicine> getAllMedicine(){
        ArrayList<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM "+ TABLE_MEDICINE +" ORDER BY "+COLUMN_ID_MEDICINE;
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.rawQuery(sql,null);
        cursor.moveToFirst();
        Log.d(TAG,"size cursor medicine: "+cursor.getCount());
        if(cursor.getCount() > 0 ) {
            do {
                medicines.add(new Medicine(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        db.close();
        return medicines;
    }

    public void clear_medicine() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_MEDICINE, null, null);
        db.close();

        Log.d(TAG, "Deleted all medicine info from sqlite");
    }

    //CRUS SCHEDULE
    public void addSchedule(String uid, int count) {
        SQLiteDatabase db = this.getWritableDatabase();

        int hour = 0;
        int minute = 0;
        int status = 0;

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_UIDMedicine_SCHEDULE,uid);
        cv.put(COLUMN_HOUR_SCHEDULE, hour);
        cv.put(COLUMN_MINUTE_SCHEDULE, minute);
        cv.put(COLUMN_STATUS_SCHEDULE, status);
        for (int i = 0; i < count ; i++){
            try{
                db.insert(TABLE_SCHEDULE,null,cv);
                Log.d(TAG, "Fetching schedule form "+TABLE_SCHEDULE+" : "+cv.toString());
            }catch (Exception e){
            }

        }
    }

    public ArrayList<Schedule> getAllSchedule(String uid){
        ArrayList<Schedule> schedule = new ArrayList<>();
        String sql = "SELECT * FROM "+ TABLE_SCHEDULE +" WHERE "+COLUMN_UIDMedicine_SCHEDULE+" = '"+uid+"' ORDER BY "+COLUMN_ID_SCHEDULE;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(sql,null);
        cursor.moveToFirst();
        Log.d(TAG,"size cursor schedule: "+cursor.getCount());
        if(cursor.getCount() > 0 ) {
            do {
                schedule.add(new Schedule(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        db.close();
        return schedule;
    }

    public void update_schedule(int id, int hour, int minute, int status){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_HOUR_SCHEDULE,hour);
        cv.put(COLUMN_MINUTE_SCHEDULE,minute);
        cv.put(COLUMN_STATUS_SCHEDULE,status);

        db.update(TABLE_SCHEDULE,cv,"id_schedule="+id,null);
        Log.d(TAG,"fetching from schedule = "+cv.toString());
        db.close();
    }

    public void delete_schedule(String uid){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            String sql = "DELETE FROM "+TABLE_SCHEDULE+" WHERE "+COLUMN_UIDMedicine_SCHEDULE+" = '"+uid+"'";
            db.execSQL(sql);
        }catch (Exception e){
            Log.d(TAG,"schedule not deleted");
        }

        db.close();
    }
    //CRUD Contact
    public ArrayList<Contact> getAllContacts(){
        ArrayList<Contact> contacts = new ArrayList<>();
        String sql = "SELECT * FROM "+ TABLE_CONTACTS +" ORDER BY "+COLUMN_ID_CONTACT;
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.rawQuery(sql,null);
        cursor.moveToFirst();
        Log.d(TAG,"size cursor contact: "+cursor.getCount());
        if(cursor.getCount() > 0 ) {
            do {
                contacts.add(new Contact(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        db.close();
        return contacts;
    }

    public Contact save_contact(String name, String number){

        SQLiteDatabase db = this.getWritableDatabase();
        Contact contact = new Contact(name,number);

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME_CONTACT,name);
        cv.put(COLUMN_NUMBER_CONTACT,number);
        db.insert(TABLE_CONTACTS,null,cv);
        db.close();

        return contact;
    }

    public void delete_contact(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.delete(TABLE_CONTACTS,"id_contact="+id, null);
        }catch (Exception e){
            Log.d(TAG,"contact not deleted");
        }

        db.close();
    }

    public void clear_contact() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_CONTACTS, null, null);
        db.close();

        Log.d(TAG, "Deleted all contacts info from sqlite");
    }

    //CRUD User
    /**
     * Storing user details in database
     * */
    public void addUser(String uid, String name, String previllage, String username, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_PREVILLAGE, previllage); // previllage
        values.put(KEY_USERNAME, username); // username
        values.put(KEY_UID, uid); // uid
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("previllage", cursor.getString(2));
            user.put("username", cursor.getString(3));
            user.put("uid", cursor.getString(4));
            user.put("created_at", cursor.getString(5));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}



