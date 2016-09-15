package com.dkondratov.opengame.model;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;

public class AutocompleteCity {
    @Expose
    public Address address;
    @Expose
    public Coordinates coordinates;

    @Override
    public String toString() {
        return address.city.toString();
    }
}
