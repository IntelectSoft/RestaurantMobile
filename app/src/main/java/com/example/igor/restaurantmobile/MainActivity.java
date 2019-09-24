package com.example.igor.restaurantmobile;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.igor.restaurantmobile.Bill.ServiceGetBill;
import com.example.igor.restaurantmobile.BillList.Bill;
import com.example.igor.restaurantmobile.BillList.BillListResponseService;
import com.example.igor.restaurantmobile.BillList.BillsLine;
import com.example.igor.restaurantmobile.BillList.ServiceBillList;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.igor.restaurantmobile.GlobalVarialbles.mGuidZero;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentCount;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentName;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapBillGuid;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapBillNumber;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapBillSum;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapBillSumAfterDiscount;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapTableGuid;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapTableName;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mNewBillGuid;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mNewBillTableGuid;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mStateOpenBill;
import static com.example.igor.restaurantmobile.utilis.NetworkUtils.generateURL;

public class MainActivity extends AppCompatActivity {
    final Context context = this;
    ProgressDialog pgH;
    FloatingActionMenu menu_bill;

    String mBillID,mTableID,mTableName,mDeviceID, mPortConnect,mIPConnect, mGuidBillClicked = null ,IP_save = "IP",Port_save = "Port",Device_save = "ID_Device",mTableGuidClickedItem;
    int mBillNumber ,mDisplayDefaultHeight,mBillNumberClicked;
    int MESSAGE_SUCCES = 0,MESSAGE_RESULT_CODE = 1,MESSAGE_NULL_BODY = 2 , MESSAGE_FAILURE = 3 ,REQUEST_CODE_NewBill = 4,REQUEST_CODE_Settings = 5,REQUEST_CODE_EditBill = 6,MESSAGE_BILL_SUCCES = 7,
            MESSAGE_BILL_RESULT_CODE = 8;
    double mBillSumAfterDiscount,mBillSum;
    boolean mShowLine;

    TimerTask timerTaskUpdate;
    Timer updateBills;

    SimpleAdapter mAdapterShowLine, mAdapterBills;
    SharedPreferences sPrefSettings;
    GridView mGridViewList_Bills;
    ListView mListViewShowLine;
    ArrayList<HashMap<String, Object>> bills_list = new ArrayList<>();
    ArrayList<HashMap<String, Object>> bill_lines = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pgH=new ProgressDialog(context);
        mGridViewList_Bills = findViewById(R.id.list_bill);
        mListViewShowLine=findViewById(R.id.contentBillList);
        menu_bill = findViewById(R.id.fab1);
        FloatingActionButton fab_add = findViewById(R.id.item_add);
        FloatingActionButton fab_show_line = findViewById(R.id.item_show_line);
        //FloatingActionButton fab_print = findViewById(R.id.item_print);
        final LinearLayout mMainLayout = (LinearLayout)findViewById(R.id.LGridView);
        final LinearLayout mShowLineLayout = (LinearLayout)findViewById(R.id.ShowLine);
        final LinearLayout.LayoutParams mMainParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final LinearLayout.LayoutParams mShowLineParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        Display display = getWindowManager().getDefaultDisplay();
        final int mDisplayHeight = display.getHeight();
        int mNewDisplayHeight = mDisplayHeight / 3 ;
        final int mHeightGrid = (mNewDisplayHeight * 2) - 110;
        final int mShowLinwHeight = mDisplayHeight - mHeightGrid;
        mDisplayDefaultHeight = mDisplayHeight;
        mMainParams.height = mHeightGrid;
        mShowLineParams.height = mShowLinwHeight;
        mMainLayout.setLayoutParams(mMainParams);
        mShowLineLayout.setLayoutParams(mShowLineParams);

        sPrefSettings = getSharedPreferences("Save setting", MODE_PRIVATE);
        mAdapterShowLine = new SimpleAdapter(this, bill_lines,R.layout.bill_show, new String[]{mMapAssortmentName,mMapAssortmentCount}, new int[]{R.id.textName,R.id.textCount});
        mAdapterBills = new SimpleAdapter(this, bills_list,R.layout.lis_bills, new String[]{mMapTableName,mMapBillNumber}, new int[]{R.id.text1,R.id.text2});

