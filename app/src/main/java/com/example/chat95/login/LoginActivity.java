package com.example.chat95.login;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

import com.example.chat95.utils.MyBroadcastReceiver;
import com.example.chat95.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "life cycle";
    private static NavController navController;
    private BroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //navController = Navigation.findNavController(this, R.id.loginActivityContainer);


        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myBroadcastReceiver, intentFilter);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "inside on Destroy");
        unregisterReceiver(myBroadcastReceiver);
    }

    public static NavController getNavController() {
        return navController;
    }
}
