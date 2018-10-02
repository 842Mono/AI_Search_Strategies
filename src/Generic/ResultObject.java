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
		ArrayList<Operator> result = new ArrayList<Operator>();
		for(int i =1; i<operators.size(); i++) {
	       result.add(operators.get(i));
	    }
		return result.toString() + " " + this.cost + " " + this.numberOfNodes;
	}
}
