package Generic;
import java.util.ArrayList;

public abstract class GenericSearchProblem
{
	ArrayList<Node> queue;
	ArrayList<Operator> operators;
	QueuingFunction queuingFunction;
	
	public void initValues(ArrayList<Node> queue, ArrayList<Operator> operators) //, QueuingFunction qf)
	{
		this.queue = queue;
		this.operators = operators;
	}
	
	public abstract Node stateSpace(Node node, Operator operator);
	public abstract boolean goalTest(Node node);
	public abstract int pathCostFunction(Node node);

	/*
	 * expand method used to expand the queue according to the search strategy used
	 * @parameters 
	 * queue : ArrayList of Nodes expanded we have so far
	 * operators: ArrayList of possible operators for expansion
	 * strategy: Enum representing Search strategy used to expand the Node
	 */
	public void expand(ArrayList<Node> queue, ArrayList<Operator> operators, QueuingFunction strategy)
	{
		for(int i = 0; i < operators.size(); ++i)
		{
			switch(strategy)
			{
				case BREADTH_FIRST_SEARCH: breadthFirstSearch(queue, operators); break;
				case DEPTH_FIRST_SEARCH: depthFirstSearch(queue, operators); break;
				case UNIFORM_COST_SEARCH: uniformCostSearch(queue, operators); break;
				case GREEDY_SEARCH: greedySearch(queue, operators); break;
				case ITERATIVE_DEEPENING: depthFirstSearch(queue, operators); break;
				case A_STAR: aStarSearch(queue, operators); break;
			}
		}
	}
	
	/*
	 * Breadth First Search method representing searching by BFS algorithm
	 * @parameters 
	 * queue : ArrayList of Nodes expanded we have so far
	 * operators: ArrayList of possible operators for expansion
	 */
	private void breadthFirstSearch(ArrayList<Node> queue, ArrayList<Operator> operators)
	{
		Node firstNode = queue.remove(0);
		
		for(int i = 0; i < operators.size(); i++)
		{
			Node child = stateSpace(firstNode, operators.get(i));
			if(child != null)
				queue.add(child);
		}
	}
	
	/*
	 * Depth First Search method representing searching by DFS algorithm
	 * @parameters 
	 * queue : ArrayList of Nodes expanded we have so far
	 * operators: ArrayList of possible operators for expansion
	 */
	private void depthFirstSearch(ArrayList<Node> queue, ArrayList<Operator> operators)
	{
		Node firstNode = queue.remove(0);

		for(int i = 0; i < operators.size(); i++)
		{
			Node child = stateSpace(firstNode, operators.get(i));
			if(child != null)
				queue.add(0,child);
		}
	}
	
	/*
	 * Uniform Cost Search method representing searching by UCS algorithm
	 * @parameters 
	 * queue : ArrayList of Nodes expanded we have so far
	 * operators: ArrayList of possible operators for expansion
	 */
	private void uniformCostSearch(ArrayList<Node> queue, ArrayList<Operator> operators)
	{
		Node firstNode = queue.remove(0);
		
		for(int i = 0; i < operators.size(); ++i)
		{
			Node child = stateSpace(firstNode, operators.get(i));
			if(child != null)
			{
				if(child.parent != null)
					//System.out.println(child.operator);
					child.totalCost = child.parent.totalCost;
				child.totalCost += this.pathCostFunction(child);
				queue.add(child);
			}
		}
		queue.sort((o1, o2) -> o1.totalCost < o2.totalCost ? -1 : 1);
	}
	
	/*
	 * Greedy Search method representing searching by greedy search algorithm
	 * @parameters 
	 * queue : ArrayList of Nodes expanded we have so far
	 * operators: ArrayList of possible operators for expansion
	 */
	private void greedySearch(ArrayList<Node> queue, ArrayList<Operator> operators)
	{
		Node firstNode = queue.remove(0);
		
		for(int i = 0; i < operators.size(); ++i)
		{
			Node child = stateSpace(firstNode, operators.get(i));
			if(child != null)
				queue.add(child);
		}
		queue.sort((o1, o2) -> o1.heuristic < o2.heuristic ? -1 : 1);
	}
	
	/*
	 * Uniform Cost Search method representing searching by A* algorithm
	 * @parameters 
	 * queue : ArrayList of Nodes expanded we have so far
	 * operators: ArrayList of possible operators for expansion
	 */
	private void aStarSearch(ArrayList<Node> queue, ArrayList<Operator> operators)
	{
			Node firstNode = queue.remove(0);
			
			for(int i = 0; i < operators.size(); ++i)
			{
				Node child = stateSpace(firstNode, operators.get(i));
				if(child != null)
				{
					if(child.parent != null)
						child.totalCost = child.parent.totalCost;
					child.totalCost += this.pathCostFunction(child);
					queue.add(child);
				}
			}
			queue.sort((o1, o2) -> o1.heuristic + o1.totalCost < o2.heuristic + o2.totalCost ? -1 : 1);
	}
	
	public ResultObject GeneralSearchProcedure(GenericSearchProblem problem, QueuingFunction strategy){
		int iterativeDeepeningLevel = 0;
		Node root = queue.get(0);
		while(true)
		{
			if(queue.size() == 0)
			{
				System.out.println("No solution");
				break;
			}
			if(this.goalTest(queue.get(0)))
				break;
			if(strategy == QueuingFunction.ITERATIVE_DEEPENING)
			{
				if(queue.get(0).depth < iterativeDeepeningLevel)
					expand(this.queue, this.operators, strategy);
				else
				{
					queue.remove(0);
					if(queue.size() == 0)
					{
						queue.add(root);
						++iterativeDeepeningLevel;
					}
				}
			}
			else
				expand(this.queue, this.operators, strategy);
		}
		
		if(queue.size() == 0)
			return new ResultObject(false);

		ResultObject result = new ResultObject(true);
		
		ArrayList<Node> sequence = new ArrayList<Node>();
		
		sequence = this.backtrack(queue.get(0), sequence);
		ArrayList<Operator> operators = new ArrayList<Operator>();
		
		for (int i = 0; i < sequence.size(); i++) {
			operators.add(sequence.get(i).operator);
		}
		
		result.nodes = sequence;
		result.operators = operators;
		result.cost = queue.get(0).totalCost;

		return result;
	}
		
	/*
	 * back track it is a recursion method that returns the sequence of nodes from the root till the goal Node.
	 * @parameters 
	 * node : it is the node which we will get its parent until we reaches the root which is our first node.
	 * sequence: it is the sequence of nodes which is initially empty and is filled by the recursion calls of the method.
	 **/
	public ArrayList<Node> backtrack(Node node, ArrayList<Node> sequence)
	{
		if(node != null)
		{
			sequence.add(0, node);
			this.backtrack(node.parent, sequence);
//			System.out.println(node.totalCost);
		}
		return sequence;
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
