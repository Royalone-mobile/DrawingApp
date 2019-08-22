package com.songu.shadow.drawing;



import com.songu.shadow.drawing.doc.Globals;
import com.songu.shadow.drawing.fragments.ChatContactFragment;
import com.songu.shadow.drawing.fragments.ChatFragment;
import com.songu.shadow.drawing.fragments.ColorCaptureFragment;
import com.songu.shadow.drawing.fragments.ColorLibraryFragment;
import com.songu.shadow.drawing.fragments.ColorMatchFragment;
import com.songu.shadow.drawing.fragments.ColorPreviewFragment;
import com.songu.shadow.drawing.fragments.CustomFragment;
import com.songu.shadow.drawing.fragments.DrawFragment;
import com.songu.shadow.drawing.fragments.FriendFragment;
import com.songu.shadow.drawing.fragments.HomeFragment;
import com.songu.shadow.drawing.pref.AppPreferences;
import com.songu.shadow.drawing.utils.Imageloader;
import com.songu.shadow.drawing.view.CustomToolbar;
import com.songu.shadow.drawing.widget.ActionBarDrawerToggle;
import com.songu.shadow.drawing.widget.DrawerArrowDrawable;
import com.pkmmte.circularimageview.CircularImageView;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Build;
import android.preference.PreferenceManager;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MainActivity extends FragmentActivity  implements View.OnClickListener{

	
	
	private DrawerLayout mDrawerLayout;
    private RelativeLayout mNavView;
    private ActionBarDrawerToggle mDrawerToggle;
    
    private DrawerArrowDrawable drawerArrow;
    public Fragment mainFragment;
    
    public LinearLayout m_layoutHome;
    public LinearLayout m_layoutCapture;
    public LinearLayout m_layoutLibrary;
    public LinearLayout m_layoutMatch;
    public LinearLayout m_layoutFriends;
    public LinearLayout m_layoutChat;
    public LinearLayout m_layoutSettings;
    public LinearLayout m_layoutSignOut;
    
    public LayoutInflater inflater;
    public FrameLayout frameMain;
    public TextView m_txtTitle;
    public SharedPreferences prefs;
    public ImageView m_btnAddFriend;
    public TextView m_txtSignal;
    
    public TextView m_txtUser;
    public CircularImageView m_imgUser;
    public Imageloader imgLoader;
    
    
    
    
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*mainFragment = new HomeFragment();
		this.frameMain = (FrameLayout) this.findViewById(R.id.frameHome);
		getSupportFragmentManager().beginTransaction().add(R.id.frameHome, mainFragment).commit();*/
		
		
		//adapterStation = new MenuStationAdapter(this);
		//adapterSwitch = new MenuHDAdapter(this);
		imgLoader = new Imageloader(this);
		getScreenSize();
		try{
	        android.app.ActionBar ab = getActionBar();
	        ab.setDisplayHomeAsUpEnabled(true);
	        ab.setHomeButtonEnabled(true);
	        ab.setDisplayUseLogoEnabled(false);
	        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color_white)));	        
	        //ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar));
	        ab.setIcon(new ColorDrawable(0x8ffffff));
	        ab.setCustomView(R.layout.actionbar_layout);
	        ab.setDisplayShowCustomEnabled(true);         
	        
	        
	        
	        
	        this.m_txtTitle = (TextView)ab.getCustomView().findViewById(R.id.txtActionbarTitle);
	        Globals.g_actionbarHeight = ab.getHeight();
	        /*ivDownArrow = new ImageView(this);
	        ivDownArrow.setImageResource(R.drawable.ic_arrow_drop_down_white);
	        ab.setCustomView(R.layout.actionbar_layout);
            //ab.setCustomView(ivDownArrow);
            ivDownArrow = (ImageView)ab.getCustomView().findViewById(R.id.img_dropdown);            
	        ab.setDisplayShowCustomEnabled(true);            
	        ivDownArrow.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) 
				{
					
				}	        	
	        });*/
        }catch(Exception e){}
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavView = (RelativeLayout) findViewById(R.id.navdrawer);
        this.m_layoutHome = (LinearLayout) findViewById(R.id.layoutMenuHome);
        this.m_layoutCapture = (LinearLayout) findViewById(R.id.layoutMenuCapture);
        this.m_layoutMatch = (LinearLayout) findViewById(R.id.layoutMenuMatch);
        this.m_layoutLibrary = (LinearLayout) findViewById(R.id.layoutMenuLib);
        this.m_layoutFriends = (LinearLayout) findViewById(R.id.layoutMenuFriend);
        this.m_layoutChat = (LinearLayout) findViewById(R.id.layoutMenuChat);
        this.m_layoutSettings = (LinearLayout) findViewById(R.id.layoutMenuSettings);
        this.m_layoutSignOut = (LinearLayout) findViewById(R.id.layoutMenuSignOut);
        this.m_btnAddFriend = (ImageView) findViewById(R.id.btnAddFriend);
        this.m_txtSignal = (TextView) findViewById(R.id.txtSignalRequest);
        this.m_imgUser = (CircularImageView) findViewById(R.id.itemUserImage);
        this.m_txtUser = (TextView) findViewById(R.id.txtUserName);
        
        inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        
        this.m_txtUser.setText(Globals.mAccount.mName);
        this.m_imgUser.setTag(Globals.mAccount.mImage);
        this.imgLoader.DisplayImage(Globals.mAccount.mImage, this, this.m_imgUser);
        
        
        this.m_layoutHome.setOnClickListener(this);
        this.m_layoutCapture.setOnClickListener(this);
        this.m_layoutMatch.setOnClickListener(this);
        this.m_layoutLibrary.setOnClickListener(this);
        this.m_layoutFriends.setOnClickListener(this);
        this.m_layoutChat.setOnClickListener(this);
        this.m_layoutSettings.setOnClickListener(this);
        this.m_layoutSignOut.setOnClickListener(this);
        
        
        
       
        
        
        drawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };
        //txtCompany = (TextView) findViewById(R.id.menucompany);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                drawerArrow, R.string.drawer_open,
                R.string.drawer_close) {

                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    invalidateOptionsMenu();
                }

                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    invalidateOptionsMenu();
                }
            };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int lastFragmentNumber = prefs.getInt("frag", -1);
		AppPreferences.updatePreferences(prefs);
		
		this.m_btnAddFriend.setVisibility(View.INVISIBLE);
		this.m_txtTitle.setText("HOME");
		this.frameMain = (FrameLayout) this.findViewById(R.id.frameHome);
		currentFragment = new HomeFragment(inflater, R.layout.fragment_home);			
		frameMain.addView(currentFragment.fragmentLayout,0);
		
		
        
       // mNavStatiionList = (ListView)findViewById(R.id.lvNavBibleSettings);
       // this.mNavHd = (ListView)findViewById(R.id.lvNavBibleAdd);
        
       // this.mNavStatiionList.setAdapter(adapterStation);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);

	    // Checks the orientation of the screen
	    this.getScreenSize();
	    if (currentFragment != null)
	    {
	    	this.frameMain.addView(currentFragment.fragmentLayout,0);
	    }
	    
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mNavView)) {
                mDrawerLayout.closeDrawer(mNavView);
            } else {
                mDrawerLayout.openDrawer(mNavView);
            }
        }
        return super.onOptionsItemSelected(item);
	}
	public void getScreenSize()
	{
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		Globals.g_screenHeight = size.y;
		Globals.g_screenWidth = size.x;
	}
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_home, container,
					false);
			return rootView;
		}
	}
	public CustomFragment currentFragment;
	
	public void openHome()
	{
		if (currentFragment instanceof FriendFragment)
			((FriendFragment) currentFragment).destroy();
		if (currentFragment instanceof DrawFragment)
		{
			((DrawFragment)currentFragment).removeFree();
			android.app.ActionBar ab = getActionBar();
			ab.setCustomView(R.layout.actionbar_layout);
	        ab.setDisplayShowCustomEnabled(true);         
	        this.m_txtTitle = (TextView)ab.getCustomView().findViewById(R.id.txtActionbarTitle);
	        
	        this.m_txtTitle = (TextView)ab.getCustomView().findViewById(R.id.txtActionbarTitle);
	        Globals.g_actionbarHeight = ab.getHeight();
		}
		if (currentFragment instanceof ColorCaptureFragment)
			((ColorCaptureFragment)currentFragment).freeMemory();
		if (this.currentFragment instanceof HomeFragment)
			((HomeFragment)currentFragment).freeMemory();
		
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		frameMain.removeAllViews();
		Globals.viewNumber = 0;
		this.m_btnAddFriend.setVisibility(View.INVISIBLE);
		this.m_txtTitle.setText("HOME");
		currentFragment = new HomeFragment(inflater, R.layout.fragment_home);			
		frameMain.addView(currentFragment.fragmentLayout,0);			
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if (currentFragment instanceof FriendFragment)
			((FriendFragment) currentFragment).destroy();
		if (currentFragment instanceof DrawFragment)
		{
			((DrawFragment)currentFragment).removeFree();
			android.app.ActionBar ab = getActionBar();
			ab.setCustomView(R.layout.actionbar_layout);
	        ab.setDisplayShowCustomEnabled(true);         
	        this.m_txtTitle = (TextView)ab.getCustomView().findViewById(R.id.txtActionbarTitle);
	        
	        this.m_txtTitle = (TextView)ab.getCustomView().findViewById(R.id.txtActionbarTitle);
	        Globals.g_actionbarHeight = ab.getHeight();
		}
		if (currentFragment instanceof ColorCaptureFragment)
			((ColorCaptureFragment)currentFragment).freeMemory();
		if (this.currentFragment instanceof HomeFragment)
			((HomeFragment)currentFragment).freeMemory();
		
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		frameMain.removeAllViews();
		
		
		switch(v.getId())
		{
		case R.id.layoutMenuHome:	
			Globals.viewNumber = 0;
			this.m_btnAddFriend.setVisibility(View.INVISIBLE);
			this.m_txtTitle.setText("HOME");
			currentFragment = new HomeFragment(inflater, R.layout.fragment_home);			
			frameMain.addView(currentFragment.fragmentLayout,0);			
			mDrawerLayout.closeDrawer(mNavView);
			break;
		case R.id.layoutMenuCapture:
			Globals.viewNumber = 1;
			this.m_btnAddFriend.setVisibility(View.INVISIBLE);
			this.m_txtSignal .setVisibility(View.INVISIBLE);
			this.m_txtTitle.setText("COLOR CAPTURE");			
			currentFragment = new ColorCaptureFragment(inflater, R.layout.fragment_color_capture);			
			frameMain.addView(currentFragment.fragmentLayout,0);
			mDrawerLayout.closeDrawer(mNavView);
			break;
		case R.id.layoutMenuLib:
			Globals.viewNumber = 2;
			this.m_btnAddFriend.setVisibility(View.INVISIBLE);
			this.m_txtSignal .setVisibility(View.INVISIBLE);
			this.m_txtTitle.setText("COLOR LIBRARY");	
			getActionBar().setTitle(R.string.color_library);
			currentFragment = new ColorLibraryFragment(inflater, R.layout.fragment_color_library);
			frameMain.addView(currentFragment.fragmentLayout,0);
			mDrawerLayout.closeDrawer(mNavView);
			break;
		case R.id.layoutMenuMatch:
			Globals.viewNumber = 3;
			this.m_txtTitle.setText("COLOR MATCHING");
			this.m_btnAddFriend.setVisibility(View.INVISIBLE);
			this.m_txtSignal .setVisibility(View.INVISIBLE);
			currentFragment = new ColorMatchFragment(inflater, R.layout.fragment_color_match);
			frameMain.addView(currentFragment.fragmentLayout,0);
			mDrawerLayout.closeDrawer(mNavView);
			break;
		case R.id.layoutMenuFriend:
			Globals.viewNumber = 4;
			this.m_btnAddFriend.setVisibility(View.VISIBLE);
			this.m_txtSignal .setVisibility(View.INVISIBLE);
			this.m_txtTitle.setText("FRIENDS");
			currentFragment = new FriendFragment(inflater, R.layout.fragment_friends);
			this.m_btnAddFriend.setOnClickListener((OnClickListener) currentFragment);
			frameMain.addView(currentFragment.fragmentLayout,0);
			mDrawerLayout.closeDrawer(mNavView);
			break;
			
		case R.id.layoutMenuChat:
			Globals.viewNumber = 5;
			this.m_btnAddFriend.setVisibility(View.INVISIBLE);
			this.m_txtSignal .setVisibility(View.INVISIBLE);
			this.m_txtTitle.setText("CHAT");	
			currentFragment = new ChatContactFragment(inflater, R.layout.fragment_chatcontact);
			frameMain.addView(currentFragment.fragmentLayout,0);
			mDrawerLayout.closeDrawer(mNavView);
			break;
		case R.id.layoutMenuSettings:
			Globals.viewNumber = 6;
			break;
		case R.id.layoutMenuSignOut:
			Globals.viewNumber = 7;
			Intent m = new Intent(this,ActivitySignIn.class);
			this.startActivity(m);
			this.finish();
			break;		
		}
		
	}
	public void openPreviewFragment(String title)
	{
		Globals.viewNumber = 8;
		frameMain.removeAllViews();
		this.m_txtTitle.setText(title.toUpperCase());	
		currentFragment = new ColorPreviewFragment(inflater, R.layout.fragment_color_preview);
		frameMain.addView(currentFragment.fragmentLayout,0);
	}
	public void openChatFragment()
	{
		frameMain.removeAllViews();
		this.m_txtTitle.setText("CHAT");	
		currentFragment = new ChatFragment(inflater, R.layout.fragment_chat);
		frameMain.addView(currentFragment.fragmentLayout,0);
	}
	public void onDestroy()
	{
		if (currentFragment instanceof FriendFragment)
			((FriendFragment) currentFragment).destroy();
		if (currentFragment instanceof DrawFragment)
			((DrawFragment)currentFragment).removeFree();
		if (currentFragment instanceof ColorCaptureFragment)
			((ColorCaptureFragment)currentFragment).freeMemory();
		if (this.mainFragment != null && this.currentFragment instanceof HomeFragment)
			((HomeFragment)currentFragment).freeMemory();
		System.gc();
		System.exit(1);
		super.onDestroy();
	}

}
