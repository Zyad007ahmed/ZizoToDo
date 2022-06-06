package com.zizo.zizotodo.AddToDo;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.zizo.zizotodo.Main.MainFragment;
import com.zizo.zizotodo.R;
import com.zizo.zizotodo.Utility.ToDoItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

public class AddToDoFragment extends Fragment implements DatePickerDialog.OnDateSetListener , TimePickerDialog.OnTimeSetListener {

    private ToDoItem userTodoItem;
    private String userText;
    private String userDescription;
    private boolean userHasReminder;
    private int userColor;
    private Date userReminderDate;

    private String theme;

    private LinearLayout mContainerLayout;
    private FloatingActionButton mToDoSendFloatingActionButton;
    private SwitchCompat mToDoDateSwitch;
    private LinearLayout mUserDateSpinnerContainingLinearLayout;
    private TextView mReminderTextView;
    private EditText mToDoTextBodyEditText;
    private EditText mToDoTextBodyDescription;
    private EditText mDateEditText;
    private EditText mTimeEditText;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        theme = getActivity().getSharedPreferences(MainFragment.THEME_PREFERENCES, MODE_PRIVATE).getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME);
        if (theme.equals(MainFragment.LIGHTTHEME)) {
            getActivity().setTheme(R.style.CustomStyle_LightTheme);
        } else {
            getActivity().setTheme(R.style.CustomStyle_DarkTheme);
        }

        final Drawable cross = ContextCompat.getDrawable(getContext(), R.drawable.ic_clear_white_24dp);
        if (cross != null) {
            cross.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getContext(), R.color.icons), PorterDuff.Mode.SRC_ATOP));
        }

        ((AppCompatActivity) getActivity()).setSupportActionBar(view.findViewById(R.id.toolbar));
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(cross);
        }

        userTodoItem = (ToDoItem) getActivity().getIntent().getSerializableExtra(MainFragment.TODOITEM);

        userText = userTodoItem.getmTodoText();
        userDescription = userTodoItem.getmTodoDescription();
        userHasReminder = userTodoItem.ismHasReminder();
        userReminderDate = userTodoItem.getmTodoDate();
        userColor = userTodoItem.getmTodoColor();

        ImageButton reminderIconImageButton = view.findViewById(R.id.userToDoReminderIconImageButton);
        TextView reminderTextView = view.findViewById(R.id.userToDoRemindMeTextView);
        if (theme.equals(MainFragment.DARKTHEME)) {
            reminderIconImageButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_alarm_add_white_24dp));
            reminderTextView.setTextColor(Color.WHITE);
        }

        mContainerLayout = view.findViewById(R.id.todoReminderAndDateContainerLayout);
        mUserDateSpinnerContainingLinearLayout = view.findViewById(R.id.toDoEnterDateLinearLayout);
        mToDoTextBodyEditText = view.findViewById(R.id.userToDoEditText);
        mToDoTextBodyDescription = view.findViewById(R.id.userToDoDescription);
        mToDoDateSwitch = view.findViewById(R.id.toDoHasDateSwitchCompat);
        mToDoSendFloatingActionButton = view.findViewById(R.id.makeToDoFloatingActionButton);
        mReminderTextView = view.findViewById(R.id.newToDoDateTimeReminderTextView);
        mDateEditText = view.findViewById(R.id.newTodoDateEditText);
        mTimeEditText = view.findViewById(R.id.newTodoTimeEditText);

        mContainerLayout.setOnClickListener(v -> {
            hideKeyboard(mToDoTextBodyEditText);
            hideKeyboard(mToDoTextBodyDescription);
        });

        // different
        if (userHasReminder && userReminderDate != null) {
            setEnterDateLayoutVisibleWithAnimations(true);
            setDateAndTimeEditText();
        }

        if (userReminderDate == null) {
            mToDoDateSwitch.setChecked(false);
            mUserDateSpinnerContainingLinearLayout.setVisibility(View.INVISIBLE);   // different
        }

        mToDoTextBodyEditText.requestFocus();
        mToDoTextBodyEditText.setText(userText);
        mToDoTextBodyEditText.setSelection(mToDoTextBodyEditText.length());
        AtomicReference<InputMethodManager> imm = new AtomicReference<>((InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE));
        imm.get().showSoftInput(mToDoTextBodyEditText, InputMethodManager.SHOW_IMPLICIT);
        mToDoTextBodyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userText = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mToDoTextBodyDescription.setText(userDescription);
        mToDoTextBodyDescription.setSelection(0);   //different
        mToDoTextBodyDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userDescription = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //  different
        mToDoDateSwitch.setChecked(userHasReminder && userReminderDate != null);
        mToDoDateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                userReminderDate = null;
            }
            userHasReminder = isChecked;
            setDateAndTimeEditText();
            setEnterDateLayoutVisibleWithAnimations(isChecked);
            hideKeyboard(mToDoTextBodyEditText);
            hideKeyboard(mToDoTextBodyDescription);
        });
        setEnterDateLayoutVisible(mToDoDateSwitch.isChecked());

        mToDoSendFloatingActionButton.setOnClickListener(v -> {
            if (mToDoTextBodyEditText.getText().toString().trim().length() <= 0 &&
                    mToDoTextBodyDescription.getText().toString().trim().length() > 0) {
                mToDoTextBodyEditText.setError(getString(R.string.todo_error));
                mToDoTextBodyEditText.requestFocus();
                imm.set((InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE));
                imm.get().showSoftInput(mToDoTextBodyEditText, InputMethodManager.SHOW_IMPLICIT);
                return;
            } else {
                makeResult();
                getActivity().finish();
            }
        });

        mDateEditText.setOnClickListener(v -> {
            Date date;
            if(userReminderDate != null){
                date = userReminderDate;
            }else{
                date = new Date();
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(AddToDoFragment.this,year,month,day);
            if(theme.equals(MainFragment.DARKTHEME)){
                datePickerDialog.setThemeDark(true);
            }
            cal.setTime(new Date());
            datePickerDialog.setMinDate(cal);
            datePickerDialog.show(getActivity().getFragmentManager(),"DateFragment");
        });

        mTimeEditText.setOnClickListener(v ->{
            Date date;
            if(userReminderDate != null){
                date = userReminderDate;
            }else{
                date = new Date();
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(AddToDoFragment.this,hour,minute,DateFormat.is24HourFormat(getContext()));
            if(theme.equals(MainFragment.DARKTHEME)){
                timePickerDialog.setThemeDark(true);
            }
            timePickerDialog.show(getActivity().getFragmentManager(),"DateFragment");
        });

    }

    //  different
    private void makeResult() {
        Intent i = new Intent();
        if (userText.trim().length() > 0) {
            String capitalized = Character.toUpperCase(userText.trim().charAt(0)) + userText.trim().substring(1);
            userTodoItem.setmTodoText(capitalized);
            userTodoItem.setmTodoDescription(userDescription.trim());
        } else {
            userTodoItem.setmTodoText(userText);
            userTodoItem.setmTodoDescription(userDescription);
        }

        if (userReminderDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(userReminderDate);
            cal.set(Calendar.SECOND, 0);
            userReminderDate = cal.getTime();
        }

        userTodoItem.setmHasReminder(userHasReminder);
        userTodoItem.setmTodoDate(userReminderDate);
        userTodoItem.setmTodoColor(userColor);

        i.putExtra(MainFragment.TODOITEM, userTodoItem);
        getActivity().setResult(Activity.RESULT_OK, i);
    }

    private void setDateAndTimeEditText() {
        if (userHasReminder && userReminderDate != null) {
            String userDate = formatDate("d MMM, yyyy", userReminderDate);
            String timeFormat;
            if (DateFormat.is24HourFormat(getContext())) {
                timeFormat = "k:mm";
            } else {
                timeFormat = "h:mm a";
            }
            String userTime = formatDate(timeFormat, userReminderDate);
            mTimeEditText.setText(userTime);
            if(userDate.equals(formatDate("d MMM, yyyy",new Date()))) {
                mDateEditText.setText(getString(R.string.date_reminder_default));
            }else{
                mDateEditText.setText(userDate);
            }
        } else {
            mDateEditText.setText(getString(R.string.date_reminder_default));

            boolean time24 = DateFormat.is24HourFormat(getContext());
            Calendar cal = Calendar.getInstance();
            if (time24) {
                cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1);
            } else {
                cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) + 1);
            }
            cal.set(Calendar.MINUTE, 0);
            userReminderDate = cal.getTime();

            String timeString;
            if (time24) {
                timeString = formatDate("k:mm", userReminderDate);
            } else {
                timeString = formatDate("h:mm a", userReminderDate);
            }
            mTimeEditText.setText(timeString);
        }
    }

    private void setEnterDateLayoutVisible(boolean checked) {
        if (checked) {
            mUserDateSpinnerContainingLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mUserDateSpinnerContainingLinearLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void setEnterDateLayoutVisibleWithAnimations(boolean checked) {
        if (checked) {
            setReminderTextView();
            mUserDateSpinnerContainingLinearLayout.animate().alpha(1f).setDuration(500).setListener(
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mUserDateSpinnerContainingLinearLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }
            );
        } else {
            mUserDateSpinnerContainingLinearLayout.animate().alpha(0f).setDuration(500).setListener(
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mUserDateSpinnerContainingLinearLayout.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }
            );
        }
    }

    // different
    private void setReminderTextView() {
        if (userReminderDate != null) {
            if (userReminderDate.before(new Date())) {
                mReminderTextView.setText(getString(R.string.date_error_check_again));
                mReminderTextView.setTextColor(Color.RED);
                return;
            }

            Date date = userReminderDate;
            String dateString = formatDate("d MMM, yyyy", date);
            String timeString;
            String amPm = "";

            if (DateFormat.is24HourFormat(getContext())) {
                timeString = formatDate("k:mm", date);
            } else {
                timeString = formatDate("h:mm", date);
                amPm = formatDate("a", date);
            }

            String finalString = String.format(getResources().getString(R.string.remind_date_and_time), dateString, timeString, amPm);
            mReminderTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.secondary_text));
            mReminderTextView.setText(finalString);
        }
    }

    public static String formatDate(String format, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    private void hideKeyboard(EditText et) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    public static AddToDoFragment newInstance() {
        return new AddToDoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_to_do, container, false);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();

        if(userReminderDate != null){
            cal.setTime(userReminderDate);
        }

        int hour , minute;
        if(DateFormat.is24HourFormat(getContext())){
            hour = cal.get(Calendar.HOUR_OF_DAY);
        }else{
            hour = cal.get(Calendar.HOUR);
        }
        minute = cal.get(Calendar.MINUTE);

        cal.set(year,month,day,hour,minute,0);
        userReminderDate = cal.getTime();
        setReminderTextView();
        setDateEditText();
    }

    private void setDateEditText() {
        String dateFormat = "d MMM, yyyy";
//        mDateEditText.setText(formatDate(dateFormat, userReminderDate));
        String userDate = formatDate(dateFormat, userReminderDate);
        if(userDate.equals(formatDate(dateFormat,new Date()))) {
            mDateEditText.setText(getString(R.string.date_reminder_default));
        }else{
            mDateEditText.setText(userDate);
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        if(userReminderDate != null){
            cal.setTime(userReminderDate);
        }

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(year, month, day, hour, minute, 0);
        userReminderDate = cal.getTime();

        setReminderTextView();
        setTimeEditText();
    }

    private void setTimeEditText() {
        String dateFormat;
        if (DateFormat.is24HourFormat(getContext())) {
            dateFormat = "k:mm";
        } else {
            dateFormat = "h:mm a";

        }
        mTimeEditText.setText(formatDate(dateFormat, userReminderDate));
    }
}