package game;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NodeMCTS {
	
	BoardEvaluation boardEval = new BoardEvaluation();
	GameLogic logic = new GameLogic();
	NodeMCTS parentNode;
	List<NodeMCTS> childArray = new ArrayList<>();
	char[] boardState;
	int visitCount;
	char currentPlayer;
	char nextPlayer;
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
	
	public void setParent(NodeMCTS parentNodeIn) {
		this.parentNode = parentNodeIn;
	}
	
	public void setPlayers(char currentPlayerIn, char nextPlayerIn) {
		this.currentPlayer = currentPlayerIn;
		this.nextPlayer = nextPlayerIn;
	}
	
	public void setPosition(int positionIn) {
		this.position = positionIn;
	}
	
	public void setScore(int scoreIn) {
		this.score = score + scoreIn;
	}
	
	public char[] getBoardState() {
		return boardState;
	}
	
	public List<NodeMCTS> getChildArray() {
		return childArray;
	}
	
	public char getCurrentPlayer() {
		return currentPlayer;
	}
	
	public char getNextPlayer() {
		return nextPlayer;
	}
	
	public NodeMCTS getParent() {
		return parentNode;
	}
	
	public int getPosition() {
		return position;
	}
	
	public int getScore() {
		return score;
	}
	
	public int getVisitCount() {
		return visitCount;
	}
	
	public void incrementVisitCount() {
		this.visitCount++;
	}
	
	// Create array of available moves.
	public void createChildArray() {
		int position;
		NodeMCTS parent = getParent();
		
		// If the node has a parent, players should be set to the opposite of the parents' players (current is next, next is current).
		// Else means the node does not have parents, it's the root, so players should be set to its own current and next.  
		if (parent != null) {
			logic.setPlayers(parent.getNextPlayer(), parent.getCurrentPlayer());
		} else {
			logic.setPlayers(currentPlayer, nextPlayer);
		}
		
		// Reset available squares list.
		for (int i = 0; i < this.boardState.length; i++) {
			if (this.boardState[i] == '-' || this.boardState[i] == 'a') {
				position = i;
				logic.setBoard(this.boardState);
				logic.setPosition(position);
				boolean successfulMove = logic.checkNextItem();
				if (successfulMove) {
					// Add to available squares list.
					NodeMCTS newNode = new NodeMCTS(this.nextPlayer);
					newNode.setPosition(position);
					char[] newBoardState = logic.getNewBoard();
					newNode.setBoardState(newBoardState);
					this.childArray.add(newNode);
				}
			}
		}
	}
	
	public char getWinState() {
		HashMap<String, Integer> results =  boardEval.returnResults(boardState);
		if (results.get("white") > results.get("black")) {
			return 'w';
		} else if (results.get("white") == results.get("black")) {
			return 'd';
		} else {
			return 'b';
		}
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
