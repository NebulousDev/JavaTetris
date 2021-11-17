package nebulous.tetris;

import java.util.ArrayList;

import nebulous.tetris.Tetromino.Rotation;

public class AIPlayer extends Player
{
	class MoveTarget
	{
		public int x;
		public int y;
		public Rotation rot;
	}
	
	private ArrayList<Integer> heights;
	private ArrayList<Integer> tempHeights;
	private MoveTarget target;
	
	@Override
	public void init(Tetris tetris)
	{
		super.init(tetris);
		
		int size = tetris.getMap().getWidth();
		heights = new ArrayList<Integer>(size);
		tempHeights = new ArrayList<Integer>(size);
		
		for (int i = 0; i < size; i++)
		{ 
			heights.add(0); 
			tempHeights.add(0);
		}
	}
	
	@Override
	public void onNextPiece(Tetris tetris)
	{	
		calculateHeights(heights, tetris.getMap());
		target = bestMove(tetris.getMap());
	}

	@Override
	public void onUpdate(Tetris tetris)
	{
		while(currentShape.getRotation() != target.rot)
		{
			currentShape.rotateClockwise();
		}
		
		if(x < target.x)
		{
			while(x != target.x)
			{
				x++;				
			}
		}
		
		else if(x > target.x)
		{
			while(x != target.x)
			{
				x--;				
			}
		}
	}
	
	private void calculateHeights(ArrayList<Integer> heights, Map map)
	{
		for(int x = 0; x < map.getWidth(); x++)
		{
			heights.set(x, 0);
			
			for(int y = 0; y < map.getHeight(); y++)
			{
				if(map.check(x, y))
				{
					heights.set(x, map.getHeight() - y);
					break;
				}
			}
		}
	}
	
	private MoveTarget bestMove(Map map)
	{
		MoveTarget target = new MoveTarget();
		Tetromino shape = currentShape.copy();
		
		target.rot = Rotation.NONE;
		target.x = 0;
		target.y = 0;
		
		double bestScore = -10000000000.0;
				
		for(int r = 0; r < Rotation.values().length; r++)
		{
			for(int x = -shape.GetWidth(); x < map.getWidth(); x++)
			{
				for(int y = shape.GetHeight(); y > 0; y--)
				{
					int checkHeight = 0;
					
					if(x < 0)
					{
						checkHeight = map.getHeight() - heights.get(0) - y;
					}
					else
					{
						checkHeight = map.getHeight() - heights.get(x) - y;
					}
					
					if(checkHeight < 0)
					{
						continue;
					}
					
					if(!map.collides(x, checkHeight, shape))
					{
						double score = getMapScore(map, x, checkHeight, shape);
						
						if(score > bestScore)
						{
							target.x = x;
							target.y = checkHeight;
							target.rot = Rotation.values()[r];
							bestScore = score;
						}
					}
				}
			}
			
			shape.rotateClockwise();
		}
		
		return target;
	}
	
	public int calculateHoles(Map map)
	{
		int holes = 0;
		
		for(int x = 0; x < map.getWidth(); x++)
		{
			for(int y = 0; y < tempHeights.get(x); y++)
			{
				if (!map.check(x, map.getHeight() - y))
				{
					holes++;
				}
			}
		}
		
		return holes;
	}
	
	public int calculateLines(Map map)
	{
		int lines = 0;
		
		for(int y = 0; y < map.getHeight(); y++)
		{
			if(map.isLineFilled(y))
			{
				lines++;
			}
		}
		
		return lines;
	}
	
	// Return the heuristic score of the board
	// Assumes the shape is not colliding
	public double getMapScore(Map map, int playerX, int playerY, Tetromino shape)
	{
		int aggregateHeight = 0;
		int bumpiness = 0;
		int holes = 0;
		int lines = 0;
		
		map.place(playerX, playerY, shape);
		
		calculateHeights(tempHeights, map);
		
		for(int i = 0; i < tempHeights.size(); i++)
		{
			aggregateHeight += tempHeights.get(i);
		}
		
		for(int i = 0; i < tempHeights.size() - 1; i++)
		{
			bumpiness += Math.abs(tempHeights.get(i) - tempHeights.get(i + 1));
		}
		
		holes = calculateHoles(map);
		lines = calculateLines(map);

		map.remove(playerX, playerY, shape);
		
		//https://codemyroad.wordpress.com/2013/04/14/tetris-ai-the-near-perfect-player/
		return (-0.510066 * aggregateHeight) + (-0.184483 * bumpiness) + (-0.35663 * holes) + (8.760666 * lines);
	}

}
