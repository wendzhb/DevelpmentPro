package com.example;

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
//        Gson gson = new Gson();

//        String encrypt = new AESCrypt().encrypt("123456");
//        System.out.println(encrypt);
//        Map<String,String> map = new HashMap<>();
//        System.out.println(map.toString());
//        map.put("key","value");
//        map.put("key1","value1");
//        System.out.println(gson.toJson(map));
//
//        List<PhoneInfo> list = new ArrayList<>();
//        System.out.println(list.toString());
//
//        for (int i = 0; i < 5; i++) {
//
//            PhoneInfo info = new PhoneInfo(i+"",i+"");
//            list.add(info);
//        }
//
//        String s = gson.toJson(list);
//        System.out.println(s);

//        int k = 1;
//        System.out.println(k == 1 ? "!" : "2");
//
//        int[] a = {1, 1, 1, 12, 2, 2, 1, 1, 4, 4, 5, 1};
//        Map<Integer, Integer> map = new HashMap<>();
//        for (int i = 0; i < a.length; i++) {
//            if (map.get(a[i]) == null) {
//                map.put(a[i], 1);
//            } else {
//                Integer value = map.get(a[i]);
//                map.put(a[i], value + 1);
//            }
//        }
//        System.out.println(map);
//        int max = 0, num = 0;
//        for (Integer key : map.keySet()) {
//            if (map.get(key) > max) {
//                max = map.get(key);
//                num = key;
//            }
//        }
//        System.out.println(num);

        getLength("abcdef", "afcdek");

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

    public static void getLength(String str1, String str2) {
        char[] str1s = str1.toCharArray();
        char[] str2s = str2.toCharArray();
        int a = str1.length();
        int b = str2.length();
        int[][] record = new int[a][b];

        int end = 0, length = 0;
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; j++) {

                if (str1s[i] == str2s[j]) {
                    if (i == 0 || j == 0) {
                        record[i][j] = 1;
                    } else {
                        record[i][j] = record[i - 1][j - 1] + 1;
                    }

                } else {
                    record[i][j] = 0;
                }

                if (record[i][j] > length) {
                    length = record[i][j];
                    end = i;
                }
            }
        }

        System.out.println(str1.substring(end - length+1, end+1) + ",end = " + end + ",length = " + length);
    }
}
