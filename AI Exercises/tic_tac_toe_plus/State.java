package tic_tac_toe_plus;

import java.util.LinkedList;

import tic_tac_toe_plus.Square.SquareState;

public class State
{
	Square[][] board;
	
	public State(Square[][] board) {
		this.board = board;
	}
	
	/*************************
	 * SETTING AND GETTING
	 */
	
	public SquareState getSquare(int i, int j) {
		return board[i][j].currentState;
	}
	
	public Square[][] getState() {
		return board;
	}
	
	public State clone() {
		Square[][] clone = new Square[board.length][board[0].length];
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				clone[i][j] = new Square(board[i][j].points, board[i][j].currentState);
			}
		}
		return new State(clone);
	}
	
	public void check(int i, int j, SquareState state) {
		board[i][j].check(state);
	}
	
	public String getString() {
		String output = "";
		for (Square[] sArr : board) {
			for (Square s : sArr) {
				output += "[";
				switch (s.currentState) {
				case X: output += "X"; break;
				case O: output += "O"; break;
				case EMPTY: output += " "; break;
				}
				output += "]";
			}
			output += "\n";
		}
		output += "\nPoint values:\n";
		// values:
		for (Square[] sArr : board) {
			for (Square s : sArr) {
				output += "(" + s.points + ")";
			}
			output += "\n";
		}
		return output;
	}
	
	public void restart() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				board[i][j] = new Square();
			}
		}
	}
	
	/****************************************
	 * GAME STATUS
	 */
	
	boolean gameOver() {
		// columns:
		for (int i = 0; i < board.length; i++) {
			boolean win = true;
			for (int j = 0; j < board[i].length - 1; j++) {
				if (board[i][j].currentState == board[i][j + 1].currentState) {
					continue;
				}
				else {
					win = false;
					break;
				}
			}
			if (win && board[i][0].currentState != SquareState.EMPTY) {
				return true;
			}
		}
		// rows:
		for (int i = 0; i < board[0].length; i++) {
			boolean win = true;
			for (int j = 0; j < board.length - 1; j++) {
				if (board[j][i].currentState == board[j + 1][i].currentState) {
					continue;
				}
				else {
					win = false;
					break;
				}
			}
			if (win && board[0][i].currentState != SquareState.EMPTY) {
				return true;
			}
		}
		// if it's a tie:
		boolean tie = true;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j].currentState == SquareState.EMPTY) {
					tie = false;
				}
			}
		}
		if (tie) {
			return true;
		}
		
		// checks if diagonals exist:
		if (!(board.length == board[0].length)) return false;
		
		// both diagonals in one:
		boolean win = true, win1 = true;
		for (int i = 0; i < board.length - 1; i++) {
			// top left to bottom right:
			if (board[i][i].currentState != board[i + 1][i + 1].currentState) {
				win = false;
			}
			// bottom left to top right:
			if (board[i][board[i].length - 1 - i].currentState
				!= board[i + 1][board[i].length - 2 - i].currentState) {
				win1 = false;
			}
		}
		if (win && board[0][0].currentState != SquareState.EMPTY) return true;
		if (win1 && board[0][board[0].length - 1].currentState != SquareState.EMPTY) return true;
		else return false;
	}
	
	boolean win(SquareState possibleWinner) {
		// columns:
		for (int i = 0; i < board.length; i++) {
			boolean win = true;
			for (int j = 0; j < board[i].length - 1; j++) {
				if (board[i][j].currentState == board[i][j + 1].currentState) {
					continue;
				}
				else {
					win = false;
					break;
				}
			}
			if (win && board[i][0].currentState == possibleWinner) return true;
		}
		// rows:
		for (int i = 0; i < board[0].length; i++) {
			boolean win = true;
			for (int j = 0; j < board.length - 1; j++) {
				if (board[j][i].currentState == board[j + 1][i].currentState) {
					continue;
				}
				else {
					win = false;
					break;
				}
			}
			if (win && board[0][i].currentState == possibleWinner) return true;
		}
		
		// checks if diagonals exist:
		if (board.length != board[0].length) return false;
		
		// both diagonals in one:
		boolean win = true, win1 = true;
		for (int i = 0; i < board.length - 1; i++) {
			// top left to bottom right:
			if (board[i][i].currentState != board[i + 1][i + 1].currentState) {
				win = false;
			}
			// bottom left to top right:
			if (board[i][board[i].length - 1 - i].currentState
				!= board[i + 1][board[i].length - 2 - i].currentState) {
				win1 = false;
			}
		}
		if (win && board[0][0].currentState == possibleWinner) return true;
		if (win1 && board[0][board[0].length - 1].currentState == possibleWinner) return true;
		else return false;
	}
	
	/****************************************
	 * MINIMAX SEARCH
	 */
	
	LinkedList<State> expand(SquareState state) {
		LinkedList<State> possibleMoves = new LinkedList<State>();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j].currentState == SquareState.EMPTY) {
					State newPossibility = clone();
					newPossibility.check(i, j, state);
					possibleMoves.add(newPossibility);
				}
			}
		}
		return possibleMoves;
	}

	int maxValue(int alpha, int beta) {
		if (gameOver()) {
			return getTerminalUtility();
		}
		
		// initialized to something crazily small
		int max = -1000;
		
		LinkedList<State> successors = expand(SquareState.X);
		for (State successor : successors) {
			max = Math.max(max, successor.minValue(alpha, beta));
			if (max >= beta) return max;
			beta = Math.max(beta, max);
		}
		return max;
	}
	
	int minValue(int alpha, int beta) {
		if (gameOver()) {
			return getTerminalUtility();
		}
		
		// initialized to something crazily big
		int min = 1000;
		
		LinkedList<State> successors = expand(SquareState.O);
		for (State successor : successors) {
			min = Math.min(min, successor.maxValue(alpha, beta));
			
			/* 
			 * alpha beta pruning. if the following is the case then
			 * that means this particular call of minValue will return
			 * at best something that's worse than another option, so there's
			 * no need to keep searching.
			 */
			
			if (min <= alpha) return min;
			beta = Math.min(beta, min);		// new value of beta
		}
		return min;
	}
	
	// from computer's perspective (higher return value the better for computer)
	int getTerminalUtility() {
		int utility = 0;
		for (Square[] sArr : board) {
			for (Square s : sArr) {
				if (s.currentState == SquareState.X) {
					utility += s.points;
				}
				else if (s.currentState == SquareState.O) {
					utility -= s.points;
				}
			}
		}
		if (win(SquareState.X)) utility += 5;
		else if (win(SquareState.O)) utility -= 5;
		return utility;
	}

}
