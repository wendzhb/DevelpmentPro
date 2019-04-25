package com.example;

public class Client {

    //快捷键 pvsm
    public static void main(String[] args) {
        //例子：你要别人买饭，钱是你掏
        Man man = new Man();
        Salesman salesman = new Salesman(man);
        //饭是别人买
        salesman.applyBank();

    }

}
