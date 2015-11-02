package data_inspector.ui.menu;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import data_inspector.DataInspector;
import data_inspector.R;
import data_inspector.ui.dialog.BaseDialog;
import data_inspector.ui.dialog.PreferenceEditorDialog;

public class PreferenceMenu extends BaseMenu {

  @SuppressWarnings("ConstantConditions") public PreferenceMenu(final Context context) {
    super(context);
    DataInspector.runtimeComponentMap.get(context).inject(this);

    inflate(context, R.layout.preference_menu, this);

    View openPreferenceEditor = findViewById(R.id.open_preference_editor);
    openPreferenceEditor.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        new PreferenceEditorDialog(
            new ContextThemeWrapper(context, BaseDialog.getDialogTheme(context))).show();
      }
    });
  }
}

