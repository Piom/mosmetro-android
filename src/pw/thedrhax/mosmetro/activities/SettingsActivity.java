package pw.thedrhax.mosmetro.activities;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import pw.thedrhax.mosmetro.R;
import pw.thedrhax.mosmetro.updater.UpdateCheckTask;

public class SettingsActivity extends Activity {

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Populate preferences
        SettingsFragment settings = new SettingsFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, settings)
                .commit();
        getFragmentManager().executePendingTransactions();

        // Add version name and code
        Preference app_name = settings.findPreference("app_name");
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            app_name.setSummary("Версия: " + pInfo.versionName + "-" + pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException ex) {
            app_name.setSummary("");
        }

        // Check for updates on start
        new UpdateCheckTask(this) {
            @Override
            public void result(boolean hasUpdate, Branch current_branch) {
                if (hasUpdate) showDialog();
            }
        }.execute();

        // Update checker
        Preference pref_updater_check = settings.findPreference("pref_updater_check");
        pref_updater_check.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new UpdateCheckTask(SettingsActivity.this) {
                    @Override
                    public void result(boolean hasUpdate, final Branch current_branch) {
                        showDialog();
                    }
                }.execute();
                return false;
            }
        });
    }
}
