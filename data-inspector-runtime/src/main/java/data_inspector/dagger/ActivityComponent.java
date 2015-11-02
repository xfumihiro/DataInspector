package data_inspector.dagger;

import dagger.Component;
import data_inspector.DataInspector;
import data_inspector.dagger.scope.PerActivity;
import data_inspector.ui.DataInspectorToolbar;
import data_inspector.ui.dialog.BaseDialog;
import data_inspector.ui.dialog.DatabaseEditorDialog;
import data_inspector.ui.dialog.PreferenceEditorDialog;
import data_inspector.ui.menu.BaseMenu;
import data_inspector.ui.menu.DatabaseMenu;
import data_inspector.ui.menu.PreferenceMenu;
import data_inspector.ui.menu.SettingsMenu;
import data_inspector.ui.menu.StorageMenu;

@PerActivity @Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
  void inject(DataInspector dataInspector);

  void inject(DataInspectorToolbar dataInspectorToolbar);

  void inject(BaseMenu baseMenu);

  void inject(PreferenceMenu preferenceMenu);

  void inject(DatabaseMenu databaseMenu);

  void inject(StorageMenu storageMenu);

  void inject(SettingsMenu settingsMenu);

  void inject(BaseDialog baseDialog);

  void inject(PreferenceEditorDialog preferenceEditorDialog);

  void inject(DatabaseEditorDialog databaseEditorDialog);
}
