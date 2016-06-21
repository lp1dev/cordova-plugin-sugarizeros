package org.olpcfrance.sugarizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public  class SugarWifiManager{

    public static void scanWifi(final CallbackContext callbackContext, Context appContext){
        WifiManager mWifiManager;

        IntentFilter i = new IntentFilter();
        i.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        appContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                JSONArray output = new JSONArray();
                WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                List<ScanResult> scanResults = mWifiManager.getScanResults();
                try {
                    for (int i = 0; i < scanResults.size(); i++) {
                        JSONObject object = new JSONObject();
                        ScanResult scanResult = scanResults.get(i);
                        object.put("SSID", scanResult.SSID);
                        object.put("BSSID", scanResult.BSSID);
                        object.put("capabalities", scanResult.capabilities);
                        object.put("RSSI", scanResult.level);
                        output.put(object);
                    }
                    callbackContext.success(output);
                } catch (JSONException e) {
                    callbackContext.error("Android:" + e.toString());
                    e.printStackTrace();
                }
            }
        }
                , i);

        mWifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
        mWifiManager.startScan();
    }

}
