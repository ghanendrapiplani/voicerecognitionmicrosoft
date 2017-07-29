package com.voicerecognition;

import android.content.Context;
import android.content.SharedPreferences;

public class UserProfilesNames {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int mode=0;
    String prefname="userprofilenames";

    private String NO_ID_FOUND = "no username found";

    public UserProfilesNames(Context context)
    {
        this._context=context;
        pref = _context.getSharedPreferences(prefname,mode);
        editor = pref.edit();
    }

    public void setUsername(String identificationProfileId,String username)
    {
        editor.putString(identificationProfileId,username);
        editor.commit();
    }

    public String getUsername(String identificationProfileId)
    {
        return pref.getString(identificationProfileId,NO_ID_FOUND);
    }

    public void clearOldSetting()
    {
        editor.clear();
        editor.apply();
    }


}
