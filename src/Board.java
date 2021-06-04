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

public class Board extends Application {
	
	// Boards: current, previous and next.
	char[] board = {
		'-','-','-','-','-','-','-','-',
		'-','-','-','-','-','-','-','-',
		'-','-','-','-','-','-','-','-',
		'-','-','-','b','w','-','-','-',
		'-','-','-','w','b','-','-','-',
		'-','-','-','-','-','-','-','-',
		'-','-','-','-','-','-','-','-',
		'-','-','-','-','-','-','-','-',
	};
	char[] prevBoard = new char[64];
	char[] nextBoard = new char[64];
	
	// Information for board state.
	int position;
	char currentPlayer = 'b';
	char nextPlayer = 'w';
	boolean boardLocked = false;
	
	// Make GridPane available outside the `start` function be able to select board squares when updating UI.
	GridPane gridPane;
	
	// Logic -> rules of the game. Used to check available moves and make a move.
	GameLogic logic = new GameLogic();
	
	// Holds list of available squares. Used to check if players can move or they need to pass.
	ArrayList<Integer> availableSquares = new ArrayList<Integer>();
	
	// Hold results when game terminates.
	HashMap<String,Integer> results = new HashMap<String,Integer>();
	
	// Evaluates end of game to find winner.
	BoardEvaluation boardEval = new BoardEvaluation();
	
	// AI Search.
	Minimax minimaxSearch = new Minimax();
	
	// Difficulty level data.
	int controlLevel = 2;
	int variableLevel = 4;
	
