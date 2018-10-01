package Generic;
import java.awt.Point;

//import Action;

public class Node
{
	Node parent;
	Operator operator; //how this node was reached.
	int totalCost;
	int depth;
	
	public Node() {}
	
	public Node(Node parent, Operator operator)
	{
		this.parent = parent;
		this.operator = operator;
		this.totalCost = 0;
		this.depth = 0;
	}
}
