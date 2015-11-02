package data_inspector.logger;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import data_inspector.DataInspector;
import data_inspector.dagger.qualifier.LogPreferenceEvents;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import rx.Subscription;
import rx.functions.Action1;

public class PreferenceLogger {
  private final Application context;

  @Inject @LogPreferenceEvents Preference<Boolean> logPreferenceEvents;

  private Map<String, Subscription> subscriptionMap = new HashMap<>();

  public PreferenceLogger(Application application) {
    DataInspector.applicationComponent.inject(this);

    context = application;

    logPreferenceEvents.asObservable().subscribe(new Action1<Boolean>() {
      @Override public void call(Boolean aBoolean) {
        if (aBoolean) {
          subscribeAll();
        } else {
          unsubscribeAll();
        }
      }
    });
  }

  private void subscribeAll() {
    File prefsDir = new File("/data/data/" + context.getPackageName() + "/shared_prefs");
    final String[] prefs = prefsDir.list();
    for (int i = 0; i < prefs.length; i++) {
      prefs[i] = prefs[i].split("\\.")[0];
      final String preferenceFile = prefs[i];
      if (!preferenceFile.equals("data-inspector")) {
        SharedPreferences preferences =
            context.getSharedPreferences(preferenceFile, Context.MODE_PRIVATE);
        RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(preferences);
        Map<String, ?> prefMap = preferences.getAll();
        List<String> keys = new ArrayList<>(prefMap.keySet());

        for (final String key : keys) {
          Subscription subscription = null;
          if (prefMap.get(key) instanceof Boolean) {
            subscription = rxSharedPreferences.getBoolean(key)
                .asObservable()
                .subscribe(new Action1<Boolean>() {
                  @Override public void call(Boolean value) {
                    Log.d(DataInspector.TAG,
                        "Preference Event Log > preference: " + preferenceFile + " [key: " + key
                            + " ,value: " + value + "]");
                  }
                });
          } else if (prefMap.get(key) instanceof Float) {
            subscription =
                rxSharedPreferences.getFloat(key).asObservable().subscribe(new Action1<Float>() {
                  @Override public void call(Float value) {
                    Log.d(DataInspector.TAG,
                        "Preference Event Log > preference: " + preferenceFile + " [key: " + key
                            + " ,value: " + value + "]");
                  }
                });
          } else if (prefMap.get(key) instanceof Integer) {
            subscription = rxSharedPreferences.getInteger(key)
                .asObservable()
                .subscribe(new Action1<Integer>() {
                  @Override public void call(Integer value) {
                    Log.d(DataInspector.TAG,
                        "Preference Event Log > preference: " + preferenceFile + " [key: " + key
                            + " ,value: " + value + "]");
                  }
                });
          } else if (prefMap.get(key) instanceof Long) {
            subscription =
                rxSharedPreferences.getLong(key).asObservable().subscribe(new Action1<Long>() {
                  @Override public void call(Long value) {
                    Log.d(DataInspector.TAG,
                        "Preference Event Log > preference: " + preferenceFile + " [key: " + key
                            + " ,value: " + value + "]");
                  }
                });
          } else if (prefMap.get(key) instanceof String) {
            subscription =
                rxSharedPreferences.getString(key).asObservable().subscribe(new Action1<String>() {
                  @Override public void call(String value) {
                    Log.d(DataInspector.TAG,
                        "Preference Event Log > preference: " + preferenceFile + " [key: " + key
                            + " ,value: " + value + "]");
                  }
                });
            //} else if (prefMap.get(key) instanceof Set<?>) {
            // TODO
          }
          subscriptionMap.put(key, subscription);
        }
      }
    }
  }

  private void unsubscribeAll() {
    for (Map.Entry<String, Subscription> entry : subscriptionMap.entrySet()) {
      entry.getValue().unsubscribe();
    }
  }
}
