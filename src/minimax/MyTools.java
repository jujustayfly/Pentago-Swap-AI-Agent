package minimax;

import java.util.ArrayList;

import boardgame.Board;
import boardgame.Move;
import pentago_swap.PentagoBoardState;

public class MyTools {

	public static void RunMinimax(MinimaxNode node) {
		ArrayList<MinimaxNode> toVisit = new ArrayList<MinimaxNode>();
		node.GenerateChildren();
		MinimaxNode tempNode;
		// initialize DFS
		for (MinimaxNode temp : node.children) {
			toVisit.add(0, temp);
		}
		tempNode=node;tempNode.GenerateChildren();
		while (!(node.visitList.isEmpty())) {
			tempNode = tempNode.visitList.get(0);}
			

		}
	

	private static void PropagateResult(MinimaxNode node) {
		if (node.pbs.getWinner() == Board.NOBODY) {
			
			if(node.parent.nodeType==0) {
				if(node.parent.alpha<node.alpha) {
					node.parent.alpha=node.alpha;
				}
			}
			else {
				if(node.parent.beta<node.beta) {
					node.parent.beta=node.beta;
				}
			}

		} else if (node.pbs.getWinner() == Board.DRAW) {

		} else {
			if (node.parent.nodeType == 0) {
				node.parent.alpha = 1;
				
			} else {
				node.parent.beta = 1;
			}
		}

		node.parent.visitList.remove(node);

	}

	public static Move FindBestMove(PentagoBoardState pbs) {
		MinimaxNode rootNode = new MinimaxNode(pbs, null,null);
		RunMinimax(rootNode);
		for(MinimaxNode node:rootNode.children) {
			if (node.alpha ==1) {
				return node.parentMove;
			}
		}
		System.out.println("returning random move!!!!!");
		return pbs.getRandomMove();

	}
}
