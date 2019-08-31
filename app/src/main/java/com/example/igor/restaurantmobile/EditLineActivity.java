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

import com.example.igor.restaurantmobile.AssortimentList.Assortiment;
import com.example.igor.restaurantmobile.CreateNewBill.Order;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Count_enter = findViewById(R.id.edt_cnt_line);
        btn_plus = findViewById(R.id.image_btn_add_count);
        btn_del = findViewById(R.id.image_btn_del_count);
        btn_save = findViewById(R.id.imageButton_save_edit);
        btn_cancel = findViewById(R.id.imageButton_cancel_edit);
        name_forasl = findViewById(R.id.name_asl_line);
        price_forasl =findViewById(R.id.priceasl_line);
        coment_view = findViewById(R.id.txt_coment_line);
        coment_view.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ViewCom=findViewById(R.id.txtViewCom_line);

        Intent startIntent = getIntent();
        final String orderID = startIntent.getStringExtra("Order");

        Order editOrder = ((GlobalVarialbles)getApplication()).getOrderFromInternUid(orderID);
        Assortiment assortiment = ((GlobalVarialbles)getApplication()).getAssortmentFromID(editOrder.getAssortimentUid());
        String assortmentName = assortiment.getName();
        double assortmentPrice = assortiment.getPrice();
        IntegerSales = assortiment.getAllowNonIntegerSale();

        name_forasl.setText(assortmentName);
        price_forasl.setText("Pretul: "+ assortmentPrice);
        Count_enter.setText(String.valueOf(editOrder.getCount()));

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
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double count = 0.0;
                try{
                    count = Double.parseDouble(Count_enter.getText().toString());
                }catch (Exception e){
                    count = Double.parseDouble(Count_enter.getText().toString().replace(",","."));
                }
                ((GlobalVarialbles)getApplication()).setCountOrderFromInternUid(orderID,count);
                setResult(RESULT_OK);
                finish();
            }
        });
        btn_plus.setOnClickListener( new View.OnClickListener() {
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
        });
        btn_del.setOnClickListener(new View.OnClickListener() {
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
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
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
}