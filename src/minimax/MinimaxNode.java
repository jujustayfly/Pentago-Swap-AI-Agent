package minimax;

import java.util.ArrayList;

import boardgame.Board;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

public class MinimaxNode {

	MinimaxNode parent;
	PentagoBoardState pbs;
	ArrayList<MinimaxNode> children = new ArrayList<MinimaxNode>();
	ArrayList<MinimaxNode> visitList = new ArrayList<MinimaxNode>();
	PentagoMove parentMove;
	int nodeType;
	int alpha;
	int beta;
	
	
	public MinimaxNode(PentagoBoardState pbs,MinimaxNode parent,PentagoMove move) {
		this.parent = parent;
		this.pbs=pbs;
		alpha=0;
		beta = 0;
		parentMove=move;
		if(parent == null) {
			this.nodeType = 0; // 0 for max node
		}
		else if (parent.nodeType==0) {
			this.nodeType = 1;// 1 for min node
		}
		else {
			this.nodeType = 0; 
		}
	}
	
	public void GenerateChildren() {
		if(pbs.getWinner()==Board.NOBODY) {
		PentagoBoardState temp;
		MinimaxNode child;
		for(PentagoMove move : pbs.getAllLegalMoves()) {
			temp = (PentagoBoardState) pbs.clone();
			temp.processMove(move);
			child = new MinimaxNode((PentagoBoardState) temp.clone(),this,move);
			children.add(child);
			visitList.add(child);
		}}
		
	}
}
