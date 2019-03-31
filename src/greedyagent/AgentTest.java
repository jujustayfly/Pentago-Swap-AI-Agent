package greedyagent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import boardgame.Move;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoBoardState.Quadrant;
import pentago_swap.PentagoMove;

public class AgentTest {

	PentagoBoardState pbs = new PentagoBoardState();
	
	@Test
	public void TestQuandrantBreakdown() {
		pbs.processMove( new PentagoMove(1,2,Quadrant.TL,Quadrant.BR,0));
		pbs.processMove( new PentagoMove(4,4,Quadrant.TR,Quadrant.BR,1));
		pbs.printBoard();
		BoardAnalyzer analyzer = new BoardAnalyzer(pbs);
		analyzer.GetQuadrants();
		analyzer.PrintBoardfromQuadrants();
	}
}
