package com.example.igor.restaurantmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.example.igor.restaurantmobile.AssortimentList.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.igor.restaurantmobile.GlobalVarialbles.mGuidZero;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapTableGuid;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapTableName;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mNewBillGuid;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mNewBillTableGuid;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mStateOpenBill;

public class TableActivity extends AppCompatActivity {
    GridView mGridViewTableList;
    ArrayList<HashMap<String, String>> tables_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_table);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_table);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mGridViewTableList =findViewById(R.id.grid_view_table);

        initTableList();
        final SimpleAdapter mAdapterTable = new SimpleAdapter(this, tables_list, R.layout.table_list, new String[]{mMapTableName}, new int[]{R.id.table_list});
        mGridViewTableList.setAdapter(mAdapterTable);

        mGridViewTableList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent asl_activity = new Intent(".AssortimentActivityRestaurant");
                asl_activity.putExtra(mNewBillGuid,mGuidZero);
                asl_activity.putExtra(mNewBillTableGuid,(String) tables_list.get(position).get(mMapTableGuid));
                asl_activity.putExtra(mStateOpenBill,0);
                startActivity(asl_activity);
                finish();
            }
        });
    }
    private void initTableList() {
        List<Table> mTableList = ((GlobalVarialbles)getApplication()).getTableList();
        for (Table table:mTableList) {
            HashMap<String, String> table_map = new HashMap<>();
            String tableName = table.getName();
            if(tableName.equals(""))
                tableName = "-";
            table_map.put(mMapTableName,tableName);
            table_map.put(mMapTableGuid,table.getUid());
            tables_list.add(table_map);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent result_main3 = new Intent();
            setResult(RESULT_CANCELED, result_main3);
            finish();
        }
        return super.onOptionsItemSelected(item);
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
}
