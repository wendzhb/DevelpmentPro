package com.example.dynamic;

import com.example.IBank;
import com.example.Man;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by zhb on 2017/8/11.
 */

public class DynamicClient {
    //动态代理 Java自带动态代理机制
    public static void main(String[] args) {
        Man man = new Man();
        //获取了代理 代理了所有方法而且都会走InvocationHandler里面的invoke方法
        IBank proxy = (IBank) Proxy.newProxyInstance(
//                IBank.class.getClassLoader(),//第一个参数 classLoader
                man.getClass().getClassLoader(),
//                new Class<?>[]{IBank.class},//接口类数组
                man.getClass().getInterfaces(),
                new BankInvokationHandler(man));//回调
        //动态代理 解析interface所有的方法，新建一个class, class的名 包名+$Proxy, 实例化了proxy对象
        //proxy里面包含InvocationHandler,每次掉方法其实执行的是BankInvokationHandler里面的invoke方法
        proxy.applyBank();
        System.out.println("---------------------");
        proxy.loseBank();
    }

    private static class BankInvokationHandler implements InvocationHandler {
        private IBank man;

        public BankInvokationHandler(Man man) {
            this.man = man;
        }

        /**
         * @param o       代理对象
         * @param method
         * @param objects
         * @return
         * @throws Throwable
         */
        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            if (man != null) {
                //方法回调  方法执行还是要Man
                System.out.println("做一些事情");
                Object object = method.invoke(man, objects);
                System.out.println("完成");
                return object;
            }
            return null;
        }
    }
}
