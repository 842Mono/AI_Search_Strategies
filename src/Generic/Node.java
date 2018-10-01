package Generic;

//import Action;

public class Node
{
	public Node parent;
	Operator operator; //how this node was reached.
	public int totalCost;
	public int depth;
	
	public Node() {}
	
	public Node(Node parent, Operator operator, int cost, int depth)
	{
		this.parent = parent;
		this.operator = operator;
		this.totalCost = cost;
		this.depth = depth;
	}
}
