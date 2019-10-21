package com.example.temperaturepractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class multi_list extends AppCompatActivity implements Runnable, AdapterView.OnItemLongClickListener {
    String TAG="Info";
    Handler handler;
    ArrayList<HashMap<String,String>> list1=new ArrayList<HashMap<String, String>>();
    ArrayList<RateItem> rateItemList;
    //ArrayList<RateItem> list1;
    SimpleAdapter simpleListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_list);
        final GridView gridView =(GridView) findViewById(R.id.multiList);
        Thread t=new Thread(this);
        t.start();
       /* handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==5){
                    //list1 =(ArrayList<HashMap<String, String>>) msg.obj;
                    list1=(ArrayList<RateItem>)msg.obj;
                    Log.i("Return", "nothing");
                    //ListAdapter adapter=new ArrayAdapter<String>(multi_list.this,android.R.layout.simple_list_item_1,list1);
                    //simpleListAdapter=new SimpleAdapter(multi_list.this, list1, R.layout.list_layout,
                            //new String[]{"ItemTitle","ItemValue"},new int[]{R.id.textView3,R.id.textView4});
                    gridView.setAdapter(simpleListAdapter);
                }
                super.handleMessage(msg);
            }
        };*/
        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==5){
                    DBManager dbManager=new DBManager(multi_list.this);
                    List<RateItem> rateItems=dbManager.listAll();
                    Log.i("aaa", String.valueOf(rateItems.size()));
                    for (int i=1;i<=rateItems.size();i++){
                        RateItem item=rateItems.get(i-1);
                        Log.i("bbb", rateItems.get(1).getCurName());
                        HashMap<String,String> map=new HashMap<String, String>();
                        map.put("ItemTitle", item.getCurName());
                        Log.i("aaa", item.getCurName());
                        map.put("ItemValue", item.getCurRate());
                        list1.add(map);
                    }
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
        int year,month,day;
        SharedPreferences sp=getSharedPreferences("date.xml", Activity.MODE_PRIVATE);
        year=sp.getInt("year", 0);
        month=sp.getInt("month", 0);
        day=sp.getInt("date", 0);
        Calendar t=Calendar.getInstance();
        if((year==t.get(Calendar.YEAR)) && (month ==t.get(Calendar.MONTH)) && (day==t.get(Calendar.DAY_OF_MONTH))){  //获取过数据了
            Log.i("ccc", "first time");
            Message msg=handler.obtainMessage(5);  //send data to main thread through message
            handler.sendMessage(msg);
        }
        else{
            Log.i("ccc", "not the first time");
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("year",t.get(Calendar.YEAR));
            editor.putInt("month",t.get(Calendar.MONTH));
            editor.putInt("date",t.get(Calendar.DAY_OF_MONTH));
            editor.apply();
            String url= null;
            Document doc=null;
            rateItemList=new ArrayList<RateItem>();
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

                    //ArrayList<HashMap<String, String>> listItems = new ArrayList<HashMap<String, String>>();
                    Log.i("NUMBER_ROW", String.valueOf(trs.size()));
                    for(int i=0;i<trs.size()-1;i++){
                        int get=i+1;
                        //HashMap<String,String> map = new HashMap<String, String>();
                        Element tr= trs.get(get);
                        String td_name=tr.getElementsByTag("td").first().text();
                        String td_value=tr.getElementsByTag("td").last().text();
                        RateItem rateItem=new RateItem(td_name,td_value);
                        //map.put("ItemTitle", "Rate:"+td_name);
                        //map.put("ItemValue", "detail:"+td_value);
                        //listItems.add(map);
                        rateItemList.add(rateItem);
                    }
                    Message msg=handler.obtainMessage(5);  //send data to main thread through message
                    //msg.obj=listItems;
                    //msg.obj=rateItemList;
                    DBManager dbManager =new DBManager(multi_list.this);
                    dbManager.deleteAll();
                    dbManager.addAll(rateItemList);
                    handler.sendMessage(msg);
                }
            }
            catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
