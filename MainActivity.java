/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package org.olpcfrance.sugarizer;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import org.apache.cordova.*;

import java.util.Locale;


public class MainActivity extends CordovaActivity
{
    private Context mContext;
    private WindowManager windowManager;
    private WindowManager.LayoutParams overAllLayoutParams, classicLayoutParams;
    private CustomViewGroup customViewGroup;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mContext = this;

        //Hiding Notification Bar Hacks
        preventStatusBarExpansion(mContext);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //
        if (appView == null)
            customInit();
        this.keepRunning = preferences.getBoolean("KeepRunning", true);
        //windowManager.addView(customViewGroup, layoutParams);
        //setContentView(customViewGroup);
        appView.loadUrlIntoView(launchUrl, true);
    }

    public void customInit(){
        appView = makeWebView();
        initAppView();
        if (!appView.isInitialized()) {
            appView.init(cordovaInterface, pluginEntries, preferences);
        }
        cordovaInterface.onCordovaInit(appView.getPluginManager());
        // Wire the hardware volume controls to control media if desired.
        String volumePref = preferences.getString("DefaultVolumeStream", "");
        if ("media".equals(volumePref.toLowerCase(Locale.ENGLISH))) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
        }
    }

    public void initAppView(){
        //appView.getView().setId(100);
        appView.getView().setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        setContentView(appView.getView(), overAllLayoutParams);
        //WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //windowManager.addView(customViewGroup, overAllLayoutParams);
        if (preferences.contains("BackgroundColor")) {
            int backgroundColor = preferences.getInteger("BackgroundColor", Color.BLACK);
            // Background of activity:
            appView.getView().setBackgroundColor(backgroundColor);
        }
        appView.getView().requestFocusFromTouch();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // ATTENTION: GET THE X,Y OF EVENT FROM THE PARAMETER
        // THEN CHECK IF THAT IS INSIDE YOUR DESIRED AREA


        return false;
    }

    public void preventStatusBarExpansion(Context context) {
        windowManager = ((WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));

        Activity activity = (Activity)context;
        overAllLayoutParams = new WindowManager.LayoutParams();
        classicLayoutParams = new WindowManager.LayoutParams();
        overAllLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        classicLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        overAllLayoutParams.gravity = Gravity.TOP;
        overAllLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|

               WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

               WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        overAllLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        //http://stackoverflow.com/questions/1016896/get-screen-dimensions-in-pixels
        int resId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = 0;
        if (resId > 0) {
            result = activity.getResources().getDimensionPixelSize(resId);
        }

        overAllLayoutParams.height = result;

        overAllLayoutParams.format = PixelFormat.TRANSPARENT;

        customViewGroup = new CustomViewGroup(context);
    }

    public static class CustomViewGroup extends ViewGroup {

        public CustomViewGroup(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            Log.v("customViewGroup", "**********Intercepted");

            return false;
        }
    }
}
