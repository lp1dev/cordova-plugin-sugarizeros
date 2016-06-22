package org.olpcfrance.sugarizer;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
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

    private Bitmap getBitmap(Drawable drawable){
	Bitmap bitmap = null;

	if (drawable instanceof BitmapDrawable) {
	    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
	    if(bitmapDrawable.getBitmap() != null) {
		return bitmapDrawable.getBitmap();
	    }
	}

	if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
	    bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
	} else {
	    bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
	}

	Canvas canvas = new Canvas(bitmap);
	drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	drawable.draw(canvas);
	return bitmap;
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
		    application.put("packageName", packages.get(i).packageName);
		    application.put("flags", packages.get(i).flags);
		    application.put("name", pm.getApplicationLabel(packages.get(i)));
		    application.put("icon", getIcon(packages.get(i).packageName));
		} catch (JSONException e) {
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
	this.cordova.getActivity().startActivity( LaunchIntent );
    }

    private void scanNetwork(CallbackContext callbackContext, Context context){
	callbackContext.success(["coucou", "test"]);
    }
    
    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
	if (action.equals("runActivity")){
	    this.runActivity(callbackContext, args.getString(0));
	}
	if (action.equals("runSettings")){
	    this.runSettings(callbackContext);
	}
	if (action.equals("apps")) {
	    this.getApps(callbackContext, args.getInt(0));
	}
	if (action.equals("scanWifi")) {
	    this.scanNetwork(callbackContext, cordova.getActivity());
	}
	if (action.equals("scanNetwork")){
	    this.scanNetwork(callbackContext, cordova.getActivity());
	}
	return false;
    }
}
