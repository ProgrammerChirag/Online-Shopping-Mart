package MemoryManagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SettingMemoryData {

      Context context;
    private static final String TAG = "SettingMemoryData";

    public SettingMemoryData(Context context)
    {
        this.context = context;
    }

    public void setSharedPrefString(String KEY , String Value)
    {
        boolean result;

        Log.d(TAG, "setSharedPrefString: "+Value);

        SharedPreferences sharedPreferences = context.getSharedPreferences("MemoryData" , Context.MODE_PRIVATE);

        Editor editor = sharedPreferences.edit();

        if(!sharedPreferences.contains(KEY)){
            editor.putString(KEY, Value).apply();
            Log.d("key", String.valueOf(getSharedPrefString(KEY)));

        }else {

            editor.putString(KEY, Value);
            editor.apply();
            Log.d("key", String.valueOf(getSharedPrefString(KEY)));
        }

    }

    public boolean setSharedPrefInteger(String KEY , Integer Value)
    {
        boolean result;

        SharedPreferences sharedPreferences = context.getSharedPreferences("MemoryData" , Context.MODE_PRIVATE);

        Editor editor = sharedPreferences.edit();

        editor.putInt(KEY , Value);
        result = editor.commit();
        return result;
    }

    public String getSharedPrefString(String KEY)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MemoryData" , Context.MODE_PRIVATE);

        return  sharedPreferences.getString(KEY , null);
    }

    public int getSharedPrefInteger(String KEY)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MemoryData" , Context.MODE_PRIVATE);

        return  sharedPreferences.getInt(KEY , -1);
    }

    public void removeSharedPref( )
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MemoryData" , Context.MODE_PRIVATE);

        sharedPreferences.edit().clear().apply();
    }

}
