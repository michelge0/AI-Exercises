package maze_runner;

import maze_runner.Display.BoxState;

public class Player extends Entity
{
	public Player(int x, int y) {
	    super(x, y, BoxState.PLAYER);
    }

	protected boolean collide(Node collidee) {
		switch (collidee.getState()) {
		case GHOST: Display.gameOver = true; return true;
		case EMPTY: return false;
		case GOAL: Display.points++; return false;
		default: return true;
		}
	}
}
