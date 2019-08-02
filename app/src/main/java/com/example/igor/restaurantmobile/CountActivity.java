package com.example.igor.restaurantmobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CountActivity extends AppCompatActivity {
    final Context context = this;
    String name_asl,price_asl,comentarii,A_JSon,value,Kit_membr;
    Boolean IntegerSales;
    EditText Count_enter;
    ImageButton btn_plus,btn_del;
    TextView name_forasl,price_forasl,ViewCom,btn_save,btn_cancel,add_comment;
    ListView coment_view;
    final static String LOG_TAG = "myLogs";
    int h,k,g=1;
    int i = 0;
    ArrayList com_lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_count);
        Count_enter = findViewById(R.id.edt_cnt);
        btn_plus = findViewById(R.id.image_btn_add);
        btn_del = findViewById(R.id.image_btn_del);
        btn_save = findViewById(R.id.imageButton_save);
        btn_cancel = findViewById(R.id.imageButton_cancel);
        name_forasl = findViewById(R.id.name_asl_for_count);
        price_forasl =findViewById(R.id.priceasl_for_count);
        coment_view = findViewById(R.id.txt_coment);
        add_comment=findViewById(R.id.img_add_comment);
        coment_view.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ViewCom=findViewById(R.id.txtViewCom);

        SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
        SharedPreferences asl_u = getSharedPreferences("Assortiment",MODE_PRIVATE);
        SharedPreferences asl_price = getSharedPreferences("Assortiment_price",MODE_PRIVATE);
        SharedPreferences asl_sales = getSharedPreferences("Assortiment_Sales",MODE_PRIVATE);
        A_JSon = (sPref.getString("JSONObject", ""));
        String uid_save = (sPref.getString("Guid_Assortiment", ""));
        name_asl = (asl_u.getString(uid_save, ""));
        price_asl = (asl_price.getString(uid_save, ""));
        comentarii =(sPref.getString("coments_Assortiment", ""));
        Kit_membr = (sPref.getString("Sagi", ""));
        IntegerSales=(asl_sales.getBoolean(uid_save,false));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_count);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        name_forasl.setText(name_asl);
        price_forasl.setText("Pretul: "+price_asl);
        com_lists = new ArrayList();
        initist();
        final SimpleAdapter simpleAdapterCOM = new SimpleAdapter(this, comm_list,R.layout.comment, new String[]{"Comentarii"}, new int[]{R.id.text_view_comment});
        coment_view.setAdapter(simpleAdapterCOM);

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
        Count_enter.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_DONE) {
                    if (IntegerSales) {
                        if (isDigits(Count_enter.getText().toString())) {
                            SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
                            SharedPreferences.Editor ed = sPref.edit();
                            ed.putString("coments_Assortiment", "");
                            ed.apply();
                            Intent intent = new Intent();
                            intent.putExtra("count", String.valueOf(Count_enter.getText()));
                            intent.putExtra("comentar", com_lists);
                            setResult(RESULT_OK, intent);
                            finish();
                            comm_list.clear();
                        }
                    } else {
                        if (isDigitInteger(Count_enter.getText().toString())) {
                            SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
                            SharedPreferences.Editor ed = sPref.edit();
                            ed.putString("coments_Assortiment", "");
                            ed.apply();
                            Intent intent = new Intent();
                            intent.putExtra("count", String.valueOf(Count_enter.getText()));
                            intent.putExtra("comentar", com_lists);
                            setResult(RESULT_OK, intent);
                            finish();
                            comm_list.clear();
                        }
                    }
                }
                return false;
            }
        });

        View.OnClickListener _save = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IntegerSales) {
                        if (isDigits(Count_enter.getText().toString())) {
                            SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
                            SharedPreferences.Editor ed = sPref.edit();
                            ed.putString("coments_Assortiment", "");
                            ed.apply();
                            Intent intent = new Intent();
                            intent.putExtra("count", String.valueOf(Count_enter.getText()));
                            intent.putExtra("comentar", com_lists);
                            setResult(RESULT_OK, intent);
                            finish();
                            comm_list.clear();
                        }
                    } else {
                        if (isDigitInteger(Count_enter.getText().toString())) {
                            SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
                            SharedPreferences.Editor ed = sPref.edit();
                            ed.putString("coments_Assortiment", "");
                            ed.apply();
                            Intent intent = new Intent();
                            intent.putExtra("count", String.valueOf(Count_enter.getText()));
                            intent.putExtra("comentar", com_lists);
                            setResult(RESULT_OK, intent);
                            finish();
                            comm_list.clear();
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
                SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("coments_Assortiment","");
                ed.apply();
                Intent intent_cancel = new Intent();
                setResult(RESULT_CANCELED,intent_cancel);
                finish();
            }
        };

        View.OnClickListener _add_comm = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Comentarii:");
                builder.setMessage("Introduceti comentariul:");
                final EditText input = new EditText(context);
                input.setFocusable(true);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String singur_com= input.getText().toString();
                        com_lists.add(singur_com);
                        ViewCom.append(singur_com+ ", ");
                    }
                });
                builder.setNegativeButton("Renunt", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog.show();
            }
        };
        coment_view.setOnItemClickListener (new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                coment_view.setItemChecked(position, true);
                coment_view.setSelected(true);
                String name_com =(String)comm_list.get(position).get("Comentarii");
                com_lists.add(name_com);


            }
        });//list_bils click listener
        btn_del.setOnClickListener(_del);
        btn_plus.setOnClickListener(_add);
        btn_save.setOnClickListener(_save);
        btn_cancel.setOnClickListener(_cancel);
        add_comment.setOnClickListener(_add_comm);

    }//onCreat
    ArrayList<HashMap<String, Object>> comm_list = new ArrayList<>();
    ArrayList<HashMap<String, Object>> sag_list = new ArrayList<>();
    private void initist() {
        SharedPreferences sPref = getSharedPreferences("Save setting", MODE_PRIVATE);
        A_JSon = (sPref.getString("JSONObject", ""));
        try {
            JSONObject asl_json = new JSONObject(A_JSon);
            JSONArray asl_array = asl_json.getJSONArray("CommentsList");
            JSONArray coment_json =  new JSONArray(comentarii);
            for (int i = 0; i < coment_json.length(); i++) {
                value = coment_json.getString(i);
                for (int j = 0; j < asl_array.length(); j++) {
                    JSONObject object = asl_array.getJSONObject(j);
                    String com_name = object.getString("Comment");
                    String uid_comm = object.getString("Uid");
                    Integer price = object.getInt("Price");
                    Boolean paranoid = uid_comm.contains(value);
                    HashMap<String, Object> comm_ = new HashMap<>();
                    if (paranoid) {
                        comm_.put("Comentarii",com_name);
                         comm_.put("Uid_com",uid_comm);
//                         comm_.put("Price",String.valueOf(price));
                        comm_list.add(comm_);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    } //initASLList
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home : {
                Intent result_main = new Intent();
                setResult(RESULT_CANCELED, result_main);
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