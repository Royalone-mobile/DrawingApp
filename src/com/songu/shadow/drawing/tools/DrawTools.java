package com.songu.shadow.drawing.tools;

import java.util.ArrayList;

import android.graphics.Point;

public class DrawTools {
	
	public static ArrayList<Point> bresenhamAlgorithm(int x1, int y1, 
			  int x2, int y2, Point mBitmapBrushDimensions) {
		ArrayList<Point> tempPoints = new ArrayList<Point>();
		
		Point halfBrushSize = new Point(mBitmapBrushDimensions.x/4,mBitmapBrushDimensions.y/4);
		if (halfBrushSize.x<=2)
			halfBrushSize.x=3;
		if (halfBrushSize.y<=2)
			halfBrushSize.y =3;

	
	/*
	// SINGLE OCTANT -- INACTIVE CODE
	
	int x,                       // Current x position
	y = y1,                  // Current y position
	e = 0,                   // Current error
	m_num = y2 - y1,         // Numerator of slope
	m_denom = x2 - x1,       // Denominator of slope
	threshold  = m_denom/2;  // Threshold between E and NE increment 
	
	for (x = x1; x < x2; x++) {
	view.setPixel(x,y);
	e += m_num;
	
	if (e > threshold) {
	e -= m_denom;
	y++;
	}
	}
	view.setPixel(x,y);
	*/
	
	// MULTIPLE OCTANTS -- ACTIVE CODE
	// Bresenham algorithm for all 8 octants.
	/* Note:  in four octants, including the entire first quadrant, 
	my code produces exactly the same results as yours.  In the
	other four octants, it effectively makes the opposite decisions
	about the error = .5 case mentioned in Damian's e-mail. */
	
	// If slope is outside the range [-1,1], swap x and y
	boolean xy_swap = false;
	if (Math.abs(y2 - y1) > Math.abs(x2 - x1)) {
	xy_swap = true;
	int temp = x1;
	x1 = y1;
	y1 = temp;
	temp = x2;
	x2 = y2;
	y2 = temp;
	}
	
	// If line goes from right to left, swap the endpoints
	if (x2 - x1 < 0) {
	int temp = x1;
	x1 = x2;
	x2 = temp;
	temp = y1;
	y1 = y2;
	y2 = temp;
	}
	
	int x,                       // Current x position
	y = y1,                  // Current y position
	e = 0,                   // Current error
	m_num = y2 - y1,         // Numerator of slope
	m_denom = x2 - x1,       // Denominator of slope
	threshold  = m_denom/2;  // Threshold between E and NE increment 
	
	for (x = x1; x < x2; x+=halfBrushSize.x/2) {
	if (xy_swap)
		tempPoints.add(new Point(y, x));
	else 
		tempPoints.add(new Point(x, y));
	
	e += m_num;
	
	// Deal separately with lines sloping upward and those
	// sloping downward
	if (m_num < 0) {
	if (e < -threshold) {
	  e += m_denom;
	  y-=halfBrushSize.y/2;
	}
	}
	else if (e > threshold) {
	e -= m_denom;
	y+=halfBrushSize.y/2;
	}
	}
	
	if (xy_swap)
		tempPoints.add(new Point(y, x));
	else 
		tempPoints.add(new Point(x, y));
	
	
	return tempPoints;
	}


}
