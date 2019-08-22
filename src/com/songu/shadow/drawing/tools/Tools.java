package com.songu.shadow.drawing.tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.songu.shadow.drawing.R;
import com.songu.shadow.drawing.model.CustomColor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.format.Time;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;



@SuppressWarnings("deprecation")
public class Tools {
	
	
	
	public static CustomColor getRandomColor(){
		return new CustomColor((int) (1000000d + Math.random()*-9000000d));
	}
	
	public static int numtodp(Context c, int in){
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, in, c.getResources().getDisplayMetrics());

	}
	
	public static String getRGB(int c){
		int r = (c >> 16) & 0xFF;
		int g = (c >> 8) & 0xFF;
		int b = (c >> 0) & 0xFF;
		return r +" " + g + " " +b;
	}
	
	public static int getColorDifference(CustomColor c1, CustomColor c2){
		int difference = 0;
		difference += Math.abs(c1.r-c2.r);
		difference += Math.abs(c1.g-c2.g);
		difference += Math.abs(c1.b-c2.b);
		return difference;
	}
	
	public static int getDarknessDifference(CustomColor c1, CustomColor c2){
		int difference = 0;
		difference = Math.abs((c1.r+c1.g +c1.b) - (c2.r+c2.g+c2.b));
		return difference;
	}
	
	public static int getColorPercentageDifference(CustomColor c1, CustomColor c2){
		int diffRed   = Math.abs(c1.r  - c2.r);
		int diffGreen = Math.abs(c1.g  - c2.g);
		int diffBlue  = Math.abs(c1.b  - c2.b);
		
		float pctDiffRed   = (float)diffRed   / 255;
		float pctDiffGreen = (float)diffGreen / 255;
		float pctDiffBlue   = (float)diffBlue  / 255;
		
		return (int)(100 - ((pctDiffRed + pctDiffGreen + pctDiffBlue) / 3 * 100));
	}
	
	public static String getHex(int color){
		return String.format("#%06X", (0xFFFFFF & color));
	}
	
	
	public static String getDate(){
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		String year = ""+today.year;
		
		return today.monthDay+", "+ getMonthName(today.month)  +" "+year.substring(2) + " " + today.hour+ ":"+ today.minute; 
	} 
	
	public static String getMonthName(int month){
		switch(month+1){
		case 1:
			return "Jan";

		case 2:
			return "Feb";
		
		case 3:
			return "Mar";
			
		case 4:
			return "Apr";
			
		case 5:
			return "May";
					
		case 6:
			return "Jun";
			
		case 7:
			return "Jul";
			
		case 8:
			return "Aug";
			
		case 9:
			return "Sep";
					
		case 10:
			return "Oct";
					
		case 11:
			return "Nov";
			
		case 12:
			return "Dec";
		}
		
		return "";
	}
	public static int getDarkness(int c)
	{
		float computedC = 0;
		 float computedM = 0;
		 float computedY = 0;
		 float computedK = 0;

		 int r = (c >> 16) & 0xFF;
		 int g = (c >> 8) & 0xFF;
		 int b = (c >> 0) & 0xFF;

		 // BLACK
		 if (r==0 && g==0 && b==0) {
		  computedK = 1;
		  return 1;
		 }

		 computedC = 1 - (r/255f);
		 computedM = 1 - (g/255f);
		 computedY = 1 - (b/255f);

		 float minCMY = Math.min(computedC,Math.min(computedM,computedY));

		 if (1 - minCMY != 0){
			 computedC = (computedC - minCMY) / (1 - minCMY) ;
			 computedM = (computedM - minCMY) / (1 - minCMY) ;
			 computedY = (computedY - minCMY) / (1 - minCMY) ;
		 }
		 computedK = minCMY;

		 return (int)computedK;
	}
	public static String getCMYK(int c){
		 float computedC = 0;
		 float computedM = 0;
		 float computedY = 0;
		 float computedK = 0;

		 int r = (c >> 16) & 0xFF;
		 int g = (c >> 8) & 0xFF;
		 int b = (c >> 0) & 0xFF;

		 // BLACK
		 if (r==0 && g==0 && b==0) {
		  computedK = 1;
		  return "0 0 0 100";
		 }

		 computedC = 1 - (r/255f);
		 computedM = 1 - (g/255f);
		 computedY = 1 - (b/255f);

		 float minCMY = Math.min(computedC,Math.min(computedM,computedY));

		 if (1 - minCMY != 0){
			 computedC = (computedC - minCMY) / (1 - minCMY) ;
			 computedM = (computedM - minCMY) / (1 - minCMY) ;
			 computedY = (computedY - minCMY) / (1 - minCMY) ;
		 }
		 computedK = minCMY;

		 return (int)(computedC*100f) + " " + (int)(computedM*100f) + " " + (int)(computedY*100f) + " " + (int)(computedK*100f);
	}
	
	public static void setColorTextData(CustomColor currentColor , TextView RGBText, TextView HEXText, TextView CMYKText){
		SpannableString rgb=  new SpannableString("RGB ");
        rgb.setSpan(new StyleSpan(Typeface.BOLD), 0, rgb.length(), 0); 
        
        SpannableString hex=  new SpannableString("HEX ");
        hex.setSpan(new StyleSpan(Typeface.BOLD), 0, hex.length(), 0); 
        
        SpannableString cmyk=  new SpannableString("CMYK ");
        cmyk.setSpan(new StyleSpan(Typeface.BOLD), 0, cmyk.length(), 0); 
        
        SpannableString r = new SpannableString(""+currentColor.r);  
	    r.setSpan(new ForegroundColorSpan(Color.parseColor("#cc0000")), 0, ((String)(""+currentColor.r)).length(), 0);  
	    
	    SpannableString g = new SpannableString(" "+currentColor.g);  
	    g.setSpan(new ForegroundColorSpan(Color.parseColor("#00cc05")), 0, ((String)(" "+currentColor.g)).length(), 0);  
	    
	    SpannableString b = new SpannableString(" "+currentColor.b);  
	    b.setSpan(new ForegroundColorSpan(Color.parseColor("#000ecc")), 0, ((String)(" "+currentColor.b)).length(), 0);  
	    


	    SpannableString c = new SpannableString(""+currentColor.c);  
	    c.setSpan(new ForegroundColorSpan(Color.parseColor("#00ccb6")), 0, ((String)(""+currentColor.c)).length(), 0);  
	    
	    SpannableString m = new SpannableString(" "+currentColor.m);  
	    m.setSpan(new ForegroundColorSpan(Color.parseColor("#ca00cc")), 0, ((String)(" "+currentColor.m)).length(), 0);  
	    
	    SpannableString y = new SpannableString(" "+currentColor.y);  
	    y.setSpan(new ForegroundColorSpan(Color.parseColor("#ccb600")), 0, ((String)(" "+currentColor.y)).length(), 0);  
	    
	    SpannableString k = new SpannableString(" "+currentColor.k);  
	    k.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ((String)(" "+currentColor.k)).length(), 0);  
	    
	    
	    SpannableString hexVal = new SpannableString(""+currentColor.hex);  
	    hexVal.setSpan(new ForegroundColorSpan(Color.BLACK), 0, currentColor.hex.length(), 0); 
        
        RGBText.setText("");
        RGBText.append(rgb);
        RGBText.append(r);
        RGBText.append(g);
        RGBText.append(b);
        
        HEXText.setText("");
        HEXText.append(hex);
        HEXText.append(hexVal);
        CMYKText.setText("");
        CMYKText.append(cmyk);
        CMYKText.append(c);
        CMYKText.append(m);
        CMYKText.append(y);
        CMYKText.append(k);
	}
	
	public static void setColorTextData(CustomColor currentColor , TextView colorText){
		SpannableString rgb=  new SpannableString("RGB ");
        rgb.setSpan(new StyleSpan(Typeface.BOLD), 0, rgb.length(), 0); 
        
        SpannableString hex=  new SpannableString("HEX ");
        hex.setSpan(new StyleSpan(Typeface.BOLD), 0, hex.length(), 0); 
        
        SpannableString cmyk=  new SpannableString("CMYK ");
        cmyk.setSpan(new StyleSpan(Typeface.BOLD), 0, cmyk.length(), 0); 
        
        SpannableString r = new SpannableString(""+currentColor.r);  
	    r.setSpan(new ForegroundColorSpan(Color.parseColor("#cc0000")), 0, ((String)(""+currentColor.r)).length(), 0);  
	    
	    SpannableString g = new SpannableString(" "+currentColor.g);  
	    g.setSpan(new ForegroundColorSpan(Color.parseColor("#00cc05")), 0, ((String)(" "+currentColor.g)).length(), 0);  
	    
	    SpannableString b = new SpannableString(" "+currentColor.b);  
	    b.setSpan(new ForegroundColorSpan(Color.parseColor("#000ecc")), 0, ((String)(" "+currentColor.b)).length(), 0);  
	    


	    SpannableString c = new SpannableString(""+currentColor.c);  
	    c.setSpan(new ForegroundColorSpan(Color.parseColor("#00ccb6")), 0, ((String)(""+currentColor.c)).length(), 0);  
	    
	    SpannableString m = new SpannableString(" "+currentColor.m);  
	    m.setSpan(new ForegroundColorSpan(Color.parseColor("#ca00cc")), 0, ((String)(" "+currentColor.m)).length(), 0);  
	    
	    SpannableString y = new SpannableString(" "+currentColor.y);  
	    y.setSpan(new ForegroundColorSpan(Color.parseColor("#ccb600")), 0, ((String)(" "+currentColor.y)).length(), 0);  
	    
	    SpannableString k = new SpannableString(" "+currentColor.k);  
	    k.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ((String)(" "+currentColor.k)).length(), 0);  
	    
	    
	    SpannableString hexVal = new SpannableString(""+currentColor.hex);  
	    hexVal.setSpan(new ForegroundColorSpan(Color.BLACK), 0, currentColor.hex.length(), 0); 
	    
	    
	    SpannableStringBuilder spanstr = new SpannableStringBuilder("");

	    spanstr.append(rgb);
	    spanstr.append(r);
	    spanstr.append(g);
	    spanstr.append(b);
        
	    spanstr.append("   ");
	    spanstr.append(hex);
	    spanstr.append(hexVal);
        
	    spanstr.append("   ");
	    spanstr.append(cmyk);
	    spanstr.append(c);
	    spanstr.append(m);
	    spanstr.append(y);
	    spanstr.append(k);
	    
	    colorText.setText(spanstr);
	}
	
	
	public static View getViewByPosition(int pos, ListView listView) {
	    final int firstListItemPosition = listView.getFirstVisiblePosition();
	    final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
	 
	    if (pos < firstListItemPosition || pos > lastListItemPosition ) {
	        return listView.getAdapter().getView(pos, null, listView);
	    } else { 
	        final int childIndex = pos - firstListItemPosition;
	        return listView.getChildAt(childIndex);
	    } 
	} 
	
	public static void serializeColors(ArrayList<CustomColor> colors, Context c){
		try
	      {
	         FileOutputStream fileOut =
	         new FileOutputStream(c.getApplicationInfo().dataDir+"/"+"colors");
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(colors);
	         out.close();
	         fileOut.close();
	         System.out.printf(c.getApplicationInfo().dataDir+"/"+"colors");
	      }catch(IOException i)
	      {
	          i.printStackTrace();
	      }
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<CustomColor> getSerializedColors(Context c){
		ArrayList<CustomColor> colors;
		try
	      {
	         FileInputStream fileIn = new FileInputStream(c.getApplicationInfo().dataDir+"/"+"colors");
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         colors = (ArrayList<CustomColor>) in.readObject();
	         in.close();
	         fileIn.close();
	         return colors;
	      }catch(IOException i)
	      {
	         return new ArrayList<CustomColor>();
	      }catch(ClassNotFoundException ce)
	      {
	         return new ArrayList<CustomColor>();
	      }

	}
	
	public static ArrayList<CustomColor> getFavoritedColors(ArrayList<CustomColor> colors){
		ArrayList<CustomColor> fColors = new ArrayList<CustomColor>();
		
		for (int i =0; i<colors.size(); i++){
			if (colors.get(i).favorited)
				fColors.add(colors.get(i));
		}
		
		return fColors;
		
	}
	
	public static void alert(final int message, Context c){
        	 AlertDialog.Builder adb = new AlertDialog.Builder(c);
             adb.setMessage(message);
             adb.setTitle(R.string.alert); 
             adb.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { 
                 public void onClick(DialogInterface dialog, int which) {
                 	dialog.dismiss();
               } }); 

             adb.create().show();
	}
	
	public static int getActionBarHeight(Activity act){
		return (int) act.getResources().getDimension(R.dimen.actionBarSize);
	}

}