	@Override
	public void start(Stage stage) throws Exception {
		
		try {
			// Check for available moves before rendering UI so that available squares are coloured correctly.
			checkAvailableMoves();
			
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
					Image imageOk = new Image(getClass().getResourceAsStream("othello-disc-black.png"));
					boardButton = new Button("", new ImageView(imageOk));
				} else if (board[i] == 'w') {
					Image imageOk = new Image(getClass().getResourceAsStream("othello-disc-white.png"));
					boardButton = new Button("", new ImageView(imageOk));
				} else if (board[i] == 'a') {
					Image imageAvailableSquare = new Image(getClass().getResourceAsStream("available-square.png"));
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
				//boardButton.setOnAction(e -> handleSquareClick(boardPos));
				
				boardButton.setOnAction(e -> {
					handleSquareClick(boardPos);
					updateBoard();
					runAISearch();
				});

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
			Text currentPlayer = new Text("Current player: Black"); 
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
			//backButton.setOnAction(e -> handleBackClick());
			Button forwardButton = new Button("Forward");
			forwardButton.setId("forwardButton");
			forwardButton.setDisable(true);
			//forwardButton.setOnAction(e -> handleForwardClick());
			HBox historyButtonsHBox = new HBox();
			historyButtonsHBox.setPadding(new Insets(10,0,10,0));
			historyButtonsHBox.setAlignment(Pos.CENTER);
			historyButtonsHBox.getChildren().addAll(backButton, forwardButton);
			GridPane.setConstraints(historyButtonsHBox, 0, 10);
			GridPane.setColumnSpan(historyButtonsHBox, 8);
			
			Button startButton = new Button("AI move"); 
			startButton.setOnAction(e -> handleStartClick());
			HBox startButtonsHBox = new HBox();
			startButtonsHBox.setPadding(new Insets(10,0,10,0));
			startButtonsHBox.setAlignment(Pos.CENTER);
			startButtonsHBox.getChildren().addAll(startButton);
			GridPane.setConstraints(startButtonsHBox, 0, 11);
			GridPane.setColumnSpan(startButtonsHBox, 8);
			
			// Add items to the gridPane
			gridPane.getChildren().addAll(titleHBox, currentPlayerHBox, historyButtonsHBox, startButtonsHBox);

			Group root = new Group(gridPane);
			Scene scene = new Scene(root,520,700);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handleStartClick() {
		runAISearch();
	}
	
	// Create array of available moves.
	private void checkAvailableMoves() {
		char[] boardEvalClone = board.clone();
		int position;
		logic.setPlayers(currentPlayer, nextPlayer);
		// Reset available squares list.
		availableSquares.clear();
		for (int i = 0; i < board.length; i++) {
			if (board[i] == '-') {
				position = i;
				logic.setBoard(board);
				logic.setPosition(position);
				boolean successfulMove = logic.checkNextItem(boardEvalClone);
				if (successfulMove) {
					// This move is available -> update board array because array is used to render/update board UI.
					board[i] = 'a';
					// Add to available squares list.
					availableSquares.add(i);
				}
			}
		}
	}
	
	private void handleSquareClick(int positionIn) {
		 if (board[positionIn] == 'a' && boardLocked == false) {
			char[] boardEvalClone = board.clone();
			logic.setPlayers(currentPlayer, nextPlayer);
			logic.setBoard(board);
			logic.setPosition(positionIn);
			boolean successfulMove = logic.checkNextItem(boardEvalClone);
			if (successfulMove) {
				prevBoard = board;
				board = logic.getNewBoard();
				updatePlayers();
				updatePlayerText();
				resetAvailableMoves();
				checkAvailableMoves();
				if (availableSquares.size() == 0) {
					checkWinner();
				}
			}
		}
	}
	
	private void runAISearch() {
		// Minimax
		char[] boardEvalClone = board.clone();
		minimaxSearch.setBoard(board.clone());
		minimaxSearch.setPlayers(currentPlayer, nextPlayer);
		//ab.setBoard(board.clone());
		int level;
		if (currentPlayer == 'b') {
			level = controlLevel;
		} else {
			level = variableLevel;
			System.out.println(level);
		}
		int positionAI = minimaxSearch.runMinimax(level);
		System.out.println(positionAI);
		//int positionAI = ab.runMinimax();
		//System.out.println(positionAI);
		logic.setPlayers(currentPlayer, nextPlayer);
		logic.setBoard(board);
		logic.setPosition(positionAI);
		logic.checkNextItem(boardEvalClone);
		prevBoard = board;
		board = logic.getNewBoard();
		updatePlayers();
		updatePlayerText();
		resetAvailableMoves();
		checkAvailableMoves();
		if (availableSquares.size() == 0) {
			checkWinner();
		}
		updateBoard();
	}
	 
	private void updateBoard() {
		for (int i = 0; i < board.length; i++) {
			String buttonID = Integer.toString(i);
			Button boardButtonSelect = (Button) gridPane.lookup("#"+buttonID);
			if (board[i] == 'b') {
				Image imageBlackDisc = new Image(getClass().getResourceAsStream("othello-disc-black.png"));
				boardButtonSelect.setGraphic(new ImageView(imageBlackDisc));
			} else if (board[i] == 'w') {
				Image imageWhiteDisc = new Image(getClass().getResourceAsStream("othello-disc-white.png"));
				boardButtonSelect.setGraphic(new ImageView(imageWhiteDisc));
			} else if (board[i] == 'a') {
				Image imageAvailableSquare = new Image(getClass().getResourceAsStream("available-square.png"));
				boardButtonSelect.setGraphic(new ImageView(imageAvailableSquare));
			} else if (board[i] == '-') {
				boardButtonSelect.setGraphic(null);
			}
		}
	}

	private void updatePlayerText() {
		// Lookup the Text node with the CSS ID set earlier and set text to currentPlayer.
		String playerString;
		if (currentPlayer == 'b') {
			playerString = "black";
		} else {
			playerString = "white";
		}
		Text playerText = (Text) gridPane.lookup("#currenPlayerText");
		playerText.setText("Current player: " + playerString);
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
				updatePlayers();
				updatePlayerText();
				resetAvailableMoves();
				checkAvailableMoves();
				return;
			}
		}
		results = boardEval.returnResults(board);
		System.out.println(results);
	}
	
	public static void main(String[] args) {
		
		// Launch JavaFX UI.
		launch(args);

	}

}