import static org.junit.Assert.*;
import com.atomicobject.othello.AI;
import com.atomicobject.othello.GameState;
import org.junit.Test;

public class AITest {

	@Test
	public void testCornerMovePriority() {
		// Test that the AI prioritizes corner moves in most situations.
		AI ai = new AI();
		GameState state = new GameState();
		state.setPlayer(1);
		state.setBoard(new int[][]{
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 2, 0},
				{0, 0, 0, 0, 1, 2, 1, 0},
				{0, 0, 0, 1, 2, 0, 0, 0},
				{0, 0, 0, 1, 2, 0, 0, 0},
				{0, 0, 0, 1, 2, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0}
		});

		int[] expectedMove = {0, 7};
		int[] actualMove = ai.computeMove(state);

		System.out.println("Expected move: " + java.util.Arrays.toString(expectedMove));
		System.out.println("Actual move: " + java.util.Arrays.toString(actualMove));

		assertArrayEquals("AI should prioritize the corner move.", expectedMove, actualMove);

	}



	@Test
	public void returnsNullWhenBoardNotFullNoValidMove() {
		// Test that the AI recognizes that the board is not full, but no valid move can be made. Invalid moves arent forced
		AI ai = new AI();
		GameState state = new GameState();
		state.setPlayer(1);
		state.setBoard(new int[][]{
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 1, 1, 1, 1, 1, 1, 0},
				{0, 1, 1, 1, 1, 1, 1, 0},
				{0, 1, 1, 1, 1, 1, 1, 0},
				{0, 1, 1, 1, 1, 1, 1, 0},
				{0, 1, 1, 1, 1, 1, 1, 0},
				{0, 1, 1, 1, 1, 1, 1, 0},
				{0, 0, 0, 0, 0, 0, 0, 0}
		});

		assertNull("AI should return null when there are no valid moves.", ai.computeMove(state));
	}


	@Test
	public void testAvoidAdjacentToCorner() {
		// Test that the AI avoids placing pieces near corners if it's risky.
		AI ai = new AI();
		GameState state = new GameState();
		state.setPlayer(1);
		state.setBoard(new int[][]{
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 2, 0, 0},
				{0, 0, 0, 1, 1, 0, 0, 0},
				{0, 0, 0, 2, 1, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 1, 0, 0, 0, 0, 0, 0}
		});

		int[] riskyMove = {6, 1}; // Adjacent to corner
		int[] actualMove = ai.computeMove(state);

		System.out.println("Expected move: " + java.util.Arrays.toString(riskyMove));
		System.out.println("Actual move: " + java.util.Arrays.toString(actualMove));

		assertNotEquals("AI should avoid risky adjacent-to-corner moves.", riskyMove, actualMove);
	}

	@Test
	public void testMaximizeFlipsLateGame() {
		// Test that the AI prioritizes moves maximizing flips in the late game. Corner [7,0] is an option, but [5,6] flips more pieces.
		AI ai = new AI();
		GameState state = new GameState();
		state.setPlayer(1);
		state.setBoard(new int[][]{
				{2, 2, 2, 2, 2, 2, 2, 2},
				{2, 1, 1, 1, 1, 1, 1, 1},
				{2, 1, 2, 2, 2, 2, 2, 2},
				{2, 1, 2, 1, 2, 2, 2, 2},
				{2, 1, 2, 2, 1, 2, 2, 2},
				{2, 1, 1, 2, 2, 2, 0, 0},
				{2, 2, 2, 1, 2, 1, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0}
		});

		int[] expectedMove = {5, 6}; // Flip the most pieces in this state
		int[] actualMove = ai.computeMove(state);

		System.out.println("Expected move: " + java.util.Arrays.toString(expectedMove));
		System.out.println("Actual move: " + java.util.Arrays.toString(actualMove));

		assertArrayEquals("AI can prioritize a normal cell over a corner, if the flips are substantial.", expectedMove, actualMove);
	}

	@Test
	public void testMaximizeFlipsLateGameEdge() {
		// Test that the AI prioritizes moves maximizing flips in the late game for edges. Corner [7,0] is an option, but [7,5] is weighted high (edge) and flips more pieces.
		AI ai = new AI();
		GameState state = new GameState();
		state.setPlayer(1);
		state.setBoard(new int[][]{
				{2, 2, 2, 2, 2, 2, 2, 2},
				{2, 1, 1, 1, 1, 1, 1, 1},
				{2, 1, 2, 2, 2, 2, 2, 2},
				{2, 1, 2, 1, 2, 2, 2, 2},
				{2, 1, 2, 2, 1, 2, 2, 2},
				{2, 1, 1, 2, 2, 2, 0, 0},
				{2, 2, 2, 1, 2, 2, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0}
		});

		int[] expectedMove = {7, 5}; // Flip the most pieces in this state
		int[] actualMove = ai.computeMove(state);

		System.out.println("Expected move: " + java.util.Arrays.toString(expectedMove));
		System.out.println("Actual move: " + java.util.Arrays.toString(actualMove));

		assertArrayEquals("AI can prioritize an edge over a corner, if the flips are substantial.", expectedMove, actualMove);
	}

	@Test
	public void testNoValidMoves() {
		// Test that the AI returns null when there are no valid moves.
		AI ai = new AI();
		GameState state = new GameState();
		state.setPlayer(1);
		state.setBoard(new int[][]{
				{2, 2, 2, 2, 2, 2, 2, 2},
				{2, 2, 2, 2, 2, 2, 2, 2},
				{2, 2, 2, 2, 2, 2, 2, 2},
				{2, 2, 2, 2, 2, 2, 2, 2},
				{2, 2, 2, 2, 2, 2, 2, 2},
				{2, 2, 2, 2, 2, 2, 2, 2},
				{2, 2, 2, 2, 2, 2, 2, 2},
				{2, 2, 2, 2, 2, 2, 2, 2}
		});

		assertNull("AI should return null when there are no valid moves.", ai.computeMove(state));
	}

	@Test
	public void testFlippedMultipleDirections() {
		// Test that computeMove counts flips correctly when a flip occurs in all three directions.
		AI ai = new AI();
		GameState state = new GameState();
		state.setPlayer(1);
		state.setBoard(new int[][]{
				{2, 2, 2, 1, 2, 2, 2, 2},
				{0, 2, 2, 2, 1, 1, 1, 1},
				{2, 1, 2, 1, 2, 2, 1, 2},
				{2, 1, 2, 1, 2, 2, 1, 1},
				{2, 1, 2, 2, 1, 2, 1, 2},
				{1, 1, 1, 1, 1, 1, 2, 2},
				{2, 2, 2, 1, 1, 2, 2, 0},
				{1, 0, 0, 0, 0, 0, 0, 1}
		});

		// move at [6,7] flips 7 pieces, 3 diagonal, 2 up, and 2 right
		// move at [1,0] flips 6 pieces, 3 to the right and 3 down

		// if diagonal moves aren't recognized, the move at [1,0] will be made.

		int[] expectedMove = {6, 7}; // Flip the most pieces in this state
		int[] actualMove = ai.computeMove(state);

		System.out.println("Expected move: " + java.util.Arrays.toString(expectedMove));
		System.out.println("Actual move: " + java.util.Arrays.toString(actualMove));

		assertArrayEquals("computeMove should count flips in multiple directions correctly.", expectedMove, actualMove);

	}

	}

