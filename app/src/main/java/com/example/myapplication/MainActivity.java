package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNativeDirectory(getApplication());
        System.load("/data/data/com.example.myapplication/files/libc++_shared.so");
        System.load("/data/data/com.example.myapplication/files/libaaa.so");

    }
    public static void initNativeDirectory(Application application) {
        if (hasDexClassLoader()) {
            try {
                Log.v("TAG", "create new native dir");
                createNewNativeDir(application);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private static void createNewNativeDir(Context context) throws Exception{
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        Object pathList = getPathList(pathClassLoader);
        //获取当前类的属性
        Object nativeLibraryDirectories = pathList.getClass().getDeclaredField("nativeLibraryDirectories");
        ((Field) nativeLibraryDirectories).setAccessible(true);
        //获取 DEXPATHList中的属性值
        ArrayList pathObjects = (ArrayList)((Field) nativeLibraryDirectories).get(pathList);
        Object[] patharr = pathObjects.toArray();
        //添加自定义.so路径
        String mylibpath = "/data/data/com.example.myapplication/files";//context.getFilesDir().getAbsolutePath();

        ArrayList<File> fl = new ArrayList<>();
        fl.add(new File(mylibpath));
        for (int i = 0; i < patharr.length; i++) {
            fl.add(new File(patharr[i].toString()));
        }
        ((Field) nativeLibraryDirectories).set(pathList, fl);
    }
    private static Object getPathList(Object obj) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(obj, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }
    private static Object getField(Object obj, Class cls, String str) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = cls.getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get(obj);
    }
    /**
     *  仅对4.0以上做支持
     * @return
     */
    private static boolean hasDexClassLoader() {
        try {
            Class.forName("dalvik.system.BaseDexClassLoader");
            return true;
        } catch (ClassNotFoundException var1) {
            return false;
        }
    }

}
