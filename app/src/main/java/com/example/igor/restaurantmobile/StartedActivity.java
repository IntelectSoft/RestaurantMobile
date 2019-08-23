package com.example.igor.restaurantmobile;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.igor.restaurantmobile.AssortimentList.Assortiment;
import com.example.igor.restaurantmobile.AssortimentList.AssortmentService;
import com.example.igor.restaurantmobile.AssortimentList.ClosureType;
import com.example.igor.restaurantmobile.AssortimentList.Comments;
import com.example.igor.restaurantmobile.AssortimentList.ServiceGetAssortmentList;
import com.example.igor.restaurantmobile.AssortimentList.Table;

import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.example.igor.restaurantmobile.utilis.NetworkUtils.generateURL;
import static com.example.igor.restaurantmobile.utilis.NetworkUtils.generateURLASL;
import static com.example.igor.restaurantmobile.utilis.NetworkUtils.getResponseFromURL;
import static com.example.igor.restaurantmobile.utilis.NetworkUtils.getResponseFromURLASL;

public class StartedActivity extends AppCompatActivity {
    final int REQUEST_CODE_Settings = 4;
    Button start;
    final Context context = this;
    final String IP_save = "IP";
    final String Port_save = "Port";
    final String Device_save = "ID_Device";
    String ip_,port,mDeviceID;

    ProgressBar progressBar;
    TimerTask timerTaskSync;
    Timer sync;
    private Menu mMenu;

