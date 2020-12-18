package maze_runner;

import java.util.ArrayList;
import java.util.LinkedList;

import maze_runner.Display.BoxState;

public class Ghost extends Entity
{	
	private static ArrayList<Ghost> ghosts = new ArrayList<Ghost>();
	
	private LinkedList<Node> path = new LinkedList<Node>();
	
	private int playerX, playerY;
	private int pathUpdateDistance;
	
	public Ghost(int x, int y, int pathUpdateDistance) {
	    super(x, y, BoxState.GHOST);
	    this.pathUpdateDistance = pathUpdateDistance;
	    
	    ghosts.add(this);
    }
	
	public static void clear() {
		ghosts.clear();
	}

	/*************************
	 * COLLISION
	 */
	
	protected boolean collide(Node collidee) {
		switch (collidee.getState()) {
		case PLAYER: Display.gameOver = true; return true;
		case EMPTY: return false;
		case GOAL: return false;
		default: return true;
		}
	}
	
	/***************************
	 * AI
	 */
	
	public static void moveGhosts() {
		for (Ghost g : ghosts) {
			g.calculateMovements();
			g.move();
		}
	}
	
	private void getPath(int x, int y) {
		path = Display.grid.grid[xPos][yPos].findPath(x, y);
	}
	
	// used to calculate if the player has strayed too far from
	// the last goal point
	private int euclidDistance() {
		return (Math.abs(playerX - Display.player.getX()) +
				Math.abs(playerY - Display.player.getY()));
	}
	
	// Switches ghost's direction based on its current path. 
	private void calculateMovements() {		
		if (euclidDistance() > pathUpdateDistance || path.size() == 0) {
			direction = NONE;
			playerX = Display.player.getX();
			playerY = Display.player.getY();
			getPath(playerX, playerY);
			return;
		}
		
		Node nextStep = path.getFirst();		
		if (nextStep.getX() == xPos + 1) direction = RIGHT;
		else if (nextStep.getX() == xPos - 1) direction = LEFT;
		else if (nextStep.getY() == yPos + 1) direction = DOWN;
		else if (nextStep.getY() == yPos - 1) direction = UP;
		
		else {
			path.removeFirst();
			calculateMovements();
			return;
		}
		
		path.removeFirst();
	}
}
