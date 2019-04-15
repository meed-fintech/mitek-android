var exec = require('cordova/exec');

exports.runMiSnap = function(arg0, success, error) {
    exec(success, error, "MiSnapCordovaPlugin", "runMiSnap", [arg0]);
};
