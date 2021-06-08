package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import game.GameLogic;

class GameLogicTest {
	
	private GameLogic logic;
	private char[] board  = {
		'-','-','-','-','-','-','-','-',
		'-','-','-','-','-','-','-','-',
		'-','-','-','-','-','-','-','-',
		'-','-','-','b','w','-','-','-',
		'-','-','-','w','b','-','-','-',
		'-','-','-','-','-','-','-','-',
		'-','-','-','-','-','-','-','-',
		'-','-','-','-','-','-','-','-',
	};

	@BeforeEach
	void setUp() throws Exception {
		logic = new GameLogic();
	}

	@AfterEach
	void tearDown() throws Exception {
		logic = null;
	}

	@Test
	@DisplayName("Return the board that is previously set")
	void testGetNewBoard() {
		logic.setBoard(board);
		assertSame(board, logic.getNewBoard());
	}

	@Test
	@DisplayName("Check next item with legal move")
	void testCheckNextItem() {
		logic.setBoard(board);
		logic.setPlayers('b', 'w');
		logic.setPosition(20);
		assertTrue(logic.checkNextItem());
	}
	
	@Test
	@DisplayName("Check next item with illegal move")
	void testCheckNextItem2() {
		logic.setBoard(board);
		logic.setPlayers('w', 'b');
		logic.setPosition(20);
		assertFalse(logic.checkNextItem());
	}
	
	@Test
	@DisplayName("Check next item with illegal move, position too small to be on board")
	void testCheckNextItem3() {
		logic.setBoard(board);
		logic.setPlayers('w', 'b');
		logic.setPosition(-10);
		assertFalse(logic.checkNextItem());
	}
	
	@Test
	@DisplayName("Check next item with illegal move, position too large to be on board")
	void testCheckNextItem4() {
		logic.setBoard(board);
		logic.setPlayers('w', 'b');
		logic.setPosition(64);
		assertFalse(logic.checkNextItem());
	}

}
