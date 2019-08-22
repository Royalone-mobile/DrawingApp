package com.songu.shadow.drawing.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.songu.shadow.drawing.ActivityChat;
import com.songu.shadow.drawing.MainActivity;
import com.songu.shadow.drawing.R;
import com.songu.shadow.drawing.adapter.AdapterChatContact;
import com.songu.shadow.drawing.contact.ContactItemInterface;
import com.songu.shadow.drawing.contact.ExampleContactAdapter;
import com.songu.shadow.drawing.contact.ExampleContactListView;
import com.songu.shadow.drawing.contact.ExampleDataSource;
import com.songu.shadow.drawing.db.ProfileDBManager;
import com.songu.shadow.drawing.doc.Globals;
import com.songu.shadow.drawing.model.CustomColor;
import com.songu.shadow.drawing.model.UserProfile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ChatContactFragment extends CustomFragment implements TextWatcher{
	
	CustomColor color;
	Context context;
	AdapterChatContact adapter;
	List<UserProfile> contactList;
	ListView m_lstChatContact;
	

	public ChatContactFragment(LayoutInflater inflater, int resource) {
		super(inflater, resource);
		context = inflater.getContext();
		initControl();
		updateUI();
	}
	
	public void initControl()
	{
		//contactList = ExampleDataSource.getSampleContactList();
		adapter = new AdapterChatContact(context);
		
		m_lstChatContact = (ListView) fragmentLayout.findViewById(R.id.lstChatContact);
		m_lstChatContact.setFastScrollEnabled(true);
		m_lstChatContact.setAdapter(adapter);
		if (Globals.dbMan == null)
			Globals.dbMan = new ProfileDBManager(context);
		loadContactList();
		m_lstChatContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				final int p = arg2;
                ((MainActivity) ChatContactFragment.this.context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Globals.mTarget = (UserProfile)adapter.getItem(p);
                        Globals.currentMode = 0;
                        //((MainActivity)context).openChatFragment();
                        Intent m = new Intent(context,ActivityChat.class);
                        context.startActivity(m);
                    }
                });
                
                
				
			}
		});
	}
	public void loadContactList()
	{
		Globals.m_service.onLoadContactList(null,this);
	}
	public void updateUI(){
		
	}
	
	public void onSuccess(String jsonArray)
    {//Parsing
        //{"contacts":[{"id":"1","name":"Fg","state":"1"},{"id":"2","name":"Fg1","state":"1"},{"id":"3","name":"Fg2","state":"1"},{"id":"4","name":"Fg3","state":"1"}],"response":200}
        if (Globals.contactList != null)
            Globals.contactList.clear();
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
		adapter.update(Globals.contactList);
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


}
