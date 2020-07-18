package com.example.android.inventoryapp.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.android.inventoryapp.Data.BookContract.BookEntry;
import com.example.android.inventoryapp.R;

public class BookProvider extends ContentProvider {
    public static final String LOG_TAG = BookProvider.class.getName();

    /** URI matcher code for the content URI for the books table */
    private static final int BOOKS = 1000;

    /** URI matcher code for the content URI for a single book in the books table */
    private static final int BOOK_ID = 1001;

    private BookDbHelper mDbHelper;

    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_INVENTORY, BOOKS);

        mUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_INVENTORY + "/#", BOOK_ID);
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query( Uri uri,  String[] projection, String selection,
                         String[] selectionArgs, String sortArgs) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor = null;

        int match = mUriMatcher.match(uri);

        switch (match){
            case BOOKS:
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection,
                        selectionArgs , null, null, sortArgs);
                break;

            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(BookEntry.TABLE_NAME, projection, selection,
                        selectionArgs , null, null, sortArgs);
                break;
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        int match = mUriMatcher.match(uri);

        switch (match){
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;

            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;

            default:
                return null;
        }
    }

    @Override
    public Uri insert( Uri uri, ContentValues contentValues) {
        int match = mUriMatcher.match(uri);

        switch (match){
            case BOOKS:
                return insertBook(uri, contentValues);

            default:
                return null;
        }
    }

    private Uri insertBook(Uri uri, ContentValues values){
        int errorCode = 0;

        //Check the product name is not null.
        String productName = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
        if(productName == null){
            errorCode = 1;
        }

        //Check the price is not null.
        Float price = values.getAsFloat(BookEntry.COLUMN_PRICE);
        if(price == null){
            errorCode = 1;
        }

        // If the quantity is provided, check that it's greater than or equal to 0.
        Integer quantity = values.getAsInteger(BookEntry.COLUMN_QUANTITY);
        if(quantity != null && quantity < 0){
            errorCode = 1;
        }

        //Check the product name is not empty.
        String supplierName = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
        if(TextUtils.isEmpty(supplierName)){
            errorCode = 1;
        }

        //Check the product name is not empty.
        String supplierPhoneNumber = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if(TextUtils.isEmpty(supplierPhoneNumber)){
            errorCode = 1;
        }


        if(errorCode == 0) {
            SQLiteDatabase database = mDbHelper.getWritableDatabase();

            // Insert the new book with the given values
            long id = database.insert(BookEntry.TABLE_NAME, null, values);

            if (id == -1) {
                return null;
            }

            getContext().getContentResolver().notifyChange(uri, null);

            return ContentUris.withAppendedId(uri, id);
        } else{
            Toast.makeText(getContext(), getContext().getString(R.string.invalid_inputs),
                    Toast.LENGTH_SHORT).show();

            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDelete;

        int match = mUriMatcher.match(uri);

        switch (match){
            case BOOKS:
                rowsDelete = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDelete = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                rowsDelete = 0;
        }

        if(rowsDelete != 0){
            getContext().getContentResolver().notifyChange(uri ,null);
        }

        return rowsDelete;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = mUriMatcher.match(uri);

        int rowsUpdate;

        switch (match){
            case BOOKS:
                rowsUpdate = updateBook(uri, values, selection, selectionArgs);
                break;

            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsUpdate = updateBook(uri, values, selection, selectionArgs);
                break;

            default:
                rowsUpdate = 0;
        }

        if(rowsUpdate != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdate;
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        int errorCode = 0;

        if(values.containsKey(BookEntry.COLUMN_PRODUCT_NAME)) {
            //Check the product name is not null.
            String productName = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
            if (productName == null) {
                errorCode = 1;
            }
        }

        if(values.containsKey(BookEntry.COLUMN_PRICE)) {
            //Check the price is not null.
            Float price = values.getAsFloat(BookEntry.COLUMN_PRICE);
            if (price == null) {
                errorCode = 1;
            }
        }

        if(values.containsKey(BookEntry.COLUMN_QUANTITY)) {
            // If the quantity is provided, check that it's greater than or equal to 0.
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_QUANTITY);
            if (quantity != null && quantity < 0) {
                errorCode = 1;
            }
        }

        if(values.containsKey(BookEntry.COLUMN_SUPPLIER_NAME)) {
            //Check the product name is not empty.
            String supplierName = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
            if (TextUtils.isEmpty(supplierName)) {
                errorCode = 1;
            }
        }

        if(values.containsKey(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            //Check the product name is not empty.
            String supplierPhoneNumber = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (TextUtils.isEmpty(supplierPhoneNumber)) {
                errorCode = 1;
            }
        }

        if(values.size() == 0){
            return -1;
        }

        if(errorCode == 0) {
            //Get writable database
            SQLiteDatabase database = mDbHelper.getWritableDatabase();

            return database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);
        } else{
            Toast.makeText(getContext(), getContext().getString(R.string.invalid_inputs),
                    Toast.LENGTH_SHORT).show();

            return -1;
        }
    }
}
