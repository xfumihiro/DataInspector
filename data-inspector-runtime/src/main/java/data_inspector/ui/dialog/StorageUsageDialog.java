package data_inspector.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import data_inspector.R;
import data_inspector.ui.WrapLinearLayoutManager;
import data_inspector.ui.dialog.adapter.StorageListAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import rx.functions.Func1;

public class StorageUsageDialog extends BaseDialog {
  private final Context context;
  private final StorageListAdapter listAdapter;
  private ArrayAdapter<String> spinnerAdapter;

  public StorageUsageDialog(Context context) {
    super(context);

    this.context = context;

    setTitle("Storage Usage");

    setView(View.inflate(context, R.layout.data_inspector_storage_usage_dialog, null));

    setButton(BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        restoreOpenedMenu();
      }
    });

    listAdapter = new StorageListAdapter(this.context);

    File dataDir = new File("/data/data/" + context.getPackageName());
    List<String> folderArray = new ArrayList<>();
    for (File file : dataDir.listFiles()) {
      if (file.isDirectory() && file.list().length > 0) folderArray.add(file.getName());
    }
    String[] folders = folderArray.toArray(new String[folderArray.size()]);

    if (folders.length == 0) {
      spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,
          new String[] { "No Usage Found" });
    } else {
      spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, folders);
    }
    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
  }

  @Override public void onAttachedToWindow() {
    super.onAttachedToWindow();
    final Spinner spinner = (Spinner) findViewById(R.id.spinner);
    spinner.setAdapter(spinnerAdapter);

    final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    final WrapLinearLayoutManager layoutManager =
        new WrapLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(listAdapter);

    RxAdapterView.itemSelections(spinner).map(new Func1<Integer, String>() {
      @Override public String call(Integer position) {
        return (String) spinner.getItemAtPosition(position);
      }
    }).subscribe(listAdapter);
  }
}
