package com.atomicobject.othello;

import java.util.ArrayList;
import java.util.List;

public class AI {

	// All possible directions for traversing the board from a given square.
	private static final int[][] DIRECTIONS = {
			{-1, -1}, {-1, 0}, {-1, 1},
			{ 0, -1},          { 0, 1},
			{ 1, -1}, { 1, 0}, { 1, 1}
	};
	// Weights for each square on the board
	private static final int[][] WEIGHTS = {
			{50, -10, 20, 20, 20, 20, -10, 50},
			{-10, -15, 5, 5, 5, 5, -15, -10},
			{20,   5, 10, 10, 10, 10,  5, 20},
			{20,   5, 10, 10, 10, 10,  5, 20},
			{20,   5, 10, 10, 10, 10,  5, 20},
			{20,   5, 10, 10, 10, 10,  5, 20},
			{-10, -15, 5, 5, 5, 5, -15, -10},
			{50, -10, 20, 20, 20, 20, -10, 50}
	};


	private static final int FLIP_MULTIPLIER = 4;
	private static final float MOBILITY_PENALTY_MULTIPLIER = 1.5F;


	public AI() {}
	/*
  Computes the best move for the current player.

  Inputs:
	 - GameState state: The current game state, including:
	     - int[][] board: The 8x8 game board.
	     - int player: The current player making the move.

  Outputs:
	  - int[]: The best move as a coordinate pair {row, col}. Returns null if no valid moves exist.

  How it works:
  1. Identifies all valid moves for the current player using `getValidMoves`.
  2. Simulates each move to calculate its score based on:
     - Positional weights (from WEIGHTS).
     - Number of opponent pieces flipped.
     - Impact on opponent mobility.

  Opponent Mobility:
  - Mobility refers to the number of valid moves available to a player.
  - Opponent mobility is a measure of how many valid moves the opponent would have
    after the current player makes a move.
  - The program calculates opponent mobility after simulating each move and penalizes
    moves that increase the opponent's mobility. This helps to reduce the opponent's
    flexibility and strategic options.
  - Weighted lower because against a "worse" opponent, more options = more room for mistakes.

  3. Adjusts scoring dynamically based on game phase (early, mid, late).
  4. Selects and returns the move with the highest score.
	 */


	public int[] computeMove(GameState state){
		int[][]board = state.getBoard();
		int player = state.getPlayer();
		int opponent = (player == 1) ? 2 : 1;

		List<int []>  validMoves = getValidMoves(board, player);
		if(validMoves.isEmpty()) return null;

		int[][] newBoard = new int[8][8];
		double bestScore = Integer.MIN_VALUE;
		int[] bestMove = null;

		int emptySquares = countEmptySquares(board);

		for(int [] move : validMoves){
			int flippedCount = simulateMove(board, player, move[0], move[1], newBoard);
			int positionalScore = WEIGHTS[move[0]][move[1]];


			double totalScore = positionalScore + flippedCount;
			// Adjust based on game phase
			if (emptySquares > 45) {
				totalScore += (flippedCount * FLIP_MULTIPLIER); // Early game: prioritize mobility less, corners more
			} else if (emptySquares > 15) {
				int opponentMobility = getValidMoves(newBoard, opponent).size(); // Mid-game: balance positional play and flipping
				totalScore -= (opponentMobility * MOBILITY_PENALTY_MULTIPLIER);
			} else {
				totalScore += flippedCount * 5; // Late game: focus on maximizing flips, where most corners are likely taken
			}

			if(totalScore > bestScore){
				bestScore = totalScore;
				bestMove = move;
			}
		}
		return bestMove;
	}

		/*
	  Determines whether placing a piece at the specified position `(row, col)` on the board
	  is a valid move for the given player.

	  A valid move satisfies the following conditions:
	  1. The target square is empty.
	  2. The move results in flipping at least one opponent piece by forming a "sandwich".

	  Inputs:
	  - int[][] board: The 8x8 game board.
	  - int player: The current player making the move.
	  - int row: The row index of the position being checked.
	  - int col: The column index of the position being checked.

	  Outputs:
	  - boolean: Returns true if the move is valid; otherwise, false.

	  How it works:
	  1. Checks if `(row, col)` is empty.
	  2. Iterates through all directions from the current position.
	  3. If a valid sandwich is formed in any direction, returns true.
	  4. Otherwise, returns false.
	 */


