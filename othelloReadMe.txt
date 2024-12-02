How to Run:


	Server (.jar): 

		- java -jar C:\Users\abduh\Documents\Othello+Game+Server\othello.jar --p1-type remote --p2-type random --wait-for-ui

	Client (new terminal):

		- java -cp ".;..\lib\gson-2.8.5.jar" com.atomicobject.othello.Main 127.0.0.1 1337
 
		- Must be in \src path

--------------------------------------------------------------------------------------------------------------------------

Watched a few videos on othello strategy. Here is a short summary on the basis of my player model (refer to picture in directory for "Red" and "Orange"):


### Corner Squares ("Red" Squares)
- The four corner squares [(0,0), (0,7), (7,0), (7,7)] are highly valuable.
  - These pieces cannot be flipped, granting more control over the late-game board.
  - Secure a piece on these corners whenever possible.

### Avoid Adjacent Squares ("Orange" Squares)
- Avoid placing pieces on squares directly adjacent to the corners, as this gives your opponent a chance to capture the corner. These adjacent squares are:
  [(1,0), (0,1), (1,1), (6,0), (7,1), (6,1), (0,6), (1,6), (1,7), (6,7), (6,6), (7,6)]

- Only play on these squares strategically, such as when it forces your opponent into a bad position or secures a long-term advantage.

### Stable Disks
- After acquiring a corner square, aim to place "stable disks."
- Stable disks are pieces that cannot be flipped for the remainder of the game due to their position (e.g., along edges or corners).
- Stability is typically achievable only after securing a corner.

### Situational Plays
- There are scenarios where placing a piece on an adjacent "orange" square is beneficial:
- If it leads to gaining multiple corners or flipping a large number of "locked" pieces.

### Diagonal Control
- Maintain control of diagonals whenever possible, as they are crucial for positioning and influencing your opponent's moves.

In summary:
- Prioritize corners.
- Avoid adjacent squares unless it offers a strong tactical advantage.
- Use stable disks to lock in late-game control.
- Adapt to the board dynamically, even if it means temporary sacrifices.

------------------------------------------------------------------------------------------------------------------------------

  Scoring Components:
  1. Positional Score:
     - Derived from the `WEIGHTS` grid, which prioritizes valuable positions like corners and de-emphasizes risky positions like squares near corners.
     - Corners (50): Highly valued for their stability and inability to be flipped.
     - Edges (20): Moderately prioritized for their potential to stabilize regions of the board (mid to late game).
     - Near-corner positions (-10, -15): Heavily penalized due to the risk of enabling opponent corner captures (penalized heavier in early to mid game, as less flipping).

  2. Flip Count:
     - The number of opponent pieces flipped by the move. This adds to the score, particularly important in the late game for maximizing board control.

  3. Game Phase Adjustments:
     - Early Game (emptySquares > 45):
       - Prioritize positional play, particularly corners, to establish stable control.
       - De-emphasize opponent mobility to focus on securing valuable positions.
     - Mid-Game (15 < emptySquares <= 45):
       - Balance between positional play and penalizing moves that increase opponent mobility.
       - Edges become more valuable for stabilizing and controlling regions of the board.
     - Late Game (emptySquares <= 15):
       - Maximize flipped pieces to gain board dominance for final scoring.
       - Corners and stable edges are critical for locking down sections of the board.



--------------------------------------------------------------------------------------------------------------------------------

What I would have improved:

  1. Calculations for how impactful an opponents piece is (how many flips they get).
    - Often my AI loses against a late game flip, when that could have been avoided had my AI accounted for the large amounts of flips the opponent had available.

  2. Deeper understanding of the game, so that the weights are better valued overall and at different points in the game.

  3. Dynamically weighted the "Game Phase Thresholds" instead of using hard coded values.

  4. Small change, but modifying the board in place and reverting after scoring (in a situation where the table is larger than 8x8).

Overall, really enjoyed this project! Thank you!





