package com.example.loadermanagertest;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private LoaderManager manager;
    private ListView listView;
    private AlertDialog alertDialog;
    private SimpleCursorAdapter mAdapter;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView1);
        //使用一个SimpleCursorAdapter，布局使用android自带的布局资源simple_list_item_1， android.R.id.text1 为simple_list_item_1中TextView的Id
        mAdapter = new SimpleCursorAdapter(MainActivity.this, android.R.layout.simple_list_item_1, null, new String[]{"name"}, new int[]{android.R.id.text1}, 0);
        //获取loader管理器
        manager = getSupportLoaderManager();
        //初始化并启动一个Loader,设定表示为1000，并指定一个回调函数
        manager.initLoader(1000, null, callback);
        //为listview注册一个上下文菜单
        registerForContextMenu(listView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contentmenu, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //声明一个上下文菜单，contentmenu中声明了两个菜单，添加和删除
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contentmenu, menu);
    }

    //单击单个的item，弹出菜单选项，选择添加还是删除
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_add:
                //添加一个对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_name, null);
                Button btnAdd = (Button) view.findViewById(R.id.btnAdd);
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText etAdd = (EditText) view.findViewById(R.id.username);
                        String name = etAdd.getText().toString();
                        //使用contentreslover进行删除操作，根据name字段
                        ContentResolver contentResolver = getContentResolver();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("name", name);
                        Uri uri = Uri.parse("content://com.example.loadermanagertest.PersonContentProvider/person");
                        Uri result = contentResolver.insert(uri, contentValues);
                        if (result != null) {
                            //result不等于空证明添加成功，重新启动loader，注意表示需要和之前init的表示一致。
                            manager.restartLoader(1000, null, callback);
                        }
                        alertDialog.dismiss();

                        Log.i(TAG, "——>>添加数据成功, name = " + name);
                    }
                });
                builder.setView(view);
                alertDialog = builder.show();
                return true;
            case R.id.menu_delete:
                //获取菜单选项的信息
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                //获取到选项的textview控件，并得到选中项的内容
                TextView tv = (TextView)info.targetView;
                String name = tv.getText().toString();
                //使用contentresolver进行删除操作
                Uri uri = Uri.parse("content://com.example.loadermanagertest.PersonContentProvider/person");
                ContentResolver contentResolver = getContentResolver();
                String where = "name = ?";
                String[] selectionArgs = {name};
                int count = contentResolver.delete(uri, where, selectionArgs);
                if (count == 1){
                    //这个操作仅删除单挑记录，如果删除行为1，则重新启动loader
                    manager.restartLoader(1000, null,callback);
                }
                Log.i(TAG, "--->>删除数据成功， name = " + name);
                return true;
            default:
                return super.onContextItemSelected(item);
        }


    }

    //loader的回调接口，在这里异步加载数据库的内容，显示在listview上，同时能够自动更新
    private LoaderManager.LoaderCallbacks<Cursor> callback = new LoaderManager.LoaderCallbacks<Cursor>() {
        @NonNull
        //在loader创建的时候被调用，这里使用一个contentprovider获取数据，所以使用cursorloader返回数据
        @Override
        public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
            Uri uri = Uri.parse("content://com.example.loadermanagertest.PersonContentProvider/person");
            CursorLoader loader = new CursorLoader(MainActivity.this, uri, null,null,null,null);
            Log.i(TAG, "--->>onCreateLoader被执行， ");
            return loader;
        }
        //完成对ui的数据提取，更新ui
        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
            //吧数据提取出来，放到适配器中完成对ui的更新操作（刷新simplecursoradapter的数据)
            mAdapter.swapCursor(cursor);
            listView.setAdapter(mAdapter);
            Log.i(TAG,"--->>onLoadFinished被执行。");
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            //当Loader被从LoaderManager中移除的时候执行，清空SimpleCUrsorAdapter适配器的Cursor
            mAdapter.swapCursor(null);
            Log.i(TAG,"--->>onLoadReset被执行。");
        }
    };
}
