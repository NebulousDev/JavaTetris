package nebulous.tetris;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Tetromino
{
	// NOTE: Tetronimos have a standardized rotation pattern:
	// 		 https://tetris.fandom.com/wiki/SRS
	
	public static enum Rotation
	{
		NONE,
		QUARTER,
		HALF,
		THREE_QUARTERS
	}
	
	private ArrayList<boolean[][]> shapes;
	private Rotation rotation;
	private Color color;
	
	// Rotate a 2d array (matrix) by 90 degrees
	// This can be accomplished by reversing then transposing the 2d array
	// Simplified here:
	private static boolean[][] rotateShape(boolean[][] shape)
	{
		boolean[][] result = new boolean[shape[0].length][shape.length];
		
		for(int y = 0; y < shape.length; y++)
		{
			for(int x = 0; x < shape[0].length; x++)
			{
				result[shape[0].length - x - 1][y] = shape[y][x];
			}
		}
	    
		return result;
	}
	
	private Tetromino() {};
	
	public Tetromino(boolean[][] baseShape, Color color)
	{
		this.shapes = new ArrayList<boolean[][]>();
		this.shapes.add(baseShape);
		this.shapes.add(baseShape = rotateShape(baseShape));
		this.shapes.add(baseShape = rotateShape(baseShape));
		this.shapes.add(baseShape = rotateShape(baseShape));
		this.rotation = Rotation.NONE;
		this.color = color;
	}
	
	public void drawShape(int originX, int originY, int blockSize, Graphics2D graphics)
	{
		// Get the current rotated shape of the tetromino
		boolean[][] shape = getShape();
		
		for(int y = 0; y < shape.length; y++)
		{
			for(int x = 0; x < shape[0].length; x++)
			{
				if(shape[y][x])
				{
					graphics.setColor(color);
					graphics.fillRect(
						originX + (x * blockSize), 
						originY + (y * blockSize), 
						blockSize, blockSize);
				}
			}
		}
	}
	
	public void rotateClockwise()
	{
		// Enum int-hacking
		rotation = Rotation.values()[(rotation.ordinal() + 1) % 4];
	}
	
	public void rotateCounterClockwise()
	{
		// Enum int-hacking
		rotation = Rotation.values()[(rotation.ordinal() + 3) % 4];
	}
	
	public boolean[][] getShape()
	{
		// Return the shape based on the ordinal index of the rotation
		return shapes.get(rotation.ordinal());
	}
	
	public int GetWidth()
	{
		return getShape()[0].length;
	}
	
	public int GetHeight()
	{
		return getShape().length;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public Rotation getRotation()
	{
		return rotation;
	}
	
	// This is needed to prevent nextShape and current shape from being identical objects
	// Oh java and you're ambiguous reference copies...
	public Tetromino copy()
	{
		Tetromino res = new Tetromino();
		res.shapes = shapes;
		res.rotation = rotation;
		res.color = color;
		return res;
	}
	
	public void debugPrintShapes()
	{
		for(int i = 0; i < shapes.size(); i++)
		{
			boolean[][] shape = shapes.get(i);
			
			System.out.println(Rotation.values()[i] + ":");
			
			for(int y = 0; y < shape.length; y++)
			{
				for(int x = 0; x < shape[0].length; x++)
				{
					System.out.print(shape[y][x] ? "X" : "_");
				}
				
				System.out.println();
			}
		}
	}

	public void reset()
	{
		rotation = Rotation.NONE;
	}
}
