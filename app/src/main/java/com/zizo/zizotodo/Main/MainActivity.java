package com.zizo.zizotodo.Main;

import static com.zizo.zizotodo.Main.MainFragment.LIGHTTHEME;
import static com.zizo.zizotodo.Main.MainFragment.RECREATE_ACTIVITY;
import static com.zizo.zizotodo.Main.MainFragment.THEME_PREFERENCES;
import static com.zizo.zizotodo.Main.MainFragment.THEME_SAVED;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.zizo.zizotodo.About.AboutActivity;
import com.zizo.zizotodo.R;
import com.zizo.zizotodo.Settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    private String theme = "name_of_the_theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        theme = getSharedPreferences(MainFragment.THEME_PREFERENCES, MODE_PRIVATE).getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME);
        if (theme.equals(MainFragment.DARKTHEME)) {
            setTheme(R.style.CustomStyle_DarkTheme);
        } else {
            setTheme(R.style.CustomStyle_LightTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpInitialFragment(savedInstanceState);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    private void setUpInitialFragment(Bundle savedInstanceState){
        if(savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, MainFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutMeMenuItem:
                Intent i = new Intent(this, AboutActivity.class);
                startActivity(i);
                return true;
            case R.id.preferences:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
        We need to do this, as this activity's onCreate won't be called when coming back from SettingsActivity,
        thus our changes to dark/light mode won't take place, as the setContentView() is not called again.
        So, inside our SettingsFragment, whenever the checkbox's value is changed, in our shared preferences,
        we mark our recreate_activity key as true.

        Note: the recreate_key's value is changed to false before calling recreate(), or we woudl have ended up in an infinite loop,
        as onResume() will be called on recreation, which will again call recreate() and so on....
        and get an ANR

         */
        if (getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).getBoolean(RECREATE_ACTIVITY, false)) {
            SharedPreferences.Editor editor = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).edit();
            editor.putBoolean(RECREATE_ACTIVITY, false);
            editor.apply();
            recreate();
        }
    }
}