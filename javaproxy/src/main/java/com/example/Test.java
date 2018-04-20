package com.example;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhb on 2018/2/7.
 */

public class Test {
    public static void main(String[] args) {
//        "��֤bug1"
//        String string = "验证bug1";
//        String stringGBK = "楠岃瘉bug1";
//        try {
//            byte[] t1 = stringGBK.getBytes("ISO-8859-1");
//            String t2 = new String(t1,"UTF-8");
//            String t3 = new String(t2.getBytes("ISO-8859-1"),"gbk");
//
//
//
//            byte[] sour = string.getBytes("utf-8");
//            String gbk = new String(sour, "gbk");
//            System.out.println(gbk);
//
//            String iso = new String(gbk.getBytes("gbk"), "ISO-8859-1");
//            System.out.println(gbk);
//
//            String gbkt = new String(iso.getBytes("ISO-8859-1"),"utf-8");
//            System.out.println(gbkt);
//
//
//            byte[] bytes = gbk.getBytes("gbk");
//            String s = new String(bytes, "utf-8");
//
//            System.out.println(s);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        String a1= "abcdefgh12345678";
//        byte[] bytes = a1.getBytes();
//        byte[] a = {16, 1, 15, 2, 14, 3, 13, 4, 12, 5, 11, 6, 10, 7, 9, 8};
//        String t2 = new String(a);
//        System.out.println(t2);
        Gson gson = new Gson();

//        String encrypt = new AESCrypt().encrypt("123456");
//        System.out.println(encrypt);
        Map<String,String> map = new HashMap<>();
        System.out.println(map.toString());
        map.put("key","value");
        map.put("key1","value1");
        System.out.println(gson.toJson(map));

        List<PhoneInfo> list = new ArrayList<>();
        System.out.println(list.toString());

        for (int i = 0; i < 5; i++) {

            PhoneInfo info = new PhoneInfo(i+"",i+"");
            list.add(info);
        }

        String s = gson.toJson(list);
        System.out.println(s);

    }

    public static class PhoneInfo {
        private String name;
        private String number;

        public PhoneInfo(String name, String number) {
            this.name = name;
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public String getNumber() {
            return number;
        }
    }
}
