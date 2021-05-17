package telvidainc.taxprogram;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class APIResponseReceiver extends BroadcastReceiver {
    public static final String RESPONSE = "Package.MichaelFranklin.Library.APIResponseReceiver.response";
    public static final String BROADCAST = "MichaelFranklin.Library.APIResponseReceiver.broadcast";

    public APIResponseReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            try
            {
                JSONObject Response = new JSONObject(bundle.getString(APICommunicationService.RESPONSE));
                String PackageFor = Response.getString("packageFor");
                APIResponse(context, Response, PackageFor);
            }
            catch (Exception e)
            {
                Log.d("APIReceiver.onAPIRecv", e.toString());
            }

        }
    }

    private void APIResponse(Context context, JSONObject Response, String PackageFor)
            throws Exception
    {
        // check Response Category for OK message from API
        if (Response.getString("Response Category").equals(context.getString(R.string.api_response_ok)))
        {
            // Determine data type of Data String
            Object Data = new JSONTokener(Response.getString("data")).nextValue();

            // call method based on PackageFor
            if (PackageFor.equals("Revalidate"))
            {
                if ((Data instanceof JSONObject))
                {
                    RevalidateResponse(context, (JSONObject) Data);
                }
                else
                {
                    throw new Exception("Revalidate: Expected JSONObject - Got: " + Data.getClass().getCanonicalName());
                }
            }
            else if (PackageFor.equals(context.getString(R.string.oauth_packagefor_login)))
            {
                if ((Data instanceof JSONObject))
                {
                    LoginResponse(context, (JSONObject) Data);
                }
                else
                {
                    // report login failed

                    throw new Exception(context.getString(R.string.oauth_packagefor_login) + ": Expected JSONObject - Got: " + Data.getClass().getCanonicalName());
                }
            }
            else
            {
                throw new Exception("API errored: No Valid PackageFor Label " + PackageFor + " on Response");
            }
        }
        else
        {
            Log.d("APIReceiver.ResNotOK","Response Category: " + Response.getString("Response Category") +"\nResponse Message: " + Response.getString("Response Message"));
            // Broadcast error back to requester

            // check the type of request
            if (PackageFor.equals(context.getString(R.string.oauth_packagefor_login)))
            {
                // login
                // report login failed

            }
            else
            {
                throw new Exception("API errored: No Valid PackageFor Label " + PackageFor + " on Error Handling");
            }
        }
    }

    private void RevalidateResponse(Context context, JSONObject Response)
            throws Exception
    {
        // save credentials
        JSONObject data = new JSONObject();

        // save JWT
        String Token = Response.getString(context.getString(R.string.oauth_key_token));
        data.put(context.getString(R.string.oauth_key_token), Token);
        FileIO.instance().SetStoredAuthInfo(context, data);

        // resend Last Resquest with new Token
        data = new JSONObject(Response.get(context.getString(R.string.api_failed_data)).toString());
        data.put(context.getString(R.string.oauth_key_token), Token);
        APICommunicationService.startActionSend(context,Response.getString(context.getString(R.string.api_failed_packagefor)),Response.getString(context.getString(R.string.api_failed_url)),data);
    }

    private void LoginResponse(Context context, JSONObject Response)
            throws Exception
    {
        if (Response.has("error"))
        {
            if (Response.getString("error").equals("invalid_request"))
            {

            }
        }
        else
        {
            // get sid
            String Token = Response.getString(context.getString(R.string.oauth_key_token));

            // send loggedin
            
        }
    }

}
