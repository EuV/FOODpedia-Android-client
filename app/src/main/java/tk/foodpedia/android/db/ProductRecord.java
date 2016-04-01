package tk.foodpedia.android.db;

import android.provider.BaseColumns;

import java.util.Date;

public class ProductRecord implements BaseColumns {
    public static final String TABLE_NAME = "product_records";

    public static final String COLUMN_EAN = "ean";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LAST_VIEWED = "last_viewed";

    public static final String[] PROJECTION = {COLUMN_EAN, COLUMN_NAME, COLUMN_LAST_VIEWED};

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_EAN + " TEXT UNIQUE NOT NULL," +
            COLUMN_NAME + " TEXT," +
            COLUMN_LAST_VIEWED + " DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP)";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public final String ean;
    public final String name;
    public final Date lastViewed;

    public ProductRecord(String ean, String name, Date lastViewed) {
        this.ean = ean;
        this.name = name;
        this.lastViewed = lastViewed;
    }
}
