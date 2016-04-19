package org.ogasimli.manat.chart;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;

import android.content.Context;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import manat.ogasimli.org.manat.R;

/**
 * Custom implementation of the MarkerView
 *
 * Created by Orkhan Gasimli on 03.04.2016.
 */
public class CustomChartMarker extends MarkerView {

    private LineChart mChart;

    @Bind(R.id.marker_y_text_view)
    TextView markerYTextView;

    @Bind(R.id.marker_x_text_view)
    TextView markerXTextView;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public CustomChartMarker(Context context, int layoutResource, LineChart lineChart) {
        super(context, layoutResource);
        mChart = lineChart;
        ButterKnife.bind(this);
    }

    /**
    * callbacks every time the MarkerView is redrawn, can be used to update the
    * content (user-interface)
    */
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {
            CandleEntry ce = (CandleEntry) e;
            markerYTextView.setText("" + Utils.formatNumber(ce.getHigh(), 4, true));
        } else {
            markerYTextView.setText("" + Utils.formatNumber(e.getVal(), 4, true));
        }

        markerXTextView.setText(mChart.getData().getXVals().get(e.getXIndex()));
    }

    @Override
    public int getXOffset(float xPosition) {
        // this will center the marker-view horizontally
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float yPosition) {
        // this will cause the marker-view to be above the selected value
        return -(getHeight()+2);
    }
}
