package data_inspector.ui.menu;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import data_inspector.DataInspector;
import data_inspector.R;
import data_inspector.ui.dialog.BaseDialog;
import data_inspector.ui.dialog.StorageUsageDialog;

public class StorageMenu extends BaseMenu {
  public StorageMenu(final Context context) {
    super(context);
    DataInspector.runtimeComponentMap.get(context).inject(this);

    inflate(context, R.layout.storage_menu, this);

    View storageUsage = findViewById(R.id.storage_usage);
    storageUsage.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        new StorageUsageDialog(new ContextThemeWrapper(context, BaseDialog.getDialogTheme(context)))
            .show();
      }
    });
  }
}
