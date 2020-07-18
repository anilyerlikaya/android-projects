package com.example.android.inventoryapp.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.inventoryapp.Data.BookContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper {

    //database version
    private static int DATABASE_VERSION = 1;

    //database name
    private static String DATABASE_NAME = "inventory.db";

    private static final String SQL_CREATE_BOOKS_TABLE =
            "CREATE TABLE " + BookEntry.TABLE_NAME + " (" +
                    BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    BookEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL," +
                    BookEntry.COLUMN_PRICE+ " FLOAT NOT NULL," +
                    BookEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0," +
                    BookEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL," +
                    BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL)";

    private static final String SQL_DELETE_BOOKS_TABLE =
            "DROP TABLE IF EXISTS " + BookEntry.TABLE_NAME;

    public BookDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_BOOKS_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
    }
}
