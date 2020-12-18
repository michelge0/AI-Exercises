package maze_runner;

import java.util.LinkedList;

import maze_runner.Display.BoxState;

public class Node
{
	BoxState state;	
	
	private LinkedList<Node> neighbors = new LinkedList<Node>();
	private int xIndex, yIndex;
	
	public Node(int x, int y, BoxState state) {
		xIndex = x;
		yIndex = y;
		this.state = state;
	}
	
	// Every box except for the last column and last row
	// checks for connectivity with the next column, next row
	public static void initialize(Node[][] grid) {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				if (grid[i][j].getState() == BoxState.BLOCKADE ||
					grid[i][j].getState() == BoxState.GOAL) continue;
				else {
					if (i < grid.length - 1) {
						if (grid[i + 1][j].getState() == BoxState.EMPTY)
							addConnection(grid[i][j], grid[i + 1][j]);
					}
					if (j < grid[i].length - 1) {
						if (grid[i][j + 1].getState() == BoxState.EMPTY)
							addConnection(grid[i][j], grid[i][j + 1]);
					}
				}
			}
		}
	}
	
	public static void addConnection(Node n1, Node n2) {
		if (!n1.neighbors.contains(n2)) n1.neighbors.add(n2);
		if (!n2.neighbors.contains(n1)) n2.neighbors.add(n1);
	}
	
	/**************************
	 * SETTING AND GETTING
	 */
	
	public int getX() {
		return xIndex;
	}
	
	public int getY() {
		return yIndex;
	}
	
	public LinkedList<Node> getNeighbors() {
		return neighbors;
	}
	
	public BoxState getState() {
		return state;
	}
	
	public void setState(BoxState state) {
		this.state = state;
	}

	/***************************
	 * SEARCH
	 */
	
	static LinkedList<Node> closedQueue = new LinkedList<Node>();
	static LinkedList<LinkedList<Node>> possiblePaths = new LinkedList<LinkedList<Node>>();
	
	// Performs a depth-first search using recursion
	// to load possiblePaths with multiple suggestions
	private void dfs(int x, int y, LinkedList<Node> currentPath) {
		currentPath.add(this);
		if (xIndex == x && yIndex == y) {
			possiblePaths.add(currentPath);
			return;
		}
		else {
			// sort nodes by likelihood of being a correct path, using selection sort
			for (int i = 0; i < neighbors.size(); i++) {
				int best = i;
				for (int j = i + 1; j < neighbors.size(); j++) {
					if (compareTo(neighbors.get(j), x, y) < compareTo(neighbors.get(best), x, y))
						best = j;
				} // switches best and i:
				neighbors.set(best, neighbors.set(i, neighbors.get(best)));
			}
			for (Node n : neighbors) {
				if (closedQueue.contains(n)) continue;
				LinkedList<Node> clone = clonePath(currentPath);

				closedQueue.add(this);
				n.dfs(x, y, clone);
			}
		}
	}
	
	public LinkedList<Node> findPath(int x, int y) {
		LinkedList<Node> root = new LinkedList<Node>();
		root.add(this);
		
		// loads possible paths:
		dfs(x, y, root);
				
		// selects shortest path from possible paths:
		LinkedList<Node> bestPath = possiblePaths.get(0);
		for (LinkedList<Node> path : possiblePaths) {
			if (path.size() < bestPath.size()) bestPath = path;
		}
		
		closedQueue.clear();
		possiblePaths.clear();
		
		return bestPath;
	}
	
	/*
	 * ORDERING:
	 * 1: + in priority direction
	 * 2: + in other direction
	 * 3: - in other direction
	 * 4: - in priority direction
	 */
	
	private int compareTo(Node n, int xGoal, int yGoal) {
		int xDistance = Math.abs(xGoal - xIndex);
		int yDistance = Math.abs(yGoal - yIndex);
		
		// if it's not y priority, it must be x priority
		boolean yPriority;
		
		if (xDistance > yDistance) {
			yPriority = false;
		}
		else {
			yPriority = true;
		}
		
		// Compares distances to determine whether following this
		// node will take you further or closer to goal
		
		// a y-changing node:
		if (n.xIndex == xIndex) {
			if (yPriority) {
				if (Math.abs(yGoal - n.yIndex) > yDistance) return 4;
				else return 1;
			}
			else {
				if (Math.abs(yGoal - n.yIndex) > yDistance) return 3;
				else return 2;
			}
		}
		// an x-changing node:
		else {
			// if x priority:
			if (!yPriority) {
				if (Math.abs(xGoal - n.xIndex) > xDistance) return 4;
				else return 1;
			}
			else {
				if (Math.abs(xGoal - n.xIndex) > xDistance) return 3;
				else return 2;
			}
		}
	}
	
	private static LinkedList<Node> clonePath(LinkedList<Node> list) {
		LinkedList<Node> clone = new LinkedList<Node>();
		for (Node n : list) {
			clone.addLast(n);
		}
		return clone;
	}
}
