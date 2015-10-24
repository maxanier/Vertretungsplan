package de.maxgb.android.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class GraphicUtils {
	public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
	    Paint paint = new Paint();
	    paint.setTextSize(textSize);
	    paint.setColor(textColor);
	    paint.setTextAlign(Paint.Align.LEFT);
	    int width = (int) (paint.measureText(text) + 0.5f); // round
	    float baseline = (int) (-paint.ascent() + 0.5f); // ascent() is negative
	    int height = (int) (baseline + paint.descent() + 0.5f);
	    Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(image);
	    canvas.drawText(text, 0, baseline, paint);
	    return image;
	}
	
    public static Bitmap combineImages(Bitmap c, Bitmap s) 
    { 
	    Bitmap cs = null; 
	
	    int width, height = 0; 
	
	    if(c.getWidth() > s.getWidth()) { 
	      width = c.getWidth() + s.getWidth(); 
	      height = c.getHeight(); 
	    } else { 
	      width = s.getWidth() + s.getWidth(); 
	      height = c.getHeight(); 
	    } 
	
	    cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); 
	
	    Canvas comboImage = new Canvas(cs); 
	
	    comboImage.drawBitmap(c, 0f, 0f, null); 
	    comboImage.drawBitmap(s, c.getWidth(), 0f, null); 

    return cs; 
    }  
}
