package org.olpcfrance.sugarizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public  class SugarWifiManager{
    static final String WEP = "[WEP";
    static final String WPA2 = "[WPA2";
    static final String WPA = "[WPA";

    public static void joinNetwork(String SSID, String pass, String capabilities, Context context) {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + SSID + "\"";
        if (capabilities.contains(WEP)){
            conf.wepKeys[0] = "\"" + pass + "\"";
            conf.wepTxKeyIndex = 0;
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        }
        else if (capabilities.contains(WPA2)){
            conf.preSharedKey = "\""+ pass +"\"";
        }
        else if (capabilities.contains(WPA)){
            conf.preSharedKey = "\""+ pass +"\"";
        }
        else {
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + SSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
    }

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
                        object.put("capabilities", scanResult.capabilities);
                        object.put("RSSI", scanResult.level);
                        output.put(object);
                    }
                    PluginResult result = new PluginResult(PluginResult.Status.OK, output);
                    result.setKeepCallback(true);
                    callbackContext.sendPluginResult(result);
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
