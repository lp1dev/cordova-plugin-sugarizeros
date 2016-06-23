package org.olpcfrance.sugarizer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.util.Base64;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SugarizerOSPlugin extends CordovaPlugin {
    private PackageManager pm;

    private String drawableToBase64(Drawable drawable){
	BitmapDrawable bitDw = ((BitmapDrawable) drawable);
	Bitmap bitmap = bitDw.getBitmap();
	ByteArrayOutputStream stream = new ByteArrayOutputStream();
	bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
	byte[] bitmapByte = stream.toByteArray();
	return String.format("data:image/png;base64,%s", Base64.encodeToString(bitmapByte, Base64.DEFAULT));
    }

    private String getIcon(String packageName){
	try {
	    if (pm == null){
		CordovaActivity activity = (CordovaActivity) this.cordova.getActivity();
		pm = activity.getPackageManager();
	    }
	    return this.drawableToBase64(pm.getApplicationIcon(packageName));
	} catch (PackageManager.NameNotFoundException e) {
	    e.printStackTrace();
	}
	return "";
    }

    private void getApps(CallbackContext callbackContext, int flags){
	JSONArray output = new JSONArray();
	CordovaActivity activity = (CordovaActivity) this.cordova.getActivity();
	pm = activity.getPackageManager();

	List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
	for (int i = 0;i < packages.size(); i++){
	    ApplicationInfo app = packages.get(i);
	    if((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1) {
	    } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
	    } else {
		JSONObject application = new JSONObject();
			try {
				PackageInfo packageInfo = pm.getPackageInfo(packages.get(i).packageName, 0);
				try {
					application.put("packageName", packages.get(i).packageName);
					application.put("flags", packages.get(i).flags);
					application.put("name", pm.getApplicationLabel(packages.get(i)));
					application.put("icon", getIcon(packages.get(i).packageName));
					application.put("version", packageInfo.versionName);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
		output.put(application);
	    }
	}
	callbackContext.success(output);
    }


    private void runSettings(CallbackContext callbackContext){
	cordova.getActivity().startActivity(
			new Intent(Settings.ACTION_SETTINGS));
    }

    private void runActivity(CallbackContext callbackContext, String packageName){
	if (pm == null){
	    CordovaActivity activity = (CordovaActivity) this.cordova.getActivity();
	    pm = activity.getPackageManager();
	}
	Intent LaunchIntent = pm.getLaunchIntentForPackage(packageName);
	this.cordova.getActivity().startActivity(LaunchIntent);
    }

	private void isMyAppLauncherDefault(CallbackContext callbackContext, Context appContext) {
		final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
		filter.addCategory(Intent.CATEGORY_HOME);

		List<IntentFilter> filters = new ArrayList<IntentFilter>();
		filters.add(filter);

		final String myPackageName = appContext.getPackageName();
		List<ComponentName> activities = new ArrayList<ComponentName>();
		if (pm == null)
			pm = (PackageManager) appContext.getPackageManager();

		// You can use name of your package here as third argument
		pm.getPreferredActivities(filters, activities, null);

		for (ComponentName activity : activities) {
			if (myPackageName.equals(activity.getPackageName())) {
				callbackContext.success(1);
			}
		}
		callbackContext.success(0);
	}

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
	if (action.equals("runActivity")){
	    this.runActivity(callbackContext, args.getString(0));
	}
	else if (action.equals("runSettings")){
	    this.runSettings(callbackContext);
	}
	else if (action.equals("apps")) {
	    this.getApps(callbackContext, args.getInt(0));
	}
	else if (action.equals("scanWifi")) {
		SugarWifiManager.scanWifi(callbackContext, cordova.getActivity());
		return true;
	}
	else if (action.equals("isDefaultLauncher")){
		this.isMyAppLauncherDefault(callbackContext, cordova.getActivity());
	}
	return false;
    }
}
