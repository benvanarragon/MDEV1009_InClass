package com.example.notificationtest;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressTask extends AsyncTask<Location, Void, String> {

    private final String TAG = FetchAddressTask.class.getSimpleName();
    private Context mContext;
    private OnTaskCompleted mListener;

    interface OnTaskCompleted{
        void onTaskCompleted(String result);
    }

    FetchAddressTask(Context applicationContext, OnTaskCompleted listener){
        mContext = applicationContext;
        mListener = listener;
    }

    @Override
    protected void onPostExecute(String address) {
        mListener.onTaskCompleted(address);
        super.onPostExecute(address);
    }

    @Override
    protected String doInBackground(Location... locations) {

        //Set up our geocoder object
        Geocoder geocoder =
                new Geocoder(mContext, Locale.getDefault());

        Location location = locations[0];

        List<Address> addresses = null;
        String resultMessage = "";

        try{
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    //in our example we only want 1 address returned
                    1);

        }catch(IOException ioException){
            //catch network or other input output errors
            resultMessage = mContext
                    .getString(R.string.service_not_available);
            Log.e(TAG, resultMessage, ioException);
        }catch(IllegalArgumentException illegalArgumentException){
            //catch invalid latitude or longitude value
            resultMessage = mContext
                    .getString(R.string.invalid_lat_long_used);
            Log.e(TAG,
                    resultMessage +". " +
                    "Lat: " + location.getLatitude() +
                    ", Longitude: " + location.getLongitude()
                    ,illegalArgumentException);
        }

        if (addresses == null || addresses.size() == 0) {
            if(resultMessage.isEmpty()){
                resultMessage = mContext.getString(R.string.no_address_found);
                Log.e(TAG, resultMessage);
            }

        }else{
            //if an address is found through the geocoder, read it
            //to the resultMessage variable
            Address address = addresses.get(0);
            ArrayList<String> addressParts = new ArrayList<>();


            //get the address lines using getAddressLine method
            // loop through and join them, send them to the thread

            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++){
                addressParts.add(address.getAddressLine(i));
            }

            resultMessage = TextUtils.join("\n"
                    ,addressParts);
        }


        return resultMessage;
    }
}
