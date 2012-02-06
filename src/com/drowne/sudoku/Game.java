package com.drowne.sudoku;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


public class Game extends Activity implements Runnable {

	private PuzzleView puzzleView;
	private int puzzle[] = new int[9*9];

	// default sudoku
	private final String currentSudoku = "000000000000000000000000000000000000000000000000000000000000000000000000000000000";
	private final String emptySudoku = "000000000000000000000000000000000000000000000000000000000000000000000000000000000";
	private final int used[][][] = new int[9][9][];
    
	// backtrack counter
	private int backtrack;
    private boolean first;
    private ProgressDialog pd;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);   
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
                                WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		
		puzzle = getPuzzle();
		calculateUsedTiles();
		
		puzzleView = new PuzzleView(this);
		setContentView(puzzleView);
		puzzleView.requestFocus();
		
		this.setResult(RESULT_OK);
		first=true;

	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

    	super.onCreateOptionsMenu(menu);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);

    	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    	switch(item.getItemId()) {

    	case R.id.solve:

    		pd = ProgressDialog.show(this, "Sudoku Solver", "Smart Solver...", true, false);

    		Thread thread = new Thread(this);
    		thread.start();

    		return true;

    	case R.id.restart:
    		puzzle = getEmptyPuzzle();
    		puzzleView.invalidate();
    		calculateUsedTiles();
    		return true;

    	case R.id.exit:
    		this.setResult(RESULT_CANCELED);
    		finish();
    		return true;

    	}

    	return false;
    }

    private boolean solveSudoku() {

    	puzzle = getActualPuzzle();

        if (!is_Solved()) {
		   // if sudoku wasn't solved, use backtrack algorithm
		   return backtrack(0, 0);
	   }

      return false;
  }

   private boolean backtrack(int i, int j) {

	   // limits
	   if (i == 9) {
            i = 0;
            if (++j == 9)
                return true;

        }

	   // if cell is empty, ignore it
        if (getTile(i, j) != 0) {
        	return backtrack(i+1, j);
        } else {
        	// if this is the first cell to fill, save the coords
        	if(first) {
        		calculateUsedTiles();
        		first=false;

        	}

        }

        for(int x=1; x <= 9; x++) {

        	calculateUsedTiles(i, j);
        	if(setTileIfValid(i, j, x)) {

 					if (backtrack(i + 1, j)) {
						return true;
				}
        	}
        }

        // backtrack
        backtrack++;
        setTile(i, j, 0);

        handler.sendEmptyMessage(backtrack);

        return false;

   }

	private boolean is_Solved() {
		int g[] = getActualPuzzle();
		int nused = 0;
		for(int t : g) {
			if(t==0) nused++;
		}

		if(nused==0) return true;
		else return false;

	}

	// keypad
	protected void showKeypadOrError(int x, int y) {
		int tiles[] = getUsedTiles(x, y);
		if(tiles.length == 9) {
			Toast toast = Toast.makeText(this, R.string.no_moves_label, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		} else {
			Dialog v = new Keypad(this, tiles, puzzleView);
			v.setTitle(R.string.keypad_title);
			v.show();
		}
	}

	// sets the value of the cell only it is valid
	protected boolean setTileIfValid(int x, int y, int value) {

		int tiles[] = getUsedTiles(x, y);
		if(value != 0) {
			for(int tile : tiles) {
				if(tile == value) return false;
			}
		}
		setTile(x, y, value);
		calculateUsedTiles();
		return true;
	}

	protected int[] getUsedTiles(int x, int y) {
		return used[x][y];
	}

	private void calculateUsedTiles() {
		for(int x=0; x<9; x++) {
			for(int y=0; y<9; y++) {
				used[x][y] = calculateUsedTiles(x, y);

			}
		}
	}

	private int[] calculateUsedTiles(int x, int y) {
		int c[] = new int[9];

		// hotizontal
		for(int i=0; i<9; i++) {
			if(i == y) continue;
			int t = getTile(x, i);
			if(t!=0) c[t-1] = t;
		}

		// vertical
		for(int i=0; i<9; i++) {
			if(i==x) continue;
			int t = getTile(i, y);
			if(t!=0) c[t-1] = t;

		}

		// 3x3 block
		int startx = (x/3) *3;
		int starty = (y/3) *3;

		for(int i = startx; i<startx+3; i++) {
			for(int j = starty; j< starty+3; j++) {
				if(i==x && j==y) continue;
				int t = getTile(i, j);
				if(t!=0) c[t-1]= t;
			}
		}

		int nused = 0;
		for(int t : c) {
			if(t!=0) nused++;
		}

		int c1[] = new int[nused];
		nused = 0;
		for (int t : c) {
			if(t!=0) c1[nused++] = t;
		}

		return c1;
	}

	private int getTile(int x, int y) {
		return puzzle[y*9 + x];
	}

	private void setTile( int x, int y, int value) {
		puzzle[y*9 + x] = value;
		calculateUsedTiles();
	}

	private int[] getPuzzle() {
		return fromPuzzleString(currentSudoku);
	}

	private int[] getActualPuzzle() {
		return puzzle;
	}

	private int[] getEmptyPuzzle() {
		return fromPuzzleString(emptySudoku);
	}

	private int[] fromPuzzleString(String string) {
		int[] puz = new int[string.length()];
		for(int i=0; i< puz.length; i++) {
			puz[i] = string.charAt(i) - '0';
		}
		return puz;
	}

	static private String toPuzzleString(int[] puz) {
		StringBuilder buf = new StringBuilder();
		for(int element : puz) {
			buf.append(element);
		}
		return buf.toString();
	}

	protected String getTileString( int x, int y) {
		int v = getTile(x, y);
		if( v==0 ) return "";
		else return String.valueOf(v);
	}

	private void showToastShort(String p) {
		Toast toast = Toast.makeText(this, p, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if(msg.what==0) {
				pd.dismiss();
				puzzleView.invalidate();
				if( is_Solved() ) showToastShort( getString(R.string.result) +" "+backtrack);
				else showToastShort( getString(R.string.noresult) +" "+backtrack);
			} else
				pd.setMessage("backtracking: "+backtrack);
		}
	};

	@Override
	public void run() {

		Looper.prepare();

		backtrack = 0;
		solveSudoku();

		handler.sendEmptyMessage(0);

	}



}


