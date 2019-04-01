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
		pbs = new PentagoBoardState();
		pbs.processMove( new PentagoMove(1,2,Quadrant.TL,Quadrant.BR,0));
		pbs.processMove( new PentagoMove(4,4,Quadrant.TR,Quadrant.BR,1));
		pbs.printBoard();
		BoardAnalyzer analyzer = new BoardAnalyzer(pbs);
		analyzer.GetQuadrants();
		analyzer.PrintBoardfromQuadrants();
	}
	
	@Test
	public void TestPotentialScores() {
		pbs = new PentagoBoardState();
		pbs.printBoard();
		BoardAnalyzer analyzer = new BoardAnalyzer(pbs);
		analyzer.GetQuadrants();
		analyzer.AssignMoveValues();
		System.out.println("Printing horizontal potentials");
		analyzer.PrintValuesBoardfromQuadrants(analyzer.getHorizontalPotentials());
		System.out.println("Printing vertical potentials");
		analyzer.PrintValuesBoardfromQuadrants(analyzer.getVerticalPotentials());
		System.out.println("Printing diagonal potentials");
		analyzer.PrintValuesBoardfromQuadrants(analyzer.getDiagonalPotentials());
	}
	
	@Test
	public void TestPotentialScores2() {
		pbs = new PentagoBoardState();
		pbs.processMove((PentagoMove) pbs.getRandomMove());
		pbs.printBoard();
		BoardAnalyzer analyzer = new BoardAnalyzer(pbs);
		analyzer.GetMyColor();
		analyzer.GetQuadrants();
		analyzer.AssignMoveValues();
		System.out.println("Printing horizontal potentials");
		analyzer.PrintValuesBoardfromQuadrants(analyzer.getHorizontalPotentials());
		System.out.println("Printing vertical potentials");
		analyzer.PrintValuesBoardfromQuadrants(analyzer.getVerticalPotentials());
		System.out.println("Printing diagonal potentials");
		analyzer.PrintValuesBoardfromQuadrants(analyzer.getDiagonalPotentials());
	}
}
