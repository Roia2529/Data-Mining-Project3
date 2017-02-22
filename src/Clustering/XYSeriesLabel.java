package Clustering;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.PublicCloneable;

//import datamining01.Birthdaypara;

import java.io.IOException;

public class XYSeriesLabel extends XYSeries
{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Hashtable<ArrayList<Double>,String> label_present = new Hashtable<ArrayList<Double>,String>();
	//private data

	public XYSeriesLabel(Comparable key) {
        super(key, true, true);
    }

    
    public void addLabel(ArrayList<Double> list,String label) {
        
    	label_present.put(list, label);
    }
    
    public String getLabel(ArrayList<Double> key) {
        
    	return label_present.get(key);
    }

}


