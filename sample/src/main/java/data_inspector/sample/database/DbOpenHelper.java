package data_inspector.sample.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper {
  public static final String DB_NAME = "stockDB.db";
  private static final int VERSION = 1;

  private static final String CREATE_STOCK_PRICE =
      "" + "CREATE TABLE " + StockPrice.TABLE + "(" + StockPrice.ID
          + " INTEGER NOT NULL PRIMARY KEY," + StockPrice.NAME + " TEXT NOT NULL," + StockPrice.ASK
          + " FLOAT NOT NULL DEFAULT -1," + StockPrice.BID + " FLOAT NOT NULL DEFAULT -1)";

  public DbOpenHelper(Context context) {
    super(context, DB_NAME, null, VERSION);
  }

  @Override public void onCreate(SQLiteDatabase db) {
    db.execSQL(CREATE_STOCK_PRICE);

    db.insert(StockPrice.TABLE, null,
        new StockPrice.Builder().name("GOOL").ask(734.00f).bid(732.38f).build());

    db.insert(StockPrice.TABLE, null,
        new StockPrice.Builder().name("AAPL").ask(121.11f).bid(121.02f).build());

    db.insert(StockPrice.TABLE, null,
        new StockPrice.Builder().name("MSFT").ask(54.94f).bid(54.82f).build());
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  }
}
