import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class homework {

	HashMap<String,List<List<predicateNode>>> map = new HashMap<>();
	public static void main(String[] args) {

			homework ob = new homework();
			String input = ob.ReadInputFile();
			//System.out.println("input::"+input);

			String x[] = input.split("\n");

			int noOfQueries = Integer.parseInt(x[0]);
			int noOfSentences = Integer.parseInt(x[noOfQueries+1]);

			int start = noOfQueries+2;
			int end = noOfQueries+noOfSentences+2;
			//System.out.println("start:"+start);
			//System.out.println("end:"+end);
			for(int i=start;i<end;i++) {
					ob.buildKB(i,x);
			}
			//ob.printMap(ob.map);

			PrintWriter writer = null;
			try {
				writer = new PrintWriter("output.txt", "UTF-8");
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}



			//Queries

			for(int j=1;j<=noOfQueries;j++)
			{
				String predicateQuery = x[j];
				String predicateContradiction = ob.negate(predicateQuery);
				//System.out.println(predicateContradiction);
				HashMap<String,List<List<predicateNode>>> KB = new HashMap<>();
				KB = ob.makeDeepCopy(ob.map);

				ob.addQueryToKB(predicateContradiction,KB);

				//System.out.println("Initial Knowledgebase for query: "+j);
				//ob.printMap(KB);


					boolean res = ob.prove(KB);
					//System.out.println("FInal result:"+res);

					OutputFile(res,writer);

			}
			writer.close();
	}

	private static void OutputFile(boolean res, PrintWriter writer)
	{
		if(res == true)
			writer.println("TRUE");
		else
			writer.println("FALSE");

	}

	List<List<predicateNode>> newSent;
	boolean f = false;
	private boolean prove(HashMap<String, List<List<predicateNode>>> KB)
	{
			long start = System.currentTimeMillis();
			while(true && ((System.currentTimeMillis() - start)/1000) < 30) {

				newSent = new ArrayList<>();
				f = false;

				for(String key : KB.keySet()) {

					if(((System.currentTimeMillis() - start)/1000) > 30)
						break;


					String negatedKey = negate(key);

					if(!KB.containsKey(negatedKey))
						continue;


					for(List<predicateNode> kList: KB.get(key)) {

						if(((System.currentTimeMillis() - start)/1000) > 30)
									break;

						for(List<predicateNode> nList : KB.get(negatedKey)) {

							if(((System.currentTimeMillis() - start)/1000) > 30)
								break;

							if(unification(kList,nList,start)) {
								//System.out.println("Empty List:"+true);
								return true;
							}

						}
					}
				}

				//System.out.println("newSent size:"+newSent.size());

				addToKB(newSent,KB,start);

				if(f == false) {
					//System.out.println("KB ma kai add nai thayu:"+false);
					return false;
				}





		}
		//System.out.println("Timeout:"+false);
		return false;


	}

	private boolean unification(List<predicateNode> kList, List<predicateNode> nList, long start)
	{


		for(predicateNode outer : kList)
		{
			if(((System.currentTimeMillis() - start)/1000) > 30)
				break;

			for(predicateNode inner : nList)
			{
				if(((System.currentTimeMillis() - start)/1000) > 30)
					break;


				if(outer.name.equals(negate(inner.name)))
				{
					/*System.out.print("klist: ");
					printList(kList);
					System.out.print("nList: ");
					printList(nList);



					System.out.print(formNewNode(outer)+" ");
					System.out.print(formNewNode(inner));*/
					String var1[] = outer.variables;
					String var2[] = inner.variables;



					HashMap<String,String> hmap = new HashMap<>();
					int UR_flag = 0;
					for(int i=0;i<var1.length;i++)
					{
						//var1[i] = var1[i].trim();
						//var2[i] = var2[i].trim();

					   if(isConst(var1[i]) && isConst(var2[i]) && var1[i].equals(var2[i]))
						   continue;

					   if(!isConst(var1[i]) && !isConst(var2[i]) && var1[i].equals(var2[i]))
						   continue;

					   if(isConst(var1[i]) && isConst(var2[i]) && (!var1[i].equals(var2[i])))
					   {
						   UR_flag = 1;
						   //System.out.println("UR_flag:"+UR_flag);
						   break;
					   }

					   if(!isConst(var1[i]) && !isConst(var2[i]) && (!var1[i].equals(var2[i])))
					   {
						   UR_flag = 1;
						   //System.out.println("UR_flag:"+UR_flag);
						   break;
					   }

					   if(!hmap.isEmpty()) {

							if(!isConst(var1[i])) {
									if(hmap.containsKey(var1[i])) {
										UR_flag = 1;
										break;
									}
							}
							if(!isConst(var2[i])) {
									if(hmap.containsKey(var2[i])) {
										UR_flag = 1;
										break;
									}
							}

						}

					   hmap = unify(var1[i],var2[i],hmap);

					}

					if(UR_flag == 0) {
						//System.out.println("");
						//printUMap(hmap);
						List<predicateNode> newClause = new ArrayList<>();
						newClause = UR(kList,nList,outer,inner);

						if(newClause.size() == 0) {
							//System.out.println("New Clause Khali");
							return true;
						}

						List<predicateNode> newList = new ArrayList<>();
						List<String> newListDisplay = new ArrayList<>();
						for(predicateNode node : newClause) {

							String nNode = formNewNode(node);
							//System.out.println(nNode);
							predicateNode newNode = createPredicateNode(nNode);
							//System.out.println("newnode name:"+newNode.name);
							//printStrinArray(newNode.variables);
							//System.out.println(node.hashCode()+" "+newNode.hashCode());

							if(!hmap.isEmpty()) {
								substituteValues(hmap,newNode);
							}
							//System.out.println("newnode after substitution:"+newNode.name);
							//printStrinArray(newNode.variables);
							newList.add(newNode);
							newListDisplay.add(formNewNode(newNode));
						}
						//System.out.print("new Clause: ");
						//System.out.println(newListDisplay);


						newSent.add(newList);
					}
					//System.out.println("");


				}


			}

		}

		return false;


	}

	private String formNewNode(predicateNode node)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(node.name).append("(");
		for(int i=0;i<node.variables.length;i++) {
			sb.append(node.variables[i]).append(",");
		}

		sb.replace(sb.lastIndexOf(","), sb.length(), "");
		sb.append(")");

		return sb.toString();


	}

	private List<predicateNode> UR(List<predicateNode> kList, List<predicateNode> nList, predicateNode outer, predicateNode inner) {
		//printList(kList);
		//printList(nList);
		List<predicateNode> newClause = new ArrayList<>();
		for(predicateNode element : kList)
		{

				if(!outer.name.equals(element.name) || (!Arrays.equals(outer.variables, element.variables)))
				{


						newClause.add(element);



				}

		}
		for(predicateNode element : nList)
		{
			if(!inner.name.equals(element.name) || (!Arrays.equals(inner.variables, element.variables)))
			{


					newClause.add(element);

			}
		}
		//System.out.println("new clause return");
		//printList(newClause);
		return newClause;

	}

	private HashMap<String,String> unify(String var1, String var2, HashMap<String, String> hmap)
	{


			if(isConst(var1) && !isConst(var2))
			{
				hmap.put(var2, var1);


			}
			else
			if(!isConst(var1) && isConst(var2))
			{
				hmap.put(var1, var2);

			}

			return hmap;
	}

	private void substituteValues(HashMap<String, String> hmap, predicateNode node)
	{
			String var[] = node.variables;
			for(int i=0;i<var.length;i++)
			{
				var[i] = var[i].trim();
				if(hmap.containsKey(var[i]))
				{
					var[i] = hmap.get(var[i]);
				}
			}
	}

	private void addToKB(List<List<predicateNode>> newSentence, HashMap<String, List<List<predicateNode>>> KB, long start)
	{

		for(List<predicateNode> newList : newSentence)
		{
			if(((System.currentTimeMillis() - start)/1000) > 30)
				break;

			int flag = 0;
			predicateNode nodeListToCompareWith = newList.get(0);
			if(KB.containsKey(nodeListToCompareWith.name)) {

				List<List<predicateNode>> list = new ArrayList<>();
				list = KB.get(nodeListToCompareWith.name);

				for(List<predicateNode> innerList : list) {
						//printList(innerList);
						if(newList.size() == innerList.size()) {
								int cnt = checkEveryNode(newList,innerList,start);
								//System.out.println("cnt:"+cnt);
								if(cnt == newList.size()) {
									flag = 1;
									break;
								}
						}

				}
				if(flag == 0)
				{
					f = true;
					//System.out.println("Not in KB");
					for(predicateNode node : newList) {
						addToKBs(newList,node.name,KB);
					}


				}

			}
			//printMap(KB);


		}


	}

	private void addToKBs(List<predicateNode> innerList, String key, HashMap<String, List<List<predicateNode>>> KB) {

		if(KB.containsKey(key))
		{
			List<List<predicateNode>> temp = new ArrayList<>();
			temp = KB.get(key);
			temp.add(innerList);
			KB.put(key,temp);
		}
	}

	private int checkEveryNode(List<predicateNode> newList, List<predicateNode> innerList, long start)
	{
		int cnt = 0;
		for(predicateNode newListNode : newList) {

			if(((System.currentTimeMillis() - start)/1000) > 30)
				break;

			for(predicateNode innerListNode : innerList) {

				if(((System.currentTimeMillis() - start)/1000) > 30)
					break;

				if(newListNode.name.equals(innerListNode.name) && Arrays.equals(newListNode.variables, innerListNode.variables)) {
					cnt++;
				}

			}

		}
		return cnt;

	}



	private boolean isConst(String var)
	{
		if(Character.isUpperCase(var.charAt(0)))
			return true;

		return false;
	}



	private String ReadInputFile()
	{
		String str = null;
		String input = "";
		try {
			//FileReader f = new FileReader("/Users/dhairyapujara/eclipse-workspace/AI-HW5/src/HW5/input.txt");
			FileReader f = new FileReader("input.txt");
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

	private void buildKB(int i, String[] sentence)
	{

		String words[] = sentence[i].split("\\|");
		//printStrinArray(words);
		List<predicateNode> innerList = new ArrayList<>();
		innerList = formList(words);
		//printList(innerList);
		//System.out.println("innerList:"+innerList);

		for(String predicate : words) {


				predicate = predicate.trim();
				String key = getFirstLetter(predicate);
				//System.out.println("key:"+key);
				if(map.containsKey(key)) {

					List<List<predicateNode>> temp = new ArrayList<>();
					temp = map.get(key);
					temp.add(innerList);
					map.put(key,temp);
				}

				else {

					List<List<predicateNode>> outerList = new ArrayList<>();
					outerList.add(innerList);

					map.put(key,outerList);


				}

		}
		//System.out.println("");


	}

	private List<predicateNode> formList(String[] words)
	{
		//printStrinArray(words);

		List<predicateNode> innerList = new ArrayList<>();
		for(String predicate : words) {
			predicate = predicate.trim();
			predicateNode node = createPredicateNode(predicate);
			innerList.add(node);
		}
		return innerList;
	}





	private predicateNode createPredicateNode(String predicate)
	{

		predicate = predicate.replace("(", " ");
	    predicate = predicate.replace(")", "");
	    //System.out.println("predicate:"+predicate);

	    String predicateName = predicate.split(" ")[0];
		String variables = predicate.split(" ")[1];

		String var[] = variables.split(",");
		//printStrinArray(var);

		predicateNode pn = new predicateNode(predicateName,var);
		//System.out.println("node name:"+pn.name+" variables:"+ pn.variables.length);
		return pn;

	}

	private String negate(String predicate)
	{
		if(predicate == null)
				return null;
		//System.out.println("predicate in negate:("+predicate+")");
			StringBuilder sb = new StringBuilder();
			if(predicate.charAt(0) == '~')
				return sb.append(predicate.substring(1)).toString();
			else
				return sb.append("~").append(predicate).toString();


	}

	private void addQueryToKB(String predicate, HashMap<String, List<List<predicateNode>>> KB) {

		predicate = predicate.trim();
		String key = getFirstLetter(predicate);
		//System.out.println("key:"+key);
		predicateNode node = createPredicateNode(predicate);

		//System.out.println(node.name);
		//printStrinArray(node.variables);

		List<predicateNode> innerList = new ArrayList<>();
		innerList.add(node);

		if(KB.containsKey(key))
		{
			List<List<predicateNode>> temp = new ArrayList<>();
			temp = KB.get(key);
			temp.add(innerList);
			KB.put(key,temp);


		}
		else {
			List<List<predicateNode>> temp = new ArrayList<>();
			temp.add(innerList);
			KB.put(key,temp);
		}

	}

	private String getFirstLetter(String predicate)
	{
		 predicate = predicate.replace("(", " ");
		 predicate = predicate.replace(")", "");
		 return predicate.split(" ")[0];


	}

	private HashMap<String, List<List<predicateNode>>> makeDeepCopy(HashMap<String, List<List<predicateNode>>> map) {

		HashMap<String,List<List<predicateNode>>> KB = new HashMap<>();
		for(Map.Entry<String, List<List<predicateNode>>> entry : map.entrySet())
		 {
			 KB.put(entry.getKey(),new ArrayList<List<predicateNode>>(entry.getValue()));
		 }

		return KB;

	}

	private void printList(List<predicateNode> newClause)
	{

		for(predicateNode node : newClause) {
			System.out.print(formNewNode(node)+" ");
		}
		System.out.println("");

	}

	private void printStrinArray(String[] words) {
		for(String s : words) {
			System.out.print(s+",");
		}
		System.out.println("");

	}

	private void printUMap(HashMap<String, String> hmap)
	{
				if(hmap.isEmpty())
				{
					System.out.println("Empty Map");
				}
				else
				{
					for(String key : hmap.keySet()) {
						System.out.println(key +":("+ hmap.get(key)+")");
					}
				}


	}

	private void printMap(HashMap<String, List<List<predicateNode>>> map)
	{

				//Knowledge Base
				//System.out.println("HashMap KB:");
				for(String key : map.keySet()) {
					System.out.print(key +":");
					for(List<predicateNode> nodeList : map.get(key)) {
						System.out.print("[");
						for(predicateNode node : nodeList) {
							System.out.print(node.name+",");
						}
						System.out.print("]");
					}

					System.out.println("");
				}

	}



}

class predicateNode
{
	String name;
	String[] variables;

	public predicateNode(String name, String[] variables)
	{
		this.name = name;
		this.variables = variables;

	}

	public int getVariableLength() {
		return variables.length;
	}

	public String[] getVariables() {
		return variables;
	}

	public void setVariables(String[] variables) {
		this.variables = variables;
	}
}
