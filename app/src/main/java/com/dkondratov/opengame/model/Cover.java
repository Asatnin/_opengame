package com.dkondratov.opengame.model;

public class Cover {
    private String id;
    private String name;

    public Cover(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
