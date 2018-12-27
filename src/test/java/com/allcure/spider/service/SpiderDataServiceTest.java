package com.allcure.spider.service;

import org.junit.Test;

import static org.junit.Assert.*;

public class SpiderDataServiceTest {

    @Test
    public void jsonToExcel() {
        SpiderDataService spiderDataService = new SpiderDataService();
        spiderDataService.jsonToExcel();
    }

    @Test
    public void decodeMosaiq() {
        String xx = "194C7CB2E21A3A5A7A9ABADAFA1A3A";
        String code = "930608";

//        System.out.println(xx.length());
//
//        for(int i= 0; i< xx.length(); i++) {
//            Integer integer = Integer.parseInt(String.valueOf(xx.charAt(i)), 16);
//        }

        byte[] ttt = hexStringToBytes(xx);
        System.out.println(ttt);
        String s = new String(ttt);
        System.out.println(s);
    }

    public static String  convertStringToHex(String str){

        char[] chars = str.toCharArray();

        StringBuffer hex = new StringBuffer();
        for(int i = 0; i < chars.length; i++){
            hex.append(Integer.toHexString((int)chars[i]));
        }

        return hex.toString();
    }


    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        // toUpperCase将字符串中的所有字符转换为大写
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        // toCharArray将此字符串转换为一个新的字符数组。
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;

    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

}