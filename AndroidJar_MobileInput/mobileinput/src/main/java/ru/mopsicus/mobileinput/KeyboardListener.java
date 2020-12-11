// ----------------------------------------------------------------------------
// The MIT License
// UnityMobileInput https://github.com/mopsicus/UnityMobileInput
// Copyright (c) 2018 Mopsicus <mail@mopsicus.ru>
// ----------------------------------------------------------------------------

package ru.mopsicus.mobileinput;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mopsicus.common.Common;

public class KeyboardListener implements KeyboardObserver {

    private boolean isPreviousState = false;
    private int preKeyboardHeight = 0;
    private Common common = new Common();

    private static int keyboardHeightcons = -1;

    @Override
    public void onKeyboardHeight(float height, int keyboardHeight, int orientation) {
        boolean isShow = (keyboardHeight > 0);
        Log.i("Unity", "Java端onKeyboardHeight得到相应:" + keyboardHeight);
        JSONObject json = new JSONObject();
        try {
            json.put("msg", Plugin.KEYBOARD_ACTION);
            json.put("show", isShow);
            json.put("height", height);
        } catch (JSONException e) {
        }

        //增加preKeyboardHeight判断，输入法切换时键盘高度变化 也需要发给Unity
        if (isPreviousState != isShow || preKeyboardHeight != keyboardHeight) {
            isPreviousState = isShow;
            preKeyboardHeight = keyboardHeight;
            common.sendData(Plugin.name, json.toString());
        }

//        if (isShow) {
//            //UnityPlayer.currentActivity.findViewById(android.R.id.content).setTranslationY(-200);
//            keyboardHeightcons = keyboardHeight;
//            if (edit != null) {
//                View view = UnityPlayer.currentActivity.findViewById(android.R.id.content);
//                view.setTranslationY(-(edit.getY() + edit.getHeight() - (view.getHeight() - keyboardHeight)));
//            }
//        } else {
//            UnityPlayer.currentActivity.findViewById(android.R.id.content).setTranslationY(0);
//        }
    }

//    private static EditText edit;
//
//    public static void setFoucsEdit(EditText exit1) {
//        edit = exit1;
//
//        if (edit != null && keyboardHeightcons != -1) {
//            View view = UnityPlayer.currentActivity.findViewById(android.R.id.content);
//            view.setTranslationY(-(edit.getY() + edit.getHeight() - (view.getHeight() - keyboardHeightcons)));
//        }
//    }

}

