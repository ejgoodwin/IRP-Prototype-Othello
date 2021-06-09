package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import game.AlphaBeta;
import game.GameLogic;

class AlphaBetaTest {
	
	private AlphaBeta alphaBeta;
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
		alphaBeta = new AlphaBeta();
		logic = new GameLogic();
	}

	@AfterEach
	void tearDown() throws Exception {
		alphaBeta = null;
		logic = null;
	}

	@Test
	@DisplayName("Return a legal move")
	void testRunAlphaBeta() {
		alphaBeta.setBoard(board);
		alphaBeta.setPlayers('b', 'w');
		int alphaBetaPosition = alphaBeta.runAlphaBeta(2);
		logic.setPosition(alphaBetaPosition);
		logic.setBoard(board);
		logic.setPlayers('b','w');
		assertTrue(logic.checkNextItem());
	}

}
