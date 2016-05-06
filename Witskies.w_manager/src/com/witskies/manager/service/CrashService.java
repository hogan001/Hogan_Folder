package com.witskies.manager.service;

import com.witskies.manager.activity.CrashActivity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class CrashService extends Service {  
      
    private static CrashService mInstance = null;  
      
    @Override  
    public IBinder onBind(Intent arg0) {  
        return null;  
    }  
  
    @Override  
    public void onCreate() {  
        super.onCreate();  
        mInstance = this;  
    }  
  
    public static CrashService getInstance() {  
        return mInstance;  
    }  
      
    public  void sendError(final String message){  
        Intent intent = new Intent(this, CrashActivity.class);  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        intent.putExtra("msg", message);  
        startActivity(intent);  
        stopSelf();  
    }  
  
}