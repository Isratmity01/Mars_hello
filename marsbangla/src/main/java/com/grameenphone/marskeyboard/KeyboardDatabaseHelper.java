package com.grameenphone.marskeyboard;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {


    public static final String DB_NAME = "marskeyboard";
    private static final String TAG = "DatabaseHelper";

    public static String DB_PATH;
    private SQLiteDatabase database;
    private Context context;


    public DatabaseHelper(Context context) {


        super(context, DB_NAME, null, 1);
        this.context = context;

        DB_PATH = context.getFilesDir().getPath() + "/databases/";
        this.database = openDatabase();

    }


    public SQLiteDatabase openDatabase() {
        if (database == null) {
            createDatabase();
            Log.e(getClass().getName(), "Database created...");
        }

        return database;
    }


    private void createDatabase() {
        boolean dbExists = checkDB();
        if (!dbExists) {
            this.getReadableDatabase();
            database = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
            createTable();
            Log.e(getClass().getName(), "No Database");
        }

    }


    public SQLiteDatabase getDatabase() {
        String path = DB_PATH + DB_NAME;
        database = SQLiteDatabase.openDatabase(path, null,
                SQLiteDatabase.OPEN_READWRITE);
        return database;
    }


    public void createTable() {
        String WORD_SUGGESTION = "CREATE TABLE IF NOT EXISTS " + Constant.Database.TABLE_WORD_SUGGESTION + "("
                + Constant.Database.WordPair.INDEX + " VARCHAR PRIMARY KEY UNIQUE,"
                + Constant.Database.WordPair.WORD + " TEXT UNIQUE,"
                + Constant.Database.WordPair.VALUE + " TEXT"
                + ")";




        try {
            database.execSQL(WORD_SUGGESTION);
            database.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), "Error Creating Table");
        }
    }






    private boolean checkDB(){
        String path = DB_PATH + DB_NAME;
        File file = new File(path);
        if(file.exists()){
            return true;
        }
        return false;
    }




    public synchronized void close(){
        if(this.database != null){
            this.database.close();
        }
    }




    public ArrayList<String> getSuggestion(String search){

        ArrayList<String> allWord = new ArrayList<>();

        String selectAll = "SELECT * FROM "+ Constant.Database.TABLE_WORD_SUGGESTION + " WHERE "+Constant.Database.WordPair.WORD +
                " LIKE  '"+search+"%'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectAll, null);




        try {
            if (cursor.moveToFirst()) {
                do {
                    String value = cursor.getString(2);

                    allWord.add(value);

                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }




        return allWord;
    }




    public void setSuggestion(String word, String value){


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.Database.WordPair.WORD, word);
        values.put(Constant.Database.WordPair.VALUE, value);

        db.insertWithOnConflict(Constant.Database.TABLE_WORD_SUGGESTION, Constant.Database.WordPair.WORD , values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();


    }






    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+Constant.Database.TABLE_WORD_SUGGESTION);
        onCreate(db);
    }










}