package com.zizo.zizotodo.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class ToDoItem implements Serializable {
    private String mTodoText;
    private boolean mHasReminder;
    private String mTodoDescription;
    private int mTodoColor;
    private Date mTodoDate;
    private UUID mTodoIdentifier;

    private static final String TODODESCRIPTION = "tododescription";
    private static final String TODOTEXT = "todotext";
    private static final String TODOREMINDER = "todoreminder";
    private static final String TODOCOLOR = "todocolor";
    private static final String TODODATE = "tododate";
    private static final String TODOIDENTIFIER = "todoidentifier";

    public ToDoItem(String mTodoText, String mTodoDescription, boolean mHasReminder, Date mTodoDate) {
        this.mTodoText = mTodoText;
        this.mHasReminder = mHasReminder;
        this.mTodoDescription = mTodoDescription;
        this.mTodoDate = mTodoDate;
        this.mTodoColor = 1677725;
        this.mTodoIdentifier = UUID.randomUUID();
    }

    public ToDoItem(JSONObject jsonObject) throws JSONException{
        mTodoText = jsonObject.getString(TODOTEXT);
        mTodoColor = jsonObject.getInt(TODOCOLOR);
        mTodoDescription = jsonObject.getString(TODODESCRIPTION);
        mHasReminder = jsonObject.getBoolean(TODOREMINDER);
        mTodoIdentifier = UUID.fromString(jsonObject.getString(TODOIDENTIFIER));

        if(jsonObject.has(TODODATE)){
            mTodoDate = new Date(jsonObject.getLong(TODODATE));
        }
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(TODOTEXT,mTodoText);
        jsonObject.put(TODOREMINDER,mHasReminder);
        jsonObject.put(TODODESCRIPTION,mTodoDescription);
        jsonObject.put(TODOCOLOR,mTodoColor);
        jsonObject.put(TODOIDENTIFIER,mTodoIdentifier.toString());
        if(mTodoDate != null)
            jsonObject.put(TODODATE,mTodoDate.getTime());

        return jsonObject;
    }

    public String getmTodoText() {
        return mTodoText;
    }

    public void setmTodoText(String mTodoText) {
        this.mTodoText = mTodoText;
    }

    public boolean ismHasReminder() {
        return mHasReminder;
    }

    public void setmHasReminder(boolean mHasReminder) {
        this.mHasReminder = mHasReminder;
    }

    public String getmTodoDescription() {
        return mTodoDescription;
    }

    public void setmTodoDescription(String mTodoDescription) {
        this.mTodoDescription = mTodoDescription;
    }

    public int getmTodoColor() {
        return mTodoColor;
    }

    public void setmTodoColor(int mTodoColor) {
        this.mTodoColor = mTodoColor;
    }

    public Date getmTodoDate() {
        return mTodoDate;
    }

    public void setmTodoDate(Date mTodoDate) {
        this.mTodoDate = mTodoDate;
    }

    public UUID getmTodoIdentifier() {
        return mTodoIdentifier;
    }
}
