package com.drowne.sudoku;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;


public class Main extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);   
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
                                WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        
        setContentView(R.layout.main);
        
        setClickListeners();
        
    }
    
    private void setClickListeners() {
        View newButton = this.findViewById(R.id.new_button);
        newButton.setOnClickListener(this);
        View aboutButton = this.findViewById(R.id.about_button);
        aboutButton.setOnClickListener(this);
        View exitButton = this.findViewById(R.id.exit_button);
        exitButton.setOnClickListener(this);		
	}

	public void onClick(View V) {
    	
    	switch(V.getId()) {
    	
    	// caso about
    	case R.id.about_button:
    		startActivity(new Intent(this, About.class));
    		break;
    	// caso exit
    	case R.id.exit_button:
    		finish();
    		break;
    	// si gioca
    	case R.id.new_button:
    		startActivityForResult(new Intent(this, Game.class), 123456789);
    		break;

    	}

    }
    
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
    	switch(requestCode){
    		case 123456789:
    				if(resultCode==RESULT_CANCELED)	finish();
    		default:
    			break;
    	}
    }

    
}