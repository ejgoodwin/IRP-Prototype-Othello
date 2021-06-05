package minimax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import game.GameLogic;

public class AlphaBeta {
	
	GameLogic logic = new GameLogic();
	char[] board;
	int counter = 0;
	
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
	
	public int runMinimax() {
		counter = 0;
		Map<String, Integer> minimaxMap = alphaBeta(board, 'w', 0, -1000000, 1000000);
		int chosenPosition = minimaxMap.get("index"); 
		System.out.println("Counter " + counter);
		return chosenPosition;
	}
	
	private Map<String, Integer> alphaBeta(char[] boardMinMAx, char player, int depth, int alpha, int beta) {
		counter++;
		//count += 1;
		ArrayList<Integer> availSquares = evaluateBoard(boardMinMAx, player);
		// If end of depth, see who has the best score.
		//System.out.println(availSquares);
		if (depth == 6 || availSquares.size() < 1) {
			int score = boardValue(player, boardMinMAx, availSquares.size());
			Map<String, Integer> scoreReturn = new HashMap<>();
			scoreReturn.put("score", score);
			return scoreReturn;
		}
		// Start min/max
		if (player == 'w') {
	 		Map<String, Integer> bestScore = new HashMap<>();
	 		bestScore.put("score", -1000);
			// loop through available squares.
			for (int i = 0; i < availSquares.size(); i++) {
				// assign player to the current square.
				//boardMinMAx[availSquares.get(i)] = player;
				//System.out.println(boardMinMAx);
				logic.setPosition(availSquares.get(i));
				logic.setPlayers('w', 'b');
				logic.setBoard(boardMinMAx);
				logic.checkNextItem(boardMinMAx);
				char[] newBoard = logic.getNewBoard();
				//System.out.println(newBoard);
				// store result of minimax
				Map<String, Integer> result = alphaBeta(newBoard, 'b', depth+1, alpha, beta);
				// Find the MAXIMUM score
				if (result.get("score").doubleValue() > bestScore.get("score").doubleValue()) {	
					bestScore.put("score", result.get("score"));
					bestScore.put("index", availSquares.get(i));
				}
				alpha = result.get("score");
				// Find alpha value
				if (alpha >= beta) {
					break;
				}
				// Reset current square to null 
				// -> next iteration needs to see state of board prior to that potential move
				//boardMinMAx[availSquares.get(i)] = '-';
			}
			// console.log(bestScore);
			return bestScore;
		} else {
			Map<String, Integer> bestScore = new HashMap<>();
			bestScore.put("score", 1000);
			for (int i = 0; i < availSquares.size(); i++) {
				//boardMinMAx[availSquares.get(i)] = player;
				logic.setPosition(availSquares.get(i));
				logic.setPlayers('b', 'w');
				logic.setBoard(boardMinMAx);
				logic.checkNextItem(boardMinMAx);
				char[] newBoard = logic.getNewBoard();
				Map<String, Integer> result = alphaBeta(newBoard, 'w', depth+1, alpha, beta);
				// Find the MINIMUM score
				if (result.get("score").doubleValue() < bestScore.get("score").doubleValue()) {
					bestScore.put("score", result.get("score"));
					bestScore.put("index", availSquares.get(i));
				}
				beta = result.get("score");	
				// Find alpha value
				if (alpha >= beta) {
					break;
				}
				//boardMinMAx[availSquares.get(i)] = '-';
			}
			// console.log(bestScore);
			return bestScore;
		}
	}
	
	private int boardValue(char player, char[] boardMinMAx, int availableSquaresNum) {
		int score = 0;
		// Compare player's squares against weighted board.
		for (int i = 0; i < weightedBoard.length; i++) {
			if (boardMinMAx[i] == player) {
				if (player == 'w') {
					score += weightedBoard[i];
				} else {
					score -= weightedBoard[i];
				}
			}
		}
		// Test for mobility.
		if (player == 'w') {
			score += availableSquaresNum;
		} else {
			// Should these be + or - ???
			score -= availableSquaresNum;
		}
		return score;
	}

	private ArrayList<Integer> evaluateBoard(char[] boardEval, char player) {
		ArrayList<Integer> availSquares = new ArrayList<Integer>();
		//char[] boardEvalClone = boardEval.clone();
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
				boolean successfulMove = logic.checkNextItem(boardEval);
				if (successfulMove) {
					//System.out.println(i);
					// This move is available -> add it to the array.
					availSquares.add(i);
				}
			}
		}
		// Return an array of available squares (empty if non available).
		//System.out.println(availSquares);
		return availSquares;
	}
}

