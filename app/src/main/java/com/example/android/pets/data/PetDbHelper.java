package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.version;

/**
 * Created by Steven on 17/02/2017.
 */

public class PetDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "shelter.db" ;
    public static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + PetsContract.PetEntry.TABLE_NAME
            + "( " + PetsContract.PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PetsContract.PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL, " +
            PetsContract.PetEntry.COLUMN_PET_BREED + " TEXT, " +
            PetsContract.PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, " +
            PetsContract.PetEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";

    private static final String SQL_DELETE_ENTRIES ="DROP TABLE IF EXISTS " + PetsContract.PetEntry.TABLE_NAME;

    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
