package GOT;
import java.awt.Point;
import java.rmi.dgc.Lease;
import java.util.ArrayList;

import Generic.GenericSearchProblem;
import Generic.Node;
import Generic.Operator;
import Generic.QueuingFunction;

public class SaveWesteros extends GenericSearchProblem
{
	Cell[][] grid;
	int m;
	int n;
	int inventory;
	State player;
	
	public SaveWesteros()
	{
		genGrid();
		visualizeGrid(grid);
		
		ArrayList<Node> firstState = new ArrayList<Node>();
		firstState.add(genPlayer());
		ArrayList<Operator> actions = new ArrayList<Operator>();
		actions.add(Action.KILL);
		actions.add(Action.ROTATE_LEFT);
		actions.add(Action.FORWARD);
		actions.add(Action.ROTATE_RIGHT);
		this.initValues(firstState, actions);
		
		//this.search(grid, QueuingFunction.BREADTH_FIRST_SEARCH, true);
		this.search(QueuingFunction.A_STAR, true);
	}
	
	public SaveWesteros(Node initial, ArrayList<Operator> operators)
	{
		super(initial, operators);
		//Node initial = new GOTNode(null, null, );
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
		
		//switch
		
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
			default: throw new Error("offf");
		}
		
		if(resultantState != null)
			resultantState.heuristic = estimateHeuristic1(resultantState);
		
		return resultantState;
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
		
//		ArrayList<Point> threeKills = new ArrayList<Point>();
//		ArrayList<Point> twoKills = new ArrayList<Point>();
//		ArrayList<Point> oneKill = new ArrayList<Point>();
		
