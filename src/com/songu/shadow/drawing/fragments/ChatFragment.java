package com.songu.shadow.drawing.fragments;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ipmacro.utils.aes.AESUtil;
import com.songu.shadow.drawing.ActivityChat;
import com.songu.shadow.drawing.R;
import com.songu.shadow.drawing.adapter.AdapterChatContact;
import com.songu.shadow.drawing.db.ProfileDBManager;
import com.songu.shadow.drawing.doc.Globals;
import com.songu.shadow.drawing.model.MessageModel;
import com.songu.shadow.drawing.service.HttpPosting;
import com.songu.shadow.drawing.utils.Imageloader;
import com.songu.shadow.drawing.utils.SyncImageLoader;

public class ChatFragment extends CustomFragment implements View.OnClickListener{
	
	Context context;
	public ImageButton btnSend;
	public EditText editTextInput;
	public LinearLayout layoutMessage; 
	public ScrollView scrollMessage;
	
	
	private Timer myTimer;  //Timer for Read Message
    private ReadMesasgeTask myTask;
    private boolean isText = true;
    TextView cur_val;
    private List<MessageModel> history = new ArrayList<MessageModel>();  //History Message
    
    public Imageloader imageLoader; // Load profile image for target..
    public Imageloader imageLoader1;
    
    
    
    protected Handler mHandler = new Handler() {
        public void handleMessage(Message paramMessage) {
            switch (paramMessage.what) {
                case 0:
                    for (int i = 0; i< Globals.newMessages.size();i++)
                    {
                        MessageModel msg = Globals.newMessages.get(i);
                        addView(false,msg);
                    }
                    Globals.newMessages.clear();
                    break;
            }
        }
    };

