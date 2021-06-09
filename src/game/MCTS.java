package game;


import java.util.List;
import java.util.Random;

public class MCTS {
	
	public int findNextMove(char[] boardIn, char currentPlayer, char nextPlayer, int level) {
		long start = System.currentTimeMillis();
        double end = start + 60 * 2;
        
        // Create a new tree.
        Tree tree = new Tree();
        // Get the root of that tree.
        NodeMCTS rootNode = tree.getRoot();
        // Set players of the root node according to the current state (passed into this function).
        rootNode.setPlayers(currentPlayer, nextPlayer);
        // Set board state of root node.
        rootNode.setBoardState(boardIn);
		// Run while there's time left, which is determined by the difficulty level.
		while (System.currentTimeMillis() < end) {
			// Selection - select a promising node from the rootNode.
			NodeMCTS promisingNode = selectPromisingNode(rootNode);
			// Expansion - expand the promising node selected in the previous method.
			NodeMCTS expandedNode = expandNode(promisingNode);
			// Simulation - take the expanded node and run a simulation of the game,
			// choosing random children until a terminal node is reached.
			NodeMCTS simulationResult = simulationPlayout(expandedNode);
			// Propagation - the score from the simulation needs to be propagated up the tree
			// until it reaches the original child node.
			backPropogation(simulationResult, currentPlayer);
		}
		// Get the root node's child that has the highest score.
		NodeMCTS winnerNode = rootNode.getChildMaxScore();
		System.out.println("pos: " + winnerNode.getPosition() + " score: " + winnerNode.getScore());
		// Return the position of the highest scoring node.
		return winnerNode.getPosition();
	}
	
	private NodeMCTS selectPromisingNode(NodeMCTS rootNodeIn) {
		// Successively select child nodes until a leaf node has been found.
		NodeMCTS node = rootNodeIn;
		List<NodeMCTS> children = node.getChildArray();
		// If root node does not have children yet, create a child array.
		if (children.size() == 0) {
			node.createChildArray();
			children = node.getChildArray();
			// Set parent and players for child nodes.
			for (int i = 0; i < children.size(); i++) {
				children.get(i).setParent(rootNodeIn);
				children.get(i).setPlayers(rootNodeIn.getNextPlayer(), rootNodeIn.getCurrentPlayer());
			}
		}
		
		// Run until a leaf node is found.
		while (node.getChildArray().size() > 0) {
			node = findBestNodeUTC(node);
		}
		return node;
	}
	
	private NodeMCTS expandNode(NodeMCTS node) {
		// Use promising node, expand again to create a child that will be used in simulation.
		node.createChildArray();
		List<NodeMCTS> children = node.getChildArray();
		
		for (int i = 0; i < children.size(); i++) {
			children.get(i).setParent(node);
			children.get(i).setPlayers(node.getNextPlayer(), node.getCurrentPlayer());
		}
		// Chose random node to expand.
		if (children.size() > 0) {
			Random random = new Random();
		    int randomNode = random.nextInt(children.size());
		    node = children.get(randomNode);
		}
		return node;
	}
	
	private NodeMCTS simulationPlayout(NodeMCTS node) {
		node.createChildArray();
		List<NodeMCTS> children = node.getChildArray();
		while (children.size() > 0) {
			for (int i = 0; i < children.size(); i++) {
				children.get(i).setParent(node);
				children.get(i).setPlayers(node.getNextPlayer(), node.getCurrentPlayer());
			}
			Random random = new Random();
		    int randomNode = random.nextInt(children.size());
			node = children.get(randomNode);
			node.createChildArray();
			children = node.getChildArray();
		}
		return node;
	}
	
	private void backPropogation(NodeMCTS nodeToExplore, char currentPlayer) {
		char aiWin = nodeToExplore.getWinState();
		int score;
		if (aiWin == currentPlayer) {
			score = 10;
		} else {
			score = 0;
		}
		NodeMCTS tempNode = nodeToExplore;
		while (tempNode != null) {
			tempNode.setScore(score);
			tempNode.incrementVisitCount();
			tempNode = tempNode.getParent();
		}
	}
	
	private NodeMCTS findBestNodeUTC(NodeMCTS node) {
		int parentVisit = node.getVisitCount();
	    
	    // Create child array.
	    List<NodeMCTS> children = node.getChildArray();
	    // Loop through to find the best node using UTC tree policy.
	    NodeMCTS bestNodeUTC = children.get(0);
	    double utcValue = 0;
	    double utcValueTemp = 0;
	    for (int i = 0; i < children.size(); i++) {
	    	int totalVisit = parentVisit;
	    	int nodeWinScore = children.get(i).getScore();
	    	int nodeVisit = children.get(i).getVisitCount();
	    	
	    	// If a node has not yet been visited, it should be returned to be explored.
	    	if (nodeVisit == 0) {
	    		return children.get(i);
	    	}
	    	
	    	utcValueTemp = (nodeWinScore / nodeVisit) +  1.41*Math.sqrt(Math.log(totalVisit) / nodeVisit);
	    	if (utcValueTemp > utcValue) {
	    		bestNodeUTC = children.get(i);
	    		utcValue = utcValueTemp;
	    	}
	    }
	    return bestNodeUTC;
	}
}
