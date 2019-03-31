package greedyagent;

import boardgame.Move;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoBoardState.Piece;

/*This strategy aims at greedily playing the game by recognizing 
 * that there are only a few ways to actually win.
 * Here are the so far identified solutions:
 * - 3 vertical in one quadrant and at least 2 vertical in same column in other quadrant)
 * - 3 horizontal in one quadrant and at least 2 horizontal in same row in other quadrant)
 * - 3 diagonal in one quadrant and 2 diagonal (same slope) in other quadrant)
 * - 2 on 'side diagonal' in 2 quadrants(same slope) and 1 on opposite corner in 3rd quadrant
 * 
 * */

public class BoardAnalyzer {
	private PentagoBoardState pbs;
	private Piece[][][] quadrants = new Piece[4][3][3];

	public BoardAnalyzer(PentagoBoardState boardState) {
		this.pbs = boardState;
	}

	public Move FindBestMove() {

		GetQuadrants();

		return null;

	}

	void GetQuadrants() {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				if (i < 3 && j < 3) {
					this.quadrants[0][i][j] = pbs.getPieceAt(i, j);
				}
				if (i < 3 && j >= 3) {
					this.quadrants[1][i][j - 3] = pbs.getPieceAt(i, j);
				}
				if (i >= 3 && j < 3) {
					this.quadrants[2][i - 3][j] = pbs.getPieceAt(i, j);
				}
				if (i >= 3 && j >= 3) {
					this.quadrants[3][i - 3][j - 3] = pbs.getPieceAt(i, j);
				}
			}
		}

	}

	void PrintBoardfromQuadrants() {
		for (int line = 0; line < 3; line++) {
			for (int i = 0; i < 3; i++) {
				System.out.print("|"+quadrants[0][line][i].toString());
			}
			for (int i = 0; i < 3; i++) {
				System.out.print("|"+quadrants[1][line][i].toString());
			}
			System.out.print("|\n");
		}
		for (int line = 0; line < 3; line++) {
			for (int i = 0; i < 3; i++) {
				System.out.print("|"+quadrants[2][line][i].toString());
			}
			for (int i = 0; i < 3; i++) {
				System.out.print("|"+quadrants[3][line][i].toString());
			}
			System.out.print("|\n");
		}
	}

}
