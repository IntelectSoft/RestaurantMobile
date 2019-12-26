package com.example.igor.restaurantmobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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

import com.example.igor.restaurantmobile.AssortimentList.Assortiment;
import com.example.igor.restaurantmobile.AssortimentList.KitMember;
import com.example.igor.restaurantmobile.CreateNewBill.Order;
import com.example.igor.restaurantmobile.CreateNewBill.OrderParcelable;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapAssortmentGuid;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapCommentGuid;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mMapCommentName;
import static com.example.igor.restaurantmobile.GlobalVarialbles.mSaveOrderIntent;

public class CountActivity extends AppCompatActivity {
    final Context context = this;
    boolean mAllowNonIntegerSales,mCanSaveOrder = true;
    EditText Count_enter;
    ImageButton btn_plus,btn_del;
    TextView mTextViewName,mTextViewPrice,mTextViewComments,btn_save,btn_cancel,add_comment;
    ListView mListViewComments;
    ArrayList<String> mArrayCommentsListAdded  = new ArrayList<>();
    ArrayList<HashMap<String, Object>> mArrayCommentsList = new ArrayList<>();
    ArrayList<HashMap<String, Object>> kitAssortmentList = new ArrayList<>();
    List<KitMember> mKitMebmers = new ArrayList<>();
    int indexKitMember = 0,kitSizeMembers;
    String mGuidAssortment,mPriceLineGuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_count);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_count);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Count_enter = findViewById(R.id.edt_cnt);
        btn_plus = findViewById(R.id.image_btn_add);
        btn_del = findViewById(R.id.image_btn_del);
        btn_save = findViewById(R.id.imageButton_save);
        btn_cancel = findViewById(R.id.imageButton_cancel);
        mTextViewName = findViewById(R.id.name_asl_for_count);
        mTextViewPrice =findViewById(R.id.priceasl_for_count);
        mListViewComments = findViewById(R.id.txt_coment);
        add_comment=findViewById(R.id.img_add_comment);
        mListViewComments.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mTextViewComments=findViewById(R.id.txtViewCom);

        Intent startIntent = getIntent();
        mGuidAssortment = startIntent.getStringExtra(mMapAssortmentGuid);

        final Assortiment assortiment = ((GlobalVarialbles)getApplication()).getAssortmentFromID(mGuidAssortment);
        mAllowNonIntegerSales = assortiment.getAllowNonIntegerSale();
        mPriceLineGuid = assortiment.getPricelineUid();
        List<String> mCommentsList = assortiment.getComments();
        mKitMebmers = assortiment.getKitMembers();

        if(mCommentsList!= null){
            for (String commentGuid:mCommentsList) {
                HashMap<String, Object> commentMap = new HashMap<>();
                commentMap.put(mMapCommentName,((GlobalVarialbles)getApplication()).getCommentName(commentGuid));
                commentMap.put(mMapCommentGuid,commentGuid);
                mArrayCommentsList.add(commentMap);
            }
        }
        mTextViewName.setText(assortiment.getName());
        mTextViewPrice.setText("Pretul: "+ assortiment.getPrice());
        final SimpleAdapter mAdapterComments = new SimpleAdapter(this, mArrayCommentsList,R.layout.comment, new String[]{mMapCommentName}, new int[]{R.id.text_view_comment});
        mListViewComments.setAdapter(mAdapterComments);

        Count_enter.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!mAllowNonIntegerSales) {
                if (!isInteger(Count_enter.getText().toString())) {
                    Count_enter.setError("Numai numere intregi!");
                    mCanSaveOrder = false;
                }
                else mCanSaveOrder = true;
            }
            else mCanSaveOrder = true;
        }
        @Override
        public void afterTextChanged(Editable editable) {
        }
        });
        Count_enter.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_DONE) {
                    saveOrder();
                }
                return false;
            }
        });

        btn_save.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrder();
            }
        });
        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAllowNonIntegerSales) {
                    Double curr = 0.0;
                    try{
                        curr = Double.valueOf(Count_enter.getText().toString());
                    }catch (Exception e){
                        curr = Double.valueOf(Count_enter.getText().toString().replace(",","."));
                    }


                    curr += 1;
                    Count_enter.setText(String.valueOf(curr));
                }
                else{
                    Integer curr = Integer.valueOf(Count_enter.getText().toString());
                    curr += 1;
                    Count_enter.setText(String.valueOf(curr));
                }
            }
        });
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAllowNonIntegerSales) {
                    Double curr = Double.valueOf(String.valueOf(Count_enter.getText()));
                    if (curr - 1 >= 0) curr -= 1;
                    Count_enter.setText(String.valueOf(curr));
                }
                else{
                    int curr = Integer.valueOf(Count_enter.getText().toString());
                    if (curr - 1 >= 0) curr -= 1;
                    Count_enter.setText(String.valueOf(curr));
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

        add_comment.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Comentarii:");
                builder.setMessage("Introduceti comentariul:");
                final EditText input = new EditText(context);
                input.setFocusable(true);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("Adauga", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mInputComment= input.getText().toString();
                        mArrayCommentsListAdded.add(mInputComment);
                        mTextViewComments.append( ", " + mInputComment);
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
        });
        mListViewComments.setOnItemClickListener (new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListViewComments.setItemChecked(position, true);
                mListViewComments.setSelected(true);
                mArrayCommentsListAdded.add((String)mArrayCommentsList.get(position).get(mMapCommentGuid));
            }
        });
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private static boolean isInteger(String s) throws NumberFormatException {
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
    private void saveOrder (){
            if (mCanSaveOrder){
                if(mKitMebmers == null || mKitMebmers.size() == 0 ) {
                    saveOrderOnly();
                }
                else{
                    showKitMembers();
                }
            }
            else Count_enter.setError("Cantitate  incorect!");
    }
    private void showKitMembers() {
        kitAssortmentList = new ArrayList<>();
        kitSizeMembers = mKitMebmers.size();
        if(indexKitMember < kitSizeMembers){
            KitMember kitMember = mKitMebmers.get(indexKitMember);
            List<String> assortmentListKitMember = kitMember.getAssortimentList();
            int kitStepNumber = kitMember.getStepNumber();
            boolean kitMandatory = kitMember.getMandatory();

            for (int i = 0; i < assortmentListKitMember.size(); i++){
                String kitName = ((GlobalVarialbles)getApplication()).getAssortmentName(assortmentListKitMember.get(i));
                HashMap<String, Object> asortimentKitMebmerMap = new HashMap<>();
                asortimentKitMebmerMap.put("Name",kitName);
                asortimentKitMebmerMap.put("Guid",assortmentListKitMember.get(i));
                kitAssortmentList.add(asortimentKitMebmerMap);
            }
            if(assortmentListKitMember.size() == 1 && kitMandatory){
                mArrayCommentsListAdded.add((String)kitAssortmentList.get(0).get("Guid"));
                if (indexKitMember < kitSizeMembers) {
                    indexKitMember += 1;
                    showKitMembers();
                } else {
                    saveOrderOnly();
                }
            }
            else{
                SimpleAdapter adapterKitMebmers = new SimpleAdapter(this, kitAssortmentList, android.R.layout.simple_list_item_1, new String[]{"Name"}, new int[]{android.R.id.text1});
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("Kit step: " + kitStepNumber);
                dialog.setCancelable(false);
                dialog.setAdapter(adapterKitMebmers, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int wich) {
                        mArrayCommentsListAdded.add((String)kitAssortmentList.get(wich).get("Guid"));
                        if (indexKitMember < kitSizeMembers) {
                            indexKitMember += 1;
                            showKitMembers();
                        } else {
                            saveOrderOnly();
                        }
                    }
                });
                if(!kitMandatory){
                    dialog.setPositiveButton("Пропустить шаг", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (indexKitMember < kitSizeMembers) {
                                indexKitMember += 1;
                                showKitMembers();
                            } else {
                                saveOrderOnly();
                            }
                        }
                    });
                }
                dialog.show();
            }

        }
        else{
            saveOrderOnly();
        }

    }
    private void saveOrderOnly() {
        Order newOrder = new Order();
        newOrder.setAssortimentUid(mGuidAssortment);
        double count = 0.0;
        try{
            count = Double.parseDouble(Count_enter.getText().toString());
        }catch (Exception e){
            count = Double.parseDouble(Count_enter.getText().toString().replace(",","."));
        }
        newOrder.setCount(count);
        newOrder.setPriceLineUid(mPriceLineGuid);
        newOrder.setComments(mArrayCommentsListAdded);
        String uid = UUID.randomUUID().toString();
        newOrder.setInternUid(uid);
        newOrder.setUid("00000000-0000-0000-0000-000000000000");
        OrderParcelable saveOrder = new OrderParcelable(newOrder);

        Intent intent = new Intent();
        intent.putExtra(mSaveOrderIntent,saveOrder);
        setResult(RESULT_OK, intent);
        finish();
    }
}