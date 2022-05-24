package com.zizo.zizotodo.Reminder;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.zizo.zizotodo.Main.MainFragment;
import com.zizo.zizotodo.R;
import com.zizo.zizotodo.Utility.StoreRetrieveData;
import com.zizo.zizotodo.Utility.ToDoItem;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import fr.ganfra.materialspinner.MaterialSpinner;

public class ReminderFragment extends Fragment {

    public static final String TODOUUID = "com.zizo.todouuid";

    private TextView mtoDoTextTextView;
    private TextView mtoDoDescriptionTextView;
    private Button mRemoveToDoButton;
    private MaterialSpinner mSnoozeSpinner;
    private String[] snoozeOptionsArray;
    private TextView mSnoozeTextView;

    private StoreRetrieveData storeRetrieveData;
    ArrayList<ToDoItem> toDoItems;
    ToDoItem mItem;
    String theme;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        theme = getActivity().getSharedPreferences(MainFragment.THEME_PREFERENCES, MODE_PRIVATE).getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME);
        if (theme.equals(MainFragment.LIGHTTHEME)) {
            getActivity().setTheme(R.style.CustomStyle_LightTheme);
        } else {
            getActivity().setTheme(R.style.CustomStyle_DarkTheme);
        }

        storeRetrieveData = new StoreRetrieveData(getContext(), MainFragment.FILENAME);
        toDoItems = MainFragment.getLocallyStoredData(storeRetrieveData);

        ((AppCompatActivity) getActivity()).setSupportActionBar(view.findViewById(R.id.toolbar));

        Intent i = getActivity().getIntent();
        UUID id = (UUID) i.getSerializableExtra(TODOUUID);
        mItem = null;
        for (ToDoItem item : toDoItems) {
            if (item.getmTodoIdentifier().equals(id)) {
                mItem = item;
                break;
            }
        }

        snoozeOptionsArray = getResources().getStringArray(R.array.snooze_options);

        mRemoveToDoButton = view.findViewById(R.id.toDoReminderRemoveButton);
        mtoDoTextTextView = view.findViewById(R.id.toDoReminderTextViewBody);
        mSnoozeTextView = view.findViewById(R.id.reminderViewSnoozeTextView);
        mSnoozeSpinner = view.findViewById(R.id.todoReminderSnoozeSpinner);

        mtoDoTextTextView.setText(mItem.getmTodoText());

        if(!mItem.getmTodoDescription().isEmpty()){
            mtoDoDescriptionTextView = view.findViewById(R.id.description);
            mtoDoDescriptionTextView.setVisibility(View.VISIBLE);
            mtoDoDescriptionTextView.setText(mItem.getmTodoDescription());
        }

        if (theme.equals(MainFragment.LIGHTTHEME)) {
            mSnoozeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.secondary_text));
        } else {
            mSnoozeTextView.setTextColor(Color.WHITE);
            mSnoozeTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_snooze_white_24dp, 0, 0, 0);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),R.layout.spinner_text_view,snoozeOptionsArray);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mSnoozeSpinner.setAdapter(adapter);

        mRemoveToDoButton.setOnClickListener(v -> {
            toDoItems.remove(mItem);
            changeOccurred();
            saveData();
            getActivity().finish();
        });
    }

    private void changeOccurred() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainFragment.SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(MainFragment.CHANGE_OCCURED, true);
        editor.apply();
    }

    private void saveData() {
        try {
            storeRetrieveData.saveToFile(toDoItems);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_reminder,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.toDoReminderDoneMenuItem){
            Date date = addTimeToDate(valueFromSpinner());
            mItem.setmTodoDate(date);
            mItem.setmHasReminder(true);
            changeOccurred();
            saveData();
            getActivity().finish();
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    private Date addTimeToDate(int mins) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, mins);
        return calendar.getTime();
    }

    private int valueFromSpinner() {
        switch (mSnoozeSpinner.getSelectedItemPosition()) {
            case 0:
                return 10;
            case 1:
                return 30;
            case 2:
                return 60;
            default:
                return 0;
        }
    }

    public static ReminderFragment newInstance() {
        return new ReminderFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reminder, container, false);
    }
}