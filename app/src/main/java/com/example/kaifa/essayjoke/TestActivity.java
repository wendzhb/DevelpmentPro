package com.example.kaifa.essayjoke;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.alex.framelibrary.http.HttpCallBack;
import com.example.alex.framelibrary.http.OkHttpEngine;
import com.example.kaifa.essayjoke.model.DiscoverListResult;
import com.zhbstudy.baselibrary.base.BaseActivity;
import com.zhbstudy.baselibrary.dialog.MyAlertDialog;
import com.zhbstudy.baselibrary.http.HttpUtils;

public class TestActivity extends BaseActivity {

    @Override
    protected void initData() {

        /*//1. 为什么用factory 目前的数据是在 内存卡中， 有时候我们需要放到data/data/xxx/database
        //获取的factory不一样那么写入的位置 是可以不一样的

        //2.面向接口编程，获取IDaoSupport 那么不需要关心实现 ， 目前是我们自己写的，方便以后使用第三方的

        //3.就是为了高扩展

        IDaoSupport<Person> daoSupport = DaoSupportFactory.getFactory().getDao(Person.class);

        //最少的知识原则 插入的是对象
        //daoSupport.insert(new Person("test",1));

        List<Person> persons = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            persons.add(new Person("test", 22 + i));
        }

        long startTime = System.currentTimeMillis();
        daoSupport.insert(persons);
//        DataSupport.saveAll(persons);
        long endTime = System.currentTimeMillis();

        Log.e("test", "time = " + (endTime - startTime));

        //第三方的 Litepal比较方便

        //自己的 14463 --> 采用事务 110 -->最后优化74
        //litepal 371

        // 查询所有
        List<Person> person1 = daoSupport.querySupport().queryAll();

        //根据条件进行查询
        List<Person> person2 = daoSupport.querySupport()
                .selection("age = ?").selectionArgs("23").query();

        //根据条件进行删除
        daoSupport.delete("age = ?", "23");

        //根据条件进行更新
        Person person = new Person("Jack", 24);
        daoSupport.update(person, "age = ?", "23");*/

        HttpUtils.with(this).get()
                .url("http://is.snssdk.com/2/essay/discovery/v3/")//路径参数，放到jni里面
                .exchangeEngine(new OkHttpEngine())//切换引擎
                .addParams("iid", "6152551759")
                .addParams("aid", "7")
                .cache(true)//读取缓存
                .execte(new HttpCallBack<DiscoverListResult>() {
                    @Override
                    public void onError(Exception e) {
                        Log.e("tag", "onerror");
                    }

                    @Override
                    public void onSuccess(DiscoverListResult result) {
                        Log.e("tag", result.getMessage() + result.getData().getCategories().getName());
                        //没有缓存， 有了数据库 以及网络引擎

                        //思路 某些接口如果需要缓存自己标识
                    }

                });


    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_test);
    }

    public void andfix(View view) {

        MyAlertDialog dialog = new MyAlertDialog.Builer(this)
                .setContentView(R.layout.dialog_custom)
                .setCancelable(true)
                .setText(R.id.tv_dialog_custom, "修改")
//                .fromBottom(true)
                .setDefaultAnimation()
                .fullWidth()
                .create();
        dialog.show();
        //dialog操作点击事件
        dialog.setOnClickListener(R.id.tv_dialog_custom, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TestActivity.this, "Bug修复测试" + 2 / 1, Toast.LENGTH_LONG).show();
            }
        });
        //弹出软键盘

    }
}
