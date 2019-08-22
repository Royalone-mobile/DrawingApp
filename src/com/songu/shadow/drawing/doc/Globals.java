package com.songu.shadow.drawing.doc;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.songu.shadow.drawing.contact.ContactItemInterface;
import com.songu.shadow.drawing.db.ProfileDBManager;
import com.songu.shadow.drawing.model.CustomColor;
import com.songu.shadow.drawing.model.MessageModel;
import com.songu.shadow.drawing.model.UserProfile;
import com.songu.shadow.drawing.service.CServiceManager;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.widget.TextView;

public class Globals {
	public static int g_screenWidth;
	public static int g_screenHeight;
	
	public static int g_colorWidth;
	
	public static int viewNumber = 0;
	public static Bitmap back = null;
	
	
	public static String m_baseUrl = "http://ywc1209.xicp.net:88/chat/";
	public static final String cityUrl = "http://ip-api.com/json";
	//public static String m_baseUrl = "http://192.168.1.101:88/chat/";
    public static CServiceManager m_service;
    public static UserProfile mAccount = new UserProfile();  //Current Account... logined user
    public static List<ContactItemInterface> contactList = new ArrayList<ContactItemInterface>();  //  Contact for User
    public static List<UserProfile> requestList = new ArrayList<UserProfile>();  //Request User..
    //public static List<GroupProfile> groupList = new ArrayList<GroupProfile>();  //Group List
    //public static List<GroupProfile> requestGroupList = new ArrayList<GroupProfile>(); //Request Group List


    //public static List<ReminderModel> requestReminderList = new ArrayList<ReminderModel>();
    //public static List<ReminderModel> reminderList = new ArrayList<ReminderModel>();

    public static UserProfile mTarget = new UserProfile();  //Target for Send 1:1
    //public static GroupProfile mTargetGroup = new GroupProfile(); //Target for Group 1:N
    public static List<MessageModel> newMessages = new ArrayList<MessageModel>();  //New Messages
    public static ProfileDBManager dbMan;  //Profile DB

    public static List<UserProfile> searchResult = new ArrayList<UserProfile>();

    public static int currentMode; // 0:1:1.....1: 1:N Group
    public static CustomColor selColor;
    
    public static int g_actionbarHeight;
    public static String g_cityName;







    public static String getRealPathFromURI(Context act, Uri contentURI)  //that is get Real path in Phone....
    {
        Cursor cursor = act.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public static Bitmap setImageScale(Context activity,Uri imageUri)  //Set Image Scale for Bitmap
    {
        String path = Globals.getRealPathFromURI(activity,imageUri);
        File file = new File(path);
        Bitmap bitmap = decodeFile(file);
        Bitmap bt=Bitmap.createScaledBitmap(bitmap, 400, 300, false);
        return bt;
    }

    public static Bitmap decodeFile(File f)  //Decode File
    {
        Bitmap b = null;
        int IMAGE_MAX_WIDTH = 800;
        int IMAGE_MAX_HEIGHT = 600;
        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        FileInputStream fis;
        try
        {
            //fis = new FileInputStream(f);
            try
            {
                BitmapFactory.decodeFile(f.getAbsolutePath(), o);

                //fis.close();
                //fis = null;
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            int scale = 1;

            if (o.outHeight > IMAGE_MAX_WIDTH || o.outWidth > IMAGE_MAX_WIDTH)
            {
                int maxwh = Math.max(o.outWidth,o.outHeight);
                while(maxwh / scale > IMAGE_MAX_WIDTH)
                    scale *= 2;
            }


            Log.d("twinklestar.containerrecog", "width: " + o.outWidth + "height: " + o.outHeight + "scale:" + scale);
            //Decode with inSampleSize

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            o2.inJustDecodeBounds = false;
            o2.inPurgeable = true;

            //fis = new FileInputStream(f);

            try
            {
                b = BitmapFactory.decodeFile(f.getAbsolutePath(), o2);

                //fis.close();
                //fis = null;
            }
            catch (Exception e)
            {
                //	TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
    }
    
    
    
	
}
