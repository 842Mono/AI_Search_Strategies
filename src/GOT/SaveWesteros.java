package GOT;
import java.awt.Point;
import java.util.ArrayList;

import Generic.GenericSearchProblem;
import Generic.Node;
import Generic.Operator;
import Generic.QueuingFunction;
import Generic.ResultObject;

public class SaveWesteros extends GenericSearchProblem
{
	public static Cell[][] grid;
	static int m;
	static int n;
	int randomWW;
	int inventory;
	State player;
	int numberOfNodes = 0;
	int randomObstacles;
	
	public SaveWesteros()
	{
		genGridStatic();
		visualizeGrid(grid);
		
		ArrayList<Node> firstState = new ArrayList<Node>();
		
		//Assigns the right bottom cell in the grid for the player
		firstState.add(genPlayer());
		
		ArrayList<Operator> actions = new ArrayList<Operator>();
		actions.add(Action.KILL);
		actions.add(Action.ROTATE_LEFT);
		actions.add(Action.FORWARD);
		actions.add(Action.ROTATE_RIGHT);
		
		//This calls a method in the genericSearchProblem class that initializes values and begins the queue with the player node which is the root.
		this.initValues(firstState, actions);
	}
	
	public static ResultObject search(Cell[][]grid, QueuingFunction strategy, boolean visualize){
		SaveWesteros x = new SaveWesteros();
		grid = SaveWesteros.grid;
		ResultObject result= x.GeneralSearchProcedure(x, strategy);
		result.numberOfNodes = x.numberOfNodes;
		if(visualize){
			System.out.println("VISUALIZED SEQUENCE OF ACTIONS IN GRID:");
			
			for (int i = 0; i < result.nodes.size(); i++) {
				visualizeState(grid, (State)result.nodes.get(i));
			}
			System.out.println();
		}
		
		return result;
	}
	
	public static void visualizeState(Cell[][] grid, Node node)
	{
		State state= null;
		if(node instanceof State)
		{
			state=(State) node;
		}
		
		System.out.println();
		
		if(state.operator == null)
			System.out.println("INITIAL GRID" + " Orientation: " + state.orientation);
		else
			System.out.println("Action Taken: " + state.operator);
			
		System.out.println();
			
		for(int i = 0; i < n; i++)
		{
			for(int j = 0; j < m; j++)
			{
				switch(grid[j][i].type)
				{
					case EMPTY: 
						if(i == state.getY() && j == state.getX())
						{
							System.out.print("[J]");
						}
						else 
						{
							System.out.print("[E]"); 
						}
						break;
					case OBSTACLE: System.out.print("[X]"); break;
					case DRAGON_STONE:
						if(i == state.getY() && j == state.getX())
						{
							System.out.print("[JD]");
						}
						else 
						{
							System.out.print("[D]");
						}
						break;
					case WHITE_WALKER:
						
						if(i == state.getY() && j == state.getX())
						{
							System.out.print("[J]"); break;
						}
						else 
						{
							boolean found = false;
							for(int k = 0; k < state.remainingWW.size(); k++)
							{
								if(state.remainingWW.get(k).y == i && state.remainingWW.get(k).x == j)
								{
									System.out.print("[W]"); found = true;  break;
								}	
							}
							
							if(found == false)
							{
								System.out.print("[E]");
							}
							break;
						}
				}
			}
			System.out.println();
		}
	}

