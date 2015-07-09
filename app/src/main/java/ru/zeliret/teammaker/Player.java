package ru.zeliret.teammaker;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;

public class Player implements Parcelable {
    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(final Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(final int size) {
            return new Player[size];
        }
    };
    public final String name;
    public final String photoUri;

    public Player(final String name, final String photoUri) {
        this.name = name;
        this.photoUri = photoUri;
    }

    protected Player(final Parcel in) {
        name = in.readString();
        photoUri = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(name);
        dest.writeString(photoUri);
    }

    public static Player fromCursor(final Cursor c) {
        if (null != c) {
            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
            String photoUrl = c.getString(c.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
            return new Player(name, photoUrl);
        }

        return null;
    }
}
