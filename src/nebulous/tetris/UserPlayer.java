package nebulous.tetris;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class UserPlayer extends Player implements KeyListener
{	
	private Map map;
	private boolean noReset; // fixes a bug when resetting
	
	UserPlayer()
	{
		noReset = false;
	}
	
	@Override
	public void init(Tetris tetris)
	{
		super.init(tetris);
		this.map = tetris.getMap();
		
		if(!noReset)
		{
			tetris.getCanvas().addKeyListener(this);
			noReset = true;
		}
	}
	
	@Override
	public void onNextPiece(Tetris tetris)
	{
		// Do nothing
	}
	
	@Override
	public void onUpdate(Tetris tetris)
	{
		// Do nothing
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		// Move Right
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
		{
			if(!map.collides(x + 1, y, currentShape))
			{
				x += 1;
			}
		}
		
		// Move Left
		if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
		{
			if(!map.collides(x - 1, y, currentShape))
			{
				x -= 1;					
			}
		}
		
		// Move Down
		if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S)
		{
			if(!map.collides(x, y + 1, currentShape))
			{
				y += 1;
			}
		}
		
//		// Rotate Left
//		if(e.getKeyCode() == KeyEvent.VK_Q)
//		{
//			currentShape.rotateClockwise();
//			
//			if(map.collides(x, y, currentShape))
//			{		
//				currentShape.rotateCounterClockwise();
//			}
//		}
		
		// Rotate Right
		//if(e.getKeyCode() == KeyEvent.VK_E)
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W)
		{
			currentShape.rotateCounterClockwise();
			
			if(map.collides(x, y, currentShape))
			{
				currentShape.rotateClockwise();
			}
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}
}
