package com.example.temperaturepractice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView inp,out;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inp = findViewById(R.id.input);
        out = findViewById(R.id.output1);

        Button btn  = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = String.valueOf(inp.getText());
                float a = Float.parseFloat(s);
                double b = (a*1.8)+32;
                String s1 = String.format("%.2f",b);
                out.setText(getString(R.string.result)+s1);
            }
        });

    }
}
