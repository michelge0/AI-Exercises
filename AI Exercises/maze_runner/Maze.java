package maze_runner;

import java.util.ArrayList;

import maze_runner.Display.BoxState;

public class Maze
{
	Node[][] grid;
	
	public Maze(int cols, int rows) {
		grid = new Node[cols][rows];
		
		// initialize all to a blockade, except for top and bottom rows,
		// which are all empty:
		for (int i = 0; i < grid.length; i++) {
			grid[i][0] = new Node(i, 0, BoxState.EMPTY);
			grid[i][grid[i].length - 1] = new Node(i, grid[i].length - 1, BoxState.EMPTY);
		}
		for (int i = 0; i < grid.length; i++) {
			for (int j = 1; j < grid[i].length - 1; j++) {
				grid[i][j] = new Node(i, j, BoxState.BLOCKADE);
			}
		}

		ArrayList<Node> run = new ArrayList<Node>();
		Node currentCell;
		
		// use sidewinder algorithm on every other row:
		for (int i = 2; i < grid[0].length; i += 2) {
			currentCell = grid[0][i];
			for (int j = 0; j < grid.length; j++) {
				run.add(currentCell);
				currentCell.setState(BoxState.EMPTY);
				if (Math.random() > .5) {
					try {
						currentCell = grid[j + 1][i];
						//currentCell = grid[currentCell.getX() + 1][currentCell.getY()];
					} catch (IndexOutOfBoundsException e) {}
				}
				else {
					Node rand = run.get((int) (Math.random() * run.size()));
					grid[rand.getX()][rand.getY() - 1].setState(BoxState.EMPTY);
					run.clear();
					try {
						currentCell = grid[currentCell.getX() + 2][currentCell.getY()];
					} catch (IndexOutOfBoundsException e) {}
				}
			}
		}
		
		// creates extra openings to make a spacier maze:
		for (int i = 1; i < grid[0].length; i += 2) {
			// initializes a queue of boxes in that row
			ArrayList<Node> queue = new ArrayList<Node>();
			for (int k = 0; k < grid.length; k++) {
				queue.add(grid[i][k]);
			}
			
			// 2-4 openings per row
			int newOpenings = (int) (Math.random() * 6 + 2);
			for (int j = 0; j < newOpenings; j++) {
				if (queue.isEmpty()) break;
				Node rand = queue.get((int) (Math.random() * queue.size()));
				if (rand.getState() == BoxState.EMPTY) {
					queue.remove(rand);
					j--;
					continue;
				}
				else rand.setState(BoxState.EMPTY);
			}
		}
		
		Node.initialize(grid);
	}
	
	public void newGoals() {
		for (Node[] nArr : grid) {
			for (Node n : nArr) {
				if (n.getState() == BoxState.EMPTY &&
					Math.random() > .99) n.setState(BoxState.GOAL);
			}
		}
	}
	
	/*****************************
	 * SETTING AND GETTING
	 */
	
	public int getCols(int row) {
		return grid[row].length;
	}
	
	public int getRows() {
		return grid.length;
	}
	
	public BoxState getBox(int col, int row) {
		return grid[col][row].getState();
	}
	
	public void setBox(int col, int row, BoxState state) {
		grid[col][row].setState(state);
	}
	
	public void clear() {
		for (Node[] nArr : grid) {
			for (Node n : nArr) {
				n.setState(BoxState.EMPTY);
			}
		}
	}
	
	public boolean isClear() {
		for (Node[] nArr : grid) {
			for (Node n : nArr) {
				if (n.getState() != BoxState.EMPTY) return false;
			}
		}
		return true;
	}
	
	public Maze clone() {
		Maze newGrid = new Maze(grid.length, grid[0].length);
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				newGrid.setBox(i, j, grid[i][j].getState());
			}
		}
		return newGrid;
	}
}
