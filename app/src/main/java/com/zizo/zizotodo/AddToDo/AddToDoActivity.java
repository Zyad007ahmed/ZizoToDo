package com.zizo.zizotodo.AddToDo;

import static com.zizo.zizotodo.Main.MainFragment.LIGHTTHEME;
import static com.zizo.zizotodo.Main.MainFragment.THEME_PREFERENCES;
import static com.zizo.zizotodo.Main.MainFragment.THEME_SAVED;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import com.zizo.zizotodo.Main.MainFragment;
import com.zizo.zizotodo.R;

public class AddToDoActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_add_to_do);
        setUpInitialFragment(savedInstanceState);
    }

    private void setUpInitialFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, AddToDoFragment.newInstance())
                    .commit();
        }
    }
}