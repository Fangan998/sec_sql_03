package com.example.a12102020;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private  MyDB db =null;
    /* 資料表欄位 */
    // private final static String	_ID	= "_id";
    // private final static String	NAME = "name";
    // private final static String	PRICE = "price";

    Button btn_add,btn_clear,btn_del,btn_modify;
    EditText edTxtID,edTxtPr;
    ListView listView01;
    Cursor cursor;
    long myId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*---------Button-------------*/
        btn_add=(Button)findViewById(R.id.btn_new) ;
        btn_clear=(Button)findViewById(R.id.btn_clear);
        btn_del=(Button)findViewById(R.id.btn_delete);
        btn_modify=(Button)findViewById(R.id.btn_modify);
        /*------------EditText-----------*/
        edTxtID=(EditText)findViewById(R.id.edTxt_name);
        edTxtPr=(EditText)findViewById(R.id.edTxt_py);
        /*----------ListView-----------*/
        listView01=(ListView)findViewById(R.id.list_sql);
        /*-----------Listener---------*/
        btn_add.setOnClickListener(myListener);
        btn_clear.setOnClickListener(myListener);
        btn_del.setOnClickListener(myListener);
        btn_modify.setOnClickListener(myListener);
        listView01.setOnItemClickListener(listview01Listener);
        /*------------DB-------------*/
        db =new MyDB(this);
        db.open();
        cursor=db.getAll();
        UpdateAdapter(cursor);
        
    }

    private ListView.OnItemClickListener listview01Listener= new ListView.OnItemClickListener(){
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    ShowData(id);
                    cursor.moveToPosition(position);
                }
            };

    private void ShowData(long id){
        Cursor c = db.get(id);
        myId=id;
        edTxtID.setText(c.getString(1));
        edTxtPr.setText("" + c.getInt(2));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    private  Button.OnClickListener myListener =new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            try{
                switch (v.getId()){
                    case R.id.btn_new:{ // 新增
                        int price=Integer.parseInt(edTxtPr.getText().toString());
                        String name=edTxtID.getText().toString();
                        if ( db.append(name,price)>0){
                            cursor=db.getAll();// 載入全部資料
                            UpdateAdapter(cursor);  // 載入資料表至 ListView 中
                            ClearEdit();
                        }
                        break;
                    }case R.id.btn_modify: {  //修改
                        int price=Integer.parseInt(edTxtPr.getText().toString());
                        String name=edTxtID.getText().toString();
                        if (db.update(myId,name,price)){
                            cursor=db.getAll();// 載入全部資料
                            UpdateAdapter(cursor);  // 載入資料表至 ListView 中
                        }
                        break;
                    }case R.id.btn_delete: { //刪除
                        if (cursor != null && cursor.getCount() >= 0){
                            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("確定刪除");
                            builder.setMessage("確定要刪除" + edTxtID.getText() + "這筆資料?");
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int i) {
                                }
                            });
                            builder.setPositiveButton("確定",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int i) {
                                    if (db.delete(myId)){
                                        cursor=db.getAll();// 載入全部資料
                                        UpdateAdapter(cursor); // 載入資料表至 ListView 中
                                        ClearEdit();
                                    }
                                }
                            });
                            builder.show();
                        }
                        break;
                    }case R.id.btn_clear: { //清除
                        ClearEdit();
                        break;
                    }
                }
            }catch (Exception err){
                Toast.makeText(getApplicationContext(), "資料不正確!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void ClearEdit() {
        edTxtPr.setText("");
        edTxtID.setText("");
    }


    private void UpdateAdapter(Cursor cursor) {
        if(cursor !=null && cursor.getCount()>=0){
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_2,        // 包含兩個資料項
                    cursor,                                     // 資料庫的 Cursors 物件
                    new String[]{"name","price"},               // name、price 欄位
                    new int[]{android.R.id.text1,android.R.id.text2},0);
            listView01.setAdapter(adapter);                     // 將adapter增加到listview01中
        }
    }
}