package com.songu.shadow.drawing;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;





import org.json.JSONObject;

import com.songu.shadow.drawing.doc.Globals;
import com.songu.shadow.drawing.model.UserProfile;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//Screen for Register----------------------------------------
public class ActivitySignUp extends Activity implements View.OnClickListener {

    private Button btnRegister; //Button for Register
    private EditText m_editUser; //UserName
    private EditText m_editPassword; //Password
    private EditText m_editConfirm; //Confirm Password
    private EditText m_editFirstName; //First name
    private EditText m_editLastName; //Last name
    private EditText m_editEmail; //Email Address
    //private EditText m_editMobile; //Mobile number
    //private EditText m_editCountry; //Country code
    //public TextView m_editBirth; //Birthday
    //public CheckBox m_chkMale; // Checkbox for male
    //public CheckBox m_chkFemale; //Checkbox for female
    public ImageView m_imgProfile; // Profile Image for User
    public String imagePath=""; // Image Path for upload image
    public static Context con; //Context for this screen
    //public int mGender = 0; // Gender Mode ---- 0:Male 1:Female

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        //-------------- Get Control From xml and set Event Listener------------------
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
        m_editUser = (EditText) findViewById(R.id.editName);
        m_editPassword = (EditText) findViewById(R.id.editPass);
        m_editConfirm = (EditText) findViewById(R.id.editConfrimPass);
        m_imgProfile = (ImageView) findViewById(R.id.imgProfile);

        m_editFirstName = (EditText) findViewById(R.id.editFirstname);
        m_editLastName = (EditText) findViewById(R.id.editLastname);
        m_editEmail = (EditText) findViewById(R.id.editEmail);
        //m_editMobile = (EditText) findViewById(R.id.editMobile);
        //m_editCountry = (EditText) findViewById(R.id.editCode);
        //m_editBirth = (TextView)findViewById(R.id.editBirth);
        //m_chkMale = (CheckBox) findViewById(R.id.chkMale);
        //m_chkFemale = (CheckBox) findViewById(R.id.chkFemale);

        m_imgProfile.setOnClickListener(this);
        //m_chkMale.setOnClickListener(this);
        //m_chkFemale.setOnClickListener(this);
        //m_editBirth.setOnClickListener(this);
        //m_chkMale.setChecked(true);  //Standard Male Check box is checked
        con = this;

