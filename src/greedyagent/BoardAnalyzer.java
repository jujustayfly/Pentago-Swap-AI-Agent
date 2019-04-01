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
	private int[][][] placeValues = new int[4][3][3];
	private int[][][] verticalPotentials = new int[4][3][3];
	private int[][][] horizontalPotentials = new int[4][3][3];
	private int[][][] diagonalPotentials = new int[4][3][3];
	private Piece mycolor;
	private Piece opponent;

	public BoardAnalyzer(PentagoBoardState boardState) {
		this.pbs = boardState;
	}

	public Move FindBestMove() {
		GetMyColor();
		GetQuadrants();
		AssignMoveValues();

		return null;

	}

	public void GetMyColor() {
		if (pbs.getTurnPlayer() == pbs.WHITE) {
			mycolor = Piece.WHITE;
			opponent = Piece.BLACK;
		}
		if (pbs.getTurnPlayer() == pbs.BLACK) {
			mycolor = Piece.BLACK;
			opponent = Piece.WHITE;
		}

	}

	void AssignMoveValues() {
		for (int i = 0; i < 4; i++) {
			UpdateQuandrantValues(i);
			}
		}

	

	

	private void UpdateQuandrantValues(int i) {
		Piece[][] quadrant = new Piece[3][3];
		quadrant = quadrants[i];
		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 3; k++) {
				UpdatePotential(i, j, k);
			}
		}

	}

	private void UpdatePotential(int i, int j, int k) {
		if (quadrants[i][j][k] == Piece.WHITE || quadrants[i][j][k] == Piece.BLACK) {
			placeValues[i][j][k] = Integer.MIN_VALUE + 1;
		} else {
			UpdateHorizontalPotential(i, j, k);
			UpdateVerticalPotential(i, j, k);
			UpdateDiagonalPotential(i, j, k);
		}

	}

	private void UpdateDiagonalPotential(int i, int j, int k) {
		if (k == j && j == 1) {
			UpdateNegSlopeDiagPotentials(i, j, k);
			UpdatePosSlopeDiagPotentials(i, j, k);

		} else if (k == j) {
			UpdateNegSlopeDiagPotentials(i, j, k);
			UpdateSmallDiagPotentials(i, j, k);
		} else if (k + j == 2) {
			UpdatePosSlopeDiagPotentials(i, j, k);
			UpdateSmallDiagPotentials(i, j, k);
		} else {
			UpdateSmallDiagPotentials(i, j, k);
		}

	}

	private void UpdateNegSlopeDiagPotentials(int i, int j, int k) {
		if (quadrants[i][1][1] == opponent) {
			getDiagonalPotentials()[i][j][k] += 0;
		} else {
			if (j == 0 && k == 0) {
				if (quadrants[i][1][1] == Piece.EMPTY && quadrants[i][2][2] == Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][1][1] == Piece.EMPTY && quadrants[i][2][2] == mycolor) {
					getDiagonalPotentials()[i][j][k] += 4;
				} else if (quadrants[i][1][1] == Piece.EMPTY && quadrants[i][2][2] == opponent) {
					getDiagonalPotentials()[i][j][k] += 2;
				} else if (quadrants[i][1][1] == mycolor && quadrants[i][2][2] == Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 4;
				} else if (quadrants[i][1][1] == mycolor && quadrants[i][2][2] == mycolor) {
					getDiagonalPotentials()[i][j][k] += 5;
				} else if (quadrants[i][1][1] == mycolor && quadrants[i][2][2] == opponent) {
					getDiagonalPotentials()[i][j][k] += 3;
				} else {
					System.out.println("Error calculating Neg Slope Potential");
				}
			}
			else if( j ==1 && k==1) {
				//need 9 here
				if(quadrants[i][0][0]==Piece.EMPTY && quadrants[i][2][2]==Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 3;
				}
				else if(quadrants[i][0][0]==Piece.EMPTY && quadrants[i][2][2]==mycolor) {
					getDiagonalPotentials()[i][j][k] += 4;
				}
				else if(quadrants[i][0][0]==Piece.EMPTY && quadrants[i][2][2]==opponent) {
					getDiagonalPotentials()[i][j][k] += 2;
				}
				else if(quadrants[i][0][0]==mycolor && quadrants[i][2][2]==Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 4;
				}
				else if(quadrants[i][0][0]==mycolor && quadrants[i][2][2]==mycolor) {
					getDiagonalPotentials()[i][j][k] += 5;
				}
				else if(quadrants[i][0][0]==mycolor && quadrants[i][2][2]==opponent) {
					getDiagonalPotentials()[i][j][k] += 3;
				}
				else if(quadrants[i][0][0]==opponent && quadrants[i][2][2]==Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 2;
				}
				else if(quadrants[i][0][0]==opponent && quadrants[i][2][2]==opponent) {
					getDiagonalPotentials()[i][j][k] += 0;
				}
				else if(quadrants[i][0][0]==opponent && quadrants[i][2][2]==mycolor) {
					getDiagonalPotentials()[i][j][k] += 3;
				}else {
					System.out.println("Error calculating Neg Slope Potential");
				}
			}
			else if (j == 2 && k == 2) {
				if (quadrants[i][1][1] == Piece.EMPTY && quadrants[i][0][0] == Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][1][1] == Piece.EMPTY && quadrants[i][0][0] == mycolor) {
					getDiagonalPotentials()[i][j][k] += 4;
				} else if (quadrants[i][1][1] == Piece.EMPTY && quadrants[i][0][0] == opponent) {
					getDiagonalPotentials()[i][j][k] += 2;
				} else if (quadrants[i][1][1] == mycolor && quadrants[i][0][0] == Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 4;
				} else if (quadrants[i][1][1] == mycolor && quadrants[i][0][0] == mycolor) {
					getDiagonalPotentials()[i][j][k] += 5;
				} else if (quadrants[i][1][1] == mycolor && quadrants[i][0][0] == opponent) {
					getDiagonalPotentials()[i][j][k] += 3;
				} else {
					System.out.println("Error calculating Neg Slope Potential");
				}
			}
		}

	}

	private void UpdateSmallDiagPotentials(int i, int j, int k) {
		if(j==0 &&k ==1) {
			if(quadrants[i][1][0]==Piece.EMPTY && quadrants[i][1][2]==Piece.EMPTY) {
				getDiagonalPotentials()[i][j][k]+= 2;
				}
			else if(!(quadrants[i][1][0]==opponent && quadrants[i][1][2]==opponent)) {
				getDiagonalPotentials()[i][j][k]+= 1;
			}
		}
		else if(j==2 &&k ==1) {
			if(quadrants[i][1][0]==Piece.EMPTY && quadrants[i][1][2]==Piece.EMPTY) {
				getDiagonalPotentials()[i][j][k]+= 2;
				}
			else if(!(quadrants[i][1][0]==opponent && quadrants[i][1][2]==opponent)) {
				getDiagonalPotentials()[i][j][k]+= 1;
			}
		}
		else if(j==1 &&k ==0) {
			if(quadrants[i][0][1]==Piece.EMPTY && quadrants[i][2][1]==Piece.EMPTY) {
				getDiagonalPotentials()[i][j][k]+= 2;
				}
			else if(!(quadrants[i][0][1]==opponent && quadrants[i][2][1]==opponent)) {
				getDiagonalPotentials()[i][j][k]+= 1;
			}
		}
		else if(j==1 &&k ==2) {
			if(quadrants[i][0][1]==Piece.EMPTY && quadrants[i][2][1]==Piece.EMPTY) {
				getDiagonalPotentials()[i][j][k]+= 2;
				}
			else if(!(quadrants[i][0][1]==opponent && quadrants[i][2][1]==opponent)) {
				getDiagonalPotentials()[i][j][k]+= 1;
			}
		}

	}

	private void UpdatePosSlopeDiagPotentials(int i, int j, int k) {
		if (quadrants[i][1][1] == opponent) {
			getDiagonalPotentials()[i][j][k] += 0;
		} else {
			if (j == 2 && k == 0) {
				if (quadrants[i][1][1] == Piece.EMPTY && quadrants[i][0][2] == Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][1][1] == Piece.EMPTY && quadrants[i][0][2] == mycolor) {
					getDiagonalPotentials()[i][j][k] += 4;
				} else if (quadrants[i][1][1] == Piece.EMPTY && quadrants[i][0][2] == opponent) {
					getDiagonalPotentials()[i][j][k] += 2;
				} else if (quadrants[i][1][1] == mycolor && quadrants[i][0][2] == Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 4;
				} else if (quadrants[i][1][1] == mycolor && quadrants[i][0][2] == mycolor) {
					getDiagonalPotentials()[i][j][k] += 5;
				} else if (quadrants[i][1][1] == mycolor && quadrants[i][0][2] == opponent) {
					getDiagonalPotentials()[i][j][k] += 3;
				} else {
					System.out.println("Error calculating Pos Slope Potential");
				}
			}
			else if( j ==1 && k==1) {
				//need 9 here
				if(quadrants[i][0][2]==Piece.EMPTY && quadrants[i][2][0]==Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 3;
				}
				else if(quadrants[i][0][2]==Piece.EMPTY && quadrants[i][2][0]==mycolor) {
					getDiagonalPotentials()[i][j][k] += 4;
				}
				else if(quadrants[i][0][2]==Piece.EMPTY && quadrants[i][2][0]==opponent) {
					getDiagonalPotentials()[i][j][k] += 2;
				}
				else if(quadrants[i][0][2]==mycolor && quadrants[i][2][0]==Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 4;
				}
				else if(quadrants[i][0][2]==mycolor && quadrants[i][2][0]==mycolor) {
					getDiagonalPotentials()[i][j][k] += 5;
				}
				else if(quadrants[i][0][2]==mycolor && quadrants[i][2][0]==opponent) {
					getDiagonalPotentials()[i][j][k] += 3;
				}
				else if(quadrants[i][0][2]==opponent && quadrants[i][2][0]==Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 2;
				}
				else if(quadrants[i][0][2]==opponent && quadrants[i][2][0]==opponent) {
					getDiagonalPotentials()[i][j][k] += 0;
				}
				else if(quadrants[i][0][2]==opponent && quadrants[i][2][0]==mycolor) {
					getDiagonalPotentials()[i][j][k] += 3;
				}
				else {
					System.out.println("Error calculating Pos Slope Potential");
				}
			}
			else if (j == 0 && k == 2) {
				if (quadrants[i][1][1] == Piece.EMPTY && quadrants[i][2][0] == Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][1][1] == Piece.EMPTY && quadrants[i][2][0] == mycolor) {
					getDiagonalPotentials()[i][j][k] += 4;
				} else if (quadrants[i][1][1] == Piece.EMPTY && quadrants[i][2][0] == opponent) {
					getDiagonalPotentials()[i][j][k] += 2;
				} else if (quadrants[i][1][1] == mycolor && quadrants[i][2][0] == Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 4;
				} else if (quadrants[i][1][1] == mycolor && quadrants[i][2][0] == mycolor) {
					getDiagonalPotentials()[i][j][k] += 5;
				} else if (quadrants[i][1][1] == mycolor && quadrants[i][2][0] == opponent) {
					getDiagonalPotentials()[i][j][k] += 3;
				} else {
					System.out.println("Error calculating Pos Slope Potential");
				}
			}
		}

	}

	

	private void UpdateVerticalPotential(int i, int j, int k) {
		if (quadrants[i][1][k] == opponent) {
			getVerticalPotentials()[i][j][k] += 0;
		} else {
			if (j == 0) {
				if (quadrants[i][1][k] == Piece.EMPTY && quadrants[i][2][k] == Piece.EMPTY) {
					getVerticalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][1][k] == Piece.EMPTY && quadrants[i][2][k] == mycolor) {
					getVerticalPotentials()[i][j][k] += 4;
				} else if (quadrants[i][1][k] == Piece.EMPTY && quadrants[i][2][k] == opponent) {
					getVerticalPotentials()[i][j][k] += 2;
				} else if (quadrants[i][1][k] == mycolor && quadrants[i][2][k] == opponent) {
					getVerticalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][1][k] == mycolor && quadrants[i][2][k] == mycolor) {
					getVerticalPotentials()[i][j][k] += 6;
				} else if (quadrants[i][1][k] == mycolor && quadrants[i][2][k] == Piece.EMPTY) {
					getVerticalPotentials()[i][j][k] += 5;
				} else {
					System.out.println("Error calculating vertical potential");
				}
			} else if (j == 1) {
				if (quadrants[i][0][k] == Piece.EMPTY && quadrants[i][2][k] == Piece.EMPTY) {
					getVerticalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][0][k] == Piece.EMPTY && quadrants[i][2][k] == mycolor) {
					getVerticalPotentials()[i][j][k] += 4;
				} else if (quadrants[i][0][k] == mycolor && quadrants[i][2][k] == Piece.EMPTY) {
					getVerticalPotentials()[i][j][k] += 4;
				} else if (quadrants[i][0][k] == Piece.EMPTY && quadrants[i][2][k] == opponent) {
					getVerticalPotentials()[i][j][k] += 2;
				} else if (quadrants[i][0][k] == mycolor && quadrants[i][2][k] == mycolor) {
					getVerticalPotentials()[i][j][k] += 6;
				} else if (quadrants[i][0][k] == mycolor && quadrants[i][2][k] == opponent) {
					getVerticalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][0][k] == opponent && quadrants[i][2][k] == mycolor) {
					getVerticalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][0][k] == opponent && quadrants[i][2][k] == Piece.EMPTY) {
					getVerticalPotentials()[i][j][k] += 2;
				} else if (quadrants[i][0][k] == opponent && quadrants[i][2][k] == opponent) {
					getVerticalPotentials()[i][j][k] += 0;
				} else {
					System.out.println("Error calculating vertical potential");
				}
			} else {
				if (quadrants[i][1][k] == Piece.EMPTY && quadrants[i][0][k] == Piece.EMPTY) {
					getVerticalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][1][k] == Piece.EMPTY && quadrants[i][0][k] == mycolor) {
					getVerticalPotentials()[i][j][k] += 4;
				} else if (quadrants[i][1][k] == Piece.EMPTY && quadrants[i][0][k] == opponent) {
					getVerticalPotentials()[i][j][k] += 2;
				} else if (quadrants[i][1][k] == mycolor && quadrants[i][0][k] == opponent) {
					getVerticalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][1][k] == mycolor && quadrants[i][0][k] == mycolor) {
					getVerticalPotentials()[i][j][k] += 6;
				} else if (quadrants[i][1][k] == mycolor && quadrants[i][0][k] == Piece.EMPTY) {
					getVerticalPotentials()[i][j][k] += 5;
				} else {
					System.out.println("Error calculating vertical potential");
				}
			}

		}

	}

	private void UpdateHorizontalPotential(int i, int j, int k) {
		if (quadrants[i][j][1] == opponent) {
			getHorizontalPotentials()[i][j][k] += 0;
		} else {
			if (k == 0) {
				if (quadrants[i][j][1] == Piece.EMPTY && quadrants[i][j][2] == Piece.EMPTY) {
					getHorizontalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][j][1] == Piece.EMPTY && quadrants[i][j][2] == mycolor) {
					getHorizontalPotentials()[i][j][k] += 4;
				} else if (quadrants[i][j][1] == Piece.EMPTY && quadrants[i][j][2] == opponent) {
					getHorizontalPotentials()[i][j][k] += 2;
				} else if (quadrants[i][j][1] == mycolor && quadrants[i][j][2] == opponent) {
					getHorizontalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][j][1] == mycolor && quadrants[i][j][2] == mycolor) {
					getHorizontalPotentials()[i][j][k] += 6;
				} else if (quadrants[i][2][1] == mycolor && quadrants[i][j][2] == Piece.EMPTY) {
					getHorizontalPotentials()[i][j][k] += 5;
				} else {
					System.out.println("Error calculating vertical potential");
				}
			} else if (k == 1) {
				if (quadrants[i][j][0] == Piece.EMPTY && quadrants[i][j][2] == Piece.EMPTY) {
					getHorizontalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][j][0] == Piece.EMPTY && quadrants[i][j][2] == mycolor) {
					getHorizontalPotentials()[i][j][k] += 4;
				} else if (quadrants[i][j][0] == mycolor && quadrants[i][j][2] == Piece.EMPTY) {
					getHorizontalPotentials()[i][j][k] += 4;
				} else if (quadrants[i][j][0] == Piece.EMPTY && quadrants[i][j][2] == opponent) {
					getHorizontalPotentials()[i][j][k] += 2;
				} else if (quadrants[i][j][0] == mycolor && quadrants[i][j][2] == mycolor) {
					getHorizontalPotentials()[i][j][k] += 6;
				} else if (quadrants[i][j][0] == mycolor && quadrants[i][j][2] == opponent) {
					getHorizontalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][j][0] == opponent && quadrants[i][j][2] == mycolor) {
					getHorizontalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][j][0] == opponent && quadrants[i][j][2] == Piece.EMPTY) {
					getHorizontalPotentials()[i][j][k] += 2;
				} else if (quadrants[i][j][0] == opponent && quadrants[i][j][2] == opponent) {
					getHorizontalPotentials()[i][j][k] += 0;
				} else {
					System.out.println("Error calculating vertical potential");
				}
			} else {
				if (quadrants[i][j][1] == Piece.EMPTY && quadrants[i][j][0] == Piece.EMPTY) {
					getHorizontalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][j][1] == Piece.EMPTY && quadrants[i][j][0] == mycolor) {
					getHorizontalPotentials()[i][j][k] += 4;
				} else if (quadrants[i][j][1] == Piece.EMPTY && quadrants[i][j][0] == opponent) {
					getHorizontalPotentials()[i][j][k] += 2;
				} else if (quadrants[i][j][1] == mycolor && quadrants[i][j][0] == opponent) {
					getHorizontalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][j][1] == mycolor && quadrants[i][j][0] == mycolor) {
					getHorizontalPotentials()[i][j][k] += 6;
				} else if (quadrants[i][j][1] == mycolor && quadrants[i][j][0] == Piece.EMPTY) {
					getHorizontalPotentials()[i][j][k] += 5;
				} else {
					System.out.println("Error calculating vertical potential");
				}
			}

		}

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
				System.out.print("|" + quadrants[0][line][i].toString());
			}
			for (int i = 0; i < 3; i++) {
				System.out.print("|" + quadrants[1][line][i].toString());
			}
			System.out.print("|\n");
		}
		for (int line = 0; line < 3; line++) {
			for (int i = 0; i < 3; i++) {
				System.out.print("|" + quadrants[2][line][i].toString());
			}
			for (int i = 0; i < 3; i++) {
				System.out.print("|" + quadrants[3][line][i].toString());
			}
			System.out.print("|\n");
		}
	}

	void PrintValuesBoardfromQuadrants(int[][][] quads) {
		for (int line = 0; line < 3; line++) {
			for (int i = 0; i < 3; i++) {
				System.out.print("|" + quads[0][line][i]);
			}
			for (int i = 0; i < 3; i++) {
				System.out.print("|" + quads[1][line][i]);
			}
			System.out.print("|\n");
		}
		for (int line = 0; line < 3; line++) {
			for (int i = 0; i < 3; i++) {
				System.out.print("|" + quads[2][line][i]);
			}
			for (int i = 0; i < 3; i++) {
				System.out.print("|" + quads[3][line][i]);
			}
			System.out.print("|\n");
		}
	}

	public int[][][] getVerticalPotentials() {
		return verticalPotentials;
	}

	public void setVerticalPotentials(int[][][] verticalPotentials) {
		this.verticalPotentials = verticalPotentials;
	}

	public int[][][] getHorizontalPotentials() {
		return horizontalPotentials;
	}

	public void setHorizontalPotentials(int[][][] horizontalPotentials) {
		this.horizontalPotentials = horizontalPotentials;
	}

	public int[][][] getDiagonalPotentials() {
		return diagonalPotentials;
	}

	public void setDiagonalPotentials(int[][][] diagonalPotentials) {
		this.diagonalPotentials = diagonalPotentials;
	}
}
