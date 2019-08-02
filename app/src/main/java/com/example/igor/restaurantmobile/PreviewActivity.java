package com.example.igor.restaurantmobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class PreviewActivity extends AppCompatActivity {
    final static String LOG_TAG = "myLogs";
    final String IP_save = "IP";
    final String Port_save = "Port";
    final String ID_resp_save = "ID_Mob";
    String ip_,port,id_base_tel,table_uid,A_JSon,bills_uid,BillPreview,Uid_asl;
    ListView list_preview;
    SimpleAdapter simpleAdapterPreview;
    final Context context = this;
    int REQUESTCODE_Edit_Line=9;
    ProgressDialog pgH;
    Integer state;
    Boolean selector;
    class querrySendbill extends AsyncTask<URL, String,String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response2="null";
            try {
                response2 = getResponseFromURLSendB(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response2;
        }
        @Override
        protected void onPostExecute(String response2) {
            Intent intent = new Intent();
            if (response2!="") {
                try {
                    JSONObject response_to_close = new JSONObject(response2);
                    int result = response_to_close.getInt("Result");
                    switch (result){
                        case 0 : {
                            pgH.dismiss();
                            setResult(RESULT_OK, intent);
                            finish();
                        }break;
                        case 2: {
                            pgH.dismiss();
                            final android.app.AlertDialog.Builder eroare = new android.app.AlertDialog.Builder(context);
                            eroare.setTitle("Atentie!");
                            eroare.setMessage("Dispozitivul nu este inregistrat");
                            eroare.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            eroare.show();
                        }break;
                        case 3:{
                            pgH.dismiss();
                            final android.app.AlertDialog.Builder eroare = new android.app.AlertDialog.Builder(context);
                            eroare.setTitle("Atentie!");
                            eroare.setMessage("Tura nu este valabila!");
                            eroare.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            eroare.show();
                        }break;
                        default:{
                            pgH.dismiss();
                            Toast.makeText(context, "Eroare: " + result, Toast.LENGTH_SHORT).show();
                        }break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                pgH.dismiss();
                Toast.makeText(context, "Nu este raspuns de la server.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    class querryGetBill extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response_getsbills = "null";
            try {
                response_getsbills = getResponseFromGetBill(urls[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response_getsbills;
        }

        @Override
        protected void onPostExecute(String response) {
            if (!response.equals("")) {
                CurentBill();
                SavedBill(response);
                list_preview.setAdapter(simpleAdapterPreview);
                simpleAdapterPreview.notifyDataSetChanged();
                pgH.dismiss();
            }else {
                pgH.dismiss();
                Intent intent2 = new Intent();
                setResult(RESULT_CANCELED, intent2);
                finish();
                Toast.makeText(context, "Eroare! Incercati inca o data.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_preview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_preview);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Uid_asl = "0";
        list_preview=findViewById(R.id.list_preview);

        final SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
        A_JSon = sPref.getString("JSONObject", "");
        bills_uid = sPref.getString("bills_uid", "");
        ip_=sPref.getString(IP_save,"");
        port=(sPref.getString(Port_save,""));
        id_base_tel = sPref.getString(ID_resp_save,"");
        table_uid = sPref.getString("TableUid","");

        simpleAdapterPreview = new SimpleAdapter(this, preview_list,R.layout.preview_adapter, new String[]{"icon","Name","Count","Prices"}, new int[]{R.id.image_pre_asl,R.id.text_view_asl_prew,R.id.text_count_prew,R.id.text_price_prew});
        list_preview.setAdapter(simpleAdapterPreview);
        String uid="00000000-0000-0000-0000-000000000000";
        selector = bills_uid.contains(uid);
            if (selector){//if new bill
                CurentBill();
                list_preview.setAdapter(simpleAdapterPreview);
            }else{//if bill edited
                pgH=new ProgressDialog(context);
                pgH.setMessage("Asteptati...");
                pgH.setCancelable(false);
                pgH.setIndeterminate(true);
                pgH.show();
                URL generatedURLGet_BilL = generateURLGetBill(ip_, port, id_base_tel, bills_uid);
                new querryGetBill().execute(generatedURLGet_BilL);

            }
        FloatingActionButton fab_save = (FloatingActionButton) findViewById(R.id.fab);
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pgH=new ProgressDialog(context);
                pgH.setMessage("Asteptati...");
                pgH.setCancelable(false);
                pgH.setIndeterminate(true);
                pgH.show();
                URL generateURLSendBil = generateURLSendBill(ip_,port);
                new querrySendbill().execute(generateURLSendBil);
            }
        });

        list_preview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                 state =(Integer)preview_list.get(position).get("State");
                if (state==1){//is exist bill
                    list_preview.setItemChecked(position, false);
                    list_preview.setSelected(false);
                    Uid_asl = "0";
                }else if (state==0){//if added new
                    list_preview.setItemChecked(position, true);
                    list_preview.setSelected(true);
                    Uid_asl=(String) preview_list.get(position).get("Uid");
                    String countLine =(String) preview_list.get(position).get("Count");
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString("Guid_Assortiment", Uid_asl);
                    ed.putString("CountLine",countLine);
                    ed.apply();

                }
            }
        });
    }//OnCreate
    ArrayList<HashMap<String, Object>> preview_list = new ArrayList<>();

        public URL generateURLGetBill (String ip, String port, String id ,String bill_uids){
            Uri getUri;
            getUri =Uri.parse("http://" + ip + ":" + port + "/MobileCash/json/GetBill?deviceId=" + id+"&billUid=" +bill_uids)
                    .buildUpon()
                    .build();
            URL url_bill = null;
            try {
                url_bill =new URL (getUri.toString());
            } catch (MalformedURLException e) {
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
            return url_bill;

        }
        public String getResponseFromGetBill (URL url_bill) {
            String data = "";
            HttpURLConnection get_bill_Connection=null;
            try {
                get_bill_Connection =(HttpURLConnection) url_bill.openConnection();
                get_bill_Connection.setRequestMethod("GET");
                get_bill_Connection.setConnectTimeout(5000);
                InputStream in = get_bill_Connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                int inputStreamData = inputStreamReader.read();

                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }

            } catch (Exception e) {
                final AlertDialog.Builder eroare = new AlertDialog.Builder(context);
                eroare.setTitle("Atentie!");
                eroare.setMessage("Eroare! Mesajul erorii:"+ "\n"+ e);
                eroare.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                eroare.show();
            } finally {
                get_bill_Connection.disconnect();
            }
            return data;

        }
    public URL generateURLSendBill (String ip,String port){
        Uri send_b;
        send_b = Uri.parse("http://" + ip + ":" + port + "/MobileCash/json/AddOrders")
                .buildUpon()
                .build();
        URL send_b_url =null;
        try {
            send_b_url= new URL (send_b.toString());
        } catch (MalformedURLException e) {
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
        return send_b_url;

    }
    public String getResponseFromURLSendB(URL send_bills) throws IOException {
        String data = "";
        HttpURLConnection send_bill_Connection = null;
        try {
            send_bill_Connection = (HttpURLConnection) send_bills.openConnection();
            send_bill_Connection.setRequestMethod("POST");
            send_bill_Connection.setConnectTimeout(8000);
            send_bill_Connection.setDoOutput(true);
            //add reuqest header
            send_bill_Connection.setRequestMethod("POST");
            send_bill_Connection.setRequestProperty("Content-Type", "application/json");
            send_bill_Connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(send_bill_Connection.getOutputStream());
            SharedPreferences preview = getSharedPreferences("Bill_preview",MODE_PRIVATE);
            String finalbil= preview.getString("CreatedBill","");
            wr.writeBytes(finalbil);
            wr.flush();
            wr.close();

            InputStream in = send_bill_Connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            int inputStreamData = inputStreamReader.read();

            while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                inputStreamData = inputStreamReader.read();
                data += current;
            }

        } catch (Exception e) {
            final AlertDialog.Builder eroare = new AlertDialog.Builder(context);
            eroare.setTitle("Atentie!");
            eroare.setMessage("Eroare! Mesajul erorii:"+ "\n"+ e);
            eroare.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            eroare.show();
        } finally {
            send_bill_Connection.disconnect();
        }
        return data;
    }
        private void CurentBill(){
            SharedPreferences asl_u = getSharedPreferences("Assortiment",MODE_PRIVATE);
            SharedPreferences sPrew = getSharedPreferences("Bill_preview",MODE_PRIVATE);
            SharedPreferences asl_price = getSharedPreferences("Assortiment_price",MODE_PRIVATE);
            BillPreview= sPrew.getString("CreatedBill","");
            try {
                JSONObject bill= new JSONObject(BillPreview);
                JSONArray orders = bill.getJSONArray("orders");
                for (int l = 0; l < orders.length(); l++) {
                    JSONObject object = orders.getJSONObject(l);
                    String uid_asortiment = object.getString("AssortimentUid");
                    String nameASL= asl_u.getString(uid_asortiment,"");
                    String Price = asl_price.getString(uid_asortiment,"");
                    Double countASL = object.getDouble("Count");
                    Double countPrice = countASL * Double.valueOf(Price);
                    HashMap<String, Object> bill_lines = new HashMap<>();
                    bill_lines.put("State",0);
                    bill_lines.put("icon",R.drawable.add_circle_black_48dp);
                    bill_lines.put("Name",nameASL);
                    bill_lines.put("Count",String.format("%.2f",countASL));
                    bill_lines.put("Prices",String.format("%.2f",countPrice));
                    bill_lines.put("Uid",uid_asortiment);
                    preview_list.add(bill_lines);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        private void SavedBill(String response){
        SharedPreferences asl_u = getSharedPreferences("Assortiment",MODE_PRIVATE);
        try {
            JSONObject bill_content_json = new JSONObject(response);
            JSONArray billis= bill_content_json.getJSONArray("BillsList");
            JSONObject cont= billis.getJSONObject(0);
            JSONArray bill_array = cont.getJSONArray("Lines");
            for (int l = 0; l < bill_array.length(); l++) {
                JSONObject object = bill_array.getJSONObject(l);
                String uid_asortiment = object.getString("AssortimentUid");
                String nameASL= asl_u.getString(uid_asortiment,"");
                String Price = object.getString("Sum");
                Double price = Double.valueOf(Price);
                Double countASL = object.getDouble("Count");
                HashMap<String, Object> bill_lines = new HashMap<>();
                bill_lines.put("State",1);
                bill_lines.put("icon",R.drawable.check_circle_black_48dp);
                bill_lines.put("Name",nameASL);
                bill_lines.put("Count",String.format("%.2f",countASL));
                bill_lines.put("Prices",String.format("%.2f",price));
                bill_lines.put("Uid",uid_asortiment);
                preview_list.add(bill_lines);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
        public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home : {
                            Intent intent2 = new Intent();
                            setResult(RESULT_CANCELED, intent2);
                            finish();
            }break;
            case R.id.action_edit_line:{
                if (Uid_asl!="0") {
                    Intent count_activity = new Intent(".EditLineActivityRestaurant");
                    startActivityForResult(count_activity,REQUESTCODE_Edit_Line);
                }else {
                    Toast.makeText(this,"Alegeti pozitia!",Toast.LENGTH_SHORT).show();
                }
            }break;
            case R.id.action_delete : {
                if (!Uid_asl.equals("0")) {
                    if (selector) {
                        SharedPreferences preview = getSharedPreferences("Bill_preview", MODE_PRIVATE);
                        JSONObject created = null;
                        A_JSon = (preview.getString("CreatedBill", ""));
                        try {
                            created = new JSONObject(A_JSon);
                            JSONArray orders = created.getJSONArray("orders");
                            for (int i = 0; i < orders.length(); i++) {
                                JSONObject object = orders.getJSONObject(i);
                                String uid_prev = object.getString("AssortimentUid");
                                Boolean concait = uid_prev.contains(Uid_asl);
                                if (concait) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        orders.remove(i);
                                    }
                                }
                            }
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
                        SharedPreferences.Editor inputBill = preview.edit();
                        inputBill.putString("CreatedBill", String.valueOf(created));
                        inputBill.apply();
                        preview_list.clear();
                        CurentBill();
                        list_preview.setAdapter(simpleAdapterPreview);
                        Uid_asl = "0";
                    } else {
                        SharedPreferences preview = getSharedPreferences("Bill_preview", MODE_PRIVATE);
                        JSONObject created = null;
                        A_JSon = (preview.getString("CreatedBill", ""));
                        try {
                            created = new JSONObject(A_JSon);
                            JSONArray orders = created.getJSONArray("orders");
                            for (int i = 0; i < orders.length(); i++) {
                                JSONObject object = orders.getJSONObject(i);
                                String uid_prev = object.getString("AssortimentUid");
                                Boolean concait = uid_prev.contains(Uid_asl);
                                if (concait) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        orders.remove(i);
                                    }
                                }
                            }
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
                        SharedPreferences.Editor inputBill = preview.edit();
                        inputBill.putString("CreatedBill", String.valueOf(created));
                        inputBill.apply();
                        pgH=new ProgressDialog(context);
                        pgH.setMessage("Asteptati...");
                        pgH.setCancelable(false);
                        pgH.setIndeterminate(true);
                        pgH.show();
                        preview_list.clear();
                        URL generatedURLGet_BilL = generateURLGetBill(ip_, port, id_base_tel, bills_uid);
                        new querryGetBill().execute(generatedURLGet_BilL);
                        Uid_asl = "0";
                    }
                }
                else {
                    Toast.makeText(this,"Alegeti pozitia!",Toast.LENGTH_SHORT).show();
                }
            }break;

        }
        return super.onOptionsItemSelected(item);
    }
        public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_preview, menu);
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_Edit_Line) {
            if (resultCode == RESULT_OK) {
                preview_list.clear();
                pgH=new ProgressDialog(context);
                pgH.setMessage("Asteptati...");
                pgH.setIndeterminate(true);
                pgH.setCancelable(false);
                pgH.show();
                URL generatedURLGet_BilL = generateURLGetBill(ip_, port, id_base_tel, bills_uid);
                new querryGetBill().execute(generatedURLGet_BilL);
                list_preview.setAdapter(simpleAdapterPreview);
                Uid_asl = "0";
            }
        }
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
}
