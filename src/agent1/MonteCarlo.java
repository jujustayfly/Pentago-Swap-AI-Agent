package agent1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import boardgame.Board;
import boardgame.Move;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

public class MonteCarlo {

	ArrayList<Move> moves;
	ArrayList<Node> decisionNodes;
	static Node root;
	static Node current;

	public MonteCarlo(PentagoBoardState pbs) {
		this.root = new Node(pbs);
	}

	public Move FindBestMove(int iterations) {
		PentagoMove move;
		PentagoBoardState tempPbs;
		current = root;
		current.GenChildren();
		Node node;
		
		for(Node node2 :current.children) {
			if(node2.pbs.getWinner()==node2.pbs.getOpponent()) {
				return node2.parentMove;
			}
		}
		while (iterations > 0) {
			 Collections.sort(current.children, new SortbyUCB1()); 
			 node=current.children.get(0);
			PropagateRandomOutcome(node);
			node.UpdateUBC1();

			iterations--;
		}
		move = (PentagoMove) current.extractBestMove();
		return move;
	}

	public static int PropagateRandomOutcome(Node node) {
		Move move;
		PentagoBoardState pbs = node.pbs;
		while (pbs.getWinner() == Board.NOBODY) {
			double rand = Math.random();
			move = pbs.getAllLegalMoves().get((int) (rand * pbs.getAllLegalMoves().size()));
			pbs.processMove((PentagoMove) move);
		}
		if (pbs.getWinner() == root.pbs.getTurnPlayer()) {
			UpdateChildScores(node, 3);
		} else if (pbs.getWinner() == Board.DRAW) {
			UpdateChildScores(node, 1);
		} else {
			UpdateChildScores(node, -1);
		}
		return 0;
	}

	public static void UpdateChildScores(Node node, int score) {
		node.score += score;
		node.attempts += 1;
		current.attempts += 1;

	}
}
