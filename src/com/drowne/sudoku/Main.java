package com.drowne.sudoku;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Main extends Activity implements OnClickListener {
    
	private Button solveButton, aboutButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        registerViews();
        setupClickListeners();
        
    }

	private void setupClickListeners() {
		solveButton.setOnClickListener(this);
		aboutButton.setOnClickListener(this);
		
	}

	private void registerViews() {
		solveButton = (Button) findViewById(R.id.solve);
		aboutButton = (Button) findViewById(R.id.about);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.solve:
			break;
			
		case R.id.about:
			break;
		}
	}
}