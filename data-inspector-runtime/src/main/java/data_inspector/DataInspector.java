package data_inspector;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import data_inspector.dagger.ActivityComponent;
import data_inspector.dagger.ActivityModule;
import data_inspector.dagger.ApplicationComponent;
import data_inspector.dagger.ApplicationModule;
import data_inspector.dagger.DaggerActivityComponent;
import data_inspector.dagger.DaggerApplicationComponent;
import data_inspector.logger.PreferenceLogger;
import data_inspector.logger.StorageLogger;
import data_inspector.ui.DataInspectorToolbar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;

public final class DataInspector {
  public static final String TAG = "DataInspector";
  public static ApplicationComponent applicationComponent;
  public static Set<Context> contextSet = new HashSet<>();
  public static Map<Context, ActivityComponent> runtimeComponentMap = new HashMap<>();
  public static Map<Context, DataInspectorToolbar> toolbarMap = new HashMap<>();

  @Inject WindowManager windowManager;
  @Inject DataInspectorToolbar toolbar;

  private PreferenceLogger preferenceLogger;
  private StorageLogger storageLogger;

  public static DataInspector create() {
    return new DataInspector();
  }

  @SuppressWarnings("unused") public void onCreate(Context context) {
    if (!contextSet.contains(context)) {
      contextSet.add(context);

      if (applicationComponent == null) {
        // create dagger component for the application
        Application application = (Application) context.getApplicationContext();
        applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(new ApplicationModule(application))
            .build();
      }

      if (!runtimeComponentMap.containsKey(context)) {
        // create dagger components per activity
        ActivityComponent activityComponent = DaggerActivityComponent.builder()
            .applicationComponent(applicationComponent)
            .activityModule(new ActivityModule((Activity) context))
            .build();
        runtimeComponentMap.put(context, activityComponent);
        activityComponent.inject(this);
      }

      windowManager.addView(toolbar, DataInspectorToolbar.createLayoutParams(context));
      toolbarMap.put(context, toolbar);

      // Initial Loggers
      if (preferenceLogger == null) {
        preferenceLogger = new PreferenceLogger((Application) context.getApplicationContext());
      }

      if (storageLogger == null) {
        storageLogger = new StorageLogger((Application) context.getApplicationContext());
      }
    }
  }

  @SuppressWarnings("unused") public void onResume() {
    toolbar.setVisibility(View.VISIBLE);
  }

  @SuppressWarnings("unused") public void onPause() {
    toolbar.closeMenu();
    toolbar.setVisibility(View.GONE);
  }

  @SuppressWarnings("unused") public void onDestroy(Context context) {
    // remove dagger component map
    runtimeComponentMap.remove(context);

    DataInspectorToolbar toolbarInstance = toolbarMap.get(context);
    if (toolbarInstance != null) windowManager.removeViewImmediate(toolbarInstance);
  }
}
