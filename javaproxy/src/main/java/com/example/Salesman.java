package com.example;

/**
 * Created by zhb on 2017/8/11.
 */

public class Salesman implements IBank {
    private Man man;

    public Salesman(Man man) {
        this.man = man;
    }

    @Override
    public void applyBank() {
        //买饭
        System.out.println("先搞一些事情");
        //调用我们的方法
        //付钱
        man.applyBank();
        System.out.println("完成");
    }

    @Override
    public void loseBank() {
        System.out.println("先搞一些事情");
        //调用我们的方法
        man.loseBank();
        System.out.println("完成");
    }

    //发现问题：代码量增加了
}