    public void addView(boolean isTarget,MessageModel data) {
        View m_view = new View(this.context);
        if (isTarget)
            m_view = LayoutInflater.from(context).inflate(R.layout.msg_item, null);
        else
            m_view = LayoutInflater.from(context).inflate(R.layout.msg_item1, null);
        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        params.setMargins(3, 10, 3, 10);

        if (isTarget) {
            FromViewGruop viewgroup = new FromViewGruop();
            viewgroup.m_img = (ImageView) m_view.findViewById(R.id.img_profile_item);
            viewgroup.m_message = (TextView) m_view.findViewById(R.id.txt_message_content);
            viewgroup.m_timestamp = (TextView) m_view.findViewById(R.id.txt_timestamp);
            viewgroup.m_container = (LinearLayout) m_view.findViewById(R.id.layout_message);


                try {
                    String ss = URLDecoder.decode(data.content, "UTF-8");
                    byte[] arrSS = Base64.decode(ss, Base64.DEFAULT);
                    String strSS = new String(arrSS, "UTF-8");
                    String strAES = AESUtil.decrypt(strSS);
                    viewgroup.m_message.setText(strAES);
                }
                catch(Exception e)
                {

                }


            String timeStamp = data.timeStamp.substring(10);
            viewgroup.m_timestamp.setText(timeStamp);
            viewgroup.m_img.setTag( Globals.mAccount.mImage);
            imageLoader.DisplayImage( Globals.mAccount.mImage, (Activity)context, viewgroup.m_img);
            params.gravity = Gravity.LEFT;
            m_view.setTag(viewgroup);
        }
        else
        {
            ToViewGroup viewGroup = new ToViewGroup();
            viewGroup.m_img = (ImageView) m_view.findViewById(R.id.img_to_profile_item);
            viewGroup.m_message = (TextView) m_view.findViewById(R.id.txt_to_message_content);
            viewGroup.m_timestamp = (TextView) m_view.findViewById(R.id.txt_to_timestamp);
            viewGroup.m_container = (LinearLayout) m_view.findViewById(R.id.layout_to_message);

                try {

                    String ss = URLDecoder.decode(data.content, "UTF-8");
                    byte[] arrSS = Base64.decode(ss, Base64.DEFAULT);
                    String strSS = new String(arrSS, "UTF-8");
                    String strAES = AESUtil.decrypt(strSS);
                    viewGroup.m_message.setText(strAES);
                }
                catch(Exception e)
                {

                }


            String timeStamp = data.timeStamp.substring(10);
            viewGroup.m_timestamp.setText(timeStamp);     
            viewGroup.m_img.setTag(Globals.mTarget.mImage);
            imageLoader1.DisplayImage(Globals.mTarget.mImage,(Activity)context,viewGroup.m_img);
            params.gravity = Gravity.RIGHT;
            m_view.setTag(viewGroup);

        }
        m_view.setLayoutParams(params);
        layoutMessage.addView(m_view);

        Globals.dbMan.insertData(data);

        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {Thread.sleep(100);} catch (InterruptedException e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollMessage.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }).start();
    }
    
	public ChatFragment(LayoutInflater inflater, int resource) {
		super(inflater, resource);
		context = inflater.getContext();
		initControl();
		//updateUI();
	}
	
	public void initControl()
	{
		btnSend = (ImageButton) fragmentLayout.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        
        layoutMessage = (LinearLayout) fragmentLayout.findViewById(R.id.layout_message_content);
        scrollMessage = (ScrollView) fragmentLayout.findViewById(R.id.scroll_message_content);
        editTextInput = (EditText) fragmentLayout.findViewById(R.id.editInputText);
        
        imageLoader = new Imageloader(context);
        imageLoader1 = new Imageloader(context);
        
        myTimer = new Timer();
        myTask = new ReadMesasgeTask(Globals.mTarget.id);
        myTimer.schedule(myTask,500,2000);
	}
	public void setList()  //Only for Design will use future work
    {
        if (history != null) {
            for (int i = 0; i < history.size(); i++) {
                MessageModel model = history.get(i);
                if (model.fromId == Globals.mAccount.id) {
                    addView(true, model);
                } else
                    addView(false, model);
            }
        }
    }
	
	public static String getCurrentTimeString()  //Get Current Time.. for Send Message TimeStamp
    {

        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss");//isNotEmpty
        Date currentTime = new Date ();
        String dTime = formatter.format(currentTime);
        return dTime;
    }
	
	public void onSend(int toId)  //Send Message Function
    {
        String strInputText = editTextInput.getText().toString();
        if (strInputText.equals(""))
            return;

        editTextInput.setText("");


        //Message Model for Send................
        MessageModel msgModel = new MessageModel();
        msgModel.fromId = Globals.mAccount.id;
        msgModel.toId = toId;
        try {
            String ss = AESUtil.encrypt(strInputText);  //Security for Message
            byte[] data = ss.getBytes("UTF-8");
            String base64 = Base64.encodeToString(data, Base64.DEFAULT);
            msgModel.content = URLEncoder.encode(base64,"UTF-8");
        }
        catch (Exception e)  //If Can't Convert to Base64 then occure Exception..
        {
            e.printStackTrace();
        }
        msgModel.timeStamp = getCurrentTimeString();  //Set Time Stamp
        if(isText) {  //Message is Text
            msgModel.isFile = 0;
            msgModel.attach = "";
            //Globals.m_service.onSendMessage(msgModel,this,0);
        }
        else  //Message is File
        {
            String strAES = "";
            try {
                String ss = URLDecoder.decode(msgModel.content, "UTF-8");
                byte[] arrSS = Base64.decode(ss, Base64.DEFAULT);
                String strSS = new String(arrSS, "UTF-8");
                strAES = AESUtil.decrypt(strSS);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            msgModel.isFile = 0;
            isText = true;
        }
        addView(true,msgModel);
    }
    public static void onFail(ChatFragment activity,int memberId)  //If Read Message Fail
    {
        
    }
    public static void onSuccessRead(ChatFragment activity,String jsonArray,int memberId) //Read Message From Server
    {//Parsing        
        if (Globals.contactList != null)
            Globals.contactList.clear();
        ArrayList localArrayList1 = new ArrayList();
        ArrayList localArrayList2 = new ArrayList();

        //-----------Parse Json Message and show on View------------------------------
        try {
            JSONObject localJSONObject1 = new JSONObject(jsonArray);
            JSONArray localJSONArray1 = localJSONObject1.getJSONArray("message");
            for (int i = 0;i < localJSONArray1.length();i++)
            {
                JSONObject localJSONObject2 = localJSONArray1.getJSONObject(i);
                MessageModel data = new MessageModel();
                data.mId = localJSONObject2.getInt("id");
                data.toId = localJSONObject2.getInt("to");
                data.fromId = localJSONObject2.getInt("from");
                data.content = localJSONObject2.getString("content");
                data.timeStamp = localJSONObject2.getString("time");
                data.isFile = localJSONObject2.getInt("attach");
                data.attach = localJSONObject2.getString("file");
                Globals.newMessages.add(data);
            }
            activity.appendMessage();  //Add Message on View
        }
        catch (Exception e)  //If Can't parse with Json String.. then occure
        {
            e.printStackTrace();
        }
    }
    public void onSuccessSend(MessageModel msg,int memberIndex)   //Send success..
    {//Parsing
        //{"id":"4","name":"Fg3","image":"","password":"fgy","response":200}
        //Globals.mAccount.mPassword

        /*if (Globals.currentMode == 1 && memberIndex < Globals.mTargetGroup.members.size() - 1)
        {
            int toId = Globals.mTargetGroup.members.get(memberIndex + 1).id;
            if (toId == Globals.mAccount.id)
                msg.toId = Globals.mTargetGroup.owner;
            else msg.toId = toId;
            Globals.m_service.onSendMessage(msg,this,memberIndex + 1);
        }*/
    }
    public void appendMessage()
    {
        mHandler.sendEmptyMessageDelayed(0, 100);
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.btnSend) {  //Send Message Button Event        
                onSend(Globals.mTarget.id);
        }
    }
    public class ReadMesasgeTask extends TimerTask {  //Read Message Thread Class
        //public Context con;
        public int targetId;  // To Id for message send

        public ReadMesasgeTask(int targetId)
        {
            this.targetId = targetId;
        }
        @Override
        public void run() {  //run send message
            //Globals.m_service.onReadMessage(targetId,ActivityChat.this,0);
        }
    }
	public class FromViewGruop
    {
        public ImageView m_img;
        public TextView m_message;
        public TextView m_timestamp;
        public LinearLayout m_container;
    }
    public class ToViewGroup
    {
        public ImageView m_img;
        public TextView m_message;
        public TextView m_timestamp;
        public LinearLayout m_container;
    }
}
