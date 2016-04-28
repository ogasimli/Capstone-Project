package org.ogasimli.manat.model;

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class for holding Currency object
 *
 * Created by Orkhan Gasimli on 03.04.2016.
 */
public class Currency implements Parcelable {

    @SerializedName("code")
    private String code;

    private String name;

    @SerializedName("nominal")
    private String nominal;

    @SerializedName("value")
    private String value;

    @SerializedName("date")
    private String date;

    @SerializedName("trend")
    private String trend;

    public Currency() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if(value.equals("")){
            value = "0";
        }
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeString(this.name);
        dest.writeString(this.nominal);
        dest.writeString(this.value);
        dest.writeString(this.date);
        dest.writeString(this.trend);
    }

    protected Currency(Parcel in) {
        this.code = in.readString();
        this.name = in.readString();
        this.nominal = in.readString();
        this.value = in.readString();
        this.date = in.readString();
        this.trend = in.readString();
    }

    public static final Creator<Currency> CREATOR = new Creator<Currency>() {
        @Override
        public Currency createFromParcel(Parcel source) {
            return new Currency(source);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };
}
