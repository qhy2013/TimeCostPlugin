package com.example.asmdemo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.zjy.cost.TimeTotal;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test();
    }

    @TimeTotal
    public void test(){
        System.out.println("this is test");
    }
}
