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
	
	public cluster(String path)
	{
		this.input_data = readdata(path);
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
	public Double EuclideanD(final ArrayList<Double> point1, final ArrayList<Double> point2)
	{
		try {
			if(point1.size() != point2.size())
				throw new Exception();
				
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("dimension are different!");
		}
		
		Double squaresum=0.0;
		for(int i=0;i<point1.size();i++)
		{
			double d= (double)point1.get(i);
			squaresum+=Math.pow(d-point2.get(i),2);
		}
		
		return Math.sqrt(squaresum); 
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

	private ArrayList<Double> setMean(final Set<Integer> index, final Map<Integer, ArrayList<Double>> data)
	{
		ArrayList<Double> mean_array = new ArrayList<Double>();
		double num = 0.0;
		for(Integer i:index)
		{
			if(num==0)
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

	public static void main(String[] args) throws Exception
	{
		
		//Q1-A
		cluster C1= new cluster("txt/C1.txt");
		cluster C2= new cluster("txt/C2.txt");
		System.out.println("Load file done");
		
		Map<Integer, Set<Integer>> C1_mean = C1.HierarchicalClustering(4, def_Link.Mean);
		//clusterPlot C1_chart3 = new clusterPlot("C1 Mean link", C1_mean,C1.data());
		
		Map<Integer, Set<Integer>> C1_result = C1.HierarchicalClustering(4,def_Link.Single);
		clusterPlot C1_chart1 = new clusterPlot("C1 single link", C1_result,C1.data());
		
	    Map<Integer, Set<Integer>> C1_com = C1.HierarchicalClustering(4, def_Link.Complete);
		//clusterPlot C1_chart2 = new clusterPlot("C1 complete link", C1_com,C1.data());
		
		
		//C1_plot
		C1_chart1.pack( );          
		RefineryUtilities.centerFrameOnScreen( C1_chart1 );          
		C1_chart1.setVisible( true );
		/*
		C1_chart2.pack( );          
	    RefineryUtilities.positionFrameRandomly( C1_chart2 );          
	    C1_chart2.setVisible( true );
	    C1_chart3.pack( );          
	    RefineryUtilities.positionFrameRandomly( C1_chart3 );          
	    C1_chart3.setVisible( true );
	    
	    //save png
	    /*
	    int width = 640;
	    int height = 480;
	    File BarChart = new File( "C1_chart.png" ); 
	    ChartUtilities.saveChartAsPNG( BarChart , C1_chart.ScatterChart() , width , height );
	    */

	}

}