	private boolean isValidMove(int[][] board, int player, int row, int col){
		if(board[row][col]!= 0)
			return false;

		int opponent = (player == 1) ? 2 : 1;

		for(int [] dir: DIRECTIONS){
			int x = row + dir[0];
			int y = col + dir[1];
			boolean foundOpponent = false;

			while (x >= 0 && x < 8 && y >= 0 && y < 8) {
				if(board[x][y] == opponent)
					foundOpponent = true;
				else if(board[x][y] == player && foundOpponent)
					return true;
				else
					break;

				x+= dir[0];
				y+= dir[1];

			}
		}
		return false;
	}

		/*
	  Finds all valid moves for the given player.

	  Inputs:
	  - int[][] board: The 8x8 game board.
	  - int player: The player for whom valid moves are calculated.

	  Outputs:
	  - List<int[]>: A list of valid moves, where each move is a coordinate pair {row, col}.

	  How it works:
	  1. Iterates through all board positions.
	  2. Uses `isValidMove` to determine if placing a piece at a given position is valid.
	  3. Adds valid moves to the result list.
	 */


	private List<int[]> getValidMoves(int[][]board, int player){
		List<int[]> validMoves = new ArrayList<>();
		for(int row = 0; row < 8; row++){
			for(int col = 0; col < 8; col++){
				if(isValidMove(board, player, row, col))
					validMoves.add(new int[]{row,col});
			}
		}
		return validMoves;
	}

	/*
	  Simulates a move by placing a piece and flipping opponent pieces.

	  Inputs:
	  - int[][] board: The current 8x8 game board.
	  - int player: The player making the move.
	  - int row: The row index where the piece is placed.
	  - int col: The column index where the piece is placed.
	  - int[][] newBoard: The board state after simulating the move.

	  Outputs:
	  - int: The number of opponent pieces flipped.

	  How it works:
	  1. Copies the current board into `newBoard`.
	  2. Places the player's piece at `(row, col)`.
	  3. For each direction, checks for valid flips.
	  4. Flips opponent pieces in valid directions and returns the total number of flips.

	  Note on the copy:
	  /*
   Creates a copy of the board to simulate the move without modifying the original.
   Efficient for Othello's fixed 8x8 board due to the small size and use of
   `System.arraycopy.` However, repeated copying can become costly
    in scenarios with deep lookahead.
	 */

	private int simulateMove(int[][] board, int player, int row, int col, int[][] newBoard){
		for (int i = 0; i < 8; i++){
			System.arraycopy(board[i], 0, newBoard[i], 0, 8);
		}

		newBoard[row][col] = player;

		int opponent = (player == 1) ? 2 : 1;
		int flippedCount = 0; //flipped disks from ALL directions (one move can flip a diagonal and a horizontal at once)

		for(int [] dir : DIRECTIONS){
			int x = row + dir[0];
			int y = col + dir[1];
			boolean foundOpponent = false;
			int count = 0; //flipped disks from one direction at a time

			while (x >= 0 && x < 8 && y >= 0 && y < 8 && board[x][y] == opponent) {
				foundOpponent = true;
				count++;
				x += dir[0];
				y += dir[1];
			}

			//process of flipping pieces of sandwich found above
			if (foundOpponent && x >= 0 && x < 8 && y >= 0 && y < 8 && board[x][y] == player) {
				int flipX = row + dir[0];
				int flipY = col + dir[1];
				while(flipX != x || flipY != y){
					newBoard[flipX][flipY] = player;
					flipX += dir[0];
					flipY += dir[1];
				}
				flippedCount+= count;
			}
		}
		return flippedCount;
	}

	/*
   Counts the number of empty squares on the board, representing potential moves or game progression.

   Inputs:
   - int[][] board: The 8x8 game board where 0 represents an empty square.

   Outputs:
   - int: The total number of empty squares on the board.

*/


	private int countEmptySquares(int [][] board){
		int count = 0;
		for(int [] row: board){
			for(int cell : row){
				if(cell == 0)
					count++;
			}
		}
		return count;
	}

}
