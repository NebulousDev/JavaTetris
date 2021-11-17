package nebulous.tetris;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

public class Shapes
{
	public static final Tetromino I = new Tetromino
	(
		new boolean[][]
		{ 
			{false,false,false,false}, 
			{true ,true ,true ,true }, 
			{false,false,false,false}, 
			{false,false,false,false}
		}, 
		Color.CYAN
	);
	
	public static final Tetromino J = new Tetromino
	(
		new boolean[][]
		{ 
			{true ,false,false}, 
			{true ,true ,true }, 
			{false,false,false}
		}, 
		Color.BLUE
	);
	
	public static final Tetromino L = new Tetromino
	(
		new boolean[][]
		{ 
			{false,false,true }, 
			{true ,true ,true }, 
			{false,false,false}
		},
		Color.ORANGE
	);
	
	public static final Tetromino O = new Tetromino
	(
		new boolean[][]
		{ 
			{true,true,}, 
			{true,true,} 
		}, 
		Color.YELLOW
	);
	
	public static final Tetromino S = new Tetromino
	(
		new boolean[][]
		{ 
			{false,true, true }, 
			{true ,true ,false}, 
			{false,false,false}
		}, 
		Color.GREEN
	);
	
	public static final Tetromino T = new Tetromino
	(
		new boolean[][]
		{ 
			{false,true, false}, 
			{true ,true ,true }, 
			{false,false,false}
		}, 
		Color.MAGENTA
	);
	
	public static final Tetromino Z = new Tetromino
	(
		new boolean[][]
		{ 
			{true ,true, false}, 
			{false,true ,true }, 
			{false,false,false}
		}, 
		Color.RED
	);
	
	private static ArrayList<Tetromino> allShapes;
	private static ArrayList<Tetromino> bag;
	
	public static void init()
	{
		allShapes = new ArrayList<Tetromino>();
		bag = new ArrayList<Tetromino>();
		allShapes.add(Shapes.I);
		allShapes.add(Shapes.J);
		allShapes.add(Shapes.L);
		allShapes.add(Shapes.O);
		allShapes.add(Shapes.S);
		allShapes.add(Shapes.T);
		allShapes.add(Shapes.Z);
	}
	
	@SuppressWarnings("unchecked")
	public static Tetromino randomShape()
	{
		if(bag.size() < 2)
		{
			bag = (ArrayList<Tetromino>) allShapes.clone();
			Collections.shuffle(bag);
		}
		
		return bag.remove(0).copy();
	}
}
