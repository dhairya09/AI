import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class homework {
	
	public static void main(String[] args) 
	{

		homework ob = new homework();
		String input = ob.ReadInputFile();
		//System.out.println(input);

		String x[] = input.split("\n");

		String algorithm = x[0];
		int n = Integer.parseInt(x[1]);
		int p = Integer.parseInt(x[2]);

		

		HashMap<Integer,ArrayList<Integer>> map = new HashMap<>();
		HashMap<Integer,ArrayList<Integer>> valid_set = new HashMap<>();

		PrintWriter writer = null;
		try {
			writer = new PrintWriter("output.txt", "UTF-8");
		} catch (FileNotFoundException e) {
		
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}


		int mat[][] = ob.GetGrid(n,p,x,map,valid_set,writer);
		
		int cnt = 0;




		switch(algorithm) 
		{
		case "SA" :  
			
			
					
			
					//Failed to Find Proper Arrangement SA
					if(ob.ArrangeQueens_Tree_SA_Final(mat,n,p,map,valid_set) == false) {
					
						
						writer.println("FAIL");
						writer.close();
						
					}
					
		
					break;

		case "BFS" :   
			
						ExecutorService esa_b = Executors.newSingleThreadExecutor();
				        Future<Boolean> fut_b = esa_b.submit(() -> ob.ArrangeQueens_Tree_BFS_Optimized(mat,n,p,map));
				            try {
				                boolean chk = (boolean) fut_b.get(285, TimeUnit.SECONDS);
				                if(chk == false) {
									
										
										writer.println("FAIL");
										writer.close();
										

									}
									
				            } catch (InterruptedException | ExecutionException | TimeoutException e) {
				               
				               
				                esa_b.shutdown();
				                
				                writer.println("FAIL");
				                writer.close();
				                
				               
				            }
				
						break;

		case "DFS" : 
			
					ExecutorService esa_d = Executors.newSingleThreadExecutor();
			        Future<Boolean> fut_d = esa_d.submit(() -> ob.ArrangeQueens_Tree_DFS_Optimized_Map_PM(mat,n,p,cnt,0,0,map));
			            try {
			                boolean chk = (boolean) fut_d.get(285, TimeUnit.SECONDS);
			                if(chk == false) {
								
									
									writer.println("FAIL");
									writer.close();
									
								}
								else{
								
									writer.println("OK");
									OutputFile(mat,n,writer);
									
								}
			            } catch (InterruptedException | ExecutionException | TimeoutException e) {
			               
			                
			                esa_d.shutdown();
			                
			                writer.println("FAIL");
			                writer.close();
			               
			            }
			
					break;



		}

		System.exit(0);

		


	}
	
			//Function to read input text file and return String
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
			
			
			//Function to Form integer matrix from string array and return multidimensional integer array
			private int[][] GetGrid(int n, int p, String[] x, HashMap<Integer, ArrayList<Integer>> map, HashMap<Integer, ArrayList<Integer>> valid_set, PrintWriter writer)
			{
				
				int tot_tree_cnt = 0;
				int a[][] = new int[n][n];
				int k = 0;
				for(int i=3;i<x.length;i++){
					String y[] = x[i].split("");


					for(int j=0;j<y.length;j++){
						a[k][j] = Integer.parseInt(y[j]);

				
						if(a[k][j] == 2){
							tot_tree_cnt++;	
			
							if(!map.containsKey(j)){
								map.put(j,new ArrayList<Integer>());
							}
							map.get(j).add(k);
						}
					}
					k++;
				}
				
				
					

				int col = 0;
				while(col < a[0].length) 
				{
					ArrayList<Integer> col_list = new ArrayList<>();
					for(int i=0;i<a.length;i++) {

						if(a[i][col] == 0) {
							col_list.add(i);
							valid_set.put(col,col_list);
						}
					}
					col++;

				}
				
				if(tot_tree_cnt == 0) {
					if(p > n) {
						
						writer.println("FAIL");
						writer.close();
						System.exit(0);
					}
				}
				
				
				if(n*n - tot_tree_cnt < p) {
					
					writer.println("FAIL");
					writer.close();
					System.exit(0);
				}

				
				return a;


			}
			
			private static void OutputFile(int[][] mat, int n, PrintWriter writer) 
			{


				for (int i = 0; i < n; i++) {
					for (int j = 0; j < n; j++) {
						writer.print(mat[i][j]);
					}
					writer.println();
				}

				writer.close();

			}
			
			
			//Function to place/arrange queens with tree check and return boolean using DFS and its cutshorting the traversal and map
			private boolean ArrangeQueens_Tree_DFS_Optimized_Map_PM(int[][] mat, int n, int p, int cnt, int r, int c, HashMap<Integer, ArrayList<Integer>> map)
			{

				while(c<n)
				{

					if(r >= n){
						r = 0;
						if(c == n-1){
							break;
						}
						c++;
					}

				
					if(mat[r][c] == 2){
						
						if(cnt == p){

							return true;
						}

					}
					else
					{
						if(isSafe_Tree(mat,r,c,n))
						{
							cnt++;
						
							mat[r][c] = 1;

							if(cnt == p){

								return true;
							}

							if(!ShouldProceed(mat,n,p,cnt,r,c,map)){

								mat[r][c] = 0;
								cnt --;
								break;
							}


							if(map.containsKey(c)){

							
								ArrayList<Integer> al = new ArrayList<>();
								al = map.get(c);

								for(Integer i : al){
									if(i >= r){

										if(i == n-1){


											if(ArrangeQueens_Tree_DFS_Optimized_Map_PM(mat,n,p,cnt,0,c+1,map) == true){
												return true;
											}
										}
										else{
											if(ArrangeQueens_Tree_DFS_Optimized_Map_PM(mat,n,p,cnt,i+1,c,map) == true){
												return true;
											}
										}

										cnt--;
										mat[r][c] = 0;


									}
								}
								
							}



							if(ArrangeQueens_Tree_DFS_Optimized_Map_PM(mat,n,p,cnt,0,c+1,map) == true){
								return true;
							}

							cnt--;
							mat[r][c] = 0;



						}

					}

					r++;

				}
				return false;

			}
			
			//Function to check whether it is safe to place queen considering tree in given row/column and return boolean
			private boolean isSafe_Tree(int[][] mat, int r, int c, int n)
			{

				for(int i=c-1;i>=0;i--){

					if(mat[r][i] == 2){
						break;
					}

					if(mat[r][i] == 1){
						return false;
					}
				}

				for(int i=r-1, j = c-1;i>=0 && j>=0;i--,j--){
					if(mat[i][j] == 2){
						break;
					}
					if(mat[i][j] == 1){
						return false;
					}
				}

				for(int i=r+1,j = c-1;i < n && j >=0;i++,j--){

					if(mat[i][j] == 2)
						break;

					if(mat[i][j] == 1){
						return false;
					}
				}

				for(int i = r-1;i>=0;i--){

					if(mat[i][c] == 2){
						break;
					}

					if(mat[i][c] == 1){
						return false;

					}

				}

				return true;
			}
			
			private boolean ShouldProceed(int[][] mat, int n, int p, int cnt, int r, int c, HashMap<Integer, ArrayList<Integer>> map)
			{
				int total_tree_cnt = 0;
				int total_lizards_tobe_placed = p - cnt;
				int further_cols = n - c - 1;

				ArrayList<Integer> al = new ArrayList<>();

				if(map.containsKey(c)){


					al = map.get(c);
					
					for(Integer z : al){
						if(z >= r) {
							
						
							if(z != n-1){
								total_tree_cnt += 1;
							}

						}

					}

				}

				for(int i = c+1;i<n;i++) {
					if (map.containsKey(i)) {

						total_tree_cnt += map.get(i).size();
					}
				}



				if((total_tree_cnt + further_cols) < total_lizards_tobe_placed){
					return false;

				}

				return true;

			}
			
			
			//Function to place/arrange queens with tree check and return boolean using BFS
			private boolean ArrangeQueens_Tree_BFS_Optimized(int[][] mat, int n, int p, HashMap<Integer, ArrayList<Integer>> map)
			{
				
				PrintWriter writer = null;
				try {
					writer = new PrintWriter("output.txt", "UTF-8");
				} catch (FileNotFoundException e) {
				
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					
					e.printStackTrace();
				}
				
				
				Queue<Object> q = new LinkedList<>();
				Queue<List<Integer>> q_cnt = new LinkedList<>();
				List<Integer> al = Arrays.asList(0,0,0);
				q.add(mat);
				q_cnt.add(al);

				while(!q.isEmpty()) {

				

					int [][]poped = (int[][]) q.remove();

				
					int cnt = q_cnt.peek().get(0);
					int r= q_cnt.peek().get(1);
					int c= q_cnt.peek().get(2);

					q_cnt.remove();

					while(c < n){

						if(r >= n){
							r = 0;
							if(c == n-1){
								break;
							}
							c++;
						
						}

						

						List<Integer> list_child;
						

						if(poped[r][c] == 2){
							
							if(cnt == p) {
								writer.println("OK");
								OutputFile(poped,n,writer);
								
								

								return true;
							}
								

						}
						else{

							if(isSafe_Tree(poped,r,c,n))
							{
								

								homework object = new homework();
								int[][] child = object.GenerateNew(n,poped);




								int cnt_child = cnt + 1;
								child[r][c] = 1;





								if(cnt_child == p){

									//System.out.println("Found the proper arrangement");

									
									
									writer.println("OK");
									OutputFile(child,n,writer);
									
									

									return true;
								}


								if(!ShouldProceed(child,n,p,cnt_child,r,c,map)){
									child[r][c] = 0;
									cnt_child--;
									
									break;
								}

								q.add(child);



								if(r == n-1){

									list_child = Arrays.asList(cnt_child,0,c+1);
									q_cnt.add(list_child);


								}
								else{

									list_child = Arrays.asList(cnt_child,r+1,c);
									q_cnt.add(list_child);


								}

							}

						}


						r++;

					}


				}
				return false;


			}
			
			public int[][] GenerateNew(int n,int[][] poped){


				int child[][] = new int [n][n];
				for(int i=0;i<n;i++){
					for(int j=0;j<n;j++){
						child[i][j] = poped[i][j];
					}
				}

				return child;


			}
	
			private String PickRandom(ArrayList<String> al) 
			{
				
				if(al.size() != 0) {
					int i = ThreadLocalRandom.current().nextInt(0, al.size());
					return al.get(i);
				}
				else {
					return null;
				}




			}
			
			private void MQR(int[][] orig, int n, ArrayList<String> al, HashMap<Integer, ArrayList<Integer>> map) 
			{
				
				
				String q_coord;
				while((q_coord = PickRandom(al)) != null) {

					
					String x[] = q_coord.split(" ");
					int r = Integer.parseInt(x[0]);
					int c = Integer.parseInt(x[1]);

					
					String new_loc = PRD(r,c,orig,n);

					
					if(new_loc != null) 
					{
						String y[] = new_loc.split(" ");
						int r_new = Integer.parseInt(y[0]);
						int c_new = Integer.parseInt(y[1]);
						
						
						orig[r][c] = 0;
						al.remove(r+" "+c);
						orig[r_new][c_new] = 1;
						al.add(r_new+" "+c_new);

						break;

					}
				}

			}
			
			
			
			
			
			private String PRD(int r, int c, int[][] mat, int n) 
			{
				ArrayList<String> next_coord = new ArrayList<>();
				
				for(int i=c-1;i>=0;i--){

					if(mat[r][i] == 0){
						next_coord.add(r+" "+i);
						break;
					}

					
				}

				for(int i=r-1, j = c-1;i>=0 && j>=0;i--,j--){
					if(mat[i][j] == 0){
						next_coord.add(i+" "+j);
						break;
					}
					
				}

				for(int i=r+1,j = c-1;i < n && j >=0;i++,j--){

					if(mat[i][j] == 0) {
						next_coord.add(i+" "+j);
						break;
					}
				}

				for(int i = r-1;i>=0;i--){

					if(mat[i][c] == 0){
						next_coord.add(i+" "+c);
						break;
					}

					
				}
				
				for(int i = r+1;i<n;i++){

					if(mat[i][c] == 0){
						next_coord.add(i+" "+c);
						break;
					}

					
				}
				
				for(int i=r+1, j = c+1;i<n && j<n;i++,j++){
					if(mat[i][j] == 0){
						next_coord.add(i+" "+j);
						break;
					}
					
				}
				
				for(int i=r-1,j = c+1;i >= 0 && j < n;i--,j++){

					if(mat[i][j] == 0) {
						next_coord.add(i+" "+j);
						break;
					}
				}
				
				for(int i = c+1;i<n;i++){

					if(mat[r][i] == 0){
						next_coord.add(r+" "+i);
						break;
					}

					
				}


				
				return PickRandom(next_coord);



			}
			
			private boolean CheckNoSpace(int[][] mat) 
			{
				for(int i=0;i<mat.length;i++) {
					for(int j=0;j<mat[i].length;j++) {
						if(mat[i][j] == 0) {
							return false;
						}
					}
				}
				return true;
				
				
			}
			
			private boolean ArrangeQueens_Tree_SA_Final(int[][] mat, int n, int p, HashMap<Integer, ArrayList<Integer>> map, HashMap<Integer, ArrayList<Integer>> valid_set) 
			{
				int noi = 1;
				int flag = 0;
				homework o = new homework();
				PrintWriter writer = null;
				ArrayList<String> al = new ArrayList<>();
				
				int dfs[][] = o.GenerateNew(n, mat);
				int totalTime = 180000; 
				long startTime = System.currentTimeMillis();
				
				try {
					writer = new PrintWriter("output.txt", "UTF-8");
				} catch (FileNotFoundException e) {
					
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					
					e.printStackTrace();
				}
				
				


				if(!PQR(mat,n,al,p,map,valid_set)) {
					return false;
				}
				
				
				
				int orig[][] = o.GenerateNew(n, mat);
				
			
				
				
				int noc = NumberOfConflict(mat,al,n);	
				
				if(noc == 0) {
					
					writer.println("OK");
					OutputFile(mat,n,writer);
					
					
					return true;
				}
					

			
				double initialT = 100;
				double T = Math.log10(1 + (1/initialT));
				
				
				
				while(System.currentTimeMillis() - startTime < totalTime) {
					if(CheckNoSpace(mat)) {
						return false;
					}
					
					MQR(mat,n,al,map);
					
					
					
					
					

					int new_cost = NumberOfConflict(mat,al,n);	
					

					if(new_cost == 0) {
						
						flag = 1;
						break;
					}


					int diff = new_cost - noc;
					
					if(diff < 0) {
						
						
						
						for(int i=0;i<n;i++){
				            System.arraycopy(mat[i], 0, orig[i], 0, n);
				        }
						

						
						





					}
					else {
						diff =  noc - new_cost;
						double prob = CalculateProbability(diff,T);
						


						if(prob > Math.random()) {
							

							
							for(int i=0;i<n;i++){
					            System.arraycopy(mat[i], 0, orig[i], 0, n);
					        }

							
							



						}
						else {
							
						
							
								al.clear();
								

								
								for(int i=0;i<n;i++){
						            System.arraycopy(orig[i], 0, mat[i], 0, n);
						        }
								
								
								


								
								
								
								
								
								for(int i=0;i<mat.length;i++) 
								{
									for(int j=0;j<mat[i].length;j++) 
									{
										if(mat[i][j] == 1) {
											al.add(i+" "+j);
										}
									}
								}
							}





					}
					noc = new_cost;

					noi++;

					
					T = Math.log10(1 + (1/T));
					
					
					
				}
				
				if(flag == 1) {
					
					
					writer.println("OK");
					
					
					
					OutputFile(mat,n,writer);
					
					return true;
					
				 }
				
				
				
				if(o.ArrangeQueens_Tree_DFS_Optimized_Map_PM(dfs,n,p,0,0,0,map) == true)
				{
					
					
					writer.println("OK");
					OutputFile(dfs,n,writer);
					
					
					
					return true;
				}
				
				
				
				
				
				return false;
				
				
				
			}
			
			

			private boolean PQR(int[][] mat, int n, ArrayList<String> al,int p, HashMap<Integer, ArrayList<Integer>> map, HashMap<Integer, ArrayList<Integer>> valid_set) 
			{
				
				
				
				Random r = new Random();
				int liz_cnt = 0;
				for(int i=0;i<n;i++) {
					if(map.containsKey(i)) {
						if(map.get(i).size() == n)
							continue;
					}
					
					
				
					int pos = ThreadLocalRandom.current().nextInt(0,n);

					while(mat[pos][i] == 2) {
						
						pos = ThreadLocalRandom.current().nextInt(0,n);
					}
					
					mat[pos][i] = 1;
					al.add(pos+" "+i);
					
					
					int ind = valid_set.get(i).indexOf(pos);
					valid_set.get(i).remove(ind);
					if(valid_set.get(i).size() == 0) {
						valid_set.remove(i);
					}
					
					liz_cnt++;
					
					if(liz_cnt == p) {
						return true;
					}
					
				}
				
				
				while(p > liz_cnt) {
					
					
					List<Integer> keys = new ArrayList<>(valid_set.keySet());
					
					if(keys.size() == 0) {
						break;
					}
						
						
						int col_ind = ThreadLocalRandom.current().nextInt(keys.size());
						int col = keys.get(col_ind);
						
						ArrayList<Integer> randomKey = valid_set.get(col);
						
						int row_ind = ThreadLocalRandom.current().nextInt(randomKey.size());
						int row = randomKey.get(row_ind);
						
						mat[row][col] = 1;
						al.add(row+" "+col);
						liz_cnt++;
						
						
						int ind = valid_set.get(col).indexOf(row);
						valid_set.get(col).remove(ind);
						if(valid_set.get(col).size() == 0) {
							valid_set.remove(col);
						}
						
						
						
					
				}
				if(liz_cnt == p) {
					return true;
				}
				
				
				return false;
			}
			
			private double CalculateProbability(int diff, double T) 
			{
				double prob = Math.exp((diff/T));

				return prob;


			}
			
			private int NumberOfConflict(int[][] mat, ArrayList<String> al, int n) 
			{
				int tot_conflicts = 0;
				
				for(String s : al) {
					int noc= 0;
					String x[] = s.split(" ");
					int r = Integer.parseInt(x[0]);
					int c = Integer.parseInt(x[1]);
					

					noc = CountConflicts(mat,r,c,n);
					
					if(noc!=0) {
						noc+=1;
					}
					tot_conflicts+= noc;

				}
				
				return tot_conflicts;
			}
			
			private int CountConflicts(int[][] mat, int r, int c, int n) 
			{
				int conf = 0;
				for(int i=c-1;i>=0;i--){

					if(mat[r][i] == 2){
						break;
					}

					if(mat[r][i] == 1){

						conf+=1;
						

					}
				}

				for(int i=r-1, j = c-1;i>=0 && j>=0;i--,j--){
					if(mat[i][j] == 2){
						break;
					}
					if(mat[i][j] == 1){

						conf+=1;
						

					}
				}

				for(int i=r+1,j = c-1;i < n && j >=0;i++,j--){

					if(mat[i][j] == 2)
						break;

					if(mat[i][j] == 1){
						conf+=1;
						

					}
				}

				for(int i = r-1;i>=0;i--){

					if(mat[i][c] == 2){
						break;
					}

					if(mat[i][c] == 1){
						conf+=1;
						


					}

				}

				return conf;
			}
	

}
