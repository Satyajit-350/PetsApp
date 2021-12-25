package com.satyajit.petsapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.satyajit.petsapp.data.PetContract.PetsEntry;

public class PetdbHelper extends SQLiteOpenHelper {

    //name of the database
    private static final String DATABASE_NAME = "shelter.db";

    //version of the database
    private static final int DATABASE_VERSION = 1;

    //constructor which defines this class
    public PetdbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    //method when database ids first created.
    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_PETS_TABLE =
        "CREATE TABLE " + PetsEntry.TABLE_NAME + " ("
                + PetsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PetsEntry.COLUMN_PET_NAME + " TEXT NOT NULL, "
                + PetsEntry.COLUMN_PET_BREED + " TEXT, "
                + PetsEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, "
                + PetsEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    //method when database version changes. i.e. adding a new column.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
