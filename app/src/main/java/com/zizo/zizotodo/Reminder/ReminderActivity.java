package com.zizo.zizotodo.Reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;

import com.zizo.zizotodo.Main.MainFragment;
import com.zizo.zizotodo.R;

public class ReminderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        setUpInitialFragment(savedInstanceState);
    }

    private void setUpInitialFragment(Bundle savedInstanceState){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ReminderFragment.newInstance())
                .commit();
    }
}