package com.dkondratov.opengame.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by andrew on 09.04.2015.
 */
@DatabaseTable(tableName = "categories")
public class Category implements Parcelable {

    @DatabaseField
    private String iconUrl;

    @DatabaseField
    private String name;

    @SerializedName("sport_id")
    @DatabaseField(columnName = "sport_id", id = true)
    private String sportId;

    @DatabaseField
    private int order;

    public Category() { }

    public Category(Parcel source) {
        iconUrl = source.readString();
        name = source.readString();
        sportId = source.readString();
        order = source.readInt();
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSportId() {
        return sportId;
    }

    public void setSportId(String sportId) {
        this.sportId = sportId;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (order != category.order) return false;
        if (iconUrl != null ? !iconUrl.equals(category.iconUrl) : category.iconUrl != null)
            return false;
        if (name != null ? !name.equals(category.name) : category.name != null) return false;
        if (sportId != null ? !sportId.equals(category.sportId) : category.sportId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = iconUrl != null ? iconUrl.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (sportId != null ? sportId.hashCode() : 0);
        result = 31 * result + order;
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(iconUrl);
        dest.writeString(name);
        dest.writeString(sportId);
        dest.writeInt(order);
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public String toString() {
        return name;
    }
}
