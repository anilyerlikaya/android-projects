package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.Data.BookContract.BookEntry;

import java.text.DecimalFormat;

public class BookAdapter extends CursorAdapter{
    public static final String LOG_TAG = BookEntry.class.getName();

    Context mContext;

    public BookAdapter(Context context, Cursor cursor){
        super(context, cursor, 0);

        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.book_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final int id = cursor.getInt(cursor.getColumnIndex(BookEntry._ID));

        //Set correct value for book name to TextView.
        TextView bookNameView = view.findViewById(R.id.book_name);
        String bookName = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME));
        bookNameView.setText(bookName);

        //Set correct value for book price to TextView.
        TextView bookPriceView = view.findViewById(R.id.book_price);
        float bookPrice = cursor.getFloat(cursor.getColumnIndex(BookEntry.COLUMN_PRICE));
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String bookPriceString = "$ " + String.valueOf(Float.valueOf(decimalFormat.format(bookPrice)));
        bookPriceView.setText(bookPriceString);

        //Set correct value for book quantity to TextView.
        final TextView bookQuantityView = view.findViewById(R.id.book_quantity);
        int bookQuantity = cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY));
        bookQuantityView.setText(String.valueOf(bookQuantity));

        Button saleBookView = view.findViewById(R.id.book_sale);
        saleBookView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String quantityString = bookQuantityView.getText().toString();
                int quantity = Integer.parseInt(quantityString);
                if(quantity > 0) {
                    quantity -= 1;
                } else{
                    Toast.makeText(mContext, mContext.getString(R.string.quantity_decrease_error),
                            Toast.LENGTH_SHORT).show();
                }

                ContentValues values = new ContentValues();
                values.put(BookEntry.COLUMN_QUANTITY, quantity);

                //Update database for the decreasing quantity
                Uri uri = Uri.withAppendedPath(BookEntry.CONTENT_URI,  String.valueOf(id));
                int idUpdate = mContext.getContentResolver().update(uri, values, null, null);

                bookQuantityView.setText(String.valueOf(quantity));
            }
        });
    }
}
