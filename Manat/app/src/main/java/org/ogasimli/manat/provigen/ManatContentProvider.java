package org.ogasimli.manat.provigen;

import com.tjeannin.provigen.ProviGenOpenHelper;
import com.tjeannin.provigen.ProviGenProvider;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Content provider class
 *
 * Created by Orkhan Gasimli on 21.04.2016.
 */
public class ManatContentProvider extends ProviGenProvider {

    public static final String AUTHORITY = "content://org.ogasimli.manat/";

    private static final Class[] contracts = new Class[]{ManatContract.class};

    @Override
    public SQLiteOpenHelper openHelper(Context context) {
        return new ProviGenOpenHelper(getContext(), "manat_db", null, 1, contracts);
    }

    @Override
    public Class[] contractClasses() {
        return contracts;
    }
}
