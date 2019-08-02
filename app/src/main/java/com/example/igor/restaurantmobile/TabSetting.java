package com.example.igor.restaurantmobile;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.example.igor.restaurantmobile.utilis.NetworkUtils.generateURL;
import static com.example.igor.restaurantmobile.utilis.NetworkUtils.generateURLASL;
import static com.example.igor.restaurantmobile.utilis.NetworkUtils.generateURLRegDev;
import static com.example.igor.restaurantmobile.utilis.NetworkUtils.getResponseFromDeviceReg;
import static com.example.igor.restaurantmobile.utilis.NetworkUtils.getResponseFromURL;
import static com.example.igor.restaurantmobile.utilis.NetworkUtils.getResponseFromURLASL;

public class TabSetting extends Fragment {

    EditText IPField;
    TextView ID_ET;
    TextView ASL_cont;
    TextView Folder_count, Bill_close_count;
    EditText portField;
    final String IP_save = "IP";
    final String Port_save = "Port";
    final String ID_resp = "ID_Mob";
    final String Response = "Response";
    final Context context = getContext();
    private String port;
    private String ip_;
    private String id_tel;
    private String id_in_base;
    public String asl_JSon;
    SharedPreferences sPref;
    Integer fol, asl, close_type;
    HashMap<String, String> assortiment;
    ProgressBar progress_Bar;
    int resp = 1;

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
                // IPField.setBackgroundColor(Color.rgb( 0, 255, 0));
                IPField.setBackgroundResource(R.drawable.round_edittext_true);
                ASL_cont.setText("0");
                Folder_count.setText("0");
                Bill_close_count.setText("0");
                URL generateURLRegDev = generateURLRegDev(ip_, port, id_tel);
                new querryReg().execute(generateURLRegDev);
            } else {
                progress_Bar.setVisibility(ProgressBar.INVISIBLE);
                IPField.setBackgroundResource(R.drawable.round_edittext_false);
                // IPField.setBackgroundColor(Color.rgb(255,0,0));
            }

        }
    }

    class queryASL extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String response2 = "";
            try {
                response2 = getResponseFromURLASL(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response2;
        }

        @Override
        protected void onPostExecute(String response2) {
            progress_Bar.setVisibility(ProgressBar.INVISIBLE);
            if (!response2.equals("")) {
                try {
                    JSONObject response = new JSONObject(response2);
                    int result = response.getInt("Result");
                    if (result == 0) {
                        sPref = getActivity().getSharedPreferences("Save setting", MODE_PRIVATE);
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("JSONObject", response2);
                        ed.putBoolean("Start",true);
                        ed.apply();
                        counterASL();
                        TableList_to_bill();
                        Folder_count.setText(String.valueOf(fol));
                        ASL_cont.setText(String.valueOf(asl));
                        Bill_close_count.setText(String.valueOf(close_type));
                        resp = 0;
                    } else if (result == 2) {
                        resp = 1;
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putBoolean("Start",false);
                        ed.apply();
                        android.app.AlertDialog.Builder eroare = new android.app.AlertDialog.Builder(getContext());
                        eroare.setTitle("Atentie!");
                        eroare.setMessage("Asortimentul nu a fost salvat!Inregistrati dispozitivul cu numarul "+id_in_base +" la casa!");
                        eroare.setCancelable(false);
                        eroare.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        eroare.show();
                    } else {
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putBoolean("Start",false);
                        ed.apply();
                        Toast.makeText(getContext(), "Asortimentul nu a fost salvat!Codul erorii: " + result, Toast.LENGTH_SHORT).show();
                        resp = 1;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "Nu este raspuns de la serviciu!", Toast.LENGTH_SHORT).show();
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
                id_in_base = response;
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString(ID_resp, ID_ET.getText().toString());

                ed.apply();
                URL generatedURLASL = generateURLASL(ip_, port, id_in_base);
                new queryASL().execute(generatedURLASL);
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
        View rootView = inflater.inflate(R.layout.activity_setting, container, false);
        IPField = rootView.findViewById(R.id.et_search_ip);
        portField = rootView.findViewById(R.id.et_search_port);
        TextView test = rootView.findViewById(R.id.btn_test);
        ID_ET = rootView.findViewById(R.id.et_id);
        TextView status_id = rootView.findViewById(R.id.id_status);
        Folder_count = rootView.findViewById(R.id.txt_folder_count);
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
    private void counterASL() {
        fol = 0;
        asl=0;
        close_type=0;
        sPref = getActivity().getSharedPreferences("Save setting", MODE_PRIVATE);
        asl_JSon= sPref.getString("JSONObject","");
        SharedPreferences asl_u = getActivity().getSharedPreferences("Assortiment",MODE_PRIVATE);
        SharedPreferences.Editor incom = asl_u.edit();
        SharedPreferences asl_price = getActivity().getSharedPreferences("Assortiment_price",MODE_PRIVATE);
        SharedPreferences.Editor incom_price = asl_price.edit();
        SharedPreferences asl_sales = getActivity().getSharedPreferences("Assortiment_Sales",MODE_PRIVATE);
        SharedPreferences.Editor incom_sales = asl_sales.edit();
        try {
            JSONObject asl_json = new JSONObject(asl_JSon);
            JSONArray asl_array = asl_json.getJSONArray("AssortimentList");

            for (int i = 0; i < asl_array.length(); i++) {
                JSONObject object = asl_array.getJSONObject(i);
                Boolean is_folder = object.getBoolean("IsFolder");
                assortiment = new HashMap<>();
                if (!is_folder){
                    asl+=1;
                    String uids= object.getString("Uid");
                    String names = object.getString("Name");
                    String prices= object.getString("Price");
                    Boolean alow_integer =object.getBoolean("AllowNonIntegerSale");
                    incom_sales.putBoolean(uids,alow_integer);
                    incom.putString(uids,names);
                    incom_price.putString(uids,prices);
                }else {
                    fol+=1;
                }
            }
            JSONArray close_types = asl_json.getJSONArray("ClosureTypeList");
            close_type=close_types.length();
            incom.apply();
            incom_price.apply();
            incom_sales.apply();
        } catch (JSONException e) {
            final AlertDialog.Builder eroare = new AlertDialog.Builder(context);
            eroare.setTitle("Atentie!");
            eroare.setMessage("Eroare! Mesajul erorii:"+ "\n"+ e);
            eroare.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            eroare.show();
        }

    } //initASLList
    private void TableList_to_bill() {
        String tables_name;
        sPref = getActivity().getSharedPreferences("Save setting", MODE_PRIVATE);
        asl_JSon= sPref.getString("JSONObject","");
        SharedPreferences tables = getActivity().getSharedPreferences("Tables",MODE_PRIVATE);
        SharedPreferences.Editor tabl= tables.edit();
        try {
            JSONObject table_json = new JSONObject(asl_JSon);
            JSONArray tables_array = table_json.getJSONArray("TableList");
            tabl.putInt("CountTable",tables_array.length());
            for (int i = 0; i < tables_array.length(); i++) {
                JSONObject object = tables_array.getJSONObject(i);
                tables_name = object.getString("Name");
                String uid_table = object.getString("Uid");
                tabl.putString(uid_table,tables_name);
            }
            tabl.apply();
        } catch (JSONException e) {
            final AlertDialog.Builder eroare = new AlertDialog.Builder(context);
            eroare.setTitle("Atentie!");
            eroare.setMessage("Eroare! Mesajul erorii:"+ "\n"+ e);
            eroare.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            eroare.show();
        }
    } //TablesList

}
