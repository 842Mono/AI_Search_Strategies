package Generic;

import java.util.ArrayList;

public class ResultObject
{
	boolean solution;
	public ArrayList<Operator> operators;
	public int cost;
	public int numberOfNodes;
	
	public ResultObject(boolean solution) {this.solution = solution;}
	
	@Override
	public String toString()
	{
		operators.remove(0);
		return "Listed Sequence Of Actions: " + operators.toString() + "\n" +
			   "Total Cost: " + this.cost + "\n" +
			   "Total Number of Nodes Expanded: " + this.numberOfNodes;
	}
}
