package com.songu.shadow.drawing;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;








import org.json.JSONObject;
import com.songu.shadow.drawing.doc.Globals;
import com.songu.shadow.drawing.model.UserProfile;
import com.songu.shadow.drawing.service.CServiceManager;




//Screen for Login ---------------------------------------
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class ActivitySignIn extends Activity implements View.OnClickListener {

    public Button btnSignIn; //Button for sign in
    public Button btnSignUp; //button for sign up
    public EditText editTextUserID; //userid
    public EditText editTextUserPass;//password
    //public TextView txtForget;//forget button
    public static Context con; //Context for this screen


    //This method is First called-------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //init for service connect server
        
        // Get Control from xml------------------------
        con = this;
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(this);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(this);
        editTextUserID = (EditText) findViewById(R.id.editUserName);
        editTextUserPass = (EditText) findViewById(R.id.editUserPass);
        
        
        		
        		
        //txtForget = (TextView) findViewById(R.id.txtForget);
        //txtForget.setOnClickListener(this);
        //----------------------------------------------
    }   
    public static void onFail()
    {
        Toast.makeText(con,"Login Fail.. You're not registered!",Toast.LENGTH_SHORT).show();
        return;
    }
    public static void onSuccess(String jsonArray,ActivitySignIn activity)
    {//Parsing
        try {
            JSONObject localJSONObject2 = new JSONObject(jsonArray);
            UserProfile data = new UserProfile();
            Globals.mAccount.mName = localJSONObject2.getString("name");
            Globals.mAccount.id = localJSONObject2.getInt("id");
            Globals.mAccount.mImage = localJSONObject2.getString("image");
            Globals.mAccount.mPassword = localJSONObject2.getString("password");
            activity.goNext();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void goNext() //Open Contact Screen
    {
    	SharedPreferences sp = this.getSharedPreferences("login", Context.MODE_PRIVATE);
    	sp.edit().putString("account", Globals.mAccount.mName).apply();
    	sp.edit().putString("pass", Globals.mAccount.mPassword).apply();
        Intent m_intent = new Intent(this,MainActivity.class);
        this.startActivity(m_intent);
        this.finish();
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.btnSignIn) {  //When click Sign In Button
            String user = this.editTextUserID.getText().toString();
            String pw = this.editTextUserPass.getText().toString();
            Globals.m_service.onLogin(user,pw,this,null);
        } else if (v.getId() == R.id.btnSignUp) //Go to Register screen
        {
            Intent singupIntent = new Intent(this, ActivitySignUp.class);  // go to Register Screen
            startActivity(singupIntent);
        }
        /*else if (v.getId() == R.id.txtForget) { //When click Forget text
            Intent forget = new Intent(this, ActivityForget.class); // go to Forget Screen
            startActivity(forget);
        }*/

    }
}
