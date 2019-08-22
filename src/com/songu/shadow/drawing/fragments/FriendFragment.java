package com.songu.shadow.drawing.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import com.songu.shadow.drawing.MainActivity;
import com.songu.shadow.drawing.R;
import com.songu.shadow.drawing.contact.ContactItemInterface;
import com.songu.shadow.drawing.contact.ExampleContactAdapter;
import com.songu.shadow.drawing.contact.ExampleContactListView;
import com.songu.shadow.drawing.contact.ExampleDataSource;
import com.songu.shadow.drawing.db.ProfileDBManager;
import com.songu.shadow.drawing.doc.Globals;
import com.songu.shadow.drawing.model.CustomColor;
import com.songu.shadow.drawing.model.UserProfile;
import com.songu.shadow.drawing.popup.PopupProgressBarDialog;
import com.songu.shadow.drawing.popup.ResultPopup;
import com.songu.shadow.drawing.popup.SearchPopup;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendFragment extends CustomFragment implements TextWatcher ,View.OnClickListener{
	
	CustomColor color;
	Context context;
	ExampleContactAdapter adapter;
	List<ContactItemInterface> contactList;
	ExampleContactListView m_lstContact;
	SearchPopup popSearch;//Search Dialog for Add to Contact
	PopupProgressBarDialog popProgress;
	ResultPopup popResult;
	private ReadRequestContact myTask;
	private Timer myTimer;
	private int originalSize;
	
	
	public Handler mHandler = new Handler() {
        public void handleMessage(Message paramMessage) {
            switch (paramMessage.what)
            {                   
                case 2:
                    popProgress.hide();
                    break;             
            }
        }
    };
	public FriendFragment(LayoutInflater inflater, int resource) {
		super(inflater, resource);
		context = inflater.getContext();
		initControl();
		updateUI();
	}
	public void destroy()
	{
		myTimer.cancel();
	}
	public void initControl()
	{
		//contactList = ExampleDataSource.getSampleContactList();
		//adapter = new ExampleContactAdapter(context, R.layout.example_contact_item, contactList);
		
		m_lstContact = (ExampleContactListView) fragmentLayout.findViewById(R.id.friendList);
		m_lstContact.setFastScrollEnabled(true);
		
		adapter = new ExampleContactAdapter(context, R.layout.example_contact_item, Globals.contactList);
		m_lstContact.setAdapter(adapter);
		
		popSearch = new SearchPopup(context);
		popResult = new ResultPopup(context);
		popProgress = new PopupProgressBarDialog(context);
		popSearch.btnOk.setOnClickListener(this);
		//
		if (Globals.dbMan == null)
			Globals.dbMan = new ProfileDBManager(context);
		loadContactList();
		myTimer = new Timer();
		myTask = new ReadRequestContact();
		myTimer.schedule(myTask,5000,6000);
		m_lstContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	public void loadContactList()
	{
		Globals.m_service.onLoadContactList(this,null);
	}
	public void updateUI(){
		
	}
	
	public void onSuccess(String jsonArray)
    {//Parsing
        //{"contacts":[{"id":"1","name":"Fg","state":"1"},{"id":"2","name":"Fg1","state":"1"},{"id":"3","name":"Fg2","state":"1"},{"id":"4","name":"Fg3","state":"1"}],"response":200}
		
        if (Globals.contactList != null)
        {
        	originalSize = Globals.contactList.size();
            Globals.contactList.clear();
        }
        if (Globals.requestList != null)
            Globals.requestList.clear();

        ArrayList localArrayList1 = new ArrayList();
        ArrayList localArrayList2 = new ArrayList();
        try {
            JSONObject localJSONObject1 = new JSONObject(jsonArray);
            JSONArray localJSONArray1 = localJSONObject1.getJSONArray("contacts");
            for (int i = 0;i < localJSONArray1.length();i++)
            {
                JSONObject localJSONObject2 = localJSONArray1.getJSONObject(i);
                UserProfile data = new UserProfile();
                data.mName = localJSONObject2.getString("name");
                data.id = localJSONObject2.getInt("id");
                data.mImage = localJSONObject2.getString("image");
                data.state = localJSONObject2.getInt("state");
                data.mBirthday = localJSONObject2.getString("birth");
                data.mFirstName = localJSONObject2.getString("firstname");
                data.mLastName = localJSONObject2.getString("lastname");
                data.mEmail = localJSONObject2.getString("email");
                data.mMobile = localJSONObject2.getString("mobile");
                data.mCountry = localJSONObject2.getString("country");
                data.mGender = localJSONObject2.getInt("gender");

                if (data.state == 0)
                    Globals.contactList.add(data);
                else
                    Globals.requestList.add(data);
            }
            setList();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
	public void setList()
	{
		
		if (Globals.requestList.size() > 0)
		{
			final MainActivity act = (MainActivity) context;
			act.runOnUiThread(new Runnable()
			{
				public void run()
				{
					act.m_txtSignal.setVisibility(View.VISIBLE);
				}
			}
			);
			
		}
		if (this.originalSize != Globals.contactList.size())
		{	
			
			final MainActivity act = (MainActivity) context;
			act.runOnUiThread(new Runnable()
			{
				public void run()
				{
					//adapter.clear();
					//adapter.addAll(Globals.contactList);
					//adapter.notifyDataSetChanged();
					adapter = new ExampleContactAdapter(context, R.layout.example_contact_item, Globals.contactList);
					m_lstContact.setAdapter(adapter);
					
					//originalSize = Globals.contactList.size();
				}
			}
			);
			
			
		}
	}
	
	
	
	
	
	
	
	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.btnAddFriend:
			if (Globals.requestList.size() > 0) {
                this.popResult.setResults(Globals.requestList,null);
                this.popResult.showAtLocation(m_lstContact, 0, 0);
            } else {
            	popSearch.showAtLocation(m_lstContact, 0, 0);
            }
			
			
			
			break;
		case R.id.btnSearch:  // That is Button in popup when you click add person..
            String pattern = this.popSearch.editPattern.getText().toString();
            if (pattern.equals("")) {
                Toast.makeText(context, "Empty ID..", Toast.LENGTH_SHORT).show();
                return;
            }
            this.popSearch.hide();
            popProgress.showAtLocation(m_lstContact, 0, 0);
            Globals.m_service.onSearchIdResult(this, pattern);
            break;
		}
	}
	
	public  void parseSearchResult(String jsonArray)
    {//Parsing
        //{"contacts":[{"id":"1","name":"Fg","state":"1"},{"id":"2","name":"Fg1","state":"1"},{"id":"3","name":"Fg2","state":"1"},{"id":"4","name":"Fg3","state":"1"}],"response":200}
        if (Globals.searchResult != null)
            Globals.searchResult.clear();
        ArrayList localArrayList1 = new ArrayList();
        ArrayList localArrayList2 = new ArrayList();
        try {
            JSONObject localJSONObject1 = new JSONObject(jsonArray);
            JSONArray localJSONArray1 = localJSONObject1.getJSONArray("contacts");
            for (int i = 0;i < localJSONArray1.length();i++)
            {
                JSONObject localJSONObject2 = localJSONArray1.getJSONObject(i);
                UserProfile data = new UserProfile();
                data.mName = localJSONObject2.getString("name");
                data.id = localJSONObject2.getInt("id");
                data.mImage = localJSONObject2.getString("image");
                data.state = localJSONObject2.getInt("state");
                data.mBirthday = localJSONObject2.getString("birth");
                data.mFirstName = localJSONObject2.getString("firstname");
                data.mLastName = localJSONObject2.getString("lastname");
                data.mEmail = localJSONObject2.getString("email");
                data.mMobile = localJSONObject2.getString("mobile");
                data.mCountry = localJSONObject2.getString("country");
                data.mGender = localJSONObject2.getInt("gender");

                Globals.searchResult.add(data);
            }
            if (Globals.searchResult.size() == 0)
            {
                Toast.makeText(context,"No Result!",Toast.LENGTH_SHORT).show();
                return;
            }
            this.popResult.setResults(Globals.searchResult,this);
            this.popResult.showAtLocation(m_lstContact,0,0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
	public void updateList()  //Update List for Contact and reload List for Event..
    {
        loadContactList();       
    }
	public class ReadRequestContact extends TimerTask {  //Read Message Thread Class
        //public Context con;

        public ReadRequestContact()
        {
            
        }
        @Override
        public void run() {  //run send message            
        	loadContactList();
        }
    }

}
