package com.example.igor.restaurantmobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.example.igor.restaurantmobile.GlobalVarialbles.mDeviceID;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mIPConnect;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentIcon;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentName;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentPrice;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mNewBillGuid;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mNewBillTableGuid;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mPortConnect;

public class AssortimentActivity extends AppCompatActivity {
    final Context context = this;
    ListView mListViewShowAssortment;
    SimpleAdapter mAdapterShowAssortment;
    String mIPAdress,mPortNumber,mDeviceNumber, mTableGuid,mGuidBill;
    ArrayList mArrayCommentList;

    ArrayList<HashMap<String, Object>> mArrayAsssortmentList = new ArrayList<>();
    final ArrayList kit_lists = new ArrayList();
    ArrayList<HashMap<String, Object>> kit_list = new ArrayList<>();
    int mIndexClickedItem = 0;
    List<String> mListClickedItems = new ArrayList<>();//List<String> a = new ArrayList<>();
// int j = 0;

    public String cnt;


    JSONObject _ass,finalbil;
    String uid_billsa,asl_name_kit,asl_name,price_asl,guid,Price_uid,folder_asl,A_JSon,Kit_membr;
    JSONArray jsonArray;
    private EditText queryEditText;

    final int REQUEST_CODE_forCount = 3,REQUEST_CODE_PreviewBill=8;
    int h,k,g,numberKit,mas,i,resultIn ,resultOut,x;
    ArrayAdapter<String> adapter;
    AlertDialog.Builder builderkit;
    SimpleAdapter simpleAdapterKIT;

    ProgressDialog pgH;


