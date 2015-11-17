package data_inspector.ui.menu;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;
import data_inspector.DataInspector;
import data_inspector.R;

import static android.graphics.PixelFormat.TRANSLUCENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;

public class BaseMenu extends LinearLayout {
  public BaseMenu(Context context) {
    super(context);
    DataInspector.runtimeComponentMap.get(context).inject(this);
  }

  public static WindowManager.LayoutParams createLayoutParams(Context context) {
    Resources res = context.getResources();
    int width = res.getDimensionPixelSize(R.dimen.data_inspector_menu_width);

    final WindowManager.LayoutParams params =
        new WindowManager.LayoutParams(width, WRAP_CONTENT, TYPE_SYSTEM_ERROR,
            FLAG_NOT_FOCUSABLE | FLAG_NOT_TOUCH_MODAL | FLAG_LAYOUT_NO_LIMITS
                | FLAG_LAYOUT_INSET_DECOR | FLAG_LAYOUT_IN_SCREEN, TRANSLUCENT);
    params.y = res.getDimensionPixelSize(R.dimen.data_inspector_toolbar_height);
    if (Build.VERSION.SDK_INT == 23) { // MARSHMALLOW
      params.y = res.getDimensionPixelSize(R.dimen.data_inspector_toolbar_height_m);
    }
    params.gravity = Gravity.TOP | Gravity.RIGHT;

    return params;
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    setAlpha(0f);

    ObjectAnimator animator = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f);
    animator.setDuration(200);
    animator.start();
  }
}
