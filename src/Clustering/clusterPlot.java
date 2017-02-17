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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;


//import datamining01.Birthdaypara;

import java.io.IOException;

public class clusterPlot extends ApplicationFrame{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public clusterPlot()
	{
		 super("Min Hashing");
		 JFreeChart xylineChart = ChartFactory.createXYLineChart(
		         "times vs accuracy" ,
		         "t" ,
		         "error" ,
		         data ,
		         PlotOrientation.VERTICAL ,
		         true , true , false);
		         
		 ChartPanel chartPanel = new ChartPanel( xylineChart );
		 chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );

		 setContentPane( chartPanel ); 
	}


}
