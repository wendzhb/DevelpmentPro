package com.example;

/**
 * Created by zhb on 2017/8/11.
 */

public class Man implements IBank {
    @Override
    public void applyBank() {
        //快捷键 sout
        System.out.println("办卡");
    }

    @Override
    public void loseBank() {
        System.out.println("挂失");
    }

    public static void main(String[] args) {
        Client client = new Client();
    }
}