        //----------------------------------------------------------------------------------
    }
    public boolean checkValidator() {

        //Get Field from Screen--------------------------------------
        String name = this.m_editUser.getText().toString();
        String pass = this.m_editPassword.getText().toString();
        String confirm = this.m_editConfirm.getText().toString();
        String firstName = this.m_editFirstName.getText().toString();
        String lastName = this.m_editLastName.getText().toString();
        String email = this.m_editEmail.getText().toString();
        //String mobile = this.m_editMobile.getText().toString();
        //String code = this.m_editCountry.getText().toString();
        //String birth =  this.m_editBirth.getText().toString();
        //------------------------------------------------------------

        //Check Validator if empty or not equal etc................
        if (name.equals("") || pass.equals("") || confirm.equals("")) {
            Toast.makeText(this, "Please Input Field!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!pass.equals(confirm)) {
            Toast.makeText(this, "Do not match Password. Check again", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (firstName.equals(""))
        {
            Toast.makeText(this, "Please Input Firstname..", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (lastName.equals(""))
        {
            Toast.makeText(this, "Please Input Lastname..", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (email.equals(""))
        {
            Toast.makeText(this, "Please Input Email Address..", Toast.LENGTH_SHORT).show();
            return false;
        }
        /*else if (mobile.equals(""))
        {
            Toast.makeText(this, "Please Input MobileNumber..", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (code.equals(""))
        {
            Toast.makeText(this, "Please Input CountryCode..", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (birth.equals(""))
        {
            Toast.makeText(this, "Please Input Birthday..", Toast.LENGTH_SHORT).show();
            return false;
        }*/
        //------------------------------------------------------------
        //if all field is good then return true
        return true;
    }

    public static void onFail() {  //Get Fail Result from Server  then occur Message
        Toast.makeText(con, "Register Fail..!", Toast.LENGTH_SHORT).show();
        return;
    }

    public static void onSuccess(String jsonResult,ActivitySignUp activity) {//Parsing  String from Server
        //Example String that string has name,id passowrd and etc info...........
        //parse like that
        //{"id":"4","name":"Fg3","image":"","password":"fgy","response":200}
        try {
            JSONObject localJSONObject1 = new JSONObject(jsonResult);
            if (localJSONObject1.has("name"))
                Globals.mAccount.mName = localJSONObject1.getString("name");
            if (localJSONObject1.has("id"))
                Globals.mAccount.id = localJSONObject1.getInt("id");
            if (localJSONObject1.has("password"))
                Globals.mAccount.mPassword = localJSONObject1.getString("password");
            if (localJSONObject1.has("image"))
                Globals.mAccount.mImage = localJSONObject1.getString("image");
            if (localJSONObject1.has("firstname"))
                Globals.mAccount.mFirstName = localJSONObject1.getString("firstname");
            if (localJSONObject1.has("lastname"))
                Globals.mAccount.mLastName = localJSONObject1.getString("lastname");
            if (localJSONObject1.has("email"))
                Globals.mAccount.mEmail = localJSONObject1.getString("email");
            if (localJSONObject1.has("mobile"))
                Globals.mAccount.mMobile = localJSONObject1.getString("mobile");
            if (localJSONObject1.has("country"))
                Globals.mAccount.mCountry = localJSONObject1.getString("country");
            if (localJSONObject1.has("birth"))
                Globals.mAccount.mBirthday = localJSONObject1.getString("birth");
            if (localJSONObject1.has("gender"))
                Globals.mAccount.mGender = localJSONObject1.getInt("gender");
        } catch (Exception e) {
            e.printStackTrace();
        }


        //All accoutn info saved in Globals.mAccount
        //And show Message Success
        Toast.makeText(con, "Register Success!", Toast.LENGTH_SHORT).show();
        activity.goNext();  //Go to Contact Screen
    }
    public void goNext()
    {
        //Intent chatIntent = new Intent(con, MainActivity.class);  //Go to Contact Screen
        //con.startActivity(chatIntent);
        finish();//Finish this screen
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {  //Get Image for Profile.. if you select image from gallery
            if (data != null) {
                Uri uri = data.getData();
                Bitmap bmp = Globals.setImageScale(this,uri);
                this.m_imgProfile.setImageBitmap(bmp);  //set bitmap to imageview
                imagePath = Globals.getRealPathFromURI(this,uri);  //image path saved
            }
        }
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {  // that is birthday datepicker..
            return new DatePickerDialog(this, fromDateListener, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }
    private DatePickerDialog.OnDateSetListener fromDateListener   //Date Picker listener... get Date for birth
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            String m_currentDate = String.valueOf(arg1) + "-" + String.valueOf(arg2 + 1) + "-" + String.valueOf(arg3);
            //m_editBirth.setText(m_currentDate);  //set Text to control
        }
    };
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.btnRegister) {  //When you click register Button
            if (checkValidator()) {  //Check Validator


                //----------Get All field in Screen----------------------
                String name = this.m_editUser.getText().toString();
                String pass = this.m_editPassword.getText().toString();
                String confirm = this.m_editConfirm.getText().toString();
                String firstName = this.m_editFirstName.getText().toString();
                String lastName = this.m_editLastName.getText().toString();
                String email = this.m_editEmail.getText().toString();
                /*String mobile = this.m_editMobile.getText().toString();
                String code = this.m_editCountry.getText().toString();
                String birth =  this.m_editBirth.getText().toString();*/

                //--------------------------------------------------------

                //--------------Set All field to Model User Profile-------------------

                UserProfile profile = new UserProfile();
                profile.mName = name;
                profile.mPassword = pass;
                profile.mImage = this.imagePath;
                profile.mFirstName = firstName;
                profile.mLastName = lastName;
                profile.mEmail = email;
                /*profile.mMobile = mobile;
                profile.mCountry = code;
                profile.mBirthday = birth;
                profile.mGender = mGender;*/
                //---------------------------------------------------------------------

                //Send Data to Server--------------------------------------------------
                Globals.m_service.onProfileImageSave(profile,this);
                //---------------------------------------------------------------------
            }
            else
            {
                //if fail then show message -------------------------------------------
                Toast.makeText(this,"Register Fail!",Toast.LENGTH_SHORT).show();
            }
        }
        else if (v.getId() == R.id.imgProfile)  //When you click profile image view then open gallery screen
        {
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(
                    Intent.createChooser(intent, "Select File"),1);
        }
        /*else if (v.getId() == R.id.chkMale)  // When you click male checkbox then female check box is false
        {
            if (m_chkMale.isChecked())
            {
                m_chkFemale.setChecked(false);
                mGender = 0;
            }
            else {
                m_chkFemale.setChecked(true);
                mGender = 1;
            }
        }
        else if (v.getId() == R.id.chkFemale) //When you click Female Checkbox then male checkbox is false
        {
            if (m_chkFemale.isChecked())
            {
                mGender = 1;
                m_chkMale.setChecked(false);
            }
            else {
                mGender = 0;
                m_chkMale.setChecked(true);
            }

        }
        else if (v.getId() == R.id.editBirth) //Open dialog for birth.. then call on Create Dialog
        {
            showDialog(999);
        }*/
    }
}
