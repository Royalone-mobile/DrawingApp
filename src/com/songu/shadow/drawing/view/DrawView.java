package com.songu.shadow.drawing.view;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import com.songu.shadow.drawing.R;
import com.songu.shadow.drawing.doc.Globals;
import com.songu.shadow.drawing.fragments.DrawFragment;
import com.songu.shadow.drawing.model.CustomPoint;
import com.songu.shadow.drawing.tools.DrawTools;
import com.songu.shadow.drawing.tools.SerializationTools;
import com.songu.shadow.drawing.tools.Tools;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DrawView extends View{

	

	public float brushSize =3;
	DrawFragment drawFragment;
	int currentSnippet;
	int currentColor;
	int forgroundColor;
	int currentBrushType; //type is defined by R.id.brush_stencil etc.
	
	Point brushDimensions;
	public boolean colorPicking;
	public boolean hadSelection = false;
	//public int selXX1 = -1;
	//public int selXX2 = -1;
	//public int selYY1 = -1;
	//public int selYY2 = -1;
	public int focusSquare = 0;
	//public int rotateAngle = 0;
	public int centerX;
	public int centerY;
	
	//public Matrix matrix = null;
	
	public Bitmap cutBitmap;
	public Bitmap foreground;
	
	public float perspectDegree1 = 0;
	public float perspectDegree2 = 0;
	
	
	//public floatactivePoint. deltaP1X;
	//public floatactivePoint. deltaP2X;
	//public floatactivePoint. deltaP3X;
	//public floatactivePoint. deltaP4X;
	
	//public float deltaP1Y;
	//public float deltaP2Y;
	//public float deltaP3Y;
	//public float deltaP4Y;
	
	
	
	
	class Brush{
		Bitmap brushBitmap;
		Point bitmapBrushDimensions;
		int brushType;
		private int size;
		
		
		public void setBrush(int brushType, int size){
			if (this.brushType==brushType && this.size==size){
				//do nothing
			}else{
				
				this.size = size;
				this.brushType = brushType;
				if (brushType != R.id.brush_pencil){
					Log.d("draw", "processing");
					Options options = new BitmapFactory.Options();
					options.inScaled = false; 
					options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			
					brushBitmap = BitmapFactory.decodeResource(getContext().getResources(), getBrush(brushType),options);
					brushBitmap = Bitmap.createScaledBitmap(brushBitmap, size, size, true);
					bitmapBrushDimensions = new Point(brushBitmap.getWidth(), brushBitmap.getHeight());
				}else
					brushBitmap = null;
			}
			
			
			
		}
		
	}
	
	public ArrayList<CustomPoint> points;
	Paint p;
	Paint p1;
	Paint canvasPaint;
	
	Bitmap drawBitmap;
	Canvas canvas;
	Bitmap originalBitmap;
	Canvas orgCanvas;
	Canvas fCanvas;
	Bitmap selectBitmap;
	Canvas selCanvas;
	
	Canvas cropCanvas;
	Bitmap transformedBitmap;
	public boolean eraserMode;

	
	public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		currentColor = Color.GRAY;
		forgroundColor = Color.WHITE;
		points = new ArrayList<CustomPoint>();
		canvasPaint = new Paint();
		canvasPaint.setAntiAlias(true);
		canvasPaint.setStyle(Style.STROKE);
		p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setColor(Color.BLACK);
		p.setStrokeWidth(12);
		p.setStrokeCap(Cap.ROUND);
		p.setFilterBitmap(true);
	}

	public DrawView(Context context) {
		super(context);

	}
	
	public void setFragment(DrawFragment frag){
		drawFragment = frag;
	}
	
	public void setColor(int color){
		//currentColor = color;
		if (this.currentBrushType == R.id.draw_watercolor)
		{
			this.forgroundColor = color;
			activePoint = new CustomPoint();
			activePoint.snippetNumber = currentSnippet;
			activePoint.color = this.forgroundColor;
			activePoint.brushType = currentBrushType;
			this.drawingActivePoint();			
			currentSnippet++;
			this.points.add(activePoint);
			drawFragment.undoActions.addUndoButtonItem();
			this.undoTo(currentSnippet);
		}
		else currentColor = color;
	}
	
	public void setBrushSize(float size){
		brushSize = size;
		
		brushDimensions = new Point((int)brushSize, (int)brushSize);
	}
	
	public void setBrushType(int id){
		saveTransform();
		initData();
		currentBrushType = id;
		brushDimensions = new Point((int)brushSize, (int)brushSize);
		invalidate();
	}
	public void saveTransform()
	{
		this.canvasPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
		if (this.transformedBitmap != null)
		{
			if (activePoint.matrix != null)
			{
				orgCanvas.drawBitmap(transformedBitmap, activePoint.matrix,canvasPaint);
			}
			else this.orgCanvas.drawBitmap(this.transformedBitmap,Math.min(activePoint.selXX1 + activePoint.deltaP1X,
					activePoint.selXX2+ activePoint.deltaP4X) ,
					Math.min(activePoint.selYY1 + activePoint.deltaP1Y, activePoint.selYY2 + activePoint.deltaP4Y),canvasPaint);
			transformedBitmap.eraseColor(Color.TRANSPARENT);
		}
	}
	public int getBrush(int type){
		switch (type) {
		case R.id.brush_stencil:
			return R.drawable.br_marker;
			//return R.drawable.br_acrylic1;
		case R.id.brush_pencil:
			return 0;
		case R.id.brush_fat_marker:
			
			return R.drawable.br_paint;
		case R.id.brush_marker:
			return R.drawable.brush_sponge;
		case R.id.brush_paint:
			//return R.drawable.br_paint;
			return R.drawable.br_bristles;
		case R.id.draw_watercolor:
			return R.drawable.br_watercolor;
		default:
			return 0;
		}
	}
	
	public void setEraserMode(boolean b){
		eraserMode = b;
	}
	
	public void clearDrawing(){
		points.clear();
		currentSnippet = 0;
		canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		this.forgroundColor = Color.WHITE;
		p.setColor(forgroundColor);
		p.setColorFilter(null);
		p.setStyle(Style.FILL_AND_STROKE);
		this.fCanvas.drawRect(new Rect(-1,-1,fCanvas.getWidth(),fCanvas.getHeight()),p);
		
		
		originalBitmap = null;
		orgCanvas = null;
		originalBitmap = Bitmap.createBitmap(fCanvas.getWidth(), fCanvas.getHeight(), Config.ARGB_4444);
		orgCanvas = new Canvas(originalBitmap);			
		originalBitmap.eraseColor(Color.TRANSPARENT);
		
		
		
		Globals.back = null;
		//this.orgCanvas.drawColor(Color.TRANSPARENT);
		drawFragment.undoActions.clearUndos();
		postInvalidate();
		
	}
	
	public int getBrushType(){
		return currentBrushType;
	}
	
	public String saveToImage(){
		
		saveTransform();
		Bitmap bitmapToSave = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), originalBitmap.getConfig());
		
		
		Canvas canvas = new Canvas(bitmapToSave);
		canvas.drawColor(Color.TRANSPARENT);
		
		canvas.drawBitmap(this.foreground, 0, 0, null);
		this.canvasPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
		canvas.drawBitmap(this.originalBitmap, 0, 0, canvasPaint);
		
		String fileName = String.valueOf(originalBitmap.toString().hashCode())+".png";
		SerializationTools.serializePoints(getContext(), points, fileName);		
		File sd = new File(getContext().getApplicationInfo().dataDir+"/Drawings/");
		sd.mkdirs();
		File dest = new File(sd, fileName);
		
		try { 
		     FileOutputStream out = new FileOutputStream(dest);
		     bitmapToSave.compress(Bitmap.CompressFormat.PNG, 100, out);
		     out.flush();
		     out.close();
		} catch (Exception e) {
		     e.printStackTrace();
		} 
		bitmapToSave.recycle();
		bitmapToSave = null;
		return fileName;
	}
	
	public void undoTo(int location){
		
		this.saveTransform();
		if (activePoint != null)
		{
			if (this.isTransformType(activePoint.brushType))
			{
				activePoint.snippetNumber = currentSnippet;
				currentSnippet++;
				this.points.add(activePoint);
				drawFragment.undoActions.addUndoButtonItem();
			}
		}
		Log.d("LC", "undo to "+location);
		if (Globals.back != null)
		{
			p.setColorFilter(null);
			orgCanvas.drawBitmap(Globals.back,0,0,p);
		}
		else this.originalBitmap.eraseColor(Color.TRANSPARENT);
		for (int i = points.size()-1; i>0; i--){
			if (points.get(i).snippetNumber>location){
				points.remove(i);
			}		
			else
			{
				break;
			}
		}
		this.initData();
		p.setColor(Color.WHITE);
		p.setColorFilter(null);
		this.forgroundColor = Color.WHITE;
		p.setStyle(Style.FILL_AND_STROKE);		
		this.fCanvas.drawRect(new Rect(-1,-1,fCanvas.getWidth(),fCanvas.getHeight()),p);
		
		for (int i = points.size() -1 ;i >= 0;i--)
		{
			if (points.get(i).brushType == R.id.draw_watercolor)
			{
				p.setColor(points.get(i).color);
				this.forgroundColor = points.get(i).color;
				p.setColorFilter(null);
				p.setStyle(Style.FILL_AND_STROKE);		
				this.fCanvas.drawRect(new Rect(-1,-1,fCanvas.getWidth(),fCanvas.getHeight()),p);
				break;
			}
		}
		for (int i = 0 ;i < points.size();i++)
		{
			points.get(i).drawn = false;
			if (points.get(i).snippetNumber <=location)
			{	
				this.activePoint  = points.get(i);
				if (this.isTransformType(this.activePoint.brushType))
				{
					this.hadSelection = true;
					this.cutBitmap = null;
				}
				else this.hadSelection = false;
				
				this.drawingActivePoint();
				this.canvasPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
				this.orgCanvas.drawBitmap(this.drawBitmap,0,0,canvasPaint);
				this.saveTransform();
				drawBitmap.eraseColor(android.graphics.Color.TRANSPARENT);				
			}
			else
				break;
		}
		this.activePoint = new CustomPoint();
		this.initData();
		this.hadSelection = false;
		this.selectBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
		currentSnippet = location+1;
		drawFragment.undoActions.updateUndos(currentSnippet);
		//postInvalidate();
		drawBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
		canvasPaint.setXfermode(null);
		invalidate();
	}
	
	public boolean isShapeType(int type)
	{
		if (type == R.id.shape_circle || type == R.id.shape_rectangle 
				|| type == R.id.shape_cubic || type == R.id.shape_roundrect
				|| type == R.id.shape_triangle)
			 return true;
		else 
			 return false;
		
	}
	public boolean isPenType(int type)
	{
		if (type == R.id.brush_stencil || type == R.id.brush_fat_marker 
				|| type == R.id.brush_paint || type == R.id.brush_pencil
				|| type == R.id.brush_marker)			
			 return true;
		else 
			 return false;
	}
	public boolean isTransformType(int type)
	{
		if (type == R.id.transform_scale || type == R.id.transform_free 
				|| type == R.id.transform_perspective || type == R.id.transform_rotate
				|| type == R.id.transform_transform1)	
			return true;
		else 
			return false;
	}
	public void drawingActivePoint()
	{
		
		if (activePoint != null)
		{
			if (isTransformType(activePoint.brushType))
			{
				drawBitmap.eraseColor(Color.TRANSPARENT);
				if (activePoint.selXX1 > -1 && activePoint.selXX2 > -1 && activePoint.selYY1 > -1 && activePoint.selYY2 > -1)
				{
					//drawSelection Area
					p.setColorFilter(null);
					p.setStyle(Style.STROKE);
					p.setColor(Color.RED);
					p.setStrokeWidth(3);		
					selectBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
					
					p.setStyle(Style.STROKE);
					selCanvas.drawLine(activePoint.selXX1 + activePoint.deltaP1X, activePoint.selYY1 + activePoint.deltaP1Y, activePoint.selXX1 + activePoint.deltaP2X, activePoint.selYY2 + activePoint.deltaP2Y, p);
					selCanvas.drawLine(activePoint.selXX1 + activePoint.deltaP2X, activePoint.selYY2 + activePoint.deltaP2Y, activePoint.selXX2 + activePoint.deltaP4X, activePoint.selYY2 + activePoint.deltaP4Y, p);
					selCanvas.drawLine(activePoint.selXX2 + activePoint.deltaP4X, activePoint.selYY2 + activePoint.deltaP4Y,activePoint. selXX2 + activePoint.deltaP3X, activePoint.selYY1 + activePoint.deltaP3Y, p);
					selCanvas.drawLine(activePoint.selXX2 + activePoint.deltaP3X, activePoint.selYY1 + activePoint.deltaP3Y, activePoint.selXX1 + activePoint.deltaP1X, activePoint.selYY1 + activePoint.deltaP1Y, p);
					
					p.setStyle(Style.FILL_AND_STROKE);
					selCanvas.drawCircle(activePoint.selXX1 + activePoint.deltaP1X,activePoint.selYY1 + activePoint.deltaP1Y , 10, p);
					selCanvas.drawCircle(activePoint.selXX1 + activePoint.deltaP2X,activePoint.selYY2 + activePoint.deltaP2Y, 10, p);
					selCanvas.drawCircle(activePoint.selXX2 + activePoint.deltaP4X, activePoint.selYY2 + activePoint.deltaP4Y, 10, p);
					selCanvas.drawCircle(activePoint.selXX2 + activePoint.deltaP3X,activePoint.selYY1 + activePoint.deltaP3Y, 10, p);
					
					
					/*if (this.currentBrushType == R.id.transform_perspective)
					{
						p.setStyle(Style.STROKE);
						selCanvas.drawLine(activePoint.selXX1 + activePoint.deltaP1X, activePoint.selYY1 + activePoint.deltaP1Y, activePoint.selXX1 + activePoint.deltaP2X, activePoint.selYY2 + activePoint.deltaP2Y, p);
						selCanvas.drawLine(activePoint.selXX1 + activePoint.deltaP2X, activePoint.selYY2 + activePoint.deltaP2Y, activePoint.selXX2 + activePoint.deltaP4X, activePoint.selYY2 + activePoint.deltaP4Y, p);
						selCanvas.drawLine(activePoint.selXX2 + activePoint.deltaP4X, activePoint.selYY2 + activePoint.deltaP4Y,activePoint. selXX2 + activePoint.deltaP3X, activePoint.selYY1 + activePoint.deltaP3Y, p);
						selCanvas.drawLine(activePoint.selXX2 + activePoint.deltaP3X, activePoint.selYY1 + activePoint.deltaP3Y, activePoint.selXX1 + activePoint.deltaP1X, activePoint.selYY1 + activePoint.deltaP1Y, p);
						
						p.setStyle(Style.FILL_AND_STROKE);
						selCanvas.drawCircle(activePoint.selXX1 + activePoint.deltaP1X,activePoint.selYY1 + activePoint.deltaP1Y , 10, p);
						selCanvas.drawCircle(activePoint.selXX1 + activePoint.deltaP2X,activePoint.selYY2 + activePoint.deltaP2Y, 10, p);
						selCanvas.drawCircle(activePoint.selXX2 + activePoint.deltaP4X, activePoint.selYY2 + activePoint.deltaP4Y, 10, p);
						selCanvas.drawCircle(activePoint.selXX2 + activePoint.deltaP3X,activePoint.selYY1 + activePoint.deltaP3Y, 10, p);
					}
					else
					{
						p.setStyle(Style.STROKE);
						selCanvas.drawLine(activePoint.selXX1, activePoint.selYY1, activePoint.selXX1, activePoint.selYY2, p);
						selCanvas.drawLine(activePoint.selXX1, activePoint.selYY2, activePoint.selXX2, activePoint.selYY2, p);
						selCanvas.drawLine(activePoint.selXX2, activePoint.selYY2, activePoint.selXX2, activePoint.selYY1, p);
						selCanvas.drawLine(activePoint.selXX2, activePoint.selYY1, activePoint.selXX1, activePoint.selYY1, p);
						
						p.setStyle(Style.FILL_AND_STROKE);
						selCanvas.drawCircle(activePoint.selXX1,activePoint.selYY1, 10, p);
						selCanvas.drawCircle(activePoint.selXX1,activePoint.selYY2, 10, p);
						selCanvas.drawCircle(activePoint.selXX2, activePoint.selYY1, 10, p);
						selCanvas.drawCircle(activePoint.selXX2,activePoint.selYY2, 10, p);
					}*/
					//selCanvas.drawRect(new RectF(activePoint.selXX1, selYY1, selXX2,selYY2), p);
					//Draw circles
					
					if (this.cutBitmap == null && this.hadSelection
							&& activePoint.selXX2 != activePoint.selXX1 && activePoint.selYY1 != activePoint.selYY2)
					{
						
						 cutBitmap = Bitmap.createBitmap(Math.abs(activePoint.selXX2 - activePoint.selXX1),Math.abs(activePoint.selYY2 - activePoint.selYY1) , Config.ARGB_4444);
						 cutBitmap.eraseColor(Color.TRANSPARENT);
						 cropCanvas = new Canvas(cutBitmap);				
						 p.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
						 cropCanvas.drawBitmap(this.originalBitmap, new Rect(Math.min(activePoint.selXX1, activePoint.selXX2),Math.min(activePoint.selYY1, activePoint.selYY2),Math.max(activePoint.selXX1, activePoint.selXX2),Math.max(activePoint.selYY1, activePoint.selYY2)), new Rect(0,0,cropCanvas.getWidth(),cropCanvas.getHeight()), p);
						 p.setStyle(Style.FILL_AND_STROKE);
						 p.setColor(this.forgroundColor);
						 this.orgCanvas.drawRect(new Rect(activePoint.selXX1,activePoint.selYY1,activePoint.selXX2,activePoint.selYY2), p);
					}
					switch(this.currentBrushType)
					{
					case R.id.transform_free:
						doFreeTransform();				
						break;
					case R.id.transform_scale:
						doScaleTransform();
						
						break;
					case R.id.transform_perspective:
						doPerspective();
						break;
					case R.id.transform_rotate:
						doRotate();
						break;
					case R.id.transform_transform1:
						doTransform1();
						break;
					}
				}	
			}
			else if (isShapeType(activePoint.brushType))
			{
				drawBitmap.eraseColor(Color.TRANSPARENT);
				p.setColor(activePoint.color);
				p.setStrokeWidth(activePoint.size);
				p.setStyle(Style.STROKE);			
				p.setColorFilter(null);
				if (activePoint.line != null && activePoint.line.size() > 1)
				{				
					Point p1 = activePoint.line.get(0);
					Point p2 = activePoint.line.get(1);
					switch(activePoint.brushType)
					{
					case R.id.shape_circle:											
						canvas.drawOval(new RectF(p1.x, p1.y, p2.x, p2.y), p);
						break;
					case R.id.shape_cubic:
						float radius = Math.abs((p1.x - p2.x )) / 2;
						float triangleHeight = (float) (Math.sqrt(3) * radius / 2);
					    float centerX = ((p1.x + p2.x ) / 2);
					    float centerY = ((p1.y + p2.y ) / 2);
					    
					    Path hexagonPath = new Path();
					    hexagonPath.moveTo(centerX, centerY + radius);
					    hexagonPath.lineTo(centerX - triangleHeight, centerY + radius/2);
					    hexagonPath.lineTo(centerX - triangleHeight, centerY - radius/2);
					    hexagonPath.lineTo(centerX, centerY - radius);
					    hexagonPath.lineTo(centerX + triangleHeight, centerY - radius/2);
					    hexagonPath.lineTo(centerX + triangleHeight, centerY + radius/2);
					    hexagonPath.lineTo(centerX, centerY + radius);
					    hexagonPath.close();
					    canvas.drawPath(hexagonPath, p);
						break;
					case R.id.shape_rectangle:
						canvas.drawRect(new RectF(p1.x, p1.y, p2.x, p2.y), p);
						break;
					case R.id.shape_roundrect:
						canvas.drawRoundRect(new RectF(Math.min(p1.x, p2.x),Math.min(p1.y,p2.y),
								Math.max(p1.x, p2.x),Math.max(p1.y,p2.y)),10,10, p);
						break;
					case R.id.shape_triangle:					
					    Point a = new Point((int)p1.x, (int)p2.y);
					    Point b = new Point((int)(p1.x + p2.x ) / 2, (int)p1.y);
					    Point c = new Point((int)p2.x, (int)p2.y);
	
					    Path path = new Path();
					    path.setFillType(FillType.EVEN_ODD);
					    path.moveTo(b.x, b.y);
					    path.lineTo(c.x, c.y);
					    path.lineTo(a.x, a.y);
					    path.close();
	
					    canvas.drawPath(path, p);
						break;
							
					}
				}
			}
			else if (activePoint.brushType == R.id.draw_eraser)
			{
				//drawBitmap.eraseColor(Color.TRANSPARENT);
				drawBitmap.eraseColor(Color.TRANSPARENT);
				p.setColorFilter(null);
				p.setColor(this.forgroundColor);
				p.setStrokeWidth(activePoint.size);
				if (activePoint.line != null && activePoint.line.size() > 0)
				{		
					for (int i = 1;i < activePoint.line.size();i++)
					{
						Point p1 = activePoint.line.get(i - 1);
						Point p2 = activePoint.line.get(i);
						canvas.drawLine(p1.x,p1.y,p2.x,p2.y,p);
					}	
				}
			}
			else if (activePoint.brushType == R.id.draw_watercolor)
			{
				drawBitmap.eraseColor(Color.TRANSPARENT);
				p.setColor(activePoint.color);
				p.setColorFilter(null);
				p.setStyle(Style.FILL_AND_STROKE);
				this.fCanvas.drawRect(new Rect(-1,-1,fCanvas.getWidth(),fCanvas.getHeight()),p);
			}
			else
			{
				//drawBitmap.eraseColor(Color.TRANSPARENT);
				p.setColor(activePoint.color);
				p.setStrokeWidth(activePoint.size);			
				if (activePoint.line != null && activePoint.line.size() > 1)
				{		
					if (activePoint.brushType == R.id.brush_pencil)
					{
						int sz = activePoint.line.size() - 1;
						Point p1 = activePoint.line.get(sz - 1);
						Point p2 = activePoint.line.get(sz);
						
						 Path myPath = new Path();
						    myPath.moveTo(p1.x,p1.y);
						    myPath.quadTo(p1.x, p1.y, p2.x,p2.y);
						    canvas.drawPath(myPath, p);
						    
						//canvas.draw(p1.x,p1.y,p2.x,p2.y,p);
						
						/*for (int i = 1;i < activePoint.line.size();i++)
						{
							Point p1 = activePoint.line.get(i - 1);
							Point p2 = activePoint.line.get(i);
							canvas.drawLine(p1.x,p1.y,p2.x,p2.y,p);
						}
						*/
					}
					else
					{
						int sz = activePoint.line.size() - 1;
						Point p1 = activePoint.line.get(sz - 1);
						Point p2 = activePoint.line.get(sz);
						ArrayList<Point> tempPoints = DrawTools.bresenhamAlgorithm(p2.x,p2.y, p1.x, p1.y, brushDimensions);
						for (int j = 0;j < tempPoints.size();j+=2)
						{	
							Point pp1 = tempPoints.get(j);
							b.setBrush(activePoint.brushType, (int) activePoint.size);
							p.setColorFilter(new PorterDuffColorFilter(currentColor, Mode.MULTIPLY));
							canvas.drawBitmap(b.brushBitmap,pp1.x-activePoint.size/2,pp1.y - activePoint.size/2,p);
						}
						/*for (int i = 1;i < activePoint.line.size();i++)
						{
							Point p1 = activePoint.line.get(i - 1);
							Point p2 = activePoint.line.get(i);
							ArrayList<Point> tempPoints = DrawTools.bresenhamAlgorithm(p2.x,p2.y, p1.x, p1.y, brushDimensions);
							for (int j = 0;j < tempPoints.size();j+=2)
							{	
								Point pp1 = tempPoints.get(j);
								b.setBrush(activePoint.brushType, (int) activePoint.size);
								p.setColorFilter(new PorterDuffColorFilter(currentColor, Mode.MULTIPLY));
								canvas.drawBitmap(b.brushBitmap,pp1.x-activePoint.size/2,pp1.y - activePoint.size/2,p);
							}
						
						}*/
					}
					
					/*if (points.get(points.size()-1).brushType==currentBrushType){
						ArrayList<Point> tempPoints = DrawTools.bresenhamAlgorithm((int)event.getX(), (int)event.getY(), (int)points.get(points.size()-1).x, (int)points.get(points.size()-1).y, brushDimensions);
						Point lastPoint = null;
						double closeness=-1;
						double dis;
						for (Point p:tempPoints){
							if (closeness==-1){
								closeness = Math.abs(distance(p, new Point((int)event.getX(), (int)event.getY())));
								lastPoint =p;
							}else{
								dis = Math.abs(distance(p, new Point((int)event.getX(), (int)event.getY())));
								if (dis<closeness){
									closeness = dis;
									lastPoint = p;
								}
							}
						}
							
						for (Point p:tempPoints){
							if (p!=lastPoint)
								points.add(new CustomPoint(p.x, p.y, false, currentColor, currentSnippet, brushSize, eraserMode, currentBrushType));
						}
						points.add(new CustomPoint(lastPoint.x, lastPoint.y, false, currentColor, currentSnippet, brushSize, eraserMode, currentBrushType));
						
					}*/
					
					
				}
			}
			invalidate();
		}
	}
	public CustomPoint activePoint; 
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			if (this.isTransformType(this.currentBrushType))
			{
				if (!this.hadSelection)
				{
					activePoint = new CustomPoint();
					activePoint.brushType = this.currentBrushType;
					activePoint.selXX1 = (int) event.getX();
					activePoint.selYY1 = (int) event.getY();					
				}
				else
				{
					 focusSquare = containSquare(event.getX(),event.getY());
					 if (focusSquare == 0)
					 {						 	
						 	this.saveTransform();
						 	
						 	
						 	activePoint.snippetNumber = currentSnippet;
							currentSnippet++;
							this.points.add(activePoint);
							drawFragment.undoActions.addUndoButtonItem();
							
						 	if (transformedBitmap != null)	
						 		transformedBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
						 	if (this.cutBitmap != null)
						 	{
						 		this.cutBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
						 	}
							this.hadSelection = false;							
							activePoint = new CustomPoint();
							activePoint.brushType = this.currentBrushType;
							activePoint.selXX1 = (int) event.getX();
							activePoint.selYY1 = (int) event.getY();
					}
					 else
					 {
						    /*int x1 = activePoint.selXX1;
						    int y1 = activePoint.selYY1;
						    int xx1 = activePoint.selXX2;
						    int yy2 = activePoint.selYY2;
						    float dx1 = activePoint.deltaP1X;
						    float dx2 = activePoint.deltaP2X;
						    float dx3 = activePoint.deltaP3X;
						    float dx4 = activePoint.deltaP4X;
						    
						    float dy1 = activePoint.deltaP1Y;
						    float dy2 = activePoint.deltaP2Y;
						    float dy3 = activePoint.deltaP3Y;
						    float dy4 = activePoint.deltaP4Y;
						    
						    int rot = activePoint.rotateAngle;
						    Matrix dmat = null;
						    if (activePoint.matrix != null)
						    	dmat = new Matrix(activePoint.matrix);
						 	activePoint = new CustomPoint();
							activePoint.brushType = this.currentBrushType;
							activePoint.selXX1 = x1;
							activePoint.selYY1 = y1;
							activePoint.selXX2 = xx1;
							activePoint.selYY2 = yy2;
							activePoint.deltaP1X = dx1;
							activePoint.deltaP2X = dx2;
							activePoint.deltaP3X = dx3;
							activePoint.deltaP4X = dx4;
							
							activePoint.deltaP1Y = dy1;
							activePoint.deltaP2Y = dy2;
							activePoint.deltaP3Y = dy3;
							activePoint.deltaP4Y = dy4;
							
							activePoint.matrix = dmat;
							activePoint.rotateAngle = rot;*/
					 }
					 
				 }
			}
			else if (this.isShapeType(currentBrushType))
			{
				//points.add(new CustomPoint(event.getX(), event.getY(), false,currentColor,currentSnippet,brushSize,eraserMode,currentBrushType));
				activePoint = new CustomPoint(currentColor,currentSnippet,brushSize,currentBrushType);
				activePoint.addPoint(new Point((int)event.getX(),(int)event.getY()));
			}
			else if (currentBrushType == R.id.draw_eraser)
			{
				activePoint = new CustomPoint(Color.WHITE,currentSnippet,brushSize,currentBrushType);
				activePoint.addPoint(new Point((int)event.getX(),(int)event.getY()));
			}
			else if (currentBrushType == R.id.draw_watercolor)
			{
				
			}	
			else
			{
				activePoint = new CustomPoint(currentColor,currentSnippet,brushSize,currentBrushType);
				activePoint.addPoint(new Point((int)event.getX(),(int)event.getY()));
			}
		}
		else if (event.getAction() == MotionEvent.ACTION_UP||event.getAction() == MotionEvent.ACTION_CANCEL)
		{
			if (activePoint != null)
			{
				if (this.isTransformType(currentBrushType))
				{
					if (!this.hadSelection && (activePoint != null) && (activePoint.selXX1  > -1 && activePoint.selYY1 > -1))
					{
						 activePoint.selXX2 = (int) event.getX();
						 activePoint.selYY2 = (int) event.getY();
						 this.hadSelection = true;
						 
						 
						 if (activePoint.selXX2 != activePoint.selXX1 && activePoint.selYY1 != activePoint.selYY2)
						 {
							 if (cutBitmap != null)
							 {
								 cutBitmap.recycle();
								 cutBitmap = null;
							 }
							 cutBitmap = Bitmap.createBitmap(Math.abs(activePoint.selXX2 - activePoint.selXX1),Math.abs(activePoint.selYY2 - activePoint.selYY1) , Config.ARGB_4444);
							 cutBitmap.eraseColor(Color.TRANSPARENT);
							 cropCanvas = new Canvas(cutBitmap);				
							 p.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
							 cropCanvas.drawBitmap(this.originalBitmap, new Rect(Math.min(activePoint.selXX1, activePoint.selXX2),Math.min(activePoint.selYY1, activePoint.selYY2),Math.max(activePoint.selXX1, activePoint.selXX2),Math.max(activePoint.selYY1, activePoint.selYY2)), new Rect(0,0,cropCanvas.getWidth(),cropCanvas.getHeight()), p);
							 p.setStyle(Style.FILL_AND_STROKE);
							 p.setColor(this.forgroundColor);
							 this.orgCanvas.drawRect(new Rect(activePoint.selXX1,activePoint.selYY1,activePoint.selXX2,activePoint.selYY2), p);
						 }
						 
					}
					
										
					//saveBitmap();
					drawingActivePoint();
					return true;
					 //doDrawing();
					 //invalidate();
					 //return true;
				}
				else if (this.isShapeType(currentBrushType))
				{
					activePoint.setPoint(new Point((int)event.getX(),(int)event.getY()),1);
					activePoint.isDraw = true;
				}
				else if (currentBrushType == R.id.draw_eraser)
				{
					activePoint.addPoint(new Point((int)event.getX(),(int)event.getY()));
					activePoint.isDraw = true;
				}
				else if (currentBrushType == R.id.draw_watercolor)
				{
					return true;
				}
				else
				{
					activePoint.addPoint(new Point((int)event.getX(),(int)event.getY()));
					activePoint.isDraw = true;
				}				
				activePoint.color = currentColor;
				activePoint.size = this.brushSize;
				this.points.add(activePoint);
				currentSnippet++;
				drawFragment.undoActions.addUndoButtonItem();
				this.activePoint = null;
				saveBitmap();
			}
		}
		else 
		{
			if (activePoint != null)
			{
				if (this.isTransformType(currentBrushType))
				{
					if (!this.hadSelection)
					{
						activePoint.selXX2 = (int) event.getX();
						activePoint.selYY2 = (int) event.getY();
					}
					else
					{
						this.processTransform(event.getX(),event.getY());
					}
				}
				else if (this.isShapeType(currentBrushType))
				{
					activePoint.setPoint(new Point((int)event.getX(),(int)event.getY()),1);				
				}
				else if (currentBrushType == R.id.draw_eraser)
				{
					activePoint.addPoint(new Point((int)event.getX(),(int)event.getY()));
				}
				else if (currentBrushType == R.id.draw_watercolor)
				{
					return true;
				}
				else
				{
					activePoint.addPoint(new Point((int)event.getX(),(int)event.getY()));				
				}
			}
			
		}
		drawingActivePoint();
		return true;
	}
	public void processTransform(float x, float y)
	{
			switch(focusSquare)
			{
			case 1:
				if (this.currentBrushType == R.id.transform_perspective)
				{
					float dx =  x - activePoint.selXX1;
					float dy = y - activePoint.selYY1;
					if (activePoint.selYY1 <= activePoint.selYY2)
					{
						if (activePoint.checkOneLine(activePoint.selXX2 + activePoint.deltaP2X,activePoint.selYY1 + activePoint.deltaP2Y,
								activePoint.selXX1 + activePoint.deltaP4X,activePoint.selYY2 + activePoint.deltaP4Y,
								activePoint.selXX1 + dx,activePoint.selYY1 + dy))
						{
							activePoint.deltaP1X =  x - activePoint.selXX1;
							activePoint.deltaP1Y = y - activePoint.selYY1;
						}
					}
					else
					{
						if (!activePoint.checkOneLine(activePoint.selXX2 + activePoint.deltaP2X,
								activePoint.selYY1 + activePoint.deltaP2Y,activePoint.selXX1 + activePoint.deltaP4X,
								activePoint.selYY2 + activePoint.deltaP4Y,activePoint.selXX1 + dx,activePoint.selYY1 + dy))
						{
							activePoint.deltaP1X =  x - activePoint.selXX1;
							activePoint.deltaP1Y = y - activePoint.selYY1;
						}
					}
					
					
					
					//this.selXX1 = (int) event.getX();
					//this.selYY1 = (int) event.getY();
				}
				else if (this.currentBrushType == R.id.transform_rotate)
				{
					
					centerX = (activePoint.selXX1 + activePoint.selXX2) /2;
					centerY = (activePoint.selYY1 + activePoint.selYY2) / 2;
					
					float xx = (centerX- x);
					float yy = (centerY - y);
					
					//centerX = xx / 2;
					//centerY = yy / 2;
					int radius = Math.min(Math.abs(activePoint.selXX1 - activePoint.selXX2), Math.abs(activePoint.selYY1 - activePoint.selYY2)) / 2;								
					double angle = Math.atan(yy/xx);
					double an = Math.toDegrees(angle);
					if (xx > 0 && yy < 0)
						an = 180 + an;
					else if (xx > 0 && yy > 0)
					{
						an = an - 180;
					}
					activePoint.rotateAngle = (int) an;
							
				}
				else
				{
					activePoint.deltaP1X = x - activePoint.selXX1;
					activePoint.deltaP1Y = y - activePoint.selYY1;
					activePoint.deltaP2X = x - activePoint.selXX1;
					activePoint.deltaP3Y = y - activePoint.selYY1;
					
					
					
					
					//activePoint.selXX1 = (int) x;
					//activePoint.selYY1 = (int) y;
				}
				break;
			case 2:
				if (this.currentBrushType == R.id.transform_perspective)
				{
					
					float dx =  x - activePoint.selXX1;
					float dy = y - activePoint.selYY2;
					if (activePoint.selYY1 <= activePoint.selYY2)
					{
						if (!activePoint.checkOneLine(activePoint.selXX1 + activePoint.deltaP1X,activePoint.selYY1 + activePoint.deltaP1Y,
								activePoint.selXX2 + activePoint.deltaP3X,activePoint.selYY2 +activePoint.deltaP3Y,
								activePoint.selXX1 + dx,activePoint.selYY2 + dy))
						{
							activePoint.deltaP2X =  x - activePoint.selXX1;
							activePoint.deltaP2Y =  y - activePoint.selYY2;
						}
					}
					else
					{
						if (activePoint.checkOneLine(activePoint.selXX1 + activePoint.deltaP1X,activePoint.selYY1 +activePoint.deltaP1Y,
								activePoint.selXX2 + activePoint.deltaP3X,
								activePoint.selYY2 + activePoint.deltaP3Y,
								activePoint.selXX1 + dx,
								activePoint.selYY2 + dy))
						{
							activePoint.deltaP2X =  x - activePoint.selXX1;
							activePoint.deltaP2Y =  y - activePoint.selYY2;
						}
					}
					
					
					//this.selXX1 = (int) event.getX();
					//this.selYY2 = (int) event.getY();
				}
				else if (this.currentBrushType == R.id.transform_rotate)
				{
					
					int centerX = (activePoint.selXX1 + activePoint.selXX2) /2;
					int centerY = (activePoint.selYY1 + activePoint.selYY2) / 2;
					
					float xx = (centerX- x);
					float yy = (centerY - y);
					
					
					int radius = Math.max(Math.abs(activePoint.selXX1 - activePoint.selXX2), Math.abs(activePoint.selYY1 - activePoint.selYY2)) / 2;								
					double angle = Math.atan(yy/xx);
					double an = Math.toDegrees(angle);
					if (xx > 0 && yy < 0)
						an = 180 + an;
					else if (xx > 0 && yy > 0)
					{
						an = an - 180;
					}
					activePoint.rotateAngle = (int) an;
							
				}
				else
				{
					
					activePoint.deltaP1X = x - activePoint.selXX1;
					activePoint.deltaP2Y = y - activePoint.selYY2;
					activePoint.deltaP2X = x - activePoint.selXX1;
					activePoint.deltaP4Y = y - activePoint.selYY2;
					
					
					//activePoint.selXX1 = (int) x;
					//activePoint.selYY2 = (int) y;
				}
				
				break;
			case 3:
				if (this.currentBrushType == R.id.transform_perspective)
				{
					float dx =  x - activePoint.selXX2;
					float dy = y - activePoint.selYY1;
					if (activePoint.selYY1 <= activePoint.selYY2)
					{
						if (activePoint.checkOneLine(activePoint.selXX1 + activePoint.deltaP1X,activePoint.selYY1 + activePoint.deltaP1Y,
								activePoint.selXX2 + activePoint.deltaP4X,activePoint.selYY2 + activePoint.deltaP4Y,
								activePoint.selXX2 + dx,activePoint.selYY1 + dy))
						{
							activePoint.deltaP3X =  x - activePoint.selXX2;
							activePoint.deltaP3Y =  y - activePoint.selYY1;
						}
					}
					else
					{
						if (!activePoint.checkOneLine(activePoint.selXX1 + activePoint.deltaP1X,activePoint.selYY1 + activePoint.deltaP1Y,
								activePoint.selXX2 + activePoint.deltaP4X,activePoint.selYY2 + activePoint.deltaP4Y,
								activePoint.selXX2 + dx,activePoint.selYY1 + dy))
						{
							activePoint.deltaP3X =  x - activePoint.selXX2;
							activePoint.deltaP3Y =  y - activePoint.selYY1;
						}
					}
					
					
					//this.selXX2 = (int) event.getX();
					//this.selYY1 = (int) event.getY();
				}
				else if (this.currentBrushType == R.id.transform_rotate)
				{
					
					int centerX = (activePoint.selXX1 + activePoint.selXX2) /2;
					int centerY = (activePoint.selYY1 + activePoint.selYY2) / 2;
					
					float xx = (centerX- x);
					float yy = (centerY - y);
					
					
					int radius = Math.max(Math.abs(activePoint.selXX1 - activePoint.selXX2), Math.abs(activePoint.selYY1 - activePoint.selYY2)) / 2;								
					double angle = Math.atan(yy/xx);
					double an = Math.toDegrees(angle);
					if (xx > 0 && yy < 0)
						an = 180 + an;
					else if (xx > 0 && yy > 0)
					{
						an = an - 180;
					}
					activePoint.rotateAngle = (int) an;
							
				}
				else
				{
					
					activePoint.deltaP3X = x - activePoint.selXX2;
					activePoint.deltaP3Y = y - activePoint.selYY1;
					activePoint.deltaP4X = x - activePoint.selXX2;
					activePoint.deltaP1Y = y - activePoint.selYY1;
					
					
					//activePoint.selXX2 = (int) x;
					//activePoint.selYY1 = (int) y;
				}							
				break;
			case 4:
				if (this.currentBrushType == R.id.transform_perspective)
				{
					
					float dx =  x - activePoint.selXX2;
					float dy = y - activePoint.selYY2;
					if (activePoint.selYY1 <= activePoint.selYY2)
					{
						if (!activePoint.checkOneLine(activePoint.selXX1 + activePoint.deltaP2X,activePoint.selYY2 + activePoint.deltaP2Y,
								activePoint.selXX2 + activePoint.deltaP3X,activePoint.selYY1 + activePoint.deltaP3Y,
								activePoint.selXX2 + dx,activePoint.selYY2 + dy))
						{
							activePoint.deltaP4X =  x - activePoint.selXX2;
							activePoint.deltaP4Y =  y - activePoint.selYY2 ;
						}
					}
					else
					{
						if (activePoint.checkOneLine(activePoint.selXX1 + activePoint.deltaP2X,activePoint.selYY2 + activePoint.deltaP2Y,
								activePoint.selXX2 + activePoint.deltaP3X,activePoint.selYY1 + activePoint.deltaP3Y,
								activePoint.selXX2 + dx,activePoint.selYY2 + dy))
						{
							activePoint.deltaP4X =  x - activePoint.selXX2;
							activePoint.deltaP4Y =  y - activePoint.selYY2 ;
						}
					}
					
					
					
					
					//this.selXX2 = (int) event.getX();
					//this.selYY2 = (int) event.getY();
				}
				else if (this.currentBrushType == R.id.transform_rotate)
				{
					
					int centerX = (activePoint.selXX1 + activePoint.selXX2) /2;
					int centerY = (activePoint.selYY1 + activePoint.selYY2) / 2;
					
					float xx = (centerX- x);
					float yy = (centerY - y);
					
					
					int radius = Math.max(Math.abs(activePoint.selXX1 - activePoint.selXX2), Math.abs(activePoint.selYY1 - activePoint.selYY2)) / 2;								
					double angle = Math.atan(yy/xx);
					double an = Math.toDegrees(angle);
					if (xx > 0 && yy < 0)
						an = 180 + an;
					else if (xx > 0 && yy > 0)
					{
						an = an - 180;
					}
					activePoint.rotateAngle = (int) an;
							
				}
				else
				{
					
					activePoint.deltaP4X = x - activePoint.selXX2;
					activePoint.deltaP4Y = y - activePoint.selYY2;
					activePoint.deltaP3X = x - activePoint.selXX2;
					activePoint.deltaP2Y = y - activePoint.selYY2;
					
					
					//activePoint.selXX2 = (int) x;
					//activePoint.selYY2 = (int) y;
				}
				break;
			case 5:
				break;
			 default:
				 break;
			}
		
	}
	public void saveBitmap()
	{
		this.canvasPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
		this.orgCanvas.drawBitmap(this.drawBitmap,0,0,canvasPaint);
		drawBitmap.eraseColor(Color.TRANSPARENT);
	}
		
		/*if (event.getAction() == MotionEvent.ACTION_UP||event.getAction() == MotionEvent.ACTION_CANCEL){
			if (colorPicking){
				int color = hideColorPickerPreview();
				colorPicking = false;
				Log.d("draw", "C:"+color);
				if (color!=0)
					currentColor = color;
				drawFragment.colorDropperButton.setBackgroundResource(R.drawable.ring_more_padding);
			}else{
				 //Transform------------------------------------------------------
				 if (currentBrushType == R.id.transform_scale || currentBrushType == R.id.transform_free 
							|| currentBrushType == R.id.transform_perspective || currentBrushType == R.id.transform_rotate
							|| currentBrushType == R.id.transform_transform1)			
				 {
					 if (!this.hadSelection && (this.selXX1  > -1 && this.selYY1 > -1))
					 {
						 this.selXX2 = (int) event.getX();
						 this.selYY2 = (int) event.getY();
						 this.hadSelection = true;
						 
						 
						 if (selXX2 != selXX1 && selYY1 != selYY2)
						 {
							 if (cutBitmap != null)
							 {
								 cutBitmap.recycle();
								 cutBitmap = null;
							 }
							 cutBitmap = Bitmap.createBitmap(Math.abs(selXX2 - selXX1),Math.abs(selYY2 - selYY1) , Config.ARGB_4444);
							 cutBitmap.eraseColor(Color.BLACK);
							 cropCanvas = new Canvas(cutBitmap);				
							 p.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
							 cropCanvas.drawBitmap(this.originalBitmap, new Rect(Math.min(activePoint.selXX1, selXX2),Math.min(activePoint.selYY1, selYY2),Math.max(activePoint.selXX1, selXX2),Math.max(activePoint.selYY1, selYY2)), new Rect(0,0,cropCanvas.getWidth(),cropCanvas.getHeight()), p);
							 p.setStyle(Style.FILL_AND_STROKE);
							 p.setColor(Color.WHITE);
							 this.orgCanvas.drawRect(new Rect(activePoint.selXX1,selYY1,selXX2,selYY2), p);
						 }
						 
					 }
					 else if (this.hadSelection)
					 {
						 
					 }
					 
					 doDrawing();
					 invalidate();
					 return true;
				 }
				 //Shape----------------------------------------------------------
				 else if (currentBrushType == R.id.shape_circle || currentBrushType == R.id.shape_rectangle 
						|| currentBrushType == R.id.shape_cubic || currentBrushType == R.id.shape_roundrect
						|| currentBrushType == R.id.shape_triangle)
				{
					if (points.size() > 1 && !points.get(points.size() - 2).cut && points.get(points.size() - 2).brushType == currentBrushType)
					{
						points.set(points.size() - 1, new CustomPoint(event.getX(), event.getY(), true,currentColor,currentSnippet,brushSize,eraserMode,currentBrushType));
					}
					else 
						points.add(new CustomPoint(event.getX(), event.getY(), true,currentColor,currentSnippet,brushSize,eraserMode,currentBrushType));
				}
				else
				{
					points.add(new CustomPoint(event.getX(), event.getY(), true,currentColor,currentSnippet,brushSize,eraserMode,currentBrushType));
					
				}
				currentSnippet ++;
				drawFragment.undoActions.addUndoButtonItem();
			
			}
			doDrawing();
			invalidate();
			this.canvasPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
			this.orgCanvas.drawBitmap(this.drawBitmap,0,0,canvasPaint);
			drawBitmap.eraseColor(Color.TRANSPARENT);
			return true;
			
			
		}else if (event.getAction() == MotionEvent.ACTION_DOWN){
			if (colorPicking){
				showColorPickerPreview((int)event.getX(), (int)event.getY());
			}
			
			//Transform--------------------------------------------------------------------
			else if (currentBrushType == R.id.transform_scale || currentBrushType == R.id.transform_free 
					|| currentBrushType == R.id.transform_perspective || currentBrushType == R.id.transform_rotate
					|| currentBrushType == R.id.transform_transform1)					
			{
				if (!this.hadSelection)
				{
					this.selXX1 = (int) event.getX();
					this.selYY1 = (int) event.getY();
				}
				else
				{
					 focusSquare = containSquare(event.getX(),event.getY());
					 if (focusSquare == 0)
					 {						 	
						 	this.saveTransform();
						 	if (transformedBitmap != null)	
						 		transformedBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
						 	if (this.cutBitmap != null)
						 	{
						 		this.cutBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
						 	}
							this.hadSelection = false;
							this.initData();
							this.selXX1 = (int) event.getX();
							this.selYY1 = (int) event.getY();
					}
				 }
			}
			else
			{				
				points.add(new CustomPoint(event.getX(), event.getY(), false,currentColor,currentSnippet,brushSize,eraserMode,currentBrushType));
			}
			
		}
		//Mouse Move-----------------
		else{
			if (colorPicking){
				showColorPickerPreview((int)event.getX(), (int)event.getY());
			}else{
				if (currentBrushType == R.id.brush_pencil)
					points.add(new CustomPoint(event.getX(), event.getY(), false,currentColor,currentSnippet,brushSize,eraserMode,currentBrushType));
				
				//Transform------------------------------------------------------------------------------------
				else if (currentBrushType == R.id.transform_scale || currentBrushType == R.id.transform_free 
						|| currentBrushType == R.id.transform_perspective || currentBrushType == R.id.transform_rotate
						|| currentBrushType == R.id.transform_transform1)					
				{
					if (!this.hadSelection)
					{					
						this.selXX2 = (int) event.getX();
						this.selYY2 = (int) event.getY();
					}
					else
					{
						switch(focusSquare)
						{
						case 1:
							if (this.currentBrushType == R.id.transform_perspective)
							{
								float dx =  event.getX() - this.selXX1;
								float dy = event.getY() - this.selYY1;
								if (this.selYY1 <= this.selYY2)
								{
									if (checkOneLine(this.selXX2 +activePoint. deltaP2X,selYY1 + deltaP2Y,selXX1 +activePoint. deltaP4X,selYY2 + deltaP4Y,selXX1 + dx,selYY1 + dy))
									{
										this.deltaP1X =  event.getX() - this.selXX1;
										this.deltaP1Y = event.getY() - this.selYY1;
									}
								}
								else
								{
									if (!checkOneLine(this.selXX2 +activePoint. deltaP2X,selYY1 + deltaP2Y,selXX1 +activePoint. deltaP4X,selYY2 + deltaP4Y,selXX1 + dx,selYY1 + dy))
									{
										this.deltaP1X =  event.getX() - this.selXX1;
										this.deltaP1Y = event.getY() - this.selYY1;
									}
								}
								
								
								
								//this.selXX1 = (int) event.getX();
								//this.selYY1 = (int) event.getY();
							}
							else if (this.currentBrushType == R.id.transform_rotate)
							{
								
								centerX = (activePoint.selXX1 + selXX2) /2;
								centerY = (activePoint.selYY1 + selYY2) / 2;
								
								float xx = (centerX- event.getX());
								float yy = (centerY - event.getY());
								
								//centerX = xx / 2;
								//centerY = yy / 2;
								int radius = Math.min(Math.abs(activePoint.selXX1 - selXX2), Math.abs(activePoint.selYY1 - selYY2)) / 2;								
								double angle = Math.atan(yy/xx);
								double an = Math.toDegrees(angle);
								if (xx > 0 && yy < 0)
									an = 180 + an;
								else if (xx > 0 && yy > 0)
								{
									an = an - 180;
								}
								rotateAngle = (int) an;
										
							}
							else
							{
								this.selXX1 = (int) event.getX();
								this.selYY1 = (int) event.getY();
							}
							break;
						case 2:
							if (this.currentBrushType == R.id.transform_perspective)
							{
								
								float dx =  event.getX() - this.selXX1;
								float dy = event.getY() - this.selYY2;
								if (this.selYY1 <= this.selYY2)
								{
									if (!checkOneLine(this.selXX1 +activePoint. deltaP1X,selYY1 + deltaP1Y,selXX2 +activePoint. deltaP3X,selYY2 + deltaP3Y,selXX1 + dx,selYY2 + dy))
									{
										this.deltaP2X =  event.getX() - this.selXX1;
										this.deltaP2Y =  event.getY() - this.selYY2;
									}
								}
								else
								{
									if (checkOneLine(this.selXX1 +activePoint. deltaP1X,selYY1 + deltaP1Y,selXX2 +activePoint. deltaP3X,selYY2 + deltaP3Y,selXX1 + dx,selYY2 + dy))
									{
										this.deltaP2X =  event.getX() - this.selXX1;
										this.deltaP2Y =  event.getY() - this.selYY2;
									}
								}
								
								
								//this.selXX1 = (int) event.getX();
								//this.selYY2 = (int) event.getY();
							}
							else if (this.currentBrushType == R.id.transform_rotate)
							{
								
								int centerX = (activePoint.selXX1 + selXX2) /2;
								int centerY = (activePoint.selYY1 + selYY2) / 2;
								
								float xx = (centerX- event.getX());
								float yy = (centerY - event.getY());
								
								
								int radius = Math.max(Math.abs(activePoint.selXX1 - selXX2), Math.abs(activePoint.selYY1 - selYY2)) / 2;								
								double angle = Math.atan(yy/xx);
								double an = Math.toDegrees(angle);
								if (xx > 0 && yy < 0)
									an = 180 + an;
								else if (xx > 0 && yy > 0)
								{
									an = an - 180;
								}
								rotateAngle = (int) an;
										
							}
							else
							{
								this.selXX1 = (int) event.getX();
								this.selYY2 = (int) event.getY();
							}
							
							break;
						case 3:
							if (this.currentBrushType == R.id.transform_perspective)
							{
								float dx =  event.getX() - this.selXX2;
								float dy = event.getY() - this.selYY1;
								if (this.selYY1 <= this.selYY2)
								{
									if (checkOneLine(this.selXX1 +activePoint. deltaP1X,selYY1 + deltaP1Y,selXX2 +activePoint. deltaP4X,selYY2 + deltaP4Y,selXX2 + dx,selYY1 + dy))
									{
										this.deltaP3X =  event.getX() - this.selXX2;
										this.deltaP3Y =  event.getY() - this.selYY1;
									}
								}
								else
								{
									if (!checkOneLine(this.selXX1 +activePoint. deltaP1X,selYY1 + deltaP1Y,selXX2 +activePoint. deltaP4X,selYY2 + deltaP4Y,selXX2 + dx,selYY1 + dy))
									{
										this.deltaP3X =  event.getX() - this.selXX2;
										this.deltaP3Y =  event.getY() - this.selYY1;
									}
								}
								
								
								//this.selXX2 = (int) event.getX();
								//this.selYY1 = (int) event.getY();
							}
							else if (this.currentBrushType == R.id.transform_rotate)
							{
								
								int centerX = (activePoint.selXX1 + selXX2) /2;
								int centerY = (activePoint.selYY1 + selYY2) / 2;
								
								float xx = (centerX- event.getX());
								float yy = (centerY - event.getY());
								
								
								int radius = Math.max(Math.abs(activePoint.selXX1 - selXX2), Math.abs(activePoint.selYY1 - selYY2)) / 2;								
								double angle = Math.atan(yy/xx);
								double an = Math.toDegrees(angle);
								if (xx > 0 && yy < 0)
									an = 180 + an;
								else if (xx > 0 && yy > 0)
								{
									an = an - 180;
								}
								rotateAngle = (int) an;
										
							}
							else
							{
								this.selXX2 = (int) event.getX();
								this.selYY1 = (int) event.getY();
							}							
							break;
						case 4:
							if (this.currentBrushType == R.id.transform_perspective)
							{
								
								float dx =  event.getX() - this.selXX2;
								float dy = event.getY() - this.selYY2;
								if (this.selYY1 <= this.selYY2)
								{
									if (!checkOneLine(this.selXX1 +activePoint. deltaP2X,selYY2 + deltaP2Y,selXX2 +activePoint. deltaP3X,selYY1 + deltaP3Y,selXX2 + dx,selYY2 + dy))
									{
										this.deltaP4X =  event.getX() - this.selXX2;
										this.deltaP4Y =  event.getY() - this.selYY2 ;
									}
								}
								else
								{
									if (checkOneLine(this.selXX1 +activePoint. deltaP2X,selYY2 + deltaP2Y,selXX2 +activePoint. deltaP3X,selYY1 + deltaP3Y,selXX2 + dx,selYY2 + dy))
									{
										this.deltaP4X =  event.getX() - this.selXX2;
										this.deltaP4Y =  event.getY() - this.selYY2 ;
									}
								}
								
								
								
								
								//this.selXX2 = (int) event.getX();
								//this.selYY2 = (int) event.getY();
							}
							else if (this.currentBrushType == R.id.transform_rotate)
							{
								
								int centerX = (activePoint.selXX1 + selXX2) /2;
								int centerY = (activePoint.selYY1 + selYY2) / 2;
								
								float xx = (centerX- event.getX());
								float yy = (centerY - event.getY());
								
								
								int radius = Math.max(Math.abs(activePoint.selXX1 - selXX2), Math.abs(activePoint.selYY1 - selYY2)) / 2;								
								double angle = Math.atan(yy/xx);
								double an = Math.toDegrees(angle);
								if (xx > 0 && yy < 0)
									an = 180 + an;
								else if (xx > 0 && yy > 0)
								{
									an = an - 180;
								}
								rotateAngle = (int) an;
										
							}
							else
							{
								this.selXX2 = (int) event.getX();
								this.selYY2 = (int) event.getY();
							}
							break;
						case 5:
							break;
						 default:
							 break;
						}
					}
				}
				
				//Shape--------------------------------------------------------------------------
				else if (currentBrushType == R.id.shape_circle || currentBrushType == R.id.shape_rectangle 
						|| currentBrushType == R.id.shape_cubic || currentBrushType == R.id.shape_roundrect
						|| currentBrushType == R.id.shape_triangle)
				{
					if (points.size() > 1 && !points.get(points.size() - 2).cut && points.get(points.size() - 2).brushType == currentBrushType)
					{	
						points.set(points.size() - 1, new CustomPoint(event.getX(), event.getY(), false,currentColor,currentSnippet,brushSize,eraserMode,currentBrushType));
					}
					else 
						points.add(new CustomPoint(event.getX(), event.getY(), false,currentColor,currentSnippet,brushSize,eraserMode,currentBrushType));	
				}
				else{
					if (points.get(points.size()-1).brushType==currentBrushType){
						ArrayList<Point> tempPoints = DrawTools.bresenhamAlgorithm((int)event.getX(), (int)event.getY(), (int)points.get(points.size()-1).x, (int)points.get(points.size()-1).y, brushDimensions);
						Point lastPoint = null;
						double closeness=-1;
						double dis;
						for (Point p:tempPoints){
							if (closeness==-1){
								closeness = Math.abs(distance(p, new Point((int)event.getX(), (int)event.getY())));
								lastPoint =p;
							}else{
								dis = Math.abs(distance(p, new Point((int)event.getX(), (int)event.getY())));
								if (dis<closeness){
									closeness = dis;
									lastPoint = p;
								}
							}
						}
							
						for (Point p:tempPoints){
							if (p!=lastPoint)
								points.add(new CustomPoint(p.x, p.y, false, currentColor, currentSnippet, brushSize, eraserMode, currentBrushType));
						}
						points.add(new CustomPoint(lastPoint.x, lastPoint.y, false, currentColor, currentSnippet, brushSize, eraserMode, currentBrushType));
						
					}
				}
			}
		}
		doDrawing();
		invalidate();
		
		return true;
	}
	*/	
	static class colorPickerPreview{
		static boolean showPreview;
		static int x , y;
		static int color;
		
		public static void draw(Canvas c, Paint p, Context ctxt){
			//p.setStyle(Style.FILL);
			p.setColor(color);
			p.setStrokeWidth(Tools.numtodp(ctxt,10));
			c.drawCircle(x, y, Tools.numtodp(ctxt,55), p);
		}
		
	}
	
	public void showColorPickerPreview(int x , int y){
		int c = drawBitmap.getPixel(x, y);
		colorPickerPreview.x = x;
		colorPickerPreview.y = y;
		colorPickerPreview.color = c;
		colorPickerPreview.showPreview = true;
		invalidate();
	}
	
	public int hideColorPickerPreview(){
		colorPickerPreview.showPreview = false;
		invalidate();
		return colorPickerPreview.color;
	}
	
	Xfermode clearXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
	Brush b = new Brush();
	
	
	
	public void drawItem(int index)
	{
		if (points.size()>index+1){
			if (!points.get(index).cut){
				if (!points.get(index+1).drawn){
					points.get(index+1).drawn = true;
					
					p.setColor(points.get(index).color);
					
					if (points.get(index).eraser)
					{
						p.setXfermode(clearXfermode);
					}
					else
					{
						p.setXfermode(null);
					
						if (points.get(index).brushType== R.id.brush_pencil && points.get(index+1).brushType== R.id.brush_pencil){
							p.setStrokeWidth(points.get(index).size);
							canvas.drawLine(points.get(index).x, points.get(index).y, points.get(index+1).x, points.get(index+1).y, p);
						}
						else if (points.get(index).brushType == R.id.shape_circle && points.get(index + 1).brushType == R.id.shape_circle)
						{		
							p.setStyle(Style.STROKE);
							p.setStrokeWidth(points.get(index).size);
							drawBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
							p.setStrokeWidth(points.get(index).size);						
							canvas.drawOval(new RectF(points.get(index).x, points.get(index).y, points.get(index+1).x, points.get(index+1).y), p);
						
						}
						else if (points.get(index).brushType == R.id.shape_rectangle && points.get(index + 1).brushType == R.id.shape_rectangle)
						{
							p.setStyle(Style.STROKE);
							p.setStrokeWidth(points.get(index).size);
							drawBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
							p.setStrokeWidth(points.get(index).size);						
							canvas.drawRect(new RectF(points.get(index).x, points.get(index).y, points.get(index+1).x, points.get(index+1).y), p);						
						}
						else if (points.get(index).brushType == R.id.shape_roundrect && points.get(index + 1).brushType == R.id.shape_roundrect)
						{
							p.setStyle(Style.STROKE);
							p.setStrokeWidth(points.get(index).size);
							drawBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
							p.setStrokeWidth(points.get(index).size);
							
							canvas.drawRoundRect(new RectF(Math.min(points.get(index).x, points.get(index+1).x),Math.min(points.get(index).y,points.get(index+1).y),Math.max(points.get(index).x, points.get(index+1).x),Math.max(points.get(index).y,points.get(index+1).y)),10,10, p);
						}
						else if (points.get(index).brushType == R.id.shape_cubic && points.get(index + 1).brushType == R.id.shape_cubic)
						{
							p.setStyle(Style.STROKE);
							p.setStrokeWidth(points.get(index).size);					    
							drawBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
							
							
							float radius = Math.abs((points.get(index).x - points.get(index + 1).x )) / 2;
							float triangleHeight = (float) (Math.sqrt(3) * radius / 2);
						    float centerX = ((points.get(index).x + points.get(index + 1).x ) / 2);
						    float centerY = ((points.get(index).y + points.get(index + 1).y ) / 2);
						    
						    Path hexagonPath = new Path();
						    hexagonPath.moveTo(centerX, centerY + radius);
						    hexagonPath.lineTo(centerX - triangleHeight, centerY + radius/2);
						    hexagonPath.lineTo(centerX - triangleHeight, centerY - radius/2);
						    hexagonPath.lineTo(centerX, centerY - radius);
						    hexagonPath.lineTo(centerX + triangleHeight, centerY - radius/2);
						    hexagonPath.lineTo(centerX + triangleHeight, centerY + radius/2);
						    hexagonPath.lineTo(centerX, centerY + radius);
						    hexagonPath.close();
						    canvas.drawPath(hexagonPath, p);
						}
						else if (points.get(index).brushType == R.id.shape_triangle && points.get(index + 1).brushType == R.id.shape_triangle)
						{
							p.setStyle(Style.STROKE);
							p.setStrokeWidth(points.get(index).size);					    
							drawBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
						    Point a = new Point((int)points.get(index).x, (int)points.get(index + 1).y);
						    Point b = new Point((int)((points.get(index).x + points.get(index + 1).x ) / 2), (int)points.get(index).y);
						    Point c = new Point((int)points.get(index + 1).x, (int)points.get(index+ 1).y);
	
						    Path path = new Path();
						    path.setFillType(FillType.EVEN_ODD);
						    path.moveTo(b.x, b.y);
						    path.lineTo(c.x, c.y);
						    path.lineTo(a.x, a.y);
						    path.close();
	
						    canvas.drawPath(path, p);
						}
						else if(points.get(index).eraser)
						{
							
						}
						else{
							b.setBrush(points.get(index+1).brushType, (int)points.get(index+1).size);
							p.setColorFilter(new PorterDuffColorFilter(currentColor, Mode.MULTIPLY));
							//p.setColor(currentColor);
							//p.setXfermode(new PorterDuffXfermode(Mode.MULTIPLY));
							canvas.drawBitmap(b.brushBitmap,points.get(index+1).x-points.get(index+1).size/2,points.get(index+1).y-points.get(index+1).size/2,p);
						}
					}
				}
			}
				
			}
	}
	/*
	public void doDrawing(){
		for (int i = 0; i< points.size(); i++){
			if (points.size()>i+1){
			if (!points.get(i).cut){
				if (!points.get(i+1).drawn){
					points.get(i+1).drawn = true;
					
					p.setColor(points.get(i).color);
					
					if (points.get(i).eraser)
					{
						p.setXfermode(clearXfermode);
					}
					else					
						p.setXfermode(null);
					
						if (points.get(i).brushType== R.id.brush_pencil && points.get(i+1).brushType== R.id.brush_pencil){
							p.setStrokeWidth(points.get(i).size);
							canvas.drawLine(points.get(i).x, points.get(i).y, points.get(i+1).x, points.get(i+1).y, p);
						}
						else if (points.get(i).brushType == R.id.shape_circle && points.get(i + 1).brushType == R.id.shape_circle)
						{		
							p.setStyle(Style.STROKE);
							p.setStrokeWidth(points.get(i).size);
							drawBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
							p.setStrokeWidth(points.get(i).size);						
							canvas.drawOval(new RectF(points.get(i).x, points.get(i).y, points.get(i+1).x, points.get(i+1).y), p);
							//canvas.draw(points.get(i).x + (points.get(i+1).x - points.get(i).x) / 2.0f, (points.get(i).y + points.get(i+1) .y) / 2, Math.abs(points.get(i).x - point), p);
						}
						else if (points.get(i).brushType == R.id.shape_rectangle && points.get(i + 1).brushType == R.id.shape_rectangle)
						{
							p.setStyle(Style.STROKE);
							p.setStrokeWidth(points.get(i).size);
							drawBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
							p.setStrokeWidth(points.get(i).size);						
							canvas.drawRect(new RectF(points.get(i).x, points.get(i).y, points.get(i+1).x, points.get(i+1).y), p);						
						}
						else if (points.get(i).brushType == R.id.shape_roundrect && points.get(i + 1).brushType == R.id.shape_roundrect)
						{
							p.setStyle(Style.STROKE);
							p.setStrokeWidth(points.get(i).size);
							drawBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
							p.setStrokeWidth(points.get(i).size);
							
							canvas.drawRoundRect(new RectF(Math.min(points.get(i).x, points.get(i+1).x),Math.min(points.get(i).y,points.get(i+1).y),Math.max(points.get(i).x, points.get(i+1).x),Math.max(points.get(i).y,points.get(i+1).y)),10,10, p);
						}
						else if (points.get(i).brushType == R.id.shape_cubic && points.get(i + 1).brushType == R.id.shape_cubic)
						{
							p.setStyle(Style.STROKE);
							p.setStrokeWidth(points.get(i).size);					    
							drawBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
							
							
							float radius = Math.abs((points.get(i).x - points.get(i + 1).x )) / 2;
							float triangleHeight = (float) (Math.sqrt(3) * radius / 2);
						    float centerX = ((points.get(i).x + points.get(i + 1).x ) / 2);
						    float centerY = ((points.get(i).y + points.get(i + 1).y ) / 2);
						    
						    Path hexagonPath = new Path();
						    hexagonPath.moveTo(centerX, centerY + radius);
						    hexagonPath.lineTo(centerX - triangleHeight, centerY + radius/2);
						    hexagonPath.lineTo(centerX - triangleHeight, centerY - radius/2);
						    hexagonPath.lineTo(centerX, centerY - radius);
						    hexagonPath.lineTo(centerX + triangleHeight, centerY - radius/2);
						    hexagonPath.lineTo(centerX + triangleHeight, centerY + radius/2);
						    hexagonPath.lineTo(centerX, centerY + radius);
						    hexagonPath.close();
						    canvas.drawPath(hexagonPath, p);
						}
						else if (points.get(i).brushType == R.id.shape_triangle && points.get(i + 1).brushType == R.id.shape_triangle)
						{
							p.setStyle(Style.STROKE);
							p.setStrokeWidth(points.get(i).size);					    
							drawBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
						    Point a = new Point((int)points.get(i).x, (int)points.get(i + 1).y);
						    Point b = new Point((int)((points.get(i).x + points.get(i + 1).x ) / 2), (int)points.get(i).y);
						    Point c = new Point((int)points.get(i + 1).x, (int)points.get(i + 1).y);
	
						    Path path = new Path();
						    path.setFillType(FillType.EVEN_ODD);
						    path.moveTo(b.x, b.y);
						    path.lineTo(c.x, c.y);
						    path.lineTo(a.x, a.y);
						    path.close();
	
						    canvas.drawPath(path, p);
						}
						else if(points.get(i).eraser)
						{
							
						}
						else{
							b.setBrush(points.get(i+1).brushType, (int)points.get(i+1).size);
							p.setColorFilter(new PorterDuffColorFilter(currentColor, Mode.MULTIPLY));
							//p.setColor(currentColor);
							//p.setXfermode(new PorterDuffXfermode(Mode.MULTIPLY));
							canvas.drawBitmap(b.brushBitmap,points.get(i+1).x-points.get(i+1).size/2,points.get(i+1).y-points.get(i+1).size/2,p);
						}
					}
				}
			
				
			}
		}
		
		 
		 
		if (activePoint.selXX1 > -1 && selXX2 > -1 && selYY1 > -1 && selYY2 > -1)
		{
			//drawSelection Area
			p.setColorFilter(null);
			p.setStyle(Style.STROKE);
			p.setColor(Color.RED);
			p.setStrokeWidth(3);		
			selectBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
			if (this.currentBrushType == R.id.transform_perspective)
			{
				p.setStyle(Style.STROKE);
				selCanvas.drawLine(activePoint.selXX1 +activePoint. deltaP1X, selYY1 + deltaP1Y, selXX1 +activePoint. deltaP2X, selYY2 + deltaP2Y, p);
				selCanvas.drawLine(activePoint.selXX1 +activePoint. deltaP2X, selYY2 + deltaP2Y, selXX2 +activePoint. deltaP4X, selYY2 + deltaP4Y, p);
				selCanvas.drawLine(selXX2 +activePoint. deltaP4X, selYY2 + deltaP4Y, selXX2 +activePoint. deltaP3X, selYY1 + deltaP3Y, p);
				selCanvas.drawLine(selXX2 +activePoint. deltaP3X, selYY1 + deltaP3Y, selXX1 +activePoint. deltaP1X, selYY1 + deltaP1Y, p);
				
				p.setStyle(Style.FILL_AND_STROKE);
				selCanvas.drawCircle(activePoint.selXX1 +activePoint. deltaP1X,selYY1 + deltaP1Y , 10, p);
				selCanvas.drawCircle(activePoint.selXX1 +activePoint. deltaP2X,selYY2 + deltaP2Y, 10, p);
				selCanvas.drawCircle(selXX2 +activePoint. deltaP4X, selYY2 + deltaP4Y, 10, p);
				selCanvas.drawCircle(selXX2 +activePoint. deltaP3X,selYY1 + deltaP3Y, 10, p);
			}
			else
			{
				p.setStyle(Style.STROKE);
				selCanvas.drawLine(activePoint.selXX1, selYY1, selXX1, selYY2, p);
				selCanvas.drawLine(activePoint.selXX1, selYY2, selXX2, selYY2, p);
				selCanvas.drawLine(selXX2, selYY2, selXX2, selYY1, p);
				selCanvas.drawLine(selXX2, selYY1, selXX1, selYY1, p);
				
				p.setStyle(Style.FILL_AND_STROKE);
				selCanvas.drawCircle(activePoint.selXX1,selYY1, 10, p);
				selCanvas.drawCircle(activePoint.selXX1,selYY2, 10, p);
				selCanvas.drawCircle(selXX2, selYY1, 10, p);
				selCanvas.drawCircle(selXX2,selYY2, 10, p);
			}
			//selCanvas.drawRect(new RectF(activePoint.selXX1, selYY1, selXX2,selYY2), p);
			//Draw circles
			
			
			switch(this.currentBrushType)
			{
			case R.id.transform_free:
				doFreeTransform();				
				break;
			case R.id.transform_scale:
				doScaleTransform();
				
				break;
			case R.id.transform_perspective:
				doPerspective();
				break;
			case R.id.transform_rotate:
				doRotate();
				break;
			case R.id.transform_transform1:
				doTransform1();
				break;
			}
		}	
		
			
	}
	*/
	@Override
	public void draw(Canvas can) {
		if (foreground==null || (foreground.getWidth()!=can.getWidth() || foreground.getHeight()!=can.getHeight()) ){
			foreground = Bitmap.createBitmap(can.getWidth(), can.getHeight(), Config.ARGB_4444);
			fCanvas = new Canvas(foreground);			
			foreground.eraseColor(this.forgroundColor);			
			falsifyPointsDrawn();
			//doDrawing();
			invalidate();
		}
		if (originalBitmap==null || (originalBitmap.getWidth()!=can.getWidth() || originalBitmap.getHeight()!=can.getHeight()) ){
			originalBitmap = Bitmap.createBitmap(can.getWidth(), can.getHeight(), Config.ARGB_4444);
			orgCanvas = new Canvas(originalBitmap);			
			originalBitmap.eraseColor(Color.TRANSPARENT);
			if (Globals.back != null)
			{
				orgCanvas.drawBitmap(Globals.back, 0,0,canvasPaint);
				//Globals.back = null;
			}
			falsifyPointsDrawn();
			//doDrawing();
			invalidate();
		}
		if (selectBitmap==null || (selectBitmap.getWidth()!=can.getWidth() || selectBitmap.getHeight()!=can.getHeight()) ){
			selectBitmap = Bitmap.createBitmap(can.getWidth(), can.getHeight(), Config.ARGB_4444);
			selCanvas = new Canvas(selectBitmap);
			falsifyPointsDrawn();
			//doDrawing();
			invalidate();
		}
		if (drawBitmap==null || (drawBitmap.getWidth()!=can.getWidth() || drawBitmap.getHeight()!=can.getHeight()) ){
			drawBitmap = Bitmap.createBitmap(can.getWidth(), can.getHeight(), Config.ARGB_4444);
			canvas = new Canvas(drawBitmap);
			falsifyPointsDrawn();
			//doDrawing();
			invalidate();
		}
		
		
		
		
		canvasPaint.setColor(this.forgroundColor);
		canvasPaint.setXfermode(new PorterDuffXfermode(Mode.SRC));
		can.drawBitmap(this.foreground, 0,0,canvasPaint);
		
		//canvasPaint.setColor(Color.WHITE);		
		canvasPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
		can.drawBitmap(originalBitmap, 0, 0,canvasPaint);
		
		
		canvasPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
		can.drawBitmap(drawBitmap, 0, 0, canvasPaint);
		
		if (this.transformedBitmap != null)
		{
			canvasPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
			
			if (activePoint.matrix != null)
				can.drawBitmap(transformedBitmap,activePoint.matrix,canvasPaint);
			else	
			{
				can.drawBitmap(transformedBitmap, 
						Math.min(activePoint.selXX1 + activePoint.deltaP1X, activePoint.selXX2 + activePoint.deltaP4X),
						Math.min(activePoint.selYY1 + activePoint.deltaP1Y, activePoint.selYY2 + activePoint.deltaP4Y),
						canvasPaint);
			}
			//can.drawBitmap(transformedBitmap, Math.min(activePoint.selXX1, selXX2), Math.min(activePoint.selYY1, selYY2), canvasPaint);
				
		}
		
		canvasPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
		can.drawBitmap(selectBitmap, 0, 0, canvasPaint);
		
		
		
		if (colorPickerPreview.showPreview){
			colorPickerPreview.draw(can, canvasPaint,getContext());
		}
		
	}
	
	
	public void falsifyPointsDrawn(){
		for (CustomPoint p : points)
			p.drawn = false;
	}
	
	
	public static double distance(Point a, Point b)
	{ 
	  double dx = a.x - b.x;
	  double dy = a.y - b.y;
	  return Math.sqrt(dx * dx + dy * dy);
	}
	
	public void doFreeTransform()
	{
		if (cutBitmap != null)
		{
			if ((int)(activePoint.selXX1 + activePoint.deltaP1X )!= (int)(activePoint.selXX2 + activePoint.deltaP4X) && 
					(int)(activePoint.selYY1 + activePoint.deltaP1Y) != (int)(activePoint.selYY2 + activePoint.deltaP4Y))
			{				
				Bitmap result = Bitmap.createScaledBitmap(this.cutBitmap,
						(int) Math.abs(activePoint.selXX1 + activePoint.deltaP1X- (activePoint.selXX2 + activePoint.deltaP4X)),
						(int) Math.abs(activePoint.selYY1 + activePoint.deltaP1Y - (activePoint.selYY2 + activePoint.deltaP4Y)),
						true);
				transformedBitmap = result;	
			}
		}
		else
		{
			
		}
	}
	public void initData()
	{
		this.hadSelection = false;
		if (activePoint != null)
		{
			activePoint.deltaP1X = 0;
			activePoint.deltaP1Y = 0;
			activePoint.deltaP2X = 0;
			activePoint.deltaP2Y = 0;
			activePoint.deltaP3X = 0;
			activePoint.deltaP3Y = 0;
			activePoint.deltaP4X = 0;
			activePoint.deltaP4Y = 0;
			
			activePoint.selXX1 = -1;
			activePoint.selXX2 = -1;
			activePoint.selYY1 = -1;
			activePoint.selYY2 = -1;
			activePoint.matrix = null;
			
			activePoint.rotateAngle = 0;
		}
		if (this.cutBitmap != null)
		{
			this.cutBitmap.recycle();
			this.cutBitmap = null;
		}
		if (this.transformedBitmap != null)
		{
			this.transformedBitmap.recycle();
			this.transformedBitmap = null;
		}
		if (this.selectBitmap != null)
			selectBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
		
	}
	public void doPerspective()
	{
		if (cutBitmap != null)
		{
			if (activePoint.selXX1 != activePoint.selXX2 || activePoint.selYY1 != activePoint.selYY2)
			{
				
				activePoint.matrix = new Matrix();				
				
				
				int bw = cutBitmap.getWidth();
		        int bh = cutBitmap.getHeight();
		        float[] src = {0, 0, 0, bh, bw, bh, bw, 0};
		        int DX = 100;		        
		        if (activePoint.selXX1 < activePoint.selXX2 && activePoint.selYY1 < activePoint.selYY2)
		        {
		        	float[] dst = {activePoint.selXX1+ activePoint.deltaP1X, activePoint.selYY1+ activePoint.deltaP1Y, activePoint.selXX1 + activePoint.deltaP2X, activePoint.selYY2 + activePoint.deltaP2Y, 
		        			activePoint.selXX2 + activePoint.deltaP4X, activePoint.selYY2 + activePoint.deltaP4Y, activePoint.selXX2 + activePoint.deltaP3X, activePoint.selYY1 + activePoint.deltaP3Y};
		        	activePoint.matrix.setPolyToPoly(src, 0, dst, 0, 4);
		        }
		        else if (activePoint.selXX1 <activePoint. selXX2 && activePoint.selYY1 >= activePoint.selYY2)
		        {
		        	float[]dst = {activePoint.selXX1 + activePoint.deltaP2X, activePoint.selYY2 + activePoint.deltaP2Y,activePoint.selXX1+ activePoint.deltaP1X, activePoint.selYY1+ activePoint.deltaP1Y, 
		        			activePoint.selXX2 + activePoint.deltaP3X, activePoint.selYY1 + activePoint.deltaP3Y,activePoint.selXX2 + activePoint.deltaP4X, activePoint.selYY2 + activePoint.deltaP4Y};
		        	/*float[] dst = {selXX1+ this.deltaP1X, selYY1+ this.deltaP1Y, selXX1 + this.deltaP2X, selYY2 + this.deltaP2Y, 
		        			selXX2 + this.deltaP4X, selYY2 + this.deltaP4Y, selXX2 + this.deltaP3X, selYY1 + this.deltaP3Y};*/
		        	activePoint.matrix.setPolyToPoly(src, 0, dst, 0, 4);
		        }
		        else if (activePoint.selXX1 >= activePoint.selXX2 && activePoint.selYY1 < activePoint. selYY2)
		        {
		        	float[] dst = {activePoint.selXX2 + activePoint.deltaP3X, activePoint.selYY1 + activePoint.deltaP3Y, activePoint.selXX2 + activePoint.deltaP4X, activePoint.selYY2 + activePoint.deltaP4Y, 
		        			activePoint.selXX1 + activePoint.deltaP2X, activePoint.selYY2 + activePoint.deltaP2Y, activePoint.selXX1+ activePoint.deltaP1X, activePoint.selYY1+ activePoint.deltaP1Y};
		        	
		        	/*float[] dst = {selXX1+ this.deltaP1X, selYY1+ this.deltaP1Y, selXX1 + this.deltaP2X, selYY2 + this.deltaP2Y, 
		        			selXX2 + this.deltaP4X, selYY2 + this.deltaP4Y, selXX2 + this.deltaP3X, selYY1 + this.deltaP3Y};*/
		        	activePoint.matrix.setPolyToPoly(src, 0, dst, 0, 4);
		        }
		        else if (activePoint.selXX1 >= activePoint.selXX2 && activePoint.selYY1 >= activePoint.selYY2)
		        {
		        	float[] dst = {activePoint.selXX2 + activePoint.deltaP4X, activePoint.selYY2 + activePoint.deltaP4Y,  activePoint.selXX2 + activePoint.deltaP3X, activePoint.selYY1 + activePoint.deltaP3Y,
		        			activePoint.selXX1+ activePoint.deltaP1X, activePoint.selYY1+ activePoint.deltaP1Y,  activePoint.selXX1 + activePoint.deltaP2X, activePoint.selYY2 + activePoint.deltaP2Y};
		        	/*float[] dst = {selXX1+ this.deltaP1X, selYY1+ this.deltaP1Y, selXX1 + this.deltaP2X, selYY2 + this.deltaP2Y, 
		        			selXX2 + this.deltaP4X, selYY2 + this.deltaP4Y, selXX2 + this.deltaP3X, selYY1 + this.deltaP3Y};*/
		        	activePoint.matrix.setPolyToPoly(src, 0, dst, 0, 4);
		        }
		        
		        //this.cropCanvas.d(, matrix, null);
		        if (this.transformedBitmap != null)
		        {
		        	this.transformedBitmap.recycle();
		        	this.transformedBitmap = null;
		        }
		        this.transformedBitmap = Bitmap.createBitmap(cutBitmap);
		        
		        
		        
			}
		}
	}
	public void doRotate()
	{
		if (cutBitmap != null)
		{
			this.transformedBitmap = rotate(cutBitmap,activePoint.rotateAngle);			
		}
		
	}
	public Bitmap rotate(Bitmap b, int degrees) {
		if ( degrees != 0 && b != null ) 
		{	
			try {
				Bitmap b2 = Bitmap.createBitmap(cutBitmap.getWidth(), cutBitmap.getHeight(), Config.ARGB_4444);
				Canvas can = new Canvas(b2);
				can.rotate(degrees,b2.getWidth() / 2, b2.getHeight() / 2);
				can.drawBitmap(cutBitmap,0,0 ,p);
				b = b2;
			} catch (OutOfMemoryError ex) {
				// We have no memory to rotate. Return the original bitmap.
			}
		}
		return b;
	}
	public void doScaleTransform()
	{
		if (cutBitmap != null)
		{
			if (activePoint.selXX1 != activePoint.selXX2 && activePoint.selYY1 != activePoint.selYY2)
			{
				float deltaWidth1 = activePoint.selXX1 + activePoint.deltaP1X;
				float deltaHeight1 = activePoint.selYY1 + activePoint.deltaP1Y;
				
				float deltaWidth2 = activePoint.selXX2 + activePoint.deltaP4X;
				float deltaHeight2 = activePoint.selYY2 + activePoint.deltaP4Y;
				
				
				float w = cutBitmap.getWidth();
				float h = cutBitmap.getHeight();
				if (w != 0 && h != 0)
				{
					float deltaX = Math.abs(deltaWidth1 - deltaWidth2)  / w;
					float deltaY = Math.abs(deltaHeight1 -deltaHeight2) / h;
					
					float scale = Math.min(deltaX,deltaY);
					if ((int)(w * scale )!= 0 && (int)(h * scale )!= 0)
					{
						Bitmap result = Bitmap.createScaledBitmap(this.cutBitmap, (int)(w * scale),(int)(h * scale),true);
						if (this.transformedBitmap != null)
						{
							transformedBitmap.recycle();
							transformedBitmap = null;
						}
						
						transformedBitmap = Bitmap.createBitmap((int)Math.abs(deltaWidth1 - deltaWidth2),(int)Math.abs(deltaHeight1 - deltaHeight2),Config.ARGB_4444);
						transformedBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
						Canvas can1 = new Canvas(this.transformedBitmap);
						float leftDelta =  ((Math.abs(deltaWidth1 - deltaWidth2) - w*scale) /2);
						float topDelta = ((Math.abs(deltaHeight1 - deltaHeight2) - h * scale) / 2);
						can1.drawBitmap(result, leftDelta,topDelta, this.canvasPaint);
					}
				}
				//this.transformedBitmap = result;
				
			}
		}
		else
		{
			
		}
	}
	public void doTransform1()
	{
		
	}
	public int containSquare(float x, float y)
	{
		if (activePoint != null)
		{
			double  delta1 = Math.sqrt((activePoint.selXX1 +activePoint. deltaP1X-x)*(activePoint.selXX1 +activePoint. deltaP1X-x) + (activePoint.selYY1 + activePoint.deltaP1Y-y)*(activePoint.selYY1+ activePoint.deltaP1Y-y));
			double delta2 = Math.sqrt((activePoint.selXX1 +activePoint. deltaP2X-x)*(activePoint.selXX1+activePoint. deltaP2X-x) + (activePoint.selYY2 + activePoint.deltaP2Y-y)*(activePoint.selYY2 + activePoint.deltaP2Y-y));
			double delta3 = Math.sqrt((activePoint.selXX2 +activePoint. deltaP3X -x)*(activePoint.selXX2 +activePoint. deltaP3X-x) + (activePoint.selYY1 + activePoint.deltaP3Y-y)*(activePoint.selYY1 + activePoint.deltaP3Y-y));
			double delta4 = Math.sqrt((activePoint.selXX2 +activePoint. deltaP4X-x)*(activePoint.selXX2 +activePoint. deltaP4X-x) + (activePoint.selYY2 + activePoint.deltaP4Y-y)*(activePoint.selYY2 + activePoint.deltaP4Y-y));
			if (delta1 < 30)
			{
				return 1;
			}
			else if (delta2 < 30)
			{
				return 2;
			}
			else if (delta3 < 30)
			{
				return 3;
			}
			else if (delta4 < 30)
			{
				return 4;
			}
			else if (x >= Math.min(activePoint.selXX1, activePoint.selXX2) && x <= Math.max(activePoint.selXX1, activePoint.selXX2)
					&& y >= Math.min(activePoint.selYY1, activePoint.selYY2) && y <= Math.max(activePoint.selYY1, activePoint.selYY2))
			{
				return 5;
			}
		}
		return 0;
	}
	public void freeMemory()
	{
		if (this.drawBitmap != null)
		{
			this.drawBitmap.recycle();
			this.drawBitmap = null;
		}
		if (selectBitmap != null)
		{
			this.selectBitmap.recycle();
			this.selectBitmap = null;
		}
		if (this.cutBitmap != null)
		{
			this.cutBitmap.recycle();
			this.cutBitmap = null;
		}
		if (this.originalBitmap != null)
		{
			this.originalBitmap.recycle();
			this.originalBitmap = null;
		}
		if (this.transformedBitmap != null)
		{
			this.transformedBitmap.recycle();
			this.transformedBitmap = null;
		}
		if (canvasPaint != null)
			canvasPaint = null;
		if (cropCanvas != null)
			cropCanvas = null;
		if (orgCanvas != null)
			orgCanvas = null;
		if (selCanvas != null)
			selCanvas = null;
		canvasPaint = null;
		p = null;
		Globals.back = null;
	}
	
	
	
	
	
	

}
