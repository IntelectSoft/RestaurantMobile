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

import com.example.igor.restaurantmobile.AssortimentList.Comments;
import com.example.igor.restaurantmobile.CreateNewBill.NewBill;
import com.example.igor.restaurantmobile.CreateNewBill.Order;
import com.example.igor.restaurantmobile.CreateNewBill.OrderParcelable;

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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.example.igor.restaurantmobile.GlobalVarialbles.mDeviceID;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mIPConnect;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentGuid;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentIcon;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentIsFolder;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentName;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentParenGuid;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentPrice;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mNewBillGuid;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mNewBillTableGuid;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mPortConnect;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mSaveOrderIntent;

public class AssortimentActivity extends AppCompatActivity {
    final Context context = this;
    ListView mListViewShowAssortment;
    SimpleAdapter mAdapterShowAssortment;
    String mIPAdress,mPortNumber,mDeviceNumber, mTableGuid,mGuidBillClicked,mGuidBillIntent;
    boolean mAssortmentIsFolder;

    ArrayList<HashMap<String, Object>> mArrayAsssortmentList = new ArrayList<>();

    int mIndexClickedItem = 0;
    List<String> mListClickedItems = new ArrayList<>();


    JSONObject finalbil;
    JSONArray jsonArray;
    private EditText queryEditText;

    final int REQUEST_CODE_forCount = 3,REQUEST_CODE_PreviewBill=8;
    int resultIn ,resultOut,x;