    int MESSAGE_SUCCES = 0,MESSAGE_RESULT_CODE = 1,MESSAGE_NULL_BODY = 2 , MESSAGE_FAILURE = 3;
    boolean pingTest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_started);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_started);
        setSupportActionBar(toolbar);
        start=findViewById(R.id.btn_start);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        final SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);

        requestMultiplePermissions();
        ip_=(sPref.getString(IP_save,""));
        port=(sPref.getString(Port_save,""));
        mDeviceID = (sPref.getString(Device_save,""));
        final boolean licenta=sPref.getBoolean("Key",false);

        sync=new Timer();
        startTimetaskSync();
        sync.schedule(timerTaskSync,100,2000);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                if(licenta) {{
                    if (pingTest){
                        getAssortment(ip_,port,mDeviceID);
                    }
                }
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(StartedActivity.this, "Licenta nu este valida!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }//OnCreate
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_started, menu);
        this.mMenu = menu;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_setting_start :{
                Intent setting_activity = new Intent(".SettingsActivity");
                startActivityForResult(setting_activity,REQUEST_CODE_Settings);
            }break;
            case R.id.action_exit_start : {
                finish();
            }break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE_Settings) {
            if (resultCode == RESULT_OK) {
                SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
                boolean reg_start = sPref.getBoolean("Start",false);
                if (reg_start) {
                    Intent start_mainactivity = new Intent(".MainActivityRestaurant");
                    startActivity(start_mainactivity);
                    finish();
                }
            }

        }
    }

    class querryPing extends AsyncTask<URL, String, Boolean> {

        @Override
        protected Boolean doInBackground(URL... urls) {
            String response = "false";
            try {
                response = getResponseFromURL(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Boolean.valueOf(response);
        }

        @Override
        protected void onPostExecute(Boolean response) {
            if (response) {
                pingTest=true;
                mMenu.getItem(0).setIcon(ContextCompat.getDrawable(StartedActivity.this, R.drawable.signal_wi_fi_48));
            }else {
                pingTest=false;
                if(mMenu!=null)
                    mMenu.getItem(0).setIcon(ContextCompat.getDrawable(StartedActivity.this, R.drawable.no_signal_wi_fi_48));
            }
        }
    }
    public void requestMultiplePermissions() {
        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_PHONE_STATE

                },
                12);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 12 && grantResults.length == 2) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(StartedActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
            } else if(grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(StartedActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void getAssortment(final String ipAdress, final String portNumber, final String deviceID){
        Thread mGetAssortmentService = new Thread(new Runnable() {
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(3, TimeUnit.MINUTES)
                        .readTimeout(4, TimeUnit.MINUTES)
                        .writeTimeout(2, TimeUnit.MINUTES)
                        .build();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://" + ipAdress + ":" + portNumber)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okHttpClient)
                        .build();
                ServiceGetAssortmentList serviceGetAssortmentList = retrofit.create(ServiceGetAssortmentList.class);
                final Call<AssortmentService> assortmentServiceCall = serviceGetAssortmentList.getAssortmentList(deviceID);

                assortmentServiceCall.enqueue(new Callback<AssortmentService>() {
                    @Override
                    public void onResponse(Call<AssortmentService> call, Response<AssortmentService> response) {
                        AssortmentService assortmentService = response.body();
                        if(assortmentService!=null){
                            int mErrorCode = assortmentService.getResult();
                            if(mErrorCode == 0){
                                List<Assortiment> assortimentLists = assortmentService.getAssortimentList();
                                List<Table> tableLists = assortmentService.getTableList();
                                List<ClosureType> closureTypeLists = assortmentService.getClosureTypeList();
                                List<Comments> commentsLists = assortmentService.getCommentsList();

                                ((GlobalVarialbles)getApplication()).setAssortmentList(assortimentLists);
                                ((GlobalVarialbles)getApplication()).setTableList(tableLists);
                                ((GlobalVarialbles)getApplication()).setClosureTypeLists(closureTypeLists);
                                ((GlobalVarialbles)getApplication()).setCommentsLists(commentsLists);
                                mHandlerBills.obtainMessage(MESSAGE_SUCCES).sendToTarget();
                            }
                            else{
                                mHandlerBills.obtainMessage(MESSAGE_RESULT_CODE,mErrorCode).sendToTarget();
                            }

                        }
                        else{
                            mHandlerBills.obtainMessage(MESSAGE_NULL_BODY).sendToTarget();
                        }
                    }
                    @Override
                    public void onFailure(Call<AssortmentService> call, Throwable t) {
                        mHandlerBills.obtainMessage(MESSAGE_FAILURE,t.getMessage()).sendToTarget();
                    }
                });
            }
        });
        mGetAssortmentService.start();
    }
    private final Handler mHandlerBills = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.INVISIBLE);
            if (msg.what == MESSAGE_SUCCES) {
                Intent startmainactivity = new Intent(".MainActivityRestaurant");
                startActivity(startmainactivity);
                finish();
            }
            else if (msg.what == MESSAGE_RESULT_CODE) {
                int errorCode = Integer.valueOf(msg.obj.toString());
                if (errorCode == 2) {
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("Dispozitivul "+ mDeviceID+ " nu este inregistrat!");
                    dialog.setMessage("Doriti sa deschideti setarile?");
                    dialog.setNegativeButton("Nu", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    dialog.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent setting_activity = new Intent(".SettingsActivity");
                            startActivityForResult(setting_activity,REQUEST_CODE_Settings);
                        }
                    });
                    dialog.show();
                }

            }
            else if (msg.what == MESSAGE_NULL_BODY) {
                Toast.makeText(StartedActivity.this, "body is null", Toast.LENGTH_SHORT).show();
            }
            else if (msg.what == MESSAGE_FAILURE){
                String masjFailure = msg.obj.toString();
                Toast.makeText(StartedActivity.this, masjFailure, Toast.LENGTH_SHORT).show();

            }
        }
    };

    private void startTimetaskSync(){
        timerTaskSync = new TimerTask() {
            @Override
            public void run() {
                StartedActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
                        URL generatedURL = generateURL(sPref.getString("IP",""), sPref.getString("Port",""));
                        new querryPing().execute(generatedURL);
                    }
                });
            }
        };
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View mDecorView = getWindow().getDecorView();
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }
}
