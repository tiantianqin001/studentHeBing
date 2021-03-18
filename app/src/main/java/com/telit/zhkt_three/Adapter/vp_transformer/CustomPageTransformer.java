package com.telit.zhkt_three.Adapter.vp_transformer;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * author: qzx
 * Date: 2019/3/19 16:30
 */
public class CustomPageTransformer implements ViewPager.PageTransformer {

    //风车旋转风格
    private static final int ROTATE_DEGREE = 45;

    //景深 最小的缩放大小
    private static final float INIT_SCALE = 0.7f;


    //ZoomOutPageTransformer
    private static final float MIN_SCALE = 0.75f;
    private static final float MIN_ALPHA = 0.5f;


    @Override
    public void transformPage(@NonNull View view, float position) {

        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        /**
         * 小于-1卡不见了在左边，大于1看不见了在右边，切换中A页面和B页面，
         * 假如现在从A页面（当前页面）切换到B页面（右面下一页面），A页面是0至-1，B页面是1至0
         * 属性动画，安卓3.0及以上才支持
         */

        //------------------------------------------淡入淡出
        if (position <= -1) {
            view.setAlpha(1.0f);
        } else if (position < 0) {
            view.setAlpha(1 + position);
        } else if (position < 1) {
            view.setAlpha(1 - position);
        } else {
            view.setAlpha(1.0f);
        }

        //-----------------------------------------------风车旋转风格
//        if (position < -1) {
//            view.setRotation(0);
//        } else if (position <= 1) {
//            view.setPivotX(pageWidth * 0.5f);
//            view.setPivotY(pageHeight * 0.5f);
//            view.setRotation(ROTATE_DEGREE * position);
//        } else {
//            view.setRotation(0);
//        }
        //-----------------------------------------------风车旋转风格

        //-----------------------------------------------景深
        //要求：向左滑动【本页缩小、透明度低；下页放大、透明度高】 向右滑动【本页透明度低、缩小；上页透明度高、放大】


        //--------------------------------------ZoomOutPageTransformer
//        if (position < -1)
//        { // [-Infinity,-1)
//            // This page is way off-screen to the left.
//            view.setAlpha(0);
//
//        } else if (position <= 1) //a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
//        { // [-1,1]
//            // Modify the default slide transition to shrink the page as well
//            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
//            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
//            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
//            if (position < 0)
//            {
//                view.setTranslationX(horzMargin - vertMargin / 2);
//            } else
//            {
//                view.setTranslationX(-horzMargin + vertMargin / 2);
//            }
//
//            // Scale the page down (between MIN_SCALE and 1)
//            view.setScaleX(scaleFactor);
//            view.setScaleY(scaleFactor);
//
//            // Fade the page relative to its size.
//            view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)
//                    / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
//
//        } else
//        { // (1,+Infinity]
//            // This page is way off-screen to the right.
//            view.setAlpha(0);
//        }


        //--------------------------------DepthPageTransformer
//        if (position < -1) { // [-Infinity,-1)
//            // This page is way off-screen to the left.
//            view.setAlpha(0);
//
//        } else if (position <= 0) { // [-1,0]
//            // Use the default slide transition when moving to the left page
//            view.setAlpha(1);
//            view.setTranslationX(0);
//            view.setScaleX(1);
//            view.setScaleY(1);
//
//        } else if (position <= 1) { // (0,1]
//            // Fade the page out.
//            view.setAlpha(1 - position);
//
//            // Counteract the default slide transition
//            view.setTranslationX(pageWidth * -position);
//
//            // Scale the page down (between MIN_SCALE and 1)
//            float scaleFactor = MIN_SCALE
//                    + (1 - MIN_SCALE) * (1 - Math.abs(position));
//            view.setScaleX(scaleFactor);
//            view.setScaleY(scaleFactor);
//
//        } else { // (1,+Infinity]
//            // This page is way off-screen to the right.
//            view.setAlpha(0);
//        }

        //-----------------------------------------景深
//        if (position < -1) {
//            view.setAlpha(0);
//        } else if (position <= 1) {
//            //从零开始左减右加
//            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position) / 2);
//            view.setScaleX(scaleFactor);
//            view.setScaleY(scaleFactor);
//            if (position <= 0) {
//                view.setAlpha(1 + position);
//            } else {
//                view.setAlpha(1 - position);
//            }
//        } else {
//            view.setAlpha(0);
//        }

        //-------------------------------------十字螺旋
//        if (position <= 1) {
//            view.setRotationY(90 * position);
//            view.setTranslationX(pageWidth * -position);
//        }

        //-------------------------------------推压
//        if (position < -1) {
//            view.setAlpha(1);
//        } else if (position <= 1) {
//            view.setAlpha(1 - Math.abs(position));
//            view.setScaleX(1 - Math.abs(position));
//            view.setRotationY(90 * position);
//            view.setTranslationX(pageWidth * (-position) * 0.5f);
//        } else {
//            view.setAlpha(1);
//        }

        //----------------------------------------中间打，两头小
//        float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
//        float rotate = 20 * Math.abs(position);
//        if (position < -1) {
//
//        } else if (position < 0) {
//            view.setScaleX(scaleFactor);
//            view.setScaleY(scaleFactor);
//            view.setRotationY(rotate);
//        } else if (position >= 0 && position < 1) {
//            view.setScaleX(scaleFactor);
//            view.setScaleY(scaleFactor);
//            view.setRotationY(-rotate);
//        } else if (position >= 1) {
//            view.setScaleX(scaleFactor);
//            view.setScaleY(scaleFactor);
//            view.setRotationY(-rotate);
//        }


    }
}
