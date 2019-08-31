package com.example.igor.restaurantmobile;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

public class TabOther extends Fragment {
    TextView ShowCode;
    Context context1;
    EditText EnterCode;
    Button Verific,btn_update,btn_downgrade;
    SharedPreferences sPref;
    Boolean lic;
    Spinner spinner_time_update;
    String[] mTypeTimeList = {"1 min.", "5 min.", "10 min.","Manual"};

    public static final int progress_bar_type = 11;
    private static String file_url_apk = "http://edi.md/androidapps/RestaurantMobile.apk";
    private static String file_url_apk_old = "http://edi.md/androidapps/RestaurantMobileOld.apk";
    private static String file_version_url = "http://edi.md/androidapps/RestaurantMobileVersion.txt";
    private static String file_version_url_old = "http://edi.md/androidapps/RestaurantMobileVersionOld.txt";

    ProgressDialog pDialog;

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        context1 = context;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_other, container, false);

        context1 = rootView.getContext();
        ShowCode =rootView.findViewById(R.id.code_lic);
        EnterCode=rootView.findViewById(R.id.et_key);
        Verific=rootView.findViewById(R.id.btn_verify);
        spinner_time_update = rootView.findViewById(R.id.spinner_update_time_bill);
        btn_downgrade = rootView.findViewById(R.id.btn_downgrade);
        btn_update = rootView.findViewById(R.id.btn_updae);

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

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog = new ProgressDialog(getContext());
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                new DownloadVersionFileFromURL().execute(file_version_url);
            }
        });

        btn_downgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog = new ProgressDialog(getContext());
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                new DownloadVersionOLDFileFromURL().execute(file_version_url_old);
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

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Downloading file. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + "/IntelectSoft/RestaurantMobile.apk");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            pDialog.dismiss();

            File file = new File(Environment.getExternalStorageDirectory()+ "/IntelectSoft","/RestaurantMobile.apk"); // mention apk file path here
            Uri uri = FileProvider.getUriForFile(context1, BuildConfig.APPLICATION_ID + ".provider",file);
            if (file.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "file not exist", Toast.LENGTH_SHORT).show();
            }


        }

    }
    class DownloadFileOLDFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Downloading file. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + "/IntelectSoft/RestaurantMobileOld.apk");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            pDialog.dismiss();

            File file = new File(Environment.getExternalStorageDirectory()+ "/IntelectSoft","/RestaurantMobileOld.apk"); // mention apk file path here
            Uri uri = FileProvider.getUriForFile(context1, BuildConfig.APPLICATION_ID + ".provider",file);
            if (file.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "file not exist", Toast.LENGTH_SHORT).show();
            }


        }

    }
    class DownloadVersionFileFromURL extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.setConnectTimeout(2000);
                conection.connect();

                int lenghtOfFile = conection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + "/IntelectSoft/RestaurantMobileVersion.txt");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }finally{
                pDialog.dismiss();
            }
            return null;
        }
        protected void onProgressUpdate(String... progress) {
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(final String file_url) {

            File file = new File(Environment.getExternalStorageDirectory()+ "/IntelectSoft","/RestaurantMobileVersion.txt"); // mention apk file path here
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            }
            catch (IOException e) {
                Toast.makeText(getActivity(), "Exception read file", Toast.LENGTH_SHORT).show();
            }
            pDialog.dismiss();
            String version ="0.0";
            try {
                PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                version = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle("Atentie!");
            alertDialog.setMessage("Versiune pe server: " + text.toString()+ "\n"+"Versiune locala: " + version);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new DownloadFileFromURL().execute(file_url_apk);
                }
            });
            alertDialog.setNegativeButton("Renunt", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    pDialog.dismiss();
                }
            });
            alertDialog.show();
        }

    }
    class DownloadVersionOLDFileFromURL extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.setConnectTimeout(2000);
                conection.connect();

                int lenghtOfFile = conection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + "/IntelectSoft/RestaurantMobileVersionOld.txt");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }finally{
                pDialog.dismiss();
            }
            return null;
        }
        protected void onProgressUpdate(String... progress) {
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(final String file_url) {
            File file = new File(Environment.getExternalStorageDirectory()+ "/IntelectSoft","/RestaurantMobileVersionOld.txt"); // mention apk file path here
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            }
            catch (IOException e) {
                Toast.makeText(getActivity(), "Exception read file", Toast.LENGTH_SHORT).show();
            }
            pDialog.dismiss();
            String version ="0.0";
            try {
                PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                version = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle("Atentie!");
            alertDialog.setMessage("Versiune pe server: " + text.toString()+ "\n"+"Versiune locala: " + version);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new DownloadFileOLDFromURL().execute(file_url_apk_old);
                }
            });
            alertDialog.setNegativeButton("Renunt", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    pDialog.dismiss();
                }
            });
            alertDialog.show();
        }

    }


}
