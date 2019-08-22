package com.songu.shadow.drawing.model;

import com.songu.shadow.drawing.contact.ContactItemInterface;

/**
 * Created by Administrator on 3/11/2015.
 */
public class UserProfile implements ContactItemInterface {
    public String mName;  //Username
    public String mImage; //Image for profile
    public int id;  // Id for user
    public String mPassword; //Password
    public int state; //State for User
    public String mFirstName; //Frist name
    public String mLastName; //Last name
    public String mEmail; //E-mail address
    public String mMobile;  //Mobile
    public String mCountry;  //Country code
    public String mBirthday; //Birthday
    public int mMemId;  //Member Id
    public int mGender; //Gender
	@Override
	public String getItemForIndex() {
		// TODO Auto-generated method stub
		return mName;
	}
}
