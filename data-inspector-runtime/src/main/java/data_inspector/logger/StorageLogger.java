package data_inspector.logger;

import android.app.Application;
import android.os.FileObserver;
import android.util.Log;
import com.f2prateek.rx.preferences.Preference;
import data_inspector.DataInspector;
import data_inspector.dagger.qualifier.LogStorageEvents;
import javax.inject.Inject;
import rx.functions.Action1;

public class StorageLogger {
  private final Application context;

  @Inject @LogStorageEvents Preference<Boolean> logStorageEvents;

  private StorageObserver storageObserver;

  public StorageLogger(Application application) {
    DataInspector.applicationComponent.inject(this);

    context = application;

    logStorageEvents.asObservable().subscribe(new Action1<Boolean>() {
      @Override public void call(Boolean aBoolean) {
        if (aBoolean) {
          storageObserver = new StorageObserver("/data/data/" + context.getPackageName());
          storageObserver.startWatching();
        } else {
          if (storageObserver != null) storageObserver.stopWatching();
          storageObserver = null;
        }
      }
    });
  }

  class StorageObserver extends FileObserver {

    public StorageObserver(String path) {
      super(path);
    }

    @Override public void onEvent(int event, String path) {
      StringBuilder eventStr = new StringBuilder("Storage Event Log > event: ");
      if ((event & ACCESS) != 0) eventStr.append("ACCESS ");
      if ((event & MODIFY) != 0) eventStr.append("MODIFY ");
      if ((event & ATTRIB) != 0) eventStr.append("ATTRIB ");
      if ((event & CLOSE_WRITE) != 0) eventStr.append("CLOSE_WRITE ");
      if ((event & CLOSE_NOWRITE) != 0) eventStr.append("CLOSE_NOWRITE ");
      if ((event & OPEN) != 0) eventStr.append("OPEN ");
      if ((event & MOVED_FROM) != 0) eventStr.append("MOVED_FROM ");
      if ((event & MOVED_TO) != 0) eventStr.append("MOVED_TO ");
      if ((event & CREATE) != 0) eventStr.append("CREATE ");
      if ((event & DELETE) != 0) eventStr.append("DELETE ");
      if ((event & DELETE_SELF) != 0) eventStr.append("DELETE_SELF ");
      if ((event & MOVE_SELF) != 0) eventStr.append("MOVE_SELF ");
      eventStr.append(" ,path: ").append(path);
      Log.d(DataInspector.TAG, eventStr.toString());
    }
  }
}
