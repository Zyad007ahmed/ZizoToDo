package com.zizo.zizotodo.Utility;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class StoreRetrieveData {
    private Context context;
    private String fileName;

    public StoreRetrieveData(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    public static JSONArray toJSONArray(ArrayList<ToDoItem> items) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (ToDoItem item : items) {
            jsonArray.put(item.toJSON());
        }
        return jsonArray;
    }

    public void saveToFile(ArrayList<ToDoItem> items) throws IOException, JSONException {
        FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        outputStreamWriter.write(toJSONArray(items).toString());
        outputStreamWriter.close();
        fileOutputStream.close();
    }

    public ArrayList<ToDoItem> loadFromFile() throws IOException, JSONException {
        ArrayList<ToDoItem> items = new ArrayList<>();
        BufferedReader bufferedReader = null;
        FileInputStream fileInputStream = null;
        try{
            fileInputStream = context.openFileInput(fileName);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null){
                builder.append(line);
            }
            JSONArray jsonArray = (JSONArray) new JSONTokener(builder.toString()).nextValue();
            for(int i =0;i<jsonArray.length();i++){
                ToDoItem item = new ToDoItem(jsonArray.getJSONObject(i));
                items.add(item);
            }
        }finally {
            if(bufferedReader != null)
                bufferedReader.close();
            if(fileInputStream != null)
                fileInputStream.close();
        }

        return items;
    }
}
