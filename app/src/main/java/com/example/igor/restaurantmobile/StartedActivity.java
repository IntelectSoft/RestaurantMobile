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
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import static com.example.igor.restaurantmobile.utilis.NetworkUtils.generateURL;
import static com.example.igor.restaurantmobile.utilis.NetworkUtils.generateURLASL;
import static com.example.igor.restaurantmobile.utilis.NetworkUtils.getResponseFromURL;
import static com.example.igor.restaurantmobile.utilis.NetworkUtils.getResponseFromURLASL;

public class StartedActivity extends AppCompatActivity {
int REQUEST_CODE_Setng_Start=10;
    final int REQUEST_CODE_Setng = 4;
    Button start;
    final Context context = this;
    final String IP_save = "IP";
    final String Port_save = "Port";
    final String ID_resp_save = "ID_Mob";
    String ip_,port,id_base_tel,asl_JSon;

    ProgressBar progressBar;
    HashMap<String,String> assortiment;

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
                URL generatedURLASL = generateURLASL(ip_,port,id_base_tel);
                new queryASL().execute(generatedURLASL);
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(StartedActivity.this, "Nu este legatura cu serviciul!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    class queryASL extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            String response2 = null;
            try {
                response2 = getResponseFromURLASL(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response2;
        }

        @Override
        protected void onPostExecute(String response2) {
            try {
                JSONObject response= new JSONObject(response2);
                Integer result = response.getInt("Result");
                if (result==0) {
                    SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString("JSONObject",response2);
                    ed.putBoolean("Start",true);
                    ed.apply();
                    ASL_aslPrice();
                    TableList_to_bill();
                    Intent startmainactivity = new Intent(".MainActivityRestaurant");
                    startActivity(startmainactivity);
                    finish();
                }else{
                    SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putBoolean("Start",false);
                    ed.apply();
                    final AlertDialog.Builder exit = new AlertDialog.Builder(context);
                    exit.setTitle("Dispozitivul "+id_base_tel+ " nu este inregistrat!");
                    exit.setMessage("Doriti sa deschideti setarile?");
                    exit.setNegativeButton("Nu", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    exit.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent setting_activity = new Intent(".TabSetActivity");
                            startActivityForResult(setting_activity,REQUEST_CODE_Setng);
                        }
                    });
                    exit.show();
                }
                progressBar.setVisibility(View.INVISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 12 && grantResults.length == 2) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(StartedActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
            } else if(grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(StartedActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ip_=(sPref.getString(IP_save,""));
                port=(sPref.getString(Port_save,""));
                id_base_tel = (sPref.getString(ID_resp_save,""));
                boolean licenta=sPref.getBoolean("Key",false);
                if(licenta) {
                    progressBar.setVisibility(View.VISIBLE);
                    URL generatedURL = generateURL(ip_, port);
                    new querryPing().execute(generatedURL);
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
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_setting_start :{
                Intent setting_activity = new Intent(".TabSetActivity");
                startActivityForResult(setting_activity,REQUEST_CODE_Setng);
            }break;
            case R.id.action_exit_start : {
                finish();
            }break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View mDecorView = getWindow().getDecorView();
            mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE_Setng) {
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
    private void ASL_aslPrice() {
        SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
        asl_JSon= sPref.getString("JSONObject","");
        SharedPreferences asl_u = getSharedPreferences("Assortiment",MODE_PRIVATE);
        SharedPreferences.Editor incom = asl_u.edit();
        SharedPreferences asl_price = getSharedPreferences("Assortiment_price",MODE_PRIVATE);
        SharedPreferences.Editor incom_price = asl_price.edit();
        SharedPreferences asl_sales = getSharedPreferences("Assortiment_Sales",MODE_PRIVATE);
        SharedPreferences.Editor incom_sales = asl_sales.edit();
        try {
            JSONObject asl_json = new JSONObject(asl_JSon);
            JSONArray asl_array = asl_json.getJSONArray("AssortimentList");

            for (int i = 0; i < asl_array.length(); i++) {
                JSONObject object = asl_array.getJSONObject(i);
                Boolean is_folder = object.getBoolean("IsFolder");
                assortiment = new HashMap<>();
                if (!is_folder){
                    String uids= object.getString("Uid");
                    String names = object.getString("Name");
                    String prices= object.getString("Price");
                    Boolean alow_integer =object.getBoolean("AllowNonIntegerSale");
                    incom_sales.putBoolean(uids,alow_integer);
                    incom.putString(uids,names);
                    incom_price.putString(uids,prices);
                }
            }
            incom.apply();
            incom_price.apply();
            incom_sales.apply();
        } catch (JSONException e) {
            final AlertDialog.Builder eroare = new AlertDialog.Builder(context);
            eroare.setTitle("Atentie!");
            eroare.setMessage("Eroare! Mesajul erorii:"+ "\n"+ e);
            eroare.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            eroare.show();
        }

    } //initASLList
    private void TableList_to_bill() {
        String tables_name;
        SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
        asl_JSon= sPref.getString("JSONObject","");
        SharedPreferences tables = getSharedPreferences("Tables",MODE_PRIVATE);
        SharedPreferences.Editor tabl= tables.edit();
        try {
            JSONObject table_json = new JSONObject(asl_JSon);
            JSONArray tables_array = table_json.getJSONArray("TableList");
            tabl.putInt("CountTable",tables_array.length());
            for (int i = 0; i < tables_array.length(); i++) {
                JSONObject object = tables_array.getJSONObject(i);
                tables_name = object.getString("Name");
                String uid_table = object.getString("Uid");
                tabl.putString(uid_table,tables_name);
            }
            tabl.apply();
        } catch (JSONException e) {
            final AlertDialog.Builder eroare = new AlertDialog.Builder(context);
            eroare.setTitle("Atentie!");
            eroare.setMessage("Eroare! Mesajul erorii:"+ "\n"+ e);
            eroare.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            eroare.show();
        }
    } //TablesList
}
