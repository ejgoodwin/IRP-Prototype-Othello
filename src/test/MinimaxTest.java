package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import game.GameLogic;
import minimax.Minimax;

class MinimaxTest {
	
	private Minimax minimax;
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
		minimax = new Minimax();
		logic = new GameLogic();
	}

	@AfterEach
	void tearDown() throws Exception {
		minimax = null;
		logic = null;
	}

	@Test
	@DisplayName("Return a legal move")
	void testRunMinimax() {
		minimax.setBoard(board);
		minimax.setPlayers('b', 'w');
		int minimaxPosition = minimax.runMinimax(2);
		logic.setPosition(minimaxPosition);
		logic.setBoard(board);
		logic.setPlayers('b','w');
		assertTrue(logic.checkNextItem());
	}

}
