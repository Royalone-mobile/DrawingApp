package com.songu.shadow.drawing.fragments;

import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class CustomFragment {
	
	public LinearLayout fragmentLayout;
	
	public CustomFragment(LayoutInflater inflater, int resource){
		fragmentLayout = getFragment(inflater, resource);
	}

	private LinearLayout getFragment(LayoutInflater inflater, int resource){
		return ((LinearLayout)inflater.inflate(resource, null));
	}
	
	void exit(){
		
	}
	
}
