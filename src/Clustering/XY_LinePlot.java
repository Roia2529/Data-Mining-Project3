package Clustering;

import java.util.List;
import java.util.Map;
import java.nio.charset.Charset;

import java.util.ArrayList;
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

public class XY_LinePlot extends ApplicationFrame{
    
	private static final long serialVersionUID = 1L;
	private JFreeChart xyLineChart = null;
	/**
	 * Plot Scatter data on XY-plane, color of element represent which cluster it belongs to 
	 * @param title 
	 * @param xydata
	 * @throws Exception 
	 */
	public XY_LinePlot(String title, final List<ArrayList<Double>> xydata) throws Exception
	{
		 super(title);
		 
		 if(xydata.get(0).size()!=2)
			 throw new Exception("Cant plot on XY plane");
		 
		 XYSeriesCollection seriesdata = new XYSeriesCollection();
		 XYSeries elmt_cord=new XYSeriesLabel("xydata");
		 for(ArrayList<Double> point:xydata)
		 {
			elmt_cord.add(point.get(0),point.get(1));
		 }
		 seriesdata.addSeries(elmt_cord);
		 this.xyLineChart = ChartFactory.createXYLineChart(
		         "Clustering" ,
		         "X" ,
		         "Y" ,
		         seriesdata ,
		         PlotOrientation.VERTICAL ,
		         true , true , false);

		 ChartPanel chartPanel = new ChartPanel( xyLineChart );
		 chartPanel.setPreferredSize( new java.awt.Dimension( 640 , 480 ) );

		 setContentPane( chartPanel ); 
	}
	
	public JFreeChart ScatterChart()
	{
		return this.xyLineChart;
	}

}


