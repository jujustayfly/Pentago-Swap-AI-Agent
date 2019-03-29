package agent1;

import java.util.ArrayList;

import boardgame.Move;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

public class Node {
	ArrayList<Node> children = new ArrayList<Node>();
	Node parent;
	int attempts;
	int score;
	PentagoBoardState pbs;

	public Node(PentagoBoardState pbs) {
		this.attempts = 0;
		this.score = 0;
		this.pbs = pbs;
	}
	
	public void GenChildren() {
		ArrayList<PentagoMove> moves = pbs.getAllLegalMoves();
		PentagoBoardState temp;
		for(PentagoMove move : moves) {
			temp = (PentagoBoardState) this.pbs.clone();
			temp.processMove(move);
			this.children.add(new Node(temp));
		}
	}
	
  public void GetUniqueChildren() {
	  ArrayList<Node> temp = new ArrayList<Node>();
	  for(Node node :this.children) {
		  temp=checkUniquePBS(temp,node);
	  }
	  this.children=temp;
  }

  public ArrayList<Node> checkUniquePBS(ArrayList<Node> list,Node node){
	  for(Node child: list) {
		  if (MyTools.checkBoardsEqual(MyTools.GetBoard(child.pbs), MyTools.GetBoard(node.pbs))) {
			  return list;
		  }
		  
		}
		list.add(node);
		return list;
  }
}
