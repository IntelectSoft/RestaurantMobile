package com.example.igor.restaurantmobile;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igor.restaurantmobile.AssortimentList.Assortiment;
import com.example.igor.restaurantmobile.AssortimentList.AssortmentService;
import com.example.igor.restaurantmobile.AssortimentList.ClosureType;
import com.example.igor.restaurantmobile.AssortimentList.Comments;
import com.example.igor.restaurantmobile.AssortimentList.ServiceGetAssortmentList;
import com.example.igor.restaurantmobile.AssortimentList.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static com.example.igor.restaurantmobile.utilis.NetworkUtils.generateURL;
import static com.example.igor.restaurantmobile.utilis.NetworkUtils.generateURLASL;
import static com.example.igor.restaurantmobile.utilis.NetworkUtils.generateURLRegDev;
import static com.example.igor.restaurantmobile.utilis.NetworkUtils.getResponseFromDeviceReg;
import static com.example.igor.restaurantmobile.utilis.NetworkUtils.getResponseFromURL;
import static com.example.igor.restaurantmobile.utilis.NetworkUtils.getResponseFromURLASL;

public class TabConnect extends Fragment {
    final Context context = getContext();
    TextView  Bill_close_count,ASL_cont,ID_ET,test;
    EditText portField,IPField;
    final String IP_save = "IP",Port_save = "Port",ID_resp = "ID_Device";
    private String ip_,id_tel,port;
    SharedPreferences sPref;
    ProgressBar progress_Bar;
    int mClosureType = 0,mAssortmentSize = 0;

    int MESSAGE_SUCCES = 0,MESSAGE_RESULT_CODE = 1,MESSAGE_NULL_BODY = 2 , MESSAGE_FAILURE = 3;

    class querryPing extends AsyncTask<URL, String, Boolean> {