    @Override
    public boolean onCreateOptionsMenu(Menu menu1) {
        getMenuInflater().inflate(R.menu.asortiment_menu, menu1);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home : {
                int siz = a.size();
                if (siz==0) {
                        resultOut=jsonArray.length();
                    if (resultIn==resultOut){
                        Intent intent2 = new Intent();
                        setResult(RESULT_CANCELED, intent2);
                        finish();
                    }else{
                        AlertDialog.Builder exit = new AlertDialog.Builder(context);
                        exit.setTitle("Documentul nu este salvat!");
                        exit.setMessage("Doriti sa slavati comanda? Daca doriti sa ramineti ,apasati in orice punct a ecranului.");
                        exit.setNegativeButton("Nu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent2 = new Intent();
                                setResult(RESULT_CANCELED, intent2);
                                finish();
                            }
                        });
                        exit.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
                                uid_billsa = (sPref.getString("bills_uid", ""));
                                String uid="00000000-0000-0000-0000-000000000000";
                                Boolean selector = uid_billsa.contains(uid);
                                try {
                                    finalbil.put("deviceId",id_base_tel);
                                    if (selector){
                                        finalbil.put("tableUid",table_uid);
                                    }
                                    finalbil.put("billUid", uid_billsa);
                                    finalbil.put("tableUid", table_uid);
                                    finalbil.put("orders", jsonArray);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                URL generateURLSendBil = generateURLSendBill(ip_, port);
                                new querrySendbill().execute(generateURLSendBil);
                            }
                        });
                        exit.show();
                    }
                }else{
                    x=siz-1;
                    j-=1;
                    guid=a.get(x);
                    asl_list.clear();
                    initASLList();
                    asl_view.setAdapter(simpleAdapterASL);
                    a.remove(x);
                }
            }break;
            case R.id.action_home : {
                queryEditText.clearFocus();
                queryEditText.setText("");
                a.clear();
                j=0;
                guid="00000000-0000-0000-0000-000000000000";
                asl_list.clear();
                initASLList();
                asl_view.setAdapter(simpleAdapterASL);
            }break;
            case R.id.action_search : {
                int siz = a.size();
                String text_search =queryEditText.getText().toString();
                asl_list.clear();
                onSearch(text_search);
                asl_view.setAdapter(simpleAdapterASL);
            }break;
        }
        return super.onOptionsItemSelected(item);
    }
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
                if (!response2.equals("")) {
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
                                final AlertDialog.Builder eroare = new AlertDialog.Builder(context);
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
                                final AlertDialog.Builder eroare = new AlertDialog.Builder(context);
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_assortiment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sales);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent startIntent = getIntent();
        pgH=new ProgressDialog(context);
        queryEditText = toolbar.findViewById(R.id.search_edit_text);
        mListViewShowAssortment = findViewById(R.id.list_aslsale);
        FloatingActionButton mSaveBill = (FloatingActionButton) findViewById(R.id.save_bill);
        FloatingActionButton mFabPreviewBill = (FloatingActionButton) findViewById(R.id.preview_bill);

        mAdapterShowAssortment = new SimpleAdapter(this, mArrayAsssortmentList,R.layout.tesrt, new String[]{mMapAssortmentName,mMapAssortmentIcon,mMapAssortmentPrice}, new int[]{R.id.text_view_asl,R.id.image_view_asl_xm,R.id.text_test2});
        mListViewShowAssortment.setAdapter(mAdapterShowAssortment);

        SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
        SharedPreferences sPrefPre = getSharedPreferences("Bill_previewg", MODE_PRIVATE);

        mIPAdress = (sPref.getString(mIPConnect,""));
        mPortNumber = (sPref.getString(mPortConnect,""));
        mDeviceNumber = (sPref.getString(mDeviceID,""));

        mTableGuid = startIntent.getStringExtra(mNewBillTableGuid);
        mGuidBill = startIntent.getStringExtra(mNewBillGuid);

        initAssortmentList();

        A_JSon = (sPref.getString("JSONObject", ""));
        finalbil= new JSONObject();
        jsonArray = new JSONArray();

        try {
            JSONArray orders = new JSONArray(sPrefPre.getString("orders",""));
            resultIn=orders.length();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        mSaveBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
                uid_billsa =(sPref.getString("bills_uid",""));
                String uid="00000000-0000-0000-0000-000000000000";
                Boolean selector = uid_billsa.contains(uid);
                try {
                    finalbil.put("deviceId",id_base_tel);
                    if (selector){
                        if (!table_uid.equals("")) {
                            finalbil.put("tableUid", table_uid);
                        }
                    }
                    finalbil.put("billUid",uid_billsa);
                    finalbil.put("orders",jsonArray);
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

                pgH.setMessage("Asteptati...");
                pgH.setCancelable(false);
                pgH.setIndeterminate(true);
                pgH.show();
                URL generateURLSendBil = generateURLSendBill(ip_,port);
                new querrySendbill().execute(generateURLSendBil);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        mFabPreviewBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
                uid_billsa =(sPref.getString("bills_uid",""));
                String uid="00000000-0000-0000-0000-000000000000";
                Boolean selector = uid_billsa.contains(uid);
                try {
                    finalbil.put("deviceId",id_base_tel);
                    if (selector){
                        if (!table_uid.equals("")) {
                            finalbil.put("tableUid", table_uid);
                        }
                    }
                    finalbil.put("billUid",uid_billsa);
                    finalbil.put("orders",jsonArray);
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
                SharedPreferences previewBill= getSharedPreferences("Bill_preview",MODE_PRIVATE);
                SharedPreferences.Editor inputBill =previewBill.edit();
                inputBill.putString("CreatedBill",String.valueOf(finalbil));
                inputBill.apply();
                Intent new_bill_activity = new Intent(".PreviewActivityRestaurant");
                startActivityForResult(new_bill_activity, REQUEST_CODE_PreviewBill);
            }
        });
        mListViewShowAssortment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {





                SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                guid=(String)asl_list.get(position).get("Udale");
                folder_asl=(String)asl_list.get(position).get("Folder_is");
                Price_uid=(String) asl_list.get(position).get("Price_line_uid") ;
                price_asl = (String) asl_list.get(position).get("Price") ;
                asl_name  = (String) asl_list.get(position).get("Name");
                String comentar = (String) asl_list.get(position).get("Comentarii");
                String saghi = (String) asl_list.get(position).get("Sag");
                String ParentUid = (String)asl_list.get(position).get("Parent_uid");

                if(folder_asl=="false"){
                    if (comentar!="null"){
                        ed.putString("coments_Assortiment", comentar);
                    }
                    if(saghi!="null"){
                        ed.putString("Sagi", saghi);
                    }else{
                        ed.putString("Sagi", "0");
                    }
                    ed.putString("Guid_Assortiment", guid);
                    ed.apply();
                    Intent count_activity = new Intent(".CountActivityRestaurant");
                    startActivityForResult(count_activity,REQUEST_CODE_forCount);
                    _ass = new JSONObject();
                    try {
                        _ass.put("AssortimentUid", guid);
                        _ass.put("PriceLineUid", Price_uid);
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

                }else {
                    mListClickedItems.add(mIndexClickedItem,ParentUid);
                    mArrayAsssortmentList.clear();
                    initAssortmentList();
                    mIndexClickedItem += 1;
                }
            }
        });//OnItemClickListener
        queryEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String text_search =queryEditText.getText().toString().toLowerCase();
                    asl_list.clear();
                    onSearch(text_search);
                    mListViewShowAssortment.setAdapter(mAdapterShowAssortment);
                    return true;
                }
                return false;
            }

        });
    }//onCreate
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_forCount) {
            if (resultCode == RESULT_OK) {
                SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("coments_Assortiment", "");
                ed.apply();
                cnt = data.getStringExtra("count");
                com_lists = data.getStringArrayListExtra("comentar");
                Kit_membr = (sPref.getString("Sagi", ""));
                if (!Kit_membr.equals("0")) {
                    g = 1;
                    i = 0;
                    initKitMember();
                    onP();
                } else if (Kit_membr.equals("0")) {
                    try {
                        JSONArray comen__ = new JSONArray(com_lists);
                        _ass.put("Count", cnt);
                        _ass.put("Comments", comen__);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArray.put(_ass);
                }
            } else {
                SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("coments_Assortiment", "");
                ed.commit();

            }
        }
        if (requestCode==REQUEST_CODE_PreviewBill){
            if (resultCode==RESULT_CANCELED){
                SharedPreferences previewBill= getSharedPreferences("Bill_preview",MODE_PRIVATE);
                String bill=previewBill.getString("CreatedBill","");
                try {
                    JSONObject orders_editted = new JSONObject(bill);
                    jsonArray=orders_editted.getJSONArray("orders");
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
            }else{
                    Intent intent = new Intent();
                    setResult(RESULT_OK,intent);
                    finish();
            }
        }
    }

    private void initAssortmentList() {
        mArrayAsssortmentList = ((GlobalVarialbles)getApplication()).getAssortmentFromParent("00000000-0000-0000-0000-000000000000");
        mListViewShowAssortment.setAdapter(mAdapterShowAssortment);
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
            send_bill_Connection.setConnectTimeout(6000);
            send_bill_Connection.setRequestMethod("POST");
            send_bill_Connection.setRequestProperty("Content-Type", "application/json");
            send_bill_Connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(send_bill_Connection.getOutputStream());
            wr.writeBytes(String.valueOf(finalbil));
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
            e.printStackTrace();
        } finally {
            send_bill_Connection.disconnect();
        }
        return data;
    }
    private void initKitMember() {
        try {
            JSONObject asl_jsonKIT = new JSONObject(A_JSon);
            JSONArray asl_arrayKIT = asl_jsonKIT.getJSONArray("AssortimentList");
            JSONArray kitM_json =  new JSONArray(Kit_membr);
            mas =kitM_json.length();
            for (; i < g; i++) {  //Array KitMembers
                JSONObject object = kitM_json.getJSONObject(i);
                JSONArray asl_lis =object.getJSONArray("AssortimentList");
                Boolean mandator = object.getBoolean("Mandatory");
                numberKit = object.getInt("StepNumber");
                kit_list.clear();
                kit_lists.clear();
                for (h= 0; h < asl_lis.length(); h++) {      //AsortimentList din KitMembers
                    String asl_guid = asl_lis.getString(h);
                    for (k = 0; k < asl_arrayKIT.length(); k++) {//AsortimentList din ttot Json-ul
                        JSONObject object_asl = asl_arrayKIT.getJSONObject(k);
                        asl_name_kit = object_asl.getString("Name");
                        String uid_asl = object_asl.getString("Uid");
                        boolean paranoid = uid_asl.contains(asl_guid);
                        HashMap<String, Object> asortiment = new HashMap<>();
                        if (paranoid) {
                            asortiment.put("Names", asl_name_kit);
                            asortiment.put("Uid_kit", asl_guid);
                            asortiment.put("Mandatory", mandator);
                            kit_list.add(asortiment);
                            kit_lists.add(asl_name_kit);
                        }
                    } // 3 for
                }//2 for
            } //1 for

        } catch (JSONException e) {
           e.printStackTrace();
        }
    } //initKitMember
    protected void onP() {
        adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, kit_lists);
        simpleAdapterKIT = new SimpleAdapter(this, kit_list,android.R.layout.simple_list_item_1, new String[]{"Name"}, new int[]{android.R.id.text1});
        builderkit = new AlertDialog.Builder(context);
        builderkit.setTitle("Kit step: " + numberKit);
        builderkit.setAdapter(adapter, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int wich) {
                com_lists.add(kit_list.get(wich).get("Uid_kit"));
                if(i<mas) {
                    kit_lists.clear();
                    g += 1;
                    initKitMember();
                    onP();

                }else{
                    try {
                        JSONArray comen__ = new JSONArray(com_lists);
                        _ass.put("Count", cnt);
                        _ass.put("Comments", comen__);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArray.put(_ass);
                }
            }
        });
        builderkit.show();
    }
    private void onSearch(String search_text) {
        try {
            JSONObject asl_json = new JSONObject(A_JSon);
            JSONArray asl_array = asl_json.getJSONArray("AssortimentList");
            for (int l = 0; l < asl_array.length(); l++) {
                JSONObject object = asl_array.getJSONObject(l);
                String asl_name = object.getString("Name");
                String uid_asl = object.getString("Uid");
                Integer price = object.getInt("Price");
                String kit_sag =object.getString("KitMembers");
                Boolean is_folder = object.getBoolean("IsFolder");
                String price_line_uid = object.getString("PricelineUid");
                Boolean paranoid = asl_name.toLowerCase().contains(search_text);
                HashMap<String, Object> asl_ = new HashMap<>();
                String coment = object.getString("Comments");
                if (paranoid) {
                    if (!is_folder){
                        asl_.put("Folder_is",String.valueOf(is_folder));
                        asl_.put("icon",R.drawable.asl901);
                        asl_.put("Name", asl_name);
                        asl_.put("Udale",uid_asl);
                        asl_.put("Sag",kit_sag);
                        asl_.put("Comentarii", coment);
                        asl_.put("Price",String.valueOf(price));
                        asl_.put("Price_line_uid",String.valueOf(price_line_uid));
                        asl_list.add(asl_);
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
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                v.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        int siz = a.size();
        if (siz==0) {
            resultOut=jsonArray.length();
            if (resultIn==resultOut){
                Intent intent2 = new Intent();
                setResult(RESULT_CANCELED, intent2);
                finish();
            }else{
                AlertDialog.Builder exit = new AlertDialog.Builder(context);
                exit.setTitle("Documentul nu este salvat!");
                exit.setMessage("Doriti sa slavati comanda? Daca doriti sa ramineti ,apasati in orice punct a ecranului.");
                exit.setNegativeButton("Nu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent2 = new Intent();
                        setResult(RESULT_CANCELED, intent2);
                        finish();
                    }
                });
                exit.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
                        uid_billsa = (sPref.getString("bills_uid", ""));
                        String uid="00000000-0000-0000-0000-000000000000";
                        Boolean selector = uid_billsa.contains(uid);
                        try {
                            finalbil.put("deviceId",id_base_tel);
                            if (selector){
                                finalbil.put("tableUid",table_uid);
                            }
                            finalbil.put("billUid", uid_billsa);
                            finalbil.put("tableUid", table_uid);
                            finalbil.put("orders", jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        URL generateURLSendBil = generateURLSendBill(ip_, port);
                        new querrySendbill().execute(generateURLSendBil);
                    }
                });
                exit.show();
            }
        }else{
            x=siz-1;
            j-=1;
            guid=a.get(x);
            asl_list.clear();
            initASLList();
            asl_view.setAdapter(simpleAdapterASL);
            a.remove(x);
        }
    }
}