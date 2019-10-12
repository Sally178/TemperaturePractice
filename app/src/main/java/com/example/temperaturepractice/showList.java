package com.example.temperaturepractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class showList extends ListActivity implements Runnable {
    String TAG="Info";
    Handler handler;
    String[] list1={};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread t=new Thread(this);
        t.start();
        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==5){
                    list1 =(String[]) msg.obj;
                    Log.i("Return", "nothing");
                    ListAdapter adapter=new ArrayAdapter<String>(showList.this,android.R.layout.simple_list_item_1,list1);
                    setListAdapter(adapter);
                }
                super.handleMessage(msg);
            }
        };

    }

    @Override
    public void run() {
        Log.i(TAG,"RUN:run()...");
        String url= null;
        try {
            url = "http://www.usd-cny.com/bankofchina.htm";
            Document doc = Jsoup.connect(url).get();
            Element table=doc.getElementsByTag("table").first();
            Elements trs=table.getElementsByTag("tr");
            //ArrayList<String> rate=new ArrayList<String>();
            String[] rate=new String[trs.size()-1];
            Log.i("NUMBER_ROW", String.valueOf(trs.size()));
            int i;
            for(i=0;i<trs.size()-1;i++){
                int get=i+1;
                Element tr= trs.get(get);
                String td_name=tr.getElementsByTag("td").first().text();
                String td_value=tr.getElementsByTag("td").last().text();
                rate[i]=td_name+":"+td_value;
            }
            Log.i("Send",rate[0]);

            Message msg=handler.obtainMessage(5);  //send data to main thread through message
            msg.obj=rate;
            handler.sendMessage(msg);
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
