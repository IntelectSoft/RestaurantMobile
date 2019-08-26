package com.example.igor.restaurantmobile;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

public class TabOther extends Fragment {
    TextView ShowCode;
    EditText EnterCode;
    Button Verific;
    SharedPreferences sPref;
    Boolean lic;
    Spinner spinner_time_update;
    String[] mTypeTimeList = {"1 min.", "5 min.", "10 min.","Manual"};

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_other, container, false);

        ShowCode =rootView.findViewById(R.id.code_lic);
        EnterCode=rootView.findViewById(R.id.et_key);
        Verific=rootView.findViewById(R.id.btn_verify);
        spinner_time_update = rootView.findViewById(R.id.spinner_update_time_bill);
        sPref = getActivity().getSharedPreferences("Save setting", MODE_PRIVATE);

        final TelephonyManager tm = (TelephonyManager) getActivity().getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            }
        }

        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mTypeTimeList);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_time_update.setAdapter(adapterType);

        int period = sPref.getInt("TimeUpdate",0);
        if(period == 0)
            spinner_time_update.setSelection(3);
        else if( period == 60000)
            spinner_time_update.setSelection(0);
        else if (period == 300000)
            spinner_time_update.setSelection(1);
        else if (period == 600000)
            spinner_time_update.setSelection(2);

        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        deviceId=deviceId.replace("-","");
        String code = deviceId.substring(10,18);
        ShowCode.setText(code.toUpperCase());
        final String enternKey = md5(code.toUpperCase() + "ENCEFALOMIELOPOLIRADICULONEVRITA");
        EnterCode.setText(sPref.getString("KeyText",""));

        Verific.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = EnterCode.getText().toString().toUpperCase();
                SharedPreferences.Editor ed = sPref.edit();
                if (Test(key,enternKey)){
                    EnterCode.setBackgroundResource(R.drawable.round_edittext_true);
                    ed.putBoolean("Key",true);
                    ed.putString("KeyText",key);
                    ed.apply();
                    lic=true;
                }
                else{
                    EnterCode.setBackgroundResource(R.drawable.round_edittext_false);
                    ed.putBoolean("Key",false);
                    ed.apply();
                    lic=false;
                }
            }
        });

        spinner_time_update.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                SharedPreferences.Editor ed = sPref.edit();
                if(position == 0){
                    ed.putInt("TimeUpdate",60000);
                    ed.apply();
                }
                else if(position == 1){
                    ed.putInt("TimeUpdate",300000);
                    ed.apply();
                }
                else if(position == 2){
                    ed.putInt("TimeUpdate",600000);
                    ed.apply();
                }
                else if(position == 3){
                    ed.putInt("TimeUpdate",0);
                    ed.apply();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return rootView;
    }
    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            byte[] encode = Base64.encode(messageDigest,0);
            String respencode = new String(encode).toUpperCase();
            // Create String
            String digits="";
            for (int i = 0; i < respencode.length(); i++) {
                char chrs = respencode.charAt(i);
                if (!Character.isDigit(chrs))
                    digits = digits+chrs;
            }
            String keyLic = "";
            for (int k=0;k<digits.length();k++){
                if (Character.isLetter(digits.charAt(k))){
                    keyLic=keyLic + digits.charAt(k);
                }
            }
            keyLic=keyLic.substring(0,8);

            return keyLic;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    public boolean Test (String key,String entern_key){
        return key.equals(entern_key);
    }

}
