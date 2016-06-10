var exec = require('cordova/exec');

var sugarizeros;

function onSuccess(message){
    alert("onSuccess");
    alert(message);
}

function onFailure(){
    alert("onFailure");
}

function init(){
    exec(onSuccess, onFailure, "SugarizerOSPlugin", "apps", []);
    sugarizeros = true;
    console.log("SugarizerOS initialized");
}

init();
