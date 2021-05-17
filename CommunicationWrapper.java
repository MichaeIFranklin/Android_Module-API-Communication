package org.ucalltel;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Michael Franklin on 5/11/2016.
 */
public class CommunicationWrapper {
    
    private static CommunicationWrapper instance;
    private boolean InternetConnected;

    // singleton calls
    public static final synchronized CommunicationWrapper instance() {
        if (instance == null) {
            instance = new CommunicationWrapper();
        }
        return instance;
    }
    private CommunicationWrapper()
    {

    }

    // returns a pre-populated JSONObject filled with a OAuth Token and a FID
    public JSONObject GetAuthToken(Context context)
    {
        JSONObject Data = new JSONObject();
        try
        {
            Data.put(context.getString(R.string.oauth_key_token), FileIO.instance().GetStoredAuthInfo(context).getString(context.getString(R.string.oauth_key_token)));
            Data.put(context.getString(R.string.oauth_key_firmware_id), Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        }
        catch (Exception e)
        {
            Log.d("Dash.GetAuthToken",e.toString());
        }
        return Data;
    }

    public void SendCommunicationRequest(Context context, final String Packagefor, final String Url, final JSONObject Request)
    {
        SendCommunicationRequest(context, Packagefor,Url,Request,0);
    }

    /*
    call when you wish to send a request to the API or through SMS if that fails
    Param 1: Unique Identifier for the Request (Refer to APIStrings)
    Param 2: Url Endpoint (not included domain) for API Request
    Param 3: JSON Object holding all data to be sent
    */
    public void SendCommunicationRequest(final Context context, final String Packagefor, final String Url, final JSONObject Request, final int APISendType)
    {
        Thread Communicate = new Thread(new Runnable() {
            @Override
            public void run() {
                // Check For Internet
                if (CheckInternet(context))
                {
                    // Send API Request
                    APICommunicationService.startActionSend(context,Packagefor,Url,Request,APISendType);
                }
                else
                {
                   if (Packagefor.equals(context.getString(R.string.oauth_packagefor_login)))
                   {
                       ((Activity)context).runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               // send login failed
                               
                           }
                       });
                   }
                    // Send SMS Request
                    //SMSCommunicationService.startActionSend(context,Request);
                }
            }
        });

        try
        {
            Communicate.start();
        }
        catch (Exception e)
        {
            Log.d("Comunicate",e.toString());
        }
    }

    public boolean CheckServer(final Context context)
    {
        // check network connection
        ConnectivityManager ConMgr = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo NetInfo = ConMgr.getActiveNetworkInfo();
        InternetConnected = (NetInfo != null && NetInfo.isConnected());

        Thread InternetCheck = new Thread(new Runnable() {
            @Override
            public void run() {

                // if we have an interent connection
                if (InternetConnected)
                {
                    // test internet properly using actual server
                    Uri.Builder Builder = new Uri.Builder();
                    if (BuildConfig.DEBUG) {
                        Builder.scheme(context.getString(R.string.oauth_dev_scheme));
                        Builder.authority(context.getString(R.string.oauth_dev_domain));
                    }
                    else
                    {
                        Builder.scheme(context.getString(R.string.oauth_scheme));
                        Builder.authority(context.getString(R.string.oauth_domain));
                    }

                    // add ping endpoint to domain
                    // append URL endpoint
                    String AppendURL = context.getString(R.string.api_doamin_check);
                    while (!AppendURL.equals(""))
                    {
                        if (AppendURL.contains("/"))
                        {
                            String SubUrl = AppendURL.substring(0, AppendURL.indexOf('/'));
                            Builder.appendPath(SubUrl);
                            AppendURL = AppendURL.substring(AppendURL.indexOf('/')+1,AppendURL.length());
                        }
                        else
                        {
                            Builder.appendPath(AppendURL);
                            AppendURL = "";
                        }
                    }

                    HttpURLConnection urlConnection = null;
                    try
                    {
                        URL url = new URL(Builder.build().toString());

                        // open url connection
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setConnectTimeout(10000);
                        urlConnection.setReadTimeout(10000);
                        urlConnection.setInstanceFollowRedirects(true);
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setDoInput(true);
                        urlConnection.connect();
                    }
                    catch (Exception e)
                    {
                        Log.d("Comunicate.ChkInternet", "No Internet: " + e.toString());
                    }

                    try
                    {
                        // get responsde code
                        int ResCode = urlConnection.getResponseCode();

                        // place code in response category
                        if (ResCode >= 200 && ResCode < 300) {
                            // Connection truly successful
                            InternetConnected = true;
                        }
                        else
                        {
                            Log.d("Comunicate.ChkServer","Response Code:" + ResCode);
                            // connection failed
                            InternetConnected = false;
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d("Comunicate.ChkServer",e.toString());

                        // Error getting Response code: connection failed
                        InternetConnected = false;
                    }

                    if (urlConnection != null)
                    {
                        urlConnection.disconnect();
                    }
                }
            }
        });

        try
        {
            InternetCheck.start();
            InternetCheck.join();
        }
        catch (Exception e)
        {
            Log.d("Comunicate.ChkServer",e.toString());
        }


        return InternetConnected;
    }

    public boolean CheckInternet(final Context context)
    {
        // check network connection
        ConnectivityManager ConMgr = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo NetInfo = ConMgr.getActiveNetworkInfo();
        InternetConnected = (NetInfo != null && NetInfo.isConnected());

        Thread InternetCheck = new Thread(new Runnable() {
            @Override
            public void run() {

                // if we have an interent connection
                if (InternetConnected)
                {
                    // test internet properly using actual server
                    Uri.Builder Builder = new Uri.Builder();
                    Builder.scheme(context.getString(R.string.oauth_dev_scheme));
                    Builder.authority("www.purple.com");


                    HttpURLConnection urlConnection = null;
                    try
                    {
                        URL url = new URL(Builder.build().toString());

                        // open url connection
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setConnectTimeout(10000);
                        urlConnection.setReadTimeout(10000);
                        urlConnection.setInstanceFollowRedirects(true);
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setDoInput(true);
                        urlConnection.connect();
                    }
                    catch (Exception e)
                    {
                        Log.d("Comunicate.ChkInternet", "No Internet: " + e.toString());
                    }

                    try
                    {
                        // get responsde code
                        int ResCode = urlConnection.getResponseCode();

                        // place code in response category
                        if (ResCode >= 200 && ResCode < 300) {
                            // Connection truly successful
                            InternetConnected = true;
                        }
                        else
                        {
                            Log.d("Comunicate.ChkInternet","Response Code:" + ResCode);
                            // connection failed
                            InternetConnected = false;
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d("Comunicate.ChkInternet",e.toString());

                        // Error getting Response code: connection failed
                        InternetConnected = false;
                    }

                    if (urlConnection != null)
                    {
                        urlConnection.disconnect();
                    }
                }
            }
        });

        try
        {
            InternetCheck.start();
            InternetCheck.join();
        }
        catch (Exception e)
        {
            Log.d("Comunicate.ChkInternet",e.toString());
        }


        return InternetConnected;
    }
    public boolean CheckNetwork(Context context)
    {
        ConnectivityManager ConMgr = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo NetInfo = ConMgr.getActiveNetworkInfo();
        // check if we are connected to the network
        if (NetInfo != null)
        {
            if (NetInfo.isConnected())
            {
                return true;
            }
        }
        return false;
    }
    public boolean CheckWifi(Context context)
    {
        ConnectivityManager ConMgr = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo NetInfo = ConMgr.getActiveNetworkInfo();
        // check if we are on Wifi
        if (NetInfo.getType()==ConnectivityManager.TYPE_ETHERNET || NetInfo.getType()== ConnectivityManager.TYPE_WIFI)
        {
            return true;
        }
        return false;
    }
}
