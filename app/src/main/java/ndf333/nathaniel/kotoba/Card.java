package ndf333.nathaniel.kotoba;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nathaniel on 3/9/2018.
 */

public class Card implements Parcelable {

    //class that models a card

    //card attributes
    public String front;
    public String back;

    public Card(String front, String back) {
        this.front = front;
        this.back = back;
    }

    public Card() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.front);
        dest.writeString(this.back);
    }

    protected Card(Parcel in) {
        this.front = in.readString();
        this.back = in.readString();
    }

    public static final Parcelable.Creator<Card> CREATOR = new Parcelable.Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel source) {
            return new Card(source);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };
}
