package com.drowne.sudoku;

import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.view.*;



public class PuzzleView extends View {

	private final 	Game game;
	private float 	width;
	private float 	height;
	private int 	selX;
	private int 	selY;
	private final 	Rect selRect = new Rect();
	
	
	public PuzzleView(Context context) {
		super(context);
		this.game = (Game) context;
		setFocusable(true);
		setFocusableInTouchMode(true);
	}
	
	@Override
	protected void onSizeChanged( int w, int h, int oldw, int oldh) {
		
		width = w / 9f;
		height = h / 9f;
		getRect(selX, selY, selRect);
		super.onSizeChanged(w, h, oldw, oldh);	
		
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		// disegna lo sfondo
		Paint background = new Paint();
		background.setColor(getResources().getColor(R.color.puzzle_background));
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		
		//Impostiamo i colori per la griglia
		Paint dark = new Paint();
		dark.setColor(getResources().getColor(R.color.puzzle_dark));
		
		Paint hilite = new Paint();
		hilite.setColor(getResources().getColor(R.color.puzzle_hilite));
		
		Paint light = new Paint();
		light.setColor(getResources().getColor(R.color.puzzle_light));
		
		// disegna le righe sottili
		for(int i=0; i<9; i++) {
			canvas.drawLine(0, i*height, getWidth(), i*height, light);
			canvas.drawLine(0, i*height+1, getWidth(), i*height+1, hilite);
			
			canvas.drawLine(i*width, 0, i*width, getHeight(), light);
			canvas.drawLine(i*width+1, 0, i*width+1, getHeight(), hilite);
		}
		
		// disegna le righe spesse
		for(int i=0; i<9; i++) {
			if(i%3 != 0) continue;
			
			canvas.drawLine(0, i*height, getWidth(), i*height, dark);
			canvas.drawLine(0, i*height+1, getWidth(), i*height+1, hilite);
			
			canvas.drawLine(i*width, 0, i*width, getHeight(), dark);
			canvas.drawLine(i*width+1, 0, i*width+1, getHeight(), hilite);
			
		}
		
		// definiamo lo stile per disegnare i numeri
		Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
		foreground.setColor(getResources().getColor(R.color.puzzle_foreground));
		foreground.setStyle(Style.FILL);
		foreground.setTextSize(height * 0.75f);
		foreground.setTextScaleX(width / height);
		foreground.setTextAlign(Paint.Align.CENTER);
		
		// disegna i numeri al centro
		FontMetrics fm = foreground.getFontMetrics();
		float x = width / 2;
		float y = height / 2 - (fm.ascent + fm.descent) / 2;
		
		for(int i=0; i<9; i++) {
			for(int j=0; j<9; j++) {
				canvas.drawText(this.game.getTileString(i, j), i*width +x, j*height +y, foreground);
			}
		}
		
		// disegna il rettangolino di selezione
      Paint selected = new Paint();
      selected.setColor(getResources().getColor(
            R.color.puzzle_selected));
      canvas.drawRect(selRect, selected);
		
	}
	
	
	// sposta il rettangolo di selezione
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode) {
		
		// casi di movimento
		case KeyEvent.KEYCODE_DPAD_UP:
			select(selX, selY-1);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			select(selX, selY+1);
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			select(selX-1, selY);
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			select(selX+1, selY);
			break;
			
		//casi di inserimento
			
		case KeyEvent.KEYCODE_0:
		case KeyEvent.KEYCODE_SPACE: 	setSelectedTile(0); break;
		case KeyEvent.KEYCODE_1:		setSelectedTile(1); break;
		case KeyEvent.KEYCODE_2:		setSelectedTile(2); break;
		case KeyEvent.KEYCODE_3:		setSelectedTile(3); break;
		case KeyEvent.KEYCODE_4:		setSelectedTile(4); break;
		case KeyEvent.KEYCODE_5:		setSelectedTile(5); break;
		case KeyEvent.KEYCODE_6:		setSelectedTile(6); break;
		case KeyEvent.KEYCODE_7:		setSelectedTile(7); break;
		case KeyEvent.KEYCODE_8:		setSelectedTile(8); break;
		case KeyEvent.KEYCODE_9:		setSelectedTile(9); break;
		case KeyEvent.KEYCODE_ENTER:	
		case KeyEvent.KEYCODE_DPAD_CENTER:
			game.showKeypadOrError(selX, selY);
			break;		
			
			default:
				return super.onKeyDown(keyCode, event);
		}
		
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
		
		select((int)(event.getX() / width), (int)(event.getY() / height));
		game.showKeypadOrError(selX, selY);
		return true;
	}
	
	// select calcola il rettangolo selezionato dal cursore
	protected void select(int x, int y) {
		invalidate(selRect);
		selX = Math.min(Math.max(x, 0), 8);
		selY = Math.min(Math.max(y, 0), 8);
		getRect(selX, selY, selRect);
		invalidate(selRect);
		
	}

	private void getRect(int x, int y, Rect rect) {
		rect.set((int)(x*width), (int)(y*height), (int)(x*width + width), (int)(y*height+height));
	}
	
	public void setSelectedTile(int tile) {
		if(game.setTileIfValid(selX, selY, tile)) {
			// colora la cella selezionata di un colore diverso
			//*** al momento non implementata ***
			//Paint colorTileInserted = new Paint();
			//colorTileInserted.setColor(getResources().getColor(R.color.tile_selected));
			//c.drawRect(selRect, colorTileInserted);
			invalidate(selRect);
		} else {
			//numero non valido, scuoti lo schermo
			//*** al momento non implementata ***
			//startAnimation(AnimationUtils.loadAnimation(game, R.anim.shake));
			
		}
		}
}
	

