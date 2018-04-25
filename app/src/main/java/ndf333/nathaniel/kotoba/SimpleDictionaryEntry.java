package ndf333.nathaniel.kotoba;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nathaniel on 2/28/2018.
 */

public class SimpleDictionaryEntry implements Parcelable {

    //A simple dictionary entry, to be displayed when a user enters a search.
    //This entry consists only of the term, reading, and meaning.

    public String term;
    public String reading;
    public String meaning;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.term);
        dest.writeString(this.reading);
        dest.writeString(this.meaning);
    }

    public SimpleDictionaryEntry() {
    }

    protected SimpleDictionaryEntry(Parcel in) {
        this.term = in.readString();
        this.reading = in.readString();
        this.meaning = in.readString();
    }

    public static final Creator<SimpleDictionaryEntry> CREATOR = new Creator<SimpleDictionaryEntry>() {
        @Override
        public SimpleDictionaryEntry createFromParcel(Parcel source) {
            return new SimpleDictionaryEntry(source);
        }

        @Override
        public SimpleDictionaryEntry[] newArray(int size) {
            return new SimpleDictionaryEntry[size];
        }
    };
}
