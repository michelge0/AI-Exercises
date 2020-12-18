package maze_runner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.Timer;

public class Display extends JPanel
{
	enum BoxState { EMPTY, BLOCKADE, GHOST, PLAYER, GOAL }

	static Maze grid;
	static Display display;
	
	static Entity player;
	
	static JTextPane highScoreDisplay, pointsDisplay;
	static int highScore = 0, points = 0;
	
	static boolean gameOver = false;
	
	//static Grid grid
	static final int COLS = 40, ROWS = 40;
	static final int GRID_WIDTH = 800, GRID_HEIGHT = 800;
	
	//static Grid resetState;

	public static void main(String[] args) {
		newGame();
		
		JPanel bottomPane = new JPanel();
			bottomPane.setBackground(new Color(180, 180, 180));
			JLabel pts = new JLabel("Points:");
				bottomPane.add(pts);
			pointsDisplay = new JTextPane();
				pointsDisplay.setEditable(false);
				bottomPane.add(pointsDisplay);
				
		JPanel topPane = new JPanel();
			topPane.setBackground(new Color(180, 180, 180));
			JLabel hs = new JLabel("High Score:");
				topPane.add(hs);
			highScoreDisplay = new JTextPane();
				highScoreDisplay.setEditable(false);
				highScoreDisplay.setText("0");
				topPane.add(highScoreDisplay);
				
		JFrame window = new JFrame("Move the Box");
			window.setLocation(100, 30);
			window.setSize(1000, 1000);
			window.setResizable(false);
			window.setVisible(true);
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.setLayout(new BorderLayout());
			
			window.add(display, BorderLayout.CENTER);
			window.add(bottomPane, BorderLayout.SOUTH);
			window.add(topPane, BorderLayout.NORTH);
			
		display.init();
	}
	
	private static void newGame() {
		grid = new Maze(COLS, ROWS);
		grid.newGoals();
		
		display = new Display();
		
		player = new Player(5, ROWS - 1);
		new Ghost(COLS / 2, 0, (int) (Math.random() * 10 + 15));
		new Ghost(COLS - 1, 0, (int) (Math.random() * 15 + 20));
	}
	
	private void init() {
		addMouseListener(new MouseListener() {
			public void mousePressed(MouseEvent evt) {
				requestFocus();
			}
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
		});
		addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent evt) {
				int code = evt.getKeyCode();
				switch (code) {
				case KeyEvent.VK_UP: player.setDirection(Entity.UP); break;
				case KeyEvent.VK_DOWN: player.setDirection(Entity.DOWN); break;
				case KeyEvent.VK_RIGHT: player.setDirection(Entity.RIGHT); break;
				case KeyEvent.VK_LEFT: player.setDirection(Entity.LEFT); break;
				}
				player.move();
				repaint();
				player.setDirection(Entity.NONE);
			}
			
			public void keyReleased(KeyEvent evt) {}
			public void keyTyped(KeyEvent evt) {}
		});
		
		Timer actionTimer = new Timer(130, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (gameOver) {
					pointsDisplay.setText("GAME OVER");
					if (points > highScore) {
						highScore = points;
						highScoreDisplay.setText(highScore + "");
					}
					points = 0;
					
					Ghost.clear();
					gameOver = false;
					newGame();
				}
				
				pointsDisplay.setText(points + "");
				
				if (Math.random() > .985) grid.newGoals();
								
				Ghost.moveGhosts();
				
				repaint();
			}
		});
		actionTimer.start();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int width = getWidth(), height = getHeight();
		
		int gridX = (width - GRID_WIDTH) / 2, gridY = (height - GRID_HEIGHT) / 2;
		
		// draw grid with margin
		g.drawRect(gridX, gridY, GRID_WIDTH, GRID_HEIGHT);
		int boxWidth = GRID_WIDTH / COLS;
		int boxHeight = GRID_HEIGHT / ROWS;
		
		for (int i = 0; i < COLS; i++) {
			g.drawLine(gridX + i * boxWidth, gridY, gridX + i * boxWidth, gridY + GRID_HEIGHT);
		}
		for (int i = 0; i < ROWS; i++) {
			g.drawLine(gridX, gridY + i * boxHeight, gridX + GRID_WIDTH, gridY + i * boxHeight);
		}
		
		// color grid
		for (int i = 0; i < grid.getRows(); i++) {
			for (int j = 0; j < grid.getCols(i); j++) {
				switch (grid.getBox(i, j)) {
				case BLOCKADE: g.setColor(Color.BLACK); break;
				case GHOST: g.setColor(Color.RED); break;
				case PLAYER: g.setColor(Color.GREEN); break;
				case GOAL: g.setColor(Color.YELLOW); break;
				default: g.setColor(getBackground()); break;
				}
				g.fillRect(gridX + i * boxWidth + 1, gridY + j * boxHeight + 1, boxWidth - 1, boxHeight - 1);
			}
		}
	}
}
