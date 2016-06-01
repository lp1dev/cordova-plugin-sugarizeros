#!/usr/bin/env node

var del  = require('del');
var fs   = require('fs');
var path = require('path');

var rootdir = process.argv[2];

if (rootdir) {
    var platforms = (process.env.CORDOVA_PLATFORMS ? process.env.CORDOVA_PLATFORMS.split(',') : []);
    
    for(var x=0; x<platforms.length; x++) {
	try {
	    var platform = platforms[x].trim().toLowerCase();
	    var filepath;
	    
	    if(platform == 'android') {
		filepath = path.join('platforms', platform, 'src', 'org', 'olpcfrance', 'sugarizer', 'MainActivity.java');
		if (fs.existsSync(filepath)){
		    console.log("Removing original MainActivity file before build");
		    del.sync(filepath);
		}
		else{
		    console.log("File "+filepath+" does not exists and cannot be removed");
		}
	    }
	} catch(e) {
	    process.stdout.write(e);
	}
    }
}
