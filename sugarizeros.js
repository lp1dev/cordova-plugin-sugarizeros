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
    exec(onSuccess, onFailure, "SugarizerOSPlugin", "echo", ["I'm Alive"]);    
    sugarizeros = true;
    console.log("SugarizerOS initialized");
}

//document.addEventListener('deviceready', init, false);
init();
