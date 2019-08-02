package com.example.igor.restaurantmobile.utilis;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    String Json_save;

    public static URL generateURL (String ip,String port){
        Uri builtUri;
        builtUri = Uri.parse("http://" + ip + ":" + port + "/MobileCash/json/Ping")
                .buildUpon()
                .build();
        URL url =null;
        try {
            url= new URL (builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;

    }
    public static URL generateURLRegDev (String ip,String port,String dev_id){
        Uri reg_dev;
        reg_dev = Uri.parse("http://" + ip + ":" + port + "/MobileCash/json/RegisterDevice?deviceId="+ dev_id)
                .buildUpon()
                .build();
        URL reg_dev_url =null;
        try {
            reg_dev_url= new URL (reg_dev.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return reg_dev_url;

    }
    public static URL generateURLASL (String ip,String port, String id){
        Uri getUri;
        getUri =Uri.parse("http://" + ip + ":" + port + "/MobileCash/json/GetAssortimentList?deviceId=" + id)
                .buildUpon()
                .build();
        URL url_asl = null;
        try {
            url_asl =new URL (getUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url_asl;

    }
    public static String getResponseFromURL (URL url) throws IOException{
        String resp = "false";
        HttpURLConnection urlConnection =(HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(5000);
        try {
                InputStream in = urlConnection.getInputStream();
                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return resp;
                }

        } finally{
            urlConnection.disconnect();
        }
    }
    public static String getResponseFromURLASL (URL url_asl) throws IOException{
        HttpURLConnection urlaslConnection =(HttpURLConnection) url_asl.openConnection();
        urlaslConnection.setRequestMethod("GET");
        urlaslConnection.setConnectTimeout(6000);
        try {
            InputStream in2 = urlaslConnection.getInputStream();

            Scanner scanner2 = new Scanner(in2);
            scanner2.useDelimiter("\\A");

            boolean hasInput = scanner2.hasNext();
            if (hasInput) {
                return scanner2.next();
            } else {
                return "";
            }
        }  finally {
            urlaslConnection.disconnect();
        }

    }
    public static String getResponseFromDeviceReg (URL reg_dev) throws IOException{
        String device = "null";
        HttpURLConnection reg_dev_Connection =(HttpURLConnection) reg_dev.openConnection();
        reg_dev_Connection.setConnectTimeout(5000);
        reg_dev_Connection.setRequestMethod("GET");
        try {
            InputStream in2 = reg_dev_Connection.getInputStream();

            Scanner scanner2 = new Scanner(in2);
            scanner2.useDelimiter("\\A");

            boolean hasInput = scanner2.hasNext();
            if (hasInput) {
                return scanner2.next();
            } else {
                return device;
            }
        }  finally {
            reg_dev_Connection.disconnect();
        }

    }

}
