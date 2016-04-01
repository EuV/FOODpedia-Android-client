package tk.foodpedia.android.concurrent;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;

import java.util.ArrayList;
import java.util.Date;

import tk.foodpedia.android.App;
import tk.foodpedia.android.util.DBHelper;
import tk.foodpedia.android.db.ProductRecord;


public final class Database extends HandlerThread {
    private static Database databaseInstance;

    private Handler dbThreadHandler;

    public interface DatabaseCallbacks<T> {
        void onReadDatabaseCompleted(T object);
    }

    private Database() {
        super("DatabaseThread");
        start();
        dbThreadHandler = new Handler(getLooper());
    }


    public static Database getInstance() {
        if (databaseInstance == null) {
            databaseInstance = new Database();
        }
        return databaseInstance;
    }


    public void loadHistory(final DatabaseCallbacks<ArrayList<ProductRecord>> callbacks) {
        dbThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                loadHistoryInBackground(callbacks);
            }
        });
    }


    private void loadHistoryInBackground(final DatabaseCallbacks<ArrayList<ProductRecord>> callbacks) {
        final ArrayList<ProductRecord> productRecords = new ArrayList<>();
        Cursor cursor = DBHelper.select(ProductRecord.TABLE_NAME, ProductRecord.PROJECTION, ProductRecord.COLUMN_LAST_VIEWED);
        try {
            if (cursor.moveToLast()) {
                do {
                    String ean = DBHelper.getString(cursor, ProductRecord.COLUMN_EAN);
                    String name = DBHelper.getString(cursor, ProductRecord.COLUMN_NAME);
                    Date lastViewed = DBHelper.getDate(cursor, ProductRecord.COLUMN_LAST_VIEWED);
                    productRecords.add(new ProductRecord(ean, name, lastViewed));
                } while (cursor.moveToPrevious());
            }
        } finally {
            cursor.close();
        }

        App.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callbacks.onReadDatabaseCompleted(productRecords);
            }
        });
    }


    public void saveHistory(final ProductRecord productRecord) {
        dbThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                saveHistoryInBackground(productRecord);
            }
        });
    }


    private void saveHistoryInBackground(final ProductRecord productRecord) {
        ContentValues record = new ContentValues();
        record.put(ProductRecord.COLUMN_EAN, productRecord.ean);
        record.put(ProductRecord.COLUMN_NAME, productRecord.name);
        record.put(ProductRecord.COLUMN_LAST_VIEWED, DBHelper.dateToString(productRecord.lastViewed));

        DBHelper.insertOrUpdate(ProductRecord.TABLE_NAME, record);
    }
}
