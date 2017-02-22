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

public class LegendXYItemLabelGenerator extends StandardXYItemLabelGenerator
implements XYItemLabelGenerator, Cloneable, PublicCloneable,
Serializable {
    
	private LegendItemCollection legendItems;
	//private data

    public LegendXYItemLabelGenerator(LegendItemCollection legendItems) {
        super();
        this.legendItems = legendItems;
    }

    @Override
    public String generateLabel(XYDataset dataset, int series, int item) {
        LegendItem legendItem = legendItems.get(series);
        XYSeries series_t = ((XYSeriesCollection)dataset).getSeries(series); 
        ArrayList<Double> list = new ArrayList<Double>();
        list.add((Double) series_t.getX(item));
        list.add((Double) series_t.getY(item));
        return(( XYSeriesLabel)series_t).getLabel(list);
        //System.out.println(item);
        //return legendItem.getLabel();
    }

}


