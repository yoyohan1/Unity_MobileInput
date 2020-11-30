// ----------------------------------------------------------------------------
// The MIT License
// UnityMobileInput https://github.com/mopsicus/UnityMobileInput
// Copyright (c) 2018 Mopsicus <mail@mopsicus.ru>
// ----------------------------------------------------------------------------

package ru.mopsicus.mobileinput;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.print.PrintAttributes;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class KeyboardProvider extends PopupWindow {

    private KeyboardObserver observer;
    private int keyboardLandscapeHeight;
    private int keyboardPortraitHeight;
    private View popupView;
    private View parentView;
    private Activity activity;

    // Constructor
    public KeyboardProvider(Activity activity, ViewGroup parent, KeyboardObserver listener) {
        super(activity);
        this.observer = listener;
        this.activity = activity;
        Resources resources = this.activity.getResources();
        String packageName = this.activity.getPackageName();
        int id = resources.getIdentifier("popup", "layout", packageName);
        LayoutInflater inflator = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        this.popupView = inflator.inflate(id, null, false);
        setContentView(popupView);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        parentView = parent;
        setWidth(0);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        setBackgroundDrawable(new ColorDrawable(0));
        showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0);
        popupView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (popupView != null) {
                    handleOnGlobalLayout();
                }
            }
        });
    }

    // Close fake popup
    public void disable() {
        dismiss();
    }

    // Return screen orientation
    private int getScreenOrientation() {
        return activity.getResources().getConfiguration().orientation;
    }

    // Handler to get keyboard height
    private void handleOnGlobalLayout() {
        Point screenSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(screenSize);
        Rect rect = new Rect();
        popupView.getWindowVisibleDisplayFrame(rect);
        int orientation = getScreenOrientation();
        int keyboardHeight = screenSize.y - rect.bottom;
        float height = keyboardHeight / (float) screenSize.y;
        if (keyboardHeight == 0) {
            notifyKeyboardHeight(0, 0, orientation);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.keyboardPortraitHeight = keyboardHeight;
            notifyKeyboardHeight(height, keyboardPortraitHeight, orientation);
        } else {
            this.keyboardLandscapeHeight = keyboardHeight;
            notifyKeyboardHeight(height, keyboardLandscapeHeight, orientation);
        }

//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(popupView.getLayoutParams().width, popupView.getLayoutParams().height);
//        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) popupView.getLayoutParams();
//        Log.i("Unity", " marginLayoutParams.topMargin:" + marginLayoutParams.topMargin + " " + (marginLayoutParams.topMargin - KeyboardListener.keyheight));
//        params.setMargins(marginLayoutParams.leftMargin, marginLayoutParams.topMargin - KeyboardListener.keyheight, 0, 0);
//
//        popupView.setLayoutParams(params);
    }

    // Send data observer
    private void notifyKeyboardHeight(float height, int keyboardHeight, int orientation) {
        if (observer != null) {
            observer.onKeyboardHeight(height, keyboardHeight, orientation);
        }
    }
}
