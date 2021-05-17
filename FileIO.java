package com.telvida.cruxmobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Michael Franklin on 1/13/2016.
 */
public class FileIO {

    private static FileIO instance;

    public static final synchronized FileIO instance() {
        if (instance == null) {
            instance = new FileIO();
        }
        return instance;
    }
    public FileIO() {

    }

    public JSONArray GetJSONArrayFromJSONObjectsFile(Context context,String FileName)
    {
        JSONArray data = new JSONArray();


        try {
            InputStream inputStream = context.openFileInput(FileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                    data.put(new JSONObject(receiveString));
                }

                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("FileIO", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("FileIO", "Can not read file: " + e.toString());
        }
        catch (Exception e) {
            Log.e("FileIO", e.toString());
        }

        return data;
    }

    public JSONArray ReadInJSONArrayFromFile(Context context,String FileName)
    {

        String ret = "";
        JSONArray Data = null;
        try {
            InputStream inputStream = context.openFileInput(FileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();

                Data = new JSONArray(ret);
            }
        }
        catch (FileNotFoundException e) {
            Log.e("FileIO", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("FileIO", "Can not read file: " + e.toString());
        }
        catch (Exception e) {
            Log.e("FileIO", e.toString());
        }

        return Data;
    }
    public JSONObject ReadInJSONObjectFromFile(Context context,String FileName)
    {

        String ret = "";
        JSONObject Data = null;
        try {
            InputStream inputStream = context.openFileInput(FileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();

                Data = new JSONObject(ret);
            }
        }
        catch (FileNotFoundException e) {
            Log.e("FileIO", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("FileIO", "Can not read file: " + e.toString());
        }
        catch (Exception e) {
            Log.e("FileIO", e.toString());
        }

        return Data;
    }
    public void AppendJSONToFile(Context context, Object Data, String FileName)
    {
        try
        {
            // write data to file
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(FileName, Context.MODE_PRIVATE | Context.MODE_APPEND));
            outputStreamWriter.write(Data.toString());
            outputStreamWriter.close();
        } catch (Exception e)
        {
            Log.d("FileIO.UpdateLayouts", e.toString());
        }
    }
    public void JSONToFile(Context context, Object Data, String FileName)
    {
        try
        {
            // write data to file
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(FileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(Data.toString());
            outputStreamWriter.close();
        }
        catch (Exception e)
        {
            Log.d("FileIO.UpdateLayouts", e.toString());
        }

    }

    public JSONObject GetStoredAccInfo(Context context)
    {
        JSONObject Data = null;

        // Get Info
        SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.pref_api_accinfo_file), Context.MODE_PRIVATE);

        // Check for AccInfo on file
        if (sharedPrefs.contains(context.getString(R.string.pref_api_nonsipnumber)))
        {
            // we have AccInfo on file. Get it
            String nonsipnumber = sharedPrefs.getString(context.getString(R.string.pref_api_nonsipnumber), "NONE");
            String displayName = sharedPrefs.getString(context.getString(R.string.pref_api_displayname), "NONE");
            String domain = sharedPrefs.getString(context.getString(R.string.pref_api_sip_domain), "NONE");
            String sipusername = sharedPrefs.getString(context.getString(R.string.pref_api_sip_username), "NONE");
            String credit = sharedPrefs.getString(context.getString(R.string.pref_api_acc_credit), "N/A");
            String sipPassword = sharedPrefs.getString(context.getString(R.string.pref_api_sip_password), "NONE");
            String fallbackid = sharedPrefs.getString(context.getString(R.string.pref_api_fallback_id), "NONE");

            // Populate JSONObject
            Data = new JSONObject();
            try
            {
                Data.put(context.getString(R.string.pref_api_sip_password),sipPassword);
                Data.put(context.getString(R.string.pref_api_nonsipnumber),nonsipnumber);
                Data.put(context.getString(R.string.pref_api_fallback_id),fallbackid);
                Data.put(context.getString(R.string.pref_api_sip_domain),domain);
                Data.put(context.getString(R.string.pref_api_acc_credit),credit);
                Data.put(context.getString(R.string.pref_api_sip_username),sipusername);
                if (displayName.equals("NONE"))
                {
                    displayName = "User";
                }
                Data.put(context.getString(R.string.pref_api_displayname),displayName);
            }
            catch (Exception e)
            {
                Log.d("FileIO.GetAccInfo",e.toString());
            }
        }
        return Data;
    }
    public void SetStoredAccInfo(Context context, JSONObject Data)
    {
        // Store Data
        // get AccInfo File
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.pref_api_accinfo_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor PrefEditor = sharedPref.edit();

        // Store Data into AccInfo
        try
        {
            if (Data.has(context.getString(R.string.pref_api_nonsipnumber))) {
                PrefEditor.putString(context.getString(R.string.pref_api_nonsipnumber), Data.getString(context.getString(R.string.pref_api_nonsipnumber)));
            }
            if (Data.has(context.getString(R.string.pref_api_fallback_id))) {
                PrefEditor.putString(context.getString(R.string.pref_api_fallback_id), Data.getString(context.getString(R.string.pref_api_fallback_id)));
            }
            if (Data.has(context.getString(R.string.pref_api_sip_username))) {
                PrefEditor.putString(context.getString(R.string.pref_api_sip_username), Data.getString(context.getString(R.string.pref_api_sip_username)));
            }
            if (Data.has(context.getString(R.string.pref_api_sip_password))) {
                PrefEditor.putString(context.getString(R.string.pref_api_sip_password), Data.getString(context.getString(R.string.pref_api_sip_password)));
            }
            if (Data.has(context.getString(R.string.pref_api_sip_domain))) {
                PrefEditor.putString(context.getString(R.string.pref_api_sip_domain), Data.getString(context.getString(R.string.pref_api_sip_domain)));
            }
            if (Data.has(context.getString(R.string.pref_api_displayname))) {
                PrefEditor.putString(context.getString(R.string.pref_api_displayname), Data.getString(context.getString(R.string.pref_api_displayname)));
            }
            if (Data.has(context.getString(R.string.pref_api_acc_credit))) {
                PrefEditor.putString(context.getString(R.string.pref_api_acc_credit), Data.getString(context.getString(R.string.pref_api_acc_credit)));
            }
            PrefEditor.commit();
        }
        catch (Exception e)
        {
            Log.d("FileIO.SetAccInfo",e.toString());
        }
    }


    public boolean AuthInfoStored(Context context)
    {
        SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.oauth_file_authinfo), Context.MODE_PRIVATE);
        return sharedPrefs.contains(context.getString(R.string.oauth_key_username));
    }

    public JSONObject GetStoredAuthInfo(Context context)
    {
        JSONObject Data = new JSONObject();

        // Get Info
        SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.oauth_file_authinfo), Context.MODE_PRIVATE);

        // Check for AuthInfo on file
        if (sharedPrefs.contains(context.getString(R.string.oauth_key_username)))
        {
            // we have Auth on file. Get it
            String email = sharedPrefs.getString(context.getString(R.string.oauth_key_username), "NONE");
            String CCode = sharedPrefs.getString("CCode", "NONE");
            String password = sharedPrefs.getString(context.getString(R.string.oauth_key_password), "NONE");
            String token = sharedPrefs.getString(context.getString(R.string.oauth_key_token),"NONE");
            int LoggedIn = sharedPrefs.getInt(context.getString(R.string.oauth_key_logged_in), -1);

            // Populate JSONObject
            Data = new JSONObject();
            try
            {
                Data.put(context.getString(R.string.oauth_key_username),email);
                Data.put("CCode",CCode);
                Data.put(context.getString(R.string.oauth_key_password),password);
                Data.put(context.getString(R.string.oauth_key_token),token);
                Data.put(context.getString(R.string.oauth_key_logged_in),LoggedIn);
            }
            catch (Exception e)
            {
                Log.d("FileIO.GetAuthInfo",e.toString());
            }
        }
        return Data;
    }
     public void SetStoredAuthInfo(Context context, JSONObject Data)
    {
        // Store Data
        // get AuthInfo File
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.oauth_file_authinfo), Context.MODE_PRIVATE);
        SharedPreferences.Editor PrefEditor = sharedPref.edit();

        // Store Data into AuthInfo
        try
        {
            if (Data.has(context.getString(R.string.oauth_key_username))) {
                PrefEditor.putString(context.getString(R.string.oauth_key_username), Data.getString(context.getString(R.string.oauth_key_username)));
            }
            if (Data.has("CCode")) {
                PrefEditor.putString("CCode", Data.getString("CCode"));
            }
            if (Data.has(context.getString(R.string.oauth_key_password))) {
                PrefEditor.putString(context.getString(R.string.oauth_key_password), Data.getString(context.getString(R.string.oauth_key_password)));
            }
            if (Data.has(context.getString(R.string.oauth_key_token))) {
                PrefEditor.putString(context.getString(R.string.oauth_key_token), Data.getString(context.getString(R.string.oauth_key_token)));
            }
            if (Data.has(context.getString(R.string.oauth_key_logged_in))) {
                PrefEditor.putInt(context.getString(R.string.oauth_key_logged_in), Data.getInt(context.getString(R.string.oauth_key_logged_in)));
            }
            PrefEditor.commit();
        }
        catch (Exception e)
        {
            Log.d("FileIO.SetAuthInfo",e.toString());
        }
    }


    public boolean GetLoggedIn(Context context)
    {
        // Get Info
        SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.pref_api_accinfo_file), Context.MODE_PRIVATE);
        return sharedPrefs.getBoolean(context.getString(R.string.oauth_key_logged_in),false);
    }
    public void SetLoggedIn(Context context, boolean LoggedIn)
    {
        if (!LoggedIn)
        {

        }
        // Store Data
        // get AuthInfo File
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.pref_api_accinfo_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor PrefEditor = sharedPref.edit();
        PrefEditor.putBoolean(context.getString(R.string.oauth_key_logged_in), LoggedIn);
        PrefEditor.commit();
    }

    public void ClearStoredAuthInfo(Context context)
    {
        SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.oauth_file_authinfo), Context.MODE_PRIVATE);
        SharedPreferences.Editor PrefsEditor = sharedPrefs.edit();
        PrefsEditor.remove(context.getString(R.string.oauth_key_username));
        PrefsEditor.remove(context.getString(R.string.oauth_key_password));
        PrefsEditor.remove(context.getString(R.string.oauth_key_token));
        PrefsEditor.remove(context.getString(R.string.oauth_key_logged_in));
        PrefsEditor.commit();

    }
}
