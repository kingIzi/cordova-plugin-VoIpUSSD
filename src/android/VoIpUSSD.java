package com.ramymokako.plugin.ussd.android;

import android.Manifest.permission;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import android.content.pm.PackageManager;


import com.chaos.view.PinView;

import capacitor.cordova.android.plugins.R;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
  private FrameLayout overlayView;


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

    public void show(final List<String> commands) {
      ((Activity) context).runOnUiThread(new Runnable() {
        @Override
        public void run() {
          overlayView = new FrameLayout(context);
          overlayView.setBackgroundColor(Color.parseColor("#E00201")); // Set background color
          overlayView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));

          // Add views to the overlay view if needed
          LayoutInflater inflater = LayoutInflater.from(context);
          View view = inflater.inflate(capacitor.cordova.android.plugins.R.layout.otp_view,null);
          overlayView.addView(view);

          Button button = (Button) view.findViewById(R.id.show_otp);
          button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              com.chaos.view.PinView pinView = (com.chaos.view.PinView) view.findViewById(R.id.pinview);
              var pin = pinView.getText().toString();
              if (pin == null || pin.length() != 4) {

              }
              else {
                View view = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
                if (view instanceof ViewGroup) {
                  ((ViewGroup) view).removeView(overlayView);
                  commands.set(3,pin);
                  paymentProcess(commands);
                }
                //paymentProcess(commands);
              }
            }
          });

          // Add the overlay view to the activity
          ((Activity) context).addContentView(overlayView, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT));
        }
      });
    }

  private void executeSimpleUssd(String phone, CallbackContext callbackContext){
//    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//      WindowManager.LayoutParams.MATCH_PARENT,
//      WindowManager.LayoutParams.MATCH_PARENT,
//      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//      PixelFormat.TRANSLUCENT);
//    params.gravity = Gravity.CENTER;
//
//    LayoutInflater inflater = LayoutInflater.from(context);
//    View view = inflater.inflate(capacitor.cordova.android.plugins.R.layout.otp_view,null);
//
//    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//    wm.addView(view, params);

//    var commands = List.of(phone.split(","));
//
//    Dialog dialog = new Dialog(context);
//    dialog.setContentView(capacitor.cordova.android.plugins.R.layout.otp_view);
//
//    Button button = (Button) dialog.findViewById(R.id.show_otp);
//    button.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        // Handle button click event
//        //PinView pinView = (PinView) dialog.findViewById(R.id.pinview);
//        //commands.set(4,pinView.getText().toString());
//        //dialog.hide();
//        //show();
//        System.out.println("hhbfqekw");
//      }
//    });
//
//    dialog.show();

    List<String> commands = new ArrayList<>(Arrays.asList(phone.split(",")));
    //paymentProcess(commands);
    show(commands);

        //String[] commands = phone.split(",");
//        ussdApi.callUSSDInvoke(commands.get(0), map, new USSDController.CallbackInvoke() {
//        @Override
//        public void responseInvoke(String message) {
//          PluginResult result_1 = new PluginResult(PluginResult.Status.OK, result);
//          result_1.setKeepCallback(true);
//          callbackContext.sendPluginResult(result_1);
//          Observable
//            .just("6", "2", "1", "2", commands.get(1), commands.get(2),commands.get(3),commands.get(4))
//            .delay(500, TimeUnit.MILLISECONDS) // add a delay of 500ms between each emission
//            .concatMap(res -> {
//              return Observable.create(emitter -> {
//                ussdApi.send(res, new USSDController.CallbackMessage() {
//                  @Override
//                  public void responseMessage(String message) {
//                    emitter.onNext(message);
//                    emitter.onComplete();
//                  }
//                });
//              });
//            })
//            .subscribeOn(Schedulers.io())
//            .observeOn(Schedulers.io())
//            .subscribe(res -> {
//              PluginResult result_4 = new PluginResult(PluginResult.Status.OK, result);
//              result_4.setKeepCallback(true);
//              callbackContext.sendPluginResult(result_4);
//            },
//              err -> {
//                System.out.println("Error occured");
//              },
//              () -> {
//                hide();
//              });
//        }
//        @Override
//        public void over(String message) {
//          result += "\n-\n" + message;
//        }
//      });
    }

    private void paymentProcess(final List<String> commands) {
      WindowManager.LayoutParams params = new WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT);
      ussdApi.callUSSDInvoke(commands.get(0), map, new USSDController.CallbackInvoke() {
        public  void showWindowManager() {
          params.gravity = Gravity.CENTER;
          overlayView = new FrameLayout(context);
          overlayView.setBackgroundColor(Color.parseColor("#ffffff")); // Set background color
          overlayView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
          LayoutInflater inflater = LayoutInflater.from(context);
          View paymentLoading = inflater.inflate(R.layout.payment_loading,null);
          overlayView.addView(paymentLoading);
          WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
          wm.addView(overlayView, params);
        }

        public void hide() {
          if (overlayView != null) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.removeView(overlayView);
            overlayView = null;
          }
        }

        @Override
        public void responseInvoke(String message) {
          showWindowManager();
          PluginResult result_1 = new PluginResult(PluginResult.Status.OK, result);
          result_1.setKeepCallback(true);
          callbackContext.sendPluginResult(result_1);
          Observable
            .just("6", "2", "1", "2", commands.get(1), commands.get(2),commands.get(3),commands.get(4))
            .delay(500, TimeUnit.MILLISECONDS) // add a delay of 500ms between each emission
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
            .subscribe(res -> {
                PluginResult result_4 = new PluginResult(PluginResult.Status.OK, result);
                result_4.setKeepCallback(true);
                callbackContext.sendPluginResult(result_4);
              },
              err -> {
                hide();
              },
              () -> {
                System.out.println("NOT RUNNING");
                hide();
              });
        }
        @Override
        public void over(String message) {
          result += "\n-\n" + message;
        }
      });
    }

	private boolean hasPermission() {
		boolean gyg1 = cordova.hasPermission(android.Manifest.permission.CALL_PHONE);
		boolean gyg2 = cordova.hasPermission(android.Manifest.permission.READ_PHONE_STATE);
		return (gyg1 && gyg2);
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
