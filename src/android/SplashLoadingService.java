//package com.romellfudi.ussdlibrary;

package com.ramymokako.plugin.ussd.android;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import android.app.Application;
import android.content.res.Resources;
import android.widget.TextView;

import capacitor.cordova.android.plugins.R;

/**
 * SplashLoadingService for Android splashing dialog
 *
 * @author Romell Dominguez
 * @version 1.1.d 23/02/2017
 * @since 1.1.d
 */
public class SplashLoadingService extends Service {
    private LinearLayout overlayView;
    private LinearLayout layout;
    private WindowManager wm;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ResourceAsColor")
    public int onStartCommand(Intent intent, int flags, int startId) {
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        int padding_in_dp = 100;
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

        layout = new LinearLayout(this);
        //layout.setBackgroundColor(R.color.blue_background);
        layout.setOrientation(LinearLayout.VERTICAL);

        WindowManager.LayoutParams params =
                new WindowManager.LayoutParams
                        (WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.MATCH_PARENT,
                                LAYOUT_FLAG
                                , WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                                PixelFormat.RGB_565);

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.logo);
        imageView.setPaddingRelative(0,padding_in_px,0,padding_in_px);
        FrameLayout imageFrame = new FrameLayout(this);
        imageFrame.setBackgroundColor(Color.parseColor("#FFFFFF"));
        imageFrame.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
        imageFrame.addView(imageView);
        LinearLayout.LayoutParams params_ll = new LinearLayout
                .LayoutParams(LinearLayout.MarginLayoutParams.MATCH_PARENT, 0);
        params_ll.gravity = Gravity.CENTER;
        params_ll.weight = 1;

        RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        rp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        relativeLayout.addView(imageFrame,rp);
        layout.addView(relativeLayout, params_ll);

        GifImageView gifImageView = new GifImageView(this);
        gifImageView.setGifImageResource(R.drawable.loading_7528_256);
        gifImageView.setPaddingRelative(0,padding_in_px,0,padding_in_px);
//
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams.gravity = Gravity.CENTER; // Center the view horizontally and vertically
//
//        gifImageView.setLayoutParams(layoutParams);
//
//        overlayView = new FrameLayout(this);
//        overlayView.setBackgroundColor(Color.parseColor("#FFFFFF"));
//        overlayView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
//        overlayView.addView(gifImageView);

      overlayView = new LinearLayout(this);
      overlayView.setBackgroundColor(Color.parseColor("#FFFFFF"));
      overlayView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
      overlayView.setOrientation(LinearLayout.VERTICAL); // Set the orientation to vertical
      overlayView.setGravity(Gravity.CENTER); // Center all child views

      gifImageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

      TextView textView = new TextView(this);
      textView.setText("Payment in progress...");
      textView.setTextSize(16); // Set the text size
      textView.setTextColor(Color.BLACK); // Set the text color
      textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

      overlayView.addView(gifImageView);
      overlayView.addView(textView);


        relativeLayout = new RelativeLayout(this);
        rp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        rp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        relativeLayout.addView(overlayView,rp);

        layout.addView(relativeLayout,params_ll);

        wm.addView(layout,params);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (layout != null) {
                    wm.removeView(layout);
                    layout = null;
                }
            }
        },500);
    }

}
