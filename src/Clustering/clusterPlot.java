package Clustering;

import java.util.List;
import java.util.Map;
import java.nio.charset.Charset;

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
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import Clustering.LegendXYItemLabelGenerator;
import Clustering.XYSeriesLabel;

public class clusterPlot extends ApplicationFrame{
    
	private static final long serialVersionUID = 1L;
	private JFreeChart xyScatterChart = null;
	/**
	 * Plot Scatter data on XY-plane, color of element represent which cluster it belongs to 
	 * @param title
	 * @param cluster
	 * @param data
	 */
	public clusterPlot(String title, final Map<Integer, Set<Integer>> cluster, 
			final Map<Integer, ArrayList<Double>> data)
	{
		 super(title);
		 XYSeriesCollection seriesdata = new XYSeriesCollection();

		 for(Integer i:cluster.keySet())
		 {
			 Set<Integer> set_elmt = cluster.get(i);
			 XYSeries elmt_cord=new XYSeriesLabel(i);
			 for(Integer elmt:set_elmt)
			 {
				 elmt_cord.add(data.get(elmt).get(0), data.get(elmt).get(1));
				 ((XYSeriesLabel) elmt_cord).addLabel(data.get(elmt),elmt.toString());
			 }
			 seriesdata.addSeries(elmt_cord);
		 }
		 this.xyScatterChart = ChartFactory.createScatterPlot(
		         "Clustering" ,
		         "X" ,
		         "Y" ,
		         seriesdata ,
		         PlotOrientation.VERTICAL ,
		         true , true , false);
		   
		 
		XYPlot plot = (XYPlot)this.xyScatterChart.getPlot();
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		renderer.setBaseItemLabelsVisible(true);
		renderer.setBaseItemLabelGenerator(new LegendXYItemLabelGenerator(plot.getLegendItems()));
		
		 ChartPanel chartPanel = new ChartPanel( xyScatterChart );
		 chartPanel.setPreferredSize( new java.awt.Dimension( 640 , 480 ) );

		 setContentPane( chartPanel ); 
	}
	
	public JFreeChart ScatterChart()
	{
		return this.xyScatterChart;
	}

}