        @Override
        protected Boolean doInBackground(URL... urls) {
            String response = "false";
            try {
                response = getResponseFromURL(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Boolean.valueOf(response);
        }

        @Override
        protected void onPostExecute(Boolean response) {
            if (response) {
                IPField.setBackgroundResource(R.drawable.round_edittext_true);
                ASL_cont.setText("0");
                Bill_close_count.setText("0");
                URL generateURLRegDev = generateURLRegDev(ip_, port, id_tel);
                new querryReg().execute(generateURLRegDev);
            } else {
                progress_Bar.setVisibility(ProgressBar.INVISIBLE);
                IPField.setBackgroundResource(R.drawable.round_edittext_false);
            }

        }
    }
    class querryReg extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response = "null";
            try {
                response = getResponseFromDeviceReg(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if (!response.equals("null")) {
                ID_ET.setText(response);

                SharedPreferences.Editor ed = sPref.edit();
                ed.putString(ID_resp, ID_ET.getText().toString());
                ed.apply();

                getAssortment(ip_,port, response);
            } else {
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString(ID_resp, ID_ET.getText().toString());
                ed.apply();
                progress_Bar.setVisibility(ProgressBar.INVISIBLE);
                Toast.makeText(getContext(), "Dispozitivul nu este inregistrat!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_connect, container, false);
        IPField = rootView.findViewById(R.id.et_search_ip);
        portField = rootView.findViewById(R.id.et_search_port);
        test = rootView.findViewById(R.id.btn_test);
        ID_ET = rootView.findViewById(R.id.et_id);
        TextView status_id = rootView.findViewById(R.id.id_status);
        ASL_cont = rootView.findViewById(R.id.txt_asl_count);
        Bill_close_count = rootView.findViewById(R.id.txt_bill_close_type_count);
        progress_Bar = rootView.findViewById(R.id.progressBar_setting);
        sPref = getActivity().getSharedPreferences("Save setting", MODE_PRIVATE);

        loadText();

        final TelephonyManager tm = (TelephonyManager) getActivity().getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        final String deviceId = deviceUuid.toString();
        status_id.setText(deviceId);

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SharedPreferences sPref = getActivity().getSharedPreferences("Save setting", MODE_PRIVATE);
                progress_Bar.setVisibility(ProgressBar.VISIBLE);
                port = portField.getText().toString();
                ip_ = IPField.getText().toString();
                id_tel = deviceId;
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString(IP_save, IPField.getText().toString());
                ed.putString(Port_save, portField.getText().toString());
                ed.apply();
                URL generatedURL = generateURL(ip_,port);
                new querryPing().execute(generatedURL);
            }
        });
        return rootView;

    }
    private void loadText () {
        IPField.setText(sPref.getString(IP_save, ""));
        portField.setText(sPref.getString(Port_save, ""));
        ID_ET.setText(sPref.getString(ID_resp,""));
    }
    private void getAssortment(final String ipAdress, final String portNumber, final String deviceID){
        Thread mGetAssortmentService = new Thread(new Runnable() {
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
                ServiceGetAssortmentList serviceGetAssortmentList = retrofit.create(ServiceGetAssortmentList.class);
                final Call<AssortmentService> assortmentServiceCall = serviceGetAssortmentList.getAssortmentList(deviceID);

                assortmentServiceCall.enqueue(new Callback<AssortmentService>() {
                    @Override
                    public void onResponse(Call<AssortmentService> call, retrofit2.Response<AssortmentService> response) {
                        AssortmentService assortmentService = response.body();
                        if(assortmentService!=null){
                            int mErrorCode = assortmentService.getResult();
                            if(mErrorCode == 0){
                                List<Assortiment> assortimentLists = assortmentService.getAssortimentList();
                                List<Table> tableLists = assortmentService.getTableList();
                                List<ClosureType> closureTypeLists = assortmentService.getClosureTypeList();
                                List<Comments> commentsLists = assortmentService.getCommentsList();

                                ((GlobalVarialbles)getActivity().getApplication()).setAssortmentList(assortimentLists);
                                ((GlobalVarialbles)getActivity().getApplication()).setTableList(tableLists);
                                ((GlobalVarialbles)getActivity().getApplication()).setClosureTypeLists(closureTypeLists);
                                ((GlobalVarialbles)getActivity().getApplication()).setCommentsLists(commentsLists);
                                mClosureType = closureTypeLists.size();
                                mAssortmentSize = assortimentLists.size();
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
                    public void onFailure(Call<AssortmentService> call, Throwable t) {
                        mHandlerBills.obtainMessage(MESSAGE_FAILURE,t.getMessage()).sendToTarget();
                    }
                });
            }
        });
        mGetAssortmentService.start();
    }
    private final Handler mHandlerBills = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_SUCCES) {
                progress_Bar.setVisibility(ProgressBar.INVISIBLE);
                ((GlobalVarialbles)getActivity().getApplication()).setStartWork(true);
                ASL_cont.setText(String.valueOf(mAssortmentSize));
                Bill_close_count.setText(String.valueOf(mClosureType));
            }
            else if (msg.what == MESSAGE_RESULT_CODE) {
                progress_Bar.setVisibility(ProgressBar.INVISIBLE);
                int errorCode = Integer.valueOf(msg.obj.toString());
                switch (errorCode){
                    case 1 : {
                        Snackbar.make(test, "UnknownError", Snackbar.LENGTH_LONG)
                                .show();
                    }break;
                    case 2 : {
                        Snackbar.make(test, "DeviceNotRegistered", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 3 : {
                        Snackbar.make(test, "ShiftIsNotValid", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 4 : {
                        Snackbar.make(test, "BillNotFound", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 5 : {
                        Snackbar.make(test, "ClientNotFound", Snackbar.LENGTH_LONG).show();
                    }break;
                    case 6 : {
                        Snackbar.make(test, "SecurityException", Snackbar.LENGTH_LONG).show();
                    }break;
                }

            }
            else if (msg.what == MESSAGE_NULL_BODY) {
                progress_Bar.setVisibility(ProgressBar.INVISIBLE);
                Snackbar.make(test, "Response body is null: "+ msg.obj.toString(), Snackbar.LENGTH_LONG).show();
            }
            else if (msg.what == MESSAGE_FAILURE){
                progress_Bar.setVisibility(ProgressBar.INVISIBLE);
                Snackbar.make(test, "Failure save bill: "+ msg.obj.toString(), Snackbar.LENGTH_LONG).show();
            }
        }
    };
}
