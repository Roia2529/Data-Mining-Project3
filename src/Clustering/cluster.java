package Clustering;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.jfree.ui.RefineryUtilities;

import java.io.IOException;
import Clustering.clusterPlot;

public class cluster{
	class Mergeset_Info{
		public int set1;
		public int set2;
		public double distance;
		Mergeset_Info(){}
		Mergeset_Info(int s1,int s2,double d)
		{
			this.set1=s1;
			this.set2=s2;
			this.distance=d;
		}
	};
	
	class Cluster_Info{
		private Set<Integer> centers;
		private double distance;
		private Map<Integer, Set<Integer>> t;
		private Map<Integer, ArrayList<Double>> cord;
		Cluster_Info(){}
		Cluster_Info(Set<Integer> c,double d)
		{
			this.centers = c;
			this.distance=d;
		}
		
		//set 
		void setClusters(final Map<Integer, Set<Integer>> c)
		{
			 this.t= c;
		}
		void setCenter_Cord(final Map<Integer, ArrayList<Double>> cord)
		{
			 this.cord= cord;
		}
		
		//get
		double getMeansCost()
		{
			return this.distance;
		}
		Set<Integer> getCenters()
		{
			return this.centers;
		}
		Map<Integer, Set<Integer>> getCluster()
		{
			return this.t;
		}
		Map<Integer, ArrayList<Double>> getCenter_Cord()
		{
			return this.cord;
		}
		
	};
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	enum def_Link{
		Single,
		Complete,
		Mean,
	}
	
	private Map<Integer, ArrayList<Double>> input_data = new HashMap<Integer, ArrayList<Double>>();
	private Map<Integer, Double> data_dim_ave = new HashMap<Integer, Double>();
	
	public cluster(String path)
	{
		this.input_data = readdata(path);
	}
	public cluster(String path,boolean do_dim_ave)
	{
		this.input_data = readdata(path);
		Double value = 0.0;
		int dim = this.input_data.values().size();
		for(Integer i:this.input_data.keySet())
		{
			value=0.0;
			//for(Double d : this.input_data.get(i))
			//	value += d;
			for(int j=0;j<1;j++)
				value += this.input_data.get(i).get(j);
			data_dim_ave.put(i, value);
		}
	}
	
