package nebulous.tetris;

public abstract class Player
{
	public int x = 0;
	public int y = 0;
	public Tetromino currentShape;
	public Tetromino nextShape;
	
	public void init(Tetris tetris)
	{
		x = 0;
		y = 0;
		this.currentShape = Shapes.randomShape();
		this.nextShape = Shapes.randomShape();
	}
	
	public abstract void onNextPiece(Tetris tetris);
	public abstract void onUpdate(Tetris tetris);
	
	public final void nextShape()
	{
		this.currentShape = nextShape;
		this.nextShape = Shapes.randomShape();
	}
	
	public final void resetCenter(Map map)
	{
		x = map.getWidth() / 2 - currentShape.GetWidth() / 2;
		y = 0;
	}
}
