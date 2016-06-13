var sugarizerOS = {};
var exec = require('cordova/exec');

sugarizerOS.init = function(){
    if (window){
	window.sugarizerOS = sugarizerOS;
	console.log("SugarizerOS initialized");
    }
    else{
	console.log("No window to initialize sugarizerOS");
    }
}

sugarizerOS.echo = function(onSuccess, onFailure, string){
    exec(onSuccess, onFailure, "SugarizerOSPlugin", "echo", string);
}

sugarizerOS.getAndroidApplications = function(onSuccess, onFailure, flags){
    exec(onSuccess, onFailure, "SugarizerOSPlugin", "apps", flags);
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
		version: 0,
		type: "Android"});
	}
    return activities;
}

sugarizerOS.initActivitiesPreferences = function(){
    sugarizerOS.getAndroidApplications(function(applications){
	var activities = preferences.getActivities();
	activities = activities.concat(sugarizerOS.applicationsToActivities(applications));
	preferences.setActivities(activities);
    }
				       , sugarizerOS.log, [0]);
}

sugarizerOS.log = function(m){
    console.log(m);
}

sugarizerOS.init();

