package data_inspector.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import data_inspector.DataInspector;
import data_inspector.R;
import data_inspector.ui.WrapLinearLayoutManager;
import data_inspector.ui.dialog.adapter.PreferenceListAdapter;
import java.io.File;
import rx.functions.Func1;

public class PreferenceEditorDialog extends BaseDialog {
  private final Context context;
  private ArrayAdapter<String> spinnerAdapter;

  public PreferenceEditorDialog(Context context) {
    super(context);
    DataInspector.runtimeComponentMap.get(((ContextThemeWrapper) context).getBaseContext())
        .inject(this);

    this.context = context;
    setTitle("Edit Preference");

    setView(View.inflate(context, R.layout.preference_editor_dialog, null));

    setButton(BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        restoreOpenedMenu();
      }
    });
  }

  @Override public void onAttachedToWindow() {
    super.onAttachedToWindow();

    File prefsDir = new File("/data/data/" + context.getPackageName() + "/shared_prefs");
    String[] prefs = prefsDir.list();

    Spinner spinner = (Spinner) findViewById(R.id.spinner);
    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    final WrapLinearLayoutManager layoutManager =
        new WrapLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(layoutManager);

    if (prefs != null) {
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
          | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

      getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
          | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

      for (int i = 0; i < prefs.length; i++) prefs[i] = prefs[i].split("\\.")[0];
      spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, prefs);
      spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      PreferenceListAdapter listAdapter = new PreferenceListAdapter(context);
      recyclerView.setAdapter(listAdapter);
      spinner.setAdapter(spinnerAdapter);
      RxAdapterView.itemSelections(spinner).map(new Func1<Integer, String>() {
        @Override public String call(Integer position) {
          return spinnerAdapter.getItem(position).split("\\.")[0];
        }
      }).subscribe(listAdapter);
    } else {
      spinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,
          new String[] { "No Prefs Found" }));
    }
  }
}