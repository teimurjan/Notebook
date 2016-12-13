package com.example.android.notebook;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Теймур on 19.06.2016.
 */
public class NotebookDbAdapter {

    private static final String DATABASE_NAME = "notebook.db";
    private static final int DATABASE_VERSION = 1;

    public static final String NOTE_TABLE = "note";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_COLOR = "color";

    private String[] allColumns = {COLUMN_ID, COLUMN_TITLE, COLUMN_MESSAGE, COLUMN_CATEGORY,
            COLUMN_COLOR, COLUMN_DATE};

    public static final String CREATE_TABLE_NOTE = "create table " + NOTE_TABLE + " ( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_MESSAGE + " text not null, "
            + COLUMN_CATEGORY + " text not null, "
            + COLUMN_COLOR + " text not null, "
            + COLUMN_DATE + ");";

    private SQLiteDatabase sqlDB;
    private Context context;

    private NotebookDbHelper notebookDbHelper;

    public NotebookDbAdapter(Context ctx){
        context = ctx;
    }

    public NotebookDbAdapter open() throws android.database.SQLException{
        notebookDbHelper = new NotebookDbHelper(context);
        sqlDB = notebookDbHelper.getWritableDatabase();
        return this;
    }


    public void close(){
        notebookDbHelper.close();
    }


    public long updateNote(long idToUpdate, String newTitle, String newMessage, Note.Category newCategory, String newColor){
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, newTitle);
        values.put(COLUMN_MESSAGE, newMessage);
        values.put(COLUMN_CATEGORY, newCategory.name());
        values.put(COLUMN_COLOR, newColor);
        values.put(COLUMN_DATE, Calendar.getInstance().getTimeInMillis() + "");

        return sqlDB.update(NOTE_TABLE, values, COLUMN_ID + " = " + idToUpdate, null);
    }

    public long deleteNote(long idToDelete){
        return sqlDB.delete(NOTE_TABLE, COLUMN_ID + " = " + idToDelete, null);
    }


    public Note createNote(String title, String message, Note.Category category, String color){
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_MESSAGE, message);
        values.put(COLUMN_CATEGORY, category.name());
        values.put(COLUMN_COLOR, color);
        values.put(COLUMN_DATE, Calendar.getInstance().getTimeInMillis() + "");


        long insertId = sqlDB.insert(NOTE_TABLE, null, values);

        Cursor cursor = sqlDB.query(NOTE_TABLE,
                allColumns, COLUMN_ID + " = " + insertId, null, null, null, null, null);

        cursor.moveToFirst();
        Note newNote = cursorToNote(cursor);
        cursor.close();
        return newNote;
    }



    public ArrayList<Note> getSomeNotes(Note.Category category){
        ArrayList<Note> notes = new ArrayList<Note>();
        //Cursor cursor = sqlDB.query(NOTE_TABLE, allColumns, null, null, null, null, null );
        String q = "SELECT * FROM " + NOTE_TABLE  + " WHERE " + COLUMN_CATEGORY + "='" + category.name() + "'";
        Cursor cursor = sqlDB.rawQuery(q, null);
        for(cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()){
            Note note  = cursorToNote(cursor);
            notes.add(note);
        }
        cursor.close();

        return notes;
    }


    public ArrayList<Note> getAllNotes(){
        NotebookDbHelper notebookDbHelper = new NotebookDbHelper(context);

        ArrayList<Note> notes = new ArrayList<Note>();


        Cursor cursor = sqlDB.query(NOTE_TABLE, allColumns, null, null, null, null, null, null );


        for(cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()){
            Note note  = cursorToNote(cursor);
            notes.add(note);
        }

        cursor.close();

        return notes;
    }


    private Note cursorToNote(Cursor cursor){
        Note newNote =  new Note( cursor.getString(1), cursor.getString(2),
                Note.Category.valueOf(cursor.getString(3)), cursor.getLong(0), cursor.getString(4), cursor.getLong(4));
        return newNote;
    }






    private static class NotebookDbHelper extends SQLiteOpenHelper{

        NotebookDbHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(CREATE_TABLE_NOTE);
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            Log.w(NotebookDbHelper.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + NOTE_TABLE);
            onCreate(db);
        }


    }

}

