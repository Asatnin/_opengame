package com.dkondratov.opengame.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "events")
public class Event implements Parcelable {

    @Expose
    @DatabaseField(columnName = "event_id", id = true)
    private String event_id;

    @Expose
    @DatabaseField(columnName = "field_id")
    private String field_id;

    @Expose
    @DatabaseField
    private String title;

    @Expose
    @DatabaseField
    private Long start;

    @Expose
    @DatabaseField
    private Long end;

    @Expose
    @DatabaseField
    private String description;

    @Expose
    private Field field;

    @Expose
    private User creator;

    @DatabaseField
    private String creatorString;

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getCreatorString() {
        return creatorString;
    }

    public void setCreatorString(String creatorString) {
        this.creatorString = creatorString;
    }

    private int my;

    private String invitationID;

    public Event() { }

    public Event(Parcel source) {
        event_id = source.readString();
        field_id = source.readString();
        field = source.readParcelable(Field.class.getClassLoader());
        creator = source.readParcelable(User.class.getClassLoader());
        creatorString = source.readString();
        title = source.readString();
        start = source.readLong();
        end = source.readLong();
        description = source.readString();
        my = source.readInt();
    }

    public String getInvitationID() {
        return invitationID;
    }

    public void setInvitationID(String invitationID) {
        this.invitationID = invitationID;
    }

    public boolean isMy() {
        return my == 0;
    }

    public void setMy(int my) {
        this.my = my;
    }

    public String getField_id() {
        return field_id;
    }

    public void setField_id(String field_id) {
        this.field_id = field_id;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (event_id != null ? !event_id.equals(event.event_id) : event.event_id != null) return false;
        if (description != null ? !description.equals(event.description) : event.description != null) return false;
        if (start != null ? !start.equals(event.start) : event.start != null) return false;
        if (end != null ? !end.equals(event.end) : event.end != null) return false;
        if (title != null ? !title.equals(event.title) : event.title != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = event_id != null ? event_id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (start != null ? start.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public int getMy() {
        return my;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(event_id);
        dest.writeString(field_id);
        dest.writeParcelable(field, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeParcelable(creator, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeString(creatorString);
        dest.writeString(title);
        dest.writeLong(start);
        dest.writeLong(end);
        dest.writeString(description);
        dest.writeInt(my);
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
