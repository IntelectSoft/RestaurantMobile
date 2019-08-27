package com.example.igor.restaurantmobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.example.igor.restaurantmobile.Bill.ServiceGetBill;
import com.example.igor.restaurantmobile.Bill.ServiceSaveBill;
import com.example.igor.restaurantmobile.BillList.Bill;
import com.example.igor.restaurantmobile.BillList.BillListResponseService;
import com.example.igor.restaurantmobile.BillList.BillsLine;
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
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.igor.restaurantmobile.GlobalVarialbles.mDeviceID;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mIPConnect;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentCount;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentGuid;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentIcon;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentIsFolder;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentName;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentParenGuid;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentPrice;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapBillNumber;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mNewBillGuid;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mNewBillTableGuid;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mPortConnect;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mSaveOrderIntent;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mStateOpenBill;

public class AssortimentActivity extends AppCompatActivity {
    final Context context = this;

    ListView mListViewShowAssortment;
    SimpleAdapter mAdapterShowAssortment;
    String mIPAdress,mPortNumber,mDeviceNumber, mTableGuid,mGuidBillClicked,mGuidBillIntent;
    boolean mAssortmentIsFolder;
    ArrayList<HashMap<String, Object>> mArrayAsssortmentList = new ArrayList<>();
    int MESSAGE_SUCCES = 0,MESSAGE_RESULT_CODE = 1,MESSAGE_NULL_BODY = 2 , MESSAGE_FAILURE = 3,
            mIndexClickedItem = 0,mLastIndexClickedItem,REQUEST_CODE_forCount = 3,REQUEST_CODE_PreviewBill = 8;
    int MESSAGE_BILL_SUCCES = 7,MESSAGE_BILL_RESULT_CODE = 8;
    List<String> mListClickedItems = new ArrayList<>();
    FloatingActionButton mSaveBill,mFabPreviewBill;
    ProgressDialog pgH;
    NewBill mCreateEditBill = new NewBill();
    List<Order> orderListCreateEditBill = new ArrayList<>();
    private EditText queryEditText;

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
                    if (orderListCreateEditBill.size() == 0){
                        finish();
                    }else{
                        exitDialog();
                    }
                }else{
                    mLastIndexClickedItem = mListClickedItems.size() - 1;
                    mIndexClickedItem -= 1;
                    mGuidBillClicked = mListClickedItems.get(mLastIndexClickedItem);
                    initAssortmentList(mGuidBillClicked);
                    mListClickedItems.remove(mLastIndexClickedItem);
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
        mSaveBill = (FloatingActionButton) findViewById(R.id.save_bill);
        mFabPreviewBill = (FloatingActionButton) findViewById(R.id.preview_bill);


        mListViewShowAssortment.setAdapter(mAdapterShowAssortment);

        SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);

        mIPAdress = (sPref.getString(mIPConnect,""));
        mPortNumber = (sPref.getString(mPortConnect,""));
        mDeviceNumber = (sPref.getString(mDeviceID,""));

        mTableGuid = startIntent.getStringExtra(mNewBillTableGuid);
        mGuidBillIntent = startIntent.getStringExtra(mNewBillGuid);
        //1 - edit bill ;  0  - new bill
        int stateBill = startIntent.getIntExtra(mStateOpenBill,0);
        if(stateBill == 1){
            getBillPreview(mIPAdress,mPortNumber,mDeviceNumber,mGuidBillIntent);
        }
        else{
            initAssortmentList("00000000-0000-0000-0000-000000000000");
        }

        mSaveBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBill();
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
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_forCount) {
            if (resultCode == RESULT_OK) {
                OrderParcelable parcelableOrder = data.getParcelableExtra(mSaveOrderIntent);
                Order saveOrder = new Order();
                saveOrder.setPriceLineUid(parcelableOrder.getPriceLineUid());
                double count = 0.0;
                try{
                    count = Double.parseDouble(parcelableOrder.getCount());
                }catch (Exception e){
                    count = Double.parseDouble(parcelableOrder.getCount().replace(",","."));
                }
                saveOrder.setCount(count);
                saveOrder.setAssortimentUid(parcelableOrder.getAssortimentUid());
                saveOrder.setComments(parcelableOrder.getComments());
                saveOrder.setUid(parcelableOrder.getUid());
                orderListCreateEditBill.add(saveOrder);
            }
        }
        if (requestCode==REQUEST_CODE_PreviewBill){
            if (resultCode==RESULT_CANCELED){


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
    @Override
    public void onBackPressed() {
        if (mListClickedItems.size() == 0) {
            if (orderListCreateEditBill.size() == 0){
                finish();
            }else{
                exitDialog();
            }
        }else{
            mLastIndexClickedItem = mListClickedItems.size() - 1;
            mIndexClickedItem -= 1;
            mGuidBillClicked = mListClickedItems.get(mLastIndexClickedItem);
            initAssortmentList(mGuidBillClicked);
            mListClickedItems.remove(mLastIndexClickedItem);
        }
    }
    private void saveBill(){
        pgH.setMessage("Asteptati..");
        pgH.setIndeterminate(true);
        pgH.setCancelable(false);
        pgH.show();
        Thread saveBillThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mCreateEditBill.setDeviceId(mDeviceNumber);
                mCreateEditBill.setTableUid(mTableGuid);
                mCreateEditBill.setBillUid(mGuidBillIntent);
                mCreateEditBill.setOrders(orderListCreateEditBill);

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(3, TimeUnit.MINUTES)
                        .readTimeout(4, TimeUnit.MINUTES)
                        .writeTimeout(2, TimeUnit.MINUTES)
                        .build();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://" + mIPAdress + ":" + mPortNumber)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okHttpClient)
                        .build();
                ServiceSaveBill serviceSaveBill = retrofit.create(ServiceSaveBill.class);
                Call<BillListResponseService> billCallback = serviceSaveBill.saveBill(mCreateEditBill);
                billCallback.enqueue(new Callback<BillListResponseService>() {
                    @Override
                    public void onResponse(Call<BillListResponseService> call, Response<BillListResponseService> response) {
                        if (response.isSuccessful()) {
                            BillListResponseService billListResponseService = response.body();
                            int result = billListResponseService.getResult();
                            if(result == 0){
                                mHandlerSaveBills.obtainMessage(MESSAGE_SUCCES).sendToTarget();
                            }
                            else{
                                mHandlerSaveBills.obtainMessage(MESSAGE_RESULT_CODE,result).sendToTarget();
                            }

                        } else {
                            mHandlerSaveBills.obtainMessage(MESSAGE_NULL_BODY,response.message()).sendToTarget();

                        }
                    }

                    @Override
                    public void onFailure(Call<BillListResponseService> call, Throwable t) {
                        mHandlerSaveBills.obtainMessage(MESSAGE_FAILURE,t.getMessage()).sendToTarget();
                    }
                });
            }
        });saveBillThread.start();
    }

    private void exitDialog(){
        AlertDialog.Builder exit = new AlertDialog.Builder(context);
        exit.setTitle("Documentul nu este salvat!");
        exit.setMessage("Doriti sa slavati comanda? Daca doriti sa ramineti ,apasati in orice punct a ecranului.");
        exit.setNegativeButton("Nu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        exit.setPositiveButton("Da", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveBill();
            }
        });
        exit.show();
    }

    private void getBillPreview (final String ipAdress, final String portNumber, final String deviceID, final String billID){
        Thread mGetBillsList = new Thread(new Runnable() {
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
                ServiceGetBill serviceGetBill = retrofit.create(ServiceGetBill.class);
                final Call<BillListResponseService> getBillCall = serviceGetBill.getBill(deviceID, billID);
                getBillCall.enqueue(new Callback<BillListResponseService>() {
                    @Override
                    public void onResponse(Call<BillListResponseService> call, Response<BillListResponseService> response) {
                        BillListResponseService responseBillsList = response.body();
                        if(responseBillsList!=null){
                            int mErrorCode = responseBillsList.getResult();
                            if(mErrorCode == 0){
                                List<Bill> billsList = responseBillsList.getBillsList();
                                List<BillsLine> mBillLines = billsList.get(0).getLines();
                                for (BillsLine line:mBillLines) {
                                    Order orderInBill = new Order();
                                    orderInBill.setAssortimentUid(line.getAssortimentUid());
                                    orderInBill.setCount(line.getCount());
                                    orderInBill.setPriceLineUid(line.getPriceLineUid());
                                    orderInBill.setComments(line.getComments());
                                    orderInBill.setUid(line.getUid());

                                    orderListCreateEditBill.add(orderInBill);

                                }
                                mHandlerSaveBills.obtainMessage(MESSAGE_BILL_SUCCES).sendToTarget();
                            }
                            else{
                                mHandlerSaveBills.obtainMessage(MESSAGE_BILL_RESULT_CODE,mErrorCode).sendToTarget();
                            }

                        }
                        else{
                            mHandlerSaveBills.obtainMessage(MESSAGE_NULL_BODY).sendToTarget();
                        }
                    }
                    @Override
                    public void onFailure(Call<BillListResponseService> call, Throwable t) {
                        mHandlerSaveBills.obtainMessage(MESSAGE_FAILURE,t.getMessage()).sendToTarget();
                    }
                });
            }
        });
        mGetBillsList.start();
    }

    private final Handler mHandlerSaveBills = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_SUCCES) {
                pgH.dismiss();
                setResult(RESULT_OK);
                finish();
            }
            else if (msg.what == MESSAGE_RESULT_CODE) {
                pgH.dismiss();
                int errorCode = Integer.valueOf(msg.obj.toString());
                switch (errorCode){
                    case 1 : {
                        Snackbar.make(mSaveBill, "UnknownError", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 2 : {
                        Snackbar.make(mSaveBill, "DeviceNotRegistered", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 3 : {
                        Snackbar.make(mSaveBill, "ShiftIsNotValid", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 4 : {
                        Snackbar.make(mSaveBill, "BillNotFound", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 5 : {
                        Snackbar.make(mSaveBill, "ClientNotFound", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 6 : {
                        Snackbar.make(mSaveBill, "SecurityException", Snackbar.LENGTH_LONG).show();
                    }break;
                }
            }
            else if (msg.what == MESSAGE_NULL_BODY) {
                Snackbar.make(mSaveBill, "Response body is null: "+ msg.obj.toString(), Snackbar.LENGTH_LONG).show();
            }
            else if (msg.what == MESSAGE_FAILURE){
                Snackbar.make(mSaveBill, "Failure save bill: "+ msg.obj.toString(), Snackbar.LENGTH_LONG).show();
            }
            else if( msg.what == MESSAGE_BILL_SUCCES){
                initAssortmentList("00000000-0000-0000-0000-000000000000");
            }
            else if(msg.what == MESSAGE_BILL_RESULT_CODE){
                int errorCode = Integer.valueOf(msg.obj.toString());
                switch (errorCode){
                    case 1 : {
                        Snackbar.make(mSaveBill, "UnknownError", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 2 : {
                        Snackbar.make(mSaveBill, "DeviceNotRegistered", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 3 : {
                        Snackbar.make(mSaveBill, "ShiftIsNotValid", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 4 : {
                        Snackbar.make(mSaveBill, "BillNotFound", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 5 : {
                        Snackbar.make(mSaveBill, "ClientNotFound", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 6 : {
                        Snackbar.make(mSaveBill, "SecurityException", Snackbar.LENGTH_LONG).show();
                    }break;
                }
            }
        }
    };
}