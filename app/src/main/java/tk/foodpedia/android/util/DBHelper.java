package tk.foodpedia.android.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import tk.foodpedia.android.App;
import tk.foodpedia.android.db.ProductRecord;

public final class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FOODpedia.db";

    private static DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private static DBHelper dbHelperInstance;

    private DBHelper() {
        super(App.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static DBHelper getInstance() {
        if (dbHelperInstance == null) {
            dbHelperInstance = new DBHelper();
        }
        return dbHelperInstance;
    }


    public static Cursor select(String table, String[] columns, String orderBy) {
        return getInstance().getReadableDatabase().query(table, columns, null, null, null, null, orderBy);
    }


    public static void insertOrUpdate(String table, ContentValues values) {
        getInstance().getWritableDatabase().insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }


    public static String getString(Cursor cursor, String column) {
        return cursor.getString(cursor.getColumnIndexOrThrow(column));
    }


    public static Date getDate(Cursor cursor, String column) {
        try {
            return iso8601Format.parse(getString(cursor, column));
        } catch (ParseException | NullPointerException e) {
            Log.e("DBHelper", "Failed to parse date in ISO8601 format", e);
            return null;
        }
    }


    public static String dateToString(Date date) {
        return iso8601Format.format(date);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ProductRecord.SQL_CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ProductRecord.SQL_DROP_TABLE);
        onCreate(db);
    }


    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
