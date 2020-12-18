package missionaries_and_cannibals;

import java.util.ArrayList;
import java.util.LinkedList;

/*
 * Five components of a node:
 * - STATE to which it corresponds;
 * - PARENT NODE;
 * - ACTION that was applied to generate this node;
 * - PATH COST of the initial state to this node;
 * - DEPTH from initial state to this node
 */

public class Node
{
	public static LinkedList<Node> queue = new LinkedList<Node>();
	public static LinkedList<State> closedQueue = new LinkedList<State>();
	
	public State state;
	public Node parent;
	public int depth;
	
	public Node(State state, Node parent, int depth) {
		this.state = state;
		this.parent = parent;
		this.depth = depth;
	}
	
	public String toString() {
		if (parent == null) return "";
		else {
			String output = "";
			int deltaM = parent.state.rightM - state.rightM;
			int deltaC = parent.state.rightC - state.rightC;
			
			if (deltaM != 0) {
				if (deltaM > 0) output += "Send " + deltaM + " missionaries left. ";
				else output += "Send " + -deltaM + " missionaries right. ";
			}
			if (deltaC != 0) {
				if (deltaC > 0) output += "Send " + deltaC + " cannibals left. ";
				else output += "Send " + -deltaC + " cannibals right. ";
			}
			return output;
		}
	}
	
	public void printPath() {
		System.out.println(state);
		System.out.println(toString());
		if (parent != null) parent.printPath();
	}
	
	/*
	 * expands this node via an action, for example:
	 * if this was a city you could travel() from it,
	 * expanding it into a series of new cities
	 */
	public boolean expand() {
		ArrayList<State> newStates = newStates(state);
		for (State s : newStates) {
			if (!inClosedQueue(s) && s.goalCheck() != State.FAILURE) {
				queue.addLast(new Node(s, this, depth + 1));
				closedQueue.add(s);
			}
			if (s.goalCheck() == State.SUCCESS) {
				new Node(s, this, depth + 1).printPath();
				return true;
			}
		}
		return false;
	}
	
	/******************************
	 * QUEUE MANAGEMENT
	 */
	
	public boolean inClosedQueue(State s) {
		for (State s2 : closedQueue) {
			if (s.toString().equals(s2.toString())){
				return true;
			}
		}
		return false;
	}
	
	public static void initializeQueue(Node root) {
		queue.addFirst(root);
		closedQueue.clear();
	}
	
	/********************
	 * SEARCHING
	 */
	
	// searches from this node down
	public static boolean bfs() {
		if (queue.isEmpty()) {
			System.out.println("failure");
			System.exit(0);
		}
		Node expandedNode = queue.getFirst();
		queue.removeFirst();
		
		return expandedNode.expand();
	}
	
	/********************
	 * MISSIONARIES AND CANNIBALS: "MOVE"
	 */
	
	public static ArrayList<State> newStates(State state) {
		ArrayList<State> newStates = new ArrayList<State>();
		
		int rM = state.rightM, lM = state.leftM;
		int rC = state.rightC, lC = state.leftC;
		
		if (state.boatRight) {
			if (rM >= 1) newStates.add(new State(rM - 1, rC, lM + 1, lC, false));
			if (rM >= 2) newStates.add(new State(rM - 2, rC, lM + 2, lC, false));
			if (rC >= 1) newStates.add(new State(rM, rC - 1, lM, lC + 1, false));
			if (rC >= 2) newStates.add(new State(rM, rC - 2, lM, lC + 2, false));
			if (rM >= 1 && rC > 1) newStates.add(new State(rM - 1, rC - 1, lM + 1, lC + 1, false));
		}
		else {
			if (lM >= 1) newStates.add(new State(rM + 1, rC, lM - 1, lC, true));
			if (lM >= 2) newStates.add(new State(rM + 2, rC, lM - 2, lC, true));
			if (lC >= 1) newStates.add(new State(rM, rC + 1, lM, lC - 1, true));
			if (lC >= 2) newStates.add(new State(rM, rC + 2, lM, lC - 2, true));
			if (lM >= 1 && lC > 1) newStates.add(new State(rM + 1, rC + 1, lM - 1, lC - 1, true));	
		}		
		
		return newStates;
	}
}
