// ----------------------------------------------------------------------------
// The MIT License
// UnityMobileInput https://github.com/mopsicus/UnityMobileInput
// Copyright (c) 2018 Mopsicus <mail@mopsicus.ru>
// ----------------------------------------------------------------------------

package ru.mopsicus.mobileinput;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.transition.Visibility;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import ru.mopsicus.common.Common;

import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;


public class Plugin {

    public static String name = "mobileinput";

    public static String KEYBOARD_ACTION = "KEYBOARD_ACTION";
    public static Activity activity;
    public static RelativeLayout layout;
    public static Common common;
    private static ViewGroup group;
    private static KeyboardProvider keyboardProvider;
    private static KeyboardListener keyboardListener;
    public static int maskMarginLeft;
    public static int maskMarginTop;
//    private static TextView textView;
    public static double layoutWidth;
    public static double layoutHeight;//为什么使用layout宽高是因为跟Unity的Screent值相同，而不使用android代码activity.getResources().getDisplayMetrics()或者activity.getWindowManager().getDefaultDisplay().getSize获取的宽高 因为在有的机型值不一样 比如vivoX21A

    public static double getLayoutWidth() {
        if (layoutWidth == 0) {
            layoutWidth = layout.getWidth();
        }
        return layoutWidth;
    }

    public static double getLayoutHeight() {
        if (layoutHeight == 0) {
            layoutHeight = layout.getHeight();
        }
        return layoutHeight;
    }


