package data_inspector.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import data_inspector.DataInspector;
import data_inspector.R;
import data_inspector.dagger.scope.PerActivity;
import data_inspector.ui.menu.BaseMenu;
import data_inspector.ui.menu.DatabaseMenu;
import data_inspector.ui.menu.PreferenceMenu;
import data_inspector.ui.menu.SettingsMenu;
import data_inspector.ui.menu.StorageMenu;
import javax.inject.Inject;

import static android.graphics.PixelFormat.TRANSLUCENT;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;

@PerActivity public class DataInspectorToolbar extends FrameLayout {
  private static final int TOOLBAR_MENU_ITEMS = 5;
  private final Context context;
  private final int toolbarWidth;
  private final int toolbarClosedWidth;

  @Inject WindowManager windowManager;

  private View mToolbar;
  private ImageButton mToggleButton;
  private BaseMenu mMenu;

  @Inject public DataInspectorToolbar(Context context) {
    super(context);
    DataInspector.runtimeComponentMap.get(context).inject(this);
    this.context = context;
    inflate(context, R.layout.data_inspector_toolbar, this);

    Resources resources = this.context.getResources();
    toolbarWidth = resources.getDimensionPixelSize(R.dimen.toolbar_header_width)
        + resources.getDimensionPixelSize(R.dimen.toolbar_icon_width) * TOOLBAR_MENU_ITEMS;
    toolbarClosedWidth = resources.getDimensionPixelSize(R.dimen.toolbar_closed_width);
  }

  public static WindowManager.LayoutParams createLayoutParams(Context context) {
    Resources res = context.getResources();
    int width = res.getDimensionPixelSize(R.dimen.toolbar_header_width)
        + res.getDimensionPixelSize(R.dimen.toolbar_icon_width) * TOOLBAR_MENU_ITEMS;
    int height = res.getDimensionPixelSize(R.dimen.toolbar_height);
    if (Build.VERSION.SDK_INT == 23) { // MARSHMALLOW
      height = res.getDimensionPixelSize(R.dimen.toolbar_height_m);
    }

    final WindowManager.LayoutParams params =
        new WindowManager.LayoutParams(width, height, TYPE_SYSTEM_ERROR,
            FLAG_NOT_FOCUSABLE | FLAG_NOT_TOUCH_MODAL | FLAG_LAYOUT_NO_LIMITS
                | FLAG_LAYOUT_INSET_DECOR | FLAG_LAYOUT_IN_SCREEN, TRANSLUCENT);
    params.gravity = Gravity.TOP | Gravity.RIGHT;

    return params;
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();

    mToolbar = findViewById(R.id.toolbar);
    mToggleButton = (ImageButton) findViewById(R.id.toggle_menu);
    ImageButton buttonPreferenceMenu = (ImageButton) findViewById(R.id.preference_menu);
    ImageButton buttonDatabaseMenu = (ImageButton) findViewById(R.id.database_menu);
    ImageButton buttonStorageMenu = (ImageButton) findViewById(R.id.storage_menu);
    ImageButton buttonSettingsMenu = (ImageButton) findViewById(R.id.settings_menu);

    mToolbar.setTranslationX(toolbarWidth);

    ObjectAnimator animator = ObjectAnimator.ofFloat(mToolbar, "translationX", toolbarWidth,
        toolbarWidth - toolbarClosedWidth);
    animator.setInterpolator(new DecelerateInterpolator());
    animator.start();

    mToggleButton.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        closeMenu();
        toggleToolbar();
      }
    });

    buttonPreferenceMenu.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (!(mMenu instanceof PreferenceMenu)) {
          closeMenu();
          mMenu = new PreferenceMenu(context);
          windowManager.addView(mMenu, BaseMenu.createLayoutParams(context));
        } else {
          closeMenu();
        }
      }
    });

    buttonDatabaseMenu.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (!(mMenu instanceof DatabaseMenu)) {
          closeMenu();
          mMenu = new DatabaseMenu(context);
          windowManager.addView(mMenu, BaseMenu.createLayoutParams(context));
        } else {
          closeMenu();
        }
      }
    });

    buttonStorageMenu.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (!(mMenu instanceof StorageMenu)) {
          closeMenu();
          mMenu = new StorageMenu(context);
          windowManager.addView(mMenu, BaseMenu.createLayoutParams(context));
        } else {
          closeMenu();
        }
      }
    });

    buttonSettingsMenu.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (!(mMenu instanceof SettingsMenu)) {
          closeMenu();
          mMenu = new SettingsMenu(context);
          windowManager.addView(mMenu, BaseMenu.createLayoutParams(context));
        } else {
          closeMenu();
        }
      }
    });
  }

  @Override protected void onDetachedFromWindow() {
    closeMenu();
    super.onDetachedFromWindow();
  }

  public void closeMenu() {
    if (mMenu != null) {
      windowManager.removeViewImmediate(mMenu);
      mMenu = null;
    }
  }

  public BaseMenu getMenu() {
    return mMenu;
  }

  public void openMenu(BaseMenu baseMenu) {
    mMenu = baseMenu;
    windowManager.addView(mMenu, BaseMenu.createLayoutParams(context));
  }

  @SuppressWarnings("deprecation") public void toggleToolbar() {
    ObjectAnimator animator =
        ObjectAnimator.ofFloat(mToolbar, "translationX", mToolbar.getTranslationX(),
            mToolbar.getTranslationX() < toolbarClosedWidth ? toolbarWidth - toolbarClosedWidth
                : 0);
    animator.setInterpolator(new DecelerateInterpolator());
    animator.start();
    if (mToolbar.getTranslationX() < toolbarClosedWidth) {
      mToggleButton.setImageDrawable(
          getResources().getDrawable(R.drawable.ic_chevron_left_white_24dp));
    } else {
      mToggleButton.setImageDrawable(
          getResources().getDrawable(R.drawable.ic_chevron_right_white_24dp));
    }
  }

  @SuppressWarnings("deprecation") public void closeToolbar() {
    closeMenu();
    ObjectAnimator animator =
        ObjectAnimator.ofFloat(mToolbar, "translationX", mToolbar.getTranslationX(), toolbarWidth);
    animator.setInterpolator(new DecelerateInterpolator());
    animator.start();
    mToggleButton.setImageDrawable(
        getResources().getDrawable(R.drawable.ic_chevron_left_white_24dp));
  }
}
