package data_inspector.sample.database;

import android.content.ContentValues;
import auto.parcel.AutoParcel;

@AutoParcel public abstract class StockPrice {
  public static final String TABLE = "stock";
  public static final String ID = "_id";
  public static final String NAME = "name";
  public static final String ASK = "ask";
  public static final String BID = "bid";

  public abstract long id();
  public abstract String name();
  public abstract float ask();
  public abstract float bid();

  public static final class Builder {
    private final ContentValues values = new ContentValues();

    public Builder id(long id) {
      values.put(ID, id);
      return this;
    }

    public Builder name(String name) {
      values.put(NAME, name);
      return this;
    }

    public Builder ask(float ask) {
      values.put(ASK, ask);
      return this;
    }

    public Builder bid(float bid) {
      values.put(BID, bid);
      return this;
    }

    public ContentValues build() {
      return values;
    }
  }
}
