package com.example.temperaturepractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

public class design_adapter extends ListActivity implements Runnable, AdapterView.OnItemClickListener {
    String TAG="Info";
    Handler handler;
    ArrayList<HashMap<String,String>> list1;
    //ListView listView;
    MyAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_list_method2);
        //listView=(ListView) findViewById(R.id.mylist);
        Thread t=new Thread(this);
        t.start();
        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==5){
                    list1 =(ArrayList<HashMap<String,String>>) msg.obj;
                    Log.i("Return", "nothing");
                    myAdapter=new MyAdapter(design_adapter.this,R.layout.list_layout, list1);
                    //listView.setAdapter(myAdapter);
                    design_adapter.this.setListAdapter(myAdapter);
                }
                super.handleMessage(msg);
            }
        };
        Log.i("return", "anything");

        getListView().setOnItemClickListener(this);

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
            ArrayList<HashMap<String, String>> listItems = new ArrayList<HashMap<String, String>>();
            Log.i("NUMBER_ROW", String.valueOf(trs.size()));
            for(int i=0;i<trs.size()-1;i++){
                int get=i+1;
                HashMap<String,String> map = new HashMap<String, String>();
                Element tr= trs.get(get);
                String td_name=tr.getElementsByTag("td").first().text();
                String td_value=tr.getElementsByTag("td").last().text();
                map.put("ItemTitle", td_name);
                map.put("ItemValue", td_value);
                listItems.add(map);
            }
            Message msg=handler.obtainMessage(5);  //send data to main thread through message
            msg.obj=listItems;
            handler.sendMessage(msg);
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Object itemAtPosition = getListView().getItemAtPosition(position);
        HashMap<String,String> map=(HashMap<String, String>) itemAtPosition;
        String titleStr=map.get("ItemTitle");
        String detailStr=map.get("ItemValue");
        Log.i("click", detailStr);
        Intent intent= new Intent(this,Click_list.class);
        intent.putExtra("title", titleStr);
        intent.putExtra("value", Float.parseFloat(detailStr));
        startActivity(intent);
    }
}
