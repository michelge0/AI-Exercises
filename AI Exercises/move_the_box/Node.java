package move_the_box;

import java.util.ArrayList;
import java.util.LinkedList;

import move_the_box.Display.BoxState;

public class Node
{
	static LinkedList<Node> queue = new LinkedList<Node>();
		
	Grid state;
	Action action;
	Node parent;
	int depth;
	
	static int searchLimit = 2;
	
	static class Action {
		public int x, y; // x and y indices of box on grid
		public static final int DOWN = 0, RIGHT = 1; // whether it was shifted down or right
		public int direction;
		public Action(int x, int y, int direction) {
			this.x = x; this.y = y; this.direction = direction;
		}
	}
	
	public Node(Grid state, Action action, Node parent, int depth) {
		this.state = state;
		this.action = action;
		this.parent = parent;
		this.depth = depth;		
	}
	
	public String toString() {
		return state.toString();
	}
	
	public void printPath() {
		System.out.println(toString());
		if (parent != null) parent.printPath();
	}
	
	// Later we can speed this up by instead of having nodePath
	// just directly expand paths in the while loop
	
	public LinkedList<Grid> getPath() {
		LinkedList<Grid> path = new LinkedList<Grid>();
		Node currentNode = this;

		while (currentNode.parent != null) {
			path.addAll(0, expandPath(currentNode));
			currentNode = currentNode.parent;
		}
		
		path.addFirst(currentNode.state); // adds root, which has no preceding action
				
		System.out.println("Size: " + path.size());
		return path;
	}
	
	public static LinkedList<Grid> expandPath(Node node) {
		LinkedList<Grid> expandedPath = new LinkedList<Grid>();
		Grid grid = node.parent.state;
		
		// applying preceding action to parent's state:
		Action action = node.action;
		if (action.direction == Action.DOWN) {
			// switches the two. works backwards, because
			// the action was the action that CREATED this state.
			BoxState temp = grid.getBox(action.x, action.y);
			grid.setBox(action.x, action.y, grid.getBox(action.x, action.y + 1));
			grid.setBox(action.x, action.y + 1, temp);
			
		}
		else {
			BoxState temp = grid.getBox(action.x, action.y);
			grid.setBox(action.x, action.y, grid.getBox(action.x + 1, action.y));
			grid.setBox(action.x + 1, action.y, temp);
		}
		expandedPath.add(grid.clone());
		
		// applying transformations, step by step:
		while (!(grid.settled() && grid.scanSettled())) {
			while (!grid.settled()) {
				grid.updateStep();
				expandedPath.add(grid.clone());
			}			
			if (!grid.scanSettled()) {
				grid.scan();
				expandedPath.add(grid.clone());
			}
		}
		
		return expandedPath;
	}
	
	public boolean expand() {
		if (depth > searchLimit) {
			System.out.println(searchLimit + " " + depth);
			return false;
		}
		if (state.isImpossible()) return false;
		ArrayList<Node> newStates = state.newStates(this);
		for (Node n : newStates) {
			if (n.state.isClear()) {
				n.printPath();
				Display.solutionText = "Solved!";
				Display.setSolutionPath(n.getPath());
				return true;
			}
			queue.addLast(n);
		}
		return false;
	}
	
	/******************************
	 * QUEUE MANAGEMENT
	 */
	
	private void initializeQueue() {
		queue.addFirst(this);
	}
	
	/********************
	 * SEARCHING
	 */
	
	public void findSolution() {
		initializeQueue();
		
		boolean solved = false;
		
		while (!solved) {
			solved = bfs();
		}
		
		queue.clear();
	}
	
	// searches from this node down
	public static boolean bfs() {
		if (queue.isEmpty()) {
			Display.solutionText = "No solution found.";
			return true;
		}
		Node expandedNode = queue.getFirst();
		queue.removeFirst();
		
		return expandedNode.expand();
	}
}
