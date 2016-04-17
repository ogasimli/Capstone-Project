package org.ogasimli.manat.chart;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

import java.util.ArrayList;

import manat.ogasimli.org.manat.R;

/**
 * Class for creating Line Chart
 *
 * Created by Orkhan Gasimli on 03.04.2016.
 */
public class ChartMaker {

    private Context mContext;
    private Typeface tf;
    private LineChart mChart;

    public ChartMaker(Context context, LineChart lineChart) {
        mContext = context;
        mChart = lineChart;
    }

    public void createChart(){
        tf = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription(mContext.getString(R.string.line_chart_no_data_text));

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);
        mChart.setDrawGridBackground(false);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        CustomChartMarker mv = new CustomChartMarker(mContext, R.layout.custom_marker_view);

        // set the marker to the chart
        mChart.setMarkerView(mv);

        //XAxis settings
        XAxis x = mChart.getXAxis();
        x.setEnabled(true);
        x.setDrawAxisLine(true);
        x.setDrawGridLines(false);
        x.setDrawLabels(true);
        x.setTextColor(Color.WHITE);
        x.setTypeface(tf);
        x.setAxisLineColor(Color.WHITE);
        x.setAxisLineWidth(2f);
        x.setAvoidFirstLastClipping(true);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);

        //YAxis settings
        YAxis y = mChart.getAxisLeft();
        y.setEnabled(true);
        y.setDrawAxisLine(true);
        y.setAxisLineColor(Color.WHITE);
        y.setAxisLineWidth(2f);
        y.setDrawGridLines(true);
        y.setGridColor(Color.WHITE);
        y.setGridLineWidth(0.4f);
        y.setDrawLabels(true);
        y.setTextColor(Color.WHITE);
        y.setTypeface(tf);
        y.setLabelCount(6, false);
        y.setStartAtZero(false);

        mChart.getAxisRight().setEnabled(false);

        // add data
        setData(12, 20);

        mChart.getLegend().setEnabled(false);

        mChart.animateXY(2000, 2000);
    }

    private void setData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            xVals.add((1990 +i) + "");
        }

        ArrayList<Entry> yVals = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult) + 20;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
        set1.setDrawCubic(true);
        set1.setCubicIntensity(0.1f);
        set1.setDrawFilled(true);
        set1.setDrawCircles(false);
        set1.setLineWidth(1.8f);
        //set1.setCircleSize(4f);
        set1.setCircleColor(Color.WHITE);
        set1.setHighlightEnabled(true);
        set1.setDrawHorizontalHighlightIndicator(false);
        set1.setDrawVerticalHighlightIndicator(true);
        set1.setHighLightColor(Color.WHITE);
        set1.setColor(Color.WHITE);
        set1.setFillColor(Color.WHITE);
        //set1.setFillAlpha(255);

        // create a data object with the datasets
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the datasets
        LineData data = new LineData(xVals, dataSets);
        data.setValueTypeface(tf);
        data.setValueTextSize(9f);
        data.setDrawValues(false);

        // set data
        mChart.setData(data);
    }
}
