package com.dkondratov.opengame.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class Invitation implements Parcelable {

    @Expose
    private String invitation_id;

    @Expose
    private String invitation_status;

    @Expose
    private Event event;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(invitation_id);
        dest.writeString(invitation_status);
        dest.writeParcelable(event, PARCELABLE_WRITE_RETURN_VALUE);
    }

    public Invitation(Parcel source) {
        invitation_id = source.readString();
        invitation_status = source.readString();
        event = source.readParcelable(Event.class.getClassLoader());
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getInvitation_id() {
        return invitation_id;
    }

    public void setInvitation_id(String invitation_id) {
        this.invitation_id = invitation_id;
    }

    public String getInvitation_status() {
        return invitation_status;
    }

    public void setInvitation_status(String invitation_status) {
        this.invitation_status = invitation_status;
    }
}
