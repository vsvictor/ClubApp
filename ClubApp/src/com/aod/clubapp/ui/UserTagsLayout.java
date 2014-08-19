package com.aod.clubapp.ui;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.aod.clubapp.R;

public class UserTagsLayout extends ViewGroup {

    private static class Trio {
    	int x;
    	int width;
    	int level;
    	
    	public Trio(int x, int w, int l) {
    		this.x = x;
    		this.width = w;
    		this.level = 0;
    	}
    	public void setLevel(int l) {
    		level = l;
    	}
    	public boolean isOverlaps(Trio obj){
    		if(level != obj.level) {
    			return false;
    		}
    		//выбираем отрезок, который левей, и если Х правого отрезка оказывается 
    		//всередине выбраного - отрезки пересекаются
    		if(obj.x >= x) {
    			return obj.x <= (x + width);
    		}
    		return x <= (obj.x + obj.width); 
    	}
    }
        
    public UserTagsLayout(Context context) {
        super(context);
    }

    public UserTagsLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserTagsLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }	
	
    /**
     * Any layout manager that doesn't scroll will want this.
     */
    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

	/**
	 * Ask all children to measure themselves and compute the measurement of
	 * this layout based on the children.
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int wspec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() / 3,
				MeasureSpec.AT_MOST);
		int hspec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(),
				MeasureSpec.EXACTLY);
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			v.measure(wspec, hspec);
		}
	}  
    
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {		
		int parentHeight = b- t; 
		ArrayList<Trio> prosesedElem = new ArrayList<UserTagsLayout.Trio>();
		int verticalOfset = 0;
		for (int i = 0; i < this.getChildCount(); i++) {
			View v = getChildAt(i);
			if(verticalOfset == 0) {
				TextView tv = (TextView) v.findViewById(R.id.tv_user);
				verticalOfset = tv.getMeasuredHeight();
			}
			float xpos = (Float)v.getTag();
			int childWidth = v.getMeasuredWidth();
			
			int itemX = (Math.round((r - l) * xpos) - childWidth/2);
			itemX = itemX < 0 ? 0 : itemX;
			Trio position = new Trio(itemX, childWidth, 0);
    		
levelFound:
			for(int level = 0 ; level < 4; level++){
				position.setLevel(level);
				boolean freeSpaceFound = true;
				for(Trio tr : prosesedElem) {
					if(tr.isOverlaps(position)){
						freeSpaceFound = false;
						break;
					}
				}
				if(freeSpaceFound){
					break levelFound;
				}
    		}
			
			int yTop = (int) (0 - position.level * Math.round(verticalOfset * 1.5));
			int yBottom = (int) (parentHeight  - position.level * Math.round(verticalOfset * 1.5));
			prosesedElem.add(position);
			v.layout(position.x, 
					yTop, 
					position.x + position.width,
					yBottom);
		}
	}

}
