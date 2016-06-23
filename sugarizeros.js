var sugarizerOS = {};
var exec = require('cordova/exec');

sugarizerOS.applicationsLoaded = false;

sugarizerOS.setApplicationsLoaded = function(value){
    sugarizerOS.applicationsLoaded = true;
}

sugarizerOS.init = function(){
    if (window){
	window.sugarizerOS = sugarizerOS;
	console.log("SugarizerOS initialized");
    }
    else{
	console.log("No window to initialize sugarizerOS");
    }
}

sugarizerOS.chooseLauncher = function(){
    exec(null, null, "SugarizerOSPlugin", "chooseLauncher", []);
}

sugarizerOS.isDefaultLauncher = function(onSuccess, onFailure){
    exec(onSuccess, onFailure, "SugarizerOSPlugin", "isDefaultLauncher", []);
}

sugarizerOS.echo = function(onSuccess, onFailure, string){
    exec(onSuccess, onFailure, "SugarizerOSPlugin", "wifi", string);
}

sugarizerOS.getAndroidApplications = function(onSuccess, onFailure, flags){
    exec(onSuccess, onFailure, "SugarizerOSPlugin", "apps", flags);
}

sugarizerOS.scanWifi = function(onSuccess, onFailure){
    exec(onSuccess, onFailure, "SugarizerOSPlugin", "scanWifi", []);
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
