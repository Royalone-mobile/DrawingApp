package com.songu.shadow.drawing.model;

import java.io.Serializable;

import com.songu.shadow.drawing.tools.Tools;
import android.graphics.Color;

public class CustomColor implements Serializable {
	private static final long serialVersionUID = 5796523214612154412L;
	
	public int color;
	public String rgb;
	public String cmyk;
	public String hex;
	public String dateCaptured;
	public String name;
	public String location;
	
	public int r;
	public int g;
	public int b;
	
	public int c;
	public int m;
	public int y;
	public int k;
	
	public boolean favorited;
	
	public CustomColor(int color){
		setColorData(color);
	}
	
	public void setColorData(int color){
		setData(color);
	}
	
	void setData(int color){
		this.rgb = Tools.getRGB(color);
		this.hex = Tools.getHex(color);
		this.color = Color.parseColor(this.hex);
		this.cmyk = Tools.getCMYK(this.color);
		this.dateCaptured = Tools.getDate();
		
		r = (this.color >> 16) & 0xFF;
		g = (this.color >> 8) & 0xFF;
		b = (this.color >> 0) & 0xFF;

		int space1 = this.cmyk.indexOf(" ", 0);
		c = Integer.parseInt(this.cmyk.substring(0, space1));
		int space2 = this.cmyk.indexOf(" ", space1+1);
		m = Integer.parseInt(this.cmyk.substring(space1+1, space2));
		space1 = this.cmyk.indexOf(" ", space2+1);
		y = Integer.parseInt(this.cmyk.substring(space2+1, space1));
		space2 = cmyk.length();
		k = Integer.parseInt(this.cmyk.substring(space1+1, space2));
	}
	
	public String getDetails(){
		
		return name +"\n" + "From " + location + "\n" + "RGB:" + rgb + "HEX:"+hex + "  CMYK" +cmyk; 
	}
}
