package org.ogasimli.manat.ui.chart;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.ogasimli.manat.helper.Constants;
import org.ogasimli.manat.helper.Utilities;
import org.ogasimli.manat.model.Currency;

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

    private final Context mContext;

    private Typeface tf;

    private final LineChart mChart;

    private final ArrayList<Currency> mCurrencyList;

    public ChartMaker(Context context, LineChart lineChart, ArrayList<Currency> currencyList) {
        mContext = context;
        mChart = lineChart;
        mCurrencyList = currencyList;
    }

    public void createChart(){
        tf = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.roboto_light));

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
        CustomChartMarker mv = new CustomChartMarker(mContext, R.layout.custom_marker_view, mChart);

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
        y.setLabelCount(8, true);
        y.setValueFormatter(new MyYAxisValueFormatter());

        mChart.getAxisRight().setEnabled(false);

        // add data
        setData(mCurrencyList);

        mChart.getLegend().setEnabled(false);

        mChart.animateXY(1000, 1000);
    }

    private void setData(ArrayList<Currency> currencyList) {

        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> yVals = new ArrayList<>();

        for (int i = 0; i < currencyList.size(); i++) {
            Currency currency = currencyList.get(i);
            String dateString = currency.getDate();
            String dateSubstring = dateString.substring(0, dateString.indexOf("T"));
            dateString = Utilities.modifyDateString(dateSubstring,
                    Constants.DATE_FORMATTER_WITH_DASH,
                    Constants.DATE_FORMATTER_DMMMYY, "");
            float value = Float.parseFloat(currency.getValue());
            xVals.add(dateString);
            yVals.add(new Entry(value, i));
        }

        // create a dataset and give it a type
        LineDataSet lineDataSet = new LineDataSet(yVals, "DataSet 1");
        lineDataSet.setDrawCubic(true);
        lineDataSet.setCubicIntensity(0.1f);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setLineWidth(1.8f);
        //set1.setCircleSize(4f);
        lineDataSet.setCircleColor(Color.WHITE);
        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawVerticalHighlightIndicator(true);
        lineDataSet.setHighLightColor(Color.WHITE);
        lineDataSet.setColor(Color.WHITE);
        lineDataSet.setFillColor(Color.WHITE);
        //set1.setFillAlpha(255);

        // create a data object with the datasets
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet); // add the datasets
        LineData data = new LineData(xVals, dataSets);
        data.setValueTypeface(tf);
        data.setValueTextSize(9f);
        data.setDrawValues(false);

        // set data
        mChart.setData(data);
    }
}
