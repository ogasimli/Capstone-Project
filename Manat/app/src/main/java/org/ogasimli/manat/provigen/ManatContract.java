package org.ogasimli.manat.provigen;

import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.ContentUri;

import android.net.Uri;

/**
 * Rates table contract
 *
 * Created by Orkhan Gasimli on 22.04.2016.
 */
public interface ManatContract extends ProviGenBaseContract {

    @Column(Column.Type.TEXT)
    String CODE = "code";

    @Column(Column.Type.TEXT)
    String NOMINAL = "nominal";

    @Column(Column.Type.TEXT)
    String VALUE = "value";

    @Column(Column.Type.TEXT)
    String DATE = "date";

    @Column(Column.Type.TEXT)
    String TREND = "trend";

    @ContentUri
    Uri CONTENT_URI = Uri.parse(ManatContentProvider.AUTHORITY + "rates");
}
