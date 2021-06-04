import java.util.HashMap;

public class BoardEvaluation {
	
	public HashMap<String, Integer> returnResults(char[] board) {
		HashMap<String,Integer> results = new HashMap<String,Integer>();
		int playerB = 0;
		int playerW = 0;
		for (int i = 0; i < board.length; i++) {
			if (board[i] == 'b') {
				playerB++;
			} else if (board[i] == 'w') {
				playerW++;
			}
		}
		results.put("black", playerB);
		results.put("white", playerW);
		return results;
	}

}