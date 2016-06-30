var sugarizerOS = {};
var exec = require('cordova/exec');

sugarizerOS.max
sugarizerOS.applicationsLoaded = false;
sugarizerOS.isDefaultLauncher = false;
sugarizerOS.networks = [];

sugarizerOS.setApplicationsLoaded = function(value){
    sugarizerOS.applicationsLoaded = true;
}

sugarizerOS.getEncryptionString = function(capabilities){
    var WEP = "[WEP"
    var WPA2 = "[WPA2"
    var WPA = "[WPA"
    if (capabilities.indexOf(WEP) != -1)
	return "WEP";
    else if (capabilities.indexOf(WPA2) != -1)
	return "WPA2";
    else if (capabilities.indexOf(WPA) != -1)
	return "WPA";
    return "OPEN";
}

sugarizerOS.joinNetwork = function(SSID, pass, capabilities, onSuccess, onFailure){
    exec(onSuccess, onFailure, "SugarizerOSPlugin", "joinNetwork", [SSID, pass, capabilities]);
}

sugarizerOS.init = function(){
    if (window){
	window.sugarizerOS = sugarizerOS;
	sugarizerOS.checkIfDefaultLauncher();
	console.log("SugarizerOS initialized");
    }
    else{
	console.log("No window to initialize sugarizerOS");
    }
}

sugarizerOS.chooseLauncher = function(){
    exec(null, null, "SugarizerOSPlugin", "chooseLauncher", []);
}

sugarizerOS.checkIfDefaultLauncher = function(){
    exec(sugarizerOS.setIsDefaultLauncher, null, "SugarizerOSPlugin", "isDefaultLauncher", []);
}

sugarizerOS.setIsDefaultLauncher = function(value){
    if (value)
	sugarizerOS.isDefaultLauncher = true;
    else
	sugarizerOS.isDefaultLauncher = false;
}

sugarizerOS.echo = function(onSuccess, onFailure, string){
    exec(onSuccess, onFailure, "SugarizerOSPlugin", "echo", string);
}

sugarizerOS.getAndroidApplications = function(onSuccess, onFailure, flags){
    exec(onSuccess, onFailure, "SugarizerOSPlugin", "apps", flags);
}

sugarizerOS.updateNetworks = function(networks){
    sugarizerOS.networks = networks;
}

sugarizerOS.scanWifi = function(){
    exec(sugarizerOS.updateNetworks, null, "SugarizerOSPlugin", "scanWifi", []);
}

sugarizerOS.runActivity = function(packageName){
    exec(null, null, "SugarizerOSPlugin", "runActivity", [packageName])
}

sugarizerOS.runSettings = function(){
    exec(null, null, "SugarizerOSPlugin", "runSettings", []);
}

sugarizerOS.applicationsToActivities = function(applications){
    var activities = [];
    for (i = 0; i < applications.length; i++){
	activities.push({
	    activityId: null,
	    directory: null,
	    favorite: false,
	    icon: applications[i].icon,
	    id: applications[i].packageName,
	    instances: [],
	    name: applications[i].name,
	    version: applications[i].version,
	    type: "native"});
    }
    return activities;
}

sugarizerOS.initActivitiesPreferences = function(){
    if (!sugarizerOS.applicationsLoaded){
	sugarizerOS.getAndroidApplications(function(applications){
	    console.log("getAndroidapplicationsCalled");
	    var activities = preferences.getActivities();
	    for (i = 0; i < activities.length; i++)
		if (activities[i].id == applications[0].packageName)
		    sugarizerOS.setApplicationsLoaded(true);
	    if (!sugarizerOS.applicationsLoaded){
		activities = sugarizerOS.applicationsToActivities(applications).concat(activities);
	    preferences.setActivities(activities);
		sugarizerOS.setApplicationsLoaded(true);
	    }
	}
					   , sugarizerOS.log, [0]);
    }
}
    
sugarizerOS.log = function(m){
    console.log(m);
}

sugarizerOS.init()
