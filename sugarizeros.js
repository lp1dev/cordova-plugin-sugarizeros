var sugarizerOS = (
    function () {
	var self = {};
	var exec = require('cordova/exec');
	
	self.init = function(){
	    console.log("SugarizerOS initialized");
	}

	self.get_applications = function(){
	    exec(onSuccess, onFailure, "SugarizerOSPlugin", "apps", []);
	}

	self.on_get_applications_success = function(apps){
	    console.log(apps);
	}

	self.on_get_applications_failure = function(error){
	    console.log(error);
	}
	return self;
    }
)();

