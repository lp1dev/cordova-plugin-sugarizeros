package org.olpcfrance.sugarizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class WifiPlugin extends CordovaPlugin {
    WifiManager mWifiManager;

    public void scanWifi(final CallbackContext callbackContext){

        IntentFilter i = new IntentFilter();
        i.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        cordova.getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                JSONArray output = new JSONArray();
                WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                List<ScanResult> scanResults = mWifiManager.getScanResults ();
                try {
                    for (int i = 0; i < scanResults.size(); i++){
                        JSONObject object = new JSONObject();
                        ScanResult scanResult = scanResults.get(i);
                        object.put("SSID", scanResult.SSID);
                        object.put("BSSID", scanResult.BSSID);
                        object.put("capabalities", scanResult.capabilities);
                        object.put("RSSI", scanResult.level);
                        output.put(object);
                    }
                    callbackContext.success(output);
                }
                catch (JSONException e) {
                    callbackContext.error("Android:"+e.toString());
                    e.printStackTrace();
                }
            }
        }
                , i);

        mWifiManager = (WifiManager) cordova.getActivity().getSystemService(Context.WIFI_SERVICE);
        mWifiManager.startScan();

    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
	Log.e("WifiPlugin", action);
	if (action.equals("scanWifi")){
            this.scanWifi(callbackContext);
        }
        return false;
    }
}
