package com.example.igor.restaurantmobile;

import android.Manifest;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    final Context context = this;
    final int REQUEST_CODE_NewBill = 1;
    final int REQUEST_CODE_Setng = 4;
    final int REQUEST_CODE_EDBill = 5;
    final String IP_save = "IP";
    final String Port_save = "Port";
    final String ID_resp_save = "ID_Mob";
    String ip_,port,id_base_tel,A_JSon,B_Json,Bill_list,table_name,uid_table,closeUid;
    int Number,Number_closed;
    String uid_table_in_bill,sum_bill,name_tabl,uid_billa,uid_bill_to_close,uid_billchecked=null,Sum_closed;
    GridView list_bills_;
    SimpleAdapter simpleAdapterASL,simpleAdapterShow,simpleAdapterType;
    SharedPreferences sPref;
    final static String LOG_TAG = "myLogs";
    ArrayAdapter<String> adapter;
    AlertDialog.Builder builderType;
    int countH=0;
    int defoult,result_forSetting;
    ListView listContent;
    ProgressDialog pgH;
    FloatingActionMenu menu_bill;

    class querryGetBill extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response_getbill = "null";
            try {
                response_getbill = getResponseFromGetBill(urls[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response_getbill;
        }

        @Override
        protected void onPostExecute(String response_getbill) {
            if (!response_getbill.equals("")) {
                pgH.dismiss();
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("bills_uid", "00000000-0000-0000-0000-000000000000");
                ed.apply();
                Bill_list = response_getbill;
                bills_list.clear();
                setBills();
            }else {
                pgH.dismiss();
                Toast.makeText(context, "Eroare! Finisat dupa timeout.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    class querryCloseBill extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response_closebill = "null";
            try {
                response_closebill = getResponseFromCloseBill(urls[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response_closebill;
        }

        @Override
        protected void onPostExecute(String response) {
            if(!response.equals("null")) {
                try {
                    JSONObject response_to_close = new JSONObject(response);
                    int result = response_to_close.getInt("Result");
                    String resultMessage = response_to_close.getString("ResultMessage");
                    if (result == 0) {
                        pgH.dismiss();
                        Toast.makeText(context, "Contul a fost inchis!", Toast.LENGTH_SHORT).show();
                        bills_list.clear();
                        URL generatedURLGet_Bill = generateURLGetBill(ip_, port, id_base_tel);
                        new querryGetBill().execute(generatedURLGet_Bill);
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("bills_uid", "00000000-0000-0000-0000-000000000000");
                        ed.apply();
                        bill_lines.clear();
                        listContent.setAdapter(simpleAdapterShow);
                    } else {
                        pgH.dismiss();
                        Toast.makeText(context, resultMessage, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                pgH.dismiss();
                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
            }
        }
    }
    class querryGetBillLines extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response_getsbills = "null";
            try {
                response_getsbills = getResponseFromGetsBills(urls[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            B_Json=response_getsbills;
            return response_getsbills;
        }

        @Override
        protected void onPostExecute(String response) {
            bill_lines.clear();
            ShowLines(response);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                bill_lines.sort(new Comparator<HashMap<String, Object>>() {
                    @Override
                    public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
                        return o1.get("Name").toString().compareTo(o2.get("Name").toString());
                    }
                });
            }
            listContent.setAdapter(simpleAdapterShow);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sPref = getSharedPreferences("Save setting", MODE_PRIVATE);

        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("bills_uid","00000000-0000-0000-0000-000000000000");
        ed.apply();

        list_bills_ = findViewById(R.id.list_bill);
        listContent=findViewById(R.id.contentBillList);
        FloatingActionButton fab_show_line = findViewById(R.id.item_show_line);
        FloatingActionButton fab_close = findViewById(R.id.item_close);


        ip_=(sPref.getString(IP_save,""));
        port=(sPref.getString(Port_save,""));
        id_base_tel = (sPref.getString(ID_resp_save,""));

        pgH=new ProgressDialog(context);
        pgH.setMessage("Asteptati...");
        pgH.setIndeterminate(true);
        pgH.setCancelable(false);
        pgH.show();
        URL generatedURLGet_Bill = generateURLGetBill(ip_,port,id_base_tel);
        new querryGetBill().execute(generatedURLGet_Bill);

        final LinearLayout mainLayout = (LinearLayout)findViewById(R.id.LGridView);
        final LinearLayout ShowLayout = (LinearLayout)findViewById(R.id.ShowLine);
        final LinearLayout.LayoutParams gridviewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final LinearLayout.LayoutParams LineLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        simpleAdapterShow = new SimpleAdapter(this, bill_lines,R.layout.bill_show, new String[]{"Name","Count"}, new int[]{R.id.textName,R.id.textCount});
        Display display = getWindowManager().getDefaultDisplay();
        final int height = display.getHeight();
        final SharedPreferences checkTable = getSharedPreferences("Tables",MODE_PRIVATE);

        int heightnew1 = height / 3 ;
        final int heightGrid = (heightnew1*2)-100;
        final int heightShow = height - heightGrid;
        defoult= height;
        gridviewLayout.height=heightGrid;
        LineLayout.height=heightShow+20;
        mainLayout.setLayoutParams(gridviewLayout);
        ShowLayout.setLayoutParams(LineLayout);
        countH=1;
        if (uid_billchecked!= null) {
            URL generatedURLGets_Bills = generateURLGetBillLines(ip_, port, id_base_tel, uid_billchecked);
            new querryGetBillLines().execute(generatedURLGets_Bills);
        }
        fab_show_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(countH==0){
                    defoult= height;
                    gridviewLayout.height=heightGrid;
                    LineLayout.height=heightShow+20;
                    mainLayout.setLayoutParams(gridviewLayout);
                    ShowLayout.setLayoutParams(LineLayout);
                    countH=1;
                    if (uid_billchecked!= null) {
                        URL generatedURLGets_Bills = generateURLGetBillLines(ip_, port, id_base_tel, uid_billchecked);
                        new querryGetBillLines().execute(generatedURLGets_Bills);
                    }
                }else if (countH==1) {
                    gridviewLayout.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    mainLayout.setLayoutParams(gridviewLayout);
                    LineLayout.height = 0;
                    ShowLayout.setLayoutParams(LineLayout);
                    countH = 0;
                }
                menu_bill.close(true);
            }
        });


        menu_bill = findViewById(R.id.fab1);
        FloatingActionButton fab_add = findViewById(R.id.item_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Integer NrTable=checkTable.getInt("CountTable",0);
                if (NrTable!=0) {
                    sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString("TableUid","");
                    ed.putString("bills_uid", "00000000-0000-0000-0000-000000000000");
                    ed.apply();
                    Intent new_bill_activity = new Intent(".TableActivityRestaurant");
                    startActivityForResult(new_bill_activity, REQUEST_CODE_NewBill);
                    menu_bill.close(true);
                }else{
                    sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString("bills_uid", "00000000-0000-0000-0000-000000000000");
                    ed.putString("TableUid","");
                    ed.apply();
                    Intent new_bill_activity = new Intent(".AssortimentActivityRestaurant");
                    startActivityForResult(new_bill_activity, REQUEST_CODE_EDBill);
                    menu_bill.close(true);
                }
            }
        });

        fab_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uid_bill_to_close =(sPref.getString("bills_uid",""));
                String uid="00000000-0000-0000-0000-000000000000";
                Boolean selector = uid_bill_to_close.contains(uid);
                if (selector){
                    Toast.makeText(context,"Alegeti contul!",Toast.LENGTH_SHORT).show();
                }else {
                    close_bill();
                    onCloseType();
                }
                menu_bill.close(true);
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }
        simpleAdapterASL = new SimpleAdapter(this, bills_list,R.layout.lis_bills, new String[]{"Name","Number"}, new int[]{R.id.text1,R.id.text2});


        list_bills_.setOnItemClickListener (new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                list_bills_.setItemChecked(position, true);
                list_bills_.setSelected(true);
                uid_billchecked  = (String) bills_list.get(position).get("Uid") ;
                Number_closed = (Integer)bills_list.get(position).get("Number");
                Sum_closed = (String)bills_list.get(position).get("Sum");
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("bills_uid",uid_billchecked);
                ed.apply();
                if (countH==1){
                    URL generatedURLGets_Bills = generateURLGetBillLines(ip_, port, id_base_tel, uid_billchecked);
                    new querryGetBillLines().execute(generatedURLGets_Bills);
                }
            }
        });//list_bils click listener

        list_bills_.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String NameTable = (String)bills_list.get(i).get("Name");
                String Suma = (String)bills_list.get(i).get("Suma");
                int number = (Integer)bills_list.get(i).get("Number");
                String sum = (String)bills_list.get(i).get("Sum");
                AlertDialog.Builder detail_bill = new AlertDialog.Builder(context);
                detail_bill.setTitle("Detalii cont: " +number );
                detail_bill.setMessage("Nr. contului: " + number +"\nMasa: "+NameTable +"\nSuma: "+Suma+ "\nSuma cu reducere: " + sum );
                detail_bill.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                detail_bill.show();
                return false;
            }
        });
    }//OnCreate
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings :{
                Intent setting_activity = new Intent(".TabSetActivity");
                startActivityForResult(setting_activity,REQUEST_CODE_Setng);
            }break;
            case R.id.action_refresh: {
                pgH=new ProgressDialog(context);
                pgH.setMessage("Asteptati...");
                pgH.setIndeterminate(true);
                pgH.setCancelable(false);
                pgH.show();
                bill_lines.clear();
                listContent.setAdapter(simpleAdapterShow);
                URL generatedURLGet_Bill = generateURLGetBill(ip_,port,id_base_tel);
                new querryGetBill().execute(generatedURLGet_Bill);
            }break;
            case R.id.action_edit : {
                String uid_bila2 =(sPref.getString("bills_uid",""));
                String uid1="00000000-0000-0000-0000-000000000000";
                Boolean selector = uid_bila2.contains(uid1);
                if (selector){
                    Toast.makeText(context,"Alegeti contul!",Toast.LENGTH_SHORT).show();
                }else {
                    Intent new_bill_activity = new Intent(".AssortimentActivityRestaurant");
                    startActivityForResult(new_bill_activity, REQUEST_CODE_EDBill);
                }
            }break;
            case R.id.action_exit :{
                finish();
            }break;
        }
        return super.onOptionsItemSelected(item);
    }
    final ArrayList closure_lists = new ArrayList();
    ArrayList<HashMap<String, Object>> closure_list = new ArrayList<>();
    ArrayList<HashMap<String, Object>> bills_list = new ArrayList<>();
    ArrayList<HashMap<String, Object>> bill_lines = new ArrayList<>();
    public  URL generateURLGetBill (String ip, String port, String id){
        Uri getUri;
        getUri =Uri.parse("http://" + ip + ":" + port + "/MobileCash/json/GetBillsList?deviceId=" + id+"&includeLines=false")
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
            get_bill_Connection.setConnectTimeout(8000);
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
    private void reload_billlist(){
        SharedPreferences tables = getSharedPreferences("Tables",MODE_PRIVATE);
        try {
            JSONObject bill_list_json = new JSONObject(Bill_list);
            JSONArray bill_array = bill_list_json.getJSONArray("BillsList");
            for (int i = 0; i < bill_array.length(); i++) {
                JSONObject object = bill_array.getJSONObject(i);
                Number = object.getInt("Number");
                uid_table_in_bill=object.getString("TableUid");
                name_tabl=tables.getString(uid_table_in_bill,"");
                uid_billa = object.getString("Uid");
                sum_bill = object.getString("SumAfterDiscount");
                String sum = object.getString("Sum");
                HashMap<String, Object> bill_ = new HashMap<>();
                if(name_tabl.equals("")){
                    name_tabl="--";
                }
                bill_.put("Number",Number);
                bill_.put("Name",name_tabl);
                bill_.put("Uid",uid_billa);
                bill_.put("Sum",sum_bill);
                bill_.put("Suma",sum);
                bills_list.add(bill_);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void setBills(){
        reload_billlist();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bills_list.sort(new Comparator<HashMap<String, Object>>() {
                @Override
                public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
                    return o2.get("Number").toString().compareTo(o1.get("Number").toString());
                }
            });
        }
        list_bills_.setAdapter(simpleAdapterASL);
    }
    private void close_bill(){
        String closure_name;
        A_JSon = (sPref.getString("JSONObject", ""));
        try {
            JSONObject A_json = new JSONObject(A_JSon);
            JSONArray closure_array = A_json.getJSONArray("ClosureTypeList");
            for (int i = 0; i < closure_array.length(); i++) {
                JSONObject object = closure_array.getJSONObject(i);
                closure_name = object.getString("Name");
                closeUid = object.getString("Uid");
                HashMap<String,Object> type = new HashMap<>();
                type.put("NameC",closure_name);
                type.put("UidC",closeUid);
                closure_list.add(type);
                closure_lists.add(closure_name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    protected void onCloseType() {
        adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_singlechoice, closure_lists);
        simpleAdapterType = new SimpleAdapter(this, closure_list,android.R.layout.simple_list_item_1, new String[]{"NameС"}, new int[]{android.R.id.text1});
        builderType = new AlertDialog.Builder(context);
        builderType.setTitle("Inchiderea contului: "+Number_closed+ "\nSuma contului: " + Sum_closed );
        builderType.setNegativeButton("Renunt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                closure_list.clear();
                closure_lists.clear();
                dialogInterface.dismiss();
            }
        });
        builderType.setAdapter(adapter, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int wich) {
                pgH=new ProgressDialog(context);
                pgH.setMessage("Asteptati...");
                pgH.setIndeterminate(true);
                pgH.setCancelable(false);
                pgH.show();
                String type_guid= String.valueOf(closure_list.get(wich).get("UidC"));
                URL generatedURLClose_Bill = generateURLCloseBill(ip_,port,id_base_tel,uid_bill_to_close,type_guid);
                new querryCloseBill().execute(generatedURLClose_Bill);
                  closure_list.clear();
                  closure_lists.clear();
              }
        });
        builderType.setCancelable(false);
        builderType.show();


    }//onCloseType
    private void ShowLines(String bill_response){
        SharedPreferences asl_u = getSharedPreferences("Assortiment",MODE_PRIVATE);
        try {
            JSONObject bill_content_json = new JSONObject(bill_response);
            JSONArray billis= bill_content_json.getJSONArray("BillsList");
            JSONObject cont= billis.getJSONObject(0);
            JSONArray bill_array = cont.getJSONArray("Lines");
            for (int l = 0; l < bill_array.length(); l++) {
                JSONObject object = bill_array.getJSONObject(l);
                String uid_asortiment = object.getString("AssortimentUid");
                String nameASL= asl_u.getString(uid_asortiment,"");
                Double countASL = object.getDouble("Count");
                HashMap<String, Object> bill_l = new HashMap<>();
                String Count = String.valueOf(countASL) + " ";
                bill_l.put("Name",nameASL);
                bill_l.put("Count",Count);
                bill_lines.add(bill_l);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
}
    public URL generateURLCloseBill (String ip, String port, String id ,String bill_uid,String TypeUid){
        Uri getUri;
        getUri =Uri.parse("http://" + ip + ":" + port + "/MobileCash/json/CloseBill?deviceId=" + id+"&billUid=" +bill_uid+"&closeTypeUid="+TypeUid)
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
    public String getResponseFromCloseBill (URL url_bill) {
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
    public URL generateURLGetBillLines(String ip, String port, String id , String bill_uids){
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
    public String getResponseFromGetsBills (URL url_bill) {
        String data = "";
        HttpURLConnection get_bill_Connection=null;
        try {
            get_bill_Connection =(HttpURLConnection) url_bill.openConnection();
            get_bill_Connection.setRequestMethod("GET");
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==REQUEST_CODE_NewBill) {
            if (resultCode == RESULT_OK) {
                ip_=(sPref.getString(IP_save,""));
                port=(sPref.getString(Port_save,""));
                id_base_tel = (sPref.getString(ID_resp_save,""));
                pgH=new ProgressDialog(context);
                pgH.setMessage("Asteptati...");
                pgH.setIndeterminate(true);
                pgH.setCancelable(false);
                pgH.show();
                bill_lines.clear();
                listContent.setAdapter(simpleAdapterShow);
                URL generatedURLGet_Bill = generateURLGetBill(ip_,port,id_base_tel);
                new querryGetBill().execute(generatedURLGet_Bill);
            }else if (resultCode == RESULT_CANCELED){
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("bills_uid",uid_billchecked);
                ed.apply();
            }
        }
        if (requestCode==REQUEST_CODE_Setng) {
            if (resultCode == RESULT_OK) {
                ip_=(sPref.getString(IP_save,""));
                port=(sPref.getString(Port_save,""));
                id_base_tel = (sPref.getString(ID_resp_save,""));
                bills_list.clear();
                pgH=new ProgressDialog(context);
                pgH.setMessage("Asteptati...");
                pgH.setIndeterminate(true);
                pgH.setCancelable(false);
                pgH.show();
                URL generatedURLGet_Bill = generateURLGetBill(ip_, port, id_base_tel);
                new querryGetBill().execute(generatedURLGet_Bill);
            }
        }
        if (requestCode==REQUEST_CODE_EDBill) {
            if (resultCode == RESULT_OK) {
                bill_lines.clear();
                listContent.setAdapter(simpleAdapterShow);
                ip_=(sPref.getString(IP_save,""));
                port=(sPref.getString(Port_save,""));
                id_base_tel = (sPref.getString(ID_resp_save,""));
                pgH=new ProgressDialog(context);
                pgH.setMessage("Asteptati...");
                pgH.setIndeterminate(true);
                pgH.setCancelable(false);
                pgH.show();
                URL generatedURLGet_Bill = generateURLGetBill(ip_,port,id_base_tel);
                new querryGetBill().execute(generatedURLGet_Bill);
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
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                v.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        }
        menu_bill.close(true);
        return super.dispatchTouchEvent(event);
    }
}
