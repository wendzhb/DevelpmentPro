package com.example.alex.framelibrary.db;

import android.text.TextUtils;

import java.util.Locale;

/**
 * Created by zhb on 2017/4/13.
 */

public class DaoUtil {
    private DaoUtil(){
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取类的表名
     * @param clazz
     * @return
     */
    public static String getTableName(Class<?> clazz){
        return clazz.getSimpleName();
    }

    /**
     * 获取type的列类型
     * @param type
     * @return
     */
    public static String getColumnType(String type){
        String value = null;

        if (type.contains("String")){
            value = "text";
        }else if (type.contains("int")){
            value = "integer";
        }else if (type.contains("boolean")){
            value = "boolean";
        }else if (type.contains("float")){
            value = "float";
        }else if (type.contains("double")){
            value = "double";
        }else if (type.contains("char")){
            value = "varchar";
        }else if (type.contains("long")){
            value = "long";
        }
        return value;

    }

    /**
     * 分析其
     * @param string
     * @return
     */
    public static String capitalize(String string) {

        if (!TextUtils.isEmpty(string)){
            return string.substring(0,1).toUpperCase(Locale.US) + string.substring(1);
        }
        return null;
    }
}
