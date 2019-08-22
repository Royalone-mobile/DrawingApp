package com.songu.shadow.drawing.popup;



import com.songu.shadow.drawing.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;



public class PopupProgressBarDialog {  //Progress Dialog only show progress.....
	public View parent;
	public PopupWindow popupWindow;
	public TextView m_ctrl_txtError;
	public Button m_btnItem1;
	public Button m_btnItem2;
	public Button m_btnItem3;
	
	public PopupProgressBarDialog(Context paramContext)
	{
		this.parent = ((LayoutInflater)paramContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_progress, null);
		this.popupWindow = new PopupWindow(this.parent,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		this.popupWindow.setOutsideTouchable(false);
		
		
	}
	public void showAtLocation(View pView,int left,int top)
	{
		this.popupWindow.showAtLocation(pView, Gravity.CENTER, left, top);
	}
	public void hide()
	{
		this.popupWindow.dismiss();
	}
	public boolean isVisible()
	{
		return this.popupWindow.isShowing();
	}
}
