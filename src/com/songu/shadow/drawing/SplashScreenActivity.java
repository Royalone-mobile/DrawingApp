package com.songu.shadow.drawing;

import org.json.JSONObject;

import com.songu.shadow.drawing.doc.Globals;
import com.songu.shadow.drawing.model.UserProfile;
import com.songu.shadow.drawing.service.CServiceManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class SplashScreenActivity extends Activity {

    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.GRAY);
        }*/
        Globals.m_service = new CServiceManager(this);
        
        
        CServiceManager.getCityName(this);
        
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_around_center_point);
        findViewById(R.id.outer_logo).startAnimation(animation);
        
    }
    public void login()
    {
    	SharedPreferences sp = this.getSharedPreferences("login", Context.MODE_PRIVATE);
        final String mName = sp.getString("account", "");
        final String mPassword = sp.getString("pass", "");
        new Thread() {
            public void run() { 
            	
            	try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
                		
                runOnUiThread(new Runnable() {
                    @Override 
                    public void run() {
                    	if (!mName.equals(""))
                        {
                        	Globals.m_service.onLogin(mName,mPassword,null,SplashScreenActivity.this);
                        }
                    	else 
                    	{
                    		
                    		finish();
                    		startActivity(new Intent(SplashScreenActivity.this, ActivitySignIn.class));
                    	}
                        //startActivity(new Intent(SplashScreenActivity.this, ActivitySignIn.class));
                    } 
                });

            } 
        }.start();
        
    }
    public void goNext(String jsonArray)
    {
    	try {
            JSONObject localJSONObject2 = new JSONObject(jsonArray);
            UserProfile data = new UserProfile();
            Globals.mAccount.mName = localJSONObject2.getString("name");
            Globals.mAccount.id = localJSONObject2.getInt("id");
            Globals.mAccount.mImage = localJSONObject2.getString("image");
            Globals.mAccount.mPassword = localJSONObject2.getString("password");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    	startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
    	finish();
    }
}
