package hw2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class homework 
{
	static int Max = Integer.MAX_VALUE;
	static int Min = Integer.MIN_VALUE;
	int glob_cnt = 0;
	static long node_cnt = 0;
	static int prune_cnt = 0;
	
	static int n;
	
	static char ch[] = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	
	static int bf = 0;
	
	static int fin_depth = 1;
	
	int depth = 0;
	
	
	class Node{
		
		int score;
		int r;
		int c;
		Node parent = null;
		
		int b[][] = new int[n][n];
		int visited[][] = new int[n][n];
		public Node(int a[][]) {
			r = -1;
			c = -1;
			for(int i=0;i<n;i++) {
				
				for(int j=0;j<n;j++) {
					
					b[i][j] = a[i][j];
					
				}
			}
		}
	}
	
	private void Grav(Node ob, HashMap<Integer, Integer> map) {
		
		for(int z : map.keySet()) {
			int cnt = 0;
			int strt = map.get(z);
			for(int i = strt;i>=0;i--) {
				
				if(ob.b[i][z] == -1) {
					cnt++;
				}
				else {
					
					ob.b[i+cnt][z] = ob.b[i][z];
					ob.b[i][z] = -1;
				}
			}
		}
	}

	private void checkVisited(Node node,Node child, int i, int j, int val, HashMap<Integer, Integer> map) 
	{
		if(i<0 || j<0 || i>=n || j>=n || child.b[i][j] !=val || child.b[i][j] == -1 || node.visited[i][j] == 1)
			return;
		
		child.b[i][j] = -1;
		node.visited[i][j] = 1;
		addToMap(i,j,map);
		glob_cnt++;
		
		checkVisited(node,child,i,j+1,val,map);
		checkVisited(node,child,i,j-1,val,map);
		checkVisited(node,child,i-1,j,val,map);
		checkVisited(node,child,i+1,j,val,map);
	}
	
	
	
	

	private void addToMap(int i, int j, HashMap<Integer, Integer> map) 
	{
		if(map.containsKey(j)) {
			if(map.get(j) < i) {
				map.put(j, i);
			}
		}
		else {
			map.put(j, i);
		}
	}
	
	
	
	public static void main(String[] args) 
	{
		homework ob = new homework();
		String input = ob.ReadInputFile();
		String x[] = input.split("\n");

		n = Integer.parseInt(x[0]);
		int p = Integer.parseInt(x[1]);
		float time_rem = Float.parseFloat(x[2]);
		int mat[][] = new int[n][n];
		
		
		
		mat = ob.GetGrid(n, x);
		
		Node node = ob.new Node(mat);
		node.score = 0;
		node.parent = null;
		
		
		
		
		
		TreeMap<Integer,List<Node>> tmap = new TreeMap<>(Collections.reverseOrder());
		
		for(int i = 0;i<n;i++) {
			for(int j=0;j<n;j++) {
				
				if(node.visited[i][j] == 1 || node.b[i][j] == -1) {
					continue;
				}

				Node child = ob.new Node(node.b);
				
				 bf++;
				
				
				
				child.parent = child;
				
				child.r = i+1;
				child.c = j;
				int val = child.b[i][j];
				
				HashMap<Integer,Integer> map = new HashMap<>();
				ob.checkVisited(node,child,i,j,val,map);
				//ob.connectedComponent(child,i,j,val,map);
				child.score = (int)Math.pow(ob.glob_cnt, 2);
				ob.glob_cnt = 0;
				ob.Grav(child,map);
				
				if(tmap.containsKey(child.score)) {
					tmap.get(child.score).add(child);
					tmap.put(child.score, tmap.get(child.score));
				
				}
				else {
						List<Node> list = new ArrayList<>();
						list.add(child);
						tmap.put(child.score,list);
						
				}
			}
		}
		
		ob.getDepth(time_rem,n,bf);
		
		
		//double tStart = System.currentTimeMillis();
		double tStart = ob.getCpuTime();
		
		
		
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		Node final_ans = null;
		
		int first_board_cnt = 0;
		for(Map.Entry<Integer,List<Node>> entry : tmap.entrySet()) {
			if(first_board_cnt == 1) {
				break;
			}
			first_board_cnt++;
			List<Node> value = entry.getValue();
			
			ob.PrintAns(value.get(0));
		}
	
		
		
		for(Map.Entry<Integer,List<Node>> entry : tmap.entrySet()) {
			  Integer key = entry.getKey();
			  
			  List<Node> value = entry.getValue();
			  
	
			  for(Node n : value) {
				  		 
				  		 

				  		  Node dp = 	ob.MinMax_DP(1,false,n,alpha,beta);
						  if(dp.score > alpha) {
							  alpha = dp.score;
							  final_ans = dp;
						  }
			  		}
			 }
	
		
		 //double tEnd = System.currentTimeMillis();
		 double tEnd = ob.getCpuTime();
		  //ob.PrintTime(tStart,tEnd);
		  ob.PrintAns(final_ans.parent);
		
		

	}
	
	
	private void getDepth(float remainingTime, int N,int size) {
		
		if(N<=5) {
			if(size<(N*N)/2) {
				if(remainingTime <=3 ) {
					fin_depth = 4;
				}
				else {
					fin_depth = 6;
				}
				
			}
			else {
				if(remainingTime <=3 ) {
					fin_depth = 4;
				}
				else {
					fin_depth = 6;
				}
			}
		}
		if(N>5 && N<=10) {
			if(size<(N*N)/2) {
				if(remainingTime <=3 ) {
					fin_depth = 4;
				}
				else {
					fin_depth = 5;
				}
				
			}
			else {
				if(remainingTime <=3 ) {
					fin_depth = 2;
				}
				else {
					fin_depth = 3;
				}
			}
		}
		
		if(N>10 && N<=15) {
			if(size<(N*N)/2) {
				if(remainingTime <=8 ) {
					fin_depth = 3;
				}
				else {
					fin_depth = 4;
				}
				
			}
			else {
				if(remainingTime <=8 ) {
					fin_depth = 2;
				}
				else {
					fin_depth = 3;
				}
			}
		}
		
		if(N>15 && N<=20) {
			if(size<(N*N)/2) {
				if(remainingTime <=10 ) {
					fin_depth = 3;
				}
				else {
					fin_depth = 4;
				}
				
			}
			else {
				if(remainingTime <=10 ) {
					fin_depth = 3;
				}
				else {
					fin_depth = 4;
				}
			}
		}
		
		if(N>20) {
			if(size<(N*N)/2) {
				if(remainingTime <=15 ) {
					fin_depth = 2;
				}
				else {
					fin_depth = 4;
				}
				
			}
			else {
				if(remainingTime <=15 ) {
					fin_depth = 2;
				}
				else {
					fin_depth = 4;
				}
			}
		}
	}

	

	public long getCpuTime( ) {
	    ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
	    return bean.isCurrentThreadCpuTimeSupported( ) ?
	        bean.getCurrentThreadCpuTime( ) : 0L;
	}
	
	
	private void PrintTime(double tStart, double tEnd) {
		double tot_time = (tEnd - tStart)/1000000000;
		double time_per_node = tot_time/node_cnt;
		
		System.out.println("Total Time:"+tot_time);
		System.out.println("Total nodes:"+node_cnt);
		System.out.println("Time per node:"+time_per_node);
		System.out.println("Prune Cnt:"+prune_cnt);
		
	}

	private void PrintAns(Node final_ans) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("output.txt", "UTF-8");
		} catch (FileNotFoundException e) {
		
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}
		
		
		//System.out.println(""+ch[final_ans.c]+final_ans.r);
		String result[][] = new String[n][n];
		for(int i=0;i<n;i++) {
			for(int j=0;j<n;j++) {
				
				result[i][j] = String.valueOf(final_ans.b[i][j]);
				
				if(result[i][j].equals("-1")) {
					result[i][j] = "*";
				}
					
				//System.out.print(result[i][j]);
			}
			//System.out.println("");
		}
		writer.println(""+ch[final_ans.c]+final_ans.r);
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				writer.print(result[i][j]);
			}
			writer.println();
		}

		writer.close();
		
		
	}
	
	private static void OutputFile(String[][] mat, int n, PrintWriter writer) 
	{


		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				writer.print(mat[i][j]);
			}
			writer.println();
		}

		writer.close();

	}
	
	
	private Node MinMax_DP(int depth, boolean maxPlayer, Node node, int alpha, int beta) 
	{
		node_cnt++;
		Node best_node = new Node(node.b);
		//if(boardCheck(node)) {
		if(boardCheck(node) || depth == fin_depth) {
			return node;
		}
		
		if(maxPlayer)
			best_node.score = Integer.MIN_VALUE;
		else
			best_node.score = Integer.MAX_VALUE;

				if(maxPlayer) {
					
					TreeMap<Integer,List<Node>> childmax_map = new TreeMap<>();
					childmax_map = GenerateChildren(node,maxPlayer);

					
					
					for(Map.Entry<Integer,List<Node>> entry : childmax_map.entrySet()) {
						  Integer key = entry.getKey();
						 
						  List<Node> value = entry.getValue();
						  for(Node n : value) {
							 
							  
							  Node val_rec = MinMax_DP(depth+1,false,n,alpha,beta);
								if(val_rec.score > best_node.score)
								{
									best_node = val_rec;
									
								}
				
								alpha = Math.max(alpha,best_node.score);
								
								if(alpha >= beta) {
									prune_cnt++;
									node_cnt--;
									return best_node;
								}
						  }
					}
					
	
				}
				
				else {
					TreeMap<Integer,List<Node>> childmin_map = new TreeMap<>();
					childmin_map = GenerateChildren(node,maxPlayer);

					
					for(Map.Entry<Integer,List<Node>> entry : childmin_map.entrySet()) {
						  Integer key = entry.getKey();
						
						  List<Node> value = entry.getValue();
						  for(Node n : value) {
							  
							
							  
							  Node val_rec = MinMax_DP(depth+1,true,n,alpha,beta);
							  if(val_rec.score < best_node.score)
							  {
									best_node = val_rec;
								
							  }
							  beta = Math.min(beta,best_node.score);
							  
							  if(beta <= alpha) {
								
									prune_cnt++;
									node_cnt--;
									return best_node;
								}
							  
						  }
					}

			}
	
			return best_node;

		
	}
	
	
	private TreeMap<Integer, List<Node>> GenerateChildren(Node node, boolean maxPlayer) {
		TreeMap<Integer,List<Node>> tmap = new TreeMap<>(Collections.reverseOrder());
		for(int i = 0;i<n;i++) {
			for(int j=0;j<n;j++) {
				
				if(node.visited[i][j] == 1 || node.b[i][j] == -1) {
					continue;
				}

				Node child = new Node(node.b);
				
				//node_cnt++;
				
				child.parent = node.parent;
				
				
				int val = child.b[i][j];
				
				HashMap<Integer,Integer> map = new HashMap<>();
				checkVisited(node,child,i,j,val,map);
				if(maxPlayer) {
					child.score = node.score + (int)Math.pow(glob_cnt, 2);
				}
				else {
					child.score = node.score - (int)Math.pow(glob_cnt, 2);
				}
				
				glob_cnt = 0;
				Grav(child,map);
				
				
				if(tmap.containsKey(child.score)) {
					tmap.get(child.score).add(child);
					tmap.put(child.score, tmap.get(child.score));
				
				}
				else {
						List<Node> list = new ArrayList<>();
						list.add(child);
						tmap.put(child.score,list);
						
				}
			}
		}
		
		return tmap;
	}
	
	
	

	
	
	
	
	
	





	
	
	
	
	
	

	

	

	
	private void PrintNode(Node child_node) 
	{
		for(int p=0;p<n;p++) {
			for(int q=0;q<n;q++) {
				System.out.print(child_node.b[p][q]+"  ");
			}
			System.out.println("");
		}
		System.out.println("\n");
	}

	private int[][] makeDeepCopy(int[][] b) 
	{
		int a[][] = new int[n][n];
		for(int i=0;i<n;i++) {
			
			for(int j=0;j<n;j++) {
				
				a[i][j] = b[i][j];
				
			}
			
		}
		return a;
	}

	

	private boolean boardCheck(Node node) 
	{
		
		for(int i=0;i<n;i++) {
			for(int j=0;j<n;j++) {
				
				if(node.b[i][j] != -1) {
					return false;
				}
				
			}
		}
		return true;
	}

	

	

	//Function to Form integer matrix from string array and return multidimensional integer array
	private int[][] GetGrid(int n, String[] x)
	{
		int a[][] = new int[n][n];
		int k = 0;
		
		for(int i=3;i<x.length;i++){
			
			String y[] = x[i].split("");


			for(int j=0;j<y.length;j++){
				if(y[j].equals("*")) {
					a[k][j] = -1;
				}
				else {
					a[k][j] = Integer.parseInt(y[j]);
				}
				
			}
			k++;
		}
		return a;
	}
	
	
	private String ReadInputFile()
	{
		String str = null;
		String input = "";
		try {
			FileReader f = new FileReader("/Users/dhairyapujara/eclipse-workspace/AI/src/input.txt");
			//FileReader f = new FileReader("input.txt");
			BufferedReader b = new BufferedReader(f);


			while((str = b.readLine()) != null){

				input+=str+"\n";
			}



		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return input;


	}

	
	
	
	
}




