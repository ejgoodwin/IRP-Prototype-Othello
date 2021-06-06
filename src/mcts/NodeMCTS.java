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
	char currentPlayer;
	char nextPlayer;
	GameLogic logic = new GameLogic();
	BoardEvaluation boardEval = new BoardEvaluation();
	int score;
	int position;
	
	public NodeMCTS() {
		this.childArray = new ArrayList<>();
	}
	
	public NodeMCTS(char currentPlayer) {
		this.currentPlayer = currentPlayer;
	}
	
	public void setBoardState(char[] boardIn) {
		this.boardState = boardIn;
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
		this.visitCount++;
		System.out.println("Visit count incremented to: " + visitCount);
	}
	
	public void setParent(NodeMCTS parentNodeIn) {
		this.parentNode = parentNodeIn;
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
		this.currentPlayer = currentPlayerIn;
		this.nextPlayer = nextPlayerIn;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int positionIn) {
		this.position = positionIn;
	}
	
	// Create array of available moves.
	public void createChildArray() {
		//System.out.println("create child array");
		char[] boardEvalClone = this.boardState.clone();
		int position;
		NodeMCTS parent = getParent();
		
		if (parent != null) {
			//System.out.println(parent.getNextPlayer());
			//currentPlayer = parent.getNextPlayer();
			//nextPlayer = parent.getCurrentPlayer();
			//logic.setPlayers(parent.getCurrentPlayer(), parent.getNextPlayer());
			logic.setPlayers(parent.getNextPlayer(), parent.getCurrentPlayer());
			//System.out.println("parent exists");
		} else {
			logic.setPlayers(currentPlayer, nextPlayer);
			//System.out.println("parent does not exists");
		}
		
		// Reset available squares list.
		for (int i = 0; i < this.boardState.length; i++) {
			if (this.boardState[i] == '-' || this.boardState[i] == 'a') {
				position = i;
				logic.setBoard(this.boardState);
				logic.setPosition(position);
				//System.out.println("G");
				boolean successfulMove = logic.checkNextItem(boardEvalClone);
				//System.out.println(boardEvalClone);
				if (successfulMove) {
					//System.out.println("HEY");
					//System.out.println(i);
					// This move is available -> update board.
					//boardState[i] = 'a';
					// Add to available squares list.
					NodeMCTS newNode = new NodeMCTS(this.nextPlayer);
					newNode.setPosition(position);
					char[] newBoardState = logic.getNewBoard();
					//System.out.println("position of new node" +position);
					newNode.setBoardState(newBoardState);
					//System.out.println(newBoardState);
//					if (parent != null) {
//						newNode.setPlayers(parent.getNextPlayer(), getCurrentPlayer());
//					} else {
//						newNode.setPlayers(nextPlayer, currentPlayer);
//					}
					this.childArray.add(newNode);
				}
			}
		}
	}
	
	public char getWinState() {
		HashMap<String, Integer> results =  boardEval.returnResults(boardState);
		if (results.get("white") > results.get("black")) {
			//System.out.println("White win");
			return 'w';
		} else if (results.get("white") == results.get("black")) {
			//System.out.println("draw");
			return 'd';
		} else {
			//System.out.println("Black win");
			return 'b';
		}
		
		
	}
	
	public void setScore(int scoreIn) {
		this.score = score + scoreIn;
	}
	
	public int getScore() {
		return score;
	}
	
	public NodeMCTS getChildMaxScore() {
		NodeMCTS maxNode = null;
		int score = -1000000;
		for (int i = 0; i < childArray.size(); i++) {
			System.out.println("get max score: " + childArray.get(i).getScore());
			if (childArray.get(i).getScore() > score) {
				maxNode = childArray.get(i);
				score = maxNode.getScore();
			}
		}
		return maxNode;
	}
}
