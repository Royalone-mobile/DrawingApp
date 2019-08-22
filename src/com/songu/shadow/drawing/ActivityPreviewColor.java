package com.songu.shadow.drawing;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.songu.shadow.drawing.doc.Globals;
import com.songu.shadow.drawing.model.CustomColor;
import com.songu.shadow.drawing.model.UserProfile;

public class ActivityPreviewColor extends Activity implements View.OnClickListener {

	
	CustomColor color;
	Context context;
	public LinearLayout layoutActionbar;
	public TextView txtTitle;
	public ImageView btnBack;


    //This method is First called-------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_color_preview);
        this.layoutActionbar = (LinearLayout) findViewById(R.id.layoutPreviewActionbar);
        this.txtTitle = (TextView) this.findViewById(R.id.txtPreviewTitle);
        this.btnBack = (ImageView) this.findViewById(R.id.imgPreviewBack);
        
        LayoutParams lp = (LayoutParams) this.layoutActionbar.getLayoutParams();
        //lp.height = Globals.g_actionbarHeight;
        this.layoutActionbar.setLayoutParams(lp);
        this.btnBack.setOnClickListener(this);
        setColor(Globals.selColor);
    }   
    public void setColor(CustomColor c)
    {
		color = c;
		this.setTitle(c.name);		
		updateUI();
	}
    public void setTitle(String name)
    {
    	this.txtTitle.setText(name);
    }
	public void fromRGB(float r, float g , float b) {
		
		double x = r / 255;
		double y = g / 255;
		double z = b / 255;		
		if (x > 0.04045) x = Math.pow(((x + 0.055) / 1.055),2.4);
		else x = x / 12.92;
		if (y > 0.04045) y = Math.pow((y + 0.055) / 1.055,2.4);
		else y = y / 12.92;
		if (z > 0.04045) z = Math.pow((z + 0.055) / 1.055,2.4);
		else z = z / 12.92;
		x = x * 100;
		y = y * 100;
		z = z * 100;
		
		double x1 = (float) ((0.412453 * x) + (0.357580 * y) + (0.180423  * z));
		double y1 = (float) ((0.212671 * x) + (0.715160 * y) + (0.072169  * z));
		double z1 = (float) ((0.019334 * x) + (0.119193 * y) + (0.950227 * z));
		
		double tx = x1 / 95.047;
		double ty = y1 / 100.000;
		double tz = z1 / 108.883;
		
		if (tx > 0.008856) tx = Math.pow(tx, 0.333);
		else  tx = (7.787 * tx) + (16 / 116);
		
		if (ty > 0.008856) ty = Math.pow(ty, 0.333);
		else ty = (7.787 * ty) + (16 / 116);
		
		if (tz > 0.008856) tz = Math.pow(tz,0.333);
		else tz = (7.787 * tz) + (16 / 116);
		
		int l = (int) ((116 * ty) - 16);
		int a = (int) (500 * (tx - ty));
		int b1 = (int) (200 * (ty - tz));
		
		String ss = String.valueOf(l) + "-" + String.valueOf(a) + "-" + String.valueOf(b1);
		
		
		((TextView)this.findViewById(R.id.color_preview_lab)).setText(ss);
			
		
    }
	public void updateUI(){
		((ImageView)this.findViewById(R.id.color_preview_circle)).setColorFilter(color.color);
		
		if (color.location==null)
			((TextView)this.findViewById(R.id.color_preview_location)).setText(R.string.unknown_location);
		else
			((TextView)this.findViewById(R.id.color_preview_location)).setText(color.location);
		
		((TextView)this.findViewById(R.id.color_preview_time)).setText(color.dateCaptured);
		((TextView)this.findViewById(R.id.color_preview_rgb)).setText(color.rgb);
		((TextView)this.findViewById(R.id.color_preview_hex)).setText(color.hex);
		((TextView)this.findViewById(R.id.color_preview_cmyk)).setText(color.cmyk);
		fromRGB(color.r,color.g,color.b);
	}
    @Override
    public void onClick(View v) {        
    	switch(v.getId())
    	{
    	case R.id.imgPreviewBack:
    		this.finish();
    		break;
    	}
    }
}