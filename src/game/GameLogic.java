package game;

public class GameLogic {
	
	int position;
	char nextPlayer;
	char currentPlayer;
	char[] board;
	boolean successfulMove = false;
	
	public void setPosition(int positionIn) {
		position = positionIn;
	}
	
	public void setPlayers(char currentPlayerIn, char nextPlayerIn) {
		currentPlayer = currentPlayerIn;
		nextPlayer = nextPlayerIn;
	}
	
	public void setBoard(char[] boardIn) {
		board = boardIn;
	}
	
	public char[] getNewBoard() {
		return board;
	}
	
	
	public boolean checkNextItem(char[] boardEval) {
		int condition;
		int decrement;
		int increment;
		int calcVar;
		//char[] boardClone = boardEval.clone();
		int remainder = position%8;
		successfulMove = false;
		//System.out.println(board);
		//System.out.println(currentPlayer);
		// North
		if (position > 7 && board[position - 8] == nextPlayer) {
			condition = 0;
			decrement = 8;
			evaluationFunctionNegative(board.clone(), condition, decrement);
		}

		// South.
		if (position < 56 && board[position + 8] == nextPlayer) {
			condition = 64;
			increment = 8;
			evaluationFunctionPositive(board.clone(), condition, increment);
		}
		
		// Check position is not at right edge of board.
		if (remainder != 7) {
			// Northeast.
			if (position > 6 && board[position - 7] == nextPlayer) {
				condition = position;
				decrement = 7;
				// Find most northeasterly square.
				while (condition % 8 != 7 && condition >= 0) {
					condition -= decrement;
				}
				evaluationFunctionNegative(board.clone(), condition, decrement);
			}

			// East.
			if (board[position + 1] == nextPlayer) {
				calcVar = position % 8;
				condition = position+(7-calcVar);
				increment = 1;
				evaluationFunctionPositive(board.clone(), condition, increment);
			}

			// Southeast.
			if (position < 55 && board[position + 9] == nextPlayer) {
				condition = position;
				increment = 9;
				// Find most southeasterly square.
				while (condition % 8 != 7 && condition >= 0) {
					condition += increment;
				}
				evaluationFunctionPositive(board.clone(), condition, increment);
			}
		}
		
		// Check position is not at left edge of board.
		if (remainder != 0) {
			// Southwest.
			if (position < 57 && board[position + 7] == nextPlayer) {
				condition = position;
				increment = 7;
				// Find most southwesterly square.
				while (condition % 8 != 0) {
					condition += increment;
				}
				evaluationFunctionPositive(board.clone(), condition, increment);
			}

			// West.
			if (board[position - 1] == nextPlayer) {
				calcVar = position % 8;
				condition = position-calcVar;
				decrement = 1;
				evaluationFunctionNegative(board.clone(), condition, decrement);
			}

			// Northwest.
			if (position > 8 && board[position - 9] == nextPlayer) {
				condition = position;
				decrement = 9;
				// Find most northwesterly square.
				while (condition % 8 != 0) {
					condition -= decrement;
				}
				evaluationFunctionNegative(board.clone(), condition, decrement);
			}
		}
		return successfulMove;
	}

	private void evaluationFunctionPositive(char[] boardClone, int condition, int increment) {
		boardClone[position] = currentPlayer;
		for (int i = position+increment; i < condition; i += increment) {
			if (i > 63) {
				return;
			} else if (i+increment > 63) {
				return;
			}
			// If the next square belongs to currentPlayer, cannot be flipped -> break.
			if (boardClone[i] == currentPlayer) break;
			// Check next item -> if it belongs to opponent, flip it to currentPlayer.
			if (boardClone[i] == nextPlayer) {
				boardClone[i] = currentPlayer;
				if (board[i+increment] == '-' || board[i+increment] == 'a') {
					return;
				} else if (board[i+increment] == currentPlayer) {
					board = boardClone;
					successfulMove = true;
					return;
				} 
			}
		};
	}
	
	private void evaluationFunctionNegative(char[] boardClone, int condition, int decrement) {
		boardClone[position] = currentPlayer;
		for (int i = position-decrement; i > condition; i -= decrement) {
			if (i < 0) {
				return;
			} else if (i-decrement < 0) {
				return;
			}
			// If the next square belongs to currentPlayer, cannot be flipped -> break.
			if (boardClone[i] == currentPlayer) break;
			// Check next item -> if it belongs to opponent, flip it to currentPlayer.
			if (boardClone[i] == nextPlayer) {
				boardClone[i] = currentPlayer;
				if (boardClone[i-decrement] == '-' || boardClone[i-decrement] == 'a') {
					return;
				} else if (boardClone[i-decrement] == currentPlayer) {
					board = boardClone;
					successfulMove = true;
					return;
				}
			}
		};
	}

}