        mShowLine = true;
        mIPConnect=(sPrefSettings.getString(IP_save,""));
        mPortConnect=(sPrefSettings.getString(Port_save,""));
        mDeviceID = (sPrefSettings.getString(Device_save,""));

        int period = sPrefSettings.getInt("TimeUpdate",0);
        if(period != 0){
            updateBills=new Timer();
            startTimetaskSync();
            updateBills.schedule(timerTaskUpdate,period,period);
        }

        showDialog();
        getBillList(mIPConnect,mPortConnect,mDeviceID,false);

        fab_show_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mShowLine){
                    mDisplayDefaultHeight = mDisplayHeight;
                    mMainParams.height = mHeightGrid;
                    mShowLineParams.height = mShowLinwHeight;
                    mMainLayout.setLayoutParams(mMainParams);
                    mShowLineLayout.setLayoutParams(mShowLineParams);
                    mShowLine = true;
                    if (mGuidBillClicked != null) getBill(mIPConnect, mPortConnect, mDeviceID, mGuidBillClicked);
                }
                else {
                    mMainParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    mMainLayout.setLayoutParams(mMainParams);
                    mShowLineParams.height = 0;
                    mShowLineLayout.setLayoutParams(mShowLineParams);
                    mShowLine = false;
                }
                menu_bill.close(true);
            }
        });
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int mTableListSize = ((GlobalVarialbles)getApplication()).getTableListSize();
                if (mTableListSize != 0) {
                    Intent new_bill_activity = new Intent(".TableActivityRestaurant");
                    startActivity(new_bill_activity);
                }
                else{
                    Intent new_bill_activity = new Intent(".AssortimentActivityRestaurant");
                    new_bill_activity.putExtra(mNewBillGuid,mGuidZero);
                    new_bill_activity.putExtra(mStateOpenBill,0);
                    new_bill_activity.putExtra("BillNumber",0);
                    startActivityForResult(new_bill_activity, REQUEST_CODE_EditBill);
                }
                menu_bill.close(true);
            }
        });

