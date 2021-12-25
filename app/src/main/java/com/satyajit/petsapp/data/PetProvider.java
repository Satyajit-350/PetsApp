package com.satyajit.petsapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PetProvider extends ContentProvider {

    private static final int PETS = 100;
    private static final int PETS_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS,PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS+"/#",PETS_ID);
    }

    private PetdbHelper mDbHelper;

    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    @Override
    public boolean onCreate() {
        mDbHelper = new PetdbHelper(getContext());
        return false;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match){
            case PETS:
                //TODO
                cursor = database.query(PetContract.PetsEntry.TABLE_NAME,projection,selection,selectionArgs,null
                ,null,null,sortOrder);
                break;
            case PETS_ID:
                selection = PetContract.PetsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PetContract.PetsEntry.TABLE_NAME,projection,selection,selectionArgs,null,null
                ,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot resolve URI "+ uri);
        }

        //set notification URI on the cursor
        //so we know what content URI the cursor was created
        //if the data at this URI changes, then we know we need to update the cursor.
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    /**
     * The purpose of this method is to return a String that describes the type of the data stored at the input Uri
     * This String is known as the MIME type, which can also be referred to as content type.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:
                return PetContract.PetsEntry.CONTENT_LIST_TYPE;
            case PETS_ID:
                return PetContract.PetsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        //conditions***************************************************************
        String name = contentValues.getAsString(PetContract.PetsEntry.COLUMN_PET_NAME);
        if(name==null){
            throw new IllegalArgumentException("Pet requires valid name");
        }

        Integer gender = contentValues.getAsInteger(PetContract.PetsEntry.COLUMN_PET_GENDER);

        if(gender==null||!PetContract.PetsEntry.isvalidGender(gender)){
            throw new IllegalArgumentException("Pet requires valid gender");
        }

        Integer weight = contentValues.getAsInteger(PetContract.PetsEntry.COLUMN_PET_WEIGHT);

        if(weight==null&&weight<0){
            throw new IllegalArgumentException("Pet requires valid weight");
        }
        //***************************************************************************


        //before inserting uri would be like this : content://com...../pets
        final int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:
                return inssertPet(uri,contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for uri " + uri);
        }
    }
    private Uri inssertPet(Uri uri, ContentValues values){

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //insert a new pet with the given values
        long id = database.insert(PetContract.PetsEntry.TABLE_NAME,null,values);

        if(id==-1){
            Log.e(LOG_TAG,"Failed to insert row for " + uri);
        }

        //Notifies all listeners that the data has changed for the pet content uri.
        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,id);  // The url would be like this : content://com...../pets/3
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:
                rowsDeleted = database.delete(PetContract.PetsEntry.TABLE_NAME,selection,selectionArgs);
                // If 1 or more rows were deleted, then notify all listeners that the data at the
                // given URI has changed
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case PETS_ID:
                selection = PetContract.PetsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(PetContract.PetsEntry.TABLE_NAME,selection,selectionArgs);
                // If 1 or more rows were deleted, then notify all listeners that the data at the
                // given URI has changed
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for "+uri);
        }
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:
                return updatePet(uri,contentValues,selection,selectionArgs);
            case PETS_ID:
                selection = PetContract.PetsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri,contentValues,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("update is not supported for "+uri);
        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if(values.containsKey(PetContract.PetsEntry.COLUMN_PET_NAME)){
            String name = values.getAsString(PetContract.PetsEntry.COLUMN_PET_NAME);
            if(name==null){
                throw new IllegalArgumentException("Pet requires name");
            }
        }
        if(values.containsKey(PetContract.PetsEntry.COLUMN_PET_GENDER)){
            Integer gender = values.getAsInteger(PetContract.PetsEntry.COLUMN_PET_GENDER);
            if(gender==null ||!PetContract.PetsEntry.isvalidGender(gender)){
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }
        if(values.containsKey(PetContract.PetsEntry.COLUMN_PET_WEIGHT)){
            Integer weight = values.getAsInteger(PetContract.PetsEntry.COLUMN_PET_WEIGHT);
            if(weight == null && weight<0){
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        if(values.size()==0){
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(PetContract.PetsEntry.TABLE_NAME,values,selection,selectionArgs);

        if(rowsUpdated != 0){
            // If 1 or more rows were updated, then notify all listeners that the data at the
            // given URI has changed
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowsUpdated;
    }
}
