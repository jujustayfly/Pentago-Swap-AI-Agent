package student_player;

import pentago_swap.PentagoMove;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoBoardState.Quadrant;

public class MyTools {
	public static double getSomething() {
		return Math.random();
	}

	public static PentagoMove getMove0(PentagoBoardState boardstate) {
		PentagoMove firstmove = new PentagoMove(1, 1, Quadrant.TL, Quadrant.BL, boardstate.getTurnPlayer());
		if (boardstate.isLegal(firstmove)) {
			return firstmove;
		}
		return null;
	}
	public static PentagoMove getMove1(PentagoBoardState boardstate) {
		PentagoMove firstmove = new PentagoMove(1, 1, Quadrant.TL, Quadrant.BL, boardstate.getTurnPlayer());
		if (boardstate.isLegal(firstmove)) {
			return firstmove;
		}else {
			return new PentagoMove(4, 1, Quadrant.TL, Quadrant.BL, boardstate.getTurnPlayer());
		}
	}
}