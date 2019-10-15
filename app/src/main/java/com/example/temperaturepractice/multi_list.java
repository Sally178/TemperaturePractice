package com.example.temperaturepractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
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

public class multi_list extends AppCompatActivity implements Runnable, AdapterView.OnItemLongClickListener {
    String TAG="Info";
    Handler handler;
    ArrayList<HashMap<String,String>> list1;
    SimpleAdapter simpleListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_list);
        final GridView gridView =(GridView) findViewById(R.id.multiList);
        Thread t=new Thread(this);
        t.start();
        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==5){
                    list1 =(ArrayList<HashMap<String, String>>) msg.obj;
                    Log.i("Return", "nothing");
                    //ListAdapter adapter=new ArrayAdapter<String>(multi_list.this,android.R.layout.simple_list_item_1,list1);
                    simpleListAdapter=new SimpleAdapter(multi_list.this, list1, R.layout.list_layout,
                            new String[]{"ItemTitle","ItemValue"},new int[]{R.id.textView3,R.id.textView4});
                    gridView.setAdapter(simpleListAdapter);
                }
                super.handleMessage(msg);
            }
        };
        gridView.setEmptyView(findViewById(R.id.nodata));  //没有数据时的显示
        gridView.setOnItemLongClickListener(this);
    }

    @Override
    public void run() {
        Log.i(TAG,"RUN:run()...");
        String url= null;
        Document doc=null;
        try {
            url = "http://www.usd-cny.com/bankofchina.htm";
            doc = Jsoup.connect(url).get();
            if(doc==null){
                Log.i(TAG, "NO");
            }
           if(doc!=null){
                Log.i(TAG, "YES");
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
                    map.put("ItemTitle", "Rate:"+td_name);
                    map.put("ItemValue", "detail:"+td_value);
                    listItems.add(map);
                }
                Message msg=handler.obtainMessage(5);  //send data to main thread through message
                msg.obj=listItems;
                handler.sendMessage(msg);
            }
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("确认删除当前数据？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        list1.remove(i);
                        simpleListAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("否", null);
        builder.create().show();
        return false;
    }
}
