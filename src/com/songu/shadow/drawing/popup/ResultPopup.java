package com.songu.shadow.drawing.popup;



import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;










import java.util.List;

import com.songu.shadow.drawing.MainActivity;
import com.songu.shadow.drawing.R;
import com.songu.shadow.drawing.doc.Globals;
import com.songu.shadow.drawing.fragments.FriendFragment;
import com.songu.shadow.drawing.model.UserProfile;
import com.songu.shadow.drawing.utils.Imageloader;
import com.songu.shadow.drawing.utils.SyncImageLoader;


public class ResultPopup implements View.OnClickListener{   //Result from Search ID Popup When you add member to contact..
    public View parent;
    public PopupWindow popupWindow;
    public Button btnOk;
    public Button btnCancel;
    public Button btnClose;
    public ImageButton btnNext;
    public ImageButton btnPrev;
    public TextView mName;
    public ImageView mPhoto;

    public List<UserProfile> results;
    public int sp = 0;
    public Imageloader loader;

    public Context con;
    public FriendFragment frag;



    public ResultPopup(Context paramContext)
    {
        this.parent = ((LayoutInflater)paramContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_popupresult, null);

        //this.parent.findViewBy)
        this.popupWindow = new PopupWindow(this.parent, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,true);
        this.btnOk = (Button) parent.findViewById(R.id.btnAcceptPerson);
        this.btnCancel = (Button) parent.findViewById(R.id.btnDeclinePerson);
        this.btnClose = (Button) parent.findViewById(R.id.btnResultClose);
        this.btnNext = (ImageButton)parent.findViewById(R.id.btnNextPerson);
        this.btnPrev = (ImageButton) parent.findViewById(R.id.btnBackPerson);
        this.mName = (TextView) parent.findViewById(R.id.txtPersonName);
        this.mPhoto = (ImageView)parent.findViewById(R.id.imgResultPerson);

        btnCancel.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        con = paramContext;


        loader = new Imageloader(paramContext);

    }
    public void showAtLocation(View pView,int left,int top)
    {
        checkButtonVisible();
        this.popupWindow.showAtLocation(pView, Gravity.CENTER, left, top);
        updateView();
    }
    public void updateView()
    {
        if (results != null) {
            this.mName.setText(results.get(sp).mFirstName + " " + results.get(sp).mLastName);

            if (!results.get(sp).mImage.equals("")) {
            	mPhoto.setTag(results.get(sp).mImage);
                loader.DisplayImage(results.get(sp).mImage, (Activity)con, mPhoto);
            }
        }
    }
    public void setResults(List<UserProfile> data,FriendFragment f)
    {
        results = data;        
        this.frag = f;
    }
    public void hide()
    {
        this.popupWindow.dismiss();
    }
    public boolean isVisible()
    {
        return this.popupWindow.isShowing();
    }

    public void checkButtonVisible()
    {
        if (results != null) {
            if (this.results.size() - 1 == sp) {
                btnNext.setVisibility(View.INVISIBLE);
            } else btnNext.setVisibility(View.VISIBLE);

            if (sp == 0) {
                btnPrev.setVisibility(View.INVISIBLE);
            } else btnPrev.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.btnAcceptPerson:
                if (results != null) {
                	int sz = results.size();
                    Globals.m_service.onRegisterContact(Globals.mAccount.id, results.get(sp).id, this.frag);
                    //Globals.contactList.add(results.get(sp));
                    if (sp == results.size() - 1) {
                        if (sp == 0) {
                        	
                        	if (sz == 1)
                            {
                            	final MainActivity act = (MainActivity) con;
                            	act.runOnUiThread(new Runnable()
                            	{
                            		public void run()
                            		{
                            			act.m_txtSignal.setVisibility(View.INVISIBLE);
                            		}
                            	});
                            	
                            }
                        	
                            hide();
                            return;
                        }
                        sp--;
                       // results.remove(sp);
                    } //else results.remove(sp);
                    
                    if (sz == 1)
                    {
                    	final MainActivity act = (MainActivity) con;
                    	act.runOnUiThread(new Runnable()
                    	{
                    		public void run()
                    		{
                    			act.m_txtSignal.setVisibility(View.INVISIBLE);
                    		}
                    	});
                    	
                    }
                }
                checkButtonVisible();
                updateView();
                break;
            case R.id.btnDeclinePerson:
                if (results != null) {
                    if (sp == results.size() - 1) {
                        if (sp == 0) {
                            hide();
                            return;
                        }
                        sp--;
                        results.remove(sp);
                    } else results.remove(sp);
                }
                checkButtonVisible();
                updateView();
                break;
            case R.id.btnResultClose:
                hide();
                break;
            case R.id.btnNextPerson:
                sp++;
                checkButtonVisible();
                updateView();
                break;
            case R.id.btnBackPerson:
                sp--;
                checkButtonVisible();
                updateView();
                break;


        }
    }
}