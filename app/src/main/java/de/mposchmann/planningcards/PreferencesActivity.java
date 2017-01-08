package de.mposchmann.planningcards;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;


public class PreferencesActivity extends PreferenceActivity {
    PreferencesFragment fragment = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(R.string.title_settings);
        fragment = new PreferencesFragment();
        getFragmentManager()
            .beginTransaction()
            .replace(android.R.id.content, fragment)
            .commit();

    }

    public static void start(Context context) {

        Intent myIntent = new Intent(context, PreferencesActivity.class);
        context.startActivity(myIntent);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // seems to be an Android bug. We have to forward onActivityResult manually for in app
        // billing requests.
        if (requestCode > 1000) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }


}
