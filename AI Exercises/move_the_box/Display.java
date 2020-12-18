package move_the_box;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Display extends JPanel
{
	static Display display;
	
	static Grid grid;
	static final int COLS = 10, ROWS = 10;
	static final int GRID_WIDTH = 500, GRID_HEIGHT = 500;
	static int selectedX, selectedY; // x and y indices of current selected box
	
	enum BoxState { GREEN, RED, BLUE, LIGHT_BROWN, DARK_BROWN, BLACK, EMPTY };
	static BoxState currentEditingState;
	
	static String solutionText;
	static LinkedList<Grid> solutionPath;
	static int solutionPathIndex = 0;
	static Grid resetState;
	static JTextField solutionTextField;

	public static void main(String[] args) {
		grid = new Grid(COLS, ROWS);
		display = new Display();
		
		solutionText = "";
		
		JPanel bottomPane = new JPanel();
			bottomPane.setLayout(new FlowLayout());
			bottomPane.setBackground(new Color(200, 200, 200));
			
			JButton clear = new JButton("Clear All");
				clear.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						grid.clear();
						display.repaint();
					}
				});
				bottomPane.add(clear);
			JButton scan = new JButton("Scan");
				scan.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						grid.scan();
						display.repaint();
					}
				});
				bottomPane.add(scan);
			JButton update = new JButton("Update");
				update.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						grid.updateStep();
						display.repaint();
					}
				});
				bottomPane.add(update);
			JButton updateAll = new JButton("Update All");
				updateAll.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						grid.updateAll();
						display.repaint();
					}
				});
				bottomPane.add(updateAll);
				
			// Color editor
			bottomPane.add(new JLabel ("Box Type: "));
			JComboBox colorChanger = new JComboBox<String>(new String[] { "Green", "Red", "Blue", "Light Brown", "Dark Brown", "Black", "Empty" });
				colorChanger.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						JComboBox src = (JComboBox)evt.getSource();
						String state = (String)src.getSelectedItem();
						
						if (state.equals("Green")) currentEditingState = BoxState.GREEN;
						else if (state.equals("Red")) currentEditingState = BoxState.RED;
						else if (state.equals("Blue")) currentEditingState = BoxState.BLUE;
						else if (state.equals("Light Brown")) currentEditingState = BoxState.LIGHT_BROWN;
						else if (state.equals("Dark Brown")) currentEditingState = BoxState.DARK_BROWN;
						else if (state.equals("Black")) currentEditingState = BoxState.BLACK;
						else if (state.equals("Empty")) currentEditingState = BoxState.EMPTY;
					}
				});
				bottomPane.add(colorChanger);
		
		JPanel topPane = new JPanel();
			topPane.setLayout(new FlowLayout());
			topPane.setBackground(new Color(210, 210, 210));
			
			solutionTextField = new JTextField("Move the Box Solver");
				solutionTextField.setEditable(false);
				topPane.add(solutionTextField);
			JButton findSol = new JButton("Find Solution");
				findSol.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						Node root = new Node(grid, null, null, 1);
						resetState = grid.clone();
						root.findSolution();
						
						solutionTextField.setText(solutionText);
						solutionPathIndex = 0;
						
						setGrid(resetState);
						display.repaint();
					}
				});
				topPane.add(findSol);
			JButton next = new JButton("Next");
				next.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						try {
							if (solutionPathIndex < solutionPath.size()) solutionPathIndex++;
							setGrid(solutionPath.get(solutionPathIndex));
						} catch (Exception e) {}
						display.repaint();
					}
				});
				topPane.add(next);
			JButton prev = new JButton("Prev");
				prev.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						try {
							if (solutionPathIndex >= 0) solutionPathIndex--;
							setGrid(solutionPath.get(solutionPathIndex));
						} catch (Exception e) { if (resetState != null) setGrid(resetState); }
						display.repaint();
					}
				});
				topPane.add(prev);
			topPane.add(new JLabel("Moves: "));
			JSlider moves = new JSlider(1, 5);
				moves.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent evt) {
						JSlider src = (JSlider) evt.getSource();
						Node.searchLimit = src.getValue();
						System.out.println(src.getValue());
						System.out.println("node" + Node.searchLimit);
                    }
					
				});
				moves.setMajorTickSpacing(1);
				moves.setPaintTicks(true);
				moves.setPaintLabels(true);
				topPane.add(moves);
				
		JFrame window = new JFrame("Move the Box");
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
	}
	
	public void init() {
		addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent evt) {
				grid.setBox(selectedX, selectedY, currentEditingState);
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
			
			// modified version of mouseMoved
			public void mouseDragged(MouseEvent evt) {
	            int yOnGrid = evt.getY() - (getHeight() - GRID_HEIGHT) / 2;
	            int xOnGrid = evt.getX() - (getWidth() - GRID_WIDTH) / 2;
	            int row = yOnGrid / (GRID_HEIGHT / ROWS), col = xOnGrid / (GRID_WIDTH / COLS);
	            try {
            		if (row < 0 || row > ROWS - 1 || col < 0 || col > COLS - 1) return;
            		grid.setBox(col, row, currentEditingState);
            		repaint();
	            } catch (IndexOutOfBoundsException e) {}
			}
		});
		currentEditingState = BoxState.EMPTY;
	}
	
	public static void setSolutionPath(LinkedList<Grid> path) {
		solutionPath = path;
	}
	
	public static void setGrid(Grid newGrid) {
		grid = newGrid;
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
				case GREEN: g.setColor(Color.GREEN); break;
				case RED: g.setColor(Color.RED); break;
				case BLUE: g.setColor(Color.BLUE); break;
				case LIGHT_BROWN: g.setColor(new Color(170, 120, 50)); break;
				case DARK_BROWN: g.setColor(new Color(120, 75, 0)); break;
				case BLACK: g.setColor(Color.BLACK); break;
				case EMPTY: g.setColor(getBackground()); break;
				}
				
				g.fillRect(gridX + i * boxWidth + 1, gridY + j * boxHeight + 1, boxWidth - 1, boxHeight - 1);
			}
		}
		
		// color selected box
		g.setColor(Color.YELLOW);
		g.drawRect(gridX + selectedX * boxWidth, gridY + selectedY * boxHeight, boxWidth, boxHeight);
	}
}
