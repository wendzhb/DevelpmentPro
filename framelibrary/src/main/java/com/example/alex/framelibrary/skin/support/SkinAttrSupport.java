package com.example.alex.framelibrary.skin.support;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import com.example.alex.framelibrary.skin.attr.SkinAttr;
import com.example.alex.framelibrary.skin.attr.SkinType;
import com.example.alex.framelibrary.skin.attr.SkinView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhb on 2017/7/6.
 * <p>
 * Descripte:皮肤属性解析的支持类
 */
public class SkinAttrSupport {

    public static final String TAG = "SkinAttrSupport";

    /**
     * 获取skinattr的属性
     *
     * @param context
     * @param attrs
     * @return
     */
    public static List<SkinAttr> getSkinAttrs(Context context, AttributeSet attrs) {
        //background src textcolor
        List<SkinAttr> skinAttrs = new ArrayList<>();
        int attributeCount = attrs.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            //获取名称
            String attrName = attrs.getAttributeName(i);
            String attrValue = attrs.getAttributeValue(i);
//            Log.e(TAG, "attrName = " + attrName + "attrValue = " + attrValue);
            //只获取重要的
            SkinType skinType = getSkinType(attrName);
            if (skinType != null) {
                //资源名称  目前只有attrValue  是一个int类型
                String resName = getResName(context,attrValue);
                if (TextUtils.isEmpty(resName)){
                    continue;
                }
                SkinAttr skinAttr = new SkinAttr(resName,skinType);
                skinAttrs.add(skinAttr);
            }
        }
        return skinAttrs;
    }

    /**
     * 获取资源的名称
     * @param context
     * @param attrValue
     * @return
     */
    private static String getResName(Context context, String attrValue) {
        if (attrValue.startsWith("@")) {
            attrValue = attrValue.substring(1);
            int resId = Integer.parseInt(attrValue);
            return context.getResources().getResourceEntryName(resId);
        }
        return null;
    }

    /**
     * 通过名称获取SkinType
     * @param attrName
     * @return
     */
    private static SkinType getSkinType(String attrName) {
        SkinType[] skinTypes = SkinType.values();
        for (SkinType skinType : skinTypes) {
            if (skinType.getResName().equals(attrName)) {//skinType.equals(attrName)
//                Log.e(TAG, "getSkinType: " + skinType.getResName() + ",skinType:" + skinType + ",skinType.toString:" + skinType.toString() +
//                      ",skinType.ordinal:" + skinType.ordinal() + ",skinType.name:" + skinType.name());
                //getSkinType:background,skinType:background,skinType.toString:background,skinType.ordinal:1,skinType.name:BACKGROUND
                return skinType;
            }
        }
        return null;
    }
}
