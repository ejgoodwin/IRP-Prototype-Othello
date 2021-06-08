package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import game.GameLogic;
import mcts.MCTS;

class MCTSTest {
	
	private MCTS mcts;
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
		mcts = new MCTS();
		logic = new GameLogic();
	}

	@AfterEach
	void tearDown() throws Exception {
		mcts = null;
		logic = null;
	}

	@Test
	@DisplayName("Return a legal move")
	void testFindNextMove() {
		int mctsMove = mcts.findNextMove(board, 'b', 'w', 2);
		logic.setPosition(mctsMove);
		logic.setBoard(board);
		logic.setPlayers('b', 'w');
		assertTrue(logic.checkNextItem());
	}

}
