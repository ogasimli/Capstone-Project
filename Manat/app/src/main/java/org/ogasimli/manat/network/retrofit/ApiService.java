package org.ogasimli.manat.network.retrofit;

import org.ogasimli.manat.helper.Constants;
import org.ogasimli.manat.model.Currency;

import java.util.ArrayList;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Retrofit API service interface
 *
 * Created by Orkhan Gasimli on 11.04.2016.
 */
public interface ApiService {

    @GET("/rates")
    void getCurrencyByDate(@Query(Constants.FROM_DATE_QUERY_PARAM) String fromDate,
                           retrofit.Callback<ArrayList<Currency>> callback);

    @GET("/rates")
    void getCurrencyByPeriod(@Query(Constants.FROM_DATE_QUERY_PARAM) String fromDate,
                             @Query(Constants.TILL_DATE_QUERY_PARAM) String tillDate,
                             @Query(Constants.CODE_QUERY_PARAM) String code,
                             retrofit.Callback<ArrayList<Currency>> callback);
}
