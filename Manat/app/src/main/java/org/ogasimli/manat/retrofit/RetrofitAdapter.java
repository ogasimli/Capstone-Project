package org.ogasimli.manat.retrofit;

import org.ogasimli.manat.helper.Constants;

import manat.ogasimli.org.manat.BuildConfig;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * App RestAdapter configuration class
 *
 * Created by Orkhan Gasimli on 11.04.2016.
 */
public class RetrofitAdapter {

    private static final RequestInterceptor mRequestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addQueryParam(Constants.API_KEY, BuildConfig.API_KEY);
        }
    };
    private static RestAdapter mRestAdapter;

    public static RestAdapter getRestAdapter() {
        if (mRestAdapter == null) {
            mRestAdapter = new RestAdapter.Builder()
                    .setEndpoint(Constants.BASE_URL)
                    .setRequestInterceptor(mRequestInterceptor)
                    .build();
            if (BuildConfig.DEBUG) {
                mRestAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
            }
        }
        return mRestAdapter;
    }
}
