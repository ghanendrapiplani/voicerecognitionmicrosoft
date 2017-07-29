package com.voicerecognition;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

public class UserProfilesId {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int mode=0;
    String prefname="identificationProfileId";

    private String NO_ID_FOUND = "no profile id found";

    public UserProfilesId(Context context)
    {
        this._context=context;
        pref = _context.getSharedPreferences(prefname,mode);
        editor = pref.edit();
    }

    public void setidentificationProfileId(String username,String identificationProfileId)
    {
        editor.putString(username,identificationProfileId);
        editor.commit();
    }

    public String getidentificationProfileId(String username)
    {
        return pref.getString(username,NO_ID_FOUND);
    }

    public void clearOldSetting()
    {
        editor.clear();
        editor.apply();
    }

    public ArrayList<NewProfileModel> getAllProfiles(){
        ArrayList<NewProfileModel> arr = new ArrayList<>();
        Map<String,?> keys = pref.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("map values userprfid",entry.getKey() + ": " + entry.getValue().toString());
            NewProfileModel npm = new NewProfileModel(entry.getKey(),entry.getValue().toString());
            arr.add(npm);
        }
        return arr;
    }



}
