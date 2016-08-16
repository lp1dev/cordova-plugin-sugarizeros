package org.olpcfrance.sugarizer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.net.Uri;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SugarizerOSPlugin extends CordovaPlugin {
    private PackageManager pm;

	public String getDefaultLauncherPackageName(Context context, PackageManager packageManager){
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return resolveInfo.activityInfo.packageName;
	}

	public static boolean isMyAppLauncherDefault(Context appContext, PackageManager packageManager) {
		final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
		final String myPackageName = appContext.getPackageName();

		filter.addCategory(Intent.CATEGORY_HOME);
		List<IntentFilter> filters = new ArrayList<IntentFilter>();
		filters.add(filter);

		List<ComponentName> activities = new ArrayList<ComponentName>();
		if (packageManager == null)
			packageManager = appContext.getPackageManager();

		packageManager.getPreferredActivities(filters, activities, null);
		for (ComponentName activity : activities) {
			if (myPackageName.equals(activity.getPackageName())) {
				return true;
			}
		}
		return false;
	}

    private void getApps(CallbackContext callbackContext, int flags){
	JSONArray output = new JSONArray();
	CordovaActivity activity = (CordovaActivity) this.cordova.getActivity();
		final String packageName = activity.getPackageName();
	pm = activity.getPackageManager();

	List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
	for (int i = 0;i < packages.size(); i++){
	    ApplicationInfo app = packages.get(i);
	    if((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1) {
	    } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
	    } else if (!packageName.equals(packages.get(i).packageName)){
		JSONObject application = new JSONObject();
			try {
				PackageInfo packageInfo = pm.getPackageInfo(packages.get(i).packageName, 0);
				try {
					application.put("packageName", packages.get(i).packageName);
					application.put("flags", packages.get(i).flags);
					application.put("name", pm.getApplicationLabel(packages.get(i)));
					application.put("icon", IconCacheManager.getIcon(cordova.getActivity(), pm, packages.get(i).packageName));
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


	private ComponentName resetDefaultLauncherSettings(Context context){


		if (pm == null) pm = context.getPackageManager();
		ComponentName mockupComponent = new  ComponentName(MainActivity.class.getPackage().getName(), MainActivity.class.getName());

		pm.setComponentEnabledSetting(mockupComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(startMain);

		pm.setComponentEnabledSetting(mockupComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
		return mockupComponent;
//		pm.setComponentEnabledSetting(mockupComponent,  PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
	}

    private void runSettings(CallbackContext callbackContext){
	cordova.getActivity().startActivity(
			new Intent(Settings.ACTION_SETTINGS));
		callbackContext.success();
    }


    private void runActivity(CallbackContext callbackContext, String packageName){
	if (pm == null){
	    CordovaActivity activity = (CordovaActivity) this.cordova.getActivity();
	    pm = activity.getPackageManager();
	}
	Intent LaunchIntent = pm.getLaunchIntentForPackage(packageName);
	this.cordova.getActivity().startActivity(LaunchIntent);
		callbackContext.success();
    }

	private void openAppSettings(Context context){
		String packageName =  getDefaultLauncherPackageName(context, pm);
			if (context == null || packageName == null || packageName.equals("android")) {
				return;
			}
			final Intent i = new Intent();
			i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			i.addCategory(Intent.CATEGORY_DEFAULT);
			i.setData(Uri.parse("package:" + packageName));
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			context.startActivity(i);
	}

	private void openChooseLauncherPopup(Context context){
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(startMain);
	}

    private void chooseLauncher(CallbackContext callbackContext, Context appContext, boolean reset){
		boolean isDefault = isMyAppLauncherDefault(appContext, pm);
		if (reset){
			ComponentName componentName = resetDefaultLauncherSettings(appContext);
			pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

		}
		if (isDefault)
			pm.clearPackagePreferredActivities(appContext.getPackageName());
		openChooseLauncherPopup(appContext);
		if (reset && isDefault == isMyAppLauncherDefault(appContext, pm)){
			openAppSettings(appContext);
		}
		callbackContext.success();
	}

	private void isMyAppLauncherDefault(CallbackContext callbackContext, Context appContext) {
		final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
		final String myPackageName = appContext.getPackageName();

		filter.addCategory(Intent.CATEGORY_HOME);
		List<IntentFilter> filters = new ArrayList<IntentFilter>();
		filters.add(filter);

		List<ComponentName> activities = new ArrayList<ComponentName>();
		if (pm == null)
			pm = appContext.getPackageManager();

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
	else if (action.equals("chooseLauncher")){
	    this.chooseLauncher(callbackContext, cordova.getActivity(), true);
	}
	else if (action.equals("selectLauncher")){
	    this.chooseLauncher(callbackContext, cordova.getActivity(), false);
	}
    else if (action.equals("joinNetwork")) {
		SugarWifiManager.joinNetwork(args.getString(0), args.getString(1), args.getString(2), cordova.getActivity());
	}
	else if (action.equals("getInt")){
		SharedPreferencesManager.getInt(callbackContext, cordova.getActivity(), args.getString(0));
	}
	else if (action.equals("putInt")){
		SharedPreferencesManager.putInt(cordova.getActivity(), args.getString(0), args.getInt(1));
	}
	return false;
    }
}