	@Override
	public State stateSpace(Node node, Operator operator)
	{
		//type casting
		State state;
		Action action;
		if(node instanceof State)
			state = (State)node;
		else
			throw new Error("state is not a node");
		if(operator instanceof Action)
			action = (Action)operator;
		else
			throw new Error("action is not an operator");
		
		State resultantState = null;
		Orientation newOrientation;
		
		switch(action)
		{
			case FORWARD:
				switch(state.orientation)
				{
					case NORTH: resultantState = forwardNorth(state); break;
					case SOUTH: resultantState = forwardSouth(state); break;
					case EAST: resultantState = forwardEast(state); break;
					case WEST: resultantState = forwardWest(state); break;
				}
				break;
			case ROTATE_LEFT:
				switch(state.orientation)
				{
					case NORTH: newOrientation = Orientation.WEST; break;
					case WEST: newOrientation = Orientation.SOUTH; break;
					case SOUTH: newOrientation = Orientation.EAST; break;
					case EAST: newOrientation = Orientation.NORTH; break;
					default: throw new Error("unexpected orientation assignment error");
				}
				resultantState = new State
				(
					state,
					Action.ROTATE_LEFT,
					state.getPositionObject(),
					newOrientation,
					state.dragonStones,
					state.remainingWW,
					state.depth+1
				);
				break;
			case ROTATE_RIGHT:
				switch(state.orientation)
				{
					case NORTH: newOrientation = Orientation.EAST; break;
					case EAST: newOrientation = Orientation.SOUTH; break;
					case SOUTH: newOrientation = Orientation.WEST; break;
					case WEST: newOrientation = Orientation.NORTH; break;
					default: throw new Error("unexpected orientation assignment error");
				}
				resultantState = new State
				(
					state,
					Action.ROTATE_RIGHT,
					state.getPositionObject(),
					newOrientation,
					state.dragonStones,
					state.remainingWW,
					state.depth+1
				);
				break;
			case KILL:
				resultantState = kill(state); break;
			default: throw new Error("invalid operator");
		}
		
		if(resultantState != null)
		{
			numberOfNodes++;
			resultantState.heuristic = estimateHeuristic2(resultantState);
		}
		return resultantState;
	}
	
