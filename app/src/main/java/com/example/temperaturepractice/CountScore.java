package com.example.temperaturepractice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CountScore extends AppCompatActivity {
    TextView out1,out2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_score);

        out1 = findViewById(R.id.output1);
        out2 = findViewById(R.id.output2);
        TextView reset = findViewById(R.id.Reset);

    }

    public void show1(int a){
        int s = Integer.parseInt(String.valueOf(out1.getText())) + a;
        out1.setText(String.valueOf(s));
    }
    public void Click1(View v){
        show1(3);
    }
    public void Click2(View v){
        show1(2);
    }
    public void Click3(View v){
        show1(1);
    }

    public void show2(int a){
        int s = Integer.parseInt(String.valueOf(out2.getText())) + a;
        out2.setText(String.valueOf(s));
    }
    public void Click4(View v){
        show2(3);
    }
    public void Click5(View v){
        show2(2);
    }
    public void Click6(View v){
        show2(1);
    }

    public void Reset(View v){
        out1.setText("0");
        out2.setText("0");
    }
}
