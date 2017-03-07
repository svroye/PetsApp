package com.example.android.pets.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Steven on 17/02/2017.
 */

public final class PetsContract {

    private PetsContract(){};

    public static final String CONTENT_AUTHORITY = "com.example.android.pets";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PETS = "pets";


    public static final class PetEntry implements BaseColumns{

        // name of the table
        public final static String TABLE_NAME = "pets";
        //column header for the id
        public final static String _ID = BaseColumns._ID;
        //column header for the name  of the pet
        public final static String COLUMN_PET_NAME = "name";
        //column header for the gender
        public final static String COLUMN_PET_GENDER = "gender";
        //column header for the breed
        public final static String COLUMN_PET_BREED  = "breed";
        //column header for the weight
        public final static String COLUMN_PET_WEIGHT = "weight";

        //int to point to the gender unknown
        public final static int GENDER_UNKNOWN = 0;
        //int to point to the gender male
        public final static int GENDER_MALE = 1;
        //int to point to the gender female
        public final static int GENDER_FEMALE = 2;

        public final static Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        /**
         * Returns whether or not the given gender is {@link #GENDER_UNKNOWN}, {@link #GENDER_MALE},
         * or {@link #GENDER_FEMALE}.
         */
        public static boolean isValidGender(int gender) {
            if (gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE) {
                return true;
            }
            return false;
        }

    }
}
