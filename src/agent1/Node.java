package agent1;

import java.util.ArrayList;
import java.util.Comparator;

import boardgame.Board;
import boardgame.Move;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

public class Node {
	ArrayList<Node> children = new ArrayList<Node>();
	Node parent;
	Move parentMove;
	int attempts;
	int score;
	PentagoBoardState pbs;
	boolean explored;
	double UCB1;

	public Node(PentagoBoardState pbs) {
		this.attempts = 0;
		this.score = 0;
		this.pbs = pbs;
		this.explored = true;
		this.UCB1 = 0.0;
	}

	public Node(PentagoBoardState pbs, Node parent, Move move) {
		this.attempts = 0;
		this.score = 0;
		this.pbs = pbs;
		this.explored = true;
		this.UCB1 = 0.0;
		this.parent = parent;
		this.parentMove = move;
	}

	public void GenChildren() {
		ArrayList<PentagoMove> moves = pbs.getAllLegalMoves();
		PentagoBoardState temp;
		for (PentagoMove move : moves) {
			temp = (PentagoBoardState) this.pbs.clone();
			temp.processMove(move);
			this.children.add(new Node(temp, this, move));
		}
	}

	public void GetUniqueChildren() {
		ArrayList<Node> temp = new ArrayList<Node>();
		for (Node node : this.children) {
			temp = checkUniquePBS(temp, node);
		}
		this.children = temp;
	}

	public ArrayList<Node> checkUniquePBS(ArrayList<Node> list, Node node) {
		for (Node child : list) {
			if (MyTools.checkBoardsEqual(MyTools.GetBoard(child.pbs), MyTools.GetBoard(node.pbs))) {
				return list;
			}

		}
		list.add(node);
		return list;
	}

	public void UpdateUBC1() {
		if (this.attempts == 0.0) {
			this.UCB1 = Double.MAX_VALUE;
		} else {
			this.UCB1 = (this.score/this.attempts) + 2 * Math.sqrt(Math.log1p((double) (this.parent.attempts / this.attempts)));
		}
	}

	public Move extractBestMove() {
		// TODO Auto-generated method stub
		Move move = null;
		double max = 0;
		int index = 0;
		for (Node node : this.children) {
			if (node.UCB1 > max) {
				max = node.UCB1;
				move = node.parentMove;
			}
		}
		return move;
	}
}
class SortbyUCB1 implements Comparator<Node> 
{ 
    // Used for sorting in ascending order of 
    // roll number 
    public int compare(Node a, Node b) 
    { 
        return (int) (b.UCB1 - a.UCB1); 
    } 
} 
