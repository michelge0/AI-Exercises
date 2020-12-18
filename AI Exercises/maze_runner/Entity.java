package maze_runner;

import maze_runner.Display.BoxState;

public abstract class Entity
{	
	public static final int NONE = 0, UP = 1, RIGHT = 2, DOWN = 3, LEFT = 4;
	int direction;
	int xPos, yPos;
	
	BoxState type; // either ghost or player
	
	public Entity(int x, int y, BoxState type) {
		xPos = x;
		yPos = y;
		this.type = type;
		
		Display.grid.setBox(xPos, yPos, type);
	}
	
	public int getX() {
		return xPos;
	}
	
	public int getY() {
		return yPos;
	}
	
	/**************************
	 * MOVEMENT
	 */
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public void move() {
		switch (direction) {
		case UP:
			try {
				if (collide(Display.grid.grid[xPos][yPos - 1]))
					return;
				
				Display.grid.setBox(xPos, yPos, BoxState.EMPTY);
				Display.grid.setBox(xPos, yPos - 1, type);
				yPos--;
			} catch (IndexOutOfBoundsException e) {}
			break;
		case DOWN:
			try {
				if (collide(Display.grid.grid[xPos][yPos + 1]))
					return;
	
				Display.grid.setBox(xPos, yPos, BoxState.EMPTY);
				Display.grid.setBox(xPos, yPos + 1, type);
				yPos++;
			} catch (IndexOutOfBoundsException e) {}
			break;
		case RIGHT:
			try {
				if (collide(Display.grid.grid[xPos + 1][yPos]))
					return;
				
				Display.grid.setBox(xPos, yPos, BoxState.EMPTY);
				Display.grid.setBox(xPos + 1, yPos, type);
				xPos++;
			} catch (IndexOutOfBoundsException e) {}
			break;
		case LEFT:
			try {
				if (collide(Display.grid.grid[xPos - 1][yPos]))
					return;
				
				Display.grid.setBox(xPos, yPos, BoxState.EMPTY);
				Display.grid.setBox(xPos - 1, yPos, type);
				xPos--;
			} catch (IndexOutOfBoundsException e) {}
			break;
		}
	}
	
	protected abstract boolean collide(Node collidee);
	
}
