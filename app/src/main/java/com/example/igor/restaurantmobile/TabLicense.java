package com.example.igor.restaurantmobile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class TabLicense extends Fragment {
    TextView ShowCode;
    EditText EnterCode;
    Button Verific;
    SharedPreferences sPref;
    Boolean lic;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_license, container, false);

        ShowCode =rootView.findViewById(R.id.code_lic);
        EnterCode=rootView.findViewById(R.id.et_key);
        Verific=rootView.findViewById(R.id.btn_verify);
        sPref = getActivity().getSharedPreferences("Save setting", MODE_PRIVATE);

        final TelephonyManager tm = (TelephonyManager) getActivity().getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            }
        }

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
                }else{
                    EnterCode.setBackgroundResource(R.drawable.round_edittext_false);
                    ed.putBoolean("Key",false);
                    ed.apply();
                    lic=false;
                }
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
