package org.ogasimli.manat.ui.chart;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.text.DecimalFormat;

/**
 * Custom value formatter class for Y axis
 *
 * Created by Orkhan Gasimli on 19.04.2016.
 */
class MyYAxisValueFormatter implements YAxisValueFormatter {

    private final DecimalFormat mFormat;

    public MyYAxisValueFormatter() {
        mFormat = new DecimalFormat("###,###,##0.0000"); // use two decimal
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return mFormat.format(value);
    }
}
