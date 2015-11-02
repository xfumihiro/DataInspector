package data_inspector.ui.menu;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.Switch;
import com.f2prateek.rx.preferences.Preference;
import data_inspector.DataInspector;
import data_inspector.R;
import data_inspector.dagger.qualifier.LogPreferenceEvents;
import data_inspector.dagger.qualifier.LogStorageEvents;
import javax.inject.Inject;

public class SettingsMenu extends BaseMenu {

  @Inject @LogPreferenceEvents Preference<Boolean> logPreferenceEvents;
  @Inject @LogStorageEvents Preference<Boolean> logStorageEvents;

  @SuppressWarnings("ConstantConditions") public SettingsMenu(final Context context) {
    super(context);
    DataInspector.runtimeComponentMap.get(context).inject(this);

    inflate(context, R.layout.settings_menu, this);

    Switch logPreferenceEventsSwitch = (Switch) findViewById(R.id.log_preference_events);
    logPreferenceEventsSwitch.setChecked(logPreferenceEvents.get());
    logPreferenceEventsSwitch.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            logPreferenceEvents.set(isChecked);
          }
        });

    Switch logStorageEventsSwitch = (Switch) findViewById(R.id.log_storage_events);
    logStorageEventsSwitch.setChecked(logStorageEvents.get());
    logStorageEventsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        logStorageEvents.set(isChecked);
      }
    });
  }
}
