package student_player;

import java.util.ArrayList;

import boardgame.Move;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoBoardState.Piece;
import pentago_swap.PentagoBoardState.Quadrant;
import pentago_swap.PentagoMove;

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
	
	
	private int[][][] superposedPotentials = new int[4][3][3];
	private int[][][] sumedPotentials = new int[4][3][3];
	private int[][][] finalPlayerPotential= new int [4][3][3];
	private int[][][] finalBestMove= new int [4][3][3];
	private Piece mycolor;
	private Piece opponent;

	public BoardAnalyzer(PentagoBoardState boardState) {
		this.pbs = boardState;
	}

	public Move FindBestMove() {
		GetMyColor();
		if (canIWin() != null) {
			return canIWin();
		}
		GetQuadrants();
		// We first do our move value calculations from player perspective
		AssignMoveValues();
		SuperposePotentials();
		UpdateMacroPotentials();
		//Now we switch our perspective and compute move values from opponent perspective
		SwitchPerspectives();
		AssignMoveValues();
		SuperposePotentials();
		UpdateMacroPotentials();
		
		UpdatefinalBestMove();
//		System.out.println("Printing opponent perspective");
//		PrintValuesBoardfromQuadrants(superposedPotentials);
//		System.out.println("Printing player perspective");
//		PrintValuesBoardfromQuadrants(finalPlayerPotential);
//		System.out.println("Printing final perspective");
	//	PrintValuesBoardfromQuadrants(finalBestMove);
		PentagoMove mymove = ExtractMove();

		return mymove;

	}

	private void UpdatefinalBestMove() {
		for(int i = 0 ;i<4;i++) {
			for(int j =0; j<3;j++) {
				for(int k=0; k<3;k++) {
				finalBestMove[i][j][k]= maxFinalMove(i,j,k);
				}
			}
		}
		
	}

	private int maxFinalMove(int i, int j, int k) {
		if(superposedPotentials[i][j][k]>finalPlayerPotential[i][j][k]) {
			return superposedPotentials[i][j][k];
		}
		return finalPlayerPotential[i][j][k];
	}

	private void SwitchPerspectives() {
		//first copy our player potential
		//this is a bit hacky but seems like less code to modify overall
		// since we simply save our calculations
		// then inverse the player colors
		// then rerun the process
		// this was developed because otherwise the agent can lose
		// to some very greedy strategy which do not go through
		// any quadrant centers
		// it also allows to play track the game state from both perspectives
		for(int i = 0 ;i<4;i++) {
			for(int j =0; j<3;j++) {
				for(int k=0; k<3;k++) {
					finalPlayerPotential[i][j][k]= superposedPotentials[i][j][k];
				}
			}
		}
		
		if (mycolor==Piece.BLACK) {
			mycolor=Piece.WHITE;
			opponent= Piece.BLACK;
		}
		else {
			opponent=Piece.WHITE;
			mycolor= Piece.BLACK;
		}
		
		verticalPotentials = new int[4][3][3];
		horizontalPotentials = new int[4][3][3];
		diagonalPotentials = new int[4][3][3];
		superposedPotentials = new int[4][3][3];
		
	}

	

	



	private void UpdateDefenseMicro() {
	
		
	}

	private PentagoMove ExtractMove() {
		int[] coordinates = new int[3];
		int max = 1;
		PentagoBoardState copy ;
		PentagoMove perfectswap;
		int iterations = 35;
		while (iterations > 0) {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 3; j++) {
					for (int k = 0; k < 3; k++) {
						if (finalBestMove[i][j][k] > max) {

							max = finalBestMove[i][j][k];
							coordinates[0] = i;
							coordinates[1] = j;
							coordinates[2] = k;
						}
					}
				}
			}
			int[] flatcoords = flattenCoords(coordinates);
			 copy = (PentagoBoardState) pbs.clone();
			perfectswap =GetPerfectSwap(copy,flatcoords);
			if(perfectswap!=null) {
				if(MoveNotSuicide(perfectswap))
				return perfectswap;
			}
			PentagoMove tobechecked = new PentagoMove(flatcoords[0], flatcoords[1], Quadrant.BL, Quadrant.BR,
					pbs.getTurnPlayer());
			if (MoveNotSuicide(tobechecked)) {
				return tobechecked;
			}
			tobechecked = new PentagoMove(flatcoords[0], flatcoords[1], Quadrant.BL, Quadrant.TR, pbs.getTurnPlayer());
			if (MoveNotSuicide(tobechecked)) {
				return tobechecked;
			}
			tobechecked = new PentagoMove(flatcoords[0], flatcoords[1], Quadrant.BL, Quadrant.TL, pbs.getTurnPlayer());
			if (MoveNotSuicide(tobechecked)) {
				return tobechecked;
			}
			tobechecked = new PentagoMove(flatcoords[0], flatcoords[1], Quadrant.TL, Quadrant.TR, pbs.getTurnPlayer());
			if (MoveNotSuicide(tobechecked)) {
				return tobechecked;
			}
			tobechecked = new PentagoMove(flatcoords[0], flatcoords[1], Quadrant.TL, Quadrant.BR, pbs.getTurnPlayer());
			if (MoveNotSuicide(tobechecked)) {
				return tobechecked;
			}
			tobechecked = new PentagoMove(flatcoords[0], flatcoords[1], Quadrant.TR, Quadrant.BR, pbs.getTurnPlayer());
			if (MoveNotSuicide(tobechecked)) {
				return tobechecked;
			}
			superposedPotentials[coordinates[0]][coordinates[1]][coordinates[2]] = 0;
			iterations--;
		}
		return (PentagoMove) pbs.getRandomMove();
	}

	private PentagoMove GetPerfectSwap(PentagoBoardState copy, int[] flatcoords) {
		ArrayList<PentagoMove> moves = new ArrayList<PentagoMove>();
		ArrayList<Integer> scores = new ArrayList<Integer>();
		Integer score=0;
		PentagoMove tobechecked = new PentagoMove(flatcoords[0], flatcoords[1], Quadrant.BL, Quadrant.BR,pbs.getTurnPlayer());
		if (MoveNotSuicide(tobechecked)) {
			copy=(PentagoBoardState) pbs.clone();
			copy.processMove(tobechecked);
			score=0;
			if(has3NegDiagonal(opponent)>=0) {
				if(has3NegDiagonal(opponent)==0 ||has3NegDiagonal(opponent)==3) {
					score+=3;
				}
			}
			if(has3PosDiagonal(opponent)>=0) {
				if(has3NegDiagonal(opponent)==1 ||has3NegDiagonal(opponent)==2) {
					score+=3;
				}
			
			}
		}scores.add(score);	
		moves.add(tobechecked);
		score=0;
		tobechecked = new PentagoMove(flatcoords[0], flatcoords[1], Quadrant.BL, Quadrant.TR, pbs.getTurnPlayer());
		if (MoveNotSuicide(tobechecked)) {
			copy=(PentagoBoardState) pbs.clone();
			copy.processMove(tobechecked);
			score=0;
			if(has3NegDiagonal(opponent)>=0) {
				if(has3NegDiagonal(opponent)==0 ||has3NegDiagonal(opponent)==3) {
					score+=3;
				}
			}
			if(has3PosDiagonal(opponent)>=0) {
				if(has3NegDiagonal(opponent)==1 ||has3NegDiagonal(opponent)==2) {
					score+=3;
				}
				
			}
		}
		scores.add(score);	
		moves.add(tobechecked);
		score=0;
		tobechecked = new PentagoMove(flatcoords[0], flatcoords[1], Quadrant.BL, Quadrant.TL, pbs.getTurnPlayer());
		if (MoveNotSuicide(tobechecked)) {
			copy=(PentagoBoardState) pbs.clone();
			copy.processMove(tobechecked);
			score=0;
			if(has3NegDiagonal(opponent)>=0) {
				if(has3NegDiagonal(opponent)==0 ||has3NegDiagonal(opponent)==3) {
					score+=3;
				}
			}
			if(has3PosDiagonal(opponent)>=0) {
				if(has3NegDiagonal(opponent)==1 ||has3NegDiagonal(opponent)==2) {
					score+=3;
				}
				
			}
		}
		scores.add(score);	
		moves.add(tobechecked);
		score=0;
		tobechecked = new PentagoMove(flatcoords[0], flatcoords[1], Quadrant.TL, Quadrant.TR, pbs.getTurnPlayer());
		if (MoveNotSuicide(tobechecked)) {
			copy=(PentagoBoardState) pbs.clone();
			copy.processMove(tobechecked);
			score=0;
			if(has3NegDiagonal(opponent)>=0) {
				if(has3NegDiagonal(opponent)==0 ||has3NegDiagonal(opponent)==3) {
					score+=3;
				}
			}
			if(has3PosDiagonal(opponent)>=0) {
				if(has3NegDiagonal(opponent)==1 ||has3NegDiagonal(opponent)==2) {
					score+=3;
				}
				
			}
		}
		scores.add(score);	
		moves.add(tobechecked);
		score=0;
		tobechecked = new PentagoMove(flatcoords[0], flatcoords[1], Quadrant.TL, Quadrant.BR, pbs.getTurnPlayer());
		if (MoveNotSuicide(tobechecked)) {
			copy=(PentagoBoardState) pbs.clone();
			copy.processMove(tobechecked);
			score=0;
			if(has3NegDiagonal(opponent)>=0) {
				if(has3NegDiagonal(opponent)==0 ||has3NegDiagonal(opponent)==3) {
					score+=3;
				}
			}
			if(has3PosDiagonal(opponent)>=0) {
				if(has3NegDiagonal(opponent)==1 ||has3NegDiagonal(opponent)==2) {
					score+=3;
				}
				
			}
		}
		scores.add(score);	
		moves.add(tobechecked);
		score=0;
		tobechecked = new PentagoMove(flatcoords[0], flatcoords[1], Quadrant.TR, Quadrant.BR, pbs.getTurnPlayer());
		if (MoveNotSuicide(tobechecked)) {
			copy=(PentagoBoardState) pbs.clone();
			copy.processMove(tobechecked);
			score=0;
			if(has3NegDiagonal(opponent)>=0) {
				if(has3NegDiagonal(opponent)==0 ||has3NegDiagonal(opponent)==3) {
					score+=3;
				}
			}
			if(has3PosDiagonal(opponent)>=0) {
				if(has3NegDiagonal(opponent)==1 ||has3NegDiagonal(opponent)==2) {
					score+=3;
				}
				
			}
		}
		scores.add(score);	
		moves.add(tobechecked);
		int max=-1;
		int index=-1;
		for(int j = 0 ;j<scores.size();j++) {
			if(scores.get(j)>max) {
				index=j;
				max=scores.get(j);
			}
		}
		
		if(index!=-1) {
			return moves.get(index);
		}
		
		return null;
	}

	private boolean MoveNotSuicide(PentagoMove tobechecked) {
		PentagoBoardState temp = (PentagoBoardState) pbs.clone();
		temp.processMove(tobechecked);
		PentagoBoardState temp2 = (PentagoBoardState) temp.clone();
		for (PentagoMove trial : temp.getAllLegalMoves()) {
			temp2 = (PentagoBoardState) temp.clone();
			temp2.processMove(trial);
			if (temp2.getWinner() == temp2.getOpponent()) {
				return false;
			}
		}
		return true;
	}

	private int[] flattenCoords(int[] coordinates) {
		int[] temp = new int[2];
		if (coordinates[0] == 0) {
			temp[0] = coordinates[1];
			temp[1] = coordinates[2];
		} else if (coordinates[0] == 1) {
			temp[0] = coordinates[1];
			temp[1] = coordinates[2] + 3;
		} else if (coordinates[0] == 2) {
			temp[0] = coordinates[1] + 3;
			temp[1] = coordinates[2];
		} else if (coordinates[0] == 3) {
			temp[0] = coordinates[1] + 3;
			temp[1] = coordinates[2] + 3;
		}
		return temp;
	}

	private Move canIWin() {
		PentagoBoardState temp = new PentagoBoardState();
		for (PentagoMove trial : pbs.getAllLegalMoves()) {
			temp = (PentagoBoardState) pbs.clone();
			temp.processMove(trial);
			if (temp.getWinner() == temp.getOpponent()) {
				return trial;
			}
		}

		return null;
	}

	private void SumPotentials() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 3; k++) {
					getSumedPotentials()[i][j][k] = horizontalPotentials[i][j][k] + verticalPotentials[i][j][k]
							+ diagonalPotentials[i][j][k];
				}
			}
		}

	}

	private void UpdateMacroPotentials() {
		//Method logically checks for winning
		//directions. This is have 3 lined up 
		//in a quadrant either vertically, diagonally,
		//or vertically. It can also be having 2 lined up
		// in a side diagonal and 1 in other corresponding
		// side diagonal (in another quadrant)
		if (!(has3verticalLeft(mycolor) == -1)) {
			UpdateMacroVerticalLeft();
		}
		if (!(has3verticalMiddle(mycolor) == -1)) {
			UpdateMacroVerticalMiddle();
		}
		if (!(has3verticalRight(mycolor) == -1)) {
			UpdateMacroVerticalRight();
		}
		if (!(has3horizontalTop(mycolor) == -1)) {
			UpdateMacroHorizontalTop();
		}
		if (!(has3horizontalMiddle(mycolor) == -1)) {
			UpdateMacroHorizontalMiddle();
		}
		if (!(has3horizontalBottom(mycolor) == -1)) {
			UpdateMacroHorizontalBottom();
		}
		if (!(has3NegDiagonal(mycolor) == -1)) {
			UpdateMacroNegDiag();
		}
		if (!(has3PosDiagonal(mycolor) == -1)) {
			UpdateMacroPosDiag();
		}
		if (!(hasSmallDiagPosTop2and1(mycolor).size() == 0)) {
			UpdateSmallDiagPosTop2and1(hasSmallDiagPosTop2and1(mycolor));

		}
		if (!(hasSmallDiagPosBot2and1(mycolor).size() == 0)) {
			UpdateSmallDiagPosBot2and1(hasSmallDiagPosBot2and1(mycolor));
		}
		if (!(hasSmallDiagNegTop2and1(mycolor).size() == 0)) {
			UpdateSmallDiagNegTop2and1(hasSmallDiagNegTop2and1(mycolor));
		}
		if (!(hasSmallDiagNegBot2and1(mycolor).size() == 0)) {
			UpdateSmallDiagNegBot2and1(hasSmallDiagNegBot2and1(mycolor));
		}

	}

	private void UpdateSmallDiagNegBot2and1(ArrayList<Integer> quads) {
		for (int i : quads) {
			if(superposedPotentials[i][1][0]>=0)
			superposedPotentials[i][1][0] += 3;
			if(superposedPotentials[i][2][1]>=0)
			superposedPotentials[i][2][1] += 3;
			if(superposedPotentials[i][0][2]>=0)
				superposedPotentials[i][0][2] += 3;
		}

	}

	private void UpdateSmallDiagNegTop2and1(ArrayList<Integer> quads) {
		for (int i : quads) {
			if(superposedPotentials[i][0][1]>=0)
			superposedPotentials[i][0][1] += 3;
			if(superposedPotentials[i][1][2]>=0)
			superposedPotentials[i][1][2] += 3;
			if(superposedPotentials[i][2][0]>=0)
				superposedPotentials[i][2][0] += 3;
		}

	}

	private void UpdateSmallDiagPosBot2and1(ArrayList<Integer> quads) {
		for (int i : quads) {
			if(superposedPotentials[i][2][1]>=0)
			superposedPotentials[i][2][1] += 3;
			if(superposedPotentials[i][1][2]>=0)
			superposedPotentials[i][1][2] += 3;
			if(superposedPotentials[i][0][0]>=0)
			superposedPotentials[i][0][0] += 3;

		}

	}

	private void UpdateSmallDiagPosTop2and1(ArrayList<Integer> quads) {
		for (int i : quads) {
			if(superposedPotentials[i][0][1]>=0)
			superposedPotentials[i][0][1] += 3;
			if(superposedPotentials[i][1][0]>=0)
			superposedPotentials[i][1][0] += 3;
			if(superposedPotentials[i][2][0]>=0)
				superposedPotentials[i][2][0]+=3;

		}

	}

	private void UpdateMacroPosDiag() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				if(j==1) {
					if(superposedPotentials[i][2][0]!=0 && superposedPotentials[i][1][1]!=0  && superposedPotentials[i][0][2]!=0) {
						superposedPotentials[i][1][1]+=2;
					}
				}
				if ((superposedPotentials[i][2 - j][j] >= 2)) {
					superposedPotentials[i][2 - j][j] += 3;
				}
			}
		}

	}

	private void UpdateMacroNegDiag() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				if(j==1) {
					if(superposedPotentials[i][0][0]!=0 && superposedPotentials[i][2][2]!=0  && superposedPotentials[i][1][1]!=0) {
						superposedPotentials[i][1][1]+=2;
					}
				}
				if ((superposedPotentials[i][j][j] >= 2)) {
					superposedPotentials[i][j][j] += 3;
				}
			}
		}

	}

	private void UpdateMacroHorizontalBottom() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				if(j==1) {
					if(superposedPotentials[i][2][1]!=0 && superposedPotentials[i][2][0]!=0  && superposedPotentials[i][2][2]!=0) {
						superposedPotentials[i][2][1]+=3;
					}
				}
				if ((superposedPotentials[i][2][j] >= 2)) {
					superposedPotentials[i][2][j] += 3;
				}
			}
		}

	}

	private void UpdateMacroHorizontalMiddle() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				if(j==1) {
					if(superposedPotentials[i][1][1]!=0 && superposedPotentials[i][1][0]!=0  && superposedPotentials[i][1][2]!=0) {
						superposedPotentials[i][1][1]+=3;
					}
				}
				if ((superposedPotentials[i][1][j] >= 2)) {
					superposedPotentials[i][1][j] += 3;
				}
			}
		}

	}

	private void UpdateMacroHorizontalTop() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				if(j==1) {
					if(superposedPotentials[i][0][1]!=0 && superposedPotentials[i][0][0]!=0  && superposedPotentials[i][0][2]!=0) {
						superposedPotentials[i][0][1]+=3;
					}
				}
				if ((superposedPotentials[i][0][j] >= 2)) {
					superposedPotentials[i][0][j] += 3;
				}
			}
		}

	}

	private void UpdateMacroVerticalRight() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				if(j==1) {
					if(superposedPotentials[i][0][2]!=0 && superposedPotentials[i][2][2]!=0  && superposedPotentials[i][1][2]!=0) {
						superposedPotentials[i][1][2]+=3;
					}
				}
				if ((superposedPotentials[i][j][2] >= 2)) {
					superposedPotentials[i][j][2] += 3;
				}
			}
		}
	}

	private void UpdateMacroVerticalMiddle() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				if(j==1) {
					if(superposedPotentials[i][0][1]!=0 && superposedPotentials[i][2][1]!=0  && superposedPotentials[i][1][1]!=0) {
						superposedPotentials[i][1][1]+=3;
					}
				}
				if ((superposedPotentials[i][j][1] >= 2)) {
					superposedPotentials[i][j][1] += 3;
				}
			}
		}

	}

	private void UpdateMacroVerticalLeft() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				if(j==1) {
					if(superposedPotentials[i][0][0]!=0 && superposedPotentials[i][2][0]!=0  && superposedPotentials[i][1][0]!=0) {
						superposedPotentials[i][1][0]+=3;
					}
				}
				if ((superposedPotentials[i][j][0] >= 2)) {
					superposedPotentials[i][j][0] += 3;
				}
			}
		}

	}

	private ArrayList<Integer> hasSmallDiagNegBot2and1(Piece color) {
		boolean has2 = false;
		boolean has1 = false;
		int has2quad = -1;
		int has1quad = -1;
		ArrayList<Integer> quads = new ArrayList<Integer>();
		// check for 2
		for (int i = 0; i < 4; i++) {
			if (quadrants[i][1][0] == mycolor && quadrants[i][2][1] == color) {
				has2 = true;
				has2quad = i;
			}
		}
		// check for 1 in different quadrant
		if (has2) {
			for (int i = 0; i < 4; i++) {
				if (i != has2quad) {
					if(quadrants[i][0][2] == mycolor) {
					has1 = true;
					has1quad = i;
					}
					else if(quadrants[i][1][0] == mycolor && quadrants[i][2][1] == Piece.EMPTY) {
						has1 = true;
						has1quad = i;
						superposedPotentials[i][2][1]+=5;
					}
					else if(quadrants[i][2][1] == mycolor && quadrants[i][1][0] == Piece.EMPTY) {
						has1 = true;
						has1quad = i;
						superposedPotentials[i][1][0]+=5;
						
					}
					
				} 
			}
			if (has1) {
				for (int i = 0; i < 4; i++) {
					if (i != has2quad) {
						quads.add(i);
					}
				}
			}
		}
		return quads;
	}

	private ArrayList<Integer> hasSmallDiagNegTop2and1(Piece color) {
		boolean has2 = false;
		boolean has1 = false;
		int has2quad = -1;
		int has1quad = -1;
		ArrayList<Integer> quads = new ArrayList<Integer>();
		// check for 2
		for (int i = 0; i < 4; i++) {
			if (quadrants[i][0][1] == mycolor && quadrants[i][1][2] == color) {
				has2 = true;
				has2quad = i;
			}
		}
		// check for 1 in different quadrant
		if (has2) {
			for (int i = 0; i < 4; i++) {
				if (i != has2quad) {
					if(quadrants[i][2][0] == mycolor) {
					has1 = true;
					has1quad = i;
					}
					else if(quadrants[i][1][2] == mycolor && quadrants[i][0][1] == Piece.EMPTY) {
						has1 = true;
						has1quad = i;
						superposedPotentials[i][0][1]+=5;
					}
					else if(quadrants[i][0][1] == mycolor && quadrants[i][1][2] == Piece.EMPTY) {
						has1 = true;
						has1quad = i;
						superposedPotentials[i][1][2]+=5;
						
					}}
//				if ((quadrants[i][2][0] == mycolor ||
//						(quadrants[i][1][2] == mycolor && quadrants[i][0][1] == Piece.EMPTY)||
//						(quadrants[i][0][1] == mycolor && quadrants[i][1][2] == Piece.EMPTY)
//						) && i != has2quad) {
//					has1 = true;
//					has1quad = i;
//				}
			}
			if (has1) {
				for (int i = 0; i < 4; i++) {
					if (i != has2quad) {
						quads.add(i);
					}
				}
			}
		}
		return quads;
	}

	private ArrayList<Integer> hasSmallDiagPosBot2and1(Piece color) {
		boolean has2 = false;
		boolean has1 = false;
		int has2quad = -1;
		int has1quad = -1;
		ArrayList<Integer> quads = new ArrayList<Integer>();
		// check for 2
		for (int i = 0; i < 4; i++) {
			if (quadrants[i][2][1] == mycolor && quadrants[i][1][2] == color) {
				has2 = true;
				has2quad = i;
			}
		}
		// check for 1 in different quadrant
		if (has2) {
			for (int i = 0; i < 4; i++) {
				if (i != has2quad) {
					if(quadrants[i][0][0] == mycolor) {
					has1 = true;
					has1quad = i;
					}
					else if(quadrants[i][2][1] == mycolor && quadrants[i][1][2] == Piece.EMPTY) {
						has1 = true;
						has1quad = i;
						superposedPotentials[i][1][2]+=5;
					}
					else if(quadrants[i][1][2] == mycolor && quadrants[i][2][1] == Piece.EMPTY) {
						has1 = true;
						has1quad = i;
						superposedPotentials[i][2][1]+=5;
						
					}
			}
//				if ((quadrants[i][0][0] == mycolor ||
//						(quadrants[i][2][1] == mycolor && quadrants[i][1][2] == Piece.EMPTY)||
//						(quadrants[i][1][2] == mycolor && quadrants[i][2][1] == Piece.EMPTY)
//						)&& i != has2quad) {
//					has1 = true;
//					has1quad = i;
//				}
			}
			if (has1) {
				for (int i = 0; i < 4; i++) {
					if (i != has2quad ) {
						quads.add(i);
					}
				}
			}
		}
		return quads;
	}

	private ArrayList<Integer> hasSmallDiagPosTop2and1(Piece color) {
		boolean has2 = false;
		boolean has1 = false;
		int has2quad = -1;
		int has1quad = -1;
		ArrayList<Integer> quads = new ArrayList<Integer>();
		// check for 2
		for (int i = 0; i < 4; i++) {
			if (quadrants[i][0][1] == mycolor && quadrants[i][1][0] == color) {
				has2 = true;
				has2quad = i;
			}
		}
		// check for 1 in different quadrant
		if (has2) {
			for (int i = 0; i < 4; i++) {
				if (i != has2quad) {
					if(quadrants[i][2][2] == mycolor) {
					has1 = true;
					has1quad = i;
					}
					else if(quadrants[i][0][1] == mycolor && quadrants[i][1][0] == Piece.EMPTY) {
						has1 = true;
						has1quad = i;
						superposedPotentials[i][1][0]+=5;
					}
					else if(quadrants[i][0][1] == mycolor && quadrants[i][0][1] == Piece.EMPTY) {
						has1 = true;
						has1quad = i;
						superposedPotentials[i][1][0]+=5;
						
					}
			}
//				if ((quadrants[i][2][2] == mycolor ||
//						(quadrants[i][0][1] == mycolor && quadrants[i][1][0] == Piece.EMPTY)||
//						(quadrants[i][1][0] == mycolor && quadrants[i][0][1] == Piece.EMPTY)
//						)&& i != has2quad) {
//					has1 = true;
//					has1quad = i;
//				}
			}
			if (has1) {
				for (int i = 0; i < 4; i++) {
					if (i != has2quad) {
						quads.add(i);
					}
				}
			}
		}
		return quads;
	}

	private int has3PosDiagonal(Piece color) {
		boolean has3;

		for (int i = 0; i < 4; i++) {
			has3 = true;
			for (int j = 0; j < 3; j++) {
				if (!(quadrants[i][2 - j][j] == color)) {
					has3 = false;
				}
			}
			if (has3) {
				return i;
			}
		}
		return -1;
	}

	private int has3NegDiagonal(Piece color) {
		boolean has3;

		for (int i = 0; i < 4; i++) {
			has3 = true;
			for (int j = 0; j < 3; j++) {
				if (!(quadrants[i][j][j] == color)) {
					has3 = false;
				}
			}
			if (has3) {
				return i;
			}
		}
		return -1;

	}

	private int has3horizontalBottom(Piece color) {
		boolean has3;

		for (int i = 0; i < 4; i++) {
			has3 = true;
			for (int j = 0; j < 3; j++) {
				if (!(quadrants[i][2][j] == mycolor)) {
					has3 = false;
				}
			}
			if (has3) {
				return i;
			}
		}
		return -1;
	}

	private int has3horizontalMiddle(Piece color) {
		boolean has3;

		for (int i = 0; i < 4; i++) {
			has3 = true;
			for (int j = 0; j < 3; j++) {
				if (!(quadrants[i][1][j] == color)) {
					has3 = false;
				}
			}
			if (has3) {
				return i;
			}
		}
		return -1;
	}

	private int has3horizontalTop(Piece color) {
		boolean has3;

		for (int i = 0; i < 4; i++) {
			has3 = true;
			for (int j = 0; j < 3; j++) {
				if (!(quadrants[i][0][j] == color)) {
					has3 = false;
				}
			}
			if (has3) {
				return i;
			}
		}
		return -1;
	}

	private int has3verticalMiddle(Piece color) {
		boolean has3;

		for (int i = 0; i < 4; i++) {
			has3 = true;
			for (int j = 0; j < 3; j++) {
				if (!(quadrants[i][j][1] == color)) {
					has3 = false;
				}
			}
			if (has3) {
				return i;
			}
		}
		return -1;

	}

	private int has3verticalRight(Piece color) {
		boolean has3;

		for (int i = 0; i < 4; i++) {
			has3 = true;
			for (int j = 0; j < 3; j++) {
				if (!(quadrants[i][j][2] == color)) {
					has3 = false;
				}
			}
			if (has3) {
				return i;
			}
		}
		return -1;
	}

	private int has3verticalLeft(Piece color) {
		// METHOD CHECKS TO SEE IF
		// PLAYER HAS 3 lined up vertically on the left
		// column in any quadrant
		// if it finds such a quadrant it returns the index of this quadrant
		// otherwise returns -1
		
		
		boolean has3;

		for (int i = 0; i < 4; i++) {
			has3 = true;
			for (int j = 0; j < 3; j++) {
				if (!(quadrants[i][j][0] == color)) {
					has3 = false;
				}
			}
			if (has3) {
				return i;
			}
		}
		return -1;
	}

	void SuperposePotentials() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 3; k++) {
					getSuperposedPotentials()[i][j][k] = maxPotential(i, j, k);
				}
			}
		}
	}

	private int maxPotential(int i, int j, int k) {
		if (horizontalPotentials[i][j][k] >= verticalPotentials[i][j][k]
				&& horizontalPotentials[i][j][k] >= diagonalPotentials[i][j][k]) {
			return horizontalPotentials[i][j][k];
		} else if (verticalPotentials[i][j][k] >= diagonalPotentials[i][j][k]
				&& verticalPotentials[i][j][k] >= horizontalPotentials[i][j][k]) {
			return verticalPotentials[i][j][k];
		} else if (diagonalPotentials[i][j][k] >= verticalPotentials[i][j][k]
				&& diagonalPotentials[i][j][k] >= horizontalPotentials[i][j][k]) {
			return diagonalPotentials[i][j][k];
		}
		return 0;
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
					getDiagonalPotentials()[i][j][k] += 6;
				} else if (quadrants[i][1][1] == mycolor && quadrants[i][2][2] == mycolor) {
					getDiagonalPotentials()[i][j][k] += 7;
				} else if (quadrants[i][1][1] == mycolor && quadrants[i][2][2] == opponent) {
					getDiagonalPotentials()[i][j][k] += 3;
				} else {
					System.out.println("Error calculating Neg Slope Potential");
				}
			} else if (j == 1 && k == 1) {
				// need 9 here
				if (quadrants[i][0][0] == Piece.EMPTY && quadrants[i][2][2] == Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][0][0] == Piece.EMPTY && quadrants[i][2][2] == mycolor) {
					getDiagonalPotentials()[i][j][k] += 4;
				} else if (quadrants[i][0][0] == Piece.EMPTY && quadrants[i][2][2] == opponent) {
					getDiagonalPotentials()[i][j][k] += 2;
				} else if (quadrants[i][0][0] == mycolor && quadrants[i][2][2] == Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 6;
				} else if (quadrants[i][0][0] == mycolor && quadrants[i][2][2] == mycolor) {
					getDiagonalPotentials()[i][j][k] += 7;
				} else if (quadrants[i][0][0] == mycolor && quadrants[i][2][2] == opponent) {
					getDiagonalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][0][0] == opponent && quadrants[i][2][2] == Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 2;
				} else if (quadrants[i][0][0] == opponent && quadrants[i][2][2] == opponent) {
					getDiagonalPotentials()[i][j][k] += 0;
				} else if (quadrants[i][0][0] == opponent && quadrants[i][2][2] == mycolor) {
					getDiagonalPotentials()[i][j][k] += 3;
				} else {
					System.out.println("Error calculating Neg Slope Potential");
				}
			} else if (j == 2 && k == 2) {
				if (quadrants[i][1][1] == Piece.EMPTY && quadrants[i][0][0] == Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][1][1] == Piece.EMPTY && quadrants[i][0][0] == mycolor) {
					getDiagonalPotentials()[i][j][k] += 4;
				} else if (quadrants[i][1][1] == Piece.EMPTY && quadrants[i][0][0] == opponent) {
					getDiagonalPotentials()[i][j][k] += 2;
				} else if (quadrants[i][1][1] == mycolor && quadrants[i][0][0] == Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 4;
				} else if (quadrants[i][1][1] == mycolor && quadrants[i][0][0] == mycolor) {
					getDiagonalPotentials()[i][j][k] += 7;
				} else if (quadrants[i][1][1] == mycolor && quadrants[i][0][0] == opponent) {
					getDiagonalPotentials()[i][j][k] += 3;
				} else {
					System.out.println("Error calculating Neg Slope Potential");
				}
			}
		}

	}

	private void UpdateSmallDiagPotentials(int i, int j, int k) {
		if (j == 0 && k == 1) {
			if (quadrants[i][1][0] == Piece.EMPTY && quadrants[i][1][2] == Piece.EMPTY) {
				getDiagonalPotentials()[i][j][k] += 2;
			} else if (!(quadrants[i][1][0] == opponent && quadrants[i][1][2] == opponent)) {
				getDiagonalPotentials()[i][j][k] += 1;
			}
		} else if (j == 2 && k == 1) {
			if (quadrants[i][1][0] == Piece.EMPTY && quadrants[i][1][2] == Piece.EMPTY) {
				getDiagonalPotentials()[i][j][k] += 2;
			} else if (!(quadrants[i][1][0] == opponent && quadrants[i][1][2] == opponent)) {
				getDiagonalPotentials()[i][j][k] += 1;
			}
		} else if (j == 1 && k == 0) {
			if (quadrants[i][0][1] == Piece.EMPTY && quadrants[i][2][1] == Piece.EMPTY) {
				getDiagonalPotentials()[i][j][k] += 2;
			} else if (!(quadrants[i][0][1] == opponent && quadrants[i][2][1] == opponent)) {
				getDiagonalPotentials()[i][j][k] += 1;
			}
		} else if (j == 1 && k == 2) {
			if (quadrants[i][0][1] == Piece.EMPTY && quadrants[i][2][1] == Piece.EMPTY) {
				getDiagonalPotentials()[i][j][k] += 2;
			} else if (!(quadrants[i][0][1] == opponent && quadrants[i][2][1] == opponent)) {
				getDiagonalPotentials()[i][j][k] += 1;
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
					getDiagonalPotentials()[i][j][k] += 6;
				} else if (quadrants[i][1][1] == mycolor && quadrants[i][0][2] == mycolor) {
					getDiagonalPotentials()[i][j][k] += 7;
				} else if (quadrants[i][1][1] == mycolor && quadrants[i][0][2] == opponent) {
					getDiagonalPotentials()[i][j][k] += 4;
				} else {
					System.out.println("Error calculating Pos Slope Potential");
				}
			} else if (j == 1 && k == 1) {
				// need 9 here
				if (quadrants[i][0][2] == Piece.EMPTY && quadrants[i][2][0] == Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][0][2] == Piece.EMPTY && quadrants[i][2][0] == mycolor) {
					getDiagonalPotentials()[i][j][k] += 4;
				} else if (quadrants[i][0][2] == Piece.EMPTY && quadrants[i][2][0] == opponent) {
					getDiagonalPotentials()[i][j][k] += 2;
				} else if (quadrants[i][0][2] == mycolor && quadrants[i][2][0] == Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 4;
				} else if (quadrants[i][0][2] == mycolor && quadrants[i][2][0] == mycolor) {
					getDiagonalPotentials()[i][j][k] += 7;
				} else if (quadrants[i][0][2] == mycolor && quadrants[i][2][0] == opponent) {
					getDiagonalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][0][2] == opponent && quadrants[i][2][0] == Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 2;
				} else if (quadrants[i][0][2] == opponent && quadrants[i][2][0] == opponent) {
					getDiagonalPotentials()[i][j][k] += 0;
				} else if (quadrants[i][0][2] == opponent && quadrants[i][2][0] == mycolor) {
					getDiagonalPotentials()[i][j][k] += 3;
				} else {
					System.out.println("Error calculating Pos Slope Potential");
				}
			} else if (j == 0 && k == 2) {
				if (quadrants[i][1][1] == Piece.EMPTY && quadrants[i][2][0] == Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][1][1] == Piece.EMPTY && quadrants[i][2][0] == mycolor) {
					getDiagonalPotentials()[i][j][k] += 4;
				} else if (quadrants[i][1][1] == Piece.EMPTY && quadrants[i][2][0] == opponent) {
					getDiagonalPotentials()[i][j][k] += 2;
				} else if (quadrants[i][1][1] == mycolor && quadrants[i][2][0] == Piece.EMPTY) {
					getDiagonalPotentials()[i][j][k] += 6;
				} else if (quadrants[i][1][1] == mycolor && quadrants[i][2][0] == mycolor) {
					getDiagonalPotentials()[i][j][k] += 7;
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
					getHorizontalPotentials()[i][j][k] += 7;
				} else if (quadrants[i][j][1] == mycolor && quadrants[i][j][2] == Piece.EMPTY) {
					getHorizontalPotentials()[i][j][k] += 5;
				} else {
					System.out.println("Error calculating Horizontal potential");
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
					getHorizontalPotentials()[i][j][k] += 7;
				} else if (quadrants[i][j][0] == mycolor && quadrants[i][j][2] == opponent) {
					getHorizontalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][j][0] == opponent && quadrants[i][j][2] == mycolor) {
					getHorizontalPotentials()[i][j][k] += 3;
				} else if (quadrants[i][j][0] == opponent && quadrants[i][j][2] == Piece.EMPTY) {
					getHorizontalPotentials()[i][j][k] += 2;
				} else if (quadrants[i][j][0] == opponent && quadrants[i][j][2] == opponent) {
					getHorizontalPotentials()[i][j][k] += 0;
				} else {
					System.out.println("Error calculating Horizontal potential");
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
					getHorizontalPotentials()[i][j][k] += 7;
				} else if (quadrants[i][j][1] == mycolor && quadrants[i][j][0] == Piece.EMPTY) {
					getHorizontalPotentials()[i][j][k] += 5;
				} else {
					System.out.println("Error calculating Horizontal potential");
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

	public int[][][] getSuperposedPotentials() {
		return superposedPotentials;
	}

	public void setSuperposedPotentials(int[][][] superposedPotentials) {
		this.superposedPotentials = superposedPotentials;
	}

	public int[][][] getSumedPotentials() {
		return sumedPotentials;
	}

	public void setSumedPotentials(int[][][] sumedPotentials) {
		this.sumedPotentials = sumedPotentials;
	}
}
