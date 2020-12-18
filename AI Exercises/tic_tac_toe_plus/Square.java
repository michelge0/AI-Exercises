package tic_tac_toe_plus;

public class Square
{
	enum SquareState { X, O, EMPTY }

	int points;
	SquareState currentState;
	
	public Square() {
		// any int from -3 to 3
		points = (int) (Math.random() * 7) - 3;
		//points = 0;
		currentState = SquareState.EMPTY;
	}
	
	public Square(int points, SquareState currentState) {
		this.points = points;
		this.currentState = currentState;
	}
		
	public boolean check(SquareState mark) {
		if (currentState == SquareState.EMPTY) {
			currentState = mark;
			return true;
		}
		else return false;
	}
}
