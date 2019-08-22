package com.songu.shadow.drawing.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;



import java.util.ArrayList;
import java.util.List;

import com.songu.shadow.drawing.model.MessageModel;

//Will use Future work for DB
public class ProfileDBManager {
    SQLiteDatabase database;
    String dbName = "test_db_name";
    String createTable = "create table history (id integer primary key AUTOINCREMENT ,fromid integer,toid integer,message string,isAttach integer,fname string,timestamp string,mid integer);";
    String dbname = "chatDB";
    String tableName = "history";
    UsageSQLLite helper;
    SQLiteDatabase db;
    public ProfileDBManager()
    {

    }
    public ProfileDBManager(Context con)
    {
        helper = new UsageSQLLite(con,dbname,null,1);
    }
    public void insertData(MessageModel model){
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fromid", model.fromId);
        values.put("toid", model.toId);
        values.put("message", model.content);
        values.put("isAttach", model.isFile);
        values.put("fname", model.attach);
        values.put("timestamp",model.timeStamp);
        values.put("mid",model.mId);
        db.insert(tableName, null, values);
    }
    public void updateData(MessageModel model)
    {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fromid", model.fromId);
        values.put("toid", model.toId);
        values.put("message", model.content);
        values.put("isAttach", model.isFile);
        values.put("fname", model.attach);
        values.put("timestamp",model.timeStamp);
        values.put("mid",model.mId);
        String[] query  = new String[1];
        query[0] = String.valueOf(model.mId);
        db.update(tableName, values, "mid=?", query);
    }
    public Cursor selectById(int id1,int id2)
    {
        db = helper.getWritableDatabase();
        String[] query  = new String[4];
        query[0] = String.valueOf(id1);
        query[1] = String.valueOf(id2);
        query[2] = String.valueOf(id2);
        query[3] = String.valueOf(id1);
        Cursor c = db.rawQuery("select * from history where (fromid=? and toid=?) or (fromid=? and toid=?) order by id asc", query);
        return c;
    }
    public void delete(int id)
    {
        db = helper.getWritableDatabase();
        String[] values = new String[1];
        values[0] = String.valueOf(id);
        db.delete(tableName,"id=?",values);
    }
    public Cursor select()
    {
        db = helper.getReadableDatabase();
        Cursor c = db.query(tableName, null,null,null,null,null,null);
        return c;
    }
    public MessageModel getData(int id1,int id2)
    {
        Cursor c = selectById(id1,id2);
        c.moveToFirst();
        MessageModel rule = new MessageModel();
        rule.mId=  c.getInt(c.getColumnIndex("mid"));
        rule.fromId = c.getInt(c.getColumnIndex("fromid"));
        rule.toId = c.getInt(c.getColumnIndex("toid"));
        rule.content = c.getString(c.getColumnIndex("message"));
        rule.attach = c.getString(c.getColumnIndex("fname"));
        rule.isFile = c.getInt(c.getColumnIndex("isAttach"));
        rule.timeStamp = c.getString(c.getColumnIndex("timestamp"));
        return rule;

    }
    public List<MessageModel> getListData(int id1,int id2)
    {
        Cursor c = selectById(id1,id2);
        c.moveToFirst();
        List<MessageModel> r = new ArrayList<MessageModel>();
        if (c.getCount() > 0) {
            do {
                MessageModel rule = new MessageModel();
                rule.mId=  c.getInt(c.getColumnIndex("mid"));
                rule.fromId = c.getInt(c.getColumnIndex("fromid"));
                rule.toId = c.getInt(c.getColumnIndex("toid"));
                rule.content = c.getString(c.getColumnIndex("message"));
                rule.attach = c.getString(c.getColumnIndex("fname"));
                rule.isFile = c.getInt(c.getColumnIndex("isAttach"));
                rule.timeStamp = c.getString(c.getColumnIndex("timestamp"));
                r.add(rule);
            } while (c.moveToNext());
            return r;
        }
        else return null;
    }
    public class UsageSQLLite extends SQLiteOpenHelper
    {
        public UsageSQLLite(Context con,String name,CursorFactory factory,int version)
        {
            super(con,name,factory,version);
        }

        @Override
        public void onCreate(SQLiteDatabase arg0) {
            // TODO Auto-generated method stub
            arg0.execSQL(createTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub

        }
    }
}
