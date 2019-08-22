package com.songu.shadow.drawing.model;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Matrix;
import android.graphics.Point;

public class CustomPoint implements Serializable{
	private static final long serialVersionUID = -945525483360246781L;
	
		//new CustomPoint(currentColor,currentSnippet,brushSize,currentBrushType);
		public CustomPoint(int color,int number,float size,int type)
		{
			this.color = color;
			this.snippetNumber = number;
			this.size = size;
			this.brushType = type;
		}
		public void addPoint(Point p)
		{
			if (line == null)
				this.line = new ArrayList<Point>();
			this.line.add(p);
		}
		public void setPoint(Point p, int index)
		{
			if (line.size() > index)
			{
				line.set(index, p);
			}
			else
				line.add(p);
		}
		public CustomPoint(float x,float y, boolean cut, int c, int snippetNumber, float size , boolean eraser, int brushType){
			this.x =x;
			this.y = y;
			this.cut = cut;
			color = c;
			this.snippetNumber = snippetNumber;
			this.size = size;
			this.eraser = eraser;
			this.brushType = brushType;
			
		}
		public CustomPoint()
		{
			
		}
		public boolean checkOneLine(float x1, float y1, float x2, float y2,float x, float y)
		{
			float m = (y1 - y2) / (x1 - x2);
			int value =(int)( m * (x1 - x) - (y1 - y));
			
			if (value >= 0)
			{
				return false;
			}
			else return true;
		}
		
		public boolean drawn;
		public boolean eraser;
		public int snippetNumber;
		public int brushType;
		public float size;
		public int color;
		public float x,y;
		public boolean cut;
		public ArrayList<Point> line;
		public boolean isDraw = false;
		
		public int selXX1 = -1;
		public int selYY1 = -1;
		public int selXX2 = -1;
		public int selYY2 = -1;
		public int rotateAngle;
		
		public float deltaP1X = 0;
		public float deltaP2X = 0;
		public float deltaP3X = 0;
		public float deltaP4X = 0;
		
		public float deltaP1Y = 0;
		public float deltaP2Y = 0;
		public float deltaP3Y = 0;
		public float deltaP4Y = 0;
		
		public Matrix matrix = null;
		
		
		
		
		
}