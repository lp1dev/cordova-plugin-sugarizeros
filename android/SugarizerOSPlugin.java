package org.olpcfrance.sugarizer;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.List;

/**
 * This class echoes a string called from JavaScript.
 */

public class SugarizerOSPlugin extends CordovaPlugin {

    private void getApps(CallbackContext callbackContext){
	String output = "";
	CordovaActivity activity = (CordovaActivity) this.cordova.getActivity();
	final PackageManager pm = activity.getPackageManager();
	//get a list of installed apps.
	List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
	for (int i = 0;i < packages.size(); i++){
	    output += packages.toString();
	}
	callbackContext.success(output);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
	if (action.equals("echo")) {
	    String message = args.getString(0);
	    this.echo(message, callbackContext);
	    return true;
	}
	if (action.equals("apps")) {
	    this.getApps(callbackContext);
	}
	return false;
    }

    private void echo(String message, CallbackContext callbackContext) {
	if (message != null && message.length() > 0) {
	    callbackContext.success(message);
	} else {
	    callbackContext.error("Expected one non-empty string argument.");
	}
    }
}
