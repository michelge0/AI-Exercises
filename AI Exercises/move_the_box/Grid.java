package move_the_box;

import java.util.ArrayList;

import move_the_box.Display.BoxState;
import move_the_box.Node.Action;

public class Grid
{
	Box[][] grid;
	
	// wrapper class for BoxState
	private class Box {
		BoxState state;
		
		public Box(BoxState state) {
			this.state = state;
		}
		public BoxState getState() {
			return state;
		}
		public void setState(BoxState state) {
			this.state = state;
		}
	}
		
	/*
	 * ex/ 
	 * row id:
	 * 0: BOX0 BOX1 NONE BOX3 NONE (col id)
	 * 1: BOX0 BOX1 BOX2 BOX3 NONE (col id)
	 * 2: BOX0 BOX1 BOX2 BOX3 BOX4 (col id)
	 */
	
	public Grid(int cols, int rows) {
		grid = new Box[cols][rows];
		
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				grid[i][j] = new Box(BoxState.EMPTY);
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
		for (Box[] bArr : grid) {
			for (Box b : bArr) {
				b.setState(BoxState.EMPTY);
			}
		}
	}
	
	public boolean isClear() {
		for (Box[] bArr : grid) {
			for (Box b : bArr) {
				if (b.getState() != BoxState.EMPTY) return false;
			}
		}
		return true;
	}
	
	public boolean isImpossible() {
		int[] colors = new int[6];
		for (Box[] bArr : grid) {
			for (Box b : bArr) {
				switch (b.state) {
				case GREEN: colors[0]++; break;
				case RED: colors[1]++; break;
				case BLUE: colors[2]++; break;
				case LIGHT_BROWN: colors[3]++; break;
				case DARK_BROWN: colors[4]++; break;
				case BLACK: colors[5]++; break;
				default: break;
				}
			}
		}
		for (int i : colors) {
			if (i != 0 && i < 3) return true;
		}
		return false;
	}
	
	public String toString() {
		String output = "";
		for (int i = 0; i < grid[0].length; i++) {
			for (int j = 0; j < grid.length; j++) {
				switch (grid[j][i].getState()) {
				case GREEN: output += "[Gr]"; break;
				case RED: output += "[Re]"; break;
				case BLUE: output += "[Bl]"; break;
				case LIGHT_BROWN: output += "[Br]"; break;
				case BLACK: output += "[BL]"; break;
				case EMPTY: output += "[  ]"; break;
				}
			}
			output += "\n";
		}
		return output;
	}
	
	/***********************
	 * UPDATING
	 */
	
	// checks if another update() will make a difference
	boolean settled() {
		// floating boxes:
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length - 1; j++) {
				if (grid[i][j].getState() != BoxState.EMPTY &&
					grid[i][j + 1].getState() == BoxState.EMPTY) return false;
			}
		}
		return true;
	}
	
	// checks if another scan() will make a difference
	boolean scanSettled() {
		// scan columns:
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length - 2; j++) {
				if (grid[i][j].getState() != BoxState.EMPTY &&
					grid[i][j].getState() == grid[i][j + 1].getState() &&
					grid[i][j + 1].getState() == grid[i][j + 2].getState())  return false;
			}
		}
		// scan rows:
		for (int i = 0; i < grid.length - 2; i++) {
			for (int j = 0; j < grid.length; j++) {
				if (grid[i][j].getState() != BoxState.EMPTY &&
					grid[i][j].getState() == grid[i + 1][j].getState() &&
					grid[i + 1][j].getState() == grid[i + 2][j].getState()) return false;
			}
		}
		return true;
	}
	
	void updateAll() {
		Grid initial = clone();
		while (!settled()) {
			updateStep();
		}
		scan();
		while (!settled()) {
			updateStep();
		}
		// as long as something different happens each time,
		// it continues recursing
		if (!toString().equals(initial.toString())) updateAll();
	}
	
	// only goes one step (box in midair only falls one block)
	void updateStep() {
		for (int i = 0; i < grid.length; i++) {
			for (int j = grid[i].length - 2; j >= 0; j--) {
				if (grid[i][j + 1].getState() == BoxState.EMPTY) {
					grid[i][j + 1].setState(grid[i][j].getState());
					grid[i][j].setState(BoxState.EMPTY);
				}
			}
		}
	}
	
	// scans for clearances/box poofs
	void scan() {
		// scan columns:
		ArrayList<Box> markedBoxes = new ArrayList<Box>();
		for (int i = 0; i < grid.length; i++) {
			// length - 2 because it scans entire groups of three elements
			for (int j = 0; j < grid[i].length - 2; j++) {
				if (grid[i][j].getState() == grid[i][j + 1].getState() &&
					grid[i][j + 1].getState() == grid[i][j + 2].getState()) {
					markedBoxes.add(grid[i][j]);
					markedBoxes.add(grid[i][j + 1]);
					markedBoxes.add(grid[i][j + 2]);
				}
			}
		}
		// scan rows. i = row identifier.
		for (int i = 0; i < grid.length - 2; i++) {
			for (int j = 0; j < grid.length; j++) {
				if (grid[i][j].getState() == grid[i + 1][j].getState() &&
					grid[i + 1][j].getState() == grid[i + 2][j].getState()) {
					markedBoxes.add(grid[i][j]);
					markedBoxes.add(grid[i + 1][j]);
					markedBoxes.add(grid[i + 2][j]);
				}
			}
		}
		for (Box[] bArr : grid) {
			for (Box b : bArr) {
				if (markedBoxes.contains(b)) b.setState(BoxState.EMPTY);
			}
		}
	}
	
	/***************************
	 * SEARCH
	 */
	
	public ArrayList<Node> newStates(Node parent) {
		ArrayList<Node> newStates = new ArrayList<Node>();
		// checks horizontally. switches two boxes as long
		// as both aren't the same type of box (including empty).
		for (int i = 0; i < grid.length - 1; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				if (grid[i][j].getState() != grid[i + 1][j].getState()) {
					// clones the grid and swaps two elements
					Grid newState = clone();
					newState.setBox(i, j, grid[i + 1][j].getState());
					newState.setBox(i + 1, j, grid[i][j].getState());
					newState.updateAll();
					newStates.add(new Node(newState, new Action(i, j, Node.Action.RIGHT), parent, parent.depth + 1));
				}
			}
		}
		// vertically:
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length - 1; j++) {
				if (grid[i][j].getState() != grid[i][j + 1].getState() &&
					grid[i][j].getState() != BoxState.EMPTY &&
					grid[i][j + 1].getState() != BoxState.EMPTY) {
					// clones the grid and swaps two elements
					Grid newState = clone();
					newState.setBox(i, j, grid[i][j + 1].getState());
					newState.setBox(i, j + 1, grid[i][j].getState());
					newState.updateAll();
					newStates.add(new Node(newState, new Action(i, j, Node.Action.DOWN), parent, parent.depth + 1));
				}
			}
		}
		return newStates;
	}
	
	public Grid clone() {
		Grid newGrid = new Grid(grid.length, grid[0].length);
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				newGrid.setBox(i, j, grid[i][j].getState());
			}
		}
		return newGrid;
	}
}
