package com.zizo.zizotodo.Main;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.zizo.zizotodo.AddToDo.AddToDoActivity;
import com.zizo.zizotodo.AddToDo.AddToDoFragment;
import com.zizo.zizotodo.R;
import com.zizo.zizotodo.Reminder.ReminderActivity;
import com.zizo.zizotodo.Reminder.ReminderFragment;
import com.zizo.zizotodo.Utility.ItemTouchHelperClass;
import com.zizo.zizotodo.Utility.RecyclerViewEmptySupport;
import com.zizo.zizotodo.Utility.StoreRetrieveData;
import com.zizo.zizotodo.Utility.ToDoItem;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


public class MainFragment extends Fragment {

    public static final String TODOITEM = "com.zizo.zizotodo.MainActivity";
    private ArrayList<ToDoItem> mToDoItemsArrayList;
    private StoreRetrieveData storeRetrieveData;
    private MainFragment.BasicListAdapter adapter;

    private CoordinatorLayout mCoordLayout;
    private FloatingActionButton mAddToDoItemFAB;
    private RecyclerViewEmptySupport mRecyclerView;
    private CustomRecyclerScrollViewListener customRecyclerScrollViewListener;
    private ItemTouchHelper itemTouchHelper;

    public static final String FILENAME = "todoitems.json";
    public static final String SHARED_PREF_DATA_SET_CHANGED = "com.zizo.datasetchanged";
    public static final String CHANGE_OCCURED = "com.zizo.changeoccured";
    private String theme = "name_of_the_theme";
    public static final String THEME_PREFERENCES = "com.zizo.themepref";
    public static final String RECREATE_ACTIVITY = "com.zizo.recreateactivity";
    public static final String THEME_SAVED = "com.zizo.savedtheme";
    public static final String DARKTHEME = "com.zizo.darktheme";
    public static final String LIGHTTHEME = "com.zizo.lighttheme";

    ActivityResultLauncher<Intent> launcher;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SharedPreferences sp = getContext().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(CHANGE_OCCURED, false);
        editor.apply();

