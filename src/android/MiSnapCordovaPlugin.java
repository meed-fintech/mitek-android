package cordova.plugin.misnap;

import java.util.List;
import java.util.Base64;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.miteksystems.misnap.analyzer.MiSnapAnalyzerResult;
//import com.miteksystems.misnap.misnapworkflow.MiSnapWorkflowActivity;
import com.miteksystems.misnap.misnapworkflow_UX2.MiSnapWorkflowActivity_UX2;
import com.miteksystems.misnap.misnapworkflow_UX2.params.WorkflowApi;
import com.miteksystems.misnap.params.CameraApi;
import com.miteksystems.misnap.params.MiSnapApi;
import com.miteksystems.misnap.params.ScienceApi;
//import com.miteksystems.misnap.params.CreditCardApi;
//import com.miteksystems.misnap.params.BarcodeApi;

/**
 * This class runs MiSnap from JavaScript.
 */
public class MiSnapCordovaPlugin extends CordovaPlugin {

    private CallbackContext _callback = null;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        System.out.println("...Inside Cordova Plugin execute: " + action);
        if (action.equals("runMiSnap")) {
            String side = args.getString(0);
            this.runMiSnap(side, callbackContext);
            return true;
        }
        return false;
    }

    private void runMiSnap(String side, CallbackContext callbackContext) {
        
        _callback = callbackContext;

        Context context = cordova.getActivity().getApplicationContext();

        // set the callback
        cordova.setActivityResultCallback(this);

        try {
            JSONObject misnapParams = new JSONObject();

            if (side.equalsIgnoreCase("front"))
                misnapParams.put(MiSnapApi.MiSnapDocumentType, MiSnapApi.PARAMETER_DOCTYPE_CHECK_FRONT);
            else if (side.equalsIgnoreCase("back"))
                misnapParams.put(MiSnapApi.MiSnapDocumentType, MiSnapApi.PARAMETER_DOCTYPE_CHECK_BACK);
            else {
                _callback.error("Invalid param side.");
                return;
            }

            misnapParams.put(WorkflowApi.MiSnapTrackGlare, "1");
            misnapParams.put(CameraApi.MiSnapFocusMode, CameraApi.PARAMETER_FOCUS_MODE_HYBRID);


            Intent intentMiSnap = new Intent(context, MiSnapWorkflowActivity_UX2.class);
            
            intentMiSnap.putExtra(MiSnapApi.JOB_SETTINGS, misnapParams.toString());

            System.out.println("Running MiSnap...");
            cordova.getActivity().startActivityForResult(intentMiSnap, MiSnapApi.RESULT_PICTURE_CODE);
        }
        catch (JSONException e) {
            e.printStackTrace();
            _callback.error(e.getLocalizedMessage());
        }   
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (MiSnapApi.RESULT_PICTURE_CODE == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                if (data != null) {
                    Bundle extras = data.getExtras();
                    String miSnapResultCode = extras.getString(MiSnapApi.RESULT_CODE);

                    switch (miSnapResultCode) {
                        // MiSnap check capture
                        case MiSnapApi.RESULT_SUCCESS_VIDEO:
                        case MiSnapApi.RESULT_SUCCESS_STILL:
                            System.out.println("MIBI: " + extras.getString(MiSnapApi.RESULT_MIBI_DATA));

                            // Image returned successfully
                            byte[] sImage = data.getByteArrayExtra(MiSnapApi.RESULT_PICTURE_DATA);

                            // Now Base64-encode the byte array before sending it to the server.
                            // e.g. byte[] sEncodedImage = Base64.encode(sImage, Base64.DEFAULT);
                            //      sendToServer(sEncodedImage);
                            String sEncodedImage = Base64.getEncoder().encodeToString(sImage);
                            String message = null;
                            List<String> warnings = extras.getStringArrayList(MiSnapApi.RESULT_WARNINGS);
                            if (warnings != null && !warnings.isEmpty()) {
                                if ((warnings.contains(MiSnapAnalyzerResult.FrameChecks.WRONG_DOCUMENT.name()))) {
                                    message += "\nWrong document detected";
                                }
                            }
                            System.out.println("Image captured!  Warnings: " + warnings);
                            if (sImage != null)
                                System.out.println("image size=" + sImage.length);
                            _callback.success(sEncodedImage);
                            return;
                    }
                } 
                else {
                    // Image canceled, stop
                    System.out.println("Unable to generate image.");
                    _callback.error(-2);
                    return;
                }
            } 
            else if (Activity.RESULT_CANCELED == resultCode) {
                // Camera not working or not available, stop
                String miSnapResultCode = null;
                if (data != null) {
                    Bundle extras = data.getExtras();
                    miSnapResultCode = extras.getString(MiSnapApi.RESULT_CODE);
                }

                System.out.println("MiSnap canceled.  Reason:" + miSnapResultCode);
                _callback.error(-1);
                return;
            }
        }

        _callback.error("Invalid workflow.");
    }
}
