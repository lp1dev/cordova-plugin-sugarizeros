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

sugarizerOS.getAndroidApplicationIcon = function(onSuccess, onFailure, packageName){
    exec(onSuccess, onFailure, "SugarizerOSPlugin", "icon", packageName);
}

sugarizerOS.init();

