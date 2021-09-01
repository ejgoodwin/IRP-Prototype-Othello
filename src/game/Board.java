package game;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class Board {
	
	JFrame frame;
	JPanel boardPanel;
	JPanel textPanel;
	
	// Boards: current, previous and next.
	// A starter board is kept ready to be assigned to `board` when the game is reset.
	char[] boardStarter = {
		'-','-','-','-','-','-','-','-',
		'-','-','-','-','-','-','-','-',
		'-','-','-','-','a','-','-','-',
		'-','-','-','b','w','a','-','-',
		'-','-','a','w','b','-','-','-',
		'-','-','-','a','-','-','-','-',
		'-','-','-','-','-','-','-','-',
		'-','-','-','-','-','-','-','-',
	};
	char[] board = boardStarter.clone();
	char[] prevBoard = new char[64];
	char[] nextBoard = new char[64];
	
	// Information for board state.
	char currentPlayer = 'b';
	char nextPlayer = 'w';
	int position;
	// Used with history functionality.
	char prevPlayer;
	boolean boardLocked = false;
	int prevPosition;
	
	// Logic contains rules of the game. Used to check available moves and make a move.
	GameLogic logic = new GameLogic();
	
	// Holds list of available squares. Used to check if players can move or if they need to pass.
	ArrayList<Integer> availableSquares = new ArrayList<Integer>();
	
	// Hold results when game terminates.
	HashMap<String,Integer> results = new HashMap<String,Integer>();
	
	// Evaluates end of game to find winner.
	BoardEvaluation boardEval = new BoardEvaluation();
	
	// AI Search.
	Minimax minimaxSearch = new Minimax();
	AlphaBeta alphaBeta = new AlphaBeta();
	MCTS mcts = new MCTS();
	
	// Difficulty level data.
	int controlLevel = 4;
	int variableLevel = 4;
	
	// Previous winner.
	char prevWinner;
	char variablePlayer = 'b';
	
	// Choice of algorithm.
	// Choices: "minimax", "alpha-beta", "mcts".
	String algorithmChoice = "mcts";
	
	public void startUI() {
		// Build UI using Java Swing.
		frame = new JFrame("IRP Prototype: Othello");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());
		boardPanel = new JPanel(new GridLayout(8,8));
		boardPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
		boardPanel.setLayout(new GridLayout(8,8));
		textPanel = new JPanel();
		textPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		
		for (int i = 0; i < board.length; i++) {
			JButton button = new JButton();
			button.setPreferredSize(new Dimension(40, 40));
			button.setBackground(Color.decode("#399E41"));
			button.setOpaque(true);
			button.setBorder(new LineBorder(Color.BLACK));
			BufferedImage imageButton;
			try {
				if (board[i] == 'b') {
					imageButton = ImageIO.read(getClass().getResource("../othello-disc-black.png"));
					button.setIcon(new ImageIcon(imageButton));
				} else if (board[i] == 'w') {
					imageButton = ImageIO.read(getClass().getResource("../othello-disc-white.png"));
					button.setIcon(new ImageIcon(imageButton));
				} else if (board[i] == 'a') {
					button.setBackground(Color.decode("#F0E79C"));
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			int buttonPosition = i;
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println(buttonPosition);
					handleSquareClick(buttonPosition);
				}
			});
			boardPanel.add(button);
		}
		
		// Current player text.
		JPanel currentPlayerPanel = new JPanel();
		currentPlayerPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));
		JLabel currentPlayerText = new JLabel("Current player: b");
		currentPlayerText.setAlignmentX(Component.CENTER_ALIGNMENT);
		currentPlayerPanel.add(currentPlayerText);
		textPanel.add(currentPlayerPanel);
		
		// History buttons.
		JPanel historyButtonsPanel = new JPanel();
		JButton backButton = new JButton("Back");
		backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleBackClick();
			}
		});
		JButton forwardButton = new JButton("Forward");
		forwardButton.setEnabled(false);
		forwardButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		forwardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleForwardClick();
			}
		});
		historyButtonsPanel.add(backButton);
		historyButtonsPanel.add(forwardButton);
		
		// AI move button.
		JButton aiButton = new JButton("AI move");
		aiButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		aiButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runAISearch();
			}
		});
		
		// Display Results.
		JLabel resultsLabel = new JLabel(" ");
		resultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		// Reset Button.
		JPanel resetButtonPanel = new JPanel();
		resetButtonPanel.setBorder(BorderFactory.createEmptyBorder(32, 16, 16, 16));
		JButton resetButton = new JButton("Reset game");
		resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleResetGame();
			}
		});
		resetButtonPanel.add(resetButton);
		
		textPanel.add(aiButton);
		textPanel.add(historyButtonsPanel);
		textPanel.add(resultsLabel);
		textPanel.add(resetButtonPanel);
		// Add board and text panels to frame.
		frame.add(boardPanel);
		frame.add(textPanel);
		
		frame.setSize(450,650); 
		frame.setMaximumSize(new Dimension(450, 650));
		frame.setVisible(true);  
	}
	
	private void handleResetGame() {
		board = boardStarter;
		prevBoard = boardStarter;
		prevPosition = 0;
		currentPlayer = 'b';
		nextPlayer = 'w';
		
		JPanel playerTextPanel = (JPanel) textPanel.getComponent(0);
		JLabel playerText = (JLabel) playerTextPanel.getComponent(0);
		playerText.setText("Current player: " + currentPlayer);
		
		JLabel resultsLabel = (JLabel) textPanel.getComponent(3);
		resultsLabel.setText("");
		updateBoard();
	}
	
	// Create array of available moves and update the board to reflect state with available moves.
	private void checkAvailableMoves() {
		int position;
		logic.setPlayers(currentPlayer, nextPlayer);
		// Reset available squares list.
		availableSquares.clear();
		for (int i = 0; i < board.length; i++) {
			if (board[i] == '-') {
				position = i;
				logic.setBoard(board);
				logic.setPosition(position);
				boolean successfulMove = logic.checkNextItem();
				if (successfulMove) {
					// This move is available -> update board array because array is used to render/update board UI.
					board[i] = 'a';
					// Add to available squares list.
					availableSquares.add(i);
				}
			}
		}
	}
	
	private void updateState() {
		// Update players, player text, available moves, and board.
		updatePlayers();
		updatePlayerText();
		resetAvailableMoves();
		checkAvailableMoves();
		updateBoard();
	}
	
	private void handleForwardClick() {
		// Updates board to reflect current state.
		board = nextBoard;
		boardLocked = false;
		updateState();
	
		JPanel historyPanel = (JPanel) textPanel.getComponent(2);
		JButton forwardButton = (JButton) historyPanel.getComponent(1);
		JButton backButton = (JButton) historyPanel.getComponent(0);
		
		backButton.setEnabled(true);
		forwardButton.setEnabled(false);
	}

	private void handleBackClick() {
		// Updates board to reflect previous state.
		nextBoard = board;
		board = prevBoard;
		// Lock board so user cannot click. They must return to current state using forward button.
		boardLocked = true;
		updateState();
			
		JPanel historyPanel = (JPanel) textPanel.getComponent(2);
		JButton forwardButton = (JButton) historyPanel.getComponent(1);
		JButton backButton = (JButton) historyPanel.getComponent(0);
		
		backButton.setEnabled(false);
		forwardButton.setEnabled(true);
	}
	
	private void handleSquareClick(int positionIn) {
		// To make a move, the chosen square must be available and the board must not be locked.
		 if (board[positionIn] == 'a' && boardLocked == false) {
			logic.setPlayers(currentPlayer, nextPlayer);
			logic.setBoard(board);
			logic.setPosition(positionIn);
			boolean successfulMove = logic.checkNextItem();
			if (successfulMove) {
				prevBoard = board;
				board = logic.getNewBoard();
				prevPosition = positionIn;
				prevPlayer = currentPlayer;
				updateState();
				checkWinner();
			}
		}
	}
	
	private void runAISearch() {
		char[] boardEvalClone = board.clone();
		int level;
		if (currentPlayer == variablePlayer) {
			level = variableLevel;
		} else {
			level = controlLevel;
		}
		// Run correct algorithm.
		int positionAI = -1;
		if (algorithmChoice == "minimax") {
			minimaxSearch.setBoard(board.clone());
			minimaxSearch.setPlayers(currentPlayer, nextPlayer);
			double start = System.currentTimeMillis();
			positionAI = minimaxSearch.runMinimax(level);
			double end = System.currentTimeMillis();
			System.out.print("Decision time: ");
			System.out.println(end-start);
		} else if (algorithmChoice == "alpha-beta") {
			alphaBeta.setBoard(board.clone());
			alphaBeta.setPlayers(currentPlayer, nextPlayer);
			double start = System.currentTimeMillis();
			positionAI = alphaBeta.runAlphaBeta(level);
			double end = System.currentTimeMillis();
			System.out.print("Alpha-beta | Decision time: ");
			System.out.println(end-start);
		} else if (algorithmChoice == "mcts") {
			double start = System.currentTimeMillis();
			positionAI = mcts.findNextMove(boardEvalClone, currentPlayer, nextPlayer, level);
			double end = System.currentTimeMillis();
			System.out.print("MCTS | Decision time: ");
			System.out.println(end-start);
		}
		
		logic.setPlayers(currentPlayer, nextPlayer);
		logic.setBoard(board);
		logic.setPosition(positionAI);
		logic.checkNextItem();
		prevBoard = board;
		board = logic.getNewBoard();
		prevPosition = positionAI;
		prevPlayer = currentPlayer;
		updateState();
		checkWinner();
	}
	 
	private void updateBoard() {
		for (int i = 0; i < board.length; i++) {	
			// First, if board is locked that means the history button has been clicked -> show the previous move.
			try {
				BufferedImage imageButton;
				JButton boardButton = (JButton) boardPanel.getComponent(i);
				if (boardLocked && i == prevPosition) {
					boardButton.setBackground(Color.decode("#8CEBD2"));
					boardButton.setIcon(null);
				} else if (board[i] == 'b') {
					imageButton = ImageIO.read(getClass().getResource("../othello-disc-black.png"));
					boardButton.setIcon(new ImageIcon(imageButton));
					boardButton.setBackground(Color.decode("#399E41"));
				} else if (board[i] == 'w') {
					imageButton = ImageIO.read(getClass().getResource("../othello-disc-white.png"));
					boardButton.setIcon(new ImageIcon(imageButton));
					boardButton.setBackground(Color.decode("#399E41"));
				} else if (board[i] == 'a') {
					boardButton.setBackground(Color.decode("#F0E79C"));
					boardButton.setIcon(null);
				} else if (board[i] == '-') {
					boardButton.setIcon(null);
					boardButton.setBackground(Color.decode("#399E41"));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void updatePlayerText() {
		// Lookup the Text node and set text to currentPlayer.
		JPanel playerTextPanel = (JPanel) textPanel.getComponent(0);
		JLabel playerText = (JLabel) playerTextPanel.getComponent(0);
		if (boardLocked) {
			playerText.setText("Current player: " + prevPlayer);
		} else {
			playerText.setText("Current player: " + currentPlayer);
		}
	}

	private void updatePlayers() {
		if (currentPlayer == 'b') {
			currentPlayer = 'w';
			nextPlayer = 'b';
		} else {
			currentPlayer = 'b';
			nextPlayer = 'w';
		}
	}

	private void resetAvailableMoves() {
		for (int i = 0; i < board.length; i++) {
			if (board[i] == 'a') {
				board[i] = '-';
			}
		}
	}
	
	private void checkWinner() {
		// If no available moves, check to see if game is over.
		JLabel resultsLabel = (JLabel) textPanel.getComponent(3);
		if (availableSquares.size() == 0) {
			for (int i = 0; i < board.length; i++) {
				if (board[i] == '-') {
					// If there is an empty square but no available moves -> change to next player.
					// Set a notification to tell user they are skipping a turn.
					resultsLabel.setForeground(Color.RED);
					resultsLabel.setText("Skip a turn");
					updateState();
					return;
				}
			}
			results = boardEval.returnResults(board);
			String resultsText;
			int whiteDiscs = results.get("white");
			int blackDiscs = results.get("black");
			System.out.println(results);
			
			if (whiteDiscs > blackDiscs) {
				resultsText = "Results: White " + whiteDiscs + " |  Black " + blackDiscs;
				prevWinner = 'w';
			} else {
				resultsText = "Results: Black " + blackDiscs + " |  White " + whiteDiscs;
				prevWinner = 'b';
			}
			
			resultsLabel.setForeground(Color.decode("#268914"));
			resultsLabel.setText(resultsText);
			
			if (prevWinner == variablePlayer) {
				if (variableLevel == 2) {
					variableLevel-=1;
				} else if (variableLevel > 2) {
					variableLevel-=2;
				}
			} else {
				variableLevel+=2;
			}
		} else {
			// Ensure results is cleared from any skip notification.  
			resultsLabel.setText("");
		}
	}
}
