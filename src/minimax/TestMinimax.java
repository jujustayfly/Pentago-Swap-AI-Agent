package minimax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import boardgame.Board;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

public class TestMinimax {

	PentagoBoardState pbs = new PentagoBoardState();
	
	@Test
	public void testMinimax() {
		for(int i=0;i<22;i++) {
		pbs.processMove((PentagoMove) pbs.getRandomMove());
		System.out.println(i);
		pbs.printBoard();
		
		if(pbs.getWinner()!=Board.NOBODY){
			break;
		}
		}
		PentagoMove mymove=(PentagoMove) MyTools.FindBestMove(pbs);
		System.out.println(mymove.toString());
		assertEquals(0, 0);
		
		
	}
}
