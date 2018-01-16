package com.example.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startActivity(new Intent(this, HomeActivity.class));
        finish();
        /*
        try {
            File file = new File("/data/data/" + getPackageName() + "/test.txt");
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write("hello".getBytes());
            outputStream.close();
        } catch (Throwable e) {
            Log.e("kk", "java file", e);
        }

        try {
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/data/data/" + getPackageName() + "/test.db",
                    null);
        } catch (Throwable e) {
            Log.e("kk", "SQLiteDatabase.openOrCreateDatabase", e);
        }
        try {
            String path = "/data/data/" + getPackageName() + "/io.txt";
            JniTest.writeFile(path, "time=" + new Date(System.currentTimeMillis()));
            Log.i("kk", "test.txt=" + JniTest.readFile("/data/data/" + getPackageName() + "/test.txt"));
            Log.i("kk", "io.txt=" + JniTest.readFile(path));
        } catch (Throwable e) {
            Log.e("kk", "JniTest", e);
        }
        */
    }

}
