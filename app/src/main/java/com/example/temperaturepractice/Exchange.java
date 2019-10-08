package com.example.temperaturepractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Exchange extends AppCompatActivity implements Runnable{
    TextView out,input;
    String TAG = "rate";
    float dollarRate;// = 0.1403f;
    float euroRate;// = 0.1278f;
    float wonRate;// = 167.9202f;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        input = findViewById(R.id.input);
        out = findViewById(R.id.output);

        Intent intent = getIntent();
        /*dollarRate = intent.getFloatExtra("dollar",0.0f);
        euroRate = intent.getFloatExtra("euro",0.0f);
        wonRate = intent.getFloatExtra("won",0.0f);*/

        //SharedPreferences sharedPreferences =getSharedPreferences("exchangeRate.xml", Activity.MODE_PRIVATE);
        Thread t=new Thread(this);
        t.start();
        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==5){
                    String str=(String)msg.obj;
                    dollarRate=1/(Float.parseFloat(str)/100);
                    Log.i(TAG,"handleMessage:getMessae msg="+str);
                }
                if(msg.what==6){
                    String str=(String)msg.obj;
                    euroRate=1/(Float.parseFloat(str)/100);
                    Log.i(TAG,"handleMessage:getMessae msg="+str);
                }
                if(msg.what==7){
                    String str=(String)msg.obj;
                    wonRate=1/(Float.parseFloat(str)/100);
                    Log.i(TAG,"handleMessage:getMessae msg="+str);
                }
                super.handleMessage(msg);
            }
        };

        /*dollarRate=sharedPreferences.getFloat("dollar",0.0f);  通过读取文件获得数据
        euroRate=sharedPreferences.getFloat("euro",0.0f);
        wonRate=sharedPreferences.getFloat("won",0.0f);*/
    }

    public static boolean isNumericZidai(String str) {
        for (int i = 0; i < str.length(); i++) {
            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    public void show(float a){
        String rmb = String.valueOf(input.getText());
        if (rmb.isEmpty()||!isNumericZidai(rmb)){
            Toast.makeText(this,"请输入正确的数字类型！",Toast.LENGTH_SHORT
            ).show();
        }
        else {
            float rm = Float.parseFloat(rmb);
            float result = rm * a;
            out.setText(String.format("%.2f", result));
        }
    }

    public void dollar(View v){
        show(dollarRate);
    }

    public void euro(View v){
        show(euroRate);
    }

    public void won(View v){
        show(wonRate);
    }

    public void conf(View v){
        Intent config = new Intent(this,ConfigActivity.class);
        config.putExtra("dollar",dollarRate);
        config.putExtra("euro",euroRate);
        config.putExtra("won",wonRate);

        startActivityForResult(config,1);
    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==1 && resultCode==2){
            dollarRate = data.getFloatExtra("dollar",0.0f);
            euroRate = data.getFloatExtra("euro",0.0f);
            wonRate = data.getFloatExtra("won",0.0f);
        }
        super.onActivityResult(requestCode,resultCode,data);
    }


    @Override
    public void run(){
        Log.i(TAG,"RUN:run()...");
        String url= null;
        try {
            url = "http://www.usd-cny.com/bankofchina.htm";
            Document doc = Jsoup.connect(url).get();
            Element table=doc.getElementsByTag("table").first();
            Elements tds=table.getElementsByTag("td");
            int rowDollar=27;
            int rowEuro=8;
            int rowWon=14;
            Element tdD=tds.get(rowDollar*6-1);
            Element tdE=tds.get(rowEuro*6-1);
            Element tdW=tds.get(rowWon*6-1);
            euroRate=Float.parseFloat(tdE.text());
            wonRate=Float.parseFloat(tdW.text());
            Message msg=handler.obtainMessage(5);  //send data to main thread through message
            msg.obj=tdD.text();
            handler.sendMessage(msg);
            Message msg1=handler.obtainMessage(6);  //send data to main thread through message
            msg.obj=tdE.text();
            handler.sendMessage(msg);
            Message msg2=handler.obtainMessage(7);  //send data to main thread through message
            msg.obj=tdW.text();
            handler.sendMessage(msg);
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   /* public void run() {
        /*Message msg=handler.obtainMessage(5);  //send data to main thread through message
        msg.obj="Hello from run()";
        handler.sendMessage(msg);
        Log.i(TAG,"RUN:run()...");*/
    /*    URL url= null;
        try {
            url = new URL("http://www.usd-cny.com/bankofchina.htm");
            HttpURLConnection http=(HttpURLConnection) url.openConnection();
            InputStream in=http.getInputStream();
            String html=inputStream2String(in);
            Log.i(TAG,"run:html="+html);
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String inputStream2String(InputStream inputStream) throws IOException{
        final int bufferSize=1024;
        final char[] buffer=new char[bufferSize];
        final StringBuilder out=new StringBuilder();
        Reader in =new InputStreamReader(inputStream,"gb2312");
        while (true){
            int rsz = in.read(buffer,0,buffer.length);
            if(rsz<0)
                break;
            out.append(buffer,0,rsz);
        }
        return out.toString();
    }*/

}