//        fab_print.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                uid_bill_to_close =(sPref.getString("bills_uid",""));
//                String uid="00000000-0000-0000-0000-000000000000";
//                boolean selector = uid_bill_to_close.contains(uid);
//                if (selector){
//                    Toast.makeText(context,"Alegeti contul!",Toast.LENGTH_SHORT).show();
//                }else {
//                    close_bill();
//                    onCloseType();
//                }
//                menu_bill.close(true);
//            }
//        });

        mGridViewList_Bills.setOnItemClickListener (new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mGridViewList_Bills.setItemChecked(position, true);
                mGridViewList_Bills.setSelected(true);
                mBillNumberClicked = (Integer) bills_list.get(position).get(mMapBillNumber);
                mGuidBillClicked  = (String) bills_list.get(position).get(mMapBillGuid);
                mTableGuidClickedItem = (String) bills_list.get(position).get(mMapTableGuid);
                if (mShowLine) getBill(mIPConnect, mPortConnect, mDeviceID, mGuidBillClicked);
            }
        });
        mGridViewList_Bills.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder detail_bill = new AlertDialog.Builder(context);
                detail_bill.setTitle("Detalii cont: " + (Integer)bills_list.get(i).get(mMapBillNumber) );
                detail_bill.setMessage("\nMasa: "+ (String)bills_list.get(i).get(mMapTableName) + "\nSuma: "+ String.valueOf(bills_list.get(i).get(mMapBillSum)) +
                        "\nSuma cu reducere: " + String.valueOf(bills_list.get(i).get(mMapBillSumAfterDiscount)));
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
    }
    private void startTimetaskSync(){
        timerTaskUpdate = new TimerTask() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bill_lines.clear();
                        mGuidBillClicked = null;
                        mListViewShowLine.setAdapter(mAdapterShowLine);
                        getBillList(mIPConnect,mPortConnect,mDeviceID,false);
                    }
                });
            }
        };
    }
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
                Intent setting_activity = new Intent(".SettingsActivity");
                startActivityForResult(setting_activity,REQUEST_CODE_Settings);
            }break;
            case R.id.action_refresh: {
                showDialog();
                mGuidBillClicked = null;
                mListViewShowLine.setAdapter(mAdapterShowLine);
                getBillList(mIPConnect,mPortConnect,mDeviceID,false);
            }break;
            case R.id.action_edit : {
                if (mGuidBillClicked == null){
                    Toast.makeText(context,"Alegeti contul!",Toast.LENGTH_SHORT).show();
                }else {
                    Intent new_bill_activity = new Intent(".AssortimentActivityRestaurant");
                    new_bill_activity.putExtra(mNewBillGuid,mGuidBillClicked);
                    new_bill_activity.putExtra(mNewBillTableGuid,mTableGuidClickedItem);
                    new_bill_activity.putExtra(mStateOpenBill,1);
                    new_bill_activity.putExtra("BillNumber",mBillNumberClicked);
                    startActivity(new_bill_activity);
                }
            }break;
            case R.id.action_exit :{
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
                mIPConnect = (sPrefSettings.getString(IP_save,""));
                mPortConnect = (sPrefSettings.getString(Port_save,""));
                mDeviceID = (sPrefSettings.getString(Device_save,""));

                int period = sPrefSettings.getInt("TimeUpdate",0);
                if(period != 0){
                    if (updateBills != null)
                        updateBills.cancel();
                    if(timerTaskUpdate != null)
                        timerTaskUpdate.cancel();
                    updateBills=new Timer();
                    startTimetaskSync();
                    updateBills.schedule(timerTaskUpdate,period,period);
                }
            }
        }
    }
    private void getBill(final String ipAdress, final String portNumber, final String deviceID, final String billID){
        bill_lines.clear();
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
                                List<BillsLine> mBillLines = new ArrayList<>();

                                if(billsList.get(0).getLines().size() > 0){
                                    mBillLines = billsList.get(0).getLines();
                                    for (BillsLine line:mBillLines) {
                                        HashMap<String, Object> bill_line = new HashMap<>();
                                        bill_line.put(mMapAssortmentName,((GlobalVarialbles)getApplication()).getAssortmentName(line.getAssortimentUid()));
                                        bill_line.put(mMapAssortmentCount,line.getCount());
                                        bill_lines.add(bill_line);
                                    }
                                    mHandlerBills.obtainMessage(MESSAGE_BILL_SUCCES).sendToTarget();
                                }
                                else{
                                    mHandlerBills.obtainMessage(MESSAGE_NULL_BODY).sendToTarget();
                                }
                            }
                            else{
                                mHandlerBills.obtainMessage(MESSAGE_BILL_RESULT_CODE,mErrorCode).sendToTarget();
                            }

                        }
                        else{
                            mHandlerBills.obtainMessage(MESSAGE_NULL_BODY).sendToTarget();
                        }
                    }
                    @Override
                    public void onFailure(Call<BillListResponseService> call, Throwable t) {
                        mHandlerBills.obtainMessage(MESSAGE_FAILURE,t.getMessage()).sendToTarget();
                    }
                });
            }
        });
        mGetBillsList.start();
    }
    private void getBillList(final String ipAdress, final String portNumber, final String deviceID, final boolean includeLines){
        bills_list.clear();
        bill_lines.clear();
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
                ServiceBillList getBillListServiceApi = retrofit.create(ServiceBillList.class);
                final Call<BillListResponseService> billListCall = getBillListServiceApi.getBillsList(deviceID, includeLines);

                billListCall.enqueue(new Callback<BillListResponseService>() {
                    @Override
                    public void onResponse(Call<BillListResponseService> call, Response<BillListResponseService> response) {
                        BillListResponseService responseBillsList = response.body();
                        if(responseBillsList!=null){
                            int mErrorCode = responseBillsList.getResult();
                            if(mErrorCode == 0){
                                List<Bill > billsList = responseBillsList.getBillsList();

                                for (int i = 0; i < billsList.size(); i++) {
                                    mBillNumber = billsList.get(i).getNumber();
                                    mBillID = billsList.get(i).getUid();
                                    mTableID = billsList.get(i).getTableUid();
                                    mBillSumAfterDiscount = billsList.get(i).getSumAfterDiscount();
                                    mBillSum = billsList.get(i).getSum();
                                    mTableName = ((GlobalVarialbles)getApplication()).getTableName(mTableID);

                                    HashMap<String, Object> bill_ = new HashMap<>();
                                    bill_.put(mMapBillNumber,mBillNumber);
                                    bill_.put(mMapTableName,mTableName);
                                    bill_.put(mMapBillGuid,mBillID);
                                    bill_.put(mMapTableGuid,mTableID);
                                    bill_.put(mMapBillSumAfterDiscount,mBillSumAfterDiscount);
                                    bill_.put(mMapBillSum,mBillSum);
                                    bills_list.add(bill_);
                                }
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
                    public void onFailure(Call<BillListResponseService> call, Throwable t) {
                        mHandlerBills.obtainMessage(MESSAGE_FAILURE,t.getMessage()).sendToTarget();
                    }
                });
            }
        });
        mGetBillsList.start();
    }
    private final Handler mHandlerBills = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_SUCCES) {
                pgH.dismiss();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    bills_list.sort(new Comparator<HashMap<String, Object>>() {
                        @Override
                        public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
                            return o2.get(mMapBillNumber).toString().compareTo(o1.get(mMapBillNumber).toString());
                        }
                    });
                }
                mGridViewList_Bills.setAdapter(mAdapterBills);
            }
            else if (msg.what == MESSAGE_RESULT_CODE) {
                pgH.dismiss();
                int errorCode = Integer.valueOf(msg.obj.toString());
                switch (errorCode){
                    case 1 : {
                        Snackbar.make(menu_bill, "UnknownError", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 2 : {
                        Snackbar.make(menu_bill, "Device "+ mDeviceID+ " Not Registered", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 3 : {
                        Snackbar.make(menu_bill, "ShiftIsNotValid", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 4 : {
                        Snackbar.make(menu_bill, "BillNotFound", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 5 : {
                        Snackbar.make(menu_bill, "ClientNotFound", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 6 : {
                        Snackbar.make(menu_bill, "SecurityException", Snackbar.LENGTH_LONG).show();
                    }break;
                }
            }
            else if(msg.what == MESSAGE_BILL_SUCCES){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    bill_lines.sort(new Comparator<HashMap<String, Object>>() {
                        @Override
                        public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
                            return o1.get(mMapAssortmentName).toString().compareTo(o2.get(mMapAssortmentName).toString());
                        }
                    });
                }
                mListViewShowLine.setAdapter(mAdapterShowLine);
            }
            else if(msg.what == MESSAGE_BILL_RESULT_CODE){
                int errorCode = Integer.valueOf(msg.obj.toString());
                switch (errorCode){
                    case 1 : {
                        Snackbar.make(menu_bill, "UnknownError", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 2 : {
                        Snackbar.make(menu_bill, "Device "+ mDeviceID+ " Not Registered", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 3 : {
                        Snackbar.make(menu_bill, "ShiftIsNotValid", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 4 : {
                        Snackbar.make(menu_bill, "BillNotFound", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 5 : {
                        Snackbar.make(menu_bill, "ClientNotFound", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 6 : {
                        Snackbar.make(menu_bill, "SecurityException", Snackbar.LENGTH_LONG).show();
                    }break;
                }
            }
            else if (msg.what == MESSAGE_NULL_BODY) { Snackbar.make(menu_bill, "Body is null", Snackbar.LENGTH_LONG).show();}
            else if (msg.what == MESSAGE_FAILURE){ Snackbar.make(menu_bill, "Failure: " + msg.obj.toString(), Snackbar.LENGTH_LONG).show();}
        }
    };
    private void showDialog(){
        pgH.setMessage("Asteptati...");
        pgH.setIndeterminate(true);
        pgH.setCancelable(false);
        pgH.show();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        showDialog();
        bill_lines.clear();
        mListViewShowLine.setAdapter(mAdapterShowLine);
        getBillList(mIPConnect,mPortConnect,mDeviceID,false);
    }
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        menu_bill.close(true);
        return super.dispatchTouchEvent(event);
    }
}