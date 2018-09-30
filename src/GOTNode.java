import java.awt.Point;

public class GOTNode extends Node
{
	Point position;
	Orientation orientation;
	int dragonstones;
	int remainingWW;
	
	public GOTNode
	(
		Node parent,
		Operator operator,
		Point position,
		Orientation orientation, 
		int dragonstones, 
		int remainingWW
	)
	{
		super(parent, operator);
		this.position = position;
		this.orientation = orientation;
		this.dragonstones = dragonstones;
		this.remainingWW = remainingWW;
	}
}
