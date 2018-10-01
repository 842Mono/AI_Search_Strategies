package Generic;
import java.util.ArrayList;

public abstract class GenericSearchProblem
{
	ArrayList<Node> queue;
	ArrayList<Operator> operators;
	QueuingFunction queuingFunction;
	
	public void initValues(ArrayList<Node> queue, ArrayList<Operator> operators, QueuingFunction qf)
	{
		this.queue = queue;
		this.operators = operators;
		this.queuingFunction = qf;
	}
	
	public abstract Node stateSpace(Node node, Operator operator);
	public abstract boolean goalTest(Node node);
	public abstract int pathCostFunction(Node node, Operator operator);
	public ArrayList<Node> expand(ArrayList<Node> queue, ArrayList<Operator> operators, QueuingFunction queuingFunction)
	{
		ArrayList<Node> resultantNode;
		for(int i = 0; i < operators.size(); ++i)
		{
			switch(queuingFunction)
			{
				case BREADTH_FIRST_SEARCH: resultantNode = breadthFirstSearch(queue, operators); break;
				case DEPTH_FIRST_SEARCH: break;
				case UNIFORM_COST_SEARCH: break;
				case GREEDY_SEARCH: break;
				case ITERATIVE_DEEPENING: break;
				case A_STAR: break;
			}
		}
		
		return queue;
	}
	
	private ArrayList<Node> breadthFirstSearch(ArrayList<Node> queue, ArrayList<Operator> operators)
	{
		Node firstNode = queue.remove(0);
		
		for(int i = 0; i < operators.size(); i++)
		{
			Node child = stateSpace(firstNode, operators.get(i));
			if(child != null)
				queue.add(child);
		}
//		for (int i = 0; i < queue.size(); i++)
//		{
//			System.out.println(i);
//		}
		return queue;
	}
	
	public void work() 
	{
		boolean mkhlsnash = true;
		while(mkhlsnash)
		{
			if(this.goalTest(queue.get(0)))
				break;
			expand(this.queue, this.operators, this.queuingFunction);
		}
		this.backtrack(queue.get(0));
	}
	
	public void backtrack(Node node)
	{
		if(node != null)
		{
			System.out.println(node.operator +" operator");
			this.backtrack(node.parent);
		}
	}
	
	public GenericSearchProblem() {}
	
	public GenericSearchProblem
	(
		Node initial,
		ArrayList<Operator> operators
	)
	{
		this.queue = new ArrayList<Node>();
		this.queue.add(initial);
		this.operators = operators;
	}
}
