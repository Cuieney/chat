package com.android.youtube.customview;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by cuieney on 2019/2/23.
 */

public class RelativeLayoutParamsWrapper {
    RelativeLayout.LayoutParams layoutParams;
    private View view;

    public RelativeLayoutParamsWrapper(View view) {
        if (view == null) {
            Log.i("oye", "LayoutParamsWrapper:view is null ");
        }
        this.view = view;

        if (view instanceof ViewGroup) {
            layoutParams = (RelativeLayout.LayoutParams) ((ViewGroup) view).getLayoutParams();
            Log.i("oye", "LayoutParamsWrapper:view  is ViewGroup ");

        }else{
            layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        }

        if (layoutParams == null) {
            Log.i("oye", "LayoutParamsWrapper:view getLayoutParams is null ");
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            view.setLayoutParams(layoutParams);
        }

    }

    public View getView() {
        return view;
    }

    public int getMarginLeft() {
        return layoutParams.leftMargin;
    }

    public int getMarginRight() {
        return layoutParams.rightMargin;
    }

    public int getMarginTop() {
        return layoutParams.topMargin;
    }

    public int getMarginBottom() {
        return layoutParams.bottomMargin;
    }

    public int getWidth() {
        return layoutParams.width;
    }

    public int getHeight() {
        return layoutParams.height;
    }

    public void setMarginLeft(int value){
        layoutParams.leftMargin = value;
        view.setLayoutParams(layoutParams);
    }

    public void setMarginRight(int value){
        layoutParams.rightMargin = value;
        view.setLayoutParams(layoutParams);
    }

    public void setMarginTop(int value){
        layoutParams.topMargin = value;
        view.setLayoutParams(layoutParams);
    }

    public void setMarginBottom(int value){
        layoutParams.bottomMargin = value;
        view.setLayoutParams(layoutParams);
    }


    public void setWidth(int value){
        layoutParams.width = value;
        view.setLayoutParams(layoutParams);
    }

    public void setHeight(int value){
        layoutParams.height = value;
        view.setLayoutParams(layoutParams);
    }

    public void setZ(float z) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            view.setTranslationZ(z);
    }

    public float getZ() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return view.getTranslationZ();
        else
            return 0;
    }






}
