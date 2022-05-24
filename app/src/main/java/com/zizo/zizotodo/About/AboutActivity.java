package com.zizo.zizotodo.About;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.zizo.zizotodo.Main.MainFragment;
import com.zizo.zizotodo.R;

public class AboutActivity extends AppCompatActivity {

    String theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        theme = getSharedPreferences(MainFragment.THEME_PREFERENCES, MODE_PRIVATE).getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME);
        if (theme.equals(MainFragment.DARKTHEME)) {
            setTheme(R.style.CustomStyle_DarkTheme);
        } else {
            setTheme(R.style.CustomStyle_LightTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        final Drawable backArrow = ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_24);
        if (backArrow != null) {
            backArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }

        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(backArrow);
        }

        setUpInitialFragment(savedInstanceState);
    }

    private void setUpInitialFragment(Bundle savedInstanceState){
        if(savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, AboutFragment.newInstance())
                    .commit();
        }
    }
}