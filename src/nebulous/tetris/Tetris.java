package nebulous.tetris;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Tetris implements KeyListener
{
	private JFrame window;
	private Canvas canvas;
	private Graphics2D graphics;
	private BufferStrategy backBuffer;
	
	private final int WINDOW_WIDTH = 750;
	private final int WINDOW_HEIGHT = 900;
	private final int MAP_WIDTH = 10;
	private final int MAP_HEIGHT = 20;
	private final int BLOCK_SIZE = 40;
	
	private final int TEXT_X = 20;
	private final int TEXT_Y = 28;
	private final int MAP_START_X = 20;
	private final int MAP_START_Y = 40;	
	private final int PREVIEW_START_X = 440;
	private final int PREVIEW_START_Y = 39;	
	
	private Map map;
	public Player player;
	private int score;
	private static double ups = 1;
	private boolean gameOver;
	
	private static Font font18;
	private static Font font21;
	private static Font font38;
	
	public static void main(String[] args)
	{
		// Setup fonts
		font18 = new Font("Dialog", Font.PLAIN, 18);
		font21 = new Font("Dialog", Font.PLAIN, 21);
		font38 = new Font("Dialog", Font.PLAIN, 38);
		
		JFrame playerSelect = new JFrame("Player Select");
		playerSelect.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		playerSelect.setSize(300, 400);
		
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BorderLayout());
		JLabel titleLabel = new JLabel("Java X Tetris", SwingConstants.CENTER);
		JLabel playerLabel = new JLabel("Select Tetris Player:", SwingConstants.CENTER);
		titleLabel.setFont(font38);
		titleLabel.setBorder(new EmptyBorder(10,0,0,0));
		playerLabel.setBorder(new EmptyBorder(0,0,10,0));
		labelPanel.add(titleLabel, BorderLayout.NORTH);
		labelPanel.add(playerLabel, BorderLayout.SOUTH);
		playerSelect.add(labelPanel);
		
		Button userPlayer = new Button("User Player");
		userPlayer.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Thread gameThread = new Thread(new Runnable()
				{
					
					@Override
					public void run()
					{
						Tetris tetris = new Tetris();
						tetris.player = new UserPlayer();
						tetris.initialize(false);
						tetris.startGame();
					}
				});
				
				gameThread.start();
				playerSelect.dispose();
			}
		});
		
		Button aiPlayer = new Button("AI Player");
		aiPlayer.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Thread gameThread = new Thread(new Runnable()
				{
					
					@Override
					public void run()
					{
						Tetris tetris = new Tetris();
						tetris.player = new AIPlayer();
						tetris.initialize(false);
						tetris.startGame();
					}
				});
				
				gameThread.start();
				playerSelect.dispose();
			}
		});
		
		JPanel upsPane = new JPanel();
		upsPane.setSize(300, 50);
		upsPane.add(new JLabel("Speed:"));
		
		JSlider speed = new JSlider(0, 100, 1);
		speed.setBorder(new EmptyBorder(20,0,0,0));
		speed.setMajorTickSpacing(50);
		speed.setMinorTickSpacing(10);
		speed.setPaintTicks(true);
		speed.setPaintLabels(true);
		speed.setFont(font18);
		
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(1, new JLabel("1"));
		labelTable.put(100, new JLabel("100") );
		speed.setLabelTable( labelTable );
		
		speed.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				ups = ((JSlider)e.getSource()).getValue();
			}
		});
		
		upsPane.add(speed);
		
		GridLayout layout = new GridLayout(0,1);
		playerSelect.setLayout(layout);
		playerSelect.add(userPlayer);
		playerSelect.add(aiPlayer);
		playerSelect.add(upsPane);
		
		playerSelect.setResizable(false);
		playerSelect.setLocationRelativeTo(null);
		
		playerSelect.setVisible(true);
	}
	
	private void initialize(boolean reset)
	{		
		if(!reset)
		{
			// Init frame variables
			window = new JFrame("Tetris");
			canvas = new Canvas();
			
			// Setup window properties
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
			window.setResizable(false);
			window.setLocationRelativeTo(null); // Centers the window
			window.addNotify(); // Generally fixes a bunch of dumb things (like native window hook / peering)
			
			// Setup canvas graphics
			window.add(canvas);
			canvas.setBackground(Color.BLACK);
			canvas.createBufferStrategy(2);
			canvas.setFocusable(true);
			canvas.addKeyListener(this);
			backBuffer = canvas.getBufferStrategy();
			graphics = (Graphics2D) backBuffer.getDrawGraphics();
	
			// Setup Shapes
			Shapes.init();
			
			// Show window
			window.setVisible(true);
			window.requestFocus();
		}
		
		// Setup map
		map = new Map(MAP_WIDTH, MAP_HEIGHT);
		
		// Setup Player
		player.init(this);
		player.resetCenter(map);
		
		// Setup Score
		score = 0;
		
		gameOver = false;
		reset = false;
	}
	
	private void startGame()
	{
		// Initial clear to black
		graphics.clearRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		// Initial update
		player.onNextPiece(this);
		update();
		
		// Game loop timing variables
		long currentTime = System.nanoTime();
		long lastTime = currentTime;
		long updateTime = 0;
		
		// Game Loop
		while(true)
		{
			// Calculate our time deltas
			currentTime = System.nanoTime();
			updateTime += currentTime - lastTime;
			lastTime = currentTime;
			
			// Check if sufficient time has passed to call update
			if(updateTime >= (1.0 / ups) * 1000000000L)
			{
				update();
				updateTime = 0; // Reset update timer
			}
			
			// Render the game
			render();
		}
	}
	
	private void drawGame()
	{
		// Draw title
		graphics.setColor(Color.WHITE);
		graphics.setFont(font18);
		graphics.drawString("JAVA-X-TETRIS", TEXT_X, TEXT_Y);
		graphics.drawString("VERSION 1.3.0", TEXT_X + 567, TEXT_Y);
		
		// Draw map outline
		graphics.setColor(Color.WHITE);
		graphics.drawRect(
			MAP_START_X - 1, MAP_START_Y - 1, 
			MAP_WIDTH * BLOCK_SIZE + 1, MAP_HEIGHT * BLOCK_SIZE + 1);
		
		// Draw map
		map.drawMap(MAP_START_X, MAP_START_Y, BLOCK_SIZE, graphics);
		
		// Draw current shape
		player.currentShape.drawShape(
			MAP_START_X + player.x * BLOCK_SIZE, 
			MAP_START_Y + player.y * BLOCK_SIZE, 
			BLOCK_SIZE, graphics);
		
		// Draw preview
		graphics.setColor(Color.WHITE);
		graphics.drawRect(PREVIEW_START_X, PREVIEW_START_Y, 270, 270);
		graphics.setFont(font21);
		graphics.drawString("PREVIEW", PREVIEW_START_X + 20, PREVIEW_START_Y + 36);
		
		// Draw preview shape
		int previewX = (int) (PREVIEW_START_X + 135 - (player.nextShape.GetWidth() / 2.0) * BLOCK_SIZE);
		int previewY = (int) (PREVIEW_START_Y + 165 - (player.nextShape.GetHeight() / 2.0) * BLOCK_SIZE);
		player.nextShape.drawShape(previewX, previewY, BLOCK_SIZE, graphics);
		
		// Draw score
		graphics.setColor(Color.WHITE);
		graphics.setFont(font38);
		graphics.drawString("SCORE", 510, 400);
		graphics.setColor(Color.CYAN);
		graphics.setFont(font38);
		FontMetrics metrics = graphics.getFontMetrics(font38);
		int scoreX = 576 - (int)(metrics.stringWidth(String.valueOf(score)) / 2.0);
		graphics.drawString(String.valueOf(score), scoreX, 450);
		
		if(gameOver)
		{
			graphics.setColor(Color.WHITE);
			graphics.setFont(font38);
			graphics.drawString("GAME OVER", 465, 525);
			graphics.setFont(font21);
			graphics.drawString("PRESS ENTER", 510, 555);
		}
	}
	
	private void update()
	{
		if(gameOver)
		{
			return;
		}
		
		// Check for collisions
		if(map.collides(player.x, player.y + 1, player.currentShape))
		{
			// Place the piece in the map and check for fail state
			boolean fail = !map.place(player.x, player.y, player.currentShape);
			
			if(fail)
			{
				// Game is over.
				System.out.println("FINAL SCORE: " + score);
				//System.exit(0);
				
				gameOver = true;
				return;
			}
			
			// Reset player
			player.nextShape();
			player.resetCenter(map);
			player.onNextPiece(this);
			
			// Evaluate tetris state and increase score
			score += map.evalTetris(false, MAP_START_X, MAP_START_Y, BLOCK_SIZE, graphics);
			System.out.println("SCORE: " + score);
		}
		else
		{
			// Move the player down each tick
			player.y++;
			
			// Update player logic
			player.onUpdate(this);
		}
	}
	
	public void render()
	{
		// Retrieve buffer strategy and graphics:
		// We need to do this each frame, as sometimes
		// the canvas will change them but not tell us.
		backBuffer = canvas.getBufferStrategy();
		graphics = (Graphics2D) backBuffer.getDrawGraphics();
		
		// Clear the background
		graphics.clearRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		// Draw the game elements
		drawGame();
		
		// Reset graphics for next frame and show the image
		graphics.dispose();
		backBuffer.show();
	}
	
	public Map getMap()
	{
		return map;
	}

	public Canvas getCanvas()
	{
		return canvas;
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			initialize(true);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {}
}