	public State genPlayer() 
	{
		ArrayList<Point> wwPositions = new ArrayList<Point>();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if(grid[i][j].type == CellType.WHITE_WALKER) 
				{
					wwPositions.add(new Point(i,j));
				}
			}
		}
		State s = new State(null, null, new Point(m-1,n-1), Orientation.NORTH, 0, wwPositions, 0);
		s.heuristic = estimateHeuristic2(s);
		return s;
	}
	
	public int estimateHeuristicGreedy(Node node)
	{
		//type casting
		State state = null;
		if(node instanceof State)
			state = (State)node;
		
		if(state.remainingWW.size() == 0)
			return 0;
		
		int posx = state.getX();
		int posy = state.getY();
				
		int heuristic = 0;
		for(int i = 0; i < state.remainingWW.size(); ++i)
		{
			int comparisonHeuristic = 0;
			
			int leastx = state.remainingWW.get(i).x - posx; //if -ve then west
			int leasty = state.remainingWW.get(i).y - posy; //if -ve then north
			if( (leastx < 0 && state.orientation == Orientation.EAST) ||
				(leasty < 0 && state.orientation == Orientation.SOUTH) )
				comparisonHeuristic += 5;
			
			comparisonHeuristic += Math.abs(state.remainingWW.get(i).x - posx) + Math.abs(state.remainingWW.get(i).y - posy);
			
			if(i == 0)
				heuristic = comparisonHeuristic;
			else
				if(comparisonHeuristic < heuristic)
					heuristic = comparisonHeuristic;
		}
		
		heuristic *= 5;
		
		if
		(
			   (state.operator == Action.ROTATE_LEFT || state.operator == Action.ROTATE_RIGHT)
			&& (state.parent != null
			&& (state.parent.operator == Action.ROTATE_LEFT || state.parent.operator == Action.ROTATE_RIGHT))
		)
			heuristic += 6;
		
		return heuristic;
	}
	
	public int estimateHeuristic1(Node node)
	{
		//type casting
		State state = null;
		if(node instanceof State)
			state = (State)node;
		
		if(state.remainingWW.size() == 0)
			return 0;
		
		int posx = state.getX();
		int posy = state.getY();
		
		int heuristic = 0;
		for(int i = 0; i < state.remainingWW.size(); ++i)
		{
			int comparisonHeuristic = 0;
			int leastx = state.remainingWW.get(i).x - posx; //if -ve then west
			int leasty = state.remainingWW.get(i).y - posy; //if -ve then north
			if( (leastx < 0 && state.orientation == Orientation.EAST) ||
				(leasty < 0 && state.orientation == Orientation.SOUTH) )
				comparisonHeuristic += 5;
			
			comparisonHeuristic += Math.abs(state.remainingWW.get(i).x - posx) + Math.abs(state.remainingWW.get(i).y - posy);
			
			if(i == 0)
				heuristic = comparisonHeuristic;
			else
				if(comparisonHeuristic < heuristic)
					heuristic = comparisonHeuristic;
		}
		
		heuristic *= 3;
		
		return heuristic;
	}
			
	public int estimateHeuristic2(Node node)
	{
		//type casting
		State state = null;
		if(node instanceof State)
			state = (State)node;
		
		if(state.remainingWW.size() == 0)
			return 0;
		
		int posx = state.getX();
		int posy = state.getY();
		
		int heuristic1 = 0;
		
		if(state.dragonStones == 0)
		{
			for(int i = 0; i < m; ++i)
			{
				for(int j = 0; j < n; ++j)
				{
					if(grid[i][j].type == CellType.DRAGON_STONE)
					{	
						int leastx = i - posx; //if -ve then west
						int leasty = j - posy; //if -ve then north
						if( (leastx < 0 && state.orientation == Orientation.EAST) ||
								(leasty < 0 && state.orientation == Orientation.SOUTH) )
								heuristic1 += 5;
						heuristic1 += (Math.abs(i - posx) + Math.abs(j - posy))*5;
						break;
					}	
				}
			}
		}
		else
		{
			double leastNumberDragonGlasses = Math.ceil(state.remainingWW.size() / 3.0); 
			heuristic1 = (int) leastNumberDragonGlasses;	
		}
		if
		(
			   (state.operator == Action.ROTATE_LEFT || state.operator == Action.ROTATE_RIGHT)
			&& ((state.parent != null)
			&& (state.parent.operator == Action.ROTATE_LEFT || state.parent.operator == Action.ROTATE_RIGHT))
			&& ((state.parent.parent != null)
			&& (state.parent.parent.operator == Action.ROTATE_LEFT || state.parent.parent.operator == Action.ROTATE_RIGHT))
		)
			heuristic1 += 6;
		return heuristic1;
	}
	
	public State kill(State currentState)
	{
		if(currentState.dragonStones == 0)
			return null;
		else
		{
			ArrayList<Point> killed = currentState.killWhiteWalkers(currentState.getX(), currentState.getY());
			if(killed.size() == currentState.remainingWW.size())
				return null;
			else 
			{
				return new State
				(
					currentState,
					Action.KILL,
					currentState.getPositionObject(),
					currentState.orientation,
					currentState.dragonStones - 1,
					killed,
					currentState.depth+1
				);
			}
		}
	}
	
	public State forwardNorth(State currentState)
	{
		int newMove = currentState.getY() - 1;
		
		if(newMove == -1)
			return null;
		if(grid[currentState.getX()][newMove].type == CellType.OBSTACLE)
			return null;
		if(grid[currentState.getX()][newMove].type == CellType.WHITE_WALKER
			&& currentState.walkerIsAlive(currentState.getX(), newMove))
			return null;
		if(grid[currentState.getX()][newMove].type == CellType.DRAGON_STONE)
			return new State
			(
				currentState,
				Action.FORWARD,
				new Point(currentState.getX(), newMove),
				Orientation.NORTH,
				inventory,
				currentState.remainingWW,
				currentState.depth+1
			);
		
		return new State
		(
			currentState,
			Action.FORWARD,
			new Point(currentState.getX(), newMove),
			Orientation.NORTH,
			currentState.dragonStones,
			currentState.remainingWW,
			currentState.depth+1
		);
	}

	public State forwardSouth(State currentState)
	{
		int newMove = currentState.getY() + 1;
		
		if(newMove == n)
			return null;
		if(grid[currentState.getX()][newMove].type == CellType.OBSTACLE)
			return null;
		if(grid[currentState.getX()][newMove].type == CellType.WHITE_WALKER
			&& currentState.walkerIsAlive(currentState.getX(), newMove))
			return null;
		if(grid[currentState.getX()][newMove].type == CellType.DRAGON_STONE)
			return new State
			(
				currentState,
				Action.FORWARD,
				new Point(currentState.getX(), newMove),
				Orientation.SOUTH,
				inventory,
				currentState.remainingWW,
				currentState.depth+1
			);
		
		return new State
		(
			currentState,
			Action.FORWARD,
			new Point(currentState.getX(), newMove),
			Orientation.SOUTH,
			currentState.dragonStones,
			currentState.remainingWW,
			currentState.depth+1
		);
	}
	
	public State forwardEast(State currentState)
	{
		int newMove = currentState.getX() + 1;
		
		if(newMove == m)
			return null;
		if(grid[newMove][currentState.getY()].type == CellType.OBSTACLE)
			return null;
		if(grid[newMove][currentState.getY()].type == CellType.WHITE_WALKER
				&& currentState.walkerIsAlive(newMove, currentState.getY()))
			return null;
		if(grid[newMove][currentState.getY()].type == CellType.DRAGON_STONE)
			return new State
			(
				currentState,
				Action.FORWARD,
				new Point(newMove, currentState.getY()),
				Orientation.EAST,
				inventory,
				currentState.remainingWW,
				currentState.depth+1
			);
		
		return new State
		(
			currentState,
			Action.FORWARD,
			new Point(newMove, currentState.getY()),
			Orientation.EAST,
			currentState.dragonStones,
			currentState.remainingWW,
			currentState.depth+1
		);
	}
	
	public State forwardWest(State currentState)
	{
		int newMove = currentState.getX() - 1;
		
		if(newMove == -1)
			return null;
		if(grid[newMove][currentState.getY()].type == CellType.OBSTACLE)
			return null;
		if(grid[newMove][currentState.getY()].type == CellType.WHITE_WALKER
				&& currentState.walkerIsAlive(newMove, currentState.getY()))
			return null;
		if(grid[newMove][currentState.getY()].type == CellType.DRAGON_STONE) 
		{
			return new State
			(
				currentState,
				Action.FORWARD,
				new Point(newMove, currentState.getY()),
				Orientation.WEST,
				inventory,
				currentState.remainingWW,
				currentState.depth+1
			);
		}
		
		return new State
		(
			currentState,
			Action.FORWARD,
			new Point(newMove, currentState.getY()),
			Orientation.WEST,
			currentState.dragonStones,
			currentState.remainingWW,
			currentState.depth+1
		);
	}
	
	@Override
	public boolean goalTest(Node node)
	{
		State state = null;
		if(node instanceof State)
			state = (State)node;
		
		if(state.remainingWW.size() == 0)
			return true;
		return false;
	}

	@Override
	public int pathCostFunction(Node node)
	{
		//typecasting
		State state  = null;

		if(node instanceof State) 
			state =  (State) node;
		
		switch((Action)state.operator)
		{
			case KILL:
					return (m*n);	
			case FORWARD: return 5; 
			case ROTATE_LEFT:
				if(state.parent != null && state.parent.operator == Action.ROTATE_RIGHT)
					return m*m*m*m*n*n*n*n;
				if
				(
					((state.parent != null)
					&& (state.parent.operator == Action.ROTATE_LEFT || state.parent.operator == Action.ROTATE_RIGHT))
					&& ((state.parent.parent != null)
					&& (state.parent.parent.operator == Action.ROTATE_LEFT || state.parent.parent.operator == Action.ROTATE_RIGHT))
				)
					return m*n*m*n*m*n*m*n;
				return 5;
			case ROTATE_RIGHT:
				if(state.parent != null && state.parent.operator == Action.ROTATE_LEFT)
					return m*m*m*m*n*n*n*n;
			if
			(
				((state.parent != null)
				&& (state.parent.operator == Action.ROTATE_LEFT || state.parent.operator == Action.ROTATE_RIGHT))
				&& ((state.parent.parent != null)
				&& (state.parent.parent.operator == Action.ROTATE_LEFT || state.parent.parent.operator == Action.ROTATE_RIGHT))
			)
				return m*n*m*n*m*n*m*n;
			return 5;
		}
		return 0;
	}
	
	public void visualizeGrid(Cell[][] g)
	{
		for(int i = 0; i < n; i++)
		{
			for(int j = 0; j < m; j++)
			{
				if(i == n-1 && j == m-1)
				{
					System.out.print("[J]"); break;
				}
				else
				{
				switch(g[j][i].type)
				{
					case EMPTY: System.out.print("[E]"); break;
					case WHITE_WALKER: System.out.print("[W]"); break;
					case OBSTACLE: System.out.print("[X]"); break;
					case DRAGON_STONE: System.out.print("[D]"); break;
				}
			}
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void genGrid()
	{
		m = (int)(Math.random()*4)+4;
		n = (int)(Math.random()*4)+4;
		int WWMaxNumber = (int)((m*n) / 3);
		int ObstaclesMaxNumber = (int)((m*n) / 3);

		inventory = (int)(Math.random()*4)+((m*n)-6);
		System.out.println("Inventory = " + inventory);
		
		grid = new Cell[m][n];
		
		int x = (int)(Math.random()*m);
		int y = (int)(Math.random()*n);

		System.out.println(x + " " + y);
		
		grid[x][y] = new Cell(CellType.DRAGON_STONE);
		
		randomWW = (int)(Math.random()*WWMaxNumber) + 1;
		randomObstacles = (int)(Math.random()*ObstaclesMaxNumber) + 1;

		for (int i = 0; i < randomWW ; i++) 
		{
			x = (int)(Math.random()*m);
			y = (int)(Math.random()*n);
			if(grid[x][y] == null) 
			{
				grid[x][y] = new Cell(CellType.WHITE_WALKER);
			}
			else
			{
				i--;
			}
		}
		
		for (int i = 0; i <randomObstacles ; i++) 
		{
			x = (int)(Math.random()*m);
			y = (int)(Math.random()*n);
			if(grid[x][y] == null) 
			{
				grid[x][y] = new Cell(CellType.OBSTACLE);
			}
			else
			{
				i--;
			}
		}
		
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				if(grid[i][j] == null) {
					grid[i][j] = new Cell(CellType.EMPTY);
				}
				
			}
		}
	}

	public void genGridStatic()
	{
		m = 4;
		n = 4;
		inventory = (int)(Math.random()*4)+30;
		
		grid = new Cell[m][n];
		
		grid[3][2] = new Cell(CellType.DRAGON_STONE);		
		grid[1][1] = new Cell(CellType.WHITE_WALKER);
		grid[1][2] = new Cell(CellType.WHITE_WALKER);
		grid[2][1] = new Cell(CellType.WHITE_WALKER);
		grid[0][0] = new Cell(CellType.OBSTACLE);
		
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if(grid[i][j] == null) {
					grid[i][j] = new Cell(CellType.EMPTY);
				}			
			}
		}
	}
	
	public void genGridStatic2()
	{
		m = 4;
		n = 4;
		inventory = (int)(Math.random()*4)+30;
		
		grid = new Cell[m][n];
		
		grid[2][0] = new Cell(CellType.DRAGON_STONE);
		
		grid[0][3] = new Cell(CellType.WHITE_WALKER);
		grid[2][3] = new Cell(CellType.WHITE_WALKER);
		grid[0][2] = new Cell(CellType.WHITE_WALKER);
		grid[2][2] = new Cell(CellType.WHITE_WALKER);
		
		grid[0][0] = new Cell(CellType.OBSTACLE);
		
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if(grid[i][j] == null) {
					grid[i][j] = new Cell(CellType.EMPTY);
				}			
			}
		}
	}

	public static void main(String[] args) {
		ResultObject result = search(grid, QueuingFunction.BREADTH_FIRST_SEARCH, true);
		System.out.println(result);
	}
}