    ProgressDialog pgH;
    NewBill mCreateEditBill = new NewBill();
    List<Order> orderListCreateEditBill = new List<Order>() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<Order> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] ts) {
            return null;
        }

        @Override
        public boolean add(Order order) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends Order> collection) {
            return false;
        }

        @Override
        public boolean addAll(int i, @NonNull Collection<? extends Order> collection) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public Order get(int i) {
            return null;
        }

        @Override
        public Order set(int i, Order order) {
            return null;
        }

        @Override
        public void add(int i, Order order) {

        }

        @Override
        public Order remove(int i) {
            return null;
        }

        @Override
        public int indexOf(Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(Object o) {
            return 0;
        }

        @NonNull
        @Override
        public ListIterator<Order> listIterator() {
            return null;
        }

        @NonNull
        @Override
        public ListIterator<Order> listIterator(int i) {
            return null;
        }

        @NonNull
        @Override
        public List<Order> subList(int i, int i1) {
            return null;
        }
    };

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
                if (mListClickedItems.size() == 0) {
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
                                try {
                                    finalbil.put("deviceId",mDeviceNumber);
                                    finalbil.put("billUid", mGuidBillIntent);
                                    finalbil.put("tableUid", mTableGuid);
                                    finalbil.put("orders", jsonArray);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                URL generateURLSendBil = generateURLSendBill(mIPAdress, mPortNumber);
                                new querrySendbill().execute(generateURLSendBil);
                            }
                        });
                        exit.show();
                    }
                }else{
                    x = mListClickedItems.size() - 1;
                    mIndexClickedItem -= 1;
                    mGuidBillClicked = mListClickedItems.get(x);
                    initAssortmentList(mGuidBillClicked);
                    mListClickedItems.remove(x);
                }
            }break;
            case R.id.action_home : {
                queryEditText.clearFocus();
                queryEditText.setText("");
                mListClickedItems.clear();
                mIndexClickedItem = 0;
                mGuidBillClicked = "00000000-0000-0000-0000-000000000000";
                initAssortmentList(mGuidBillClicked);
            }break;
            case R.id.action_search : {
                String text_search =queryEditText.getText().toString();
                mArrayAsssortmentList.clear();
                mArrayAsssortmentList = ((GlobalVarialbles)getApplication()).getAssortmentFromName(text_search);
                mListViewShowAssortment.setAdapter(mAdapterShowAssortment);
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


        mListViewShowAssortment.setAdapter(mAdapterShowAssortment);

        SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
        SharedPreferences sPrefPre = getSharedPreferences("Bill_previewg", MODE_PRIVATE);

        mIPAdress = (sPref.getString(mIPConnect,""));
        mPortNumber = (sPref.getString(mPortConnect,""));
        mDeviceNumber = (sPref.getString(mDeviceID,""));

        mTableGuid = startIntent.getStringExtra(mNewBillTableGuid);
        mGuidBillIntent = startIntent.getStringExtra(mNewBillGuid);



        initAssortmentList("00000000-0000-0000-0000-000000000000");

        finalbil= new JSONObject();
        jsonArray = new JSONArray();

        mSaveBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCreateEditBill.setDeviceId(mDeviceNumber);
                mCreateEditBill.setTableUid(mTableGuid);
                mCreateEditBill.setBillUid(mGuidBillIntent);
                mCreateEditBill.setOrders(orderListCreateEditBill);


//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        mFabPreviewBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCreateEditBill.setDeviceId(mDeviceNumber);
                mCreateEditBill.setTableUid(mTableGuid);
                mCreateEditBill.setBillUid(mGuidBillIntent);
                mCreateEditBill.setOrders(orderListCreateEditBill);

                ((GlobalVarialbles)getApplication()).setNewBill(mCreateEditBill);
                Intent new_bill_activity = new Intent(".PreviewActivityRestaurant");
                startActivityForResult(new_bill_activity, REQUEST_CODE_PreviewBill);
            }
        });
        mListViewShowAssortment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mGuidBillClicked = (String)mArrayAsssortmentList.get(position).get(mMapAssortmentGuid);
                String ParentUid = (String)mArrayAsssortmentList.get(position).get(mMapAssortmentParenGuid);
                mAssortmentIsFolder = (Boolean) mArrayAsssortmentList.get(position).get(mMapAssortmentIsFolder);

                if(!mAssortmentIsFolder){
                    Intent count_activity = new Intent(".CountActivityRestaurant");
                    count_activity.putExtra(mMapAssortmentGuid,mGuidBillClicked);
                    startActivityForResult(count_activity,REQUEST_CODE_forCount);
                }else {
                    mListClickedItems.add(mIndexClickedItem,ParentUid);
                    mArrayAsssortmentList.clear();
                    initAssortmentList(mGuidBillClicked);
                    mIndexClickedItem += 1;
                }
            }
        });
        queryEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String text_search =queryEditText.getText().toString().toLowerCase();
                    mArrayAsssortmentList.clear();
                    mArrayAsssortmentList = ((GlobalVarialbles)getApplication()).getAssortmentFromName(text_search);
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
                OrderParcelable parcelableOrder = data.getParcelableExtra(mSaveOrderIntent);
                Order saveOrder = new Order();
                saveOrder.setPriceLineUid(parcelableOrder.getPriceLineUid());
                saveOrder.setCount(parcelableOrder.getCount());
                saveOrder.setAssortimentUid(parcelableOrder.getAssortimentUid());
                saveOrder.setComments(parcelableOrder.getComments());
                orderListCreateEditBill.add(saveOrder);
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

    private void initAssortmentList(String guidClicked) {
        mArrayAsssortmentList.clear();
        mArrayAsssortmentList = ((GlobalVarialbles)getApplication()).getAssortmentFromParent(guidClicked);
        mAdapterShowAssortment = new SimpleAdapter(this, mArrayAsssortmentList,R.layout.tesrt, new String[]{mMapAssortmentName,mMapAssortmentIcon,mMapAssortmentPrice}, new int[]{R.id.text_view_asl,R.id.image_view_asl_xm,R.id.text_test2});
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
        if (mListClickedItems.size() == 0) {
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
                        try {
                            finalbil.put("deviceId",mDeviceNumber);
                            finalbil.put("billUid", mGuidBillIntent);
                            finalbil.put("tableUid", mTableGuid);
                            finalbil.put("orders", jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        URL generateURLSendBil = generateURLSendBill(mIPAdress, mPortNumber);
                        new querrySendbill().execute(generateURLSendBil);
                    }
                });
                exit.show();
            }
        }else{
            x = mListClickedItems.size() - 1;
            mIndexClickedItem -= 1;
            mGuidBillClicked = mListClickedItems.get(x);
            initAssortmentList(mGuidBillClicked);
            mListClickedItems.remove(x);
        }
    }
}