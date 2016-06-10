
var sugarizeros;

function onSuccess(message){
    alert("onSuccess");
    alert(message);
}

function onFailure(){
    alert("onFailure");
}

function init(){
//    alert("SugarizerOS Loaded");
    exec(onSuccess, onFailure, "SugarizerOSPlugin", "echo", ["I'm Alive"]);    
    sugarizeros = true;
}

init();