	public Map<Integer, ArrayList<Double> > data()
	{
		return this.input_data;
	}
	
	
	/**
	 * Read data by buffer reader
	 * @param path
	 * @return HashMap, key:index of data, 
	 */
	private HashMap<Integer, ArrayList<Double>> readdata(String path)
	{
		String line;
		String[] result = null;
		int item=0;
		int index = 0;
		
		HashMap<Integer, ArrayList<Double> > datamap = new HashMap<Integer, ArrayList<Double> >(); 

		try{
		    InputStream fis = new FileInputStream(path);
		    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		    BufferedReader br = new BufferedReader(isr);
		    while ((line = br.readLine()) != null)
			{
		    	result = line.split("\t"); //split by tab
		    	item = 0;
		    	
		    	ArrayList<Double> datalist = new ArrayList<Double>();
		    	for(String data_s:result)
		    	{
		    		if(item==0)
		    		{
		    			index = Integer.parseInt(data_s);
		    			item=1;
		    		}
		    		else
		    		{
		    			datalist.add(Double.parseDouble(data_s));
		    		}
		    		
		    	}
		    	datamap.put(index, datalist);
			}
		    
		    br.close();
		    return datamap;
		    
		    
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;	
	}


	/**
	 * Calculate Euclidean distance between two points, the dimension of these two
	 * points must be the same.
	 * @param point1
	 * @param point2
	 * @return distance
	 */
	public static Double EuclideanD(final ArrayList<Double> point1, final ArrayList<Double> point2)
	{
		try {
			if(point1.size() != point2.size())
				throw new Exception();
			else
			{
				Double squaresum=0.0;
				for(int i=0;i<point1.size();i++)
				{
					double d= (double)point1.get(i);
					squaresum+=Math.pow(d-point2.get(i),2);
				}
				
				return Math.sqrt(squaresum);
			}
				
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("dimension are different!");
		}
		return 0.0;
	}
	
	/**
	
	 * Generated clusters according to single-link algorithm
	 * end_num: # of clusters in the end
	 * @param end_num
	 * @return clusters result in Map
	 * @throws Exception 
	 */
	public Map<Integer, Set<Integer>> HierarchicalClustering(int end_num,final def_Link def) throws Exception
	{
		Map<Integer, Set<Integer>> t = new HashMap<Integer, Set<Integer>>();
			
		for(Integer k:this.input_data.keySet())//initialize sets
		{
			//in the beginning, set size is the same as number of data.
			Set<Integer> set_temp = new HashSet<Integer>();
			set_temp.add(k);
			t.put(k, set_temp);
		}
		Mergeset_Info m_info;
		while(t.keySet().size()!=end_num)
		{
			if(def==def_Link.Single)
				m_info = FindclosedSet_Single(t);
			else if (def==def_Link.Complete)
				m_info = this.FindclosedSet_Complete(t);
			else if (def==def_Link.Mean)
				m_info = this.FindclosedSet_Mean(t);
			else
				throw new Exception();
			MergeTwoSet(t,m_info);
		}
		if(def==def_Link.Single)
			System.out.println("Result of Single-Link hierarchical clustering");
		else if (def==def_Link.Complete)
			System.out.println("Result of Complete-Link hierarchical clustering");
		else if (def==def_Link.Mean)
			System.out.println("Result of Mean-Link hierarchical clustering");
		
		for(Integer j:t.keySet())
		{
			System.out.print(j+"cluster:");
			System.out.println(t.get(j).toString());
		}
		this.ThreeCenterCost(t);
		this.ThreeMeanCost(t);
		
		return t; 
	}

	/**
	 * Calculate distance between every pair, and distance between 2 sets is defined by the 
	 * longest link d(S1,S2)
	 * @param s original sets
	 * @return Mergeset_Info: Information of the closest 2 sets
	 */
	private Mergeset_Info FindclosedSet_Complete(Map<Integer, Set<Integer>> s)
	{
		Double min_d=Double.MAX_VALUE;
		//Double d=0.0;
		int set1_i =0,set2_i =0;
		Set<Integer> remain_set = new HashSet<Integer>(s.keySet());
		for(Integer i:s.keySet())
		{
			Set<Integer> set1=s.get(i);
			remain_set.remove(i);
			if(remain_set.size()==0) break;
			
			for(Integer j:remain_set)
			{	
				Set<Integer> set2=s.get(j);
				Double local_d = 0.0;
				for(Integer s1_ele:set1) //distance between 2 sets
				{
					for(Integer s2_ele:set2)
					{
						Double tmp_d = EuclideanD(this.input_data.get(s1_ele), this.input_data.get(s2_ele));
						if(local_d<tmp_d) //find max distance between set
							local_d = tmp_d;
					}
				}
				if(local_d<min_d)
				{
					min_d = local_d;
					set1_i = i;
					set2_i = j;
				}
			}
		}		
		return new Mergeset_Info(set1_i,set2_i,min_d); 
	}
	/**
	 * Calculate distance between every pair, and distance between 2 sets is defined by the 
	 * shortest link d(S1,S2)
	 * @param s original sets
	 * @return Mergeset_Info: Information of the closest 2 sets
	 */
	private Mergeset_Info FindclosedSet_Single(Map<Integer, Set<Integer>> s)
	{
		Double min_d=Double.MAX_VALUE;
		//Double d=0.0;
		int set1_i =0,set2_i =0;
		Set<Integer> remain_set = new HashSet<Integer>(s.keySet());
		for(Integer i:s.keySet())
		{
			Set<Integer> set1=s.get(i);
			remain_set.remove(i);
			if(remain_set.size()==0) break;
			
			for(Integer j:remain_set)
			{	
				Set<Integer> set2=s.get(j);
				Double local_d = Double.MAX_VALUE;
				for(Integer s1_ele:set1) //distance between 2 sets
				{
					for(Integer s2_ele:set2)
					{
						Double tmp_d = EuclideanD(this.input_data.get(s1_ele), this.input_data.get(s2_ele));
						if(tmp_d<local_d)
							local_d = tmp_d;
					}
				}
				//System.out.print(i+" and ");
				//System.out.println(j+":"+local_d);
				if(local_d<min_d)
				{
					min_d = local_d;
					set1_i = i;
					set2_i = j;
				}
			}
			
			
		}		
		return new Mergeset_Info(set1_i,set2_i,min_d); 
	}
	
	/**
	 * Calculate distance between every pair, d(S1,S2) = d(S1_average,S2_average)
	 * @param s original sets
	 * @return Mergeset_Info: Information of the closest 2 sets
	 */
	private Mergeset_Info FindclosedSet_Mean(Map<Integer, Set<Integer>> s)
	{
		Double min_d=Double.MAX_VALUE;
		//Double d=0.0;
		int set1_i =0,set2_i =0;
		Set<Integer> remain_set = new HashSet<Integer>(s.keySet());
		for(Integer i:s.keySet())
		{
			Set<Integer> set1=s.get(i);
			remain_set.remove(i);
			if(remain_set.size()==0) break;
			ArrayList<Double> set1Mean = this.setMean(set1, this.input_data);
			for(Integer j:remain_set)
			{	
				Set<Integer> set2=s.get(j);
				ArrayList<Double> set2Mean = this.setMean(set2, this.input_data);
				Double tmp_d = EuclideanD(set1Mean, set2Mean);
				
				//System.out.print(i+" and ");
				//System.out.println(j+":"+local_d);
				if(tmp_d<min_d)
				{
					min_d = tmp_d;
					set1_i = i;
					set2_i = j;
				}
			}		
		}		
		return new Mergeset_Info(set1_i,set2_i,min_d); 
	}

	/**
	 * Return average coordinate of one set
	 * @param index
	 * @param data
	 * @return average coordinate
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<Double> setMean(final Set<Integer> index, final Map<Integer, ArrayList<Double>> data)
	{
		ArrayList<Double> mean_array = new ArrayList<Double>();
		double num = 0.0;
		for(Integer i:index)
		{
			if(num==0 && data.get(i).getClass()==mean_array.getClass())
				mean_array = (ArrayList<Double>) data.get(i).clone();
			else
				for(int j=0;j<mean_array.size();j++)
				{
					mean_array.set(j,mean_array.get(j)+data.get(i).get(j));
				}
				
			num++;
		}
		if(num!=1) //calculate average
		{
			for(int j=0;j<mean_array.size();j++)
			{
				mean_array.set(j,mean_array.get(j)/num);
			}
		}
		return mean_array;
	}
	/**
	 * Merge two sets and update previous clusters
	 * @param set_need_merge
	 * @param merge_info
	 */
	private void MergeTwoSet(Map<Integer, Set<Integer>> set_need_merge,Mergeset_Info merge_info){
		Set<Integer> temp = new HashSet<Integer>();
		//create new integer set to add all elements in set1 and set2
		for(Integer i1:set_need_merge.get(merge_info.set1))
		{
			temp.add(i1);
		}
		for(Integer i2:set_need_merge.get(merge_info.set2))
		{
			temp.add(i2);
		}
		set_need_merge.remove(merge_info.set1);
		set_need_merge.remove(merge_info.set2);
		set_need_merge.put(merge_info.set1, temp);
	}

	/**
	
	 * Generated centers according to Gonzalez Algorithm and then do clustering 
	 * Each element is assigned to a cluster which minimize d(element,center of cluster)
	 * @param num_center how many clusters to generate
	 * @return clusters result in Map
	 * @throws Exception 
	 */
	public Map<Integer, Set<Integer>> Gonzalez(int num_center) throws Exception
	{
		Map<Integer, Set<Integer>> t = new HashMap<Integer, Set<Integer>>();
		List<Integer> center_ls = new ArrayList<Integer>();	
		Set<Integer> unused_key = new HashSet<Integer>(this.input_data.keySet());
		
		if(unused_key.size()==0)
			return null;
		
		center_ls.add(1);//default: Use first element as one center
		unused_key.remove(1);
		
		while(center_ls.size()!=num_center)
		{
			Double local_d = 0.0;
			int newcenter=0;
			int s2_ele = center_ls.get(center_ls.size()-1);
			for(Integer s1_ele:unused_key) //distance between 2 sets
			{	
				Double tmp_d = EuclideanD(this.input_data.get(s1_ele), this.input_data.get(s2_ele));
				if(local_d<tmp_d) //find max distance between precious center and other elemt
				{
					local_d = tmp_d;
					newcenter = s1_ele;
				}				
			}
			center_ls.add(newcenter);
			unused_key.remove(newcenter);
			//System.out.println(local_d);
		}
		
		for(Integer c:center_ls)
		{
			Set<Integer> set_c = new HashSet<Integer>();
			set_c.add(c);
			t.put(c, set_c);
		}
		
		for(Integer elmt:unused_key)
		{
			Double local_min = Double.MAX_VALUE;
			int belongto = 0; 
			for(Integer c:center_ls)
			{
				Double tmp_d = EuclideanD(this.input_data.get(c), this.input_data.get(elmt));
				if(local_min>tmp_d) //find max distance between precious center and other elemt
				{
					local_min = tmp_d;
					belongto = c;
				}
			}
			t.get(belongto).add(elmt);
		}
		
		for(Integer j:t.keySet())
		{
			System.out.print(j+" cluster:");
			System.out.println(t.get(j).toString());
		}
		//this.ThreeCenterCost(t);
		//this.ThreeMeanCost(t);
		return t; 
	}

	/**
	
	 * Calculate k center cost by find maximal distance inside a cluster
	 * Print center cost of each cluster 
	 * @param cluster results after clustering
	 * @return void
	 */
	private void ThreeCenterCost(final Map<Integer, Set<Integer>> cluster)
	{
		Double local_d = 0.0;
		for(Integer c:cluster.keySet())
		{
			local_d = 0.0;
			for(Integer s1_ele:cluster.get(c)) 
			{	
				Double tmp_d = EuclideanD(this.input_data.get(c), this.input_data.get(s1_ele));
				//find max distance between center and its belongings
				if(local_d<tmp_d) 
				{
					local_d = tmp_d;
				}				
			}
			System.out.println("3-center cost of cluster "+c+": "+local_d);
		}
	
	}

	/**
	
	 * Calculate k center cost by finding RMS distance inside a cluster
	 * Print RMS cost
	 * @param cluster results after clustering
	 * @param center coordinate of each center
	 * @return RMS cost
	 */
	private double ThreeMeanCost(final Map<Integer, Set<Integer>> cluster,final Map<Integer, ArrayList<Double>> center)
	{
		Double sum = 0.0;
		Double amount = (double) this.input_data.keySet().size();
		if(!cluster.keySet().equals(center.keySet()))
			System.out.println("Key Sets are not the same");
		
		
		for(Integer c:center.keySet())
		{
			ArrayList<Double> center_cord = new ArrayList<Double>(center.get(c));
			Set<Integer> members = new HashSet<Integer>(cluster.get(c));
			for(Integer s1_ele:members) 
			{	
				Double tmp_d = EuclideanD(center_cord, this.input_data.get(s1_ele));
				sum+=tmp_d * tmp_d;		
			}
		}
		sum = Math.sqrt(sum/amount);
		System.out.println(cluster.keySet().size()+"-Mean cost of cluster: "+ sum);
		return sum;
	
	}

	/**
	
	 * Randomly choose <code>num_center</code> integers from data
	 * @param num_center how many clusters to generate
	 * @return clusters result
	 * @throws Exception 
	 */
	public Set<Integer> kmeanspp_set(int num_center) throws Exception
	{
		Set<Integer> center_set = new HashSet<Integer>();	
		Set<Integer> unused_key = new HashSet<Integer>(this.input_data.keySet());
		
		if(unused_key.size()==0)
			return null;
		
		//Select centers randomly
		Random ran = new Random();
		int size = unused_key.size();
		int r;
		
		while(center_set.size()!=num_center)
		{
			r=ran.nextInt(size)+1;
			//if next random point is too close, it is barely possible to use it as one center
			if(!center_set.contains(r) && unused_key.contains(r)) 
			{
				center_set.add(r);
				unused_key.remove(r);
			}
		}
		
		return center_set; 
	}

	/**
	
	 * Generated clusters according to single-link algorithm
	 * end_num: # of clusters in the end
	 * @param end_num
	 * @return clusters result in Map
	 * @throws Exception 
	 */
	public Cluster_Info Lloyds(final Set<Integer> center) throws Exception
	{
		Map<Integer, Set<Integer>> t = new HashMap<Integer, Set<Integer>>();
		Map<Integer, Set<Integer>> to_update = new HashMap<Integer, Set<Integer>>();	
		Set<Integer> all_key = new HashSet<Integer>(this.input_data.keySet());
		int k = center.size();
		if(k==0)
			return null;
		Map<Integer, ArrayList<Double>> center_cord = new HashMap<Integer, ArrayList<Double>>();
		
		//initialize center
		for(Integer c:center)
		{
			center_cord.put(c,this.input_data.get(c));
			Set<Integer> init = new HashSet<Integer>();
			init.add(c);
			t.put(c, init);
		}
		
		int counter = 0;
		while(!t.equals(to_update))
		{		
			counter++;
			to_update.clear();
			//save previous result
			for(Integer center_name:t.keySet())
			{
				to_update.put(center_name, new HashSet<Integer>(t.get(center_name)));
				t.get(center_name).clear();
			}
			for(Integer elmt:all_key)//all elements
			{
				Double local_min = Double.MAX_VALUE;
				int belongto = 0; 
				for(Integer c_index:t.keySet())
				{
					Double tmp_d = EuclideanD(center_cord.get(c_index), this.input_data.get(elmt));
					if(tmp_d < local_min) //find max distance between precious center and other elemt
					{
						local_min = tmp_d;
						belongto = c_index;
					}
				}
				t.get(belongto).add(elmt);
			}
			for(Integer c_index:t.keySet())
			{
				ArrayList<Double> Mean_cord = new ArrayList<Double>(this.setMean(t.get(c_index), this.input_data));
				if(Mean_cord.size()==0)
				{
					System.out.println("Set is Empty!");
					t.remove(c_index);
					center_cord.remove(c_index);
					break;
				}
				else
				center_cord.put(c_index, Mean_cord);
			}
			if(t.size()<k)
				break;
		}
		
		System.out.println("Takes "+counter+" runs to converge.");
		/*
		for(Integer j:t.keySet())
		{
			System.out.print("cluster name:"+j);
			System.out.println(t.get(j).toString());
		}
		*/
		//this.ThreeCenterCost(t);
		//this.ThreeMeanCost(t);
		Cluster_Info info = new Cluster_Info(t.keySet(),this.ThreeMeanCost(t,center_cord));
		info.setClusters(t);
		
		return info;
	}

	/**
	
	 * Calculate k center cost by find maximal distance inside a cluster
	 * Print center cost of each cluster 
	 * @param cluster results after clustering
	 * @return void
	 */
	private double ThreeMeanCost(final Map<Integer, Set<Integer>> cluster)
	{
		Double sum = 0.0;
		Double amount = (double) this.input_data.keySet().size();
		//System.out.println("num of data: " +amount);
		for(Integer c:cluster.keySet())
		{
			//sum = 0.0;
			for(Integer s1_ele:cluster.get(c)) 
			{	
				Double tmp_d = EuclideanD(this.input_data.get(c), this.input_data.get(s1_ele));
				sum+=tmp_d * tmp_d;		
			}
		}
		sum = Math.sqrt(sum/amount);
		System.out.println(cluster.keySet().size()+"-Mean cost of cluster: "+ sum);
		return sum;
	
	}

	/**
	
	 * Generated clusters according to single-link algorithm
	 * end_num: # of clusters in the end
	 * @param end_num
	 * @return clusters result in Map
	 * @throws Exception 
	 */
	public Cluster_Info kmedian(final Set<Integer> center, final int trials) throws Exception
	{
		Map<Integer, Set<Integer>> t = new HashMap<Integer, Set<Integer>>();
		//Map<Integer, Set<Integer>> to_update = new HashMap<Integer, Set<Integer>>();	
		Set<Integer> all_key = new HashSet<Integer>(this.input_data.keySet());
		int k = center.size();
		if(k==0)
			return null;
		Map<Integer, ArrayList<Double>> center_cord = new HashMap<Integer, ArrayList<Double>>();
		
		//initialize center
		for(Integer c:center)
		{
			center_cord.put(c,this.input_data.get(c));
		}
		
		int counter = 0;
		while(true)
		{		
			counter++;
			t.clear();
			//update center
			for(Integer center_name:center_cord.keySet())
			{
				t.put(center_name, new HashSet<Integer>());
			}
			for(Integer elmt:all_key)//all elements
			{
				Double local_min = Double.MAX_VALUE;
				int belongto = 0; 
				for(Integer c_index:center_cord.keySet())
				{
					Double tmp_d = EuclideanD(center_cord.get(c_index), this.input_data.get(elmt));
					if(tmp_d < local_min) //find max distance between precious center and other elemt
					{
						local_min = tmp_d;
						belongto = c_index;
					}
				}
				t.get(belongto).add(elmt);
			}
			for(Integer c_index:t.keySet())
			{
				if(t.get(c_index).isEmpty())
				{
					System.out.println("Set is Empty!");
					t.remove(c_index);
					center_cord.remove(c_index);
					break;
				}
				int m = this.FindMedian_index(t.get(c_index));
				if(m!=c_index)
				{
					center_cord.remove(c_index);
					center_cord.put(m, this.input_data.get(m));
				}
			}
			
			System.out.print(counter+"th cost:");
			this.Cost_1(t);
			// k-1 clusters or result converge
			if(t.size()<k || center_cord.keySet().equals(t.keySet()) || counter >trials)
				break;
		}
		
		System.out.println("Takes "+counter+" runs to converge.");
		
		for(Integer j:t.keySet())
		{
			System.out.print("cluster name:"+j);
			System.out.println(t.get(j).toString());
		}
		this.Cost_1(t);
		//this.ThreeCenterCost(t);
		//this.ThreeMeanCost(t);
		Cluster_Info info = new Cluster_Info(t.keySet(),0);
		info.setClusters(t);
		
		return info;
	}

	private double FindMedianofDim(final Set<Integer> index,final int Dim)
	{
		Map<Double, Integer> map_d_2_index  = new HashMap<Double, Integer>();
		ArrayList<Double> ave_array = new ArrayList<Double>();
		double avg_i= 0.0;
		int size = index.size();
		for(Integer i:index)
		{
			avg_i = this.input_data.get(i).get(Dim);
			map_d_2_index.put(avg_i, i);
		}
		ave_array.addAll(map_d_2_index.keySet());
		Collections.sort(ave_array);
		//median_index = map_d_2_index.get(ave_array.get((size-1)/2));
		return ave_array.get((size-1)/2);
	}

	/**
	
	 * Calculate Cost_1 
	 * @param cluster results after clustering
	 * @return double
	 */
	private double Cost_1(final Map<Integer, Set<Integer>> cluster)
	{
		Double sum = 0.0;
		Double amount = (double) this.input_data.keySet().size();
		//System.out.println("num of data: " +amount);
		for(Integer c:cluster.keySet())
		{
			//sum = 0.0;
			for(Integer s1_ele:cluster.get(c)) 
			{	
				Double tmp_d = EuclideanD(this.input_data.get(c), this.input_data.get(s1_ele));
				sum+=tmp_d;	
			}
		}
		sum = sum/amount;
		//System.out.println("Cost_1 of "+cluster.keySet().size()+" clusters: "+ sum);
		return sum;
	
	}
	private int FindMedian_index(final Set<Integer> index)
	{
		Map<Double, Integer> map_d_2_index  = new HashMap<Double, Integer>();
		ArrayList<Double> ave_array = new ArrayList<Double>();
		double avg_i= 0.0;
		int median_index = 0;
		int size = index.size();
		for(Integer i:index)
		{
			avg_i = this.data_dim_ave.get(i);
			//ave_array.add(avg_i);
			map_d_2_index.put(avg_i, i);
		}
		ave_array.addAll(map_d_2_index.keySet());
		Collections.sort(ave_array);
		median_index = map_d_2_index.get(ave_array.get((size-1)/2));
		return median_index;
	}
	/**
	
	 * Generated clusters according to single-link algorithm
	 * end_num: # of clusters in the end
	 * @param end_num
	 * @return clusters result in Map
	 * @throws Exception 
	 */
	public Cluster_Info KMedian_2(final Set<Integer> center,int trials) throws Exception
	{
		Map<Integer, Set<Integer>> t = new HashMap<Integer, Set<Integer>>();
		Map<Integer, Set<Integer>> to_update = new HashMap<Integer, Set<Integer>>();	
		Set<Integer> all_key = new HashSet<Integer>(this.input_data.keySet());
		int k = center.size();
		int dimension = 0;
		if(k==0)
			return null;
		Map<Integer, ArrayList<Double>> center_cord = new HashMap<Integer, ArrayList<Double>>();
		
		//initialize center
		for(Integer c:center)
		{
			if(dimension==0)
				dimension = this.input_data.get(c).size();
			center_cord.put(c,this.input_data.get(c));
			Set<Integer> init = new HashSet<Integer>();
			init.add(c);
			t.put(c, init);
		}
		
		int counter = 0;
		while(!t.equals(to_update))
		{		
			counter++;
			to_update.clear();
			//save previous result
			for(Integer center_name:t.keySet())
			{
				to_update.put(center_name, new HashSet<Integer>(t.get(center_name)));
				t.get(center_name).clear();
			}
			for(Integer elmt:all_key)//all elements
			{
				Double local_min = Double.MAX_VALUE;
				int belongto = 0; 
				for(Integer c_index:t.keySet())
				{
					Double tmp_d = EuclideanD(center_cord.get(c_index), this.input_data.get(elmt));
					if(tmp_d < local_min) //find max distance between precious center and other elemt
					{
						local_min = tmp_d;
						belongto = c_index;
					}
				}
				t.get(belongto).add(elmt);
			}
			for(Integer c_index:t.keySet())
			{
				if(t.get(c_index).size()==0)
				{
					System.out.println("Set is Empty!");
					t.remove(c_index);
					center_cord.remove(c_index);
					break;
				}
				ArrayList<Double> Median_cord = new ArrayList<Double>();
				for(int dim=0;dim<dimension;dim++ )
				{
					Median_cord.add(this.FindMedianofDim(t.get(c_index), dim));
				}
				
				center_cord.put(c_index, Median_cord);
			}
			
			if(t.size()<k )
//					|| counter > trials)
				break;
		}
		
		//System.out.println("Takes "+counter+" runs to converge.");
		
//		for(Integer j:t.keySet())
//		{
//			System.out.print("cluster name:"+j);
//			System.out.println(t.get(j).toString());
//		}
		
		Cluster_Info info = new Cluster_Info(t.keySet(),this.Cost_1(t,center_cord));
		info.setClusters(t);
		info.setCenter_Cord(center_cord);
		
		return info;
	}
	/**
	
	 * Generated clusters according to k-means++ algorithm
	 * @param num_center how many clusters to generate
	 * @return clusters result
	 * @throws Exception 
	 */
	public Cluster_Info kmeansplus(int num_center) throws Exception
	{
		Map<Integer, Set<Integer>> t = new HashMap<Integer, Set<Integer>>();
		List<Integer> center_ls = new ArrayList<Integer>();	
		Set<Integer> unused_key = new HashSet<Integer>(this.input_data.keySet());
		
		if(unused_key.size()==0)
			return null;
		
		//Select centers randomly
		Random ran = new Random();
		int size = unused_key.size();
		int r;
		center_ls.add(1);//default: Use first element as one center
		unused_key.remove(1);
		
		while(center_ls.size()!=num_center)
		{
			r=ran.nextInt(size)+1;
			//if next random point is too close, it is barely possible to use it as one center
			if(!center_ls.contains(r) ) //&& 
				//EuclideanD(this.input_data.get(center_ls.get(center_ls.size()-1)), this.input_data.get(r)) > 10 )
			{
				center_ls.add(r);
				unused_key.remove(r);
			}
		}
	
		//initialize clusters
		for(Integer c:center_ls)
		{
			Set<Integer> set_c = new HashSet<Integer>();
			set_c.add(c);
			t.put(c, set_c);
		}
		
		for(Integer elmt:unused_key)
		{
			Double local_min = Double.MAX_VALUE;
			int belongto = 0; 
			for(Integer c:center_ls)
			{
				Double tmp_d = EuclideanD(this.input_data.get(c), this.input_data.get(elmt));
				if(local_min>tmp_d) //find max distance between precious center and other elemt
				{
					local_min = tmp_d;
					belongto = c;
				}
			}
			t.get(belongto).add(elmt);
		}
		
		//for(Integer j:t.keySet())
		//{
			//System.out.print("cluster key:"+t.keySet());
			//System.out.println(t.get(j).toString());
		//}
		Cluster_Info k = new Cluster_Info(t.keySet(),this.ThreeMeanCost(t));
		k.setClusters(t);
		
		return k; 
	}
	/**
	
	 * Calculate Cost_1 
	 * @param cluster results after clustering
	 * @return double
	 */
	private double Cost_1(final Map<Integer, Set<Integer>> cluster,final Map<Integer, ArrayList<Double>> center )
	{
		Double sum = 0.0;
		Double amount = (double) this.input_data.keySet().size();
		for(Integer c:center.keySet())
		{
			for(Integer s1_ele:cluster.get(c)) 
			{	
				Double tmp_d = EuclideanD(center.get(c), this.input_data.get(s1_ele));
				sum+=tmp_d;	
			}
		}
		sum = sum/amount;
		//System.out.println("Cost_1 of "+cluster.keySet().size()+" clusters: "+ sum);
		return sum;
	
	}
	/**
		
		 * Verification 
		 * @param center
		 * @param data
		 * @return clusters result in Map
		 * @throws Exception 
		 */
		public static Map<Integer, Set<Integer>> KMedian_Validation(final Map<Integer, ArrayList<Double>> center, final Map<Integer, ArrayList<Double>> data) throws Exception
		{
			Map<Integer, Set<Integer>> t = new HashMap<Integer, Set<Integer>>();//result	
			Set<Integer> all_key = new HashSet<Integer>(data.keySet());
			
			//initialize center
			for(Integer c:center.keySet())
			{
				t.put(c, new HashSet<Integer>());
			}
			for(Integer elmt:all_key)//all elements
			{
				Double local_min = Double.MAX_VALUE;
				int belongto = 0; 
				for(Integer c_index:center.keySet())
				{
					Double tmp_d = EuclideanD(center.get(c_index), data.get(elmt));
					if(tmp_d < local_min) //find max distance between precious center and other elemt
					{
						local_min = tmp_d;
						belongto = c_index;
					}
				}
				t.get(belongto).add(elmt);
			}
				
//			Cluster_Info info = new Cluster_Info(t.keySet(),this.Cost_1(t,center_cord));
//			info.setClusters(t);
//			info.setCenter_Cord(center_cord);
			
			return t;
		}
	public static void Q2_Lloyds() throws Exception
	{
		cluster C2= new cluster("txt/C2.txt");
		System.out.println("Load file done");

		Set<Integer> Lloyds_center = new HashSet<Integer>();
		
		Lloyds_center.add(1);Lloyds_center.add(2);Lloyds_center.add(3);
		Map<Integer, Set<Integer>> C2_Lloyds = C2.Lloyds(Lloyds_center).getCluster();
		clusterPlot C2_chart_Lloyds = new clusterPlot("C2 Lloyds with C{1,2,3}", C2_Lloyds, C2.data(), false);
		
		C2_chart_Lloyds.pack( );          
		RefineryUtilities.centerFrameOnScreen( C2_chart_Lloyds );          
		C2_chart_Lloyds.setVisible( true );
		
		//2
		Lloyds_center.clear();
		//Lloyds_center.addAll(C2_Gon.keySet());
		Lloyds_center.add(1);
		Lloyds_center.add(949);
		Lloyds_center.add(1004);
		Map<Integer, Set<Integer>> C2_Lloyds_G = C2.Lloyds(Lloyds_center).getCluster();
		
		clusterPlot C2_chart_Lloyds_G = new clusterPlot("C2 Lloyds with C{1, 949, 1004}", C2_Lloyds_G, C2.data(), false);
		C2_chart_Lloyds_G.pack( );          
		RefineryUtilities.centerFrameOnScreen( C2_chart_Lloyds_G );          
		C2_chart_Lloyds_G.setVisible( true );

		
	}

	public static void Q1() throws Exception
	{
		//Q1-A
	
		cluster C1= new cluster("txt/C1.txt");
				
		Map<Integer, Set<Integer>> C1_mean = C1.HierarchicalClustering(4, def_Link.Mean);
		clusterPlot C1_chart3 = new clusterPlot("C1 Mean link", C1_mean,C1.data());
				
		Map<Integer, Set<Integer>> C1_result = C1.HierarchicalClustering(4,def_Link.Single);
		clusterPlot C1_chart1 = new clusterPlot("C1 single link", C1_result,C1.data());
				
		Map<Integer, Set<Integer>> C1_com = C1.HierarchicalClustering(4, def_Link.Complete);
		clusterPlot C1_chart2 = new clusterPlot("C1 complete link", C1_com,C1.data());
				
		//C1_plot
		C1_chart1.pack( );          
		RefineryUtilities.centerFrameOnScreen( C1_chart1 );          
		C1_chart1.setVisible( true );
		C1_chart2.pack( );          
		RefineryUtilities.positionFrameRandomly( C1_chart2 );          
		C1_chart2.setVisible( true );
		C1_chart3.pack( );          
		RefineryUtilities.positionFrameRandomly( C1_chart3 );          
		C1_chart3.setVisible( true );	
	}

	public static void Q3() throws Exception
	{
		//Q3
			int dim = 4;
		System.out.println("d="+dim+": "+HighDimension(dim));
		List<ArrayList<Double>> factor_c = new ArrayList<ArrayList<Double>>();
		for(int i=2;i<=20;i+=2)
		{
			ArrayList<Double> co = new ArrayList<Double>();
			co.add((double)i);
			co.add(HighDimension(i));
			factor_c.add(co);
		}
		XY_LinePlot Q3_chart_K = new XY_LinePlot("High-dimensional Distance",
			"factor d vs c","d","c", factor_c);
				
		Q3_chart_K.pack( );          
		RefineryUtilities.centerFrameOnScreen( Q3_chart_K );          
		Q3_chart_K.setVisible( true );		
	}

	/**	
	 * Calculate factor c
	 * @param end_num
	 * @return clusters result in Map
	 * @throws Exception 
	 */
	public static Double HighDimension(int d)
	{
		if(d%2!=0) return 0.0; //not even number
		Double c = 0.0;
		Double div_pi_sqrt=2/Math.sqrt(Math.PI);
		
		//double gamma = Math.
		c = div_pi_sqrt*Math.pow(factorial(d/2), 1/(double)d);
				
		return c;
	}
	
	public static int factorial(int f)
	{
		int c = 1;
		for(int i=2;i<=f;i++)
		{
			c= c*i;
		}
				
		return c;
	}
	public static void Q2_CDF() throws Exception
	{
		cluster C2= new cluster("txt/C2.txt");
		System.out.println("Load file done");
		Set<Integer> KM_center = new HashSet<Integer>();
		List<ArrayList<Double>> xydata = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> sort_cost = new ArrayList<Double>();
		int count = 0;
		double equal =0;
		double trials = 2000;
		while(count<trials)
		{
			System.out.println("kmeans result");
			Cluster_Info C2_KM= C2.kmeansplus(3);
			KM_center.addAll(C2_KM.getCenters());
			
			System.out.println("Lloyds result");
			Cluster_Info C2_Ll = C2.Lloyds(KM_center);
			Map<Integer, Set<Integer>> C2_Lloyds_KM = C2_Ll.getCluster();
			sort_cost.add(C2_Ll.getMeansCost());
			if(C2_KM.getCluster().equals(C2_Lloyds_KM))
			{
				equal++;
				System.out.println("kmeans Cluster");
				for(Integer j:C2_KM.getCenters())
				{
					System.out.print("cluster name:"+j);
					System.out.println(C2_KM.getCluster().get(j).toString());
				}
				
				System.out.println("Lloyds Cluster");
				for(Integer j:C2_Ll.getCenters())
				{
					System.out.print("cluster name:"+j);
					System.out.println(C2_Ll.getCluster().get(j).toString());
				}
			}
			count++;
			KM_center.clear();
			System.out.println(" ");
		}
		Collections.sort(sort_cost);
		
		for(int i=1;i<=sort_cost.size();i++)
		{
			ArrayList<Double> co = new ArrayList<Double>();
			co.add(sort_cost.get(i-1));
			co.add(i/trials);
			xydata.add(co);
		}
		System.out.println("Subsets are the same: "+ equal);
		System.out.println("Fraction: "+ equal/trials);
		XY_LinePlot C2_chart03 = new XY_LinePlot("CDF",
				"C2 Lloyds use K-mean++ Result","cost","CDF(cost)", xydata);
		
		C2_chart03.pack( );          
		RefineryUtilities.centerFrameOnScreen( C2_chart03 );          
		C2_chart03.setVisible( true );
		
	}
	
	public static void Q2_A() throws Exception
	{
		cluster C2= new cluster("txt/C2.txt");
		System.out.println("Load file done");
		
		//Q2-A-Gonzalez
		
		Map<Integer, Set<Integer>> C2_Gon = C2.Gonzalez(3);
		
		clusterPlot C2_chart_Gon = new clusterPlot("C2 Gonzalez", C2_Gon,C2.data(), false);
		C2_chart_Gon.pack( );          
		RefineryUtilities.centerFrameOnScreen( C2_chart_Gon );          
		C2_chart_Gon.setVisible( true );
		
		
		//Q2-A-K-means++
		List<ArrayList<Double>> xydata = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> sort_cost = new ArrayList<Double>();
		int count = 0;
		double equal =0;
		double trials = 200;
		while(count<trials)
		{
			Cluster_Info C2_KM= C2.kmeansplus(3);
			sort_cost.add(C2_KM.getMeansCost());
			//if(C2_KM.getCenters().equals(C2_Gon.keySet()))
			//	equal++;
			count++;
		}
		Collections.sort(sort_cost);
		
		for(int i=1;i<=sort_cost.size();i++)
		{
			ArrayList<Double> co = new ArrayList<Double>();
			co.add(sort_cost.get(i-1));
			co.add(i/trials);
			xydata.add(co);
		}
		System.out.println("Centers are the same:"+ equal);
		XY_LinePlot C2_chart_K = new XY_LinePlot("C2 Kmeans++", xydata);
		
		C2_chart_K.pack( );          
		RefineryUtilities.centerFrameOnScreen( C2_chart_K );          
		C2_chart_K.setVisible( true );
		
	}
	public static void main(String[] args) throws Exception
	{
		//Q4
		cluster C3= new cluster("txt/C3.txt",true);
		System.out.println("Load file done");
		
		Map<Integer,Set<Integer>> best_cluster = null;
		Map<Integer,ArrayList<Double>> best_result = null;
		double min_cost = Double.MAX_VALUE;
		for(int i=0;i<200;i++)
		{	
			Set<Integer> center = C3.kmeanspp_set(5); 
			Cluster_Info C3_a =C3.KMedian_2(center,50);
			if(C3_a.getMeansCost()<min_cost)
			{
				min_cost = C3_a.getMeansCost();
				best_result = C3_a.getCenter_Cord();
				best_cluster = C3_a.getCluster();
			}
		}
		
		System.out.println("Min Cost1: "+min_cost);
		best_result.forEach((index,cord)->System.out.println("Cluster Name: " + index + " Coordinate: " + cord));
		best_cluster.forEach((index,ele)->System.out.println("Cluster Name: " + index + " elements: " + ele));
		
		
		System.out.println("Verification:");
		Map<Integer,Set<Integer>> v_cluster =  KMedian_Validation(best_result, C3.data()) ;
		
		if(v_cluster.equals(best_cluster))
			System.out.println("GJ!");
		//save png
	    /*
	    int width = 640;
	    int height = 480;
	    File BarChart = new File( "C1_chart.png" ); 
	    ChartUtilities.saveChartAsPNG( BarChart , C1_chart.ScatterChart() , width , height );
	    */

		
		
		

	}

}