    // Get view recursive
    private static View getLeafView(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); ++i) {
                View result = getLeafView(viewGroup.getChildAt(i));
                if (result != null) {
                    return result;
                }
            }
            return null;
        } else {
            return view;
        }
    }

    // Init plugin, create layout for MobileInputs
    public static void init() {
        common = new Common();
        activity = UnityPlayer.currentActivity;
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (layout != null) {
                    group.removeView(layout);
                }
                ViewGroup rootView = (ViewGroup) activity.findViewById(android.R.id.content);

                View topMostView = getLeafView(rootView);
                group = (ViewGroup) topMostView.getParent();
                // layout = new RelativeLayout(activity);
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(activity.getResources().getDisplayMetrics().widthPixels, activity.getResources().getDisplayMetrics().heightPixels);
//                layout.setBackgroundColor(Color.rgb(0, 0, 0));
//                layout.getBackground().setAlpha(1);

//                ScrollView scrollView = new ScrollView(activity);
//                scrollView.setLayoutParams(params);
//                scrollView.setOnTouchListener(new View.OnTouchListener(){
//                    @Override
//                    public boolean onTouch(View arg0, MotionEvent arg1) {
//                        return true;
//                    }
//                });
//                scrollView.setVerticalScrollBarEnabled(false);
                layout = new RelativeLayout(activity);
                //layout.setLayoutParams(new RelativeLayout.LayoutParams(activity.getResources().getDisplayMetrics().widthPixels,activity.getResources().getDisplayMetrics().heightPixels));
                //scrollView.addView(layout, params);

                //Button button = new Button(activity.getApplicationContext());
                //button.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                button.setText("获取edit的信息");
//                button.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        EditText editText = activity.findViewById(0);
//                        Log.i("Unity", "获取到的Edit信息" + editText.getWidth() + " " + (editText.getVisibility() == View.VISIBLE) + " " + editText.getText() + " " + editText.getAlpha());
//                        editText.setText("asdfasdfasdfsf");
//                    }
//                });

//                textView = new TextView(activity.getApplicationContext());
//                textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                textView.setText("hello world");
//                textView.setTextColor(Color.BLUE);
//                layout.addView(textView);
                //layout.setBackgroundColor(Color.TRANSPARENT);

                //layout.addView(View.inflate(activity,R.layout.hitokotodemo,null));
                //group.addView(scrollView);
                group.addView(layout, params);
                keyboardListener = new KeyboardListener();
                keyboardProvider = new KeyboardProvider(activity, layout, keyboardListener);
                Log.i("Unity", "移动输入框插件的layout创建成功！");

//              layoutWidth = (double) layout.getWidth();//layoutWidth layoutHeight不能放在这里获取 因为获取到的是0
//              layoutHeight = (double) layout.getHeight();//layoutWidth layoutHeight不能放在这里获取 因为获取到的是0

            }
        });
    }

    // Destroy plugin, remove layout
    public static void destroy() {
        Log.i("Unity", "android destroy!");
        activity.runOnUiThread(new Runnable() {
            public void run() {
                keyboardProvider.disable();
                keyboardProvider = null;
                keyboardListener = null;
                if (layout != null) {
                    group.removeView(layout);
                }
            }
        });
    }

    // 由Unity端发送过来的信息 Send data to MobileInput
    public static void execute(final int id, final String data) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                MobileInput.processMessage(id, data);
            }
        });
    }

    //由Unity端发送过来的信息
    public static void excuteOther(String data) {
        Log.i("Unity", "接收到excuteOther：" + data);
        try {
            JSONObject json = new JSONObject(data);
            String msg = json.getString("msg");
            if (msg.equals("SET_MASK")) {
                setMaskRect(json);
            }
        } catch (JSONException e) {
            Plugin.common.sendError(Plugin.name, "RECEIVE_ERROR", e.getMessage());
        }
    }

    private static void setMaskRect(JSONObject data) {
        try {
            //layoutWidth = (double) layout.getWidth();
            //layoutHeight = (double) layout.getHeight();
            Point screenSize = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(screenSize);

            //double screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
            //double screenHeight = activity.getResources().getDisplayMetrics().heightPixels;
            final double screenWidth = screenSize.x;
            final double screenHeight = screenSize.y;

            Log.e("adbd", " x:" + data.getDouble("x") + " y:" + data.getDouble("y"));
            //Log.e("adbd", "UnityscreenWidth:" + data.getDouble("ScreenWidth") + " UnityscreenHeight:" + data.getDouble("ScreenHeight"));

//            final double unityScreenWidth = data.getDouble("screenWidth");
//            final double unityScreenHeight = data.getDouble("screenHeight");
//            final double canvasHeight = data.getDouble("canvasHeight");

//            double x = data.getDouble("x") * screenWidth;
//            double y = data.getDouble("y") * screenHeight;
//            double width = data.getDouble("width") * screenWidth;
//            double height = data.getDouble("height") * screenHeight;
            double x = data.getDouble("x") * getLayoutWidth();
            double y = data.getDouble("y") * getLayoutHeight();
            double width = data.getDouble("width") * getLayoutWidth();
            double height = data.getDouble("height") * getLayoutHeight();


            Rect rect = new Rect((int) x, (int) y, (int) (x + width), (int) (y + height));
            final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(rect.width(), rect.height());
            params.setMargins(rect.left, rect.top, 0, 0);

            maskMarginLeft = rect.left;
            maskMarginTop = rect.top;

//            final RelativeLayout relativeLayout = new RelativeLayout(Plugin.activity);
//            relativeLayout.setLayoutParams(params);
//            relativeLayout.setBackgroundColor(Color.WHITE);
//            relativeLayout.getBackground().setAlpha(100);
//            activity.runOnUiThread(new Runnable() {
//                public void run() {
//                    activity.addContentView(relativeLayout, params);
//                }
//            });


            activity.runOnUiThread(new Runnable() {
                public void run() {
                    //"UW:" + unityScreenWidth + " UH:" + unityScreenHeight + " CH" + canvasHeight +
//                    textView.setText(" W:" + screenWidth + " H:" + screenHeight + " LW:" + getLayoutWidth() + " LH:" + getLayoutHeight());
//                    textView.setTextSize(30);

//                    activity.addContentView(relativeLayout, params);

                    layout.setLayoutParams(params);
                    //layout.setBackgroundColor(Color.RED);
                    //layout.getBackground().setAlpha(100);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
