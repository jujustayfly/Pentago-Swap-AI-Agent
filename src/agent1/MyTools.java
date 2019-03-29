package agent1;

/* Greedy Agent 
 * 1- Finds best move within any of 4 quadrants
 * 2- Finds best two quadrants to flip
 * */
import pentago_swap.PentagoMove;

import java.util.ArrayList;

import boardgame.Move;
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
		else {
			firstmove = new PentagoMove(4, 1, Quadrant.TL, Quadrant.BL, boardstate.getTurnPlayer());
			if (boardstate.isLegal(firstmove)) {
				return firstmove;
			}else {
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
}