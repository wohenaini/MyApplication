package cn.com.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import cn.com.jiutairural.BaseUtil;
import cn.com.jiutairural.WebActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("Mr.Kang", "onCreate: " + BaseUtil.showLog());

        startActivity(new Intent(this, WebActivity.class));
    }
}
