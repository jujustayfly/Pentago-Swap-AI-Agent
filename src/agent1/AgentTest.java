package agent1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import pentago_swap.PentagoBoardState;

public class AgentTest {

	PentagoBoardState pbs = new PentagoBoardState();
	
	@Test
	public void testGenChildren() {
		Node testRoot = new Node(pbs);
		testRoot.GenChildren();
		testRoot.GetUniqueChildren();
		assertEquals(36,testRoot.children.size());
//		for(Node child : testRoot.children) {
//			child.pbs.printBoard();
//		}
		
	}
}
