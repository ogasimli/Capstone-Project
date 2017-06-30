package org.ogasimli.manat.database.provigen;

import com.tjeannin.provigen.ProviGenOpenHelper;
import com.tjeannin.provigen.ProviGenProvider;

import org.ogasimli.manat.helper.Constants;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Content provider class
 *
 * Created by Orkhan Gasimli on 21.04.2016.
 */
public class ManatContentProvider extends ProviGenProvider {

    private static final Class[] contracts = new Class[]{ManatContract.class};

    @Override
    public SQLiteOpenHelper openHelper(Context context) {
        return new ProviGenOpenHelper(getContext(), Constants.DB_NAME, null, Constants.DB_VERSION,
                contracts);
    }

    @Override
    public Class[] contractClasses() {
        return contracts;
    }
}
