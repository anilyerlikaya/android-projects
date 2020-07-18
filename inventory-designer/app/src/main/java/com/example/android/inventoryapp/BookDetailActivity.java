package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.Data.BookContract.BookEntry;

import java.text.DecimalFormat;

public class BookDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String LOG_TAG = BookDetailActivity.class.getName();

    /** TextView field to put the book's name */
    private EditText mNameEditText;

    /** TextView field to put the book's price */
    private EditText mPriceEditText;

    /** TextView field to put the book's quantity */
    private TextView mQuantityTextView;

    /** TextView field to put the book's supplierName */
    private EditText mSupplierNameEditText;

    /** TextView field to put the book's supplierPhoneNumber */
    private EditText mSupplierPhoneNumberEditText;

    /** Button for increase quantity of book */
    private Button mIncreaseQuantityButton;

    /** Button for decrease quantity of book */
    private Button mDecreaseQuantityButton;

    /** Button for call supplier*/
    private Button mCallSupplierButton;

    /** EditText field to enter quantity change amount */
    private EditText mChangeQuantityAmountEditText;

    /** Variables for quantity change dialog message */
    private String increase = "increase";
    private String decrease = "decrease";

    /** Loader id */
    private static final int EXISTING_BOOK_LOADER_ID = 100;

    /** Total book quantity */
    private int bookQuantity;

    /** Quantity change amount of book quantity */
    private int quantityChangeAmount;

    /** Id of current book */
    private int idOfBook;

    /** Name of current book */
    private String nameOfBook;

    /** Phone number of current book supplier */
    private String phoneNumber;

    /** Control quantity change is increasing or decreasing */
    private int changeId = 0;

    private boolean mTouchAnyWhere = false;

    /** Uri for a single book */
    Uri selectedBookUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        Intent intent = getIntent();
        selectedBookUri = intent.getData();
        if(selectedBookUri == null){
            Toast.makeText(this, getString(R.string.invalid_book_uri), Toast.LENGTH_SHORT);
            finish();
        } else{
            setTitle(R.string.edit_book);

            mNameEditText = findViewById(R.id.book_name);
            mNameEditText.setOnTouchListener(mTouchListener);

            mPriceEditText = findViewById(R.id.book_price);
            mPriceEditText.setOnTouchListener(mTouchListener);

            mQuantityTextView = findViewById(R.id.book_quantity);

            mSupplierNameEditText = findViewById(R.id.book_supplier_name);
            mSupplierNameEditText.setOnTouchListener(mTouchListener);

            mSupplierPhoneNumberEditText = findViewById(R.id.book_supplier_phone_number);
            mSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);

            mCallSupplierButton = findViewById(R.id.call_supplier_button);
            mCallSupplierButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL,
                            Uri.fromParts("tel", phoneNumber, null));
                    startActivity(phoneIntent);
                }
            });

            // read default change amount of quantity
            mChangeQuantityAmountEditText = findViewById(R.id.quantity_change_amount);
            mChangeQuantityAmountEditText.setOnTouchListener(mTouchListener);

            String changeAmount = mChangeQuantityAmountEditText.getText().toString();
            quantityChangeAmount = Integer.parseInt(changeAmount);
            mDecreaseQuantityButton = findViewById(R.id.quantity_decrease_button);
            mDecreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeId = 1;
                    showQuantityChangeDialog();
                }
            });

            mIncreaseQuantityButton = findViewById(R.id.quantity_increase_button);
            mIncreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeId = 2;
                    showQuantityChangeDialog();
                }
            });

            getSupportLoaderManager().initLoader(EXISTING_BOOK_LOADER_ID, null, this);
        }
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mTouchAnyWhere = true;
            return false;
        }
    };


    private void showQuantityChangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(changeId == 1) {
            builder.setMessage(getString(R.string.change_quantit_amount_dialog, decrease, nameOfBook));
        }else{
            builder.setMessage(getString(R.string.change_quantit_amount_dialog, increase, nameOfBook));
        }
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(changeId == 1){
                    decreaseQuantity();
                } else if(changeId == 2){
                    increaseQuantity();
                }

                mTouchAnyWhere = true;
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void decreaseQuantity(){
        String quantityString = mChangeQuantityAmountEditText.getText().toString();
        quantityChangeAmount = Integer.parseInt(quantityString);

        if(bookQuantity - quantityChangeAmount >= 0){
            bookQuantity -= quantityChangeAmount;

            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_QUANTITY, bookQuantity);

            //int idUpdate = getContentResolver().update(selectedBookUri, values, null, null);

            mQuantityTextView.setText(String.valueOf(bookQuantity));
        } else{
            Toast.makeText(getBaseContext(), getString(R.string.quantity_decrease_error),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void increaseQuantity(){
        String quantityString = mChangeQuantityAmountEditText.getText().toString();
        quantityChangeAmount = Integer.parseInt(quantityString);

        bookQuantity += quantityChangeAmount;

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_QUANTITY, bookQuantity);

        //int idUpdate = getContentResolver().update(selectedBookUri, values, null, null);

        mQuantityTextView.setText(String.valueOf(bookQuantity));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_book, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case android.R.id.home:
                if(!mTouchAnyWhere) {
                    NavUtils.navigateUpFromSameTask(BookDetailActivity.this);
                    return true;
                }
                goBackDialog();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case R.id.action_save:
                if(!mTouchAnyWhere){
                    Toast.makeText(this, getString(R.string.no_change_update, nameOfBook),
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                showUpdateDatabaseDialog();
                return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    private void showUpdateDatabaseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.update_database_dialog, nameOfBook));
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateBook();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void updateBook(){
        int errorCode = 0;
        StringBuilder errorExplanation = new StringBuilder();

        //get book's name from user
        String bookName = mNameEditText.getText().toString().trim();
        if(TextUtils.isEmpty(bookName)){
            errorCode = 1;
            errorExplanation.append(getString(R.string.invalid_name) + "\n");
        }

        //get book's price from user
        String bookPrice = mPriceEditText.getText().toString().trim();
        float price = 0;
        if(TextUtils.isEmpty(bookPrice)){
            errorCode = 1;
            errorExplanation.append(getString(R.string.invalid_price) + "\n");
        } else{
            price = Float.parseFloat(bookPrice);
        }

        //get book's supplier name from user
        String bookSupplierName = mSupplierNameEditText.getText().toString();
        if(TextUtils.isEmpty(bookSupplierName)){
            errorCode = 1;
            errorExplanation.append(getString(R.string.invalid_supplier_name) + "\n");
        }

        //get book's supplier phone number from user
        String bookSupplierPhoneNumber = mSupplierPhoneNumberEditText.getText().toString().trim();
        if(TextUtils.isEmpty(bookSupplierPhoneNumber)){
            errorCode = 1;
            errorExplanation.append(getString(R.string.invalid_supplier_phone_number));
        }

        if(errorCode == 1) {
            Toast.makeText(this,
                    getString(R.string.invalid_inputs) + "\n" + errorExplanation.toString(),
                    Toast.LENGTH_LONG).show();
        } else{
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_PRODUCT_NAME, bookName);
            values.put(BookEntry.COLUMN_PRICE, price);
            values.put(BookEntry.COLUMN_QUANTITY, bookQuantity);
            values.put(BookEntry.COLUMN_SUPPLIER_NAME, bookSupplierName);
            values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, bookSupplierPhoneNumber);

            int idUpdate = getContentResolver().update(selectedBookUri, values, null, null);

            if (idUpdate == -1) {
                Toast.makeText(this, R.string.error_update_book_message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.update_book_message, Toast.LENGTH_SHORT).show();
            }

            finish();
        }
    }

    private void goBackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.go_back_dialog);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mTouchAnyWhere) {
            super.onBackPressed();
            return;
        }

        goBackDialog();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_mesage_dialog, nameOfBook));
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        int rowsDeleted = getContentResolver().delete(selectedBookUri, null, null);

        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, getString(R.string.editor_delete_book_failed, nameOfBook),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_delete_book_successful, nameOfBook),
                    Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        switch (id){
            case EXISTING_BOOK_LOADER_ID:
                return new CursorLoader(
                        this,
                        selectedBookUri,
                        projection,
                        null,
                        null,
                        null);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()){
            idOfBook = data.getInt(data.getColumnIndex(BookEntry._ID));

            nameOfBook = data.getString(data.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME));
            mNameEditText.setText(nameOfBook);

            float price = data.getInt(data.getColumnIndex(BookEntry.COLUMN_PRICE));
            DecimalFormat decimalFormat = new DecimalFormat("#,##");
            String bookPrice = String.valueOf(Float.valueOf(decimalFormat.format(price)));
            mPriceEditText.setText(bookPrice);

            bookQuantity = data.getInt(data.getColumnIndex(BookEntry.COLUMN_QUANTITY));
            mQuantityTextView.setText(String.valueOf(bookQuantity));

            mSupplierNameEditText.setText(data.getString(data.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME)));

            phoneNumber = data.getString(data.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER));
            mSupplierPhoneNumberEditText.setText(phoneNumber);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText(null);
        mPriceEditText.setText(null);
        mQuantityTextView.setText(null);
        mSupplierNameEditText.setText(null);
        mSupplierPhoneNumberEditText.setText(null);
    }
}
