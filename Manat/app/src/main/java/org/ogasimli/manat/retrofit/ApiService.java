package org.ogasimli.manat.retrofit;

import org.ogasimli.manat.helper.Constants;
import org.ogasimli.manat.model.Currency;

import java.util.ArrayList;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * API service
 *
 * Created by Orkhan Gasimli on 11.04.2016.
 */
public interface ApiService {

    @GET("/rates")
    void getCurrencyByDate(@Query(Constants.API_QUERY_PARAM) String queryStringByDate,
                           retrofit.Callback<ArrayList<Currency>> callback);

    @GET("/rates")
    void getCurrencyByPeriod(@Query(Constants.API_QUERY_PARAM) String queryStringByPeriod,
                             @Query(Constants.API_EXCLUDE_FIELDS_PARAM) String excludeFieldString,
                             retrofit.Callback<ArrayList<Currency>> callback);
}
