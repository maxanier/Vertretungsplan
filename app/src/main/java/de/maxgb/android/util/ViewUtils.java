package de.maxgb.android.util;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class ViewUtils {
	public static TextView getTextView(Context c,CharSequence text){
		TextView t =new TextView(c);
		t.setText(text);
		
		return t;
	}
	public static TextView getTextView(Context c,CharSequence text,float size){
		TextView t =getTextView(c,text);
		t.setTextSize(size);
		
		return t;
	}
	public static TextView getCenteredTextView(Context c,CharSequence text){
		TextView t = getTextView(c,text);
		t.setGravity(Gravity.CENTER);
		return t;
	}
	public static TextView getDecoratedTextView(Context c,CharSequence text,boolean centered,boolean bold,boolean italic){
		if(centered){
			return getCenteredTextView(c,getDecoratedText(text,bold,italic));
		
		}
		return getTextView(c,getDecoratedText(text,bold,italic));
	}
	
	public static SpannableString getDecoratedText(CharSequence text,boolean bold,boolean italic){
		SpannableString s = new SpannableString(text);
		if(bold&&italic){
			s.setSpan(new StyleSpan(Typeface.BOLD_ITALIC),0,s.length(),0);
		}
		else if(bold){
			s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), 0);
		}
		else if(italic){
			s.setSpan(new StyleSpan(Typeface.ITALIC), 0,s.length(), 0);
		}
		
		return s;
	}
	
	public static ImageView getImageView(Context c,Drawable d){
		ImageView r=new ImageView(c);
		r.setImageDrawable(d);
		return r;
	}

}
