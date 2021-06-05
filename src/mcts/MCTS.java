package mcts;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

// Good reference: https://towardsdatascience.com/monte-carlo-tree-search-in-reinforcement-learning-b97d3e743d0f

public class MCTS {
	
	public int findNextMove(char[] boardIn, char currentPlayer, char nextPlayer, int level) {
		long start = System.currentTimeMillis();
        double end = start + 60 * level;
        
        // Create a new tree.
        Tree tree = new Tree();
        // Get the root of that tree.
        NodeMCTS rootNode = tree.getRoot();
        rootNode.setPlayers(currentPlayer, nextPlayer);
        
		// While there's still time.
        //int count = 0;
		while (System.currentTimeMillis() < end) {
//        while (count < 50000) {
			// Create a node - select a promising node from the rootNode.
			NodeMCTS promisingNode = selectPromisingNode(rootNode, boardIn);
			// If not end state
			// TODO: why expand rootnode instead of promising node? 
			NodeMCTS playMeNode = expandNode(rootNode);
			// Simulation.
			NodeMCTS playoutResult = simulationPlayout(playMeNode);
			backPropogation(playoutResult, currentPlayer);
			//count++;
		}
		NodeMCTS winnerNode = rootNode.getChildMaxScore();
		System.out.println(winnerNode.getScore());
		//return winnerNode.getBoardState();
		return winnerNode.getPosition();
	}
	
	private NodeMCTS expandNode(NodeMCTS node) {
		// Get available moves.
//		System.out.println(node.getBoardState());
//		System.out.println(node.getCurrentPlayer());
		node.incrementVisitCount();
		node.createChildArray();
		List<NodeMCTS> children = node.getChildArray();
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
		
		node.createChildArray();
		List<NodeMCTS> children = node.getChildArray();
		while (children.size() > 0) {
			Random random = new Random();
		    int randomNode = random.nextInt(children.size());
			node = children.get(randomNode);
			node.createChildArray();
			children = node.getChildArray();
			for (int i = 0; i < children.size(); i++) {
				children.get(i).setParent(node);
			}
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
			tempNode = tempNode.getParent();
		}
	}
	
	private NodeMCTS selectPromisingNode(NodeMCTS rootNodeIn, char[] boardIn) {
		NodeMCTS node = rootNodeIn;
		node.setBoardState(boardIn);
		List<NodeMCTS> children = node.getChildArray();
		if (children.size() == 0) {
			node.createChildArray();
			children = node.getChildArray();
		}
		//System.out.println(children);
//		node = children.get(0);
//		node.setPlayers('w', 'b');
//		node.setParent(rootNodeIn);
		//System.out.println(node);
		while (node.getChildArray().size() > 0) {
			//System.out.println(node.getChildArray());
			node = findBestNodeUTC(node);
			node.setParent(rootNodeIn);
			//System.out.println(node);
		 //if node has not been explored -> break.
//			if(node.getVisitCount() == 0) {
//				break;
//			}
		}
		return node;
	}
	
	private NodeMCTS findBestNodeUTC(NodeMCTS node) {
		int parentVisit = node.getVisitCount();
		return Collections.max(node.getChildArray(), Comparator.comparing(c->utcValue(parentVisit, c.getScore(), c.getVisitCount())));
	}
	
	private double utcValue(int totalVisit, double nodeWinScore, int nodeVisit) {
		if (nodeVisit == 0) {
			return Integer.MAX_VALUE;
		}
		return ((double) nodeWinScore / (double) nodeVisit) +  1.41*Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
	}

}
