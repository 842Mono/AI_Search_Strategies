import java.awt.Point;

//import Action;

public class Node
{
	Node parent;
	Operator operator; //how this node was reached.
	int totalCost = 0;
	
	public Node(Node parent, Operator operator)
	{
		this.parent = parent;
		this.operator = operator;
	}
}
