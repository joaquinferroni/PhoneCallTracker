package com.example.joaco.phonecalltracker;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class PhoneCallService extends Service {
    PhoneCallReceiver receiver = null;
    public PhoneCallService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BootTest : ", "\non create service");
        Receiver();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("BootTest: ", "\nWill be created again....");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void Receiver() {
        receiver = new PhoneCallReceiver();
    }
}
