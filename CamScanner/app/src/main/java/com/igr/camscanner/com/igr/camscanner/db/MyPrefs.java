package com.igr.camscanner.com.igr.camscanner.db;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by DEVEN SINGH on 1/22/2015.
 */
public class MyPrefs {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor spEditor;

    private final String DATABASE_NAME="CamScannerPrefs";
    private final String DOC_COUNT="docCount";

    int docCount;

    public MyPrefs(Context context){
        sharedPreferences=context.getSharedPreferences(DATABASE_NAME,Context.MODE_PRIVATE);
    }

    public int getDocCount() {
        return sharedPreferences.getInt(DOC_COUNT,1);
    }

    public void setDocCount(int docCount) {
        spEditor=sharedPreferences.edit();
        spEditor.putInt(DOC_COUNT,docCount);
        spEditor.commit();
    }
}
