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

import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentCount;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentGuid;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentIcon;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentName;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentPrice;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentState;

public class PreviewActivity extends AppCompatActivity {
    final String IP_save = "IP";
    final String Port_save = "Port";
    final String ID_resp_save = "ID_Device";
    String ip_,port,id_base_tel;
    ListView list_preview;
    SimpleAdapter mAdapterPreview;
    final Context context = this;
    int REQUESTCODE_Edit_Line=9;
    ProgressDialog pgH;
    Integer state;
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
        FloatingActionButton fab_save = (FloatingActionButton) findViewById(R.id.fab);
        final SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);

        ip_=sPref.getString(IP_save,"");
        port=(sPref.getString(Port_save,""));
        id_base_tel = sPref.getString(ID_resp_save,"");

        mAdapterPreview = new SimpleAdapter(this, preview_list,R.layout.preview_adapter, new String[]{mMapAssortmentIcon,mMapAssortmentName,mMapAssortmentCount,mMapAssortmentPrice}, new int[]{R.id.image_pre_asl,R.id.text_view_asl_prew,R.id.text_count_prew,R.id.text_price_prew});

        NewBill bill =  ((GlobalVarialbles)getApplication()).getNewBill();
        initBillView(bill);
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pgH=new ProgressDialog(context);
                pgH.setMessage("Asteptati...");
                pgH.setCancelable(false);
                pgH.setIndeterminate(true);
                pgH.show();

                //TODO action save bill
            }
        });
        list_preview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                 state =(Integer)preview_list.get(position).get(mMapAssortmentState);
                if (state==1){//is exist bill
                    list_preview.setItemChecked(position, false);
                    list_preview.setSelected(false);
                }else if (state==0){//if added new
                    list_preview.setItemChecked(position, true);
                    list_preview.setSelected(true);
                    //TODO action if selected line
                }
            }
        });
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
          //TODO set action edit line
        }break;
        case R.id.action_delete : {
            //TODO action delete Line
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

                //TODO action from result to edit Line
            }
        }
    }

    private void initBillView (NewBill bill){
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
            preview_list.add(bill_lines);
        }
        list_preview.setAdapter(mAdapterPreview);
    }
}
