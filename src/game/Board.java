package game;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import mcts.MCTS;
import minimax.AlphaBeta;
import minimax.Minimax;

public class Board extends Application {
	
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
	
	// Make GridPane available outside the `start` function be able to select board squares when updating UI.
	GridPane gridPane;
	
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
	int controlLevel = 2;
	int variableLevel = 4;
	
	// Choice of algorithm.
	// Choices: "minimax", "alpha-beta", "mcts".
	String algorithmChoice = "minimax";
	
	@Override
	public void start(Stage stage) throws Exception {
		
		try {
			// Set title for stage.
			stage.setTitle("IRP: Othello Prototype");
			
			// Create a grid pane.
			gridPane = new GridPane();
			gridPane.setPadding(new Insets(10,100,10,100));
			
			// Create items for gridPane.
			Text title = new Text("Othello"); 
			HBox titleHBox = new HBox();
			titleHBox.setPadding(new Insets(10,0,10,0));
			titleHBox.setAlignment(Pos.CENTER);
			titleHBox.getChildren().addAll(title);
			GridPane.setConstraints(titleHBox, 0, 0);
			GridPane.setColumnSpan(titleHBox, 8);
			
			// Create board.
			int rowCounter = 1;
			int colCounter = 0;
			for (int i = 0; i < board.length; i++) {
				Button boardButton;
				if (board[i] == 'b') {
					Image imageOk = new Image(getClass().getResourceAsStream("../othello-disc-black.png"));
					boardButton = new Button("", new ImageView(imageOk));
				} else if (board[i] == 'w') {
					Image imageOk = new Image(getClass().getResourceAsStream("../othello-disc-white.png"));
					boardButton = new Button("", new ImageView(imageOk));
				} else if (board[i] == 'a') {
					Image imageAvailableSquare = new Image(getClass().getResourceAsStream("../available-square.png"));
					boardButton = new Button("", new ImageView(imageAvailableSquare));
				} else {
					boardButton = new Button();
				}
				
				// Convert integer counter to String in order to use it as board button ID.
				String idString = Integer.toString(i);
				boardButton.setId(idString);
				
				// Button sizing.
				boardButton.setMinWidth(40);
				boardButton.setMinHeight(40);
				boardButton.setMaxWidth(40);
				boardButton.setMaxHeight(40);
				
				// Store position in variable and use it in lambda function.
				int boardPos = i;
				boardButton.setOnAction(e -> handleSquareClick(boardPos));

				// Add to pane
				GridPane.setConstraints(boardButton, colCounter, rowCounter);
				gridPane.getChildren().addAll(boardButton);
				// Add CSS class
				boardButton.getStyleClass().add("button");
				
				// End of row - add 1 to rowCounter and reset colCounter.
				if (i % 8 == 7) {
					rowCounter++;
					colCounter = 0;
				} else {
					colCounter++;
				}
			}
			
			// Current player
			Text currentPlayer = new Text("Current player: b"); 
			HBox currentPlayerHBox = new HBox();
			currentPlayerHBox.setPadding(new Insets(10,0,10,0));
			currentPlayerHBox.setAlignment(Pos.CENTER);
			currentPlayerHBox.getChildren().addAll(currentPlayer);
			currentPlayer.setId("currenPlayerText");
			GridPane.setConstraints(currentPlayerHBox, 0, 9);
			GridPane.setColumnSpan(currentPlayerHBox, 8);
			
			// History buttons
			Button backButton = new Button("Back"); 
			backButton.setId("backButton");
			backButton.setOnAction(e -> handleBackClick());
			Button forwardButton = new Button("Forward");
			forwardButton.setId("forwardButton");
			forwardButton.setDisable(true);
			forwardButton.setOnAction(e -> handleForwardClick());
			HBox historyButtonsHBox = new HBox();
			historyButtonsHBox.setPadding(new Insets(10,0,10,0));
			historyButtonsHBox.setAlignment(Pos.CENTER);
			historyButtonsHBox.getChildren().addAll(backButton, forwardButton);
			GridPane.setConstraints(historyButtonsHBox, 0, 10);
			GridPane.setColumnSpan(historyButtonsHBox, 8);
			
			// Start button runs AI move.
			Button startButton = new Button("AI move"); 
			startButton.setOnAction(e -> runAISearch());
			startButton.getStyleClass().add("button-start");
			HBox startButtonsHBox = new HBox();
			startButtonsHBox.setPadding(new Insets(10,0,10,0));
			startButtonsHBox.setAlignment(Pos.CENTER);
			startButtonsHBox.getChildren().addAll(startButton);
			GridPane.setConstraints(startButtonsHBox, 0, 11);
			GridPane.setColumnSpan(startButtonsHBox, 8);
			
			// Results.
			Text resultsText = new Text(); 
			HBox resultsTextHBox = new HBox();
			resultsTextHBox.setPadding(new Insets(10,0,10,0));
			resultsTextHBox.setAlignment(Pos.CENTER);
			resultsTextHBox.getChildren().addAll(resultsText);
			resultsText.setId("resultsText");
			GridPane.setConstraints(resultsTextHBox, 0, 12);
			GridPane.setColumnSpan(resultsTextHBox, 8);
			
			// Reset button.
			Button resetButton = new Button("Reset game"); 
			resetButton.setOnAction(e -> handleResetGame());
			resetButton.getStyleClass().add("button-reset");
			HBox resetButtonsHBox = new HBox();
			resetButtonsHBox.setPadding(new Insets(10,0,10,0));
			resetButtonsHBox.setAlignment(Pos.CENTER);
			resetButtonsHBox.getChildren().addAll(resetButton);
			GridPane.setConstraints(resetButtonsHBox, 0, 13);
			GridPane.setColumnSpan(resetButtonsHBox, 8);
			
			// Add items to the gridPane
			gridPane.getChildren().addAll(titleHBox, currentPlayerHBox, historyButtonsHBox, startButtonsHBox, resultsTextHBox, resetButtonsHBox);

			Group root = new Group(gridPane);
			Scene scene = new Scene(root,520,700);
			scene.getStylesheets().add(getClass().getResource("../application.css").toExternalForm());
			stage.setScene(scene);
			stage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handleResetGame() {
		board = boardStarter;
		prevBoard = boardStarter;
		prevPosition = 0;
		currentPlayer = 'b';
		nextPlayer = 'w';
		Text resultsText = (Text) gridPane.lookup("#resultsText");
		resultsText.setText("");
		updateBoard();
	}
	
	// Create array of available moves.
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
		updatePlayers();
		updatePlayerText();
		resetAvailableMoves();
		checkAvailableMoves();
		updateBoard();
	}
	
	private void handleForwardClick() {
		board = nextBoard;
		boardLocked = false;
		updateState();
		
		Button backButton = (Button) gridPane.lookup("#backButton");
		backButton.setDisable(false);
		
		Button forwardButton = (Button) gridPane.lookup("#forwardButton");
		forwardButton.setDisable(true);
	}

	private void handleBackClick() {
		nextBoard = board;
		board = prevBoard;
		boardLocked = true;
		updateState();
		
		Button backButton = (Button) gridPane.lookup("#backButton");
		backButton.setDisable(true);
		
		Button forwardButton = (Button) gridPane.lookup("#forwardButton");
		forwardButton.setDisable(false);
	}
	
	private void handleSquareClick(int positionIn) {
		System.out.println(boardLocked);
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
				if (availableSquares.size() == 0) {
					checkWinner();
				}
			}
		}
	}
	
	private void runAISearch() {
		char[] boardEvalClone = board.clone();
		int level;
		if (currentPlayer == 'b') {
			level = controlLevel;
		} else {
			level = variableLevel;
		}
		// Run correct algorithm.
		int positionAI = -1;
		if (algorithmChoice == "minimax") {
			minimaxSearch.setBoard(board.clone());
			minimaxSearch.setPlayers(currentPlayer, nextPlayer);
			positionAI = minimaxSearch.runMinimax(level);
		} else if (algorithmChoice == "alpha-beta") {
			alphaBeta.setBoard(board.clone());
			alphaBeta.setPlayers(currentPlayer, nextPlayer);
			positionAI = alphaBeta.runAlphaBeta(level);
		} else if (algorithmChoice == "mcts") {
			positionAI = mcts.findNextMove(boardEvalClone, currentPlayer, nextPlayer, level);
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
		if (availableSquares.size() == 0) {
			checkWinner();
		}
	}
	 
	private void updateBoard() {
		for (int i = 0; i < board.length; i++) {
			String buttonID = Integer.toString(i);
			Button boardButtonSelect = (Button) gridPane.lookup("#"+buttonID);
			// First, if board is locked that means the history button has been clicked -> show the previous move.
			if (boardLocked && i == prevPosition) {
				Image imageClickedSquare = new Image(getClass().getResourceAsStream("../clicked-position.png"));
				boardButtonSelect.setGraphic(new ImageView(imageClickedSquare));
			// If the board is not locked, assign images according to chars in board array.
			} else if (board[i] == 'b') {
				Image imageBlackDisc = new Image(getClass().getResourceAsStream("../othello-disc-black.png"));
				boardButtonSelect.setGraphic(new ImageView(imageBlackDisc));
			} else if (board[i] == 'w') {
				Image imageWhiteDisc = new Image(getClass().getResourceAsStream("../othello-disc-white.png"));
				boardButtonSelect.setGraphic(new ImageView(imageWhiteDisc));
			} else if (board[i] == 'a') {
				Image imageAvailableSquare = new Image(getClass().getResourceAsStream("../available-square.png"));
				boardButtonSelect.setGraphic(new ImageView(imageAvailableSquare));
			} else if (board[i] == '-') {
				boardButtonSelect.setGraphic(null);
			}
		}
	}

	private void updatePlayerText() {
		// Lookup the Text node with the CSS ID set earlier and set text to currentPlayer.
		Text playerText = (Text) gridPane.lookup("#currenPlayerText");
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
		for (int i = 0; i < board.length; i++) {
			if (board[i] == '-') {
				// If there is an empty square but no available moves -> change to next player.
				updateState();
				return;
			}
		}
		results = boardEval.returnResults(board);
		int whiteDiscs = results.get("white");
		int blackDiscs = results.get("black");
		System.out.println(results);
		Text resultsText = (Text) gridPane.lookup("#resultsText");
		resultsText.setText("White: " + whiteDiscs + "\nBlack: " + blackDiscs);
	}
	
	public static void main(String[] args) {

		// Launch JavaFX UI.
		launch(args);

	}

}

// TODO: scenario - one player flips all of opponent's discs without finishing game. 
