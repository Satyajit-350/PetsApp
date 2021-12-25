package com.satyajit.petsapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class PetContract {

    public static final class PetsEntry implements BaseColumns{

        /**
         * Lastly, inside each of the Entry classes in the contract, we create a full URI for the class
         * as a constant called CONTENT_URI. The Uri.withAppendedPath() method appends the BASE_CONTENT_URI
         * (which contains the scheme and the content authority) to the path segment.
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

        public static final String TABLE_NAME = "Pets";
        public static final String _ID = "_id";
        public static final String COLUMN_PET_NAME = "name";
        public static final String COLUMN_PET_BREED = "breed";
        public static final String COLUMN_PET_GENDER = "gender";
        public static final String COLUMN_PET_WEIGHT = "weight";

        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
        public static final int GENDER_UNKNOWN = 0;

        public static boolean isvalidGender(int gender) {
            if(gender==GENDER_UNKNOWN||gender==GENDER_MALE|gender==GENDER_FEMALE){
                return true;
            }
            return false;
        }

        /**
         * The MIME type of the for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        /**
         * The MIME type of the for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;
    }


    /**
     * set a string constant whose value is the same as that from the AndroidManifest:
     */
    public static final String CONTENT_AUTHORITY = "com.satyajit.petsapp";
    /**
     * we concatenate the CONTENT_AUTHORITY constant with the scheme “content://” we will create
     * the BASE_CONTENT_URI which will be shared by every URI associated with PetContract
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * This constants stores the path for each of the tables which will be appended to the
     * base content URI.
     */
    public static final String PATH_PETS = "pets";






}
