package student_player;

/* Greedy Agent 
 * */
import pentago_swap.PentagoMove;

import java.util.ArrayList;

import boardgame.Move;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoBoardState.Piece;
import pentago_swap.PentagoBoardState.Quadrant;

public class MyTools {
	public static double getSomething() {
		return Math.random();
	}

	public static PentagoMove getMove0(PentagoBoardState boardstate) {

		PentagoMove firstmove = new PentagoMove(1, 1, Quadrant.TL, Quadrant.BL, boardstate.getTurnPlayer());
		if (boardstate.isLegal(firstmove)) {
			return firstmove;
		} else {
			firstmove = new PentagoMove(4, 1, Quadrant.TL, Quadrant.BL, boardstate.getTurnPlayer());
			if (boardstate.isLegal(firstmove)) {
				return firstmove;
			} else {
				return new PentagoMove(1, 4, Quadrant.TL, Quadrant.BL, boardstate.getTurnPlayer());
			}
		}
	}

	public static PentagoMove getMove1(PentagoBoardState boardstate) {
		PentagoMove firstmove = new PentagoMove(1, 1, Quadrant.TL, Quadrant.BL, boardstate.getTurnPlayer());
		if (boardstate.isLegal(firstmove)) {
			return firstmove;
		} else {
			return new PentagoMove(4, 1, Quadrant.TL, Quadrant.BL, boardstate.getTurnPlayer());
		}
	}

	public static Move getMove(PentagoBoardState boardState) {

		return null;
	}
	
	public Piece[][] GetBoard(PentagoBoardState pbs){
		Piece[][] board = new Piece[6][6];
		for(int i = 0;i<pbs.BOARD_SIZE;i++) {
			for(int j = 0;j<pbs.BOARD_SIZE;j++) {
				board[i][j]=pbs.getPieceAt(i, j);
			}
		}
		return board;
	}
	
	public boolean checkBoardsEqual(Piece[][] board1, Piece[][] board2) {
		for(int i = 0;i<6;i++) {
			for(int j = 0;j<6;j++) {
				if(board1[i][j]!=board2[i][j]) {
					return false;
				}
			}
		}
		return true;
	}
}