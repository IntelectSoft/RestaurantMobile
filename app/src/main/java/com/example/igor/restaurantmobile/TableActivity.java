package com.example.igor.restaurantmobile;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TableActivity extends AppCompatActivity {
    GridView gridview;
    String A_JSon;
    String table_uid;
    String table_name;
    final int REQUEST_CODE_forASL = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_table);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_table);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        }


        gridview =findViewById(R.id.grid_view_table);
        SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
        A_JSon = (sPref.getString("JSONObject", ""));

        initTableList();
        final SimpleAdapter simpleAdapterTable = new SimpleAdapter(this, tables_list, R.layout.table_list, new String[]{"NameTable"}, new int[]{R.id.table_list});
        gridview.setAdapter(simpleAdapterTable);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                table_name=(String) tables_list.get(position).get("NameTable");
                table_uid=(String) tables_list.get(position).get("UidTable");
                SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("TableUid",table_uid);
                ed.apply();
                Intent asl_activity = new Intent(".AssortimentActivityRestaurant");
                startActivityForResult(asl_activity,REQUEST_CODE_forASL);
            }
        });
    }
    ArrayList<HashMap<String, String>> tables_list = new ArrayList<>();
    private void initTableList() {
        try {
            JSONObject table_json = new JSONObject(A_JSon);
            JSONArray tables_array = table_json.getJSONArray("TableList");
            for (int i = 0; i < tables_array.length(); i++) {
                JSONObject object = tables_array.getJSONObject(i);
                String table_name = object.getString("Name");
                String uid_table = object.getString("Uid");
                HashMap<String, String> table_ = new HashMap<>();
                if (table_name.equals("")){
                    table_name=":(";
                }
                table_.put("NameTable", table_name);
                table_.put("UidTable", uid_table);
                tables_list.add(table_);
            }
        } catch (JSONException e) {
            final AlertDialog.Builder eroare = new AlertDialog.Builder(TableActivity.this);
            eroare.setTitle("Atentie!");
            eroare.setMessage("Eroare! Mesajul erorii:"+ "\n"+ e);
            eroare.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            eroare.show();
        }
    } //initList

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE_forASL){
            {
                Intent to_main = new Intent();
                setResult(RESULT_OK, to_main);
                finish();
            }
        }else if (resultCode==RESULT_CANCELED){
            Intent to_main2 = new Intent();
            setResult(RESULT_CANCELED, to_main2);
            finish();
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home : {
                Intent result_main3 = new Intent();
                setResult(RESULT_CANCELED, result_main3);
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
