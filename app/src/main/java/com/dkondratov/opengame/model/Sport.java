package com.dkondratov.opengame.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sport implements Parcelable{
    @SerializedName("icon_url")
    private String iconUrl;
    private String name;
    @SerializedName("sport_id")
    private String sportId;

    public String getSportId() {
        return sportId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getName() {
        return name;
    }

    protected Sport(Parcel in) {
        iconUrl = in.readString();
        name = in.readString();
        sportId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(iconUrl);
        dest.writeString(name);
        dest.writeString(sportId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Sport> CREATOR = new Creator<Sport>() {
        @Override
        public Sport createFromParcel(Parcel in) {
            return new Sport(in);
        }

        @Override
        public Sport[] newArray(int size) {
            return new Sport[size];
        }
    };


}
