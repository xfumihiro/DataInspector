package data_inspector.ui.dialog.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import data_inspector.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import rx.functions.Action1;

public class PreferenceListAdapter extends RecyclerView.Adapter<PreferenceListAdapter.ViewHolder>
    implements Action1<String> {
  private final Context context;
  private final LayoutInflater layoutInflater;

  private SharedPreferences preferences;
  private Map<String, ?> prefMap;
  private List<String> keys;

  public PreferenceListAdapter(Context context) {
    this.context = context;
    layoutInflater = LayoutInflater.from(context);
  }

  @Override public void call(String preferenceFileName) {
    preferences = context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE);
    prefMap = preferences.getAll();
    keys = new ArrayList<>(prefMap.keySet());
    Collections.sort(keys);
    notifyDataSetChanged();
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = layoutInflater.inflate(R.layout.preference_editor_listitem, parent, false);
    return new ViewHolder(view);
  }

  @SuppressLint("CommitPrefEdits") @Override
  public void onBindViewHolder(ViewHolder holder, final int position) {
    holder.key.setText(keys.get(position));
    if (prefMap.get(keys.get(position)) instanceof Boolean) {
      holder.value.setVisibility(View.GONE);
      holder.valueSwitch.setVisibility(View.VISIBLE);
      holder.valueSwitch.setChecked((Boolean) prefMap.get(keys.get(position)));
      holder.valueSwitch.setOnClickListener(new View.OnClickListener() {
        @SuppressLint("CommitPrefEdits") @Override public void onClick(View v) {
          preferences.edit().putBoolean(keys.get(position), ((Switch) v).isChecked()).commit();
        }
      });
    } else {
      Object prefValue = prefMap.get(keys.get(position));
      if (!(prefValue instanceof String)) {
        prefValue = prefValue.toString();
      }
      holder.value.setText((CharSequence) prefValue);
      holder.value.setVisibility(View.VISIBLE);
      holder.valueSwitch.setVisibility(View.GONE);

      if (prefMap.get(keys.get(position)) instanceof String) {
        holder.value.setInputType(InputType.TYPE_CLASS_TEXT);
        holder.value.setOnEditorActionListener(new TextView.OnEditorActionListener() {
          @SuppressLint("CommitPrefEdits") @Override
          public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
              handled = true;
              v.clearFocus();
              preferences.edit()
                  .putString(keys.get(position), v.getEditableText().toString())
                  .commit();
            }
            return handled;
          }
        });
      } else if (prefMap.get(keys.get(position)) instanceof Float) {
        holder.value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        holder.value.setOnEditorActionListener(new TextView.OnEditorActionListener() {
          @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
              handled = true;
              v.clearFocus();
              preferences.edit()
                  .putFloat(keys.get(position), Float.valueOf(v.getEditableText().toString()))
                  .commit();
            }
            return handled;
          }
        });
      } else if (prefMap.get(keys.get(position)) instanceof Integer) {
        holder.value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        holder.value.setOnEditorActionListener(new TextView.OnEditorActionListener() {
          @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
              handled = true;
              v.clearFocus();
              preferences.edit()
                  .putInt(keys.get(position), Integer.valueOf(v.getEditableText().toString()))
                  .commit();
            }
            return handled;
          }
        });
      } else if (prefMap.get(keys.get(position)) instanceof Long) {
        holder.value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        holder.value.setOnEditorActionListener(new TextView.OnEditorActionListener() {
          @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
              handled = true;
              v.clearFocus();
              preferences.edit()
                  .putLong(keys.get(position), Long.valueOf(v.getEditableText().toString()))
                  .commit();
            }
            return handled;
          }
        });
        //} else if (prefMap.get(keys.get(position)) instanceof Set<?>) {
        // TODO
      }
    }
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public int getItemCount() {
    return prefMap != null ? prefMap.size() : 0;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView key;
    TextView value;
    Switch valueSwitch;

    public ViewHolder(View itemView) {
      super(itemView);
      key = (TextView) itemView.findViewById(R.id.key);
      value = (EditText) itemView.findViewById(R.id.value);
      valueSwitch = (Switch) itemView.findViewById(R.id.value_switch);
    }
  }
}
