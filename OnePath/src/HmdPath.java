import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
/**
 * vxС��Ϸ��һ�ʻ��� Ѱ·
 * @author yinzw
 *
 */
public class HmdPath {

	private int m,n;//m��n��
	private int[][] edge;//�ڽӾ���
	private int e = 0;//����
	private Stack<Integer> path = new Stack<>();//��¼��ǰ·��
	private Stack<Set<Integer>> visited = new Stack<>();//��¼��ǰ·����ÿ���ڵ��Ѿ����ʹ��Ľڵ�
	private int[] dels;//ɾ���ĵ�
	private int nodes;//ͼ�Ķ�����
	
	
	public HmdPath(int m, int n, int ... dels) {
		super();
		this.m = m;
		this.n = n;
		this.edge = new int[m*n][m*n];
		this.dels = dels;
		this.initEdges(dels);
	}
	//��ʼѰ·
	private boolean findPath() {
		if(path.isEmpty()) {
			System.out.println("��ʹ��setStart(int)����������ʼ��");
			return false;
		}else
			return findPath(path.peek());
	}
	//Ѱ·��x�Ѿ���path��
	private boolean findPath(int x) {
		//��·�ж�
		if(isToDeath(x)) {
			backTrack(x);
			//System.out.println("����");
			return findPath(path.peek());
		}
		
		//����ѡ���Ϊ1�Ľڵ㣬���ڶ�Ϊ1�Ľ��Ҫô�Ѿ��ɹ���Ҫô����һ����·
		for(int j = 0; j < m * n; j++) {
			if(j != x && !visited.peek().contains(j) && edge[x][j] == 1 && getDegree(j) == 1) {
				if(path.size() == nodes - 1) {
					//���ýڵ����·����
					path.push(j);
					//ɾ��ǰһ���ڵ����б�
					delEdge(x);
					//������һ���ڵ�ķ��ʼ�¼
					visited.peek().add(j);
					//��ʼ��j�ڵ�ķ��ʼ�¼
					Set<Integer> vis = new HashSet<>();
					visited.push(vis);
					return true;//�ɹ���
				}else {//����һ����·
					//����
					backTrack(x);
					return findPath(path.peek());//
				}
			}
			
		}
		
		//���ѡ���Ϊ2�Ľڵ�
		for(int j = 0; j < m * n; j++) {
			if(j != x && !visited.peek().contains(j) && edge[x][j] == 1 && getDegree(j) == 2) {
				//���ýڵ����·����
				path.push(j);
				//ɾ��ǰһ���ڵ����б�
				delEdge(x);
				//������һ���ڵ�ķ��ʼ�¼
				visited.peek().add(j);
				//��ʼ��j�ڵ�ķ��ʼ�¼
				Set<Integer> vis = new HashSet<>();
				visited.push(vis);
				return findPath(j);
			}
			
		}

		//������һ��û�з��ʹ��Ľڵ�
		for(int i = 0; i < m*n; i++) {
			if(i != x && !visited.peek().contains(i) && edge[x][i] == 1) {
				//���ýڵ����·����
				path.push(i);
				//ɾ��x������б�
				delEdge(x);
				//����x���ķ��ʼ�¼
				visited.peek().add(i);
				//��ʼ��i���ķ��ʼ�¼
				Set<Integer> vis = new HashSet<>();
				visited.push(vis);
				return findPath(i);
			}
		}
		
		//û��·
		if(path.size() == nodes) {
			return true;
		}
		else if(path.size() < nodes) {
			backTrack(x);
			//System.out.println("����");
			return findPath(path.peek());
		}else {
			System.out.println("����֣�û��·��");
			return false;
		}
			
		
	}
	//���ݵ���һ�����
	private boolean backTrack(int x) {
		path.pop();//x��ջ
		visited.pop();//ɾ����Ӧ�ķ��ʼ�¼
		if(path.size() == 0) {
			System.out.println("�ѻ��ݵ�����㣡");
			return false;
		}
		initEdges(dels);//���³�ʼ��ͼ
		//pathջ�г�ջ���ڵ㣬ɾ���������н�����ӵıߣ����ͼ�Ļ���
		for(int i = 0; i < path.size() - 1; i++) {
			delEdge(path.get(i));
		}
		return true;
	}
	//��ȡ�ڵ�Ķ�
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
	
	//������ʼ��
	private void setStart(int s) {
		path.push(s);
		Set<Integer> vis = new HashSet<Integer>();
		visited.push(vis);
		//findPath(s);
	}
	//��ʼ���ڽӾ���
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
	//ɾ�����x���������б�
	private void delEdge(int x) {
		for(int j = 0; j< m * n; j++) {
			if(edge[x][j] == 1) {
				edge[x][j] = 0;
				edge[j][x] = 0;
				e--;
			}
		}
	}
	
	//�жϴӸý������Ƿ�Ϊ��·������չ��
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
	//��ӡͼ���ڽӾ���
	private void printEdges() {
		for(int i = 0; i < m*n; i++) {
			for(int j = 0; j< m * n; j++) {
				System.out.print(edge[i][j] + " ");
			}
			System.out.println();
		}
		
		System.out.println();
		System.out.println("��ǰ����Ϊ��" + e);
		
	}
 	
	private void printPath() {
		System.out.println();
		System.out.println("������£�");
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
		
		System.out.println("һ�ʻ���");
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
		
		HmdPath ybhw = new HmdPath(8,6,7,13,29,31,34);	
		ybhw.setStart(4);
		ybhw.findPath();
		System.out.println("path:length=" + ybhw.path.size());
		ybhw.path.stream().forEach((x) -> System.out.print(x + "->") );
		ybhw.printPath();
		
		

	}

}
