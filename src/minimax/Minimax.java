package minimax;

import boardgame.Move;

import pentago_swap.PentagoPlayer;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoBoardState.Quadrant;
import pentago_swap.PentagoMove;

/** A player file submitted by a student. */
public class Minimax extends PentagoPlayer {

	/**
	 * You must modify this constructor to return your student number. This is
	 * important, because this is what the code that runs the competition uses to
	 * associate you with your agent. The constructor should do nothing else.
	 */
	public Minimax() {
		super("Minimax");
	}

	/**
	 * This is the primary method that you need to implement. The ``boardState``
	 * object contains the current state of the game, which your agent must use to
	 * make decisions.
	 */
	public Move chooseMove(PentagoBoardState boardState) {
		// You probably will make separate functions in MyTools.
		// For example, maybe you'll need to load some pre-processed best opening
		// strategies...

		// declaring variables
		Move myMove;
		
//		System.out.println("!!!!!");
//		System.out.println(turn);
//		System.out.println("!!!!!");
		// play move 0
	

		myMove = MyTools.FindBestMove(boardState);

		// Return your move to be processed by the server.
		return myMove;
	}
}