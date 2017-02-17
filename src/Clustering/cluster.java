package Clustering;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.io.BufferedReader;
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

import java.io.IOException;
import Clustering.clusterPlot;

public class cluster{
	class mergeset{
		public int set1;
		public int set2;
		public double distance;
		mergeset(int s1,int s2,double d)
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
	
	public Map<Integer, ArrayList<Double> > C1 = new HashMap<Integer, ArrayList<Double>>();
	public Map<Integer, ArrayList<Double> > C2 = new HashMap<Integer, ArrayList<Double>>();
	public Map<Integer, ArrayList<Double> > C3 = new HashMap<Integer, ArrayList<Double>>();
	
	public cluster(String path)
	{
		this.C1 = readdata(path);
	}
	
	public cluster(String path1, String path2, String path3)
	{
		this.C1 = readdata(path1);
		this.C2 = readdata(path2);
		this.C3 = readdata(path3);
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	private HashMap<Integer, ArrayList<Double>> readdata(String path)
	{
		String line;
		String[] result = null;
		int item=0;
		int index = 0;
		ArrayList<Double> datalist = new ArrayList<Double>();
		HashMap<Integer, ArrayList<Double> > datamap = new HashMap<Integer, ArrayList<Double> >(); 

		try{
		    InputStream fis = new FileInputStream(path);
		    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		    BufferedReader br = new BufferedReader(isr);
		    while ((line = br.readLine()) != null)
			{
		    	result = line.split("\t");
		    	item = 0;
		    	
		    	System.out.println(result[2]);
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


	
	public static Double EuclideanD(ArrayList<Double> point1, ArrayList<Double> point2)
	{
		if(point1.size()!=point2.size())
			System.out.println("dimension are different!");
		
		Double squaresum=0.0;
		for(int i=0;i<point1.size();i++)
		{
			squaresum+=Math.pow(point1.get(i)-point2.get(i),2);
		}
		
		return Math.sqrt(squaresum); 
	}
	
	public Map<Integer, Set<Integer>> SingleLink(int end_num)
	{
		Map<Integer, ArrayList<Double>> temp = this.C1;
		Map<Integer, Set<Integer>> t = new HashMap<Integer, Set<Integer>>();
		Set<Integer> set_temp = new HashSet<Integer>();
		
		for(Integer k:temp.keySet())//initialize sets
		{
			//in the beginning, set size is the same as number of data.
			set_temp.add(k);
			t.put(k, set_temp);
			set_temp.clear();
		}
		ArrayList<Double> point_start= this.C1.get(1);
		while(t.size()!=end_num)
		{
			
		}
		
		
		return t; 
	}
	
	public mergeset FindclosedSet(Map<Integer, Set<Integer>> s)
	{
		Map<Integer, ArrayList<Double>> temp = this.C1;
		
		
		
		return new mergeset(1,2,5.0); 
	}


	public static void main(String[] args) throws IOException
	{
		
		//Q1-A
		cluster Q1= new cluster("txt/C1.txt");
	
		
		
		
		
	}

}
