import java.util.*;

public class maze {

    private static final int right = 0;
    private static final int down = 1;
    private static final int left = 2;
    private static final int up = 3;
    private static Random randomGenerator;  // for random numbers
    
    public static int Size;    
    public static int[] Up;
    public static int[] height;
    public static int[] pretty;
    public static int[] good = new int[]{left,up,right,down};
    public static Stack<Integer> p = new Stack<Integer>();
   
    public static class Point {  // a Point is a position in the maze

        public int x, y;
        
        // Constructor
        public Point(int x, int y) {
            this.x = x;
	       this.y = y;
        }

        public void copy(Point p) {
            this.x = p.x;
            this.y = p.y;
        }
    }
    
    public static class Edge { 
	   // an Edge links two neighboring Points: 
	   // For the grid graph, an edge can be represented by a point and a direction.
	   Point point;
	   int direction;    // one of right, down, left, up
	   boolean used;     // for maze creation
	   boolean deleted;  // for maze creation
	   boolean visited;  
	
	   // Constructor
	   public Edge(Point p, int d) {
           this.point = p;
	       this.direction = d;
	       this.used = false;
	       this.deleted = false;
       }
    }

    // A board is an SizexSize array whose values are Points                                                                                                           
    public static Point[][] board;
    
    // A graph is simply a set of edges: graph[i][d] is the edge 
    // where i is the index for a Point and d is the direction 
    public static Edge[][] graph;
    public static int N;   // number of points in the graph
    

    public static void R_Union(int i, int j){
	int ri = height[i];
	int rj = height[j];
	if (ri < rj) {
		Up[i] = j;
	} if (ri > rj){
		Up[j] = i;
	} else { // ri == rj
		height[j]++; 
		Up[j] = i;
	}
    }
   
    public static int PC_Find(int i) {
	int r = i;
	while (Up[r] != -1){
		r = Up[r];
	}
	if (i != r){
		int k = Up[i];
	while(k != r){
		Up[i] = r;
		i = k;
		k = Up[k];
		}
	}
	return r;
    }

    public static boolean all_used(int sexy){
	for(int i=0;i<4;i++) if (!graph[sexy][i].used) return false;
	return true;
    }

    public static void CreateMaze(){
	int u;
	int v;
	randomGenerator = new Random();
	while(get_size() > 1){
		int i = randomGenerator.nextInt(N);
		while (all_used(i)) i = randomGenerator.nextInt(N);
		int j = randomGenerator.nextInt(4);
		while (graph[i][j].used) j = randomGenerator.nextInt(4);
	        u = PC_Find(i);
		v = PC_Find(i+pretty[j]);
		if(u!=v){R_Union(u,v);graph[i][j].deleted=true;graph[i+pretty[j]][good[j]].deleted=true;}
		else{graph[i][j].used=true;}
	}
    }	

    public static int get_size(){
	int num = 0;
	for (int i = 0; i < Size*Size; i++){
		if (Up[i] == -1) num++;		 
	}
	return num;
    }	

    public static Stack<Integer> findPath(){
	int destination = Size*Size-1;
	int curr;
	Edge next;
	boolean dead_end;
	p.push(0);
	curr = p.peek(); 
	while(curr!=destination){
	    curr = p.peek();
	    dead_end = true;
	    for(int i=0;i<4;i++){
		next = graph[curr][i];
		if (next.deleted && !next.visited){
		    p.push(curr+pretty[i]);
		    graph[curr][i].visited=true;
		    dead_end = false;
		    break;
		}
	    } 
	    if (p.peek()==destination) break;
	    if (dead_end) p.pop(); 
        }
	return p;
    }
    
    public static void displayBoard() {
	String s;
	String me;
        for (int i = 0; i < Size; ++i) {
            System.out.print("    -");
            for (int j = 0; j < Size; ++j) {
		s = (graph[i*Size+j][up].deleted) ? "   -" : "----";
		System.out.print(s);
	    }
            System.out.println();
            if (i == 0) System.out.print("Start");
            else System.out.print("    |");
            for (int j = 0; j < Size; ++j) {
		me = p.contains(i*Size+j) ? "X" : " ";
                if (i == Size-1 && j == Size-1)
		    System.out.print("  "+me+" End");
                else{
		    s = (graph[i*Size+j][right].deleted) ? "  "+me+" " : "  "+me+"|";
		    System.out.print(s);
		}
            }
            System.out.println();
        }
        System.out.print("    -");
        for (int j = 0; j < Size; ++j) System.out.print("----");
        System.out.println();
    }
    
    public static void main(String[] args) {
         
    	// Read in the Size of a maze
	    Scanner scan = new Scanner(System.in);         
	    try {	     
	        System.out.println("What's the size of your maze? ");
	        Size = scan.nextInt();
	        
		Up = new int[Size * Size];
		height = new int[Size * Size];
		pretty = new int[]{1,Size,-1,-Size};
		for(int i = 0; i < Size * Size; i++) Up[i] = -1;
	    }
	    catch(Exception ex){
	        ex.printStackTrace();
	    }
	    scan.close();
	    
         
	    // Create one dummy edge for all boundary edges.
	    Edge dummy = new Edge(new Point(0, 0), 0);
	    dummy.used = true;
	    	     
	    // Create board and graph.
	    board = new Point[Size][Size];
	    N = Size*Size;  // number of points
	    graph = new Edge[N][4];         
	     
	    for (int i = 0; i < Size; ++i) 
		  for (int j = 0; j < Size; ++j) {
		    Point p = new Point(i, j);
		    int pindex = i*Size+j;   // Point(i, j)'s index is i*Size + j
		     
		    board[i][j] = p;
		     
		    graph[pindex][right] = (j < Size-1)? new Edge(p, right): dummy;
		    graph[pindex][down] = (i < Size-1)? new Edge(p, down) : dummy;        
		    graph[pindex][left] = (j > 0)? graph[pindex-1][right] : dummy;         
		    graph[pindex][up] = (i > 0)? graph[pindex-Size][down] : dummy;

		}
	    CreateMaze();
	    //displayBoard();
	    p = findPath();
	    displayBoard();
    }
}

