package data_inspector.dagger;

import android.view.WindowManager;
import com.f2prateek.rx.preferences.Preference;
import dagger.Component;
import data_inspector.dagger.qualifier.LogPreferenceEvents;
import data_inspector.dagger.qualifier.LogStorageEvents;
import data_inspector.logger.PreferenceLogger;
import data_inspector.logger.StorageLogger;
import javax.inject.Singleton;

@Singleton @Component(modules = ApplicationModule.class) public interface ApplicationComponent {

  void inject(PreferenceLogger preferenceLogger);

  void inject(StorageLogger storageLogger);

  // expose to sub components

  WindowManager provideWindowManager();

  @LogPreferenceEvents Preference<Boolean> provideLogPreferenceEventsFlag();

  @LogStorageEvents Preference<Boolean> provideLogStorageEventsFlag();
}
