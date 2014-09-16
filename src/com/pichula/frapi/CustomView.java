package com.pichula.frapi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CustomView extends View{

	private float percent;
	
	
	private Paint barPaint, barPaintEmpty;
	
	private static final int BAR_COLOR=0xFF1E90FF;
	private static final int BAR_EMPTY_COLOR=0xFF333333;
	private static final int BAR_WIDTH=7;
	
	public CustomView(Context context) {
		super(context);
		createObjects();
	}
	
	public CustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		createObjects();
	}
	
	private void createObjects(){
		barPaint=new Paint();
		barPaint.setColor(BAR_COLOR);
		barPaint.setStrokeWidth(BAR_WIDTH);
		
		barPaintEmpty=new Paint();
		barPaintEmpty.setColor(BAR_EMPTY_COLOR);
		barPaintEmpty.setStrokeWidth(1);
	}
	
	public void setPercent(float percent){
		this.percent=percent;
		invalidate();
	}
	
	int widthpc=0;
	int width2=0;
	int height2=0;
	int width = 0;
	int height=0;
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		setBackgroundColor(Color.TRANSPARENT);
		if(height2==0){
			height=canvas.getHeight();
			height2=canvas.getHeight()/2;
			width=canvas.getWidth();
			width2=width/2;
			widthpc=width/100;
		}
		//canvas.drawLine(0, height2, width, height2, barPaintEmpty);
		canvas.drawLine(0, height2, widthpc*percent, height2, barPaint);
	}

}
