package com.example.kaifa.essayjoke.model;

import org.litepal.crud.DataSupport;

/**
 * Created by zhb on 2017/4/13.
 */

public class Person extends DataSupport {
    private String name;
    private int age;

    //默认的构造方法
    public Person(){

    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

