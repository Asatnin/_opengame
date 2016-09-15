package com.dkondratov.opengame.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "fields")
public class Field implements Parcelable {

    @Expose
    @SerializedName("field_id")
    @DatabaseField(columnName = "field_id", id = true)
    private String fieldId;

    @Expose
    @DatabaseField
    private String name;

    @Expose
    @DatabaseField
    private Double lat;

    @Expose
    @DatabaseField
    private Double lon;

    @Expose
    @DatabaseField
    private int max_users;

    @Expose
    @DatabaseField
    private String schedule;

    @Expose
    @DatabaseField
    private String price;

    @Expose
    @DatabaseField
    private String cover;

    @Expose
    @DatabaseField
    private Boolean winter;

    @Expose
    @DatabaseField
    private String phone;

    @Expose
    @DatabaseField
    private Boolean lighting;

    @Expose
    @DatabaseField
    private String address_str;

    @Expose
    @SerializedName("icon_url")
    @DatabaseField(columnName = "icon_url")
    private String iconUrl;

    @Expose
    @SerializedName("sport_id")
    @DatabaseField(columnName = "sport_id")
    private String sportId;

    @Expose
    @DatabaseField
    private String description;

    @DatabaseField(defaultValue = "false")
    private Boolean favorite;

    public Field() { }

    public Field(Parcel source) {
        fieldId = source.readString();
        name = source.readString();
        lat = (Double) source.readValue(null);
        lon = (Double) source.readValue(null);
        max_users = source.readInt();
        address_str = source.readString();
        schedule = source.readString();
        price = source.readString();
        cover = source.readString();
        winter = (Boolean) source.readValue(null);
        phone = source.readString();
        lighting = (Boolean) source.readValue(null);
        iconUrl = source.readString();
        sportId = source.readString();
        description = source.readString();
        favorite = (Boolean) source.readValue(null);
    }

    public String getAddress() {
        return address_str;
    }

    public void setAddress(String address) {
        this.address_str = address;
    }

    public int getMax_users() {
        return max_users;
    }

    public void setMax_users(int max_users) {
        this.max_users = max_users;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Boolean getLighting() {
        return lighting;
    }

    public void setLighting(Boolean lighting) {
        this.lighting = lighting;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getSportId() {
        return sportId;
    }

    public void setSportId(String sportId) {
        this.sportId = sportId;
    }

    public Boolean getWinter() {
        return winter;
    }

    public void setWinter(Boolean winter) {
        this.winter = winter;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Field field = (Field) o;

        if (fieldId != null ? !fieldId.equals(field.fieldId) : field.fieldId != null) return false;
        if (name != null ? !name.equals(field.name) : field.name != null) return false;
        if (lat != null ? !lat.equals(field.lat) : field.lat != null) return false;
        if (lon != null ? !lon.equals(field.lon) : field.lon != null) return false;
        if (schedule != null ? !schedule.equals(field.schedule) : field.schedule != null)
            return false;
        if (price != null ? !price.equals(field.price) : field.price != null) return false;
       // if (max_users != null ? !max_users.equals(field.max_users) : field.price != null) return false;

        if (cover != null ? !cover.equals(field.cover) : field.cover != null) return false;
        if (winter != null ? !winter.equals(field.winter) : field.winter != null) return false;
        if (phone != null ? !phone.equals(field.phone) : field.phone != null) return false;
        if (lighting != null ? !lighting.equals(field.lighting) : field.lighting != null)
            return false;
        if (iconUrl != null ? !iconUrl.equals(field.iconUrl) : field.iconUrl != null) return false;
        if (sportId != null ? !sportId.equals(field.sportId) : field.sportId != null) return false;
        if (description != null ? !description.equals(field.description) : field.description != null)
            return false;
        return !(favorite != null ? !favorite.equals(field.favorite) : field.favorite != null);

    }

    @Override
    public int hashCode() {
        int result = fieldId != null ? fieldId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (lat != null ? lat.hashCode() : 0);
        result = 31 * result + (lon != null ? lon.hashCode() : 0);
        result = 31 * result + (schedule != null ? schedule.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (cover != null ? cover.hashCode() : 0);
        result = 31 * result + (winter != null ? winter.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (lighting != null ? lighting.hashCode() : 0);
        result = 31 * result + (iconUrl != null ? iconUrl.hashCode() : 0);
        result = 31 * result + (sportId != null ? sportId.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (favorite != null ? favorite.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fieldId);
        dest.writeString(name);
        dest.writeValue(lat);
        dest.writeValue(lon);
        dest.writeInt(max_users);
        dest.writeString(address_str);
        dest.writeString(schedule);
        dest.writeString(price);
        dest.writeString(cover);
        dest.writeValue(winter);
        dest.writeString(phone);
        dest.writeValue(lighting);
        dest.writeString(iconUrl);
        dest.writeString(sportId);
        dest.writeString(description);
        dest.writeValue(favorite);
    }

    public static final Creator<Field> CREATOR = new Creator<Field>() {
        @Override
        public Field createFromParcel(Parcel source) {
            return new Field(source);
        }

        @Override
        public Field[] newArray(int size) {
            return new Field[size];
        }
    };
}
