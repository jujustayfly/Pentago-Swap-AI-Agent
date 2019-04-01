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
	
	@Test
	public void TestPotentialScores3() {
		pbs = new PentagoBoardState();
		pbs.processMove((PentagoMove) pbs.getRandomMove());
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
	
	@Test
	public void TestPotentialSuperposition() {
		pbs = new PentagoBoardState();
		pbs.printBoard();
		BoardAnalyzer analyzer = new BoardAnalyzer(pbs);
		analyzer.GetMyColor();
		analyzer.GetQuadrants();
		analyzer.AssignMoveValues();
		analyzer.SuperposePotentials();
		System.out.println("Printing superposed potentials");
		analyzer.PrintValuesBoardfromQuadrants(analyzer.getSuperposedPotentials());
		
	}
	@Test
	public void TestMacroUpdateSuperposed() {
		pbs = new PentagoBoardState();
		pbs.processMove( new PentagoMove(0,0,Quadrant.BL,Quadrant.BR,pbs.getTurnPlayer()));
		pbs.processMove( new PentagoMove(5,5,Quadrant.BL,Quadrant.BR,pbs.getTurnPlayer()));
		pbs.processMove( new PentagoMove(0,1,Quadrant.BL,Quadrant.BR,pbs.getTurnPlayer()));
		pbs.processMove( new PentagoMove(3,3,Quadrant.BL,Quadrant.BR,pbs.getTurnPlayer()));
		pbs.processMove( new PentagoMove(0,2,Quadrant.BL,Quadrant.BR,pbs.getTurnPlayer()));
		pbs.processMove( new PentagoMove(2,5,Quadrant.BL,Quadrant.BR,pbs.getTurnPlayer()));

		System.out.println("Testing Macro Updated Potentials SUPERPOSED");
		pbs.printBoard();
		BoardAnalyzer analyzer = new BoardAnalyzer(pbs);
		analyzer.FindBestMove(0);
		analyzer.PrintValuesBoardfromQuadrants(analyzer.getSuperposedPotentials());
	}
	@Test
	public void TestMacroUpdateSumed() {
		pbs = new PentagoBoardState();
		pbs.processMove( new PentagoMove(0,0,Quadrant.BL,Quadrant.BR,pbs.getTurnPlayer()));
		pbs.processMove( new PentagoMove(5,5,Quadrant.BL,Quadrant.BR,pbs.getTurnPlayer()));
		pbs.processMove( new PentagoMove(0,1,Quadrant.BL,Quadrant.BR,pbs.getTurnPlayer()));
		pbs.processMove( new PentagoMove(3,3,Quadrant.BL,Quadrant.BR,pbs.getTurnPlayer()));
		pbs.processMove( new PentagoMove(0,2,Quadrant.BL,Quadrant.BR,pbs.getTurnPlayer()));
		pbs.processMove( new PentagoMove(2,5,Quadrant.BL,Quadrant.BR,pbs.getTurnPlayer()));

		System.out.println("Testing Macro Updated Potentials SUMED");
		pbs.printBoard();
		BoardAnalyzer analyzer = new BoardAnalyzer(pbs);
		analyzer.FindBestMove(1);
		analyzer.PrintValuesBoardfromQuadrants(analyzer.getSumedPotentials());
	}
	
}
