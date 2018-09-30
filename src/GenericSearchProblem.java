import java.util.ArrayList;

public abstract class GenericSearchProblem
{
	ArrayList<Node> queue;
	ArrayList<Operator> operators;
	
	public abstract Node stateSpace(Node node, Operator operator);
	public abstract boolean goalTest(Node node);
	public abstract int pathCostFunction(Node node, Operator operator);
	
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
