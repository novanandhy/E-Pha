package com.example.novan.tugasakhir.emergency_activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.novan.tugasakhir.models.Contact;
import com.example.novan.tugasakhir.models.Locations;
import com.example.novan.tugasakhir.models.User;
import com.example.novan.tugasakhir.util.database.DataHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

/**
 * Created by Novan on 25/05/2017.
 */

public class SendMessage implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public SendMessage(){}

    private String TAG;
    Context context;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    LocationManager locationManager;

    private String latitude, longitude, name;
    int count = 0;

    DataHelper dataHelper;
    ArrayList<Locations> locations;
    ArrayList<Contact> contacts;
    ArrayList<User> users;

    RelapseData relapseData;

    public SendMessage(Context context) {
        this.context = context;
        TAG = "TAGapp "+context.getClass().getSimpleName();
    }

    public void sendSMSByManager() {
        dataHelper = new DataHelper(context);
        relapseData = new RelapseData(context);

        locations = new ArrayList<>();
        contacts = new ArrayList<>();
        users = new ArrayList<>();

        users = dataHelper.getUserDetail();

        name = users.get(0).getName();

        //create google api connection
        buildGoogleApiClient();

    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void createLocationRequest() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }

        //create request for update location
        createLocationRequest();

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLastLocation != null){
            latitude = String.valueOf(mLastLocation.getLatitude());
            longitude = String.valueOf(mLastLocation.getLongitude());

            contacts = dataHelper.getAllContacts();
            for (int i = 0; i < contacts.size() ; i++){
                String number_phone = contacts.get(i).getNumber();
                try{
                    delivSMS(latitude, longitude, number_phone);
                    Log.d(TAG,"deliver by last known location");
                }catch (Exception ex){
                    Log.d(TAG,"Failed to deliver SMS");
                    ex.printStackTrace();
                }
            }

        }else {
            locations = dataHelper.getLocation();
            latitude = locations.get(count).getLatitude();
            longitude = locations.get(count).getLongitude();

            contacts = dataHelper.getAllContacts();
            for (int i = 0; i < contacts.size() ; i++){
                String number_phone = contacts.get(i).getNumber();
                try {
                    delivSMS(latitude, longitude,number_phone);
                    Log.d(TAG, "deliver by sqlite location");
                } catch (Exception ex) {
                    Log.d(TAG, "Failed to deliver SMS");
                    ex.printStackTrace();
                }
            }
        }
    }

    private void delivSMS(String latitude, String longitude, String number_phone) {
        String link = "http://google.com/maps/place/"+latitude+","+longitude;
        String message = "Halo, saya "+name+". Saya sedang ada masalah sekarang\nTolong bantu saya di lokasi:\n"+
                link+"\n\n\nvia E-Pha Apps";
        SmsManager smsManager = SmsManager.getDefault();
        try{
            smsManager.sendTextMessage(number_phone,null,message,null,null);
            Log.d(TAG,message);
        }catch (Exception e){
            Log.d(TAG, "cannot send sms");
        }


        relapseData.setRelapse(latitude,longitude);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

}
