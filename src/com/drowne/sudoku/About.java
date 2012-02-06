package com.drowne.sudoku;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

public class About extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
    }
    
	@Override
	public boolean onTouchEvent(MotionEvent event) {
			finish();
		return true;
	}
    
}