package com.example.loadermanagertest;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.loadermanagertest.dao.PersonDao;


public class PersonContentProvider extends ContentProvider {

    private final String TAG = "PersonContentProvider";
    private PersonDao personDao = null;
    //默认规则是不匹配的
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int PERSON = 1;//操作单行记录
    private static final int PERSONS = 2;//操作多行记录

    //往UriMatcher中添加匹配规则。
    static {
        //添加两个URI筛选
        URI_MATCHER.addURI("com.example.loadermanagertest.PersonContentProvider", "person", PERSONS);
        //使用通配符#匹配任意数字
        URI_MATCHER.addURI("com.example.loadermanagertest.PersonContentProvider", "person/#", PERSON);
    }

    public PersonContentProvider() {
    }

    @Override
    public boolean onCreate() {
        //初始化一个数据持久层
        personDao = new PersonDao(getContext());
        Log.i(TAG, "--->>onCreate()被调用");
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        try {
            int flag = URI_MATCHER.match(uri);
            switch (flag) {
                case PERSON:
                    long id = ContentUris.parseId(uri);
                    String where_value = "id = ?";
                    String[] args = { String.valueOf(id) };
                    cursor = personDao.queryPersons(where_value, args);
                    break;
                case PERSONS:
                    cursor = personDao.queryPersons(selection, selectionArgs);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "--->>查询成功，Count = " + cursor.getCount());
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        int flag = URI_MATCHER.match(uri);
        switch (flag){
            case PERSON:
                return "vnd.android.cursor.item/person";
                //如果是单条记录，则为vnd.android.cursor.item/  + path
            case PERSONS:
                return "vnd.android.cursor.dir/persons";
                //如果是多条记录，则为vnd.android.cursor.dir/  + path
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri resultUri = null;
        //解析uri，返回code
        int flag = URI_MATCHER.match(uri);
        switch (flag) {
            case PERSONS:
                long id = personDao.insertPerson(values);
                resultUri = ContentUris.withAppendedId(uri, id);
                Log.i(TAG, "插入成功， id = " + id);
                Log.i(TAG, "resultUri = " + resultUri.toString());
                System.out.println("insert success");
                break;

        }
        return resultUri;
    }

    //删除记录，selection和selectionArgs是查询的条件，由外部传进来
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = -1;//影像数据的行数
        try {
            int flag = URI_MATCHER.match(uri);
            switch (flag) {
                case PERSON:
                    //单条数据，用ContentUris工具类界许褚结尾的Id
                    long id = ContentUris.parseId(uri);
                    String where_value = "id = ?";
                    String[] args = {String.valueOf(id)};
                    count = personDao.deletePerson(where_value, args);
                    break;
                case PERSONS:
                    count = personDao.deletePerson(selection, selectionArgs);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "--->>删除成功, count = " + count);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = -1;
        try {
            int flag = URI_MATCHER.match(uri);
            switch (flag) {
                case PERSON:
                    long id = ContentUris.parseId(uri);
                    String where_value = "id = ?";
                    String[] args = {String.valueOf(id)};
                    count = personDao.updatePerson(values, where_value, args);
                    break;
                case PERSONS:
                    count = personDao.updatePerson(values, selection, selectionArgs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "--->>更新成功，count = " + count);
        return count;
    }

    @Override
    public Bundle call(String method,  String arg,  Bundle extras) {
        Log.i(TAG, "--->>" + method);
        Bundle bundle = new Bundle();
        bundle.putString("returnCall", "call被执行了");
        return bundle;
    }
}
