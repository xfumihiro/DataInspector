package data_inspector.sample;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Switch;
import data_inspector.sample.database.DbOpenHelper;
import data_inspector.sample.database.StockPrice;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
  private SharedPreferences preferences1;
  private SharedPreferences preferences2;
  private List<Integer> idList = new ArrayList<>();
  private List<String> nameList = new ArrayList<>();
  private List<Float> askList = new ArrayList<>();
  private List<Float> bidList = new ArrayList<>();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    preferences1 = getSharedPreferences("preference1", MODE_PRIVATE);
    preferences1.edit().putBoolean("switch1", true).commit();
    preferences1.edit().putString("strPref1", "strPref1").commit();
    preferences1.edit().putFloat("floatPref1", 0.1f).commit();
    preferences1.edit().putInt("intPref1", 100).commit();

    preferences2 = getSharedPreferences("preference2", MODE_PRIVATE);
    preferences2.edit().putBoolean("switch2", false).commit();
    preferences2.edit().putString("strPref2", "strPref2").commit();
    preferences2.edit().putFloat("floatPref2", 0.2f).commit();
    preferences2.edit().putInt("intPref2", 200).commit();

    DbOpenHelper dbOpenHelper = new DbOpenHelper(this);
    SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
    Cursor cursor = db.query(StockPrice.TABLE, null, null, null, null, null, null);
    while (cursor.moveToNext()) {
      idList.add(cursor.getInt(cursor.getColumnIndex(StockPrice.ID)));
      nameList.add(cursor.getString(cursor.getColumnIndex(StockPrice.NAME)));
      askList.add(cursor.getFloat(cursor.getColumnIndex(StockPrice.ASK)));
      bidList.add(cursor.getFloat(cursor.getColumnIndex(StockPrice.BID)));
    }
    cursor.close();
  }

  @Override protected void onResume() {
    super.onResume();

    ListView listView = (ListView) findViewById(R.id.listview);
    StockPriceAdapter adapter = new StockPriceAdapter(this, idList, nameList, askList, bidList);
    listView.setAdapter(adapter);

    Switch prefSwitch1 = (Switch) findViewById(R.id.switch1);
    prefSwitch1.setChecked(preferences1.getBoolean("switch1", false));
    prefSwitch1.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        preferences1.edit().putBoolean("switch1", ((Switch) v).isChecked()).commit();
      }
    });

    Switch prefSwitch2 = (Switch) findViewById(R.id.switch2);
    prefSwitch2.setChecked(preferences2.getBoolean("switch2", false));
    prefSwitch2.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        preferences2.edit().putBoolean("switch2", ((Switch) v).isChecked()).commit();
      }
    });
  }

  @Override protected void onPause() {
    super.onPause();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }
}
