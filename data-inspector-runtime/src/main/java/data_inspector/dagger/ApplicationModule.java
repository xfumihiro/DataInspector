package data_inspector.dagger;

import android.app.Application;
import android.content.SharedPreferences;
import android.view.WindowManager;
import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import dagger.Module;
import dagger.Provides;
import data_inspector.dagger.qualifier.LogPreferenceEvents;
import data_inspector.dagger.qualifier.LogStorageEvents;
import javax.inject.Singleton;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.WINDOW_SERVICE;

@Module public class ApplicationModule {
  private final Application application;

  public ApplicationModule(Application application) {
    this.application = application;
  }

  @Provides @Singleton Application provideApplicationContext() {
    return this.application;
  }

  @Provides @Singleton SharedPreferences provideSharedPreferences(Application app) {
    return app.getSharedPreferences("data-inspector", MODE_PRIVATE);
  }

  @Provides @Singleton RxSharedPreferences provideRxSharedPreferences(SharedPreferences prefs) {
    return RxSharedPreferences.create(prefs);
  }

  @Provides @Singleton WindowManager provideWindowManager() {
    return (WindowManager) application.getSystemService(WINDOW_SERVICE);
  }

  @Provides @Singleton @LogPreferenceEvents Preference<Boolean> provideLogPreferenceEventsFlag(
      RxSharedPreferences prefs) {
    return prefs.getBoolean("logPreferenceEvents", false);
  }

  @Provides @Singleton @LogStorageEvents Preference<Boolean> provideLogStorageEventsFlag(
      RxSharedPreferences prefs) {
    return prefs.getBoolean("logStorageEvents", false);
  }
}
