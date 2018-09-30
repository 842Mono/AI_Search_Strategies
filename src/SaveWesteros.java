import java.util.ArrayList;

public class SaveWesteros extends GenericSearchProblem
{	
	static Cell[][] grid;
	
	public static void main(String [] args)
	{
		genGrid();
		visualizeGrid(grid);
	}
	
	public SaveWesteros(Node initial, ArrayList<Operator> operators)
	{
		super(initial, operators);
		//Node initial = new GOTNode(null, null, );
	}

	@Override
	public Node stateSpace(Node node, Operator operator)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean goalTest(Node node)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int pathCostFunction(Node node, Operator operator)
	{
		return 0;
	}
	
	public static void visualizeGrid(Cell[][] g)
	{
		for(int i = 0; i < g.length; i++)
		{
			for(int j = 0; j < g[i].length; j++)
			{
				//System.out.println(g[i][j].type);
				switch(g[i][j].type)
				{
					case EMPTY: System.out.print("[E]"); break;
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
		int m = 4;
		int n = 4;
		int WWMaxNumber = 4;
		int ObstaclesMaxNumber = 4;
//		int random = (int)(Math.random()*7)+4;
		
		grid = new Cell[m][n];
		
		//grid[m-1][n-1] = new Cell(CellType.PLAYER);
		
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
}
