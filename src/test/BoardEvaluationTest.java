package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import game.BoardEvaluation;

class BoardEvaluationTest {
	
	private BoardEvaluation boardEval;

	@BeforeEach
	void setUp() throws Exception {
		System.out.println("set up");
		boardEval = new BoardEvaluation();
	}

	@AfterEach
	void tearDown() throws Exception {
		System.out.println("tear down");
		boardEval = null;
	}

	@Test
	@DisplayName("Return number of pieces for each colour")
	void testReturnResults() {
		System.out.println("testing");
		char[] board = {
			'-','-','-','-','-','-','-','-',
			'-','-','-','-','-','-','-','-',
			'-','-','-','-','b','b','b','-',
			'-','-','-','b','w','-','-','-',
			'-','-','-','w','b','w','-','-',
			'-','-','-','-','b','-','-','-',
			'-','-','-','-','-','-','-','-',
			'-','-','-','-','-','-','-','-',
		};
		assertSame(6, boardEval.returnResults(board).get("black"));
		assertSame(3, boardEval.returnResults(board).get("white"));
	}

}
