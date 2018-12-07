import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
/**
 * vx小游戏：一笔画完 寻路
 * @author yinzw
 *
 */
public class HmdPath {

	private int m,n;//m行n列
	private int[][] edge;//邻接矩阵
	private int e = 0;//边数
	private Stack<Integer> path = new Stack<>();//记录当前路径
	private Stack<Set<Integer>> visited = new Stack<>();//记录当前路径中每个节点已经访问过的节点
	private int[] dels;//删除的点
	private int nodes;//图的顶点数
	
	
	public HmdPath(int m, int n, int ... dels) {
		super();
		this.m = m;
		this.n = n;
		this.edge = new int[m*n][m*n];
		this.dels = dels;
		this.initEdges(dels);
	}
	//开始寻路
	private boolean findPath() {
		if(path.isEmpty()) {
			System.out.println("请使用setStart(int)方法设置起始点");
			return false;
		}else
			return findPath(path.peek());
	}
	//寻路，x已经在path中
	private boolean findPath(int x) {
		//死路判断
		if(isToDeath(x)) {
			backTrack(x);
			//System.out.println("回溯");
			return findPath(path.peek());
		}
		
		//优先选择度为1的节点，存在度为1的结点要么已经成功，要么这是一条死路
		for(int j = 0; j < m * n; j++) {
			if(j != x && !visited.peek().contains(j) && edge[x][j] == 1 && getDegree(j) == 1) {
				if(path.size() == nodes - 1) {
					//将该节点加入路径中
					path.push(j);
					//删除前一个节点所有边
					delEdge(x);
					//更新上一个节点的访问记录
					visited.peek().add(j);
					//初始化j节点的访问记录
					Set<Integer> vis = new HashSet<>();
					visited.push(vis);
					return true;//成功！
				}else {//这是一条死路
					//回溯
					backTrack(x);
					return findPath(path.peek());//
				}
			}
			
		}
		
		//其次选择度为2的节点
		for(int j = 0; j < m * n; j++) {
			if(j != x && !visited.peek().contains(j) && edge[x][j] == 1 && getDegree(j) == 2) {
				//将该节点加入路径中
				path.push(j);
				//删除前一个节点所有边
				delEdge(x);
				//更新上一个节点的访问记录
				visited.peek().add(j);
				//初始化j节点的访问记录
				Set<Integer> vis = new HashSet<>();
				visited.push(vis);
				return findPath(j);
			}
			
		}

		//任意找一个没有访问过的节点
		for(int i = 0; i < m*n; i++) {
			if(i != x && !visited.peek().contains(i) && edge[x][i] == 1) {
				//将该节点加入路径中
				path.push(i);
				//删除x结点所有边
				delEdge(x);
				//更新x结点的访问记录
				visited.peek().add(i);
				//初始化i结点的访问记录
				Set<Integer> vis = new HashSet<>();
				visited.push(vis);
				return findPath(i);
			}
		}
		
		//没有路
		if(path.size() == nodes) {
			return true;
		}
		else if(path.size() < nodes) {
			backTrack(x);
			//System.out.println("回溯");
			return findPath(path.peek());
		}else {
			System.out.println("很奇怪，没有路了");
			return false;
		}
			
		
	}
	//回溯到上一个结点
	private boolean backTrack(int x) {
		path.pop();//x出栈
		visited.pop();//删除对应的访问记录
		if(path.size() == 0) {
			System.out.println("已回溯到根结点！");
			return false;
		}
		initEdges(dels);//重新初始化图
		//path栈中除栈顶节点，删除其他所有结点连接的边，完成图的回溯
		for(int i = 0; i < path.size() - 1; i++) {
			delEdge(path.get(i));
		}
		return true;
	}
	//获取节点的度
	private int getDegree(int x) {
		int degree = 0;

		for(int j = 0; j < m * n; j++) {
			//System.out.println("edge[" + x + "][" + j + "] =" + edge[x][j]);
			if(edge[x][j] == 1 && j != x) {
				++degree;
			}
		}	
		
		return degree;
	}
	
	private void initEdges() {
		nodes = m*n;
		e = 0;
		for(int i = 0; i < m*n; i++) {
			for(int j = 0; j < m*n; j++) {
				if(Math.abs(i - j) == 1 && Math.max(i, j)%n != 0 || Math.abs(i - j) == n) {
					edge[i][j] = 1;
					//System.out.println(i + "," + j + "  ");
					if(i > j) {
						e++;
					}
				}else
					edge[i][j] = 0;
			}
			
		}
	}
	
	//设置起始点
	private void setStart(int s) {
		path.push(s);
		Set<Integer> vis = new HashSet<Integer>();
		visited.push(vis);
		//findPath(s);
	}
	//初始化邻接矩阵
	private void initEdges(int ... args) {
		dels = args;
		initEdges();
		if(args != null) {
			for(int i = 0; i < args.length; i++) {
				delEdge(args[i]);	
			}
		}
		nodes = nodes - args.length;
	}
	//删除结点x相连的所有边
	private void delEdge(int x) {
		for(int j = 0; j< m * n; j++) {
			if(edge[x][j] == 1) {
				edge[x][j] = 0;
				edge[j][x] = 0;
				e--;
			}
		}
	}
	
	//判断从该结点出发是否为死路（可扩展）
	private boolean isToDeath(int x) {
		int deg2 = 0;
		for(int j = 0; j < m * n; j++) {
			if(j != x 
					&& !visited.peek().contains(j) 
					&& edge[x][j] == 1 
					&& getDegree(j) == 2) {
				deg2++;
			}
		}
		if(deg2 > 1) {
			return true;
		}else {
			return false;
		}
		
		
	}
	//打印图的邻接矩阵
	private void printEdges() {
		for(int i = 0; i < m*n; i++) {
			for(int j = 0; j< m * n; j++) {
				System.out.print(edge[i][j] + " ");
			}
			System.out.println();
		}
		
		System.out.println();
		System.out.println("当前边数为：" + e);
		
	}
 	
	private void printPath() {
		System.out.println();
		System.out.println("编号如下：");
		for(int i = 0; i < m; i++) {
			for(int j = 0; j < n; j++) {
				int num = i * n + j;
				if(num < 10) {
					System.out.print(" " + num+ " ");
					
				}else
					System.out.print(num + " ");
			}
			System.out.println();
		}
		System.out.println();
		
		System.out.println("一笔画：");
		for(int i = 0; i < m; i++) {
			for(int j = 0; j < n; j++) {
				int num = i * n + j;
				
				for(int t = 0; t < path.size();t++) {
					if(num == path.get(t)) {
						if(t < 10) {
							System.out.print(" " + t+ " ");
							
						}else
							System.out.print(t + " ");
						break;
					}else if(t < path.size() - 1) {
						continue;
					}
					System.out.print("   ");
				}
			}
			System.out.println();
				
		}
		System.out.println();
	}
	
	public static void main(String[] args) {
		
		HmdPath ybhw = new HmdPath(8,6,2,7,10,26,29);	
		ybhw.setStart(37);
		ybhw.findPath();
		System.out.println("path:length=" + ybhw.path.size());
		ybhw.path.stream().forEach((x) -> System.out.print(x + "->") );
		ybhw.printPath();
		
		

	}

}
