/* ==================
 * BarChartDemo1.java
 * ==================
 *
 * Copyright (c) 2005-2017, Object Refinery Limited.
 * All rights reserved.
 *
 * http://www.jfree.org/jfreechart/index.html
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   - Neither the name of the Object Refinery Limited nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL OBJECT REFINERY LIMITED BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package logicalguess.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.chart.ui.UIUtils;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.json.JSONObject;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StatsBarChart extends ApplicationFrame {

    private static final long serialVersionUID = 1L;

    public static enum Type {
        COUNTS("Counts"),
        PROBS("Probabilities");

        public final String title;

        Type(String title) {
            this.title = title;
        }
    }

    private Type type;
    private int total;

    public StatsBarChart(String title, JSONObject json, Type type) {
        super(title);
        this.type = type;

        AtomicInteger t = new AtomicInteger(0);
        json.keys().forEachRemaining(k -> t.addAndGet(json.getInt(k)));

        total = t.get();
        CategoryDataset dataset = null;
        switch (type) {
            case COUNTS:
                dataset = createCountDataset(json);
                break;
            case PROBS:
                dataset = createProbDataset(json);
        }

        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart, false);
        //chartPanel.setFillZoomRectangle(true);
        //chartPanel.setMouseWheelEnabled(true);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(chartPanel);
    }

    private static CategoryDataset createCountDataset(JSONObject json) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        json.keys().forEachRemaining(k -> dataset.addValue(json.getInt(k), k, String.valueOf(json.getInt(k))));
        return dataset;
    }

    private CategoryDataset createProbDataset(JSONObject json) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        json.keys().forEachRemaining(k -> dataset.addValue(json.getDouble(k)/total, k, String.valueOf(json.getDouble(k)/total)));
        return dataset;
    }

    private JFreeChart createChart(CategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
            total + " shots", null /* x-axis label*/,
                type.title /* y-axis label */, dataset);
        chart.addSubtitle(new TextTitle(""));
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setBarPainter(new StandardBarPainter());
        chart.getLegend().setFrame(BlockBorder.NONE);
        return chart;
    }
}