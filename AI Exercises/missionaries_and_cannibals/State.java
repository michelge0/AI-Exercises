package missionaries_and_cannibals;

import java.util.ArrayList;

public class State {	
	// right missionaries, left cannibals, etc.
	public int rightM, rightC, leftM, leftC;
	// whether boat is on the right
	public boolean boatRight;

	public State(int rM, int rC, int lM, int lC, boolean bR) {
		this.rightM = rM;
		this.rightC = rC;
		this.leftM = lM;
		this.leftC = lC;
		this.boatRight = bR;
	}
	
	public static final int FAILURE = -1, SUCCESS = 1;
	
	public int goalCheck() {
		if ((rightC > rightM && rightM != 0) || (leftC > leftM && leftM != 0)) return FAILURE;
		else if (leftM == 3 && leftC == 3) return SUCCESS;
		else return 0;
	}
	
	public String toString() {
		return rightM + " missionaries on the right, " + rightC + " cannibals on the right; "
				+ leftM + " missionaries on the left, " + leftC + " cannibals on the left; " + 
				"boat is on the right: " + boatRight;
	}
		
	public static void main(String[] args) {
		State initialState = new State(3, 3, 0, 0, true);
		Node root = new Node(initialState, null, 1);
		Node.initializeQueue(root);
		
		boolean solved = false;
		
		while (!solved) {
			solved = Node.bfs();
		}
		
		System.out.println("finished");
	}
}