package tic_tac_toe_plus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import tic_tac_toe_plus.Square.SquareState;

public class Display extends JPanel
{	
	static State grid;
	static Display display;
	static JTextPane masterText;
	static JTextPane pointText;
	
	//static Grid grid;
	static final int COLS = 3, ROWS = 3;
	static final int GRID_WIDTH = 500, GRID_HEIGHT = 500;
	static int selectedX, selectedY; // x and y indices of current selected box
	
	//static Grid resetState;

	public static void main(String[] args) {
		Square[][] tempBoard = new Square[COLS][ROWS];
		for (int i = 0; i < tempBoard.length; i++) {
			for (int j = 0; j < tempBoard[i].length; j++) {
				tempBoard[i][j] = new Square();
			}
		}
		grid = new State(tempBoard);
		
		display = new Display();
		
		JPanel bottomPane = new JPanel();
			bottomPane.setLayout(new FlowLayout());
			bottomPane.setBackground(new Color(200, 200, 200));
			
			pointText = new JTextPane();
				pointText.setEditable(false);
				bottomPane.add(pointText);
			
		JPanel topPane = new JPanel();
			topPane.setLayout(new FlowLayout());
			topPane.setBackground(new Color(200, 200, 200));
		
			masterText = new JTextPane();
				masterText.setEditable(false);
				topPane.add(masterText);
			JButton reset = new JButton("Restart");
				reset.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						grid.restart();
						playerPoints = 0;
						computerPoints = 0;
						gameOver = false;
						
						if (Math.random() > 0.5) {
							playerStart = true;
							playerTurn = true;
						}
						else {
							playerStart = false;
							computerMove();
						}	
					}
				});
				topPane.add(reset);
		
		JFrame window = new JFrame("Tic Tac Toe Plus");
			window.setLocation(100, 30);
			window.setSize(800, 800);
			window.setResizable(false);
			window.setVisible(true);
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.setLayout(new BorderLayout());
			
			window.add(display, BorderLayout.CENTER);
			window.add(bottomPane, BorderLayout.SOUTH);
			window.add(topPane, BorderLayout.NORTH);
		
		display.init();
		
		if (Math.random() > 0.5) {
			playerStart = true;
			playerTurn = true;
		}
		else {
			playerStart = false;
			computerMove();
		}	
	}
	
	public void init() {
		addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent evt) {
				if (playerTurn) {
					grid.check(selectedX, selectedY, SquareState.O);
					playerTurn = false;
					
					refreshPoints();
					display.repaint();
					
					System.out.println(grid.getString());
					
					if (grid.gameOver()) {
						if (playerPoints > computerPoints) masterText.setText("You win!");
						else if (computerPoints > playerPoints) masterText.setText("You lose!");
						else masterText.setText("Tie!");
					}
					else computerMove();
				}
            }

			public void mouseEntered(MouseEvent evt) {}
			public void mouseExited(MouseEvent evt) {}
			public void mousePressed(MouseEvent evt) {}
			public void mouseReleased(MouseEvent evt) {}
		});
		addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent evt) {
				// x and y positions without margin
	            int yOnGrid = evt.getY() - (getHeight() - GRID_HEIGHT) / 2;
	            int xOnGrid = evt.getX() - (getWidth() - GRID_WIDTH) / 2;
	            // divides by box width (which equals grid height / cols etc.)
	            int row = yOnGrid / (GRID_HEIGHT / ROWS), col = xOnGrid / (GRID_WIDTH / COLS);
	            try {
            		// if out of bounds
            		if (row < 0 || row > ROWS - 1 || col < 0 || col > COLS - 1) return;
            		selectedX = col; selectedY = row;
            		repaint();
	            } catch (IndexOutOfBoundsException e) {}
			}
			public void mouseDragged(MouseEvent arg0) {}	
		});
		
		grid.restart();
		display.repaint();
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
		for (int i = 0; i < grid.board.length; i++) {
			for (int j = 0; j < grid.board[i].length; j++) {
				switch (grid.board[i][j].currentState) {
				case O: g.setColor(Color.GREEN); break;
				case X: g.setColor(Color.RED); break;
				default: g.setColor(getBackground()); break;
				}
				g.fillRect(gridX + i * boxWidth + 1, gridY + j * boxHeight + 1, boxWidth - 1, boxHeight - 1);
				
				// point values:
				g.setFont(new Font("Serif", Font.BOLD, 30));
				g.setColor(Color.BLACK);
				g.drawString(grid.board[i][j].points + "", gridX + i * boxWidth + boxWidth / 3, gridY + j * boxHeight + boxHeight / 3);
			}
		}
		
		// color selected box
		g.setColor(Color.YELLOW);
		g.drawRect(gridX + selectedX * boxWidth, gridY + selectedY * boxHeight, boxWidth, boxHeight);
	}
	
	/***********************
	 * GAME CONTROL
	 */
		
	static boolean playerTurn;
	static boolean playerStart;		// whoever starts has -3 point handicap
	static boolean gameOver = false;
	
	static int playerPoints, computerPoints;
	
	static void refreshPoints() {
		if (playerStart) {
			playerPoints = -3; computerPoints = 0;
		}
		else {
			playerPoints = 0; computerPoints = -3;
		}
		for (Square[] sArr : grid.board) {
			for (Square s : sArr) {
				if (s.currentState == SquareState.X) {
					computerPoints += s.points;
				}
				else if (s.currentState == SquareState.O) {
					playerPoints += s.points;
				}
			}
		}
		if (grid.win(SquareState.X)) computerPoints += 5;
		else if (grid.win(SquareState.O)) playerPoints += 5;
		
		pointText.setText("Player: " + playerPoints + " Computer: " + computerPoints);
	}
	
	static void computerMove() {
		System.out.println("thinking");
		// initialize queue:
		LinkedList<State> possibleMoves = grid.expand(SquareState.X);
	
		State decision = possibleMoves.getFirst();
		int minValue = -1000;
		
		// trying to maximize the minumum possible value of s
		for (State s : possibleMoves) {
			// initialize alpha and beta to ridiculously low/high values, respectively
			int sMax = s.minValue(-1000, 1000);
			if (sMax > minValue) {
				decision = s;
				minValue = sMax;
			}
		}
		
		grid = decision;
		refreshPoints();
		display.repaint();
		
		if (grid.gameOver()) {
			if (playerPoints > computerPoints) masterText.setText("You win!");
			else if (computerPoints > playerPoints) masterText.setText("You lose!");
			else masterText.setText("Tie!");
		}
		else playerTurn = true;
	}
}
