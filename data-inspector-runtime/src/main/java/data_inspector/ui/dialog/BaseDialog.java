package data_inspector.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import data_inspector.DataInspector;
import data_inspector.R;
import data_inspector.ui.DataInspectorToolbar;
import data_inspector.ui.menu.BaseMenu;
import javax.inject.Inject;

public class BaseDialog extends AlertDialog {
  @Inject DataInspectorToolbar toolbar;
  BaseMenu menu;

  public BaseDialog(Context context) {
    super(context);
    DataInspector.runtimeComponentMap.get(((ContextThemeWrapper) context).getBaseContext())
        .inject(this);
    setCancelable(true);
    setCanceledOnTouchOutside(true);
    setOnCancelListener(new OnCancelListener() {
      @Override public void onCancel(DialogInterface dialog) {
        restoreOpenedMenu();
      }
    });
  }

  @Override public void onAttachedToWindow() {
    menu = toolbar.getMenu();
    toolbar.closeToolbar();
    super.onAttachedToWindow();
  }

  @Override public void onDetachedFromWindow() {
    super.onDetachedFromWindow();
  }

  protected void restoreOpenedMenu() {
    toolbar.toggleToolbar();
    if (menu != null) toolbar.openMenu(menu);
  }

  public static int getDialogTheme(Context context) {
    TypedValue outValue = new TypedValue();
    context.getTheme().resolveAttribute(R.attr.isLightTheme, outValue, true);
    return outValue.data != 0 ? R.style.DialogThemeLight : R.style.DialogTheme;
  }
}
