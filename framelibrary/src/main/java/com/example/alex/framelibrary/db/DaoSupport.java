package com.example.alex.framelibrary.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by zhb on 2017/4/13.
 */

public class DaoSupport<T> implements IDaoSupport<T> {
    //SQLiteDateBase
    private SQLiteDatabase mSqLiteDatabase;
    //泛型类
    private Class<T> mClazz;

    private QuerySupport<T> mQuerySupport;

    /**
     * 反射时方法参数
     */
    private static final Object[] mPutMethodArgs = new Object[2];
    /**
     * 反射时的方法缓存
     */
    private static final Map<String, Method> mPutMethods = new ArrayMap<>();

    @Override
    public void init(SQLiteDatabase sqLiteDatabase, Class<T> clazz) {
        this.mSqLiteDatabase = sqLiteDatabase;
        this.mClazz = clazz;

        //创建表
        /*"Create table if not exists Person("
                +"id integer primary key autoincrement, "
                +"name text, "
                +"age integer, "
                +"flag boolean)";*/

        StringBuffer sb = new StringBuffer();

        sb.append("create table if not exists ")
                .append(DaoUtil.getTableName(mClazz))
                .append("(id integer primary key autoincrement, ");

        Field[] fields = mClazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);//设置权限
            String name = field.getName();
            Log.e("tag", name);
            String type = field.getType().getSimpleName();//int String boolean
            //type 需要进行转换 int -->integer ;String -->text;
            sb.append(name).append(" ").append(DaoUtil.getColumnType(type)).append(" ,");
        }

        sb.replace(sb.length() - 2, sb.length(), ")");

        String createTableSql = sb.toString();
        Log.e("test", "创建表语句" + createTableSql);

        mSqLiteDatabase.execSQL(createTableSql);
    }


    //插入数据库 t是任意对象
    @Override
    public long insert(T obj) {
        /*db = helper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", person.getname);
        values.put("age", person.getage);
        db.insert("Person", null, values);*/

        //使用的其实还是 原生的使用方式，只是我们封装了一下
        ContentValues values = contentValuesByObj(obj);
        // null 速度比第三方的快一倍左右
        return mSqLiteDatabase.insert(DaoUtil.getTableName(mClazz), null, values);
    }

    @Override
    public void insert(List<T> datas) {
        //批量插入 采用事务
        mSqLiteDatabase.beginTransaction();
        for (T data : datas) {
            //调用单挑插入
            insert(data);
        }
        mSqLiteDatabase.setTransactionSuccessful();
        mSqLiteDatabase.endTransaction();
    }

    private ContentValues contentValuesByObj(T o) {
        ContentValues values = new ContentValues();
        //封装values
        Field[] fields = mClazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                //设置权限，私有和公有都可以访问
                field.setAccessible(true);
                String key = field.getName();
                //获取value
                Object value = field.get(o);

                //put 第二个参数是类型，把它转换
//                if (value instanceof Integer){
//                } else if(){
//                }
                mPutMethodArgs[0] = key;
                mPutMethodArgs[1] = value;
                //方法使用反射， 反射在一定程度上会影响性能
                //源码里面 activity实例 反射 view创建反射
                //第三方以及源码给我们提供的是最好的学习材料  插件换肤

                String filedTypeName = field.getType().getName();
                //使用反射 获取方法 put 缓存方法
                Method putMethod = mPutMethods.get(filedTypeName);
                if (putMethod == null) {

                    //(根据类名、方法名以及方法对应的参数，获取方法，并实现方法的调用)
                    putMethod = ContentValues.class.getDeclaredMethod("put", String.class, value.getClass());
                    mPutMethods.put(filedTypeName, putMethod);

                }
                //通过反射执行
                putMethod.invoke(values, mPutMethodArgs);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } finally {
                mPutMethodArgs[0] = null;
                mPutMethodArgs[1] = null;
            }
        }
        return values;
    }

    //查询
    @Override
    public QuerySupport querySupport() {
        if (mQuerySupport == null) {
            mQuerySupport = new QuerySupport<>(mSqLiteDatabase, mClazz);
        }
        return mQuerySupport;
    }

    //删除
    @Override
    public long delete(String whereClause, String[] whereArgs) {
        return mSqLiteDatabase.delete(DaoUtil.getTableName(mClazz), whereClause, whereArgs);
    }

    //更新 需要对 最原始的写法比较了解 extends sqlitedatebase
    @Override
    public long update(T obj, String whereClause, String[] whereArgs) {
        ContentValues values = contentValuesByObj(obj);
        return mSqLiteDatabase.update(DaoUtil.getTableName(mClazz), values, whereClause, whereArgs);
    }

    //综合到
    // 1.网络引擎的缓存
    // 2.资源加载的源码


}