        storeRetrieveData = new StoreRetrieveData(getContext(), FILENAME);
        mToDoItemsArrayList = getLocallyStoredData(storeRetrieveData);
        adapter = new BasicListAdapter(mToDoItemsArrayList);
        setAlarms();

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() != Activity.RESULT_CANCELED) {
                            Intent data = result.getData();
                            ToDoItem item = (ToDoItem) data.getSerializableExtra(TODOITEM);
                            if (item.getmTodoText().length() <= 0) {
                                return;
                            }

                            if (item.ismHasReminder() && item.getmTodoDate() != null) {
                                Intent i = new Intent(getContext(), ReminderActivity.class);
                                i.putExtra(ReminderFragment.TODOUUID,item.getmTodoIdentifier());
                                createAlarm(i, item.getmTodoIdentifier().hashCode(), item.getmTodoDate().getTime());
                            }

                            boolean existed = false;
                            for (int i = 0; i < mToDoItemsArrayList.size(); i++) {
                                if (item.getmTodoIdentifier().equals(mToDoItemsArrayList.get(i).getmTodoIdentifier())) {
                                    mToDoItemsArrayList.set(i, item);
                                    existed = true;
                                    adapter.notifyDataSetChanged();
                                    break;
                                }
                            }

                            if (!existed) {
                                mToDoItemsArrayList.add(item);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
        );

        mCoordLayout = view.findViewById(R.id.myCoordinatorLayout);
        mAddToDoItemFAB = view.findViewById(R.id.addToDoItemFAB);

        mAddToDoItemFAB.setOnClickListener(v -> {
            ToDoItem item = new ToDoItem("", "", false, null);
            int color = ColorGenerator.MATERIAL.getRandomColor();
            item.setmTodoColor(color);

            Intent newTodo = new Intent(getContext(), AddToDoActivity.class);
            newTodo.putExtra(TODOITEM, item);

            launcher.launch(newTodo);
        });

        mRecyclerView = view.findViewById(R.id.toDoRecyclerView);
        if (theme.equals(LIGHTTHEME)) {
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.primary_lightest, null));
        }
        mRecyclerView.setEmptyView(view.findViewById(R.id.toDoEmptyView));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        customRecyclerScrollViewListener = new CustomRecyclerScrollViewListener() {
            @Override
            public void show() {
                mAddToDoItemFAB.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            @Override
            public void hide() {
                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) mAddToDoItemFAB.getLayoutParams();
                int fabMargin = lp.bottomMargin;
                mAddToDoItemFAB.animate().translationY(mAddToDoItemFAB.getHeight() + fabMargin).setInterpolator(new AccelerateInterpolator(2.0f)).start();
            }
        };

        mRecyclerView.addOnScrollListener(customRecyclerScrollViewListener);

        ItemTouchHelper.Callback callback = new ItemTouchHelperClass(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.setAdapter(adapter);
    }

    public static ArrayList<ToDoItem> getLocallyStoredData(StoreRetrieveData storeRetrieveData) {
        ArrayList<ToDoItem> items = null;
        try {
            items = storeRetrieveData.loadFromFile();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        if (items == null)
            items = new ArrayList<>();

        return items;
    }

    private void setAlarms() {
        if (mToDoItemsArrayList != null) {
            for (ToDoItem item : mToDoItemsArrayList) {
                if (item.ismHasReminder() && item.getmTodoDate() != null) {
                    if (item.getmTodoDate().before(new Date())) {
                        item.setmTodoDate(null);
                        continue;
                    }
                    Intent i = new Intent(getContext(), ReminderActivity.class);
                    createAlarm(i, item.getmTodoIdentifier().hashCode(), item.getmTodoDate().getTime());
                }
            }
        }
    }

    private void createAlarm(Intent i, int requestCode, long timeInMillis) {
        AlarmManager am = getAlarmManager();
        PendingIntent pi = PendingIntent.getActivity(getContext(), requestCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, timeInMillis, pi);
    }

    private void deleteAlarm(Intent i, int requestCode) {
        if (doesPendingIntentExist(i, requestCode)) {
            PendingIntent pi = PendingIntent.getActivity(getContext(), requestCode, i, PendingIntent.FLAG_NO_CREATE);
            pi.cancel();
            getAlarmManager().cancel(pi);
        }
    }

    private boolean doesPendingIntentExist(Intent i, int requestCode) {
        PendingIntent pi = PendingIntent.getActivity(getContext(), requestCode, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
    }

    private class BasicListAdapter extends RecyclerView.Adapter<BasicListAdapter.ViewHolder> implements ItemTouchHelperClass.ItemTouchHelperAdapter {

        ArrayList<ToDoItem> items;

        public BasicListAdapter(ArrayList<ToDoItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_circle_try, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ToDoItem item = items.get(position);

            SharedPreferences preferences = getActivity().getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE);
            //Background color for each to-do item. Necessary for night/day mode
            int bgColor;
            //color of title text in our to-do item. White for night mode, dark gray for day mode
            int todoTextColor;
            if (preferences.getString(THEME_SAVED, LIGHTTHEME).equals(LIGHTTHEME)) {
                bgColor = Color.WHITE;
                todoTextColor = getResources().getColor(R.color.secondary_text);
            } else {
                bgColor = Color.DKGRAY;
                todoTextColor = Color.WHITE;
            }
            holder.linearLayout.setBackgroundColor(bgColor);

            if (item.ismHasReminder() && item.getmTodoDate() != null) {
                holder.mToDoTextView.setMaxLines(1);
                holder.mTimeTextView.setVisibility(View.VISIBLE);

                holder.mTimeTextView.setText(AddToDoFragment.formatDate("dd MMM yyyy ,hh:mm a",item.getmTodoDate()));
            } else {
                holder.mToDoTextView.setMaxLines(2);
                holder.mTimeTextView.setVisibility(View.GONE);
            }
            holder.mToDoTextView.setText(item.getmTodoText());
            holder.mToDoTextView.setTextColor(todoTextColor);

            TextDrawable myDrawable = TextDrawable.builder().beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .toUpperCase()
                    .endConfig()
                    .buildRound(item.getmTodoText().substring(0, 1), item.getmTodoColor());

            holder.mColorImageView.setImageDrawable(myDrawable);

            /*
              not completed
            *
            * */
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public void onItemMoved(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++)
                    Collections.swap(items, i, i + 1);
            } else {
                for (int i = fromPosition; i > toPosition; i--)
                    Collections.swap(items, i, i - 1);
            }
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemRemoved(int position) {
            ToDoItem deletedTodoItem = items.remove(position);
            Intent i = new Intent(getContext(), ReminderActivity.class);
            deleteAlarm(i, deletedTodoItem.getmTodoIdentifier().hashCode());
            notifyItemRemoved(position);

            Snackbar.make(mCoordLayout, "Deleted TODO", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", v -> {
                        items.add(position, deletedTodoItem);
                        if (deletedTodoItem.getmTodoDate() != null && deletedTodoItem.ismHasReminder()) {
                            Intent i1 = new Intent(getContext(), ReminderActivity.class);
                            createAlarm(i1, deletedTodoItem.getmTodoIdentifier().hashCode(), deletedTodoItem.getmTodoDate().getTime());
                        }
                        notifyDataSetChanged();
                    }).show();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            View mView;
            LinearLayout linearLayout;
            TextView mToDoTextView;
            ImageView mColorImageView;
            TextView mTimeTextView;

            public ViewHolder(@NonNull View v) {
                super(v);
                mView = v;
                v.setOnClickListener(v1 -> {
                    ToDoItem item = items.get(getAdapterPosition());
                    Intent intent = new Intent(getContext(), AddToDoActivity.class);
                    intent.putExtra(TODOITEM, item);
                    launcher.launch(intent);
                });

                linearLayout = v.findViewById(R.id.listItemLinearLayout);
                mToDoTextView = v.findViewById(R.id.toDoListItemTextview);
                mColorImageView = v.findViewById(R.id.toDoListItemColorImageView);
                mTimeTextView = v.findViewById(R.id.todoListItemTimeTextView);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences sp = getActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED,Context.MODE_PRIVATE);
        if(sp.getBoolean(CHANGE_OCCURED,false)){
            mToDoItemsArrayList = getLocallyStoredData(storeRetrieveData);
            adapter = new MainFragment.BasicListAdapter(mToDoItemsArrayList);
            mRecyclerView.setAdapter(adapter);
            setAlarms();

            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(CHANGE_OCCURED,false);
            editor.apply();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            storeRetrieveData.saveToFile(mToDoItemsArrayList);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mRecyclerView.removeOnScrollListener(customRecyclerScrollViewListener);
    }
}