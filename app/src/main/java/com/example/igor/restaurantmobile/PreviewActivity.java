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
import android.os.Handler;
import android.os.Message;
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

import com.example.igor.restaurantmobile.Bill.ServiceSaveBill;
import com.example.igor.restaurantmobile.BillList.BillListResponseService;
import com.example.igor.restaurantmobile.CreateNewBill.NewBill;
import com.example.igor.restaurantmobile.CreateNewBill.Order;

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
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentCount;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentGuid;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentIcon;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentName;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentPrice;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentState;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapOrderUid;

public class PreviewActivity extends AppCompatActivity {
    final String IP_save = "IP";
    final String Port_save = "Port";
    final String ID_resp_save = "ID_Device";
    String ip_,port,id_base_tel,id_assortment =null,count_assortment,orderUid;
    ListView list_preview;
    SimpleAdapter mAdapterPreview;
    final Context context = this;
    int REQUESTCODE_Edit_Line=9;
    ProgressDialog pgH;
    Integer state;
    FloatingActionButton fab_save;
    int MESSAGE_SUCCES = 0,MESSAGE_RESULT_CODE = 1,MESSAGE_NULL_BODY = 2 , MESSAGE_FAILURE = 3;

    ArrayList<HashMap<String, Object>> preview_list = new ArrayList<>();

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
        list_preview=findViewById(R.id.list_preview);
        pgH=new ProgressDialog(context);
        fab_save = (FloatingActionButton) findViewById(R.id.fab);
        final SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);

        ip_=sPref.getString(IP_save,"");
        port=(sPref.getString(Port_save,""));
        id_base_tel = sPref.getString(ID_resp_save,"");

        mAdapterPreview = new SimpleAdapter(this, preview_list,R.layout.preview_adapter, new String[]{mMapAssortmentIcon,mMapAssortmentName,mMapAssortmentCount,mMapAssortmentPrice}, new int[]{R.id.image_pre_asl,R.id.text_view_asl_prew,R.id.text_count_prew,R.id.text_price_prew});

        initBillView();

        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBill();
            }
        });
        list_preview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                 state =(Integer)preview_list.get(position).get(mMapAssortmentState);
                if (state==1){//is exist bill
                    list_preview.setItemChecked(position, false);
                    list_preview.setSelected(false);
                    id_assortment = null;
                }else if (state==0){//if added new
                    list_preview.setItemChecked(position, true);
                    list_preview.setSelected(true);
                    id_assortment = (String)preview_list.get(position).get(mMapAssortmentGuid);
                    orderUid = (String) preview_list.get(position).get(mMapOrderUid);

                }
            }
        });
    }
    public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    switch (id){
        case android.R.id.home : {
            setResult(RESULT_CANCELED);
            finish();
        }break;
        case R.id.action_edit_line:{
          if(id_assortment != null){
              Intent editLine = new Intent(this,EditLineActivity.class);
              editLine.putExtra("Order",orderUid);
              startActivityForResult(editLine,REQUESTCODE_Edit_Line);
          }
        }break;
        case R.id.action_delete : {
            if(id_assortment != null) {
                ((GlobalVarialbles) getApplication()).removeOrderFromInternUid(orderUid);
                initBillView();
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
                initBillView();
            }
        }
    }

    private void initBillView (){
        NewBill bill =  ((GlobalVarialbles)getApplication()).getNewBill();
        preview_list.clear();
        List<Order> listOrder = bill.getOrders();
        for (Order order:listOrder) {
            HashMap<String, Object> bill_lines = new HashMap<>();

            String uid = order.getUid();
            if (uid.equals("00000000-0000-0000-0000-000000000000")) {
                bill_lines.put(mMapAssortmentIcon,R.drawable.add_circle_black_48dp);
                bill_lines.put(mMapAssortmentState,0);
            }
            else {
                bill_lines.put(mMapAssortmentIcon,R.drawable.check_circle_black_48dp);
                bill_lines.put(mMapAssortmentState,1);
            }
            bill_lines.put(mMapAssortmentName,((GlobalVarialbles)getApplication()).getAssortmentName(order.getAssortimentUid()));
            bill_lines.put(mMapAssortmentCount,String.format("%.2f",order.getCount()));
            bill_lines.put(mMapAssortmentPrice,String.format("%.2f",((GlobalVarialbles)getApplication()).getAssortmentPrice(order.getAssortimentUid())));
            bill_lines.put(mMapAssortmentGuid,order.getAssortimentUid());
            bill_lines.put(mMapOrderUid,order.getInternUid());
            preview_list.add(bill_lines);
        }
        list_preview.setAdapter(mAdapterPreview);
    }

    private void saveBill(){
        pgH.setMessage("Asteptati..");
        pgH.setIndeterminate(true);
        pgH.setCancelable(false);
        pgH.show();
        Thread saveBillThread = new Thread(new Runnable() {
            @Override
            public void run() {
                NewBill bill =  ((GlobalVarialbles)getApplication()).getNewBill();
                NewBill sendBill = new NewBill();
                sendBill.setBillUid(bill.getBillUid());
                sendBill.setTableUid(bill.getTableUid());
                sendBill.setDeviceId(bill.getDeviceId());
                List<Order> orderListSend = new ArrayList<>();
                List<Order> orderList = bill.getOrders();
                for (Order order:orderList) {
                    Order orderSend = new Order();

                    if (order.getInternUid() != null){
                        orderSend.setCount(order.getCount());
                        orderSend.setAssortimentUid(order.getAssortimentUid());
                        orderSend.setPriceLineUid(order.getPriceLineUid());
                        orderSend.setComments(order.getComments());

                        orderListSend.add(orderSend);
                    }

                }
                sendBill.setOrders(orderListSend);

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(3, TimeUnit.MINUTES)
                        .readTimeout(4, TimeUnit.MINUTES)
                        .writeTimeout(2, TimeUnit.MINUTES)
                        .build();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://" + ip_ + ":" + port)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okHttpClient)
                        .build();
                ServiceSaveBill serviceSaveBill = retrofit.create(ServiceSaveBill.class);
                Call<BillListResponseService> billCallback = serviceSaveBill.saveBill(sendBill);
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
                        Snackbar.make(fab_save, "UnknownError", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 2 : {
                        Snackbar.make(fab_save, "DeviceNotRegistered", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 3 : {
                        Snackbar.make(fab_save, "ShiftIsNotValid", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 4 : {
                        Snackbar.make(fab_save, "BillNotFound", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 5 : {
                        Snackbar.make(fab_save, "ClientNotFound", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 6 : {
                        Snackbar.make(fab_save, "SecurityException", Snackbar.LENGTH_LONG).show();
                    }break;
                }
            }
            else if (msg.what == MESSAGE_NULL_BODY) {
                pgH.dismiss();
                Snackbar.make(fab_save, "Response body is null: "+ msg.obj.toString(), Snackbar.LENGTH_LONG).show();
            }
            else if (msg.what == MESSAGE_FAILURE){
                pgH.dismiss();
                Snackbar.make(fab_save, "Failure save bill: "+ msg.obj.toString(), Snackbar.LENGTH_LONG).show();
            }
        }
    };
}
