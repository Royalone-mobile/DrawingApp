package com.songu.shadow.drawing.popup;



import com.songu.shadow.drawing.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;



public class SearchPopup implements View.OnClickListener{  //Search Id for Contact..
    public View parent;
    public PopupWindow popupWindow;
    public Button btnOk;
    public Button btnCancel;
    public EditText editPattern;

    public SearchPopup(Context paramContext)
    {
        this.parent = ((LayoutInflater)paramContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_popupsearch, null);

        //this.parent.findViewBy)
        this.popupWindow = new PopupWindow(this.parent, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,true);
        this.btnOk = (Button) parent.findViewById(R.id.btnSearch);
        this.btnCancel = (Button) parent.findViewById(R.id.btnSearchCancel);
        editPattern = (EditText) parent.findViewById(R.id.editSearchId);
        btnCancel.setOnClickListener(this);

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

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.btnSearchCancel:
                hide();
                break;
        }
    }
}