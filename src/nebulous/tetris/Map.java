package nebulous.tetris;

import java.awt.Color;
import java.awt.Graphics2D;

public class Map
{
	private int width;
	private int height;
	private Color[][] map;
	
	public Map(int width, int height)
	{
		this.width = width;
		this.height = height;
		this.map = new Color[height][width];
	}
	
	// Attempts to place the block in the map, if it cannot (ie, at the top)
	// then it will return false;
	public boolean place(int posX, int posY, Tetromino shape)
	{
		// If we're at the top, the game is over. Return false
		if(posY == 0)
		{
			return false;
		}
		
		// Get the current rotated shape of the tetromino
		boolean[][] shapeVal = shape.getShape();
		
		for(int y = 0; y < shapeVal.length; y++)
		{
			for(int x = 0; x < shapeVal[0].length; x++)
			{
				if(shapeVal[y][x])
				{					
					map[y + posY][x + posX] = shape.getColor();
				}
			}
		}
		
		return true;
	}
	
	public void remove(int posX, int posY, Tetromino shape)
	{
		// Get the current rotated shape of the tetromino
		boolean[][] shapeVal = shape.getShape();
		
		for(int y = 0; y < shapeVal.length; y++)
		{
			for(int x = 0; x < shapeVal[0].length; x++)
			{
				if(shapeVal[y][x])
				{					
					map[y + posY][x + posX] = null;
				}
			}
		}
	}
	
	public boolean check(int posX, int posY)
	{
		if(!(posX >= 0 && posX < width && posY >= 0 && posY < height))
		{
			return true;
		}
		
		return map[posY][posX] != null;
	}
	
	public boolean collides(int posX, int posY, Tetromino shape)
	{
		// Get the current rotated shape of the tetromino
		boolean[][] shapeVal = shape.getShape();
		
		for(int y = 0; y < shapeVal.length; y++)
		{
			for(int x = 0; x < shapeVal[0].length; x++)
			{
				// If valid space in shape
				if(shapeVal[y][x])
				{
					// Make sure space is in-bounds
					if(!((y + posY < height) && (x + posX >= 0) && (x + posX < width)))
					{
						// Space is out of bounds, collision detected
						return true;
					}
					else
					{
						// Check if space is filled in the map
						if(map[y + posY][x + posX] != null)
						{
							// An overlap has occurred, collision detected
							return true;
						}
					}
				}
			}
		}
		
		// All cases succeeded, no collisions detected
		return false;
	}
	
	public boolean isLineFilled(int y)
	{
		for(int x = 0; x < width; x++)
		{
			if(map[y][x] == null)
			{
				return false;
			}
		}
		
		return true;
	}
	
	public long evalTetris(boolean test, int mapX, int mapY, int blockSize, Graphics2D graphics)
	{
		long score = 0;
		
		// Check every row
		for(int y = 0; y < height; y++)
		{
			if(isLineFilled(y))
			{
				if(!test)
				{
					// Flash the screen with a white rectangle over the tetris
					graphics.setColor(Color.WHITE);
					graphics.fillRect(mapX, y, mapX + width * blockSize, y + blockSize);
					
					for(int y2 = y - 1; y2 > 1; y2--)
					{
						for(int x2 = 0; x2 < width; x2++)
						{
							map[y2 + 1][x2] = map[y2][x2];
						}
					}					
				}
				
				// Increment score
				score += 1000;
			}
		}
		
		return score;
	}
	
	public void drawMap(int originX, int originY, int blockSize, Graphics2D graphics)
	{
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
			{
				if(map[y][x] != null)
				{
					graphics.setColor(map[y][x]);
					graphics.fillRect(
						originX + (x * blockSize), 
						originY + (y * blockSize),
						blockSize, blockSize);
				}
			}
		}
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
}
