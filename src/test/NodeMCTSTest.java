package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import game.NodeMCTS;

class NodeMCTSTest {
	
	private NodeMCTS node;
	private char[] board  = {
			'-','-','-','-','-','-','-','-',
			'-','-','-','-','-','-','-','-',
			'-','-','-','-','-','-','-','-',
			'-','-','-','b','b','b','-','-',
			'-','-','-','w','b','-','-','-',
			'-','-','-','-','-','-','-','-',
			'-','-','-','-','-','-','-','-',
			'-','-','-','-','-','-','-','-',
		};

	@BeforeEach
	void setUp() throws Exception {
		node = new NodeMCTS();
		node.setBoardState(board);
		node.setPlayers('w', 'b');
	}

	@AfterEach
	void tearDown() throws Exception {
		node = null;
	}

	@Test
	@DisplayName("Check the correct winner is returned.")
	void testGetWinState() {
		assertSame(node.getWinState(), 'b');
	}
	
	@Test
	@DisplayName("Check the correct number of child nodes are created.")
	void testCreateChildArray() {
		node.createChildArray();
		List<NodeMCTS> childArray = node.getChildArray();
		assertSame(childArray.size(), 3);
	}

}
