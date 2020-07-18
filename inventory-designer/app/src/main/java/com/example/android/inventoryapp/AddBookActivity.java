package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.Data.BookContract.BookEntry;

public class AddBookActivity extends AppCompatActivity {
    public static final String LOG_TAG = AddBookActivity.class.getName();

    /** EditText field to enter the book's name */
    private EditText mNameEditText;

    /** EditText field to enter the book's price */
    private EditText mPriceEditText;

    /** EditText field to enter the book's quantity */
    private EditText mQuantityEditText;

    /** EditText field to enter the book's supplierName */
    private EditText mSupplierNameEditText;

    /** EditText field to enter the book's supplierPhoneNumber */
    private EditText mSupplierPhoneNumberEditText;

    private boolean mTouchAnyWhere = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_text_book_name);
        mPriceEditText = findViewById(R.id.edit_text_price);
        mQuantityEditText = findViewById(R.id.edit_text_quantity);
        mSupplierNameEditText = findViewById(R.id.edit_text_supplier_name);
        mSupplierPhoneNumberEditText = findViewById(R.id.edit_text_supplier_phone_number);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);

        invalidateOptionsMenu();
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mTouchAnyWhere = true;
            return false;
        }
    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem menuItem = menu.findItem(R.id.action_delete);
        menuItem.setVisible(false);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_book, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.action_save:
                showSaveDatabaseDialog();
                return true;

            case android.R.id.home:
                if (!mTouchAnyWhere) {
                    NavUtils.navigateUpFromSameTask(AddBookActivity.this);
                    return true;
                }
                goBackDialog();
                return true;
        }

        return super.onOptionsItemSelected(menuItem);
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

    private void showSaveDatabaseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.save_database_dialog);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveBook();
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

    private void saveBook(){
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

        //get book's quantity from user
        String bookQuantity = mQuantityEditText.getText().toString().trim();
        int quantity = 0;
        if(!TextUtils.isEmpty(bookQuantity)){
            quantity = Integer.parseInt(bookQuantity);
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

        if(errorCode == 1){
            Toast.makeText(this,
                    getString(R.string.invalid_inputs) + "\n" + errorExplanation.toString(),
                    Toast.LENGTH_LONG).show();
        } else{
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_PRODUCT_NAME, bookName);
            values.put(BookEntry.COLUMN_PRICE, price);
            values.put(BookEntry.COLUMN_QUANTITY, quantity);
            values.put(BookEntry.COLUMN_SUPPLIER_NAME, bookSupplierName);
            values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, bookSupplierPhoneNumber);

            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, R.string.error_saved_book_message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.saved_book_message, Toast.LENGTH_SHORT).show();
            }

            finish();
        }
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
}
