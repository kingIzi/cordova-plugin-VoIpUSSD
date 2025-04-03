package com.ramymokako.plugin.ussd.android;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.pm.PackageManager;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

//import io.sybox.easyshare.MainActivity; //(io.sybox.easyshare: this must be replaced by the name of your main package)

public class VoIpUSSD extends CordovaPlugin {

    private HashMap<String, HashSet<String>> map;
    private USSDApi ussdApi;
    private Context context;
    private String result;
	public final String ACTION_SEND_SMS = "show";
	public final String ACTION_HAS_PERMISSION = "has_permission";
	public final String ACTION_REQUEST_PERMISSION = "request_permission";
	private static final int SEND_SMS_REQ_CODE = 0;
	private static final int REQUEST_PERMISSION_REQ_CODE = 1;
	CallbackContext callbackContext;
	private JSONArray _args;

    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {

	    map = new HashMap<>();
        map.put("KEY_LOGIN", new HashSet<>(Arrays.asList("espere", "waiting", "loading", "esperando")));
        map.put("KEY_ERROR", new HashSet<>(Arrays.asList("problema", "problem", "error", "null")));
        this.context = cordova.getActivity();//.getApplicationContext();
        this.callbackContext =  callbackContext;
		this._args = args;
		ussdApi = USSDController.getInstance(this.context);
		result = "";

	    if (action.equals(ACTION_SEND_SMS)) {

	        String ussdCode;
            try {
                 JSONObject options = args.getJSONObject(0);
                 ussdCode = options.getString("ussdCode");
            } catch (JSONException e) {
                callbackContext.error("Error encountered: " + e.getMessage());
                return false;
            }

			if (hasPermission()) {
			    executeSimpleUssd(ussdCode, callbackContext);
				PluginResult pluginResult_NO_RESULT = new  PluginResult(PluginResult.Status.NO_RESULT);
				pluginResult_NO_RESULT.setKeepCallback(true);
				return true;
		    } else {
				requestPermission(SEND_SMS_REQ_CODE);
				return false;
		    }
	    }
		else if (action.equals(ACTION_HAS_PERMISSION)) {
			callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, hasPermission()));
			return false;
		}
		else if (action.equals(ACTION_REQUEST_PERMISSION)) {
			requestPermission(REQUEST_PERMISSION_REQ_CODE);
			return false;
		}
		return false;
    }

    private void executeSimpleUssd(String phone, CallbackContext callbackContext) {
      List<String> commands = new ArrayList<>(Arrays.asList(phone.split(",")));
      paymentProcess(commands);
    }

    private void paymentProcess(final List<String> commands) {
      Intent svc = new Intent(((Activity) context), com.ramymokako.plugin.ussd.android.SplashLoadingService.class);
      ((Activity) context).startService(svc);
      ussdApi.callUSSDInvoke(commands.get(0), map, new USSDController.CallbackInvoke() {
        @Override
        public void responseInvoke(String message) {
          PluginResult result_1 = new PluginResult(PluginResult.Status.OK, result);
          result_1.setKeepCallback(true);
          callbackContext.sendPluginResult(result_1);
          Observable
            .just("6", "2", "1", "2", commands.get(1), commands.get(2),commands.get(3),commands.get(4))
            .delay(500, TimeUnit.MILLISECONDS)
            .concatMap(res -> {
              return Observable.create(emitter -> {
                ussdApi.send(res, new USSDController.CallbackMessage() {
                  @Override
                  public void responseMessage(String message) {
                    emitter.onNext(message);
                    emitter.onComplete();
                  }
                });
              });
            })
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .delay(500, TimeUnit.MILLISECONDS)
            .doOnNext(res -> {
              PluginResult result_4 = new PluginResult(PluginResult.Status.OK, result);
              result_4.setKeepCallback(true);
              callbackContext.sendPluginResult(result_4);
            }).doOnError(err -> {
              ((Activity) context).stopService(svc);
            }).doOnComplete(() -> {
              ((Activity) context).stopService(svc);
            }).subscribe();
        }
        @Override
        public void over(String message) {
          //result += "\n-\n" + message;
          ((Activity) context).stopService(svc);
        }
      });
    }

	private boolean hasPermission() {
		boolean gyg1 = cordova.hasPermission(android.Manifest.permission.CALL_PHONE);
		boolean gyg2 = cordova.hasPermission(android.Manifest.permission.READ_PHONE_STATE);
		return (gyg1 == true && gyg2 == true);
	}

	private void requestPermission(int requestCode) {
		cordova.requestPermissions(this, requestCode, new String[]{android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.CALL_PHONE});
	}

	public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
		for (int r : grantResults) {
			if (r == PackageManager.PERMISSION_DENIED) {
				this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "User has denied permission"));
				return;
			}
		}
		if (requestCode == SEND_SMS_REQ_CODE) {

            String ussdCode;
            try {
                 JSONObject options = this._args.getJSONObject(0);
                 ussdCode = options.getString("ussdCode");
            } catch (JSONException e) {
                 this.callbackContext.error("Error encountered: " + e.getMessage());
                 return;
            }

			executeSimpleUssd(ussdCode, this.callbackContext);
			PluginResult pluginResult_NO_RESULT = new  PluginResult(PluginResult.Status.NO_RESULT);
			pluginResult_NO_RESULT.setKeepCallback(true);
			return;
		}
		this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, true));
	}


}
