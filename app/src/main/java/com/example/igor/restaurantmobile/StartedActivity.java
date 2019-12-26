package com.example.igor.restaurantmobile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.MenuItem;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.example.igor.restaurantmobile.utilis.NetworkUtils.generateURL;
import static com.example.igor.restaurantmobile.utilis.NetworkUtils.getResponseFromURL;

public class StartedActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

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
    TextView txt_version;
    int MESSAGE_SUCCES = 0,MESSAGE_RESULT_CODE = 1,MESSAGE_NULL_BODY = 2 , MESSAGE_FAILURE = 3;
    boolean pingTest = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_started, menu);
        this.mMenu = menu;
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_started);

        Toolbar toolbar = findViewById(R.id.toolbar_started_trattrra);
        setSupportActionBar(toolbar);

        start = findViewById(R.id.btn_start);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txt_version=findViewById(R.id.txt_version_mobile);

        final SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);

        requestMultiplePermissions();
        ip_=(sPref.getString(IP_save,""));
        port=(sPref.getString(Port_save,""));
        mDeviceID = (sPref.getString(Device_save,""));


        sync=new Timer();
        startTimetaskSync();
        sync.schedule(timerTaskSync,100,2000);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                final boolean licenta = sPref.getBoolean("Key",false);
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

        String version ="0.0";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "0-";
        }
        txt_version.setText("RestaurantMobile for Android v"+ version);
    }//OnCreate
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
                ip_=(sPref.getString(IP_save,""));
                port=(sPref.getString(Port_save,""));
                mDeviceID = (sPref.getString(Device_save,""));
                boolean reg_start = ((GlobalVarialbles)getApplication()).getStartWork();
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
                if(mMenu!=null)
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
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION

                },
                12);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 12 && grantResults.length == 5) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(StartedActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
            } else if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(StartedActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            } else if (grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(StartedActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else if (grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(StartedActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else if (grantResults[4] != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(StartedActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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
                switch (errorCode){
                    case 1 : {
                        Snackbar.make(start, "UnknownError", Snackbar.LENGTH_LONG)
                                .show();
                    }break;
                    case 2 : {
                        Snackbar.make(start, "Device "+ mDeviceID+ " Not Registered", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 3 : {
                        Snackbar.make(start, "ShiftIsNotValid", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 4 : {
                        Snackbar.make(start, "BillNotFound", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 5 : {
                        Snackbar.make(start, "ClientNotFound", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 6 : {
                        Snackbar.make(start, "SecurityException", Snackbar.LENGTH_LONG).show();
                    }break;
                }

            }
            else if (msg.what == MESSAGE_NULL_BODY) {
                Snackbar.make(start, "Body is null: "+ msg.obj.toString(), Snackbar.LENGTH_LONG).show();
            }
            else if (msg.what == MESSAGE_FAILURE){
                Snackbar.make(start, "Failure save bill: "+ msg.obj.toString(), Snackbar.LENGTH_LONG).show();

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


}
