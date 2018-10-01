package GOT;
import java.awt.Point;
import java.util.ArrayList;

import Generic.GenericSearchProblem;
import Generic.Node;
import Generic.Operator;
import Generic.QueuingFunction;

public class SaveWesteros extends GenericSearchProblem
{
	public static Cell[][] grid;
	public static int m;
	public static int n;
	public static int inventory;
	public static State player;
	
	public static void main(String [] args)
	{
//		genGrid();
//		genPlayer();
//		visualizeGrid(grid);
	}
	
	
	public SaveWesteros()
	{
//		genGrid();
//		genPlayer();
//		visualizeGrid(grid);
		genGridStatic();
		visualizeGrid(grid);
		
		ArrayList<Node> firstState = new ArrayList<Node>();
		firstState.add(genPlayer());
		ArrayList<Operator> actions = new ArrayList<Operator>();
		actions.add(Action.FORWARD);
		actions.add(Action.ROTATE_LEFT);
		actions.add(Action.KILL);
		actions.add(Action.ROTATE_RIGHT);
		this.initValues(firstState, actions, QueuingFunction.BREADTH_FIRST_SEARCH);
		
		this.work();
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
					state.remainingWW
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
					state.remainingWW
				);
				
				break;
			case KILL: resultantState = kill(state); break;
			default: throw new Error("offf");
		}
		
		return resultantState;
	}
	
	public State kill(State currentState)
	{
		System.out.println("gh hna");
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
					killed
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
				currentState.remainingWW
			);
		
		return new State
		(
			currentState,
			Action.FORWARD,
			new Point(currentState.getX(), newMove),
			Orientation.NORTH,
			currentState.dragonStones,
			currentState.remainingWW
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
				currentState.remainingWW
			);
		
		return new State
		(
			currentState,
			Action.FORWARD,
			new Point(currentState.getX(), newMove),
			Orientation.SOUTH,
			currentState.dragonStones,
			currentState.remainingWW
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
				currentState.remainingWW
			);
		
		return new State
		(
			currentState,
			Action.FORWARD,
			new Point(newMove, currentState.getY()),
			Orientation.EAST,
			currentState.dragonStones,
			currentState.remainingWW
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
			return new State
			(
				currentState,
				Action.FORWARD,
				new Point(newMove, currentState.getY()),
				Orientation.WEST,
				inventory,
				currentState.remainingWW
			);
		
		return new State
		(
			currentState,
			Action.FORWARD,
			new Point(newMove, currentState.getY()),
			Orientation.WEST,
			currentState.dragonStones,
			currentState.remainingWW
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
	
//	@Override
//	public ArrayList<Node> expand(Node node)
//	{
//		ArrayList<Node> results =  new ArrayList<Node>();
//		State forwardState = stateSpace(node, Action.FORWARD);
//		State rotateLeftState = stateSpace(node, Action.ROTATE_LEFT);
//		State rotateRightState =  stateSpace(node, Action.ROTATE_RIGHT);
//		State killState =  stateSpace(node, Action.KILL);
//		
//		results.add(killState);
//		results.add(forwardState);
//		results.add(rotateLeftState);
//		results.add(rotateRightState);
//		
//		
//		return null;
//	}
	@Override
	public int pathCostFunction(Node node, Operator operator)
	{
		return 0;
	}
	
	public static void visualizeGrid(Cell[][] g)
	{
		for(int i = 0; i <m; i++)
		{
			for(int j = 0; j < n; j++)
			{
				//System.out.println(g[i][j].type);
				switch(g[i][j].type)
				{
					case EMPTY: System.out.print("[O]"); break;
					case WHITE_WALKER: System.out.print("[W]"); break;
					case OBSTACLE: System.out.print("[X]"); break;
					case DRAGON_STONE: System.out.print("[D]"); break;
//					case PLAYER: System.out.print("[P]"); break;
					//default:System.out.println("gh hna");break;
				}
			}
			System.out.println();
		}
	}
	
	public static void genGrid()
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

	public static State genPlayer() 
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
		return new State(null,null, new Point(m-1,n-1), Orientation.NORTH,0,wwPositions);
	}
	

	public static void genGridStatic()
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
