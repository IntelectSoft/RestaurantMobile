package com.example.igor.restaurantmobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EditLineActivity extends AppCompatActivity {
    final Context context = this;
    String name_asl,price_asl,comentarii,A_JSon,value,Kit_membr,uid_save;
    EditText Count_enter;
    ImageButton btn_plus,btn_del,add_comment;
    TextView name_forasl,price_forasl,ViewCom,btn_save,btn_cancel;
    ListView coment_view;
    final static String LOG_TAG = "myLogs";
    int h,k,g=1;
    int i = 0;
    Boolean IntegerSales;
    ArrayList com_lists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit_line);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        Count_enter = findViewById(R.id.edt_cnt_line);
        btn_plus = findViewById(R.id.image_btn_add_count);
        btn_del = findViewById(R.id.image_btn_del_count);
        btn_save = findViewById(R.id.imageButton_save_edit);
        btn_cancel = findViewById(R.id.imageButton_cancel_edit);
        name_forasl = findViewById(R.id.name_asl_line);
        price_forasl =findViewById(R.id.priceasl_line);
        coment_view = findViewById(R.id.txt_coment_line);
//        add_comment=findViewById(R.id.img_add_comment_edit);
        coment_view.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ViewCom=findViewById(R.id.txtViewCom_line);
        SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
        SharedPreferences asl_u = getSharedPreferences("Assortiment",MODE_PRIVATE);
        SharedPreferences asl_price = getSharedPreferences("Assortiment_price",MODE_PRIVATE);
        SharedPreferences asl_sales = getSharedPreferences("Assortiment_Sales",MODE_PRIVATE);
        uid_save = (sPref.getString("Guid_Assortiment", ""));
        String count =sPref.getString("CountLine","");
        name_asl = (asl_u.getString(uid_save, ""));
        price_asl = (asl_price.getString(uid_save, ""));
        comentarii =(sPref.getString("coments_Assortiment", ""));
        Kit_membr = (sPref.getString("Sagi", ""));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        IntegerSales=(asl_sales.getBoolean(uid_save,false));

        name_forasl.setText(name_asl);
        price_forasl.setText("Pretul: "+price_asl);
        if (!IntegerSales){
            count=count.replace(",",".");
            int c=Math.round(Float.parseFloat(count));
            //count=count.replace(",",".");
            Double counts = Double.valueOf(count);
            Count_enter.setText(String.valueOf(c));
        }else{
            count=count.replace(",",".");
            Count_enter.setText(count);
        }

        Count_enter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (IntegerSales) {
                    if (isDigits(String.valueOf(charSequence))) {
                    } else {
                        Count_enter.setError("Format incorect!");
                    }
                }else{
                    if (isDigitInteger(Count_enter.getText().toString())) {
                    } else {
                        Count_enter.setError("Numai numere intregi!");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        View.OnClickListener _save = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IntegerSales) {
                    if (isDigits(Count_enter.getText().toString())) {
                        SharedPreferences preview = getSharedPreferences("Bill_preview", MODE_PRIVATE);
                        JSONObject created = null;
                        A_JSon = (preview.getString("CreatedBill", ""));
                        try {
                            created = new JSONObject(A_JSon);
                            JSONArray orders = created.getJSONArray("orders");
                            for (int i = 0; i < orders.length(); i++) {
                                JSONObject object = orders.getJSONObject(i);
                                String uid_prev = object.getString("AssortimentUid");
                                Boolean concait = uid_prev.contains(uid_save);
                                if (concait) {
                                    object.put("Count", String.valueOf(Count_enter.getText()));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences.Editor inputBill = preview.edit();
                        inputBill.putString("CreatedBill", String.valueOf(created));
                        inputBill.apply();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }else {
                    if (isDigitInteger(Count_enter.getText().toString())) {
                        SharedPreferences preview = getSharedPreferences("Bill_preview", MODE_PRIVATE);
                        JSONObject created = null;
                        A_JSon = (preview.getString("CreatedBill", ""));
                        try {
                            created = new JSONObject(A_JSon);
                            JSONArray orders = created.getJSONArray("orders");
                            for (int i = 0; i < orders.length(); i++) {
                                JSONObject object = orders.getJSONObject(i);
                                String uid_prev = object.getString("AssortimentUid");
                                Boolean concait = uid_prev.contains(uid_save);
                                if (concait) {
                                    object.put("Count", String.valueOf(Count_enter.getText()));
                                }
                            }
                        } catch (JSONException e) {
                            final AlertDialog.Builder eroare = new AlertDialog.Builder(context);
                            eroare.setTitle("Atentie!");
                            eroare.setMessage("Eroare! Mesajul erorii:" + "\n" + e);
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
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            }
        };
        View.OnClickListener _add = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IntegerSales) {
                    if (isDigits(Count_enter.getText().toString())) {
                        Double curr = Double.valueOf(String.valueOf(Count_enter.getText()));
                        curr += 1;
                        Count_enter.setText(String.valueOf(curr));
                    }
                }else{
                    if (isDigitInteger(Count_enter.getText().toString())) {
                        Integer curr = Integer.valueOf(Count_enter.getText().toString());
                        curr += 1;
                        Count_enter.setText(String.valueOf(curr));
                    }
                }

            }
        };
        View.OnClickListener _del = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IntegerSales) {
                    if (isDigits(Count_enter.getText().toString())) {
                        Double curr = Double.valueOf(String.valueOf(Count_enter.getText()));
                        if (curr - 1 <= 0) {

                        } else {
                            curr -= 1;
                        }
                        Count_enter.setText(String.valueOf(curr));
                    }
                }else{
                    if (isDigitInteger(Count_enter.getText().toString())) {
                        Integer curr = Integer.valueOf(Count_enter.getText().toString());
                        if (curr - 1 <= 0) {

                        } else {
                            curr -= 1;
                        }
                        Count_enter.setText(String.valueOf(curr));
                    }
                }
            }
        };
        View.OnClickListener _cancel = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_cancel = new Intent();
                setResult(RESULT_CANCELED,intent_cancel);
                finish();
            }
        };
        btn_del.setOnClickListener(_del);
        btn_plus.setOnClickListener(_add);
        btn_save.setOnClickListener(_save);
        btn_cancel.setOnClickListener(_cancel);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home : {
                Intent intent2 = new Intent();
                setResult(RESULT_CANCELED, intent2);
                finish();
            }break;

        }
        return super.onOptionsItemSelected(item);
    }
    private static boolean isDigits(String s) throws NumberFormatException {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private static boolean isDigitInteger(String s) throws NumberFormatException {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
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