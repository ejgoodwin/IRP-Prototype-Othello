package game;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Minimax {
	
	GameLogic logic = new GameLogic();
	char[] board;
	int counter = 0;
	char currentPlayer;
	char nextPlayer;
	int level;
	
	int[] weightedBoard = {
		99,-8,20,15,15,20,-8,99,
		-8,-24,-4,-3,-3,-4,-24,-8,
		20,-4,6,4,4,6,-4,20,
		15,-3,4,0,0,4,-3,15,
		15,-3,4,0,0,4,-3,15,
		20,-4,6,4,4,6,-4,20,
		-8,-24,-4,-3,-3,-4,-24,-8,
		99,-8,20,15,15,20,-8,99
	};
	
	public void setBoard(char[] boardIn) {
		board = boardIn;
	}
	
	public void setPlayers(char currentPlayerIn, char nextPlayerIn) {
		currentPlayer = currentPlayerIn;
		nextPlayer = nextPlayerIn;
	}
	
	public int runMinimax(int levelIn) {
		counter = 0;
		level = levelIn;
		Map<String, Integer> minimaxMap = minimaxSearch(board, currentPlayer, 0);
		int chosenPosition = minimaxMap.get("index"); 
		System.out.println("Counter " + counter);
		return chosenPosition;
	}
	
	private Map<String, Integer> minimaxSearch(char[] boardMinMAx, char player, int depth) {
		counter++;
		ArrayList<Integer> availSquares = evaluateBoard(boardMinMAx, player);
		// If end of depth, see who has the best score.
		if (depth == level || availSquares.size() < 1) {
			int score = boardValue(player, boardMinMAx, availSquares.size());
			Map<String, Integer> scoreReturn = new HashMap<>();
			scoreReturn.put("score", score);
			return scoreReturn;
		}
		// Start min/max
		if (player == currentPlayer) {
	 		Map<String, Integer> bestScore = new HashMap<>();
	 		bestScore.put("score", -1000);
			// loop through available squares.
			for (int i = 0; i < availSquares.size(); i++) {
				// Update board state to reflect this chosen move.
				logic.setPosition(availSquares.get(i));
				logic.setPlayers(currentPlayer, nextPlayer);
				logic.setBoard(boardMinMAx);
				logic.checkNextItem();
				char[] newBoard = logic.getNewBoard();
				// Store result of minimax.
				Map<String, Integer> result = minimaxSearch(newBoard, nextPlayer, depth+1);
				// Find the MAXIMUM score.
				if (result.get("score").doubleValue() > bestScore.get("score").doubleValue()) {	
					bestScore.put("score", result.get("score"));
					bestScore.put("index", availSquares.get(i));
				}
			}
			return bestScore;
		} else {
			Map<String, Integer> bestScore = new HashMap<>();
			bestScore.put("score", 1000);
			for (int i = 0; i < availSquares.size(); i++) {
				logic.setPosition(availSquares.get(i));
				logic.setPlayers(nextPlayer, currentPlayer);
				logic.setBoard(boardMinMAx);
				logic.checkNextItem();
				char[] newBoard = logic.getNewBoard();
				Map<String, Integer> result = minimaxSearch(newBoard, currentPlayer, depth+1);
				// Find the MINIMUM score.
				if (result.get("score").doubleValue() < bestScore.get("score").doubleValue()) {
					bestScore.put("score", result.get("score"));
					bestScore.put("index", availSquares.get(i));
				}
			}
			return bestScore;
		}
	}
	
	private int boardValue(char player, char[] boardMinMAx, int availableSquaresNum) {
		int score = 0;
		// Compare player's squares against weighted board.
		for (int i = 0; i < weightedBoard.length; i++) {
			if (boardMinMAx[i] == player) {
				if (player == currentPlayer) {
					score += weightedBoard[i];
					score += availableSquaresNum;
				} else {
					score -= weightedBoard[i];
					score -= availableSquaresNum;
				}
			}
		}
		return score;
	}

	private ArrayList<Integer> evaluateBoard(char[] boardEval, char player) {
		ArrayList<Integer> availSquares = new ArrayList<Integer>();
		if (player == 'w') {
			logic.setPlayers('w', 'b');
		} else {
			logic.setPlayers('b', 'w');
		}
		
		int position;
		for (int i = 0; i < boardEval.length; i++) {
			if (boardEval[i] == '-' || boardEval[i] == 'a') {
				position = i;
				logic.setBoard(boardEval);
				logic.setPosition(position);
				boolean successfulMove = logic.checkNextItem();
				if (successfulMove) {
					// This move is available -> add it to the array.
					availSquares.add(i);
				}
			}
		}
		// Return an array of available squares (empty if non available).
		return availSquares;
	}
}
