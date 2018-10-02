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
		return operators.toString() + " " + this.cost + " " + this.numberOfNodes;
	}
}
