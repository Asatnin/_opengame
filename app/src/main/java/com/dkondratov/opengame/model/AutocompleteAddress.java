package com.dkondratov.opengame.model;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;

public class AutocompleteAddress {
    @Expose
    public Address address;
    @Expose
    public Coordinates coordinates;

    @Override
    public String toString() {
        String a = address.street.toString();
        if (!TextUtils.isEmpty(address.home))
            a+=", "+address.home;

        return a;
    }

}
