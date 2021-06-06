package mcts;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

// Good reference: https://towardsdatascience.com/monte-carlo-tree-search-in-reinforcement-learning-b97d3e743d0f

public class MCTS {
	
	public int findNextMove(char[] boardIn, char currentPlayer, char nextPlayer, int level) {
		long start = System.currentTimeMillis();
        double end = start + 60 * 1;
        
        // Create a new tree.
        Tree tree = new Tree();
        // Get the root of that tree.
        NodeMCTS rootNode = tree.getRoot();
        // Set players of the root node according to the current state (passed into this function).
        rootNode.setPlayers(currentPlayer, nextPlayer);
        // Set board state of root node.
        rootNode.setBoardState(boardIn);
		// Run while there's time left, which is determined by the difficulty level.
        int count = 0;
		while (System.currentTimeMillis() < end) {
//        while (count < 1) {
			// Create a node - select a promising node from the rootNode.
			NodeMCTS promisingNode = selectPromisingNode(rootNode);
			
			// If not end state
			// TODO: why expand rootnode instead of promising node? 
			NodeMCTS expandedNode = expandNode(promisingNode);
//			// Simulation.
			NodeMCTS simulationResult = simulationPlayout(expandedNode);
			backPropogation(simulationResult, currentPlayer);
			count++;
		}
		NodeMCTS winnerNode = rootNode.getChildMaxScore();
		System.out.println("pos: " + winnerNode.getPosition() + " score: " + winnerNode.getScore());
		//return winnerNode.getBoardState();
		return winnerNode.getPosition();
//		return 5;
	}
	
	private NodeMCTS selectPromisingNode(NodeMCTS rootNodeIn) {
		// Successively select child nodes until a leaf node has been found.
		NodeMCTS node = rootNodeIn;
		List<NodeMCTS> children = node.getChildArray();
		// If root node does not have children yet, create a child array.
		if (children.size() == 0) {
			System.out.println("no children");
			node.createChildArray();
			children = node.getChildArray();
			// Set parent and players for child nodes.
			for (int i = 0; i < children.size(); i++) {
				children.get(i).setParent(rootNodeIn);
				children.get(i).setPlayers(rootNodeIn.getNextPlayer(), rootNodeIn.getCurrentPlayer());
//				System.out.println("Current player: " +children.get(i).getCurrentPlayer());
//				System.out.println(children.get(i).getBoardState());
			}
		}
		//System.out.println(children);
//		node = children.get(0);
//		node.setPlayers('w', 'b');
//		node.setParent(rootNodeIn);
		//System.out.println(node);
		
		// Run until a leaf node is found (without a child array).
		while (node.getChildArray().size() > 0) {
			node = findBestNodeUTC(node);
			//node.setParent(rootNodeIn);
		}
		return node;
	}
	
	private NodeMCTS expandNode(NodeMCTS node) {
		System.out.println("EXPAND");
		// Use promising node, expand again to create a child that will be used in simulation.
//		System.out.println(node.getBoardState());
//		System.out.println(node.getCurrentPlayer());
		node.incrementVisitCount();
		node.createChildArray();
		List<NodeMCTS> children = node.getChildArray();
		System.out.println(children);
		
		for (int i = 0; i < children.size(); i++) {
			children.get(i).setParent(node);
			children.get(i).setPlayers(node.getNextPlayer(), node.getCurrentPlayer());
//			System.out.println("Current player: " +children.get(i).getCurrentPlayer());
//			System.out.println(children.get(i).getBoardState());
		}
		//System.out.println(children);
		//System.out.println(node.getBoardState());
		// Chose random node to expand.
		if (children.size() > 0) {
			
			//System.out.println('f');
			Random random = new Random();
		    int randomNode = random.nextInt(children.size());
		    node = children.get(randomNode);
		}
		return node;
	}
	
	private NodeMCTS simulationPlayout(NodeMCTS node) {
		System.out.println("SIMULATION");
		// TODO: needs to run simulation to end of game.
		node.createChildArray();
		List<NodeMCTS> children = node.getChildArray();
		
//		System.out.println(children);
//		System.out.println(node.getParent());
		int counter = 0;
		while (children.size() > 0) {
			counter++;
//			System.out.println("player simulation: " + node.getCurrentPlayer());
//			System.out.println( node.getBoardState());
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
		System.out.println("counter: " + counter);
		return node;
	}
	
	private void backPropogation(NodeMCTS nodeToExplore, char currentPlayer) {
		char aiWin = nodeToExplore.getWinState();
		System.out.println("winner char: " + aiWin);
		int score;
		if (aiWin == currentPlayer) {
			score = 10;
		} else {
			score = 0;
		}
		NodeMCTS tempNode = nodeToExplore;
		while (tempNode != null) {
			tempNode.setScore(score);
			//System.out.println(tempNode.getScore());
			tempNode = tempNode.getParent();
			//System.out.println("TEMPSCORE: " + aiWin + " " + score);
			
			//System.out.println(tempNode.getBoardState());
		}
	}
	
	private NodeMCTS findBestNodeUTC(NodeMCTS node) {
		int parentVisit = node.getVisitCount();
		//System.out.println("PARENT score: " + node.getScore());
//		return Collections.max(node.getChildArray(), Comparator.comparing(c->utcValue(parentVisit, c.getScore(), c.getVisitCount())));
	
		List<NodeMCTS> children = node.getChildArray();
		Random random = new Random();
	    int randomNode = random.nextInt(children.size());
	    NodeMCTS returnNode = children.get(randomNode);
	    return returnNode;
	}
	
	private double utcValue(int totalVisit, double nodeWinScore, int nodeVisit) {
		if (nodeVisit == 0) {
			return Integer.MAX_VALUE;
		}
		//System.out.println("total visit: " + totalVisit);
		//System.out.println("node win score: " + nodeWinScore);
		//System.out.println("node visit: " + nodeVisit);
		return ((double) nodeWinScore / (double) nodeVisit) +  1.41*Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
	}

}
