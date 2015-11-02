package data_inspector.dagger;

import android.app.Activity;
import android.content.Context;
import dagger.Module;
import dagger.Provides;
import data_inspector.dagger.scope.PerActivity;

@Module public class ActivityModule {
  private final Activity activity;

  public ActivityModule(Activity activity) {
    this.activity = activity;
  }

  @Provides @PerActivity Activity provideActivity() {
    return this.activity;
  }

  @Provides @PerActivity Context provideActivityContext() {
    return this.activity;
  }
}