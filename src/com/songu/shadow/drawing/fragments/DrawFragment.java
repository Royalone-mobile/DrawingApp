package com.songu.shadow.drawing.fragments;

import java.util.ArrayList;

















import com.songu.shadow.drawing.MainActivity;
import com.songu.shadow.drawing.OpenDrawingActivity;
import com.songu.shadow.drawing.R;
import com.songu.shadow.drawing.doc.Globals;
import com.songu.shadow.drawing.model.CustomColor;
import com.songu.shadow.drawing.tools.SerializationTools;
import com.songu.shadow.drawing.tools.Tools;
import com.songu.shadow.drawing.view.DrawView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.view.animation.Transformation;
import android.widget.SeekBar.OnSeekBarChangeListener;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DrawFragment extends CustomFragment implements View.OnClickListener{

	
	LinearLayout toolbar;
	RelativeLayout colorbar;
	ListView colorList;
	ArrayList<CustomColor> colors;
	Context c;
	DrawView drawView;
	ImageView deleteButton;
	ImageView addDrawingButton;
	ImageView saveDrawingButton;
	LinearLayout rightBar;
	
	//Toolbar actionBarView;
	LayoutInflater inflater;
	
	ImageButton expandTool;
	ImageButton expandColor;
	
	
	
	
	Button brushSizeButton;
	ImageButton eraserButton;
	ImageButton shapeButton;
	ImageButton transformButton;
	public ImageButton colorDropperButton;
	ImageButton brushesButton;
	
	String drawingName;
	int actionbarHeight;
	
	int currentButtonId;
	
	public UndoActions undoActions;
	
	public DrawFragment(LayoutInflater inflater, int resource) {
		super(inflater, resource);
	}
	
	public DrawFragment(LayoutInflater inflater, int resource, Toolbar actionBarView) {
		super(inflater, resource);
		this.inflater = inflater;
		undoActions = new UndoActions();
		colorList = (ListView) fragmentLayout.findViewById(R.id.draw_color_list);
		drawView = (DrawView) fragmentLayout.findViewById(R.id.drawView1);
		toolbar = (LinearLayout) fragmentLayout.findViewById(R.id.draw_bottom_layout);
		colorbar = (RelativeLayout) fragmentLayout.findViewById(R.id.layout_color_bar);
		rightBar = (LinearLayout) fragmentLayout.findViewById(R.id.expand_rightbar);
		this.toolbar.setVisibility(View.GONE);
		this.colorbar.setVisibility(View.GONE);
		
		expandTool = (ImageButton) fragmentLayout.findViewById(R.id.expand_toolbar);
		expandColor = (ImageButton) fragmentLayout.findViewById(R.id.expand_colorbar);
		
		expandTool.setVisibility(View.VISIBLE);
		expandColor.setVisibility(View.VISIBLE);
		this.expandColor.setOnClickListener(this);
		this.expandTool.setOnClickListener(this);
		
		
		drawView.setBrushType(R.id.brush_pencil);
		drawView.setFragment(this);
		MainActivity main = (MainActivity) this.inflater.getContext(); 
		deleteButton = (ImageView) main.getActionBar().getCustomView().findViewById(R.id.draw_delete);
		addDrawingButton = (ImageView) main.getActionBar().getCustomView().findViewById(R.id.draw_add);
		saveDrawingButton = (ImageView) main.getActionBar().getCustomView().findViewById(R.id.draw_check);
		
		actionbarHeight = main.getActionBar().getHeight();
		c = inflater.getContext();
		colors = Tools.getSerializedColors(c);
		
		
		brushSizeButton = (Button) fragmentLayout.findViewById(R.id.draw_brush_size);
		shapeButton = (ImageButton) fragmentLayout.findViewById(R.id.draw_shapes);
		eraserButton = (ImageButton) fragmentLayout.findViewById(R.id.draw_eraser);
		brushesButton = (ImageButton) fragmentLayout.findViewById(R.id.draw_brushes);
		colorDropperButton = (ImageButton) fragmentLayout.findViewById(R.id.draw_watercolor);
		transformButton = (ImageButton) fragmentLayout.findViewById(R.id.draw_transform);
		colorList.setAdapter(colorListAdapter);
		colorList.setOnItemClickListener(colorClickListener);
		
		brushSizeButton.setOnClickListener(bottomButtonPressed);
		eraserButton.setOnClickListener(bottomButtonPressed);
		brushesButton.setOnClickListener(bottomButtonPressed);
		colorDropperButton.setOnClickListener(bottomButtonPressed);
		shapeButton.setOnClickListener(bottomButtonPressed);
		transformButton.setOnClickListener(bottomButtonPressed);
		
		addDrawingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//((Activity) c).startActivityForResult(new Intent(c, OpenDrawingActivity.class), 10);
				((MainActivity) c).openHome();
				
			}
		});
		
		resizeToolbar();
		resizeColorbar();
		saveDrawingButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(c);
				// Add the buttons
				builder.setTitle(R.string.save);
				builder.setMessage(R.string.save_message);
				builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   String filename = drawView.saveToImage();
				        	   AlertDialog.Builder builder = new AlertDialog.Builder(c);
								builder.setMessage(filename + "   Saved")
								       .setCancelable(false)
								       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
								           public void onClick(DialogInterface dialog, int id) {
								                //do things
								           }
								       });
								AlertDialog alert = builder.create();
								alert.show();
				           }
				       });
				builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				               // User cancelled the dialog
				           }
				       });

				builder.create().show();
				
				
			}
		});
		
		deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawView.clearDrawing();
			}
		});
	}
	public void setButtonSize(View button,int width)
	{
		LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) button.getLayoutParams();
		lp1.width = width - 10;
		lp1.leftMargin = 5;
		lp1.rightMargin = 5;
		button.setLayoutParams(lp1);
	}
	public void resizeColorbar()
	{
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.colorbar.getLayoutParams();
		if (Globals.g_screenWidth / 8  < lp.width)
		{
			lp.width = Globals.g_screenWidth / 8;
			this.colorbar.setLayoutParams(lp);
			
		}
		Globals.g_colorWidth = lp.width;
		
	}
	public void resizeToolbar()
	{
		
		LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) brushSizeButton.getLayoutParams();
		brushSizeButton = (Button) fragmentLayout.findViewById(R.id.draw_brush_size);
		shapeButton = (ImageButton) fragmentLayout.findViewById(R.id.draw_shapes);
		
		if (Globals.g_screenWidth < lp1.width * 6)
		{
			int width = Globals.g_screenWidth / 6;
			setButtonSize(brushSizeButton,width);
			setButtonSize(shapeButton,width);
			setButtonSize(eraserButton,width);
			setButtonSize(brushesButton,width);
			setButtonSize(colorDropperButton,width);
			setButtonSize(transformButton,width);
			
			brushSizeButton.setTextSize(12);
		}
		
		eraserButton = (ImageButton) fragmentLayout.findViewById(R.id.draw_eraser);
		brushesButton = (ImageButton) fragmentLayout.findViewById(R.id.draw_brushes);
		colorDropperButton = (ImageButton) fragmentLayout.findViewById(R.id.draw_watercolor);
		transformButton = (ImageButton) fragmentLayout.findViewById(R.id.draw_transform);
	}
	public class UndoActions{
	
		@SuppressLint("InflateParams")
		public void addUndoButtonItem(){
			removeUndoButton();
			MainActivity main = (MainActivity) DrawFragment.this.inflater.getContext();
			
			Button undoItemButton = (Button) inflater.inflate(R.layout.draw_undo_item, null);
			undoItemButton.setLayoutParams(new LayoutParams(actionbarHeight,actionbarHeight));
			undoItemButton.setOnClickListener(undoButtonClicked);
			LinearLayout undoHolder = ((LinearLayout)main.getActionBar().getCustomView().findViewById(R.id.draw_undo_bar));
			undoItemButton.setText(""+undoHolder.getChildCount());
			((LinearLayout) main.getActionBar().getCustomView().findViewById(R.id.draw_undo_bar)).addView(undoItemButton);
			
			addUndoButton();
			horzScrollToEndOfUndos();	
		}
		
		
		public void horzScrollToEndOfUndos(){
			
			final MainActivity main = (MainActivity) DrawFragment.this.inflater.getContext();
			
			
			((HorizontalScrollView)main.getActionBar().getCustomView().findViewById(R.id.draw_actionbar_hrz_undo_scroll)).postDelayed(new Runnable() {
			    public void run() { 
			    	((HorizontalScrollView)main.getActionBar().getCustomView().findViewById(R.id.draw_actionbar_hrz_undo_scroll)).fullScroll(HorizontalScrollView.FOCUS_RIGHT);
			    } 
			}, 100L);
		}
		
		public void clearUndos(){
			removeUndoButton();
			MainActivity main = (MainActivity) DrawFragment.this.inflater.getContext();
			int childCount = ((LinearLayout)main.getActionBar().getCustomView().findViewById(R.id.draw_undo_bar)).getChildCount();
			for (int i = childCount-1; i>0; i--){
					Log.d("LC", ""+i);
					((LinearLayout)main.getActionBar().getCustomView().findViewById(R.id.draw_undo_bar)).removeViewAt(i);
			}
		}
		
		public void updateUndos(int numUndos){
			removeUndoButton();
			MainActivity main = (MainActivity) DrawFragment.this.inflater.getContext();
			int childCount = ((LinearLayout)main.getActionBar().getCustomView().findViewById(R.id.draw_undo_bar)).getChildCount();
			for (int i = childCount-1; i>0; i--){
				 	if (i>numUndos)
				 		((LinearLayout)main.getActionBar().getCustomView().findViewById(R.id.draw_undo_bar)).removeViewAt(i);
			}
			
			System.out.println("NO "+numUndos);
			
			if (numUndos!=0)
				addUndoButton();
			else
				drawView.clearDrawing();
		}
		
		public void removeUndoButton(){
			MainActivity main = (MainActivity) DrawFragment.this.inflater.getContext();
			if (main.getActionBar().getCustomView().findViewById(R.id.undoButton)!=null)
				((LinearLayout)main.getActionBar().getCustomView().findViewById(R.id.draw_undo_bar)).removeView(main.getActionBar().getCustomView().findViewById(R.id.undoButton));
		}
		
		@SuppressLint("InflateParams")
		public void addUndoButton(){
			MainActivity main = (MainActivity) DrawFragment.this.inflater.getContext();
			ImageButton undoButton = (ImageButton) inflater.inflate(R.layout.draw_undo_image_button, null);
			undoButton.setLayoutParams(new LayoutParams(actionbarHeight,actionbarHeight));
			undoButton.setOnClickListener(undoButtonClicked);
			((LinearLayout)main.getActionBar().getCustomView().findViewById(R.id.draw_undo_bar)).addView(undoButton);
		}
		
		OnClickListener undoButtonClicked = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()){
				
				//numeric undo item in action bar
				case R.id.undoItem:
					Button b = (Button) v;
					int undoLocation = Integer.parseInt(b.getText().toString());
					drawView.undoTo(undoLocation-1);
					break;
					
				//general undo button image in action bar
				case R.id.undoButton:
					MainActivity main = (MainActivity) DrawFragment.this.inflater.getContext();
					drawView.undoTo(((LinearLayout)main.getActionBar().getCustomView().findViewById(R.id.draw_undo_bar)).getChildCount()-4);
					break;
				}
			}
		};
	
	}
	
		
	PopupWindow popupWindow;
	
	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	public void showBottomPopup(int id){
		LinearLayout content = (LinearLayout) inflater.inflate(R.layout.draw_bottom_popup, null);
		final LinearLayout contentHolder = (LinearLayout) content.findViewById(R.id.draw_bottom_popup_content);
		
		switch(id){
		case R.id.draw_shapes:
			contentHolder.addView(inflater.inflate(R.layout.draw_bottom_shape, null),LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
			if ((ImageView)contentHolder.findViewById(drawView.getBrushType()) != null)				
				((ImageView)contentHolder.findViewById(drawView.getBrushType())).setColorFilter(c.getResources().getColor(R.color.purple), Mode.SRC_IN);
			OnClickListener shapeClickListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					drawView.setBrushType(v.getId());
					
					((ImageView)contentHolder.findViewById(R.id.shape_circle)).setColorFilter(null);
					((ImageView)contentHolder.findViewById(R.id.shape_rectangle)).setColorFilter(null);
					((ImageView)contentHolder.findViewById(R.id.shape_triangle)).setColorFilter(null);
					((ImageView)contentHolder.findViewById(R.id.shape_cubic)).setColorFilter(null);
					((ImageView)contentHolder.findViewById(R.id.shape_roundrect)).setColorFilter(null);
					
					((ImageView)contentHolder.findViewById(drawView.getBrushType())).setColorFilter(c.getResources().getColor(R.color.purple), Mode.SRC_IN);
				}
			};
			contentHolder.findViewById(R.id.shape_circle).setOnClickListener(shapeClickListener);
			contentHolder.findViewById(R.id.shape_rectangle).setOnClickListener(shapeClickListener);
			contentHolder.findViewById(R.id.shape_triangle).setOnClickListener(shapeClickListener);
			contentHolder.findViewById(R.id.shape_cubic).setOnClickListener(shapeClickListener);
			contentHolder.findViewById(R.id.shape_roundrect).setOnClickListener(shapeClickListener);
			
			content.findViewById(R.id.draw_bottom_popup_arrow1).setVisibility(View.VISIBLE);
			content.findViewById(R.id.draw_bottom_popup_arrow2).setVisibility(View.INVISIBLE);
			content.findViewById(R.id.draw_bottom_popup_arrow3).setVisibility(View.INVISIBLE);
			content.findViewById(R.id.draw_bottom_popup_arrow4).setVisibility(View.INVISIBLE);
			content.findViewById(R.id.draw_bottom_popup_arrow5).setVisibility(View.INVISIBLE);	
			content.findViewById(R.id.draw_bottom_popup_arrow6).setVisibility(View.INVISIBLE);
			
			break;
		case R.id.draw_brush_size:
			contentHolder.addView(inflater.inflate(R.layout.draw_bottom_brush_size, null),LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
			SeekBar sb = (SeekBar) contentHolder.findViewById(R.id.draw_brush_size_scroll);
			sb.setProgress((int)drawView.brushSize);
			sb.setOnSeekBarChangeListener(brushSizeOnSeekBarChangedListener);
			contentHolder.findViewById(R.id.draw_brush_size_down).setOnClickListener(bottomButtonPressed);
			contentHolder.findViewById(R.id.draw_brush_size_up).setOnClickListener(bottomButtonPressed);
			content.findViewById(R.id.draw_bottom_popup_arrow1).setVisibility(View.INVISIBLE);
			content.findViewById(R.id.draw_bottom_popup_arrow2).setVisibility(View.INVISIBLE);
			content.findViewById(R.id.draw_bottom_popup_arrow3).setVisibility(View.INVISIBLE);
			content.findViewById(R.id.draw_bottom_popup_arrow4).setVisibility(View.INVISIBLE);
			content.findViewById(R.id.draw_bottom_popup_arrow5).setVisibility(View.VISIBLE);
			content.findViewById(R.id.draw_bottom_popup_arrow6).setVisibility(View.INVISIBLE);
			
			break;
		case R.id.draw_transform:
			contentHolder.addView(inflater.inflate(R.layout.draw_bottom_transform_type, null),LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
			if ((ImageView)contentHolder.findViewById(drawView.getBrushType()) != null)				
				((ImageView)contentHolder.findViewById(drawView.getBrushType())).setColorFilter(c.getResources().getColor(R.color.purple), Mode.SRC_IN);
			OnClickListener transformClickListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					drawView.setBrushType(v.getId());
					
					((ImageView)contentHolder.findViewById(R.id.transform_scale)).setColorFilter(null);
					((ImageView)contentHolder.findViewById(R.id.transform_free)).setColorFilter(null);
					((ImageView)contentHolder.findViewById(R.id.transform_perspective)).setColorFilter(null);
					((ImageView)contentHolder.findViewById(R.id.transform_rotate)).setColorFilter(null);
					((ImageView)contentHolder.findViewById(R.id.transform_transform1)).setColorFilter(null);
					
					((ImageView)contentHolder.findViewById(drawView.getBrushType())).setColorFilter(c.getResources().getColor(R.color.purple), Mode.SRC_IN);
				}
			};
			contentHolder.findViewById(R.id.transform_scale).setOnClickListener(transformClickListener);
			contentHolder.findViewById(R.id.transform_free).setOnClickListener(transformClickListener);
			contentHolder.findViewById(R.id.transform_perspective).setOnClickListener(transformClickListener);
			contentHolder.findViewById(R.id.transform_rotate).setOnClickListener(transformClickListener);
			contentHolder.findViewById(R.id.transform_transform1).setOnClickListener(transformClickListener);
			
			content.findViewById(R.id.draw_bottom_popup_arrow1).setVisibility(View.INVISIBLE);
			content.findViewById(R.id.draw_bottom_popup_arrow2).setVisibility(View.INVISIBLE);
			content.findViewById(R.id.draw_bottom_popup_arrow3).setVisibility(View.VISIBLE);
			content.findViewById(R.id.draw_bottom_popup_arrow4).setVisibility(View.INVISIBLE);
			content.findViewById(R.id.draw_bottom_popup_arrow5).setVisibility(View.INVISIBLE);	
			content.findViewById(R.id.draw_bottom_popup_arrow6).setVisibility(View.INVISIBLE);
			break;
		case R.id.draw_brushes:
			contentHolder.addView(inflater.inflate(R.layout.draw_bottom_brush_type, null),LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
			if ((ImageView)contentHolder.findViewById(drawView.getBrushType()) != null)				
				((ImageView)contentHolder.findViewById(drawView.getBrushType())).setColorFilter(c.getResources().getColor(R.color.purple), Mode.SRC_IN);
			OnClickListener brushClickListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					drawView.setBrushType(v.getId());
					
					((ImageView)contentHolder.findViewById(R.id.brush_stencil)).setColorFilter(null);
					((ImageView)contentHolder.findViewById(R.id.brush_pencil)).setColorFilter(null);
					((ImageView)contentHolder.findViewById(R.id.brush_fat_marker)).setColorFilter(null);
					((ImageView)contentHolder.findViewById(R.id.brush_marker)).setColorFilter(null);
					((ImageView)contentHolder.findViewById(R.id.brush_paint)).setColorFilter(null);
					
					((ImageView)contentHolder.findViewById(drawView.getBrushType())).setColorFilter(c.getResources().getColor(R.color.purple), Mode.SRC_IN);
				}
			};
			contentHolder.findViewById(R.id.brush_stencil).setOnClickListener(brushClickListener);
			contentHolder.findViewById(R.id.brush_pencil).setOnClickListener(brushClickListener);
			contentHolder.findViewById(R.id.brush_fat_marker).setOnClickListener(brushClickListener);
			contentHolder.findViewById(R.id.brush_marker).setOnClickListener(brushClickListener);
			contentHolder.findViewById(R.id.brush_paint).setOnClickListener(brushClickListener);
			
			content.findViewById(R.id.draw_bottom_popup_arrow1).setVisibility(View.INVISIBLE);
			content.findViewById(R.id.draw_bottom_popup_arrow2).setVisibility(View.VISIBLE);
			content.findViewById(R.id.draw_bottom_popup_arrow3).setVisibility(View.INVISIBLE);
			content.findViewById(R.id.draw_bottom_popup_arrow4).setVisibility(View.INVISIBLE);
			content.findViewById(R.id.draw_bottom_popup_arrow5).setVisibility(View.INVISIBLE);
			content.findViewById(R.id.draw_bottom_popup_arrow6).setVisibility(View.INVISIBLE);
			
			
			break;
		}
		
		LinearLayout bottomLayout = (LinearLayout) fragmentLayout.findViewById(R.id.draw_bottom_layout);
		popupWindow = new PopupWindow(content,bottomLayout.getWidth(),LayoutParams.WRAP_CONTENT,true);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable(c.getResources()));
		popupWindow.setOutsideTouchable(true); 
		content.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		popupWindow.showAtLocation(bottomLayout, Gravity.BOTTOM, 0, content.getMeasuredHeight()-Tools.numtodp(c, 20));
	}
	
	OnClickListener bottomButtonPressed = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (currentButtonId != 0)
				fragmentLayout.findViewById(currentButtonId).setBackgroundResource(R.drawable.ring_more_padding);
			currentButtonId = v.getId();
			switch (v.getId()){
			case R.id.draw_shapes:
				drawView.setEraserMode(false);
				showBottomPopup(v.getId());
				fragmentLayout.findViewById(currentButtonId).setBackgroundResource(R.drawable.ring_more_padding);
				break;
			case R.id.draw_brushes:
				drawView.setEraserMode(false);
				showBottomPopup(v.getId());
				break;
			
			case R.id.draw_brush_size:
				showBottomPopup(v.getId());
				break;
			case R.id.draw_transform:
				drawView.setEraserMode(false);
				showBottomPopup(v.getId());
				break;
			case R.id.draw_eraser:
				drawView.setEraserMode(!drawView.eraserMode);
				drawView.setBrushType(R.id.draw_eraser);
				/*if (drawView.eraserMode){
					((ImageButton)v).setBackgroundResource(R.drawable.ring_purple_more_padding);
				}else
					((ImageButton)v).setBackgroundResource(R.drawable.ring_more_padding);*/
				break;
				
			case R.id.draw_watercolor:
				drawView.setEraserMode(false);
				drawView.setBrushType(v.getId());
				
				/*drawView.colorPicking = !drawView.colorPicking;
				if (drawView.colorPicking){
					((ImageButton)v).setBackgroundResource(R.drawable.ring_purple_more_padding);
				}else
					((ImageButton)v).setBackgroundResource(R.drawable.ring_more_padding);*/
				
				break;
				
			case R.id.draw_brush_size_down:
				setBrushSize(drawView.brushSize-1);
				break;
				
			case R.id.draw_brush_size_up:
				setBrushSize(drawView.brushSize+1);
				
				break;
				
			default:
				break;
			
			
			
			}
			fragmentLayout.findViewById(currentButtonId).setBackgroundResource(R.drawable.ring_purple_more_padding);
		}
	};
	
	public void setBrushSize(float progress){
		if (progress>0 && progress<=100){
			drawView.setBrushSize(progress);
			((Button) fragmentLayout.findViewById(R.id.draw_brush_size)).setText((int)progress+"Pt");
			if (popupWindow!=null && popupWindow.isShowing()){
				((SeekBar)popupWindow.getContentView().findViewById(R.id.draw_brush_size_scroll)).setProgress((int)progress-1);
			}
		}
	}
	
	public void openDrawing(String fileName){
		drawView.clearDrawing();
		drawView.points = SerializationTools.loadPoints(c, fileName);
		drawView.falsifyPointsDrawn();
		//drawView.doDrawing();
		//drawView.
		drawView.postInvalidate();
		undoActions.clearUndos();
		for (int i =0; i < drawView.points.size(); i++){
			if (drawView.points.get(i).cut)
				undoActions.addUndoButtonItem();
		}
	}
	
	OnSeekBarChangeListener brushSizeOnSeekBarChangedListener = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			setBrushSize(progress+1);
		}
	};
	
	OnItemClickListener colorClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			drawView.setColor(colors.get(position).color);
			
		}
	};
	
	BaseAdapter colorListAdapter = new BaseAdapter() {
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ImageView colorView = new ImageView(c);
			colorView.setImageResource(R.drawable.ic_blank_circle);
			colorView.setColorFilter(colors.get(position).color);		
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
			lp.height = Globals.g_colorWidth;			
			colorView.setLayoutParams(lp);
			return colorView;
		}
		
		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		@Override
		public Object getItem(int position) {
			return null;
		}
		
		@Override
		public int getCount() {
			return colors.size();
		}
	};
	
	public void fadeInColor(final View view)
	{
		final Animation fadeInAnimation = AnimationUtils.loadAnimation(this.c, R.anim.fade_in );
		
		fadeInAnimation.setAnimationListener( new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) { 
				view.setVisibility(View.VISIBLE);
			}
			
			
			@Override
			public void onAnimationRepeat(Animation animation) { }
			
			@Override
			public void onAnimationEnd(Animation animation) {
				
			}
		});
		view.startAnimation( fadeInAnimation );	
	}
	public void fadeOutColor(final View view)
	{
		final Animation fadeOutAnimation = AnimationUtils.loadAnimation(this.c, R.anim.fade_out );
		
		fadeOutAnimation.setAnimationListener( new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) { }
			
			@Override
			public void onAnimationRepeat(Animation animation) { }
			
			@Override
			public void onAnimationEnd(Animation animation) {
				view.setVisibility(View.GONE);
			}
		});
		view.startAnimation( fadeOutAnimation );	
	}
	public static void expand(final View v) {
	    v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    final int targetHeight = v.getMeasuredHeight();

	    v.getLayoutParams().height = 0;
	    v.setVisibility(View.VISIBLE);
	    Animation a = new Animation()
	    {
	        @Override
	        protected void applyTransformation(float interpolatedTime, Transformation t) {
	            v.getLayoutParams().height = interpolatedTime == 1
	                    ? LayoutParams.WRAP_CONTENT
	                    : (int)(targetHeight * interpolatedTime);
	            v.requestLayout();
	        }

	        @Override
	        public boolean willChangeBounds() {
	            return true;
	        }
	    };

	    // 1dp/ms
	    //a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
	    a.setDuration(2000);
	    v.startAnimation(a);
	}

	public static void collapse(final View v) {
	    final int initialHeight = v.getMeasuredHeight();

	    Animation a = new Animation()
	    {
	        @Override
	        protected void applyTransformation(float interpolatedTime, Transformation t) {
	            if(interpolatedTime == 1){
	                v.setVisibility(View.GONE);
	            }else{
	                v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
	                v.requestLayout();
	            }
	        }

	        @Override
	        public boolean willChangeBounds() {
	            return true;
	        }
	    };

	    // 1dp/ms
	    a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
	    v.startAnimation(a);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.expand_toolbar:
			this.expand(this.toolbar);
			this.expandTool.setVisibility(View.GONE);
			break;
		case R.id.expand_colorbar:
			this.fadeInColor(this.colorbar);
			this.expandColor.setVisibility(View.GONE);
			break;
		}
	}
	public void removeFree()
	{
		this.drawView.freeMemory();
	}

}
