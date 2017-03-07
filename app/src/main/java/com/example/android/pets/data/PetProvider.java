package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.android.pets.CatalogActivity;
import com.example.android.pets.EditorActivity;

import static android.R.attr.id;
import static android.icu.lang.UCharacter.JoiningGroup.PE;

/**
 * Created by Steven on 19/02/2017.
 */

public class PetProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the pets table */
    private static final int PETS = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int PET_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY,PetsContract.PATH_PETS,PETS);
        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY,PetsContract.PATH_PETS+"/#",PET_ID);
    }

    private PetDbHelper mHelper;
    @Override
    public boolean onCreate() {
        mHelper = new PetDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        Cursor c;

        int match = sUriMatcher.match(uri);
        switch(match){
            case PETS : c = db.query(PetsContract.PetEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case PET_ID : selection = PetsContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                c = db.query(PetsContract.PetEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default : throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //Register to watch a content URI for changes. This can be the URI of a specific data
        // row (for example, "content://my_provider_type/23"), or a a generic URI for a content
        // type.
        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetsContract.PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetsContract.PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) {

        // Check that the name is not null
        String name = values.getAsString(PetsContract.PetEntry.COLUMN_PET_NAME);
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        Integer gender = values.getAsInteger(PetsContract.PetEntry.COLUMN_PET_GENDER);
        if (gender == null || !PetsContract.PetEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires valid gender");
        }

        // If the weight is provided, check that it's greater than or equal to 0 kg
        Integer weight = values.getAsInteger(PetsContract.PetEntry.COLUMN_PET_WEIGHT);
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }

        SQLiteDatabase db = mHelper.getWritableDatabase();
        long newId = db.insert(PetsContract.PetEntry.TABLE_NAME,null,values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri,null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, newId);
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                getContext().getContentResolver().notifyChange(uri,null);
                // Delete all rows that match the selection and selection args
                return database.delete(PetsContract.PetEntry.TABLE_NAME, selection, selectionArgs);
            case PET_ID:
                getContext().getContentResolver().notifyChange(uri,null);
                // Delete a single row given by the ID in the URI
                selection = PetsContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return database.delete(PetsContract.PetEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }



    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetsContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mHelper.getWritableDatabase();

        if(values.containsKey(PetsContract.PetEntry.COLUMN_PET_NAME)){
            String name = values.getAsString(PetsContract.PetEntry.COLUMN_PET_NAME);
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        if(values.containsKey(PetsContract.PetEntry.COLUMN_PET_GENDER)){
            Integer gender = values.getAsInteger(PetsContract.PetEntry.COLUMN_PET_GENDER);
            if (gender == null || !PetsContract.PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }

        if(values.containsKey(PetsContract.PetEntry.COLUMN_PET_WEIGHT)){
            Integer weight = values.getAsInteger(PetsContract.PetEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        int successfullRows = db.update(PetsContract.PetEntry.TABLE_NAME,values,selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri,null);

        return successfullRows;
    }
}
