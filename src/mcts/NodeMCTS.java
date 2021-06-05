package mcts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import game.GameLogic;
import game.BoardEvaluation;

public class NodeMCTS {
	char[] boardState;
	NodeMCTS parentNode;
	List<NodeMCTS> childArray = new ArrayList<>();
	int visitCount;
	char currentPlayer = 'w';
	char nextPlayer = 'b';
	GameLogic logic = new GameLogic();
	BoardEvaluation boardEval = new BoardEvaluation();
	int score;
	int position;
	
	public NodeMCTS() {
		childArray = new ArrayList<>();
	}
	
	public NodeMCTS(char currentPlayer) {
		this.currentPlayer = currentPlayer;
	}
	
	public void setBoardState(char[] boardIn) {
		boardState = boardIn;
	}
	
	public List<NodeMCTS> getChildArray() {
		return childArray;
	}
	
	public char[] getBoardState() {
		return boardState;
	}
	
	public int getVisitCount() {
		return visitCount;
	}
	
	public void incrementVisitCount() {
		visitCount++;
	}
	
	public void setParent(NodeMCTS parentNodeIn) {
		parentNode = parentNodeIn;
	}
	
	public NodeMCTS getParent() {
		return parentNode;
	}
	
	public char getCurrentPlayer() {
		return currentPlayer;
	}
	
	public char getNextPlayer() {
		return nextPlayer;
	}
	
	public void setPlayers(char currentPlayerIn, char nextPlayerIn) {
		currentPlayer = currentPlayerIn;
		nextPlayer = nextPlayerIn;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int positionIn) {
		position = positionIn;
	}
	
	// Create array of available moves.
	public void createChildArray() {
		char[] boardEvalClone = boardState.clone();
		int position;
		NodeMCTS parent = getParent();
		
		if (parent != null) {
			//System.out.println(parent.getNextPlayer());
			currentPlayer = parent.getNextPlayer();
			nextPlayer = parent.getCurrentPlayer();
		}
		logic.setPlayers(currentPlayer, nextPlayer);
		// Reset available squares list.
		for (int i = 0; i < boardState.length; i++) {
			if (boardState[i] == '-' || boardState[i] == 'a') {
				position = i;
				logic.setBoard(boardState);
				logic.setPosition(position);
				//System.out.println("G");
				boolean successfulMove = logic.checkNextItem(boardEvalClone);
				if (successfulMove) {
					//System.out.println("HEY");
					//System.out.println(i);
					// This move is available -> update board.
					//boardState[i] = 'a';
					// Add to available squares list.
					NodeMCTS newNode = new NodeMCTS(nextPlayer);
					newNode.setPosition(i);
					char[] newBoardState = logic.getNewBoard();
					newNode.setBoardState(newBoardState);
					childArray.add(newNode);
				}
			}
		}
	}
	
	public char getWinState() {
		HashMap<String, Integer> results =  boardEval.returnResults(boardState);
		if (results.get("white") > results.get("black")) {
			System.out.println("White win");
			return 'w';
		} else if (results.get("white") == results.get("black")) {
			System.out.println("draw");
			return 'd';
		} else {
			System.out.println("Black win");
			return 'b';
		}
		
		
	}
	
	public void setScore(int scoreIn) {
		score = score + scoreIn;
	}
	
	public int getScore() {
		return score;
	}
	
	public NodeMCTS getChildMaxScore() {
		NodeMCTS maxNode = null;
		int score = -1000000;
		for (int i = 0; i < childArray.size(); i++) {
			System.out.println(childArray.get(i).getScore());
			if (childArray.get(i).getScore() > score) {
				maxNode = childArray.get(i);
				score = maxNode.getScore();
			}
		}
		return maxNode;
	}
}
