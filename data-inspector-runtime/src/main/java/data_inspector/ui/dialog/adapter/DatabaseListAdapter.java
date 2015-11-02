package data_inspector.ui.dialog.adapter;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import data_inspector.R;
import rx.functions.Action1;

public class DatabaseListAdapter extends RecyclerView.Adapter<DatabaseListAdapter.ViewHolder>
    implements Action1<String> {
  private final LayoutInflater layoutInflater;
  private SQLiteDatabase database;
  private String tableName;
  private int rowCount;
  private String columnName;

  public DatabaseListAdapter(Context context) {
    layoutInflater = LayoutInflater.from(context);
  }

  @Override public void call(String columnName) {
    if (database != null && tableName != null) {
      this.columnName = columnName;
      Cursor cursor =
          database.query(tableName, new String[] { this.columnName }, null, null, null, null, null);
      rowCount = cursor.getCount();
      cursor.close();
      notifyDataSetChanged();
    }
  }

  public void setDatabase(SQLiteDatabase database) {
    this.database = database;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = layoutInflater.inflate(R.layout.database_editor_listitem, parent, false);
    return new ViewHolder(view);
  }

  @SuppressLint("SetTextI18n") @Override
  public void onBindViewHolder(ViewHolder holder, final int position) {
    holder.rowId.setText(Integer.toString(position));
    holder.data.clearFocus();

    Cursor cursor = database.query(tableName, new String[] { columnName }, "_id = ?",
        new String[] { Integer.toString(position + 1) }, null, null, null);
    if (cursor.moveToNext()) {
      int type = cursor.getType(cursor.getColumnIndex(columnName));
      if (type == Cursor.FIELD_TYPE_BLOB) {
        holder.data.setText("BLOB");
        holder.data.setFocusable(false);
        holder.data.setFocusableInTouchMode(false);
        holder.data.setClickable(false);
      } else if (type == Cursor.FIELD_TYPE_NULL) {
        holder.data.setText("NULL");
        holder.data.setFocusable(false);
        holder.data.setFocusableInTouchMode(false);
        holder.data.setClickable(false);
      } else {
        holder.data.setText(cursor.getString(0));
        holder.data.setFocusable(true);
        holder.data.setFocusableInTouchMode(true);
        holder.data.setClickable(true);
        holder.data.setOnEditorActionListener(new TextView.OnEditorActionListener() {
          @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
              handled = true;
              v.clearFocus();
              ContentValues contentValues = new ContentValues();
              contentValues.put(columnName, v.getEditableText().toString());
              database.update(tableName, contentValues, "_id = ?",
                  new String[] { Integer.toString(position + 1) });
            }
            return handled;
          }
        });
        if (type != Cursor.FIELD_TYPE_STRING) {
          holder.data.setInputType(
              InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else {
          holder.data.setInputType(InputType.TYPE_CLASS_TEXT);
        }
      }
    }

    cursor.close();
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public int getItemCount() {
    return rowCount;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView rowId;
    EditText data;

    public ViewHolder(View itemView) {
      super(itemView);
      rowId = (TextView) itemView.findViewById(R.id.row_id);
      data = (EditText) itemView.findViewById(R.id.data);
    }
  }
}
