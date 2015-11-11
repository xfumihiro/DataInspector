package data_inspector.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import data_inspector.DataInspector;
import data_inspector.R;
import data_inspector.ui.WrapLinearLayoutManager;
import data_inspector.ui.dialog.adapter.DatabaseListAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import rx.functions.Action1;
import rx.functions.Func1;

public class DatabaseEditorDialog extends BaseDialog {
  private final Context context;
  private ArrayAdapter<String> spinnerDatabaseAdapter;
  private ArrayAdapter<String> spinnerTableAdapter;
  private ArrayAdapter<String> spinnerColumnAdapter;
  private SQLiteDatabase database;
  private DatabaseListAdapter listAdapter;

  public DatabaseEditorDialog(Context context) {
    super(context);
    DataInspector.runtimeComponentMap.get(((ContextThemeWrapper) context).getBaseContext())
        .inject(this);
    this.context = context;

    setTitle("Edit Database");

    setView(View.inflate(context, R.layout.data_inspector_database_editor_dialog, null));

    setButton(BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        restoreOpenedMenu();
      }
    });
  }

  @Override public void onAttachedToWindow() {
    super.onAttachedToWindow();

    Spinner spinnerDatabase = (Spinner) findViewById(R.id.spinner_database);
    final Spinner spinnerTable = (Spinner) findViewById(R.id.spinner_table);
    final Spinner spinnerColumn = (Spinner) findViewById(R.id.spinner_column);
    final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    final WrapLinearLayoutManager layoutManager =
        new WrapLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(layoutManager);

    File databasesDir = new File("/data/data/" + context.getPackageName() + "/databases");

    if (databasesDir.listFiles() != null) {
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
          | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

      getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
          | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

      List<String> databaseArray = new ArrayList<>();
      for (File file : databasesDir.listFiles()) {
        if (file.getName().endsWith(".db")) databaseArray.add(file.getName());
      }
      String[] databases = databaseArray.toArray(new String[databaseArray.size()]);
      for (int i = 0; i < databases.length; i++) databases[i] = databases[i].split("\\.")[0];
      spinnerDatabaseAdapter =
          new ArrayAdapter<>(context, R.layout.data_inspector_database_spinner_item, databases);
      spinnerDatabaseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      spinnerDatabase.setAdapter(spinnerDatabaseAdapter);

      listAdapter = new DatabaseListAdapter(context);
      recyclerView.setAdapter(listAdapter);

      RxAdapterView.itemSelections(spinnerDatabase).filter(new Func1<Integer, Boolean>() {
        @Override public Boolean call(Integer position) {
          return position != AdapterView.INVALID_POSITION;
        }
      }).map(new Func1<Integer, String[]>() {
        @Override public String[] call(Integer position) {
          if (database != null && database.isOpen()) database.close();
          String databaseName = spinnerDatabaseAdapter.getItem(position).split("\\.")[0];
          database = SQLiteDatabase.openDatabase(
              "/data/data/" + context.getPackageName() + "/databases/" + databaseName + ".db", null,
              SQLiteDatabase.OPEN_READWRITE);
          listAdapter.setTableName(null);
          listAdapter.setDatabase(database);
          Cursor cursor = database.rawQuery(
              "SELECT name FROM sqlite_master WHERE type='table' and name is not 'android_metadata'",
              null);
          List<String> tableList = new ArrayList<>();
          if (cursor.moveToFirst()) {
            do {
              tableList.add(cursor.getString(0));
            } while (cursor.moveToNext());
          }
          cursor.close();
          return tableList.toArray(new String[tableList.size()]);
        }
      }).subscribe(new Action1<String[]>() {
        @Override public void call(String[] tables) {
          spinnerTableAdapter =
              new ArrayAdapter<>(context, R.layout.data_inspector_database_spinner_item, tables);
          spinnerTableAdapter.setDropDownViewResource(
              android.R.layout.simple_spinner_dropdown_item);
          spinnerTable.setAdapter(spinnerTableAdapter);
        }
      });

      RxAdapterView.itemSelections(spinnerTable).filter(new Func1<Integer, Boolean>() {
        @Override public Boolean call(Integer position) {
          return position != AdapterView.INVALID_POSITION && database != null;
        }
      }).map(new Func1<Integer, String[]>() {
        @Override public String[] call(Integer position) {
          Cursor cursor =
              database.rawQuery("PRAGMA table_info(" + spinnerTableAdapter.getItem(position) + ")",
                  null);
          List<String> colList = new ArrayList<>();
          while (cursor.moveToNext()) {
            String colName = cursor.getString(1);
            if (!colName.equals("_id")) colList.add(colName);
          }
          cursor.close();
          listAdapter.setTableName(spinnerTableAdapter.getItem(position));
          return colList.toArray(new String[colList.size()]);
        }
      }).subscribe(new Action1<String[]>() {
        @Override public void call(String[] columns) {
          spinnerColumnAdapter =
              new ArrayAdapter<>(context, R.layout.data_inspector_database_spinner_item, columns);
          spinnerColumnAdapter.setDropDownViewResource(
              android.R.layout.simple_spinner_dropdown_item);
          spinnerColumn.setAdapter(spinnerColumnAdapter);
        }
      });

      RxAdapterView.itemSelections(spinnerColumn).filter(new Func1<Integer, Boolean>() {
        @Override public Boolean call(Integer position) {
          return position != AdapterView.INVALID_POSITION && database != null;
        }
      }).map(new Func1<Integer, String>() {
        @Override public String call(Integer position) {
          return spinnerColumnAdapter.getItem(position);
        }
      }).subscribe(listAdapter);
    } else {
      spinnerDatabaseAdapter =
          new ArrayAdapter<>(context, R.layout.data_inspector_database_spinner_item,
              new String[] { "--" });
      spinnerDatabase.setAdapter(spinnerDatabaseAdapter);

      spinnerTableAdapter =
          new ArrayAdapter<>(context, R.layout.data_inspector_database_spinner_item,
              new String[] { "--" });
      spinnerTableAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      spinnerTable.setAdapter(spinnerTableAdapter);

      spinnerColumnAdapter =
          new ArrayAdapter<>(context, R.layout.data_inspector_database_spinner_item,
              new String[] { "--" });
      spinnerColumnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      spinnerColumn.setAdapter(spinnerColumnAdapter);
    }
  }
}