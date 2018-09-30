import java.awt.Point;

//import Action;

public class Node
{
	Node parent;
	Point position;
	Action operator;
	Orientation orientation;
	int dragonstones;
	int remainingWW;
	
	public Node
	(
		Node parent, 
		Point position,
		Action operator, 
		Orientation orientation, 
		int dragonstones, 
		int remainingWW
	)
	{
		this.parent = parent;
		this.position = position;
		this.operator = operator;
		this.orientation = orientation;
		this.dragonstones = dragonstones;
		this.remainingWW = remainingWW;
	}
}
