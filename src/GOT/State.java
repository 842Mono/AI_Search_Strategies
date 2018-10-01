package GOT;
import java.awt.Point;
import java.util.ArrayList;

import Generic.Node;
import Generic.Operator;

public class State extends Node
{
	private Point position; //the player's position
	public Orientation orientation;
	public int dragonStones;
	public ArrayList<Point> remainingWW;
	
	public State() {}
	
	public State
	(
		State parent,
		Action operator,
		Point position,
		Orientation orientation,
		int dragonStones,
		ArrayList<Point> remainingWW
//		int pathCost
	)
	{
		super(parent, operator, 0, 0);
		this.position = position;
		this.orientation = orientation;
		this.dragonStones = dragonStones;
		this.remainingWW = remainingWW;
	}
	
	public int getX()
	{
		return (int)position.getX();
	}
	
	public int getY()
	{
		return (int)position.getY();
	}
	
	public Point getPositionObject()
	{
		return this.position;
	}
	
	public boolean walkerIsAlive(int x, int y)
	{
		for(int i = 0; i < remainingWW.size(); ++i)
		{
			Point currentPoint = remainingWW.get(i);
			if((int)currentPoint.getX() == x && (int)currentPoint.getY() == y)
				return true;
		}
		return false;
	}
	
	public ArrayList<Point> killWhiteWalkers(int x, int y)
	{
		ArrayList<Point> newArrayList = (ArrayList<Point>)remainingWW.clone();
		
		for(int i = 0; i < newArrayList.size(); ++i)
		{
			Point currentPoint = newArrayList.get(i);
			if
			(
				((int)currentPoint.getX() == x - 1 && (int)currentPoint.getY() == y) ||
				((int)currentPoint.getX() == x + 1 && (int)currentPoint.getY() == y) ||
				((int)currentPoint.getX() == x && (int)currentPoint.getY() == y - 1) ||
				((int)currentPoint.getX() == x && (int)currentPoint.getY() == y + 1)
			)
				newArrayList.remove(i);
		}
		
		return newArrayList;
	}
}
