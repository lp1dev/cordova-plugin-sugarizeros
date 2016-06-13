var sugarizerOS = {};
var exec = require('cordova/exec');

sugarizerOS.init = function(){
    console.log("SugarizerOS initialized");
}

sugarizerOS.getAndroidApplications = function(onSuccess, onFailure){
    exec(onSuccess, onFailure, "SugarizerOSPlugin", "apps", []);
}

sugarizerOS.init();

if (window){
    window.sugarizerOS = sugarizerOS;
}
