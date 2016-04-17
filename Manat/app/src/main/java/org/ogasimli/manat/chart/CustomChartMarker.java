package org.ogasimli.manat.chart;

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

    @Bind(R.id.marker_textview)
    TextView markerTextView;

    public CustomChartMarker(Context context, int layoutResource) {
        super(context, layoutResource);
        ButterKnife.bind(this);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {
            CandleEntry ce = (CandleEntry) e;
            markerTextView.setText("" + Utils.formatNumber(ce.getHigh(), 0, true));
        } else {
            markerTextView.setText("" + Utils.formatNumber(e.getVal(), 0, true));
        }
    }

    @Override
    public int getXOffset(float xpos) {
        // this will center the marker-view horizontally
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float ypos) {
        // this will cause the marker-view to be above the selected value
        return -(getHeight()+2);
    }
}
