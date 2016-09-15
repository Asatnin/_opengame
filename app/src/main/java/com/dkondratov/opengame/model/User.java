package com.dkondratov.opengame.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User implements Parcelable {

    private String name;
    private String surname;
    private String city;
    private String birth;
    private String gender;
    private String description;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("image_url")
    private String imageUrl;

    private List<Sport> sports;

    public User() { }


    protected User(Parcel in) {
        name = in.readString();
        surname = in.readString();
        city = in.readString();
        birth = in.readString();
        gender = in.readString();
        description = in.readString();
        userId = in.readString();
        imageUrl = in.readString();
        sports = in.createTypedArrayList(Sport.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(surname);
        dest.writeString(city);
        dest.writeString(birth);
        dest.writeString(gender);
        dest.writeString(description);
        dest.writeString(userId);
        dest.writeString(imageUrl);
        dest.writeTypedList(sports);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };


    public List<Sport> getSports() {
        return sports;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!userId.equals(user.userId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }



    @Override
    public String toString() {
        return String.format("%s %s", name, surname);
    }
}