		int heuristic = 0;
		for(int i = 0; i < state.remainingWW.size(); ++i)
		{
			int comparisonHeuristic = 0;
			int leastx = Math.abs(state.remainingWW.get(i).x - posx); //if -ve then west
			int leasty = Math.abs(state.remainingWW.get(i).y - posy); //if -ve then north
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
	
//	@Override
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
		
//		ArrayList<Point> threeKills = new ArrayList<Point>();
//		ArrayList<Point> twoKills = new ArrayList<Point>();
//		ArrayList<Point> oneKill = new ArrayList<Point>();
		
		int heuristic = 0;
		for(int i = 0; i < state.remainingWW.size(); ++i)
		{
			int comparisonHeuristic = 0;
			int leastx = Math.abs(state.remainingWW.get(i).x - posx); //if -ve then west
			int leasty = Math.abs(state.remainingWW.get(i).y - posy); //if -ve then north
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
		
		
//		if
//		(
//			   (state.operator == Action.ROTATE_LEFT || state.operator == Action.ROTATE_RIGHT)
//			&& (state.parent != null
//			&& (state.parent.operator == Action.ROTATE_LEFT || state.parent.operator == Action.ROTATE_RIGHT))
//			&& (state.parent.parent != null
//			&& (state.parent.parent.operator == Action.ROTATE_LEFT || state.parent.parent.operator == Action.ROTATE_RIGHT))
//		)
//			heuristic += 25;
		
//		if
//		(
//			(state.operator == Action.ROTATE_LEFT && state.parent != null && state.parent.operator == Action.ROTATE_RIGHT)
//			|| (state.operator == Action.ROTATE_RIGHT && state.parent != null && state.parent.operator == Action.ROTATE_LEFT)
//		)
//			heuristic += 100;
//		if
//		(
//			(state.operator == Action.ROTATE_LEFT && state.parent != null && state.parent.operator == Action.ROTATE_LEFT && state.parent.parent != null && state.parent.parent.operator == Action.ROTATE_LEFT)
//			|| (state.operator == Action.ROTATE_RIGHT && state.parent != null && state.parent.operator == Action.ROTATE_RIGHT && state.parent.parent != null && state.parent.parent.operator == Action.ROTATE_RIGHT)
//		)
//			heuristic += 100;
		
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
						int leastx = Math.abs(i - posx); //if -ve then west
						int leasty = Math.abs(j - posy); //if -ve then north
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
				int cost = currentState.remainingWW.size() - killed.size();
				switch(cost)
				{
					case 1: cost = 4; break;
					case 2: cost = 2; break;
					case 3: cost = 1; break;
				}
				return new State
				(
					currentState,
					Action.KILL,
					currentState.getPositionObject(),
					currentState.orientation,
					currentState.dragonStones - 1,
					killed,
					currentState.depth+1
//					currentState.totalCost + cost
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
//				currentState.totalCost
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
//			currentState.totalCost
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
//				currentState.totalCost
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
//			currentState.totalCost
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
//				currentState.totalCost
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
//			currentState.totalCost
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
//				currentState.totalCost
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
//			currentState.totalCost,
			currentState.depth+1
		);
	}
	
	@Override
	public boolean goalTest(Node node)
	{
		State state = null;
		if(node instanceof State)
			state = (State)node;
		//System.out.println(state.remainingWW.size());
		if(state.remainingWW.size() == 0)
			return true;
		return false;
	}

	@Override
	public int pathCostFunction(Node node, Operator operator)
	{
		//typecasting
		Action action = null;
		State state  = null;
		State parent = null;
		if(operator instanceof Action) 
			action = (Action) operator;
		if(node instanceof State) 
			state =  (State) node;
		if(node.parent instanceof State)
			parent = (State) node.parent;
		
		switch(action)
		{
			case KILL: 
				if(parent.remainingWW.size() - state.remainingWW.size() == 1)
					return 4;
				if(parent.remainingWW.size() - state.remainingWW.size() == 2)
					return 2;
				if(parent.remainingWW.size() - state.remainingWW.size() == 3)
					return 1;			
				break;
			case FORWARD: return 5; 
			case ROTATE_LEFT: return 5; 
			case ROTATE_RIGHT: return 5;
		}
		return 0;
	}
	
	public void visualizeGrid(Cell[][] g)
	{
		for(int i = 0; i < m; i++)
		{
			for(int j = 0; j < n; j++)
			{
				switch(g[j][i].type)
				{
					case EMPTY: System.out.print("[O]"); break;
					case WHITE_WALKER: System.out.print("[W]"); break;
					case OBSTACLE: System.out.print("[X]"); break;
					case DRAGON_STONE: System.out.print("[D]"); break;
				}
			}
			System.out.println();
		}
	}
	
	public void genGrid()
	{
		m = 4;
		n = 4;
		int WWMaxNumber = 4;
		int ObstaclesMaxNumber = 4;
//		int random = (int)(Math.random()*7)+4;
		
		inventory = (int)(Math.random()*4)+3;
		
		grid = new Cell[m][n];
		
		
		int dsl = (int)(Math.random()*(m*n - 1));
		int x = dsl/m;
		int y = dsl%n;
		System.out.println(x+""+y);
		
		grid[x][y] = new Cell(CellType.DRAGON_STONE);
		System.out.println(grid[x][y].type);
		
		int randomWW = (int)(Math.random()*WWMaxNumber) + 1;
		int randomObstacles = (int)(Math.random()*ObstaclesMaxNumber) + 1;

		for (int i = 0; i <randomWW ; i++) 
		{
			dsl = (int)(Math.random()*(m*n - 1));
			x = dsl/m;
			y = dsl%n;
			if(grid[x][y] == null) 
			{
				grid[x][y] = new Cell(CellType.WHITE_WALKER);
			}else
			{
				i--;
			}
		}
		
		for (int i = 0; i <randomObstacles ; i++) 
		{
			dsl = (int)(Math.random()*(m*n - 1));
			x = dsl/m;
			y = dsl%n;
			if(grid[x][y] == null) 
			{
				grid[x][y] = new Cell(CellType.OBSTACLE);
			}else
			{
				i--;
			}
		}
		
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if(grid[i][j] == null) {
					grid[i][j] = new Cell(CellType.EMPTY);
				}
				
			}
		}
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
		State s = new State(null,null, new Point(m-1,n-1), Orientation.NORTH,0,wwPositions,0);
		s.heuristic = estimateHeuristicGreedy(s);
		return s;
	}
	

	public void genGridStatic()
	{
		m = 4;
		n = 4;
		inventory = (int)(Math.random()*4)+3;
		
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

}
